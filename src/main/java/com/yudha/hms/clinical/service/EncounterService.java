package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.*;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.DuplicateResourceException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Encounter Service.
 * Business logic for encounter/visit management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final EncounterParticipantRepository participantRepository;
    private final EncounterDiagnosisRepository diagnosisRepository;
    private final EncounterStatusHistoryRepository statusHistoryRepository;
    private final QueueIntegrationService queueIntegrationService;

    /**
     * Create a new encounter.
     */
    public EncounterResponse createEncounter(EncounterRequest request) {
        log.info("Creating new encounter for patient: {}", request.getPatientId());

        // Validate request
        validateEncounterRequest(request);

        // Check for active encounters for the same patient
        checkForActiveEncounters(request.getPatientId(), request.getEncounterType());

        // Build encounter entity
        Encounter encounter = buildEncounterFromRequest(request);

        // Generate encounter number
        encounter.setEncounterNumber(generateEncounterNumber());

        // Set insurance flags
        if (request.getInsuranceType() != null && request.getInsuranceType().isBpjs()) {
            encounter.setIsBpjs(true);
        }

        // Save encounter
        encounter = encounterRepository.save(encounter);
        log.info("Encounter created with number: {}", encounter.getEncounterNumber());

        // Add initial status history
        addStatusHistory(encounter, null, encounter.getStatus(), "Encounter created", null);

        // Add participants if provided
        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            for (EncounterParticipantDto participantDto : request.getParticipants()) {
                addParticipant(encounter.getId(), participantDto);
            }
        }

        // Add diagnoses if provided
        if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
            for (EncounterDiagnosisDto diagnosisDto : request.getDiagnoses()) {
                addDiagnosis(encounter.getId(), diagnosisDto);
            }
        }

        // Reload to get all relationships
        encounter = encounterRepository.findById(encounter.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Encounter not found after creation"));

        return mapToResponse(encounter);
    }

    /**
     * Get encounter by ID.
     */
    @Transactional(readOnly = true)
    public EncounterResponse getEncounterById(UUID id) {
        log.info("Retrieving encounter: {}", id);
        Encounter encounter = findEncounterById(id);
        return mapToResponse(encounter);
    }

    /**
     * Get encounter by encounter number.
     */
    @Transactional(readOnly = true)
    public EncounterResponse getEncounterByNumber(String encounterNumber) {
        log.info("Retrieving encounter by number: {}", encounterNumber);
        Encounter encounter = encounterRepository.findByEncounterNumber(encounterNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter not found with number: " + encounterNumber));
        return mapToResponse(encounter);
    }

    /**
     * Get encounters by patient.
     */
    @Transactional(readOnly = true)
    public List<EncounterSummaryDto> getEncountersByPatient(UUID patientId) {
        log.info("Retrieving encounters for patient: {}", patientId);
        List<Encounter> encounters = encounterRepository.findByPatientIdOrderByEncounterStartDesc(patientId);
        return encounters.stream()
            .map(this::mapToSummary)
            .collect(Collectors.toList());
    }

    /**
     * Search encounters with criteria.
     */
    @Transactional(readOnly = true)
    public Page<EncounterSummaryDto> searchEncounters(EncounterSearchCriteria criteria) {
        log.info("Searching encounters with criteria: {}", criteria);

        // Build specification
        Specification<Encounter> spec = EncounterSpecification.fromCriteria(criteria);

        // Build pageable
        Pageable pageable = buildPageable(criteria);

        // Execute search
        Page<Encounter> encounters = encounterRepository.findAll(spec, pageable);

        return encounters.map(this::mapToSummary);
    }

    /**
     * Update encounter.
     */
    public EncounterResponse updateEncounter(UUID id, EncounterRequest request) {
        log.info("Updating encounter: {}", id);

        Encounter encounter = findEncounterById(id);

        // Check if encounter can be updated
        if (encounter.isCompleted() || encounter.getStatus() == EncounterStatus.CANCELLED) {
            throw new BusinessException("Cannot update completed or cancelled encounter");
        }

        // Update fields
        updateEncounterFromRequest(encounter, request);

        encounter = encounterRepository.save(encounter);
        log.info("Encounter updated: {}", id);

        return mapToResponse(encounter);
    }

    /**
     * Update encounter status.
     */
    public EncounterResponse updateStatus(UUID id, EncounterStatus newStatus, String reason) {
        log.info("Updating encounter {} status to: {}", id, newStatus);

        Encounter encounter = findEncounterById(id);
        EncounterStatus oldStatus = encounter.getStatus();

        // Use the encounter state machine - handles validation, business rules, and status history
        encounter.changeStatus(newStatus, reason, getCurrentUser(), getCurrentUserId());

        // Save encounter (status history is cascade persisted)
        encounter = encounterRepository.save(encounter);

        // Sync queue status with encounter status change
        queueIntegrationService.syncQueueStatus(encounter);

        log.info("Encounter status updated from {} to {}", oldStatus, newStatus);

        return mapToResponse(encounter);
    }

    /**
     * Start encounter (change to IN_PROGRESS).
     */
    public EncounterResponse startEncounter(UUID id) {
        return updateStatus(id, EncounterStatus.IN_PROGRESS, "Encounter started");
    }

    /**
     * Finish encounter.
     * Business rules are validated by the encounter state machine.
     */
    public EncounterResponse finishEncounter(UUID id) {
        log.info("Finishing encounter: {}", id);
        // Business rules are validated automatically in updateStatus -> changeStatus
        return updateStatus(id, EncounterStatus.FINISHED, "Encounter selesai");
    }

    /**
     * Cancel encounter.
     */
    public EncounterResponse cancelEncounter(UUID id, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Alasan pembatalan wajib diisi");
        }
        return updateStatus(id, EncounterStatus.CANCELLED, reason);
    }

    /**
     * Add participant to encounter.
     */
    public EncounterParticipantDto addParticipant(UUID encounterId, EncounterParticipantDto participantDto) {
        log.info("Adding participant to encounter: {}", encounterId);

        Encounter encounter = findEncounterById(encounterId);

        // Check for duplicates
        if (participantRepository.existsByEncounterIdAndPractitionerIdAndParticipantType(
            encounterId,
            participantDto.getPractitionerId(),
            participantDto.getParticipantType()
        )) {
            throw new DuplicateResourceException(
                "Practitioner already participating in this encounter with the same role"
            );
        }

        // Build participant
        EncounterParticipant participant = EncounterParticipant.builder()
            .encounter(encounter)
            .practitionerId(participantDto.getPractitionerId())
            .participantType(participantDto.getParticipantType())
            .participantName(participantDto.getParticipantName())
            .participantRole(participantDto.getParticipantRole())
            .periodStart(participantDto.getPeriodStart() != null ?
                participantDto.getPeriodStart() : LocalDateTime.now())
            .periodEnd(participantDto.getPeriodEnd())
            .notes(participantDto.getNotes())
            .build();

        participant = participantRepository.save(participant);
        log.info("Participant added to encounter");

        return mapParticipantToDto(participant);
    }

    /**
     * Add diagnosis to encounter.
     */
    public EncounterDiagnosisDto addDiagnosis(UUID encounterId, EncounterDiagnosisDto diagnosisDto) {
        log.info("Adding diagnosis to encounter: {}", encounterId);

        Encounter encounter = findEncounterById(encounterId);

        // Build diagnosis
        EncounterDiagnosis diagnosis = EncounterDiagnosis.builder()
            .encounter(encounter)
            .diagnosisId(diagnosisDto.getDiagnosisId())
            .diagnosisCode(diagnosisDto.getDiagnosisCode())
            .diagnosisText(diagnosisDto.getDiagnosisText())
            .diagnosisType(diagnosisDto.getDiagnosisType())
            .clinicalStatus(diagnosisDto.getClinicalStatus() != null ?
                diagnosisDto.getClinicalStatus() : ClinicalStatus.ACTIVE)
            .rank(diagnosisDto.getRank() != null ? diagnosisDto.getRank() : 1)
            .verificationStatus(diagnosisDto.getVerificationStatus())
            .onsetDate(diagnosisDto.getOnsetDate())
            .recordedDate(LocalDateTime.now())
            .severity(diagnosisDto.getSeverity())
            .diagnosedById(diagnosisDto.getDiagnosedById())
            .diagnosedByName(diagnosisDto.getDiagnosedByName())
            .clinicalNotes(diagnosisDto.getClinicalNotes())
            .build();

        diagnosis = diagnosisRepository.save(diagnosis);
        log.info("Diagnosis added to encounter");

        return mapDiagnosisToDto(diagnosis);
    }

    /**
     * Get encounter participants.
     */
    @Transactional(readOnly = true)
    public List<EncounterParticipantDto> getEncounterParticipants(UUID encounterId) {
        List<EncounterParticipant> participants = participantRepository.findByEncounterId(encounterId);
        return participants.stream()
            .map(this::mapParticipantToDto)
            .collect(Collectors.toList());
    }

    /**
     * Get encounter diagnoses.
     */
    @Transactional(readOnly = true)
    public List<EncounterDiagnosisDto> getEncounterDiagnoses(UUID encounterId) {
        List<EncounterDiagnosis> diagnoses = diagnosisRepository.findByEncounterIdOrderByRankAsc(encounterId);
        return diagnoses.stream()
            .map(this::mapDiagnosisToDto)
            .collect(Collectors.toList());
    }

    /**
     * Get encounter status history.
     */
    @Transactional(readOnly = true)
    public List<EncounterStatusHistoryDto> getEncounterStatusHistory(UUID encounterId) {
        List<EncounterStatusHistory> history = statusHistoryRepository.findByEncounterIdOrderByStatusChangedAtDesc(encounterId);
        return history.stream()
            .map(this::mapStatusHistoryToDto)
            .collect(Collectors.toList());
    }

    /**
     * Delete encounter (soft delete if supported).
     */
    public void deleteEncounter(UUID id) {
        log.info("Deleting encounter: {}", id);
        Encounter encounter = findEncounterById(id);

        // Only allow deletion of planned/cancelled encounters
        if (encounter.getStatus() != EncounterStatus.PLANNED &&
            encounter.getStatus() != EncounterStatus.CANCELLED) {
            throw new BusinessException("Only planned or cancelled encounters can be deleted");
        }

        encounterRepository.delete(encounter);
        log.info("Encounter deleted: {}", id);
    }

    // ========== Private Helper Methods ==========

    private Encounter findEncounterById(UUID id) {
        return encounterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter tidak ditemukan dengan ID: " + id));
    }

    private void validateEncounterRequest(EncounterRequest request) {
        // Ensure exactly one registration reference is provided
        int registrationCount = 0;
        if (request.getOutpatientRegistrationId() != null) registrationCount++;
        if (request.getInpatientAdmissionId() != null) registrationCount++;
        if (request.getEmergencyRegistrationId() != null) registrationCount++;

        if (registrationCount != 1) {
            throw new ValidationException("Exactly one registration ID must be provided");
        }

        // Validate BPJS fields
        if (request.getInsuranceType() != null && request.getInsuranceType().isBpjs()) {
            if (request.getSepNumber() == null || request.getSepNumber().isEmpty()) {
                throw new ValidationException("SEP number is required for BPJS encounters");
            }
        }
    }

    private void checkForActiveEncounters(UUID patientId, EncounterType encounterType) {
        List<EncounterStatus> activeStatuses = Arrays.asList(
            EncounterStatus.PLANNED,
            EncounterStatus.ARRIVED,
            EncounterStatus.TRIAGED,
            EncounterStatus.IN_PROGRESS
        );

        long activeCount = encounterRepository.countActiveEncountersByPatientId(patientId, activeStatuses);

        if (activeCount > 0 && encounterType == EncounterType.INPATIENT) {
            log.warn("Patient {} already has {} active encounter(s)", patientId, activeCount);
            // Consider if you want to throw an exception or just log a warning
        }
    }

    private Encounter buildEncounterFromRequest(EncounterRequest request) {
        return Encounter.builder()
            .patientId(request.getPatientId())
            .encounterType(request.getEncounterType())
            .encounterClass(request.getEncounterClass())
            .outpatientRegistrationId(request.getOutpatientRegistrationId())
            .inpatientAdmissionId(request.getInpatientAdmissionId())
            .emergencyRegistrationId(request.getEmergencyRegistrationId())
            .encounterStart(request.getEncounterStart() != null ?
                request.getEncounterStart() : LocalDateTime.now())
            .status(EncounterStatus.PLANNED)
            .departmentId(request.getDepartmentId())
            .locationId(request.getLocationId())
            .currentDepartment(request.getCurrentDepartment())
            .currentLocation(request.getCurrentLocation())
            .practitionerId(request.getPractitionerId())
            .referringPractitionerId(request.getReferringPractitionerId())
            .attendingDoctorId(request.getAttendingDoctorId())
            .attendingDoctorName(request.getAttendingDoctorName())
            .primaryNurseId(request.getPrimaryNurseId())
            .primaryNurseName(request.getPrimaryNurseName())
            .priority(request.getPriority())
            .serviceType(request.getServiceType())
            .reasonForVisit(request.getReasonForVisit())
            .chiefComplaint(request.getChiefComplaint())
            .insuranceType(request.getInsuranceType())
            .insuranceNumber(request.getInsuranceNumber())
            .sepNumber(request.getSepNumber())
            .sepDate(request.getSepDate())
            .encounterNotes(request.getEncounterNotes())
            .build();
    }

    private void updateEncounterFromRequest(Encounter encounter, EncounterRequest request) {
        if (request.getDepartmentId() != null) {
            encounter.setDepartmentId(request.getDepartmentId());
        }
        if (request.getLocationId() != null) {
            encounter.setLocationId(request.getLocationId());
        }
        if (request.getCurrentDepartment() != null) {
            encounter.setCurrentDepartment(request.getCurrentDepartment());
        }
        if (request.getCurrentLocation() != null) {
            encounter.setCurrentLocation(request.getCurrentLocation());
        }
        if (request.getPriority() != null) {
            encounter.setPriority(request.getPriority());
        }
        if (request.getServiceType() != null) {
            encounter.setServiceType(request.getServiceType());
        }
        if (request.getReasonForVisit() != null) {
            encounter.setReasonForVisit(request.getReasonForVisit());
        }
        if (request.getChiefComplaint() != null) {
            encounter.setChiefComplaint(request.getChiefComplaint());
        }
        if (request.getEncounterNotes() != null) {
            encounter.setEncounterNotes(request.getEncounterNotes());
        }
    }

    private String generateEncounterNumber() {
        LocalDate today = LocalDate.now();
        String prefix = "ENC-" + today.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";

        // Find the highest number for today
        String pattern = prefix + "%";
        int sequenceNumber = 1;

        // Simple sequential numbering - in production, use a sequence table
        while (encounterRepository.existsByEncounterNumber(prefix + String.format("%04d", sequenceNumber))) {
            sequenceNumber++;
        }

        return prefix + String.format("%04d", sequenceNumber);
    }

    /**
     * Validate encounter before finishing.
     * Implements validation rules:
     * - Must have at least one diagnosis
     * - Must have attending practitioner assigned
     * - BPJS encounters require SEP number
     */
    private void validateEncounterBeforeFinish(Encounter encounter) {
        List<String> errors = new ArrayList<>();

        // Rule 1: Must have at least one diagnosis before finish
        long diagnosisCount = diagnosisRepository.countByEncounterId(encounter.getId());
        if (diagnosisCount == 0) {
            errors.add("Encounter harus memiliki minimal 1 diagnosis sebelum diselesaikan");
        }

        // Rule 2: Must have attending practitioner assigned
        if (encounter.getAttendingDoctorId() == null && encounter.getPractitionerId() == null) {
            errors.add("Encounter harus memiliki dokter yang bertugas (attending practitioner)");
        }

        // Rule 3: Insurance encounters require SEP number (BPJS)
        if (encounter.getIsBpjs() != null && encounter.getIsBpjs()) {
            if (encounter.getSepNumber() == null || encounter.getSepNumber().trim().isEmpty()) {
                errors.add("Encounter BPJS wajib memiliki nomor SEP");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(
                "Validasi gagal: " + String.join("; ", errors)
            );
        }
    }

    private void validateStatusTransition(EncounterStatus from, EncounterStatus to) {
        // Define valid transitions
        boolean valid = switch (from) {
            case PLANNED -> to == EncounterStatus.ARRIVED || to == EncounterStatus.CANCELLED;
            case ARRIVED -> to == EncounterStatus.TRIAGED || to == EncounterStatus.IN_PROGRESS ||
                           to == EncounterStatus.CANCELLED;
            case TRIAGED -> to == EncounterStatus.IN_PROGRESS || to == EncounterStatus.CANCELLED;
            case IN_PROGRESS -> to == EncounterStatus.FINISHED || to == EncounterStatus.CANCELLED;
            case FINISHED -> false; // Cannot transition from FINISHED
            case CANCELLED -> false; // Cannot transition from CANCELLED
        };

        if (!valid) {
            throw new BusinessException(
                String.format("Invalid status transition from %s to %s", from, to)
            );
        }
    }

    private void addStatusHistory(Encounter encounter, EncounterStatus fromStatus,
                                  EncounterStatus toStatus, String reason, String notes) {
        EncounterStatusHistory history = EncounterStatusHistory.builder()
            .encounter(encounter)
            .fromStatus(fromStatus)
            .toStatus(toStatus)
            .statusChangedAt(LocalDateTime.now())
            .changedById(getCurrentUserId())
            .changedByName(getCurrentUser())
            .reason(reason)
            .notes(notes)
            .build();

        statusHistoryRepository.save(history);
    }

    private Pageable buildPageable(EncounterSearchCriteria criteria) {
        int page = criteria.getPage() != null ? criteria.getPage() : 0;
        int size = criteria.getSize() != null ? criteria.getSize() : 20;
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "encounterStart";
        Sort.Direction direction = "ASC".equalsIgnoreCase(criteria.getSortDirection()) ?
            Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private String getCurrentUser() {
        // TODO: Get from security context
        return "system";
    }

    private UUID getCurrentUserId() {
        // TODO: Get from security context
        return null;
    }

    // ========== Mapping Methods ==========

    private EncounterResponse mapToResponse(Encounter encounter) {
        return EncounterResponse.builder()
            .id(encounter.getId())
            .encounterNumber(encounter.getEncounterNumber())
            .patientId(encounter.getPatientId())
            .encounterType(encounter.getEncounterType())
            .encounterClass(encounter.getEncounterClass())
            .outpatientRegistrationId(encounter.getOutpatientRegistrationId())
            .inpatientAdmissionId(encounter.getInpatientAdmissionId())
            .emergencyRegistrationId(encounter.getEmergencyRegistrationId())
            .encounterStart(encounter.getEncounterStart())
            .encounterEnd(encounter.getEncounterEnd())
            .durationHours(encounter.getDurationHours())
            .lengthOfStayHours(encounter.getLengthOfStayHours())
            .lengthOfStayDays(encounter.getLengthOfStayDays())
            .status(encounter.getStatus())
            .departmentId(encounter.getDepartmentId())
            .locationId(encounter.getLocationId())
            .currentDepartment(encounter.getCurrentDepartment())
            .currentLocation(encounter.getCurrentLocation())
            .admittingDepartment(encounter.getAdmittingDepartment())
            .practitionerId(encounter.getPractitionerId())
            .referringPractitionerId(encounter.getReferringPractitionerId())
            .attendingDoctorId(encounter.getAttendingDoctorId())
            .attendingDoctorName(encounter.getAttendingDoctorName())
            .primaryNurseId(encounter.getPrimaryNurseId())
            .primaryNurseName(encounter.getPrimaryNurseName())
            .priority(encounter.getPriority())
            .serviceType(encounter.getServiceType())
            .reasonForVisit(encounter.getReasonForVisit())
            .chiefComplaint(encounter.getChiefComplaint())
            .dischargeDisposition(encounter.getDischargeDisposition())
            .dischargeDate(encounter.getDischargeDate())
            .dischargeSummaryId(encounter.getDischargeSummaryId())
            .referredFrom(encounter.getReferredFrom())
            .referredTo(encounter.getReferredTo())
            .referralId(encounter.getReferralId())
            .insuranceType(encounter.getInsuranceType())
            .insuranceNumber(encounter.getInsuranceNumber())
            .isBpjs(encounter.getIsBpjs())
            .sepNumber(encounter.getSepNumber())
            .sepDate(encounter.getSepDate())
            .insuranceProvider(encounter.getInsuranceProvider())
            .satusehatEncounterId(encounter.getSatusehatEncounterId())
            .satusehatSynced(encounter.getSatusehatSynced())
            .satusehatSyncedAt(encounter.getSatusehatSyncedAt())
            .billingStatus(encounter.getBillingStatus())
            .totalCharges(encounter.getTotalCharges())
            .encounterNotes(encounter.getEncounterNotes())
            .cancelledAt(encounter.getCancelledAt())
            .cancelledBy(encounter.getCancelledBy())
            .cancellationReason(encounter.getCancellationReason())
            .participants(encounter.getParticipants().stream()
                .map(this::mapParticipantToDto).collect(Collectors.toList()))
            .diagnoses(encounter.getDiagnoses().stream()
                .map(this::mapDiagnosisToDto).collect(Collectors.toList()))
            .statusHistory(encounter.getStatusHistory().stream()
                .map(this::mapStatusHistoryToDto).collect(Collectors.toList()))
            .createdAt(encounter.getCreatedAt())
            .updatedAt(encounter.getUpdatedAt())
            .createdBy(encounter.getCreatedBy())
            .updatedBy(encounter.getUpdatedBy())
            .build();
    }

    private EncounterSummaryDto mapToSummary(Encounter encounter) {
        // Get primary diagnosis if available
        String primaryDiagnosis = encounter.getDiagnoses().stream()
            .filter(EncounterDiagnosis::isPrimary)
            .findFirst()
            .map(EncounterDiagnosis::getDiagnosisText)
            .orElse(null);

        return EncounterSummaryDto.builder()
            .id(encounter.getId())
            .encounterNumber(encounter.getEncounterNumber())
            .patientId(encounter.getPatientId())
            .encounterType(encounter.getEncounterType())
            .encounterClass(encounter.getEncounterClass())
            .status(encounter.getStatus())
            .encounterStart(encounter.getEncounterStart())
            .encounterEnd(encounter.getEncounterEnd())
            .currentDepartment(encounter.getCurrentDepartment())
            .attendingDoctorName(encounter.getAttendingDoctorName())
            .primaryDiagnosis(primaryDiagnosis)
            .isBpjs(encounter.getIsBpjs())
            .createdAt(encounter.getCreatedAt())
            .build();
    }

    private EncounterParticipantDto mapParticipantToDto(EncounterParticipant participant) {
        return EncounterParticipantDto.builder()
            .id(participant.getId())
            .practitionerId(participant.getPractitionerId())
            .participantType(participant.getParticipantType())
            .participantName(participant.getParticipantName())
            .participantRole(participant.getParticipantRole())
            .periodStart(participant.getPeriodStart())
            .periodEnd(participant.getPeriodEnd())
            .notes(participant.getNotes())
            .build();
    }

    private EncounterDiagnosisDto mapDiagnosisToDto(EncounterDiagnosis diagnosis) {
        return EncounterDiagnosisDto.builder()
            .id(diagnosis.getId())
            .diagnosisId(diagnosis.getDiagnosisId())
            .diagnosisCode(diagnosis.getDiagnosisCode())
            .diagnosisText(diagnosis.getDiagnosisText())
            .diagnosisType(diagnosis.getDiagnosisType())
            .clinicalStatus(diagnosis.getClinicalStatus())
            .rank(diagnosis.getRank())
            .verificationStatus(diagnosis.getVerificationStatus())
            .onsetDate(diagnosis.getOnsetDate())
            .recordedDate(diagnosis.getRecordedDate())
            .severity(diagnosis.getSeverity())
            .diagnosedById(diagnosis.getDiagnosedById())
            .diagnosedByName(diagnosis.getDiagnosedByName())
            .clinicalNotes(diagnosis.getClinicalNotes())
            .build();
    }

    private EncounterStatusHistoryDto mapStatusHistoryToDto(EncounterStatusHistory history) {
        return EncounterStatusHistoryDto.builder()
            .id(history.getId())
            .fromStatus(history.getFromStatus())
            .toStatus(history.getToStatus())
            .statusChangedAt(history.getStatusChangedAt())
            .changedById(history.getChangedById())
            .changedByName(history.getChangedByName())
            .reason(history.getReason())
            .notes(history.getNotes())
            .transitionDescription(history.getTransitionDescription())
            .transitionDescriptionIndonesian(history.getTransitionDescriptionIndonesian())
            .build();
    }
}
