package com.yudha.hms.integration.bpjs.dto.icare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS iCare JKN History Validation Response.
 *
 * Response containing secure URL with token for accessing patient
 * treatment history. The URL contains a temporary token that expires
 * after a certain period.
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
public class HistoryValidationResponse {

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
         * Secure URL with token for accessing patient history.
         * Example: "https://dvlp.bpjs-kesehatan.go.id/ihs/history?token=e6b610b4-2960-46a3-8420-de879756dce3"
         *
         * This URL should be displayed in an iframe or opened in a new window.
         * The token expires after a certain period and must be regenerated for subsequent access.
         */
        @JsonProperty("url")
        private String url;
    }

    /**
     * Check if validation was successful.
     *
     * @return true if code is 200
     */
    public boolean isSuccess() {
        return metaData != null && Integer.valueOf(200).equals(metaData.getCode());
    }

    /**
     * Get error message if validation failed.
     *
     * @return Error message or null if successful
     */
    public String getErrorMessage() {
        return metaData != null && !isSuccess() ? metaData.getMessage() : null;
    }

    /**
     * Get history URL.
     *
     * @return Secure URL with token or null if not available
     */
    public String getHistoryUrl() {
        return response != null ? response.getUrl() : null;
    }

    /**
     * Extract token from URL.
     * Example URL: "https://dvlp.bpjs-kesehatan.go.id/ihs/history?token=e6b610b4-2960-46a3-8420-de879756dce3"
     * Returns: "e6b610b4-2960-46a3-8420-de879756dce3"
     *
     * @return Token string or null if not found
     */
    public String extractToken() {
        if (response == null || response.getUrl() == null) {
            return null;
        }

        String url = response.getUrl();
        int tokenIndex = url.indexOf("token=");
        if (tokenIndex == -1) {
            return null;
        }

        String tokenPart = url.substring(tokenIndex + 6); // Skip "token="
        int ampersandIndex = tokenPart.indexOf("&");

        return ampersandIndex == -1 ? tokenPart : tokenPart.substring(0, ampersandIndex);
    }
}
