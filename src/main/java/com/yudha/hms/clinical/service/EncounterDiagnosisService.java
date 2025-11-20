package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.EncounterDiagnosisRepository;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.clinical.repository.ICD10CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Encounter Diagnosis Service.
 *
 * Manages encounter diagnoses with insurance validation and business rules.
 * Implements Phase 4.2 Diagnosis Management requirements.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EncounterDiagnosisService {

    private final EncounterDiagnosisRepository encounterDiagnosisRepository;
    private final ICD10CodeRepository icd10CodeRepository;
    private final EncounterRepository encounterRepository;
    private final DiagnosisService diagnosisService;

    // ========== Create and Update ==========

    /**
     * Add diagnosis to encounter with validation.
     */
    @Transactional
    public EncounterDiagnosis addDiagnosis(
        UUID encounterId,
        String diagnosisCode,
        DiagnosisType diagnosisType,
        UUID diagnosedById,
        String diagnosedByName
    ) {
        log.debug("Adding diagnosis {} to encounter {}", diagnosisCode, encounterId);

        // Validate encounter exists
        Encounter encounter = encounterRepository.findById(encounterId)
            .orElseThrow(() -> new IllegalArgumentException("Encounter not found: " + encounterId));

        // Validate diagnosis code
        DiagnosisService.ValidationResult validation = diagnosisService.validateDiagnosisForInsurance(diagnosisCode);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Invalid diagnosis code: " + validation.getMessage());
        }

        // Log warning if any
        if (validation.hasWarning()) {
            log.warn("Diagnosis validation warning for {}: {}", diagnosisCode, validation.getMessage());
        }

        // Get ICD-10 code details
        ICD10Code icd10 = icd10CodeRepository.findByCode(diagnosisCode)
            .orElseThrow(() -> new IllegalArgumentException("ICD-10 code not found: " + diagnosisCode));

        // Determine rank
        long existingCount = encounterDiagnosisRepository.countByEncounterId(encounterId);
        int rank = diagnosisType == DiagnosisType.PRIMARY ? 1 : (int) (existingCount + 1);

        // If setting as primary, update existing primary to secondary
        if (diagnosisType == DiagnosisType.PRIMARY) {
            updateExistingPrimaryToSecondary(encounterId);
        }

        // Create diagnosis
        EncounterDiagnosis diagnosis = EncounterDiagnosis.builder()
            .encounter(encounter)
            .diagnosisId(icd10.getId())
            .diagnosisCode(diagnosisCode)
            .diagnosisText(icd10.getDescriptionId()) // Use Indonesian description
            .diagnosisType(diagnosisType)
            .clinicalStatus(ClinicalStatus.ACTIVE)
            .rank(rank)
            .verificationStatus("PROVISIONAL")
            .recordedDate(LocalDateTime.now())
            .diagnosedById(diagnosedById)
            .diagnosedByName(diagnosedByName)
            .build();

        EncounterDiagnosis saved = encounterDiagnosisRepository.save(diagnosis);

        // Track usage
        diagnosisService.incrementCodeUsage(diagnosisCode);

        log.info("Added diagnosis {} to encounter {} as {}", diagnosisCode, encounterId, diagnosisType);
        return saved;
    }

    /**
     * Update existing primary diagnosis to secondary.
     */
    @Transactional
    protected void updateExistingPrimaryToSecondary(UUID encounterId) {
        encounterDiagnosisRepository.findPrimaryDiagnosisByEncounterId(encounterId)
            .ifPresent(existingPrimary -> {
                existingPrimary.setDiagnosisType(DiagnosisType.SECONDARY);
                existingPrimary.setRank(2);
                encounterDiagnosisRepository.save(existingPrimary);
                log.debug("Updated previous primary diagnosis to secondary for encounter {}", encounterId);
            });
    }

    /**
     * Update diagnosis details.
     */
    @Transactional
    public EncounterDiagnosis updateDiagnosis(
        UUID diagnosisId,
        String clinicalNotes,
        String severity,
        String verificationStatus
    ) {
        EncounterDiagnosis diagnosis = encounterDiagnosisRepository.findById(diagnosisId)
            .orElseThrow(() -> new IllegalArgumentException("Diagnosis not found: " + diagnosisId));

        if (clinicalNotes != null) {
            diagnosis.setClinicalNotes(clinicalNotes);
        }
        if (severity != null) {
            diagnosis.setSeverity(severity);
        }
        if (verificationStatus != null) {
            diagnosis.setVerificationStatus(verificationStatus);
        }

        return encounterDiagnosisRepository.save(diagnosis);
    }

    /**
     * Confirm diagnosis.
     */
    @Transactional
    public EncounterDiagnosis confirmDiagnosis(UUID diagnosisId) {
        EncounterDiagnosis diagnosis = encounterDiagnosisRepository.findById(diagnosisId)
            .orElseThrow(() -> new IllegalArgumentException("Diagnosis not found: " + diagnosisId));

        diagnosis.confirm();
        return encounterDiagnosisRepository.save(diagnosis);
    }

    /**
     * Mark diagnosis as resolved.
     */
    @Transactional
    public EncounterDiagnosis resolveDiagnosis(UUID diagnosisId) {
        EncounterDiagnosis diagnosis = encounterDiagnosisRepository.findById(diagnosisId)
            .orElseThrow(() -> new IllegalArgumentException("Diagnosis not found: " + diagnosisId));

        diagnosis.markAsResolved();
        return encounterDiagnosisRepository.save(diagnosis);
    }

    /**
     * Set as primary diagnosis.
     */
    @Transactional
    public EncounterDiagnosis setAsPrimaryDiagnosis(UUID diagnosisId) {
        EncounterDiagnosis diagnosis = encounterDiagnosisRepository.findById(diagnosisId)
            .orElseThrow(() -> new IllegalArgumentException("Diagnosis not found: " + diagnosisId));

        UUID encounterId = diagnosis.getEncounter().getId();

        // Update existing primary to secondary
        updateExistingPrimaryToSecondary(encounterId);

        // Set this as primary
        diagnosis.markAsPrimary();
        return encounterDiagnosisRepository.save(diagnosis);
    }

    /**
     * Remove diagnosis from encounter.
     */
    @Transactional
    public void removeDiagnosis(UUID diagnosisId) {
        log.info("Removing diagnosis: {}", diagnosisId);
        encounterDiagnosisRepository.deleteById(diagnosisId);
    }

    // ========== Query Methods ==========

    /**
     * Get all diagnoses for an encounter.
     */
    public List<EncounterDiagnosis> getEncounterDiagnoses(UUID encounterId) {
        return encounterDiagnosisRepository.findByEncounterIdOrderByRankAsc(encounterId);
    }

    /**
     * Get primary diagnosis for an encounter.
     */
    public Optional<EncounterDiagnosis> getPrimaryDiagnosis(UUID encounterId) {
        return encounterDiagnosisRepository.findPrimaryDiagnosisByEncounterId(encounterId);
    }

    /**
     * Get secondary diagnoses for an encounter.
     */
    public List<EncounterDiagnosis> getSecondaryDiagnoses(UUID encounterId) {
        return encounterDiagnosisRepository.findByEncounterIdAndDiagnosisTypeOrderByRankAsc(
            encounterId,
            DiagnosisType.SECONDARY
        );
    }

    /**
     * Get active diagnoses for an encounter.
     */
    public List<EncounterDiagnosis> getActiveDiagnoses(UUID encounterId) {
        return encounterDiagnosisRepository.findByEncounterIdAndClinicalStatusOrderByRankAsc(
            encounterId,
            ClinicalStatus.ACTIVE
        );
    }

    // ========== Insurance Validation ==========

    /**
     * Validate all diagnoses for an encounter for insurance claims.
     */
    public InsuranceValidationResult validateEncounterDiagnosesForInsurance(UUID encounterId) {
        log.debug("Validating diagnoses for insurance - encounter: {}", encounterId);

        List<EncounterDiagnosis> diagnoses = encounterDiagnosisRepository.findByEncounterIdOrderByRankAsc(encounterId);

        if (diagnoses.isEmpty()) {
            return InsuranceValidationResult.invalid("No diagnoses recorded for encounter");
        }

        // Check for primary diagnosis
        boolean hasPrimary = diagnoses.stream().anyMatch(EncounterDiagnosis::isPrimary);
        if (!hasPrimary) {
            return InsuranceValidationResult.invalid("Primary diagnosis is required for insurance claims");
        }

        // Validate each diagnosis
        StringBuilder warnings = new StringBuilder();
        for (EncounterDiagnosis diagnosis : diagnoses) {
            DiagnosisService.ValidationResult result =
                diagnosisService.validateDiagnosisForInsurance(diagnosis.getDiagnosisCode());

            if (!result.isValid()) {
                return InsuranceValidationResult.invalid(
                    "Invalid diagnosis code: " + diagnosis.getDiagnosisCode() + " - " + result.getMessage()
                );
            }

            if (result.hasWarning()) {
                warnings.append(diagnosis.getDiagnosisCode())
                    .append(": ")
                    .append(result.getMessage())
                    .append("; ");
            }
        }

        if (warnings.length() > 0) {
            return InsuranceValidationResult.warning(warnings.toString());
        }

        return InsuranceValidationResult.valid();
    }

    /**
     * Check if encounter diagnoses meet insurance requirements.
     */
    public boolean meetsInsuranceRequirements(UUID encounterId) {
        InsuranceValidationResult result = validateEncounterDiagnosesForInsurance(encounterId);
        return result.isValid();
    }

    /**
     * Insurance validation result class.
     */
    public static class InsuranceValidationResult {
        private final boolean valid;
        private final String status;
        private final String message;

        private InsuranceValidationResult(boolean valid, String status, String message) {
            this.valid = valid;
            this.status = status;
            this.message = message;
        }

        public static InsuranceValidationResult valid() {
            return new InsuranceValidationResult(true, "VALID", "All diagnoses are valid for insurance claims");
        }

        public static InsuranceValidationResult warning(String message) {
            return new InsuranceValidationResult(true, "WARNING", message);
        }

        public static InsuranceValidationResult invalid(String message) {
            return new InsuranceValidationResult(false, "INVALID", message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public boolean hasWarning() {
            return "WARNING".equals(status);
        }
    }
}
