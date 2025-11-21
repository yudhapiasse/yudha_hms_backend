package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Verify Contrast Allergy Request DTO.
 *
 * Used for verifying patient contrast allergy status.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyContrastAllergyRequest {

    /**
     * Has contrast allergy
     */
    @NotNull(message = "Status alergi kontras harus diisi")
    @Builder.Default
    private Boolean hasAllergy = false;

    /**
     * Allergy details
     */
    @Size(max = 1000, message = "Detail alergi maksimal 1000 karakter")
    private String allergyDetails;

    /**
     * Verified by (user ID)
     */
    @NotNull(message = "ID petugas verifikasi harus diisi")
    private UUID verifiedBy;
}
