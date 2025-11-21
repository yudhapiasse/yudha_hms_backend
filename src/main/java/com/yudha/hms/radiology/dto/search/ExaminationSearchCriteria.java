package com.yudha.hms.radiology.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Examination Search Criteria DTO.
 *
 * Used for searching and filtering radiology examinations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationSearchCriteria {

    /**
     * Search term (searches in exam code, name, short name)
     */
    private String searchTerm;

    /**
     * Modality ID
     */
    private UUID modalityId;

    /**
     * Body part
     */
    private String bodyPart;

    /**
     * Requires contrast
     */
    private Boolean requiresContrast;

    /**
     * Requires fasting
     */
    private Boolean requiresFasting;

    /**
     * Requires approval
     */
    private Boolean requiresApproval;

    /**
     * Laterality applicable
     */
    private Boolean lateralityApplicable;

    /**
     * Active status
     */
    private Boolean isActive;

    /**
     * CPT code
     */
    private String cptCode;

    /**
     * ICD procedure code
     */
    private String icdProcedureCode;

    // ========== Pagination and Sorting ==========

    /**
     * Page number (0-indexed)
     */
    @Builder.Default
    private Integer page = 0;

    /**
     * Page size
     */
    @Builder.Default
    private Integer size = 20;

    /**
     * Sort by field
     */
    @Builder.Default
    private String sortBy = "examName";

    /**
     * Sort direction (ASC or DESC)
     */
    @Builder.Default
    private String sortDirection = "ASC";
}
