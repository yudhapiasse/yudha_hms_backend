package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.constant.ResultStatus;
import com.yudha.hms.laboratory.constant.ValidationLevel;
import com.yudha.hms.laboratory.constant.ValidationStatus;
import com.yudha.hms.laboratory.entity.LabResult;
import com.yudha.hms.laboratory.entity.ResultValidation;
import com.yudha.hms.laboratory.repository.LabResultRepository;
import com.yudha.hms.laboratory.repository.ResultValidationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing laboratory result validation workflow.
 *
 * Implements multi-step validation workflow with different levels:
 * - TECHNICIAN: Initial validation by laboratory technician
 * - SENIOR_TECH: Review by senior technician
 * - PATHOLOGIST: Pathologist review for complex or abnormal results
 * - CLINICAL_REVIEWER: Final clinical review if needed
 *
 * Features:
 * - Multi-level validation tracking
 * - Approval/rejection workflow
 * - Repeat test triggering
 * - Validation history maintenance
 * - Role-based validation verification
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResultValidationService {

    private final ResultValidationRepository resultValidationRepository;
    private final LabResultRepository labResultRepository;

    /**
     * Validate a result at a specific validation level.
     *
     * @param resultId Result ID
     * @param level Validation level
     * @param validatorId Validator user ID
     * @param validatorName Validator name
     * @param status Validation status (APPROVED, REJECTED, NEEDS_REVIEW, NEEDS_REPEAT)
     * @param comments Validation comments
     * @return Created validation record
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if validation is not allowed
     */
    public ResultValidation validateResult(UUID resultId, ValidationLevel level, UUID validatorId,
                                          String validatorName, ValidationStatus status, String comments) {
        log.info("Validating result: {} at level: {} with status: {}", resultId, level, status);

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        // Validate result can be validated
        if (result.getStatus() == ResultStatus.CANCELLED || result.getStatus() == ResultStatus.ENTERED_IN_ERROR) {
            throw new IllegalStateException("Cannot validate cancelled or error results");
        }

        // Get current validation level
        ValidationLevel currentLevel = getCurrentValidationLevel(resultId);

        // Validate sequence (can only validate at current level or next level)
        if (currentLevel != null && level.getLevel() > currentLevel.getLevel() + 1) {
            throw new IllegalStateException(String.format(
                    "Cannot skip validation levels. Current level: %s, Attempted level: %s",
                    currentLevel, level));
        }

        // Create validation record
        ResultValidation validation = ResultValidation.builder()
                .result(result)
                .validationLevel(level)
                .validationStep(level.getLevel())
                .validatedBy(validatorId)
                .validatorName(validatorName)
                .validatedAt(LocalDateTime.now())
                .validationStatus(status)
                .validationNotes(comments)
                .build();

        validation = resultValidationRepository.save(validation);

        // Update result based on validation status
        updateResultStatus(result, level, status, validatorId, validatorName, comments);

        log.info("Validation recorded successfully. Validation ID: {}", validation.getId());
        return validation;
    }

    /**
     * Approve validation at a specific level.
     *
     * @param resultId Result ID
     * @param level Validation level
     * @param validatorId Validator user ID
     * @param validatorName Validator name
     * @return Created validation record
     */
    public ResultValidation approveValidation(UUID resultId, ValidationLevel level, UUID validatorId, String validatorName) {
        return validateResult(resultId, level, validatorId, validatorName, ValidationStatus.APPROVED,
                "Result approved at " + level.getDisplayName() + " level");
    }

    /**
     * Reject validation at a specific level.
     *
     * @param resultId Result ID
     * @param level Validation level
     * @param validatorId Validator user ID
     * @param validatorName Validator name
     * @param rejectionReason Reason for rejection
     * @param requiresRepeatTest Whether the test needs to be repeated
     * @return Created validation record
     */
    public ResultValidation rejectValidation(UUID resultId, ValidationLevel level, UUID validatorId,
                                            String validatorName, String rejectionReason, boolean requiresRepeatTest) {
        log.warn("Rejecting validation for result: {} at level: {}. Repeat required: {}",
                resultId, level, requiresRepeatTest);

        ValidationStatus status = requiresRepeatTest ? ValidationStatus.NEEDS_REPEAT : ValidationStatus.REJECTED;

        ResultValidation validation = validateResult(resultId, level, validatorId, validatorName, status, rejectionReason);

        // If repeat test is required, handle that workflow
        if (requiresRepeatTest) {
            handleRepeatTestRequired(resultId, rejectionReason);
        }

        return validation;
    }

    /**
     * Get validation history for a result.
     *
     * @param resultId Result ID
     * @return List of validation records ordered by step
     */
    @Transactional(readOnly = true)
    public List<ResultValidation> getValidationHistory(UUID resultId) {
        return resultValidationRepository.findByResultIdOrderByValidationStepAsc(resultId);
    }

    /**
     * Get current validation level for a result.
     *
     * @param resultId Result ID
     * @return Current validation level or null if no validations
     */
    @Transactional(readOnly = true)
    public ValidationLevel getCurrentValidationLevel(UUID resultId) {
        ResultValidation latestValidation = resultValidationRepository.findLatestValidationByResultId(resultId);

        if (latestValidation == null) {
            return null;
        }

        // Only return level if it was approved
        if (latestValidation.getValidationStatus() == ValidationStatus.APPROVED) {
            return latestValidation.getValidationLevel();
        }

        return null;
    }

    /**
     * Check if a result is fully validated.
     * A result is fully validated when it has been approved at the TECHNICIAN level
     * and any required higher-level validations.
     *
     * @param resultId Result ID
     * @return True if fully validated
     */
    @Transactional(readOnly = true)
    public boolean isFullyValidated(UUID resultId) {
        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        // Check if result requires pathologist review
        boolean requiresPathologist = result.getRequiresPathologistReview();

        // Get all validations
        List<ResultValidation> validations = getValidationHistory(resultId);

        // Check for technician approval
        boolean technicianApproved = validations.stream()
                .anyMatch(v -> v.getValidationLevel() == ValidationLevel.TECHNICIAN &&
                             v.getValidationStatus() == ValidationStatus.APPROVED);

        if (!technicianApproved) {
            return false;
        }

        // If pathologist review is required, check for pathologist approval
        if (requiresPathologist) {
            boolean pathologistApproved = validations.stream()
                    .anyMatch(v -> v.getValidationLevel() == ValidationLevel.PATHOLOGIST &&
                                 v.getValidationStatus() == ValidationStatus.APPROVED);

            if (!pathologistApproved) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get results awaiting validation at a specific level.
     *
     * @param level Validation level
     * @param pageable Pagination parameters
     * @return Page of results awaiting validation
     */
    @Transactional(readOnly = true)
    public Page<LabResult> getResultsAwaitingValidation(ValidationLevel level, Pageable pageable) {
        // This is a simplified implementation
        // In production, you would need a more complex query based on validation history
        return labResultRepository.findResultsAwaitingValidation(pageable);
    }

    /**
     * Validate result at technician level.
     *
     * @param resultId Result ID
     * @param validatorId Validator user ID
     * @param validatorName Validator name
     * @param comments Validation comments
     * @return Created validation record
     */
    public ResultValidation technicianValidation(UUID resultId, UUID validatorId, String validatorName, String comments) {
        return approveValidation(resultId, ValidationLevel.TECHNICIAN, validatorId, validatorName);
    }

    /**
     * Validate result at senior technician level.
     *
     * @param resultId Result ID
     * @param validatorId Validator user ID
     * @param validatorName Validator name
     * @param comments Validation comments
     * @return Created validation record
     */
    public ResultValidation seniorTechValidation(UUID resultId, UUID validatorId, String validatorName, String comments) {
        return approveValidation(resultId, ValidationLevel.SENIOR_TECH, validatorId, validatorName);
    }

    /**
     * Validate result at pathologist level.
     *
     * @param resultId Result ID
     * @param pathologistId Pathologist user ID
     * @param pathologistName Pathologist name
     * @param comments Pathologist comments
     * @return Created validation record
     */
    public ResultValidation pathologistValidation(UUID resultId, UUID pathologistId, String pathologistName, String comments) {
        log.info("Pathologist validation for result: {}", resultId);

        // Update result pathologist fields
        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        result.setReviewedByPathologist(true);
        result.setPathologistId(pathologistId);
        result.setPathologistReviewedAt(LocalDateTime.now());
        result.setPathologistComments(comments);
        labResultRepository.save(result);

        return approveValidation(resultId, ValidationLevel.PATHOLOGIST, pathologistId, pathologistName);
    }

    /**
     * Validate result at clinical reviewer level.
     *
     * @param resultId Result ID
     * @param reviewerId Reviewer user ID
     * @param reviewerName Reviewer name
     * @param comments Review comments
     * @return Created validation record
     */
    public ResultValidation clinicalReviewerValidation(UUID resultId, UUID reviewerId, String reviewerName, String comments) {
        return approveValidation(resultId, ValidationLevel.CLINICAL_REVIEWER, reviewerId, reviewerName);
    }

    /**
     * Get validation statistics for a date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Validation statistics
     */
    @Transactional(readOnly = true)
    public ValidationStatistics getValidationStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: Implement validation statistics query
        // This would include counts of approved, rejected, pending validations by level
        return new ValidationStatistics();
    }

    /**
     * Update result status based on validation.
     *
     * @param result Lab result
     * @param level Validation level
     * @param status Validation status
     * @param validatorId Validator ID
     * @param validatorName Validator name
     * @param comments Comments
     */
    private void updateResultStatus(LabResult result, ValidationLevel level, ValidationStatus status,
                                    UUID validatorId, String validatorName, String comments) {
        switch (status) {
            case APPROVED:
                // If technician approved and no pathologist review required, mark as final
                if (level == ValidationLevel.TECHNICIAN && !result.getRequiresPathologistReview()) {
                    result.setStatus(ResultStatus.FINAL);
                    result.setValidatedAt(LocalDateTime.now());
                    result.setValidatedBy(validatorId);
                    result.setValidationNotes(comments);
                }
                // If pathologist approved, mark as final
                else if (level == ValidationLevel.PATHOLOGIST) {
                    result.setStatus(ResultStatus.FINAL);
                    result.setValidatedAt(LocalDateTime.now());
                    result.setValidatedBy(validatorId);
                    result.setValidationNotes(comments);
                }
                // Otherwise keep as preliminary
                else {
                    if (result.getStatus() == ResultStatus.PENDING) {
                        result.setStatus(ResultStatus.PRELIMINARY);
                    }
                }
                break;

            case REJECTED:
                // Keep as preliminary but add notes
                result.setValidationNotes((result.getValidationNotes() != null ? result.getValidationNotes() + "\n\n" : "") +
                        String.format("REJECTED at %s level by %s: %s",
                                level.getDisplayName(), validatorName, comments));
                break;

            case NEEDS_REVIEW:
                // Flag for pathologist review if not already required
                if (!result.getRequiresPathologistReview()) {
                    result.setRequiresPathologistReview(true);
                }
                result.setValidationNotes((result.getValidationNotes() != null ? result.getValidationNotes() + "\n\n" : "") +
                        String.format("NEEDS REVIEW - %s level by %s: %s",
                                level.getDisplayName(), validatorName, comments));
                break;

            case NEEDS_REPEAT:
                // Keep as preliminary and flag for repeat
                result.setValidationNotes((result.getValidationNotes() != null ? result.getValidationNotes() + "\n\n" : "") +
                        String.format("REPEAT REQUIRED - %s level by %s: %s",
                                level.getDisplayName(), validatorName, comments));
                break;
        }

        labResultRepository.save(result);
    }

    /**
     * Handle repeat test required workflow.
     *
     * @param resultId Result ID
     * @param reason Reason for repeat
     */
    private void handleRepeatTestRequired(UUID resultId, String reason) {
        log.info("Handling repeat test required for result: {}", resultId);

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        // Mark result as cancelled
        result.setStatus(ResultStatus.CANCELLED);
        result.setNotes((result.getNotes() != null ? result.getNotes() + "\n\n" : "") +
                "REPEAT TEST REQUIRED: " + reason + " at " + LocalDateTime.now());

        labResultRepository.save(result);

        // TODO: Trigger new order/specimen collection workflow
        // This would typically involve:
        // 1. Creating a new order item
        // 2. Notifying specimen collection team
        // 3. Tracking the repeat test request
        log.info("Repeat test workflow triggered. Original result: {}", resultId);
    }

    /**
     * Validation statistics DTO.
     */
    public static class ValidationStatistics {
        public long totalValidations;
        public long approvedCount;
        public long rejectedCount;
        public long needsReviewCount;
        public long needsRepeatCount;
        public long technicianValidations;
        public long seniorTechValidations;
        public long pathologistValidations;
        public long clinicalReviewerValidations;
    }
}
