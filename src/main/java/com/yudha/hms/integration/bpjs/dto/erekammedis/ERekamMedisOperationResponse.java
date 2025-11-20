package com.yudha.hms.integration.bpjs.dto.erekammedis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS eRekam Medis Operation Response.
 *
 * Generic response for eRekam Medis submission operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ERekamMedisOperationResponse {

    @JsonProperty("metaData")
    private MetaData metaData;

    @JsonProperty("response")
    private Response response;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaData {
        /**
         * Response code (200 = Success).
         */
        @JsonProperty("code")
        private Integer code;

        /**
         * Response message.
         */
        @JsonProperty("message")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        /**
         * Submission ID from BPJS system.
         */
        @JsonProperty("submissionId")
        private String submissionId;

        /**
         * Encounter ID.
         */
        @JsonProperty("encounterId")
        private String encounterId;

        /**
         * Submission timestamp.
         */
        @JsonProperty("submittedAt")
        private String submittedAt;

        /**
         * Status: accepted, pending-review, rejected.
         */
        @JsonProperty("status")
        private String status;

        /**
         * Validation errors (if any).
         */
        @JsonProperty("validationErrors")
        private java.util.List<String> validationErrors;
    }

    /**
     * Check if operation was successful.
     *
     * @return true if code is 200
     */
    public boolean isSuccess() {
        return metaData != null && Integer.valueOf(200).equals(metaData.getCode());
    }

    /**
     * Get error message if operation failed.
     *
     * @return Error message or null if successful
     */
    public String getErrorMessage() {
        return metaData != null && !isSuccess() ? metaData.getMessage() : null;
    }

    /**
     * Get submission ID from response.
     *
     * @return Submission ID or null if not available
     */
    public String getSubmissionId() {
        return response != null ? response.getSubmissionId() : null;
    }

    /**
     * Check if submission has validation errors.
     *
     * @return true if validation errors exist
     */
    public boolean hasValidationErrors() {
        return response != null &&
               response.getValidationErrors() != null &&
               !response.getValidationErrors().isEmpty();
    }
}
