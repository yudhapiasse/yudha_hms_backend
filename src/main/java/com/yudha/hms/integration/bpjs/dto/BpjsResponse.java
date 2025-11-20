package com.yudha.hms.integration.bpjs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard BPJS API Response Wrapper.
 *
 * All BPJS web services return responses in this format:
 * {
 *   "metaData": {
 *     "code": "200",
 *     "message": "OK"
 *   },
 *   "response": { ... }
 * }
 *
 * @param <T> Type of the response data
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BpjsResponse<T> {

    /**
     * Response metadata (status code and message).
     */
    @JsonProperty("metaData")
    private BpjsMetaData metaData;

    /**
     * Response data (can be encrypted).
     */
    @JsonProperty("response")
    private T response;

    /**
     * Check if the response is successful.
     *
     * @return true if status code is "200"
     */
    public boolean isSuccess() {
        return metaData != null && "200".equals(metaData.getCode());
    }

    /**
     * Get error message if request failed.
     *
     * @return Error message or null if successful
     */
    public String getErrorMessage() {
        return metaData != null && !isSuccess() ? metaData.getMessage() : null;
    }

    /**
     * Get error code if request failed.
     *
     * @return Error code or null if successful
     */
    public String getErrorCode() {
        return metaData != null && !isSuccess() ? metaData.getCode() : null;
    }
}
