package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Sign Referral Request DTO.
 *
 * Used when a doctor signs a referral letter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignReferralRequest {

    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    private String digitalSignature; // Optional: actual signature data
}
