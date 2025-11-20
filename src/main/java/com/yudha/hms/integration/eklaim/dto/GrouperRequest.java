package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request DTO for executing grouper (grouper_1 and grouper_2 methods).
 *
 * E-Klaim uses two-stage grouping:
 * 1. grouper_1: iDRG (Indonesia Diagnosis Related Group)
 * 2. grouper_2: INACBG (Indonesian Case-Based Groups) - requires iDRG first
 *
 * Method: grouper_1 or grouper_2
 * Endpoint: POST /ws/v1.0/claim/grouper
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GrouperRequest extends EklaimBaseRequest {

    @JsonProperty("data")
    private GrouperData data;

    @Data
    public static class GrouperData {
        @NotBlank(message = "Claim number is required")
        @JsonProperty("claim_number")
        private String claimNumber;

        /**
         * Grouper type:
         * "1" = iDRG (Indonesia Diagnosis Related Group)
         * "2" = INACBG (Indonesian Case-Based Groups)
         */
        @NotBlank(message = "Grouper type is required")
        @Pattern(regexp = "^[12]$", message = "Grouper type must be 1 (iDRG) or 2 (INACBG)")
        @JsonProperty("grouper_type")
        private String grouperType;
    }
}
