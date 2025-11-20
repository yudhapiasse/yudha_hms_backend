package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Request DTO for setting diagnoses (diagnosa_set method).
 *
 * Sets ICD-10 diagnoses for the claim:
 * - Principal diagnosis (required)
 * - Secondary diagnoses (optional, up to 10)
 *
 * Method: diagnosa_set
 * Endpoint: POST /ws/v1.0/claim/diagnoses
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiagnosisRequest extends EklaimBaseRequest {

    @JsonProperty("data")
    @Valid
    private DiagnosisData data;

    @Data
    public static class DiagnosisData {
        @NotBlank(message = "Claim number is required")
        @JsonProperty("claim_number")
        private String claimNumber;

        @NotEmpty(message = "At least one diagnosis is required")
        @JsonProperty("diagnoses")
        @Valid
        private List<Diagnosis> diagnoses;
    }

    @Data
    public static class Diagnosis {
        /**
         * ICD-10 code
         */
        @NotBlank(message = "Diagnosis code is required")
        @Pattern(regexp = "^[A-Z][0-9]{2}(\\.[0-9]{1,2})?$",
                message = "Invalid ICD-10 format")
        @JsonProperty("code")
        private String code;

        /**
         * Diagnosis type:
         * 1 = Principal diagnosis
         * 2 = Secondary diagnosis
         */
        @NotBlank(message = "Diagnosis type is required")
        @Pattern(regexp = "^[12]$", message = "Type must be 1 (principal) or 2 (secondary)")
        @JsonProperty("type")
        private String type;

        /**
         * Diagnosis level:
         * 1 = Admission diagnosis
         * 2 = Discharge diagnosis
         */
        @JsonProperty("level")
        private String level;
    }
}
