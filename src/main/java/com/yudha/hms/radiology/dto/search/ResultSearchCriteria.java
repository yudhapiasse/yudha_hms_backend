package com.yudha.hms.radiology.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Result Search Criteria DTO.
 *
 * Used for searching and filtering radiology results.
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
     * Search term (searches in result number, patient name, MRN)
     */
    private String searchTerm;

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Examination ID
     */
    private UUID examinationId;

    /**
     * Modality ID
     */
    private UUID modalityId;

    /**
     * Technician ID
     */
    private UUID technicianId;

    /**
     * Radiologist ID
     */
    private UUID radiologistId;

    /**
     * Performed date from
     */
    private LocalDate performedDateFrom;

    /**
     * Performed date to
     */
    private LocalDate performedDateTo;

    /**
     * Is finalized
     */
    private Boolean isFinalized;

    /**
     * Is amended
     */
    private Boolean isAmended;

    /**
     * DICOM study ID
     */
    private String dicomStudyId;

    /**
     * Order number
     */
    private String orderNumber;

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
    private String sortBy = "performedDate";

    /**
     * Sort direction (ASC or DESC)
     */
    @Builder.Default
    private String sortDirection = "DESC";
}
