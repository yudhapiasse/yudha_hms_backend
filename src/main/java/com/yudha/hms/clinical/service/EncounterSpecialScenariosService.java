package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.EncounterDiagnosisRepository;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.clinical.repository.ProgressNoteRepository;
import com.yudha.hms.registration.repository.EmergencyRegistrationRepository;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Encounter Special Scenarios Service.
 *
 * Handles edge cases and special scenarios for encounter management:
 * - Auto-conversion Emergency to Inpatient
 * - Same-day multiple encounters validation
 * - Enhanced encounter cancellation
 * - Encounter reopening with approval
 * - External patient encounter handling
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EncounterSpecialScenariosService {

    private final EncounterRepository encounterRepository;
    private final EncounterDiagnosisRepository diagnosisRepository;
    private final ProgressNoteRepository progressNoteRepository;
    private final EmergencyRegistrationRepository emergencyRegistrationRepository;

    private static final int REOPEN_TIME_LIMIT_HOURS = 24;

    /**
     * Convert emergency encounter to inpatient.
     *
     * Creates a new inpatient encounter linked to the original emergency encounter.
     * Maintains continuity of clinical data with separate billing transactions.
     *
     * @param request Conversion request
     * @return The new inpatient encounter
     */
    public Encounter convertEmergencyToInpatient(ConvertEmergencyToInpatientRequest request) {
        log.info("Converting emergency encounter {} to inpatient", request.getEmergencyEncounterId());

        // Fetch original emergency encounter
        Encounter emergencyEncounter = encounterRepository.findById(request.getEmergencyEncounterId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Emergency encounter not found: " + request.getEmergencyEncounterId()));

        // Validate it's an emergency encounter
        if (emergencyEncounter.getEncounterType() != EncounterType.EMERGENCY) {
            throw new BusinessException(
                "Encounter is not an emergency encounter: " + emergencyEncounter.getEncounterType());
        }

        // Validate emergency encounter is not already finished
        if (emergencyEncounter.getStatus() == EncounterStatus.FINISHED ||
            emergencyEncounter.getStatus() == EncounterStatus.CANCELLED) {
            throw new BusinessException(
                "Cannot convert finished or cancelled emergency encounter");
        }

        // Create new inpatient encounter
        Encounter inpatientEncounter = new Encounter();
        inpatientEncounter.setEncounterType(EncounterType.INPATIENT);
        inpatientEncounter.setEncounterClass(EncounterClass.INPATIENT);
        inpatientEncounter.setPatientId(emergencyEncounter.getPatientId());
        inpatientEncounter.setStatus(EncounterStatus.IN_PROGRESS);
        inpatientEncounter.setPriority(emergencyEncounter.getPriority());

        // TODO: Link to original emergency encounter
        // Note: previousEncounterId field not available in Encounter entity
        // Consider adding this field or using a separate relationship table

        // TODO: Set admission details (bedId, wardId, roomId, admissionReason)
        // Note: These fields should be managed in InpatientAdmission entity
        // For now, we'll store admission details in inpatientAdmissionId

        // Set attending physician
        inpatientEncounter.setAttendingDoctorId(request.getAttendingPhysicianId());
        inpatientEncounter.setDepartmentId(emergencyEncounter.getDepartmentId());

        // Set encounter times
        inpatientEncounter.setEncounterStart(LocalDateTime.now());

        // Copy chief complaint from emergency
        inpatientEncounter.setChiefComplaint(emergencyEncounter.getChiefComplaint());

        // Generate encounter number (should use a service in real implementation)
        inpatientEncounter.setEncounterNumber("ENC-INP-" + System.currentTimeMillis());

        // Set conversion notes
        String conversionNotes = String.format(
            "Converted from emergency encounter %s. %s",
            emergencyEncounter.getEncounterNumber(),
            request.getNotes() != null ? request.getNotes() : ""
        );
        inpatientEncounter.setEncounterNotes(conversionNotes);

        // Save new inpatient encounter
        inpatientEncounter = encounterRepository.save(inpatientEncounter);

        // Update emergency encounter status to FINISHED
        emergencyEncounter.changeStatus(
            EncounterStatus.FINISHED,
            "Converted to inpatient encounter: " + inpatientEncounter.getEncounterNumber(),
            "SYSTEM",
            null
        );
        emergencyEncounter.setEncounterEnd(LocalDateTime.now());
        encounterRepository.save(emergencyEncounter);

        // Copy diagnoses from emergency encounter to new inpatient encounter
        copyDiagnoses(emergencyEncounter, inpatientEncounter);

        log.info("Successfully converted emergency encounter {} to inpatient encounter {}",
            emergencyEncounter.getEncounterNumber(), inpatientEncounter.getEncounterNumber());

        return inpatientEncounter;
    }

    /**
     * Copy diagnoses from one encounter to another.
     */
    private void copyDiagnoses(Encounter sourceEncounter, Encounter targetEncounter) {
        List<EncounterDiagnosis> sourceDiagnoses = diagnosisRepository.findByEncounter(sourceEncounter);

        for (EncounterDiagnosis sourceDiagnosis : sourceDiagnoses) {
            EncounterDiagnosis newDiagnosis = new EncounterDiagnosis();
            newDiagnosis.setEncounter(targetEncounter);
            newDiagnosis.setDiagnosisCode(sourceDiagnosis.getDiagnosisCode());
            newDiagnosis.setDiagnosisText(sourceDiagnosis.getDiagnosisText());
            newDiagnosis.setDiagnosisType(DiagnosisType.ADMISSION); // Change to admission diagnosis
            newDiagnosis.setRank(sourceDiagnosis.getRank()); // Use rank instead of priority
            // Use isPrimary() method and markAsPrimary() if true
            if (sourceDiagnosis.isPrimary()) {
                newDiagnosis.markAsPrimary();
            }
            newDiagnosis.setClinicalStatus(ClinicalStatus.ACTIVE); // Use enum, not string
            newDiagnosis.setOnsetDate(sourceDiagnosis.getOnsetDate());
            newDiagnosis.setClinicalNotes("Copied from emergency encounter: " + sourceEncounter.getEncounterNumber());

            diagnosisRepository.save(newDiagnosis);
        }

        log.info("Copied {} diagnoses from emergency to inpatient encounter", sourceDiagnoses.size());
    }

    /**
     * Check for duplicate or same-day encounters.
     *
     * @param patientId Patient ID
     * @param encounterType Type of new encounter
     * @param departmentId Department ID
     * @return Duplicate check response
     */
    @Transactional(readOnly = true)
    public DuplicateEncounterCheckResponse checkDuplicateEncounters(
            UUID patientId, EncounterType encounterType, UUID departmentId) {

        log.info("Checking duplicate encounters for patient: {}", patientId);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        // Get all encounters for patient today
        List<Encounter> sameDayEncounters = encounterRepository
            .findByPatientIdAndEncounterStartBetween(patientId, startOfDay, endOfDay);

        // Check for active inpatient encounter
        boolean hasActiveInpatient = sameDayEncounters.stream()
            .anyMatch(e -> e.getEncounterType() == EncounterType.INPATIENT &&
                          (e.getStatus() == EncounterStatus.IN_PROGRESS ||
                           e.getStatus() == EncounterStatus.ARRIVED ||
                           e.getStatus() == EncounterStatus.TRIAGED));

        // Build response
        DuplicateEncounterCheckResponse response = DuplicateEncounterCheckResponse.builder()
            .patientId(patientId)
            .hasActiveInpatientEncounter(hasActiveInpatient)
            .hasSameDayEncounters(!sameDayEncounters.isEmpty())
            .build();

        // Convert to same-day encounter DTOs
        List<DuplicateEncounterCheckResponse.SameDayEncounter> sameDayList = sameDayEncounters.stream()
            .map(e -> DuplicateEncounterCheckResponse.SameDayEncounter.builder()
                .encounterId(e.getId())
                .encounterNumber(e.getEncounterNumber())
                .encounterType(e.getEncounterType().name())
                .departmentName("Department") // TODO: Fetch from department entity
                .encounterStart(e.getEncounterStart())
                .status(e.getStatus().name())
                .build())
            .collect(Collectors.toList());
        response.setSameDayEncounters(sameDayList);

        // Check for potential duplicates
        List<DuplicateEncounterCheckResponse.PotentialDuplicateEncounter> potentialDuplicates = new ArrayList<>();

        // Flag if trying to create inpatient when one already active
        if (encounterType == EncounterType.INPATIENT && hasActiveInpatient) {
            response.setHasPotentialDuplicates(true);
            response.setCanProceed(false);
            response.setWarning("Patient already has an active inpatient encounter. Cannot create duplicate inpatient encounter.");

            sameDayEncounters.stream()
                .filter(e -> e.getEncounterType() == EncounterType.INPATIENT)
                .forEach(e -> potentialDuplicates.add(
                    DuplicateEncounterCheckResponse.PotentialDuplicateEncounter.builder()
                        .encounterId(e.getId())
                        .encounterNumber(e.getEncounterNumber())
                        .encounterType(e.getEncounterType().name())
                        .encounterStatus(e.getStatus().name())
                        .encounterStart(e.getEncounterStart())
                        .departmentName("Department")
                        .chiefComplaint(e.getChiefComplaint())
                        .similarityReason("Active inpatient encounter exists")
                        .build()));
        } else if (encounterType == EncounterType.OUTPATIENT) {
            // Allow multiple outpatient encounters but flag same department
            long sameDepartmentCount = sameDayEncounters.stream()
                .filter(e -> e.getEncounterType() == EncounterType.OUTPATIENT)
                .filter(e -> departmentId != null && departmentId.equals(e.getDepartmentId()))
                .count();

            if (sameDepartmentCount > 0) {
                response.setHasPotentialDuplicates(true);
                response.setCanProceed(true); // Can proceed but with warning
                response.setWarning("Patient already has an outpatient encounter in the same department today. Please verify this is not a duplicate.");
            } else {
                response.setCanProceed(true);
            }
        } else {
            response.setCanProceed(true);
        }

        response.setPotentialDuplicates(potentialDuplicates);

        log.info("Duplicate check completed. Has duplicates: {}, Can proceed: {}",
            response.getHasPotentialDuplicates(), response.getCanProceed());

        return response;
    }

    /**
     * Validate if encounter can be cancelled.
     *
     * Checks for linked data (medications, procedures, results, billing).
     *
     * @param encounterId Encounter ID
     * @return Validation response
     */
    @Transactional(readOnly = true)
    public CancellationValidationResponse validateCancellation(UUID encounterId) {
        log.info("Validating cancellation for encounter: {}", encounterId);

        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + encounterId));

        CancellationValidationResponse response = CancellationValidationResponse.builder()
            .canCancel(true)
            .requiresSupervisorApproval(false)
            .blockingReasons(new ArrayList<>())
            .warnings(new ArrayList<>())
            .build();

        // Check if already cancelled or finished
        if (encounter.getStatus() == EncounterStatus.CANCELLED) {
            response.setCanCancel(false);
            response.getBlockingReasons().add("Encounter is already cancelled");
            return response;
        }

        if (encounter.getStatus() == EncounterStatus.FINISHED) {
            response.setCanCancel(false);
            response.getBlockingReasons().add("Cannot cancel finished encounter. Use reopening if needed.");
            return response;
        }

        // Check for progress notes (indicates clinical activity)
        List<ProgressNote> progressNotes = progressNoteRepository.findByEncounterId(encounterId);
        response.setHasProgressNotes(!progressNotes.isEmpty());

        if (!progressNotes.isEmpty()) {
            response.getWarnings().add(progressNotes.size() + " progress note(s) recorded");
            response.setRequiresSupervisorApproval(true);
        }

        // TODO: Check for medications dispensed (requires pharmacy integration)
        // For now, set to 0
        response.setMedicationsDispensed(0);

        // TODO: Check for procedures done (requires procedure tracking)
        response.setProceduresDone(0);

        // TODO: Check for lab/radiology results
        response.setLabResultsRecorded(0);
        response.setRadiologyResultsRecorded(0);

        // TODO: Check for billing transactions
        response.setHasBillingTransactions(false);

        // If medications dispensed or procedures done, cannot cancel without supervisor
        if (response.getMedicationsDispensed() > 0) {
            response.setRequiresSupervisorApproval(true);
            response.getBlockingReasons().add("Medications have been dispensed. Requires supervisor approval and pharmacy reversal.");
        }

        if (response.getProceduresDone() > 0) {
            response.setRequiresSupervisorApproval(true);
            response.getBlockingReasons().add("Procedures have been performed. Requires supervisor approval.");
        }

        // Set recommendation
        if (response.getBlockingReasons().isEmpty()) {
            if (response.getRequiresSupervisorApproval()) {
                response.setRecommendation("Cancellation requires supervisor approval due to clinical activity");
            } else {
                response.setRecommendation("Encounter can be cancelled");
            }
        } else {
            response.setRecommendation("Cancellation blocked. Please review blocking reasons.");
            response.setCanCancel(false);
        }

        log.info("Cancellation validation completed. Can cancel: {}, Requires approval: {}",
            response.getCanCancel(), response.getRequiresSupervisorApproval());

        return response;
    }

    /**
     * Cancel encounter with validation.
     *
     * @param encounterId Encounter ID
     * @param request Cancellation request
     * @return Updated encounter
     */
    public Encounter cancelEncounter(UUID encounterId, CancelEncounterRequest request) {
        log.info("Cancelling encounter: {} by {}", encounterId, request.getCancelledBy());

        // Validate cancellation
        CancellationValidationResponse validation = validateCancellation(encounterId);

        // If cannot cancel and not forcing, throw exception
        if (!validation.getCanCancel() && !Boolean.TRUE.equals(request.getForceCancel())) {
            throw new BusinessException(
                "Cannot cancel encounter: " + String.join(", ", validation.getBlockingReasons()));
        }

        // If requires supervisor approval, check for approval code
        if (validation.getRequiresSupervisorApproval() || Boolean.TRUE.equals(request.getForceCancel())) {
            if (request.getSupervisorApprovalCode() == null || request.getSupervisorApprovalCode().isBlank()) {
                throw new BusinessException(
                    "Supervisor approval code is required for this cancellation");
            }
            // TODO: Validate supervisor approval code
        }

        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + encounterId));

        // Cancel the encounter
        encounter.changeStatus(
            EncounterStatus.CANCELLED,
            request.getCancellationReason(),
            request.getCancelledBy(),
            null
        );
        encounter.setEncounterEnd(LocalDateTime.now());

        if (request.getAdditionalNotes() != null) {
            String notes = encounter.getEncounterNotes() != null ? encounter.getEncounterNotes() + "\n" : "";
            notes += "Cancellation notes: " + request.getAdditionalNotes();
            encounter.setEncounterNotes(notes);
        }

        encounter = encounterRepository.save(encounter);

        // TODO: Handle billing reversal if requested
        if (Boolean.TRUE.equals(request.getReverseBilling())) {
            log.info("Billing reversal requested for encounter: {}", encounterId);
            // Implement billing reversal logic
        }

        log.info("Encounter {} cancelled successfully", encounter.getEncounterNumber());

        return encounter;
    }

    /**
     * Reopen a finished encounter for missed documentation.
     *
     * Requires supervisor approval and must be within time limit.
     *
     * @param encounterId Encounter ID
     * @param request Reopen request
     * @return Reopened encounter
     */
    public Encounter reopenEncounter(UUID encounterId, ReopenEncounterRequest request) {
        log.info("Reopening encounter: {} by {}", encounterId, request.getRequestedBy());

        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + encounterId));

        // Validate can reopen
        if (encounter.getStatus() != EncounterStatus.FINISHED) {
            throw new BusinessException(
                "Only finished encounters can be reopened. Current status: " + encounter.getStatus());
        }

        // Check time limit (24 hours from finish)
        if (encounter.getEncounterEnd() == null) {
            throw new BusinessException("Encounter end time not recorded");
        }

        Duration timeSinceFinish = Duration.between(encounter.getEncounterEnd(), LocalDateTime.now());
        if (timeSinceFinish.toHours() > REOPEN_TIME_LIMIT_HOURS) {
            throw new BusinessException(
                String.format("Encounter finished more than %d hours ago. Cannot reopen. Time since finish: %d hours",
                    REOPEN_TIME_LIMIT_HOURS, timeSinceFinish.toHours()));
        }

        // Validate supervisor approval code
        // TODO: Implement actual supervisor validation
        if (request.getSupervisorApprovalCode() == null || request.getSupervisorApprovalCode().isBlank()) {
            throw new BusinessException("Supervisor approval code is required");
        }

        // Reopen encounter
        encounter.changeStatus(
            EncounterStatus.IN_PROGRESS,
            "Reopened for documentation: " + request.getReopenReason(),
            request.getRequestedBy(),
            request.getSupervisorId()
        );
        encounter.setEncounterEnd(null); // Clear end time

        // Add reopen notes
        String notes = encounter.getEncounterNotes() != null ? encounter.getEncounterNotes() + "\n" : "";
        notes += String.format("REOPENED: %s by %s (Approved by supervisor: %s). Reason: %s",
            LocalDateTime.now(),
            request.getRequestedBy(),
            request.getSupervisorId(),
            request.getReopenReason());
        if (request.getDocumentationNeeded() != null) {
            notes += "\nDocumentation needed: " + request.getDocumentationNeeded();
        }
        encounter.setEncounterNotes(notes);

        encounter = encounterRepository.save(encounter);

        log.info("Encounter {} reopened successfully", encounter.getEncounterNumber());

        return encounter;
    }

    /**
     * Create encounter for external/unknown patient.
     *
     * Handles emergency patients without full registration.
     *
     * @param request External patient encounter request
     * @return Created encounter
     */
    public Encounter createExternalPatientEncounter(ExternalPatientEncounterRequest request) {
        log.info("Creating external patient encounter for: {}", request.getPatientName());

        // TODO: Create or link to patient record
        // For unknown patients, create temporary patient record with special flag
        UUID patientId = request.getLinkedPatientId();
        if (patientId == null) {
            // TODO: Create "Unknown Patient" record
            // For now, throw exception - this should integrate with patient service
            throw new BusinessException(
                "Patient ID required. Unknown patient handling requires patient service integration.");
        }

        // Create encounter
        Encounter encounter = new Encounter();
        encounter.setEncounterType(request.getEncounterType());
        encounter.setEncounterClass(request.getEncounterClass());
        encounter.setPatientId(patientId);
        encounter.setStatus(EncounterStatus.IN_PROGRESS);
        encounter.setPriority(request.getPriority());
        encounter.setChiefComplaint(request.getChiefComplaint());
        encounter.setAttendingDoctorId(request.getAttendingPhysicianId());
        encounter.setDepartmentId(request.getDepartmentId());
        encounter.setEncounterStart(request.getEncounterStart() != null ?
            request.getEncounterStart() : LocalDateTime.now());

        // Generate encounter number
        encounter.setEncounterNumber("ENC-EXT-" + System.currentTimeMillis());

        // Set police case information if applicable
        if (Boolean.TRUE.equals(request.getIsPoliceCase())) {
            String notes = String.format("POLICE CASE: %s (Station: %s)",
                request.getPoliceCaseNumber(),
                request.getPoliceStation());
            encounter.setEncounterNotes(notes);
        }

        // Set brought by information
        if (request.getBroughtByName() != null) {
            String broughtByInfo = String.format("\nBrought by: %s (%s) Contact: %s",
                request.getBroughtByName(),
                request.getBroughtByRelation(),
                request.getBroughtByContact());
            String notes = encounter.getEncounterNotes() != null ? encounter.getEncounterNotes() + broughtByInfo : broughtByInfo;
            encounter.setEncounterNotes(notes);
        }

        // Add initial assessment
        if (request.getInitialAssessment() != null) {
            String assessment = "\nInitial Assessment: " + request.getInitialAssessment();
            String notes = encounter.getEncounterNotes() != null ? encounter.getEncounterNotes() + assessment : assessment;
            encounter.setEncounterNotes(notes);
        }

        // Flag for data completion
        if (Boolean.TRUE.equals(request.getRequiresDataCompletion())) {
            String flag = "\n[REQUIRES DATA COMPLETION]";
            String notes = encounter.getEncounterNotes() != null ? encounter.getEncounterNotes() + flag : flag;
            encounter.setEncounterNotes(notes);
        }

        encounter = encounterRepository.save(encounter);

        log.info("External patient encounter created: {}", encounter.getEncounterNumber());

        return encounter;
    }
}
