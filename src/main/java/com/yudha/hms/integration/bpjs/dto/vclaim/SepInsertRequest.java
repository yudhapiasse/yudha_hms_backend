package com.yudha.hms.integration.bpjs.dto.vclaim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS SEP Insert Request DTO (Version 2.0).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SepInsertRequest {

    @JsonProperty("request")
    private RequestWrapper request;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestWrapper {
        @JsonProperty("t_sep")
        private TSep tSep;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TSep {

        @JsonProperty("noKartu")
        private String noKartu;

        @JsonProperty("tglSep")
        private String tglSep;

        @JsonProperty("ppkPelayanan")
        private String ppkPelayanan;

        @JsonProperty("jnsPelayanan")
        private String jnsPelayanan;

        @JsonProperty("klsRawat")
        private KlsRawat klsRawat;

        @JsonProperty("noMR")
        private String noMR;

        @JsonProperty("rujukan")
        private Rujukan rujukan;

        @JsonProperty("catatan")
        private String catatan;

        @JsonProperty("diagAwal")
        private String diagAwal;

        @JsonProperty("poli")
        private Poli poli;

        @JsonProperty("cob")
        private Cob cob;

        @JsonProperty("katarak")
        private Katarak katarak;

        @JsonProperty("jaminan")
        private Jaminan jaminan;

        @JsonProperty("tujuanKunj")
        private String tujuanKunj;

        @JsonProperty("flagProcedure")
        private String flagProcedure;

        @JsonProperty("kdPenunjang")
        private String kdPenunjang;

        @JsonProperty("assesmentPel")
        private String assesmentPel;

        @JsonProperty("skdp")
        private Skdp skdp;

        @JsonProperty("dpjpLayan")
        private String dpjpLayan;

        @JsonProperty("noTelp")
        private String noTelp;

        @JsonProperty("user")
        private String user;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KlsRawat {
        @JsonProperty("klsRawatHak")
        private String klsRawatHak;

        @JsonProperty("klsRawatNaik")
        private String klsRawatNaik;

        @JsonProperty("pembiayaan")
        private String pembiayaan;

        @JsonProperty("penanggungJawab")
        private String penanggungJawab;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rujukan {
        @JsonProperty("asalRujukan")
        private String asalRujukan;

        @JsonProperty("tglRujukan")
        private String tglRujukan;

        @JsonProperty("noRujukan")
        private String noRujukan;

        @JsonProperty("ppkRujukan")
        private String ppkRujukan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Poli {
        @JsonProperty("tujuan")
        private String tujuan;

        @JsonProperty("eksekutif")
        private String eksekutif;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cob {
        @JsonProperty("cob")
        private String cob;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Katarak {
        @JsonProperty("katarak")
        private String katarak;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Jaminan {
        @JsonProperty("lakaLantas")
        private String lakaLantas;

        @JsonProperty("noLP")
        private String noLP;

        @JsonProperty("penjamin")
        private Penjamin penjamin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Penjamin {
        @JsonProperty("tglKejadian")
        private String tglKejadian;

        @JsonProperty("keterangan")
        private String keterangan;

        @JsonProperty("suplesi")
        private Suplesi suplesi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Suplesi {
        @JsonProperty("suplesi")
        private String suplesi;

        @JsonProperty("noSepSuplesi")
        private String noSepSuplesi;

        @JsonProperty("lokasiLaka")
        private LokasiLaka lokasiLaka;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LokasiLaka {
        @JsonProperty("kdPropinsi")
        private String kdPropinsi;

        @JsonProperty("kdKabupaten")
        private String kdKabupaten;

        @JsonProperty("kdKecamatan")
        private String kdKecamatan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skdp {
        @JsonProperty("noSurat")
        private String noSurat;

        @JsonProperty("kodeDPJP")
        private String kodeDPJP;
    }
}
