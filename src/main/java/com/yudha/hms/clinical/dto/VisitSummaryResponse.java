package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Visit Summary Response DTO.
 *
 * Quick summary of patient's visit statistics.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitSummaryResponse {

    private UUID patientId;

    // Visit Counts
    private Long totalVisits;
    private Long outpatientVisits;
    private Long inpatientVisits;
    private Long emergencyVisits;
    private Long readmissions;
    private Long bpjsVisits;

    // Recent Activity
    private LocalDateTime lastVisitDate;
    private Boolean hasActiveEncounters;
}
