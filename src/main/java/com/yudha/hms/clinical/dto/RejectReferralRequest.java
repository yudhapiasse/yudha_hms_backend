package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reject Referral Request DTO.
 *
 * Used when a receiving facility rejects a referral.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectReferralRequest {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;
}
