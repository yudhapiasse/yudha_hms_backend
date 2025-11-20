package com.yudha.hms.integration.bpjs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS SEP Creation Response.
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
public class BpjsSepResponse {

    /**
     * SEP data.
     */
    @JsonProperty("sep")
    private SepData sep;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SepData {

        /**
         * Generated SEP number.
         */
        @JsonProperty("noSep")
        private String noSep;

        /**
         * SEP creation date and time.
         */
        @JsonProperty("tglSep")
        private String tglSep;

        /**
         * Healthcare facility name.
         */
        @JsonProperty("ppkPelayanan")
        private String ppkPelayanan;

        /**
         * Participant information.
         */
        @JsonProperty("peserta")
        private Peserta peserta;

        /**
         * Diagnosis information.
         */
        @JsonProperty("diagnosa")
        private String diagnosa;

        /**
         * Polyclinic/service unit.
         */
        @JsonProperty("poli")
        private String poli;

        /**
         * Insurance class.
         */
        @JsonProperty("kelasRawat")
        private String kelasRawat;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Peserta {

        @JsonProperty("noKartu")
        private String noKartu;

        @JsonProperty("nama")
        private String nama;

        @JsonProperty("noMr")
        private String noMr;

        @JsonProperty("tglLahir")
        private String tglLahir;

        @JsonProperty("jnsPeserta")
        private String jnsPeserta;
    }
}
