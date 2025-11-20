package com.yudha.hms.integration.bpjs.dto.icare;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS iCare JKN History Validation Request.
 *
 * Request to validate patient and retrieve secure URL for accessing
 * complete treatment history across all BPJS facilities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryValidationRequest {

    /**
     * BPJS card number (No Kartu BPJS).
     * Format: 13 digits (e.g., "0001234567890")
     */
    @JsonProperty("param")
    private String param;

    /**
     * Doctor code registered with BPJS.
     * The doctor requesting access to patient history.
     */
    @JsonProperty("kodedokter")
    private Integer kodedokter;
}
