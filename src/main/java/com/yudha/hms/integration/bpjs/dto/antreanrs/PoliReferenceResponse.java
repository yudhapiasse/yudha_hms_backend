package com.yudha.hms.integration.bpjs.dto.antreanrs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS Antrean RS Poli Reference Response.
 *
 * Response for polyclinic reference data.
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
public class PoliReferenceResponse {

    @JsonProperty("metadata")
    private Metadata metadata;

    @JsonProperty("response")
    private Response response;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("list")
        private List<Poli> list;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Poli {
        @JsonProperty("kdpoli")
        private String kdpoli;

        @JsonProperty("nmpoli")
        private String nmpoli;

        @JsonProperty("kdsubspesialis")
        private String kdsubspesialis;

        @JsonProperty("nmsubspesialis")
        private String nmsubspesialis;
    }
}
