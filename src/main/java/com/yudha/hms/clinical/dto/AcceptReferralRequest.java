package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Accept Referral Request DTO.
 *
 * Used when a receiving facility accepts a referral.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptReferralRequest {

    @NotBlank(message = "Accepted by name is required")
    private String acceptedBy;

    private LocalDateTime appointmentDate;
}
