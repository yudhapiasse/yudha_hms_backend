package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Base response DTO for all E-Klaim API responses.
 *
 * All E-Klaim responses follow a standard structure:
 * - metadata: Response metadata (status, message, etc.)
 * - response: Actual response data (varies by method)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
public class EklaimBaseResponse<T> {

    @JsonProperty("metadata")
    private ResponseMetadata metadata;

    @JsonProperty("response")
    private T response;

    @Data
    public static class ResponseMetadata {
        /**
         * Response code
         * "200" = Success
         * "201" = Created
         * "400" = Bad Request
         * "401" = Unauthorized
         * "404" = Not Found
         * "500" = Server Error
         */
        @JsonProperty("code")
        private String code;

        /**
         * Response message
         */
        @JsonProperty("message")
        private String message;

        /**
         * E-Klaim specific error code (if applicable)
         * E2001-E2020 or 36.xxxx for ungroupable errors
         */
        @JsonProperty("error_code")
        private String errorCode;

        /**
         * Request ID for tracing
         */
        @JsonProperty("request_id")
        private String requestId;
    }

    /**
     * Check if response is successful
     */
    public boolean isSuccess() {
        return metadata != null &&
               ("200".equals(metadata.code) || "201".equals(metadata.code));
    }

    /**
     * Check if response has error
     */
    public boolean hasError() {
        return metadata != null && metadata.errorCode != null;
    }
}
