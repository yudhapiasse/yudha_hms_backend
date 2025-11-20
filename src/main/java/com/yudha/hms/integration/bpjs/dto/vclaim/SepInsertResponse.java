package com.yudha.hms.integration.bpjs.dto.vclaim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS SEP Insert Response DTO.
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
public class SepInsertResponse {

    @JsonProperty("sep")
    private Sep sep;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sep {

        @JsonProperty("noSep")
        private String noSep;

        @JsonProperty("tglSep")
        private String tglSep;

        @JsonProperty("ppkPelayanan")
        private String ppkPelayanan;

        @JsonProperty("jnsPelayanan")
        private String jnsPelayanan;

        @JsonProperty("kelasRawat")
        private String kelasRawat;

        @JsonProperty("noRujukan")
        private String noRujukan;

        @JsonProperty("diagnosa")
        private String diagnosa;

        @JsonProperty("poli")
        private String poli;

        @JsonProperty("poliEksekutif")
        private String poliEksekutif;

        @JsonProperty("catatan")
        private String catatan;

        @JsonProperty("penjamin")
        private String penjamin;

        @JsonProperty("tujuanKunj")
        private String tujuanKunj;

        @JsonProperty("flagProcedure")
        private String flagProcedure;

        @JsonProperty("kdPenunjang")
        private String kdPenunjang;

        @JsonProperty("kdPoli")
        private String kdPoli;

        @JsonProperty("assestmenPel")
        private String assestmenPel;

        @JsonProperty("peserta")
        private Peserta peserta;

        @JsonProperty("informasi")
        private Informasi informasi;
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

        @JsonProperty("hakKelas")
        private String hakKelas;

        @JsonProperty("kelamin")
        private String kelamin;

        @JsonProperty("asuransi")
        private String asuransi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Informasi {
        @JsonProperty("dinsos")
        private String dinsos;

        @JsonProperty("eSEP")
        private String eSEP;

        @JsonProperty("noSKTM")
        private String noSKTM;

        @JsonProperty("prolanisPRB")
        private String prolanisPRB;
    }
}
