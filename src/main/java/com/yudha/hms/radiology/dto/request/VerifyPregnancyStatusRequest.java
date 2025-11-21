package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Verify Pregnancy Status Request DTO.
 *
 * Used for verifying patient pregnancy status for radiology orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPregnancyStatusRequest {

    /**
     * Is patient pregnant
     */
    @NotNull(message = "Status kehamilan harus diisi")
    @Builder.Default
    private Boolean isPregnant = false;

    /**
     * Verified by (user ID)
     */
    @NotNull(message = "ID petugas verifikasi harus diisi")
    private UUID verifiedBy;
}
