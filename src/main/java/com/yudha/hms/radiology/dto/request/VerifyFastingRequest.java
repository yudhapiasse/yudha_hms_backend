package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Verify Fasting Request DTO.
 *
 * Used for verifying patient fasting status.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyFastingRequest {

    /**
     * Verified by (user ID)
     */
    @NotNull(message = "ID petugas verifikasi harus diisi")
    private UUID verifiedBy;

    /**
     * Is fasting verified
     */
    @NotNull(message = "Status verifikasi puasa harus diisi")
    @Builder.Default
    private Boolean verified = false;
}
