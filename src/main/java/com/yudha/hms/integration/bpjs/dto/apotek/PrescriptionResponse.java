package com.yudha.hms.integration.bpjs.dto.apotek;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Prescription Save Response.
 *
 * Response after saving prescription.
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
public class PrescriptionResponse {

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
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("noSep_Kunjungan")
        private String noSepKunjungan;

        @JsonProperty("noKartu")
        private String noKartu;

        @JsonProperty("nama")
        private String nama;

        @JsonProperty("faskesAsal")
        private String faskesAsal;

        @JsonProperty("noApotik")
        private String noApotik;

        @JsonProperty("noResep")
        private String noResep;

        @JsonProperty("tglResep")
        private String tglResep;

        @JsonProperty("kdJnsObat")
        private String kdJnsObat;

        @JsonProperty("byTagRsp")
        private String byTagRsp;

        @JsonProperty("byVerRsp")
        private String byVerRsp;

        @JsonProperty("tglEntry")
        private String tglEntry;
    }
}
