package com.yudha.hms.integration.bpjs.dto.antreanrs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Antrean RS Operation Response.
 *
 * Generic response for queue operations (add, update, cancel).
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
public class AntreanOperationResponse {

    @JsonProperty("metadata")
    private Metadata metadata;

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
     * @return true if code is 200
     */
    public boolean isSuccess() {
        return metadata != null && Integer.valueOf(200).equals(metadata.getCode());
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
