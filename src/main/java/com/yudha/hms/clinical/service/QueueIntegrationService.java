package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.DoctorWorkloadResponse;
import com.yudha.hms.clinical.dto.QueueDisplayResponse;
import com.yudha.hms.clinical.dto.QueueItemResponse;
import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.EncounterStatus;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.registration.entity.OutpatientRegistration;
import com.yudha.hms.registration.entity.QueueStatus;
import com.yudha.hms.registration.repository.OutpatientRegistrationRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Queue Integration Service.
 *
 * Synchronizes encounter status changes with queue management system.
 * Provides real-time queue updates and display board data.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QueueIntegrationService {

    private final EncounterRepository encounterRepository;
    private final OutpatientRegistrationRepository outpatientRegistrationRepository;

    /**
     * Sync queue status based on encounter status change.
     *
     * Called automatically when encounter status changes.
     *
     * @param encounter The encounter that changed status
     */
    public void syncQueueStatus(Encounter encounter) {
        log.info("Syncing queue status for encounter: {} with status: {}",
            encounter.getEncounterNumber(), encounter.getStatus());

        // Only sync for outpatient encounters with registration
        if (encounter.getOutpatientRegistrationId() == null) {
            log.debug("Encounter {} has no outpatient registration, skipping queue sync", encounter.getEncounterNumber());
            return;
        }

        Optional<OutpatientRegistration> registrationOpt =
            outpatientRegistrationRepository.findById(encounter.getOutpatientRegistrationId());

        if (registrationOpt.isEmpty()) {
            log.warn("Outpatient registration not found for encounter: {}", encounter.getEncounterNumber());
            return;
        }

        OutpatientRegistration registration = registrationOpt.get();

        // Map encounter status to queue status
        QueueStatus newQueueStatus = mapEncounterStatusToQueueStatus(encounter.getStatus(), registration.getQueueStatus());

        if (newQueueStatus != null && newQueueStatus != registration.getQueueStatus()) {
            log.info("Updating queue status from {} to {} for registration: {}",
                registration.getQueueStatus(), newQueueStatus, registration.getRegistrationNumber());

            updateQueueStatus(registration, newQueueStatus);
            outpatientRegistrationRepository.save(registration);

            log.info("Queue status updated successfully for: {}", registration.getRegistrationNumber());
        }
    }

    /**
     * Map encounter status to queue status.
     */
    private QueueStatus mapEncounterStatusToQueueStatus(EncounterStatus encounterStatus, QueueStatus currentQueueStatus) {
        return switch (encounterStatus) {
            case ARRIVED -> {
                // If not already in queue, set to WAITING
                if (currentQueueStatus == null || currentQueueStatus == QueueStatus.CANCELLED) {
                    yield QueueStatus.WAITING;
                }
                yield null; // No change needed
            }
            case TRIAGED -> {
                // TRIAGED doesn't change queue status, just updates priority
                // Keep current status (WAITING or CALLED)
                yield null;
            }
            case IN_PROGRESS -> QueueStatus.SERVING;
            case FINISHED -> QueueStatus.COMPLETED;
            case CANCELLED -> QueueStatus.CANCELLED;
            default -> null;
        };
    }

    /**
     * Update queue status in registration.
     */
    private void updateQueueStatus(OutpatientRegistration registration, QueueStatus newStatus) {
        switch (newStatus) {
            case WAITING:
                // Already waiting, no additional action needed
                break;
            case SERVING:
                registration.startServing();
                break;
            case COMPLETED:
                registration.completeQueue();
                break;
            case CANCELLED:
                registration.cancelQueue("Encounter cancelled", "SYSTEM");
                break;
            default:
                registration.setQueueStatus(newStatus);
                break;
        }
    }

    /**
     * Get queue display data for a department/polyclinic.
     *
     * @param polyclinicId Department/polyclinic ID
     * @return Queue display data
     */
    @Transactional(readOnly = true)
    public QueueDisplayResponse getQueueDisplay(UUID polyclinicId) {
        log.info("Fetching queue display for polyclinic: {}", polyclinicId);

        LocalDate today = LocalDate.now();
        List<OutpatientRegistration> registrations =
            outpatientRegistrationRepository.findByPolyclinicIdAndRegistrationDate(polyclinicId, today);

        // Filter active registrations only
        List<OutpatientRegistration> activeRegistrations = registrations.stream()
            .filter(r -> r.getQueueStatus() != null && r.getQueueStatus().isActive())
            .sorted(Comparator.comparing(OutpatientRegistration::getQueueNumber))
            .collect(Collectors.toList());

        // Calculate statistics
        long totalWaiting = activeRegistrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.WAITING).count();
        long totalCalled = activeRegistrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.CALLED).count();
        long totalServing = activeRegistrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.SERVING).count();
        long totalCompleted = registrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.COMPLETED).count();
        long totalSkipped = registrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.SKIPPED).count();

        // Calculate average waiting time
        Double averageWaitingTime = calculateAverageWaitingTime(activeRegistrations);

        // Get currently serving and next patient
        OutpatientRegistration currentlyServing = activeRegistrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.SERVING)
            .findFirst()
            .orElse(null);

        OutpatientRegistration nextInQueue = activeRegistrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.WAITING)
            .findFirst()
            .orElse(null);

        // Build waiting queue list
        List<QueueItemResponse> waitingQueue = activeRegistrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.WAITING || r.getQueueStatus() == QueueStatus.CALLED)
            .map(this::convertToQueueItem)
            .collect(Collectors.toList());

        // Build serving queue list
        List<QueueItemResponse> servingQueue = activeRegistrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.SERVING)
            .map(this::convertToQueueItem)
            .collect(Collectors.toList());

        // Build doctor workload (placeholder - implement when doctor schedule integration is ready)
        List<DoctorWorkloadResponse> doctorWorkloads = buildDoctorWorkloads(registrations);

        return QueueDisplayResponse.builder()
            .polyclinicId(polyclinicId)
            .polyclinicName("Polyclinic") // TODO: Fetch from polyclinic entity
            .displayTime(LocalDateTime.now())
            .totalWaiting((int) totalWaiting)
            .totalCalled((int) totalCalled)
            .totalServing((int) totalServing)
            .totalCompleted((int) totalCompleted)
            .totalSkipped((int) totalSkipped)
            .averageWaitingTimeMinutes(averageWaitingTime)
            .currentlyServingQueueCode(currentlyServing != null ? currentlyServing.getQueueCode() : null)
            .currentlyServingPatientName(currentlyServing != null ? "Patient ID: " + currentlyServing.getPatientId() : null)
            .nextQueueCode(nextInQueue != null ? nextInQueue.getQueueCode() : null)
            .nextPatientName(nextInQueue != null ? "Patient ID: " + nextInQueue.getPatientId() : null)
            .waitingQueue(waitingQueue)
            .servingQueue(servingQueue)
            .doctorWorkloads(doctorWorkloads)
            .build();
    }

    /**
     * Calculate average waiting time in minutes.
     */
    private Double calculateAverageWaitingTime(List<OutpatientRegistration> registrations) {
        List<OutpatientRegistration> waiting = registrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.WAITING || r.getQueueStatus() == QueueStatus.CALLED)
            .collect(Collectors.toList());

        if (waiting.isEmpty()) {
            return 0.0;
        }

        double totalMinutes = waiting.stream()
            .mapToLong(r -> Duration.between(r.getRegistrationTime(), LocalDateTime.now()).toMinutes())
            .average()
            .orElse(0.0);

        return Math.round(totalMinutes * 10.0) / 10.0; // Round to 1 decimal
    }

    /**
     * Convert registration to queue item response.
     */
    private QueueItemResponse convertToQueueItem(OutpatientRegistration registration) {
        // Calculate waiting time
        LocalDateTime startTime = registration.getRegistrationTime();
        LocalDateTime endTime = registration.getQueueServingStartedAt() != null ?
            registration.getQueueServingStartedAt() : LocalDateTime.now();
        long waitingMinutes = Duration.between(startTime, endTime).toMinutes();

        return QueueItemResponse.builder()
            .registrationId(registration.getId())
            .encounterId(null) // TODO: Fetch from encounter if needed
            .queueCode(registration.getQueueCode())
            .queueNumber(registration.getQueueNumber())
            .patientName("Patient ID: " + registration.getPatientId()) // TODO: Fetch patient details when needed
            .patientMrn(registration.getPatientId().toString()) // TODO: Fetch actual MRN
            .queueStatus(registration.getQueueStatus().name())
            .priority("ROUTINE") // TODO: Add priority field when available
            .registrationTime(registration.getRegistrationTime())
            .queueCalledAt(registration.getQueueCalledAt())
            .servingStartedAt(registration.getQueueServingStartedAt())
            .waitingTimeMinutes((int) waitingMinutes)
            .doctorName(registration.getDoctor() != null ? registration.getDoctor().getFullName() : "Unknown")
            .consultationRoom(null) // TODO: Add consultation room when available
            .build();
    }

    /**
     * Build doctor workload summaries.
     */
    private List<DoctorWorkloadResponse> buildDoctorWorkloads(List<OutpatientRegistration> registrations) {
        // Group by doctor
        Map<UUID, List<OutpatientRegistration>> byDoctor = registrations.stream()
            .filter(r -> r.getDoctor() != null)
            .collect(Collectors.groupingBy(r -> r.getDoctor().getId()));

        return byDoctor.entrySet().stream()
            .map(entry -> {
                UUID doctorId = entry.getKey();
                List<OutpatientRegistration> doctorRegistrations = entry.getValue();

                long waiting = doctorRegistrations.stream()
                    .filter(r -> r.getQueueStatus() == QueueStatus.WAITING || r.getQueueStatus() == QueueStatus.CALLED)
                    .count();
                long serving = doctorRegistrations.stream()
                    .filter(r -> r.getQueueStatus() == QueueStatus.SERVING)
                    .count();
                long completed = doctorRegistrations.stream()
                    .filter(r -> r.getQueueStatus() == QueueStatus.COMPLETED)
                    .count();

                // Calculate average service time for completed patients
                Double avgServiceTime = doctorRegistrations.stream()
                    .filter(r -> r.getQueueStatus() == QueueStatus.COMPLETED &&
                                 r.getQueueServingStartedAt() != null &&
                                 r.getQueueServingEndedAt() != null)
                    .mapToLong(r -> Duration.between(
                        r.getQueueServingStartedAt(),
                        r.getQueueServingEndedAt()
                    ).toMinutes())
                    .average()
                    .orElse(0.0);

                String status = serving > 0 ? "BUSY" : (waiting > 0 ? "AVAILABLE" : "AVAILABLE");

                String doctorName = doctorRegistrations.get(0).getDoctor() != null ?
                    doctorRegistrations.get(0).getDoctor().getFullName() : "Unknown";

                return DoctorWorkloadResponse.builder()
                    .doctorId(doctorId)
                    .doctorName(doctorName)
                    .specialty(null) // TODO: Fetch from doctor entity
                    .waitingPatients((int) waiting)
                    .servingPatients((int) serving)
                    .completedToday((int) completed)
                    .averageServiceTimeMinutes(Math.round(avgServiceTime * 10.0) / 10.0)
                    .status(status)
                    .build();
            })
            .sorted(Comparator.comparing(DoctorWorkloadResponse::getDoctorName))
            .collect(Collectors.toList());
    }

    /**
     * Get next patient alert for a doctor.
     *
     * @param polyclinicId Polyclinic ID
     * @param doctorId Doctor ID
     * @return Next patient in queue, or null if none
     */
    @Transactional(readOnly = true)
    public QueueItemResponse getNextPatientAlert(UUID polyclinicId, UUID doctorId) {
        log.info("Getting next patient alert for doctor: {} in polyclinic: {}", doctorId, polyclinicId);

        LocalDate today = LocalDate.now();
        List<OutpatientRegistration> registrations =
            outpatientRegistrationRepository.findByPolyclinicIdAndRegistrationDate(polyclinicId, today);

        // Filter by doctor and get next waiting patient
        Optional<OutpatientRegistration> nextPatient = registrations.stream()
            .filter(r -> r.getDoctor() != null && doctorId.equals(r.getDoctor().getId()))
            .filter(r -> r.getQueueStatus() == QueueStatus.WAITING)
            .min(Comparator.comparing(OutpatientRegistration::getQueueNumber));

        return nextPatient.map(this::convertToQueueItem).orElse(null);
    }
}
