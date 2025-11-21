package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Verify Medication Hold Request DTO.
 *
 * Used for verifying medication hold status.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyMedicationHoldRequest {

    /**
     * Verified by (user ID)
     */
    @NotNull(message = "ID petugas verifikasi harus diisi")
    private UUID verifiedBy;

    /**
     * Is medication hold verified
     */
    @NotNull(message = "Status verifikasi obat harus diisi")
    @Builder.Default
    private Boolean verified = false;

    /**
     * Medication hold details
     */
    @Size(max = 1000, message = "Detail obat maksimal 1000 karakter")
    private String medicationDetails;
}
