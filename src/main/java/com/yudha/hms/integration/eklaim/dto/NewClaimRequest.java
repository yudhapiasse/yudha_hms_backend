package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request DTO for creating new claim (new_claim method).
 *
 * This is the first step in E-Klaim workflow.
 * Creates a new draft claim from SEP (Surat Elegibilitas Peserta).
 *
 * Method: new_claim
 * Endpoint: POST /ws/v1.0/claim/new
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NewClaimRequest extends EklaimBaseRequest {

    @JsonProperty("data")
    private ClaimData data;

    @Data
    public static class ClaimData {
        /**
         * SEP number from VClaim
         * Format: 4-digit hospital code + 10-digit sequential number
         */
        @NotBlank(message = "SEP number is required")
        @Pattern(regexp = "^\\d{14}$", message = "SEP number must be 14 digits")
        @JsonProperty("nomor_sep")
        private String nomorSep;

        /**
         * Hospital code (must match configuration)
         */
        @NotBlank(message = "Hospital code is required")
        @JsonProperty("hospital_code")
        private String hospitalCode;
    }
}
