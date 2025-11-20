package com.yudha.hms.integration.bpjs.dto.aplicares;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Aplicares Operation Response.
 *
 * Generic response for create/update/delete operations.
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
public class AplicaresOperationResponse {

    @JsonProperty("metadata")
    private Metadata metadata;

    @JsonProperty("response")
    private Object response;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("code")
        private Integer code;

        @JsonProperty("message")
        private String message;
    }

    /**
     * Check if operation was successful.
     *
     * @return true if code is 1 (success)
     */
    public boolean isSuccess() {
        return metadata != null && metadata.getCode() != null && metadata.getCode() == 1;
    }

    /**
     * Get error message if operation failed.
     *
     * @return Error message or null if successful
     */
    public String getErrorMessage() {
        return metadata != null && !isSuccess() ? metadata.getMessage() : null;
    }
}
