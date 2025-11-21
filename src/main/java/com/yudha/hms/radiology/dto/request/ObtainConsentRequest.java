package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Obtain Consent Request DTO.
 *
 * Used for recording informed consent.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObtainConsentRequest {

    /**
     * Obtained by (user ID)
     */
    @NotNull(message = "ID petugas harus diisi")
    private UUID obtainedBy;

    /**
     * Consent form ID
     */
    @Size(max = 100, message = "ID formulir persetujuan maksimal 100 karakter")
    private String consentFormId;
}
