package com.yudha.hms.integration.bpjs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Participant Information Response.
 *
 * Contains participant eligibility and demographic data.
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
public class BpjsParticipantResponse {

    /**
     * Participant data.
     */
    @JsonProperty("peserta")
    private Peserta peserta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Peserta {

        /**
         * BPJS card number.
         */
        @JsonProperty("noKartu")
        private String noKartu;

        /**
         * Participant name.
         */
        @JsonProperty("nama")
        private String nama;

        /**
         * National ID (NIK).
         */
        @JsonProperty("nik")
        private String nik;

        /**
         * Medical record number.
         */
        @JsonProperty("noMR")
        private String noMR;

        /**
         * Gender (L/P).
         */
        @JsonProperty("sex")
        private String sex;

        /**
         * Date of birth (YYYY-MM-DD).
         */
        @JsonProperty("tglLahir")
        private String tglLahir;

        /**
         * Phone number.
         */
        @JsonProperty("noTelp")
        private String noTelp;

        /**
         * Participant type code.
         */
        @JsonProperty("jenisPeserta")
        private JenisPeserta jenisPeserta;

        /**
         * Participant status (AKTIF/TIDAK AKTIF).
         */
        @JsonProperty("statusPeserta")
        private StatusPeserta statusPeserta;

        /**
         * Healthcare facility code (PPK).
         */
        @JsonProperty("provUmum")
        private ProvUmum provUmum;

        /**
         * Insurance class (1/2/3).
         */
        @JsonProperty("hakKelas")
        private HakKelas hakKelas;

        /**
         * Guarantor information.
         */
        @JsonProperty("informasi")
        private Informasi informasi;

        /**
         * Coverage (BPJS Kesehatan/Pribadi).
         */
        @JsonProperty("cob")
        private Cob cob;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JenisPeserta {
        @JsonProperty("kode")
        private String kode;

        @JsonProperty("keterangan")
        private String keterangan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusPeserta {
        @JsonProperty("kode")
        private String kode;

        @JsonProperty("keterangan")
        private String keterangan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProvUmum {
        @JsonProperty("kdProvider")
        private String kdProvider;

        @JsonProperty("nmProvider")
        private String nmProvider;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HakKelas {
        @JsonProperty("kode")
        private String kode;

        @JsonProperty("keterangan")
        private String keterangan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Informasi {
        @JsonProperty("dinsos")
        private String dinsos;

        @JsonProperty("noSKTM")
        private String noSKTM;

        @JsonProperty("prolanisPRB")
        private String prolanisPRB;

        @JsonProperty("nama")
        private String nama;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cob {
        @JsonProperty("noAsuransi")
        private String noAsuransi;

        @JsonProperty("nmAsuransi")
        private String nmAsuransi;

        @JsonProperty("tglTAT")
        private String tglTAT;

        @JsonProperty("tglTMT")
        private String tglTMT;
    }
}
