package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for setting procedures (procedure_set method).
 *
 * Sets ICD-9-CM procedures for the claim.
 * Required for surgical and invasive procedures.
 *
 * Method: procedure_set
 * Endpoint: POST /ws/v1.0/claim/procedures
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcedureRequest extends EklaimBaseRequest {

    @JsonProperty("data")
    @Valid
    private ProcedureData data;

    @Data
    public static class ProcedureData {
        @NotBlank(message = "Claim number is required")
        @JsonProperty("claim_number")
        private String claimNumber;

        @NotEmpty(message = "At least one procedure is required")
        @JsonProperty("procedures")
        @Valid
        private List<Procedure> procedures;
    }

    @Data
    public static class Procedure {
        /**
         * ICD-9-CM procedure code
         */
        @NotBlank(message = "Procedure code is required")
        @Pattern(regexp = "^[0-9]{2}(\\.[0-9]{1,2})?$",
                message = "Invalid ICD-9-CM format")
        @JsonProperty("code")
        private String code;

        /**
         * Procedure date and time
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("procedure_date")
        private LocalDateTime procedureDate;

        /**
         * Procedure type:
         * 1 = Principal procedure
         * 2 = Secondary procedure
         */
        @Pattern(regexp = "^[12]$", message = "Type must be 1 (principal) or 2 (secondary)")
        @JsonProperty("type")
        private String type;
    }
}
