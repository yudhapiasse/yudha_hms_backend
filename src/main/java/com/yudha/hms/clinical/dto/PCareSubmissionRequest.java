package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PCare Submission Request DTO.
 *
 * Used for PCare integration submission.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PCareSubmissionRequest {

    private String referenceNumber;
    private Boolean forceResubmit; // Allow resubmission if already submitted
}
