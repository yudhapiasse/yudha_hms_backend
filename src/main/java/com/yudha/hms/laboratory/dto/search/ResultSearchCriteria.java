package com.yudha.hms.laboratory.dto.search;

import com.yudha.hms.laboratory.constant.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Result Search Criteria DTO.
 *
 * Used for searching and filtering lab results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultSearchCriteria {

    /**
     * Search term (searches in result number, patient name, test name)
     */
    private String searchTerm;

    /**
     * Result number
     */
    private String resultNumber;

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Patient MRN
     */
    private String patientMrn;

    /**
     * Order ID
     */
    private UUID orderId;

    /**
     * Test ID
     */
    private UUID testId;

    /**
     * Test code
     */
    private String testCode;

    /**
     * Category ID
     */
    private UUID categoryId;

    /**
     * Result status
     */
    private ResultStatus status;

    /**
     * Multiple statuses
     */
    private List<ResultStatus> statuses;

    /**
     * Has panic values
     */
    private Boolean hasPanicValues;

    /**
     * Has abnormal values
     */
    private Boolean hasAbnormalValues;

    /**
     * Delta check flagged
     */
    private Boolean deltaCheckFlagged;

    /**
     * Requires pathologist review
     */
    private Boolean requiresPathologistReview;

    /**
     * Reviewed by pathologist
     */
    private Boolean reviewedByPathologist;

    /**
     * Entered date from
     */
    private LocalDateTime enteredDateFrom;

    /**
     * Entered date to
     */
    private LocalDateTime enteredDateTo;

    /**
     * Validated date from
     */
    private LocalDateTime validatedDateFrom;

    /**
     * Validated date to
     */
    private LocalDateTime validatedDateTo;

    /**
     * Entered by user ID
     */
    private UUID enteredBy;

    /**
     * Validated by user ID
     */
    private UUID validatedBy;

    /**
     * Is amended
     */
    private Boolean isAmended;

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
    private String sortBy = "enteredAt";

    /**
     * Sort direction (ASC or DESC)
     */
    @Builder.Default
    private String sortDirection = "DESC";
}
