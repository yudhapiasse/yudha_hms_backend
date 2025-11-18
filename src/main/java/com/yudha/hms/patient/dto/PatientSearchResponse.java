package com.yudha.hms.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Patient Search Response DTO.
 *
 * Contains paginated search results with metadata.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSearchResponse<T> {

    /**
     * List of patient records (type depends on data depth)
     */
    private List<T> patients;

    /**
     * Current page number (0-indexed)
     */
    private int currentPage;

    /**
     * Number of records per page
     */
    private int pageSize;

    /**
     * Total number of records matching the search criteria
     */
    private long totalRecords;

    /**
     * Total number of pages
     */
    private int totalPages;

    /**
     * Whether this is the first page
     */
    private boolean isFirst;

    /**
     * Whether this is the last page
     */
    private boolean isLast;

    /**
     * Whether there is a next page
     */
    private boolean hasNext;

    /**
     * Whether there is a previous page
     */
    private boolean hasPrevious;

    /**
     * Number of records in current page
     */
    private int numberOfRecords;

    /**
     * Applied search criteria summary
     */
    private SearchMetadata metadata;

    /**
     * Search metadata
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchMetadata {
        /**
         * Search execution time in milliseconds
         */
        private Long executionTimeMs;

        /**
         * Data depth level used
         */
        private String dataDepth;

        /**
         * Sort field used
         */
        private String sortBy;

        /**
         * Sort direction used
         */
        private String sortDirection;

        /**
         * Whether quick search was used
         */
        private boolean quickSearchUsed;

        /**
         * Whether advanced filters were used
         */
        private boolean advancedFiltersUsed;
    }
}