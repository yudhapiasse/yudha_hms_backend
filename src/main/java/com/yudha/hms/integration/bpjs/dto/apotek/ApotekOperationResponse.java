package com.yudha.hms.integration.bpjs.dto.apotek;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Apotek Operation Response.
 *
 * Generic response for pharmacy operations.
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
public class ApotekOperationResponse {

    @JsonProperty("metaData")
    private MetaData metaData;

    @JsonProperty("response")
    private Object response;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaData {
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
}
