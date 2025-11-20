package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Base request DTO for all E-Klaim API calls.
 *
 * All E-Klaim requests include metadata fields for
 * authentication and request identification.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
public abstract class EklaimBaseRequest {

    /**
     * Request metadata
     */
    @JsonProperty("metadata")
    private RequestMetadata metadata;

    @Data
    public static class RequestMetadata {
        /**
         * Method name (e.g., "new_claim", "set_claim_data")
         */
        @NotBlank(message = "Method is required")
        @JsonProperty("method")
        private String method;

        /**
         * Unique request identifier (UUID recommended)
         */
        @JsonProperty("request_id")
        private String requestId;
    }
}
