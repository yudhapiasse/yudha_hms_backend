package com.yudha.hms.integration.bpjs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS SEP (Surat Eligibilitas Peserta) Creation Request.
 *
 * SEP is required for all BPJS covered services.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpjsSepRequest {

    /**
     * Request data wrapper.
     */
    @JsonProperty("request")
    private SepData request;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SepData {

        /**
         * SEP data.
         */
        @JsonProperty("t_sep")
        private Sep tSep;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sep {

        /**
         * SEP number (for updates/cancellations).
         */
        @JsonProperty("noSep")
        private String noSep;

        /**
         * BPJS card number.
         */
        @JsonProperty("noKartu")
        private String noKartu;

        /**
         * SEP date (YYYY-MM-DD).
         */
        @JsonProperty("tglSep")
        private String tglSep;

        /**
         * Healthcare facility code.
         */
        @JsonProperty("ppkPelayanan")
        private String ppkPelayanan;

        /**
         * Service type code (1=Rawat Inap, 2=Rawat Jalan).
         */
        @JsonProperty("jnsPelayanan")
        private String jnsPelayanan;

        /**
         * Insurance class requested (1/2/3).
         */
        @JsonProperty("klsRawat")
        private String klsRawat;

        /**
         * Primary diagnosis (ICD-10 code).
         */
        @JsonProperty("diagAwal")
        private String diagAwal;

        /**
         * Polyclinic/department code.
         */
        @JsonProperty("poliKontrol")
        private String poliKontrol;

        /**
         * Referring facility code (for referrals).
         */
        @JsonProperty("asalRujukan")
        private String asalRujukan;

        /**
         * Referral number.
         */
        @JsonProperty("noRujukan")
        private String noRujukan;

        /**
         * Referral date (YYYY-MM-DD).
         */
        @JsonProperty("tglRujukan")
        private String tglRujukan;

        /**
         * Referring polyclinic code.
         */
        @JsonProperty("poliRujukan")
        private String poliRujukan;

        /**
         * Guarantor notes.
         */
        @JsonProperty("catatan")
        private String catatan;

        /**
         * Accident flag (0/1).
         */
        @JsonProperty("lakaLantas")
        private String lakaLantas;

        /**
         * Accident location.
         */
        @JsonProperty("lokasiLaka")
        private String lokasiLaka;

        /**
         * User who created the SEP.
         */
        @JsonProperty("user")
        private String user;

        /**
         * COB (Coordination of Benefits) flag (0/1).
         */
        @JsonProperty("noTelp")
        private String noTelp;
    }
}
