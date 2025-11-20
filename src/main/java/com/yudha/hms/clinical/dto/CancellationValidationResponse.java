package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Cancellation Validation Response DTO.
 *
 * Response for validating whether an encounter can be cancelled.
 * Checks for linked data (medications dispensed, procedures done, results, etc).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationValidationResponse {

    private Boolean canCancel;
    private Boolean requiresSupervisorApproval;

    @Builder.Default
    private List<String> blockingReasons = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    // Linked data summary
    private Integer medicationsDispensed;
    private Integer proceduresDone;
    private Integer labResultsRecorded;
    private Integer radiologyResultsRecorded;
    private Boolean hasBillingTransactions;
    private Boolean hasProgressNotes;

    private String recommendation;
}
