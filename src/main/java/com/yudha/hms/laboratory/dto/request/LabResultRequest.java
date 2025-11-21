package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.EntryMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Lab Result Request DTO.
 *
 * Used for entering lab test results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultRequest {

    /**
     * Order item ID
     */
    @NotNull(message = "ID item order harus diisi")
    private UUID orderItemId;

    /**
     * Specimen ID
     */
    @NotNull(message = "ID spesimen harus diisi")
    private UUID specimenId;

    /**
     * Entry method
     */
    @NotNull(message = "Metode entry harus dipilih")
    private EntryMethod entryMethod;

    /**
     * LIS result ID (if from LIS interface)
     */
    private String lisResultId;

    /**
     * Entered by user ID
     */
    @NotNull(message = "ID petugas entry harus diisi")
    private UUID enteredBy;

    /**
     * Parameter results
     */
    @NotEmpty(message = "Minimal satu parameter hasil harus diisi")
    @Valid
    private List<ResultParameterEntryRequest> parameterResults;

    // ========== Interpretation ==========

    /**
     * Overall interpretation
     */
    private String overallInterpretation;

    /**
     * Clinical significance
     */
    private String clinicalSignificance;

    /**
     * Recommendations
     */
    private String recommendations;

    // ========== Pathologist Review ==========

    /**
     * Requires pathologist review
     */
    private Boolean requiresPathologistReview;

    /**
     * Pathologist comments
     */
    private String pathologistComments;

    /**
     * Notes
     */
    private String notes;
}
