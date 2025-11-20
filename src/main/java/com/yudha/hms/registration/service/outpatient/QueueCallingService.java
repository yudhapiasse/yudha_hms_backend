package com.yudha.hms.registration.service.outpatient;

import com.yudha.hms.registration.entity.*;
import com.yudha.hms.registration.repository.OutpatientRegistrationRepository;
import com.yudha.hms.registration.repository.QueueCallHistoryRepository;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Queue Calling Service.
 * Manages queue calling workflow for outpatient registration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueueCallingService {

    private final OutpatientRegistrationRepository registrationRepository;
    private final QueueCallHistoryRepository callHistoryRepository;

    /**
     * Call next patient from queue for a specific polyclinic.
     *
     * @param polyclinicId polyclinic ID
     * @param calledBy user calling the patient
     * @param consultationRoom optional consultation room number
     * @return the called registration
     */
    @Transactional
    public OutpatientRegistration callNextPatient(
        UUID polyclinicId,
        String calledBy,
        String consultationRoom
    ) {
        log.info("Calling next patient for polyclinic: {}", polyclinicId);

        // Find next waiting patient
        List<OutpatientRegistration> waiting = registrationRepository
            .findByPolyclinicIdAndRegistrationDateAndStatusOrderByQueueNumberAsc(
                polyclinicId,
                LocalDate.now(),
                RegistrationStatus.WAITING
            );

        if (waiting.isEmpty()) {
            throw new BusinessException("Tidak ada pasien yang menunggu di polyclinic ini");
        }

        OutpatientRegistration registration = waiting.get(0);

        // Call the patient
        registration.callQueue(calledBy);
        registrationRepository.save(registration);

        // Record call in history
        recordCallHistory(registration, QueueCallType.NORMAL, calledBy, consultationRoom);

        log.info("Patient called: {} - Queue: {}", registration.getRegistrationNumber(), registration.getQueueCode());

        return registration;
    }

    /**
     * Call specific patient by queue number.
     *
     * @param polyclinicId polyclinic ID
     * @param queueNumber queue number to call
     * @param calledBy user calling the patient
     * @param consultationRoom optional consultation room number
     * @return the called registration
     */
    @Transactional
    public OutpatientRegistration callSpecificPatient(
        UUID polyclinicId,
        Integer queueNumber,
        String calledBy,
        String consultationRoom
    ) {
        log.info("Calling specific patient - Queue: {} at polyclinic: {}", queueNumber, polyclinicId);

        OutpatientRegistration registration = registrationRepository
            .findByPolyclinicIdAndQueueNumberAndRegistrationDate(
                polyclinicId,
                queueNumber,
                LocalDate.now()
            )
            .orElseThrow(() -> new ResourceNotFoundException(
                "Registration not found with queue number: " + queueNumber
            ));

        // Validate can be called
        if (registration.getQueueStatus() != null && !registration.getQueueStatus().canBeCalled()) {
            throw new BusinessException(
                "Cannot call patient in current queue status: " + registration.getQueueStatus()
            );
        }

        // Call the patient
        registration.callQueue(calledBy);
        registrationRepository.save(registration);

        // Determine call type
        QueueCallType callType = (registration.getQueueStatus() == QueueStatus.SKIPPED) ?
            QueueCallType.RECALL : QueueCallType.NORMAL;

        // Record call in history
        recordCallHistory(registration, callType, calledBy, consultationRoom);

        log.info("Patient called: {} - Queue: {}", registration.getRegistrationNumber(), registration.getQueueCode());

        return registration;
    }

    /**
     * Recall patient (call again after no response).
     *
     * @param registrationId registration ID
     * @param calledBy user calling the patient
     * @param consultationRoom optional consultation room number
     * @return the recalled registration
     */
    @Transactional
    public OutpatientRegistration recallPatient(
        UUID registrationId,
        String calledBy,
        String consultationRoom
    ) {
        log.info("Recalling patient: {}", registrationId);

        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        // Only recall if currently CALLED or SKIPPED
        if (registration.getQueueStatus() != QueueStatus.CALLED &&
            registration.getQueueStatus() != QueueStatus.SKIPPED) {
            throw new BusinessException("Can only recall patients in CALLED or SKIPPED status");
        }

        // Update to CALLED status
        registration.callQueue(calledBy);
        registrationRepository.save(registration);

        // Record recall in history
        recordCallHistory(registration, QueueCallType.RECALL, calledBy, consultationRoom);

        log.info("Patient recalled: {} - Queue: {}", registration.getRegistrationNumber(), registration.getQueueCode());

        return registration;
    }

    /**
     * Start serving patient (patient has responded to call).
     *
     * @param registrationId registration ID
     * @return the updated registration
     */
    @Transactional
    public OutpatientRegistration startServing(UUID registrationId) {
        log.info("Starting to serve patient: {}", registrationId);

        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        // Start serving
        registration.startServing();
        registrationRepository.save(registration);

        // Update latest call history as responded
        callHistoryRepository.findFirstByOutpatientRegistrationIdOrderByCalledAtDesc(registrationId)
            .ifPresent(call -> {
                call.markAsResponded();
                callHistoryRepository.save(call);
            });

        log.info("Patient serving started: {} - Queue: {}",
            registration.getRegistrationNumber(), registration.getQueueCode());

        return registration;
    }

    /**
     * Complete queue service.
     *
     * @param registrationId registration ID
     * @return the updated registration
     */
    @Transactional
    public OutpatientRegistration completeQueue(UUID registrationId) {
        log.info("Completing queue service: {}", registrationId);

        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        // Complete queue
        registration.completeQueue();
        registrationRepository.save(registration);

        log.info("Queue service completed: {} - Queue: {}",
            registration.getRegistrationNumber(), registration.getQueueCode());

        return registration;
    }

    /**
     * Skip patient (not present when called).
     *
     * @param registrationId registration ID
     * @param reason skip reason
     * @return the updated registration
     */
    @Transactional
    public OutpatientRegistration skipPatient(UUID registrationId, String reason) {
        log.info("Skipping patient: {} - Reason: {}", registrationId, reason);

        OutpatientRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Registration", "ID", registrationId));

        // Can only skip if currently CALLED
        if (registration.getQueueStatus() != QueueStatus.CALLED) {
            throw new BusinessException("Can only skip patients in CALLED status");
        }

        // Skip patient
        registration.skipQueue(reason);
        registrationRepository.save(registration);

        // Update latest call history as no response
        callHistoryRepository.findFirstByOutpatientRegistrationIdOrderByCalledAtDesc(registrationId)
            .ifPresent(call -> {
                call.markAsNoResponse();
                callHistoryRepository.save(call);
            });

        log.info("Patient skipped: {} - Queue: {}", registration.getRegistrationNumber(), registration.getQueueCode());

        return registration;
    }

    /**
     * Get current queue status for a polyclinic.
     *
     * @param polyclinicId polyclinic ID
     * @return list of all active registrations
     */
    @Transactional(readOnly = true)
    public List<OutpatientRegistration> getCurrentQueueStatus(UUID polyclinicId) {
        return registrationRepository
            .findByPolyclinicIdAndRegistrationDateOrderByQueueNumberAsc(
                polyclinicId,
                LocalDate.now()
            )
            .stream()
            .filter(OutpatientRegistration::isQueueActive)
            .collect(Collectors.toList());
    }

    /**
     * Get waiting patients for a polyclinic.
     *
     * @param polyclinicId polyclinic ID
     * @return list of waiting registrations
     */
    @Transactional(readOnly = true)
    public List<OutpatientRegistration> getWaitingPatients(UUID polyclinicId) {
        return registrationRepository
            .findByPolyclinicIdAndRegistrationDateAndStatusOrderByQueueNumberAsc(
                polyclinicId,
                LocalDate.now(),
                RegistrationStatus.WAITING
            );
    }

    /**
     * Get currently serving patients for a polyclinic.
     *
     * @param polyclinicId polyclinic ID
     * @return list of serving registrations
     */
    @Transactional(readOnly = true)
    public List<OutpatientRegistration> getServingPatients(UUID polyclinicId) {
        return registrationRepository
            .findByPolyclinicIdAndRegistrationDateOrderByQueueNumberAsc(
                polyclinicId,
                LocalDate.now()
            )
            .stream()
            .filter(reg -> reg.getQueueStatus() == QueueStatus.SERVING)
            .collect(Collectors.toList());
    }

    /**
     * Get skipped patients for a polyclinic.
     *
     * @param polyclinicId polyclinic ID
     * @return list of skipped registrations
     */
    @Transactional(readOnly = true)
    public List<OutpatientRegistration> getSkippedPatients(UUID polyclinicId) {
        return registrationRepository
            .findByPolyclinicIdAndRegistrationDateOrderByQueueNumberAsc(
                polyclinicId,
                LocalDate.now()
            )
            .stream()
            .filter(reg -> reg.getQueueStatus() == QueueStatus.SKIPPED)
            .collect(Collectors.toList());
    }

    /**
     * Get call history for a registration.
     *
     * @param registrationId registration ID
     * @return list of call history
     */
    @Transactional(readOnly = true)
    public List<QueueCallHistory> getCallHistory(UUID registrationId) {
        return callHistoryRepository.findByOutpatientRegistrationIdOrderByCalledAtDesc(registrationId);
    }

    /**
     * Get call statistics for a polyclinic today.
     *
     * @param polyclinicId polyclinic ID
     * @return call statistics
     */
    @Transactional(readOnly = true)
    public QueueCallStatistics getCallStatistics(UUID polyclinicId) {
        LocalDate today = LocalDate.now();

        long totalCalls = callHistoryRepository.countByPolyclinicAndDate(polyclinicId, today);
        long responded = callHistoryRepository.countByPolyclinicDateAndResponseStatus(
            polyclinicId, today, QueueResponseStatus.RESPONDED
        );
        long noResponse = callHistoryRepository.countByPolyclinicDateAndResponseStatus(
            polyclinicId, today, QueueResponseStatus.NO_RESPONSE
        );
        long recalls = callHistoryRepository.countByPolyclinicDateAndCallType(
            polyclinicId, today, QueueCallType.RECALL
        );
        Double avgResponseTime = callHistoryRepository.calculateAverageResponseTimeSeconds(
            polyclinicId, today
        );

        return QueueCallStatistics.builder()
            .polyclinicId(polyclinicId)
            .date(today)
            .totalCalls(totalCalls)
            .responded(responded)
            .noResponse(noResponse)
            .recalls(recalls)
            .averageResponseTimeSeconds(avgResponseTime != null ? avgResponseTime : 0.0)
            .build();
    }

    // ========== Private Helper Methods ==========

    /**
     * Record call in history.
     */
    private void recordCallHistory(
        OutpatientRegistration registration,
        QueueCallType callType,
        String calledBy,
        String consultationRoom
    ) {
        QueueCallHistory history = QueueCallHistory.builder()
            .outpatientRegistration(registration)
            .queueNumber(registration.getQueueNumber())
            .queueCode(registration.getQueueCode())
            .callType(callType)
            .calledByName(calledBy)
            .polyclinicId(registration.getPolyclinic().getId())
            .doctorId(registration.getDoctor().getId())
            .consultationRoom(consultationRoom)
            .build();

        callHistoryRepository.save(history);
    }

    /**
     * Inner class for call statistics.
     */
    @lombok.Data
    @lombok.Builder
    public static class QueueCallStatistics {
        private UUID polyclinicId;
        private LocalDate date;
        private long totalCalls;
        private long responded;
        private long noResponse;
        private long recalls;
        private double averageResponseTimeSeconds;

        public double getResponseRate() {
            return totalCalls > 0 ? (double) responded / totalCalls * 100 : 0.0;
        }

        public double getNoResponseRate() {
            return totalCalls > 0 ? (double) noResponse / totalCalls * 100 : 0.0;
        }
    }
}
