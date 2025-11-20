package com.yudha.hms.integration.bpjs.dto.aplicares;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS Aplicares Bed Availability Response.
 *
 * Response for bed availability queries.
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
public class BedAvailabilityResponse {

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

        @JsonProperty("totalitems")
        private Integer totalitems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("list")
        private List<BedInfo> list;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BedInfo {
        @JsonProperty("kodekelas")
        private String kodekelas;

        @JsonProperty("namakelas")
        private String namakelas;

        @JsonProperty("koderuang")
        private String koderuang;

        @JsonProperty("namaruang")
        private String namaruang;

        @JsonProperty("kapasitas")
        private Integer kapasitas;

        @JsonProperty("tersedia")
        private Integer tersedia;

        @JsonProperty("tersediapria")
        private Integer tersediapria;

        @JsonProperty("tersediawanita")
        private Integer tersediawanita;

        @JsonProperty("tersediapriawanita")
        private Integer tersediapriawanita;

        @JsonProperty("lastupdate")
        private String lastupdate;
    }
}
