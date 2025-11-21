package com.yudha.hms.laboratory.dto.search;

import com.yudha.hms.laboratory.constant.QualityStatus;
import com.yudha.hms.laboratory.constant.SampleType;
import com.yudha.hms.laboratory.constant.SpecimenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Specimen Search Criteria DTO.
 *
 * Used for searching and filtering specimens.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecimenSearchCriteria {

    /**
     * Search term (searches in specimen number, barcode, patient name)
     */
    private String searchTerm;

    /**
     * Specimen number
     */
    private String specimenNumber;

    /**
     * Barcode
     */
    private String barcode;

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
     * Specimen type
     */
    private SampleType specimenType;

    /**
     * Specimen status
     */
    private SpecimenStatus status;

    /**
     * Multiple statuses
     */
    private List<SpecimenStatus> statuses;

    /**
     * Quality status
     */
    private QualityStatus qualityStatus;

    /**
     * Has pre-analytical issues
     */
    private Boolean hasPreAnalyticalIssues;

    /**
     * Collected date from
     */
    private LocalDateTime collectedDateFrom;

    /**
     * Collected date to
     */
    private LocalDateTime collectedDateTo;

    /**
     * Received date from
     */
    private LocalDateTime receivedDateFrom;

    /**
     * Received date to
     */
    private LocalDateTime receivedDateTo;

    /**
     * Collected by user ID
     */
    private UUID collectedBy;

    /**
     * Received by user ID
     */
    private UUID receivedBy;

    /**
     * Storage location
     */
    private String storageLocation;

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
    private String sortBy = "collectedAt";

    /**
     * Sort direction (ASC or DESC)
     */
    @Builder.Default
    private String sortDirection = "DESC";
}
