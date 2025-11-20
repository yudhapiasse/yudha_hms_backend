package com.yudha.hms.integration.bpjs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Response Metadata.
 *
 * Standard metadata included in all BPJS API responses.
 * Contains status code and message.
 *
 * Common status codes:
 * - "200": Success
 * - "201": Created
 * - "400": Bad Request
 * - "401": Unauthorized
 * - "404": Not Found
 * - "500": Internal Server Error
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
public class BpjsMetaData {

    /**
     * Response status code.
     */
    @JsonProperty("code")
    private String code;

    /**
     * Response message.
     */
    @JsonProperty("message")
    private String message;

    /**
     * Check if response indicates success.
     *
     * @return true if code is "200" or "201"
     */
    public boolean isSuccess() {
        return "200".equals(code) || "201".equals(code);
    }
}
