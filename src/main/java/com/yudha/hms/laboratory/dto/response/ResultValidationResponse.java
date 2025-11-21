package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.ValidationLevel;
import com.yudha.hms.laboratory.constant.ValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Result Validation Response DTO.
 *
 * Response for result validation history.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultValidationResponse {

    /**
     * Validation ID
     */
    private UUID id;

    /**
     * Result ID
     */
    private UUID resultId;

    /**
     * Result number
     */
    private String resultNumber;

    // ========== Validation Step ==========

    /**
     * Validation level
     */
    private ValidationLevel validationLevel;

    /**
     * Validation step
     */
    private Integer validationStep;

    // ========== Validator Information ==========

    /**
     * Validated by user ID
     */
    private UUID validatedBy;

    /**
     * Validator name
     */
    private String validatorName;

    /**
     * Validated at
     */
    private LocalDateTime validatedAt;

    // ========== Validation Decision ==========

    /**
     * Validation status
     */
    private ValidationStatus validationStatus;

    /**
     * Validation notes
     */
    private String validationNotes;

    // ========== Issues Identified ==========

    /**
     * Issues identified
     */
    private List<String> issuesIdentified;

    /**
     * Corrective action
     */
    private String correctiveAction;

    // ========== Digital Signature ==========

    /**
     * Has digital signature
     */
    private Boolean hasDigitalSignature;

    /**
     * Created at
     */
    private LocalDateTime createdAt;
}
