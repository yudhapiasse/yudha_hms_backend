package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VClaim Submission Request DTO.
 *
 * Used for BPJS VClaim integration submission.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VClaimSubmissionRequest {

    private String referenceNumber;
    private String response;
    private Boolean forceResubmit; // Allow resubmission if already submitted
}
