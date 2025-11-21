package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.QualityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Specimen Quality Check Request DTO.
 *
 * Used for recording specimen quality check and validation.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecimenQualityCheckRequest {

    /**
     * Specimen ID
     */
    @NotNull(message = "ID spesimen harus diisi")
    private UUID specimenId;

    /**
     * Quality status
     */
    @NotNull(message = "Status kualitas harus dipilih")
    private QualityStatus qualityStatus;

    /**
     * Quality notes
     */
    private String qualityNotes;

    /**
     * Rejection reason (if rejected)
     */
    private String rejectionReason;

    // ========== Pre-analytical Validations ==========

    /**
     * Volume adequate
     */
    private Boolean volumeAdequate;

    /**
     * Container appropriate
     */
    private Boolean containerAppropriate;

    /**
     * Labeling correct
     */
    private Boolean labelingCorrect;

    /**
     * Temperature appropriate
     */
    private Boolean temperatureAppropriate;

    /**
     * Hemolysis detected (for blood samples)
     */
    private Boolean hemolysisDetected;

    /**
     * Lipemia detected (for blood samples)
     */
    private Boolean lipemiaDetected;

    /**
     * Icterus detected (for blood samples)
     */
    private Boolean icterusDetected;

    /**
     * Checked by user ID
     */
    @NotNull(message = "ID petugas pemeriksa harus diisi")
    private UUID checkedBy;
}
