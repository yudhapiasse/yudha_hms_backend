package com.yudha.hms.integration.bpjs.dto.antreanrs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS Antrean RS Queue List Response.
 *
 * Response for getting queue list by date or booking code.
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
public class QueueListResponse {

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
        private List<QueueInfo> list;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueueInfo {
        @JsonProperty("nomorkartu")
        private String nomorkartu;

        @JsonProperty("nik")
        private String nik;

        @JsonProperty("kodebooking")
        private String kodebooking;

        @JsonProperty("norm")
        private String norm;

        @JsonProperty("namapoli")
        private String namapoli;

        @JsonProperty("namadokter")
        private String namadokter;

        @JsonProperty("jampraktek")
        private String jampraktek;

        @JsonProperty("jenispasien")
        private String jenispasien;

        @JsonProperty("nomorantrean")
        private String nomorantrean;

        @JsonProperty("angkaantrean")
        private Integer angkaantrean;

        @JsonProperty("status")
        private String status;

        @JsonProperty("statusdesc")
        private String statusdesc;

        @JsonProperty("namapeserta")
        private String namapeserta;

        @JsonProperty("tanggalperiksa")
        private String tanggalperiksa;
    }
}
