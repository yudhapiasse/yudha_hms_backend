package com.yudha.hms.integration.satusehat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SATUSEHAT OAuth2 Token Response.
 *
 * Response format from SATUSEHAT follows FHIR Parameters structure:
 * {
 *   "resourceType": "Parameters",
 *   "parameter": [
 *     {"name": "access_token", "valueString": "eyJ..."},
 *     {"name": "token_type", "valueString": "Bearer"},
 *     {"name": "expires_in", "valueInteger": 3599},
 *     {"name": "issued_at", "valueString": "1234567890000"}
 *   ]
 * }
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2TokenResponse {

    @JsonProperty("resourceType")
    private String resourceType;

    @JsonProperty("parameter")
    private List<Parameter> parameters;

    /**
     * FHIR Parameter structure
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parameter {
        @JsonProperty("name")
        private String name;

        @JsonProperty("valueString")
        private String valueString;

        @JsonProperty("valueInteger")
        private Integer valueInteger;

        @JsonProperty("valueBoolean")
        private Boolean valueBoolean;
    }

    /**
     * Extract access token from parameters
     */
    public String getAccessToken() {
        return getParameterValue("access_token");
    }

    /**
     * Extract token type from parameters (usually "Bearer")
     */
    public String getTokenType() {
        return getParameterValue("token_type");
    }

    /**
     * Extract expires_in (seconds) from parameters
     */
    public Integer getExpiresIn() {
        if (parameters == null) return null;
        return parameters.stream()
            .filter(p -> "expires_in".equals(p.getName()))
            .map(Parameter::getValueInteger)
            .findFirst()
            .orElse(null);
    }

    /**
     * Extract issued_at timestamp from parameters
     */
    public String getIssuedAt() {
        return getParameterValue("issued_at");
    }

    /**
     * Helper method to extract string parameter value
     */
    private String getParameterValue(String paramName) {
        if (parameters == null) return null;
        return parameters.stream()
            .filter(p -> paramName.equals(p.getName()))
            .map(Parameter::getValueString)
            .findFirst()
            .orElse(null);
    }

    /**
     * Check if response is valid
     */
    public boolean isValid() {
        return "Parameters".equals(resourceType)
            && parameters != null
            && getAccessToken() != null
            && getExpiresIn() != null;
    }
}
