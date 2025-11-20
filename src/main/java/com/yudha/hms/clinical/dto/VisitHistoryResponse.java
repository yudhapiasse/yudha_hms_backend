package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Visit History Response DTO.
 *
 * Paginated response containing patient visit history.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitHistoryResponse {

    private List<VisitHistoryItemResponse> visits;

    // Summary Statistics
    private Long totalVisits;
    private Long outpatientVisits;
    private Long inpatientVisits;
    private Long emergencyVisits;
    private Long readmissions;
    private Long bpjsVisits;

    // Pagination
    private Integer currentPage;
    private Integer totalPages;
    private Integer pageSize;
    private Boolean hasNext;
    private Boolean hasPrevious;
}