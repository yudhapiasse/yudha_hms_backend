package com.yudha.hms.laboratory.dto.search;

import com.yudha.hms.laboratory.constant.SampleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Test Search Criteria DTO.
 *
 * Used for searching and filtering lab tests.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSearchCriteria {

    /**
     * Search term (searches in test code, name, short name)
     */
    private String searchTerm;

    /**
     * Category ID
     */
    private UUID categoryId;

    /**
     * Sample type
     */
    private SampleType sampleType;

    /**
     * Active status
     */
    private Boolean active;

    /**
     * Has critical values
     */
    private Boolean hasCriticalValues;

    /**
     * Requires pathologist review
     */
    private Boolean requiresPathologistReview;

    /**
     * Fasting required
     */
    private Boolean fastingRequired;

    /**
     * LOINC code
     */
    private String loincCode;

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
    private String sortBy = "name";

    /**
     * Sort direction (ASC or DESC)
     */
    @Builder.Default
    private String sortDirection = "ASC";
}
