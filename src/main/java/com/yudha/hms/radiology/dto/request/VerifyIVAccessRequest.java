package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Verify IV Access Request DTO.
 *
 * Used for verifying IV access for contrast administration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyIVAccessRequest {

    /**
     * Verified by (user ID)
     */
    @NotNull(message = "ID petugas verifikasi harus diisi")
    private UUID verifiedBy;

    /**
     * Is IV access verified
     */
    @NotNull(message = "Status verifikasi IV harus diisi")
    @Builder.Default
    private Boolean verified = false;

    /**
     * IV gauge (e.g., 18G, 20G, 22G)
     */
    @Size(max = 20, message = "Ukuran IV maksimal 20 karakter")
    private String ivGauge;
}
