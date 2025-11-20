package com.yudha.hms.integration.bpjs.dto.apotek;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS DPHO (Daftar Plafon Harga Obat) Response.
 *
 * Response for drug formulary with price ceiling information.
 * DPHO contains all BPJS-covered drugs with maximum reimbursement prices.
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
public class DphoResponse {

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
        @JsonProperty("list")
        private List<Drug> list;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Drug {
        /**
         * Drug code in BPJS system.
         */
        @JsonProperty("kodeobat")
        private String kodeobat;

        /**
         * Drug name (brand name).
         */
        @JsonProperty("namaobat")
        private String namaobat;

        /**
         * PRB (Program Rujuk Balik) flag.
         * True if drug is for chronic disease management.
         */
        @JsonProperty("prb")
        private String prb;

        /**
         * Chronic disease drug flag.
         */
        @JsonProperty("kronis")
        private String kronis;

        /**
         * Chemotherapy drug flag.
         */
        @JsonProperty("kemo")
        private String kemo;

        /**
         * Maximum reimbursement price (ceiling price).
         */
        @JsonProperty("harga")
        private String harga;

        /**
         * Restrictions/limitations (e.g., "Maks 90 tab/bln").
         */
        @JsonProperty("restriksi")
        private String restriksi;

        /**
         * Generic drug name.
         */
        @JsonProperty("generik")
        private String generik;

        /**
         * Active status.
         */
        @JsonProperty("aktif")
        private String aktif;
    }
}
