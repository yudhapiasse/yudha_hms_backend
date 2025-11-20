package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.entity.CommonDiagnosis;
import com.yudha.hms.clinical.entity.EncounterDiagnosis;
import com.yudha.hms.clinical.entity.ICD10Code;
import com.yudha.hms.clinical.repository.CommonDiagnosisRepository;
import com.yudha.hms.clinical.repository.EncounterDiagnosisRepository;
import com.yudha.hms.clinical.repository.ICD10CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Diagnosis Service.
 *
 * Provides diagnosis search, autocomplete, history, and common diagnosis management.
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
public class DiagnosisService {

    private final ICD10CodeRepository icd10CodeRepository;
    private final CommonDiagnosisRepository commonDiagnosisRepository;
    private final EncounterDiagnosisRepository encounterDiagnosisRepository;

    // ========== ICD-10 Code Search and Autocomplete ==========

    /**
     * Search for diagnosis codes with pagination.
     */
    public Page<ICD10Code> searchDiagnoses(String searchTerm, Pageable pageable) {
        log.debug("Searching diagnoses with term: {}", searchTerm);
        return icd10CodeRepository.searchDiagnoses(searchTerm, pageable);
    }

    /**
     * Autocomplete suggestions for diagnosis input (returns top 10).
     */
    public List<ICD10Code> getAutocompleteSuggestions(String prefix) {
        log.debug("Getting autocomplete suggestions for: {}", prefix);
        return icd10CodeRepository.findAutocompleteSuggestions(prefix);
    }

    /**
     * Advanced search with multiple criteria.
     */
    public Page<ICD10Code> advancedSearch(
        String code,
        String description,
        String chapterCode,
        String categoryCode,
        Boolean isBillable,
        Pageable pageable
    ) {
        log.debug("Advanced search - code: {}, description: {}, chapter: {}, category: {}, billable: {}",
            code, description, chapterCode, categoryCode, isBillable);
        return icd10CodeRepository.advancedSearch(
            code, description, chapterCode, categoryCode, isBillable, pageable
        );
    }

    /**
     * Get ICD-10 code by code value.
     */
    public Optional<ICD10Code> findByCode(String code) {
        return icd10CodeRepository.findByCode(code);
    }

    /**
     * Get common (frequently used) diagnosis codes.
     */
    public List<ICD10Code> getCommonDiagnoses() {
        return icd10CodeRepository.findByIsCommonTrueAndIsActiveTrueOrderByUsageCountDesc();
    }

    /**
     * Get most used diagnosis codes for analytics.
     */
    public Page<ICD10Code> getMostUsedCodes(int pageSize) {
        return icd10CodeRepository.findMostUsedCodes(PageRequest.of(0, pageSize));
    }

    // ========== Common Diagnoses per Department ==========

    /**
     * Get top diagnoses for a department (quick selection).
     */
    public List<CommonDiagnosis> getTopDiagnosesByDepartment(String departmentCode) {
        log.debug("Getting top diagnoses for department: {}", departmentCode);
        return commonDiagnosisRepository.findTopDiagnosesByDepartment(departmentCode);
    }

    /**
     * Get top N diagnoses for a department.
     */
    public List<CommonDiagnosis> getTopNDiagnoses(String departmentCode, int limit) {
        log.debug("Getting top {} diagnoses for department: {}", limit, departmentCode);
        return commonDiagnosisRepository.findTopNDiagnosesByDepartment(departmentCode, limit);
    }

    /**
     * Get pinned diagnoses for quick access.
     */
    public List<CommonDiagnosis> getPinnedDiagnoses(String departmentCode) {
        return commonDiagnosisRepository
            .findByDepartmentCodeAndIsPinnedTrueAndIsActiveTrueOrderByRankOrderAsc(departmentCode);
    }

    // ========== Diagnosis History ==========

    /**
     * Get diagnosis history for a patient.
     */
    public List<EncounterDiagnosis> getPatientDiagnosisHistory(UUID patientId) {
        log.debug("Getting diagnosis history for patient: {}", patientId);
        return encounterDiagnosisRepository.findDiagnosisHistoryByPatient(patientId);
    }

    /**
     * Get patient history for specific diagnosis code.
     */
    public List<EncounterDiagnosis> getPatientDiagnosisHistoryByCode(UUID patientId, String diagnosisCode) {
        log.debug("Getting diagnosis history for patient: {} with code: {}", patientId, diagnosisCode);
        return encounterDiagnosisRepository.findPatientDiagnosisHistory(patientId, diagnosisCode);
    }

    /**
     * Get all unique diagnoses for a patient.
     */
    public List<Object[]> getUniquePatientDiagnoses(UUID patientId) {
        return encounterDiagnosisRepository.findUniquePatientDiagnoses(patientId);
    }

    /**
     * Get recurring/chronic diagnoses for a patient.
     */
    public List<Object[]> getRecurringDiagnoses(UUID patientId) {
        return encounterDiagnosisRepository.findRecurringDiagnoses(patientId, 3L);
    }

    /**
     * Get active diagnoses for a patient.
     */
    public List<EncounterDiagnosis> getActivePatientDiagnoses(UUID patientId) {
        return encounterDiagnosisRepository.findActivePatientDiagnoses(patientId);
    }

    /**
     * Check if patient has been diagnosed with specific condition.
     */
    public boolean hasPatientBeenDiagnosedWith(UUID patientId, String diagnosisCode) {
        return encounterDiagnosisRepository.hasPatientBeenDiagnosedWith(patientId, diagnosisCode);
    }

    // ========== Insurance Validation ==========

    /**
     * Validate diagnosis for insurance claims.
     */
    public ValidationResult validateDiagnosisForInsurance(String diagnosisCode) {
        Optional<ICD10Code> icd10Optional = icd10CodeRepository.findByCode(diagnosisCode);

        if (icd10Optional.isEmpty()) {
            return ValidationResult.invalid("Diagnosis code not found in ICD-10 master data");
        }

        ICD10Code icd10 = icd10Optional.get();

        if (!icd10.getIsActive()) {
            String message = "Diagnosis code is deprecated";
            if (icd10.getReplacedByCode() != null) {
                message += ". Use code: " + icd10.getReplacedByCode();
            }
            return ValidationResult.invalid(message);
        }

        if (!icd10.getIsBillable()) {
            return ValidationResult.warning("Diagnosis code is not billable to insurance");
        }

        if (icd10.getRequiresAdditionalInfo()) {
            return ValidationResult.warning(
                "Additional documentation required: " +
                (icd10.getInsuranceNotes() != null ? icd10.getInsuranceNotes() : "See coding guidelines")
            );
        }

        return ValidationResult.valid();
    }

    /**
     * Get codes that require additional documentation.
     */
    public List<ICD10Code> getCodesRequiringAdditionalInfo() {
        return icd10CodeRepository.findByRequiresAdditionalInfoTrueAndIsActiveTrue();
    }

    // ========== Usage Tracking and Statistics ==========

    /**
     * Increment usage count for ICD-10 code.
     */
    @Transactional
    public void incrementCodeUsage(String code) {
        icd10CodeRepository.findByCode(code).ifPresent(icd10 -> {
            icd10.incrementUsage();
            icd10.markAsCommonIfPopular(100L); // Mark as common if used 100+ times
            icd10CodeRepository.save(icd10);
        });
    }

    /**
     * Get diagnosis usage statistics since date.
     */
    public List<Object[]> getDiagnosisUsageStats(LocalDateTime since) {
        return encounterDiagnosisRepository.countDiagnosisUsageSince(since);
    }

    /**
     * Get diagnosis statistics by department.
     */
    public List<Object[]> getDiagnosisStatsByDepartment(LocalDateTime since) {
        return encounterDiagnosisRepository.getDiagnosisStatsByDepartment(since);
    }

    /**
     * Recalculate common diagnoses for a department based on usage.
     */
    @Transactional
    public void recalculateCommonDiagnoses(String departmentCode, LocalDateTime sinceDate) {
        log.info("Recalculating common diagnoses for department: {}", departmentCode);

        // Get usage stats for this department
        List<Object[]> stats = encounterDiagnosisRepository.getDiagnosisStatsByDepartment(sinceDate);

        // Filter for this department and update common diagnoses
        // Implementation would analyze stats and update CommonDiagnosis entities
        // This is a placeholder for the actual calculation logic

        log.info("Recalculation complete for department: {}", departmentCode);
    }

    /**
     * Validation result helper class.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String status; // VALID, WARNING, INVALID
        private final String message;

        private ValidationResult(boolean valid, String status, String message) {
            this.valid = valid;
            this.status = status;
            this.message = message;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, "VALID", null);
        }

        public static ValidationResult warning(String message) {
            return new ValidationResult(true, "WARNING", message);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, "INVALID", message);
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
