package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.ValidationLevel;
import com.yudha.hms.laboratory.constant.ValidationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Result Validation Request DTO.
 *
 * Used for validating lab results at different levels.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultValidationRequest {

    /**
     * Result ID
     */
    @NotNull(message = "ID hasil harus diisi")
    private UUID resultId;

    /**
     * Validation level
     */
    @NotNull(message = "Level validasi harus dipilih")
    private ValidationLevel validationLevel;

    /**
     * Validation step number
     */
    @NotNull(message = "Step validasi harus diisi")
    @Min(value = 1, message = "Step validasi minimal 1")
    private Integer validationStep;

    /**
     * Validated by user ID
     */
    @NotNull(message = "ID validator harus diisi")
    private UUID validatedBy;

    /**
     * Validator name
     */
    @NotNull(message = "Nama validator harus diisi")
    private String validatorName;

    /**
     * Validation status
     */
    @NotNull(message = "Status validasi harus dipilih")
    private ValidationStatus validationStatus;

    /**
     * Validation notes
     */
    private String validationNotes;

    /**
     * Issues identified
     */
    private List<String> issuesIdentified;

    /**
     * Corrective action
     */
    private String correctiveAction;

    /**
     * Requires repeat test
     */
    private Boolean requiresRepeatTest;

    /**
     * Rejection reason (if rejected)
     */
    private String rejectionReason;

    /**
     * Digital signature data
     */
    private String signatureData;
}
