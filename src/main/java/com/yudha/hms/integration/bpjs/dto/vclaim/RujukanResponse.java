package com.yudha.hms.integration.bpjs.dto.vclaim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yudha.hms.integration.bpjs.dto.BpjsParticipantResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Rujukan (Referral) Response DTO.
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
public class RujukanResponse {

    @JsonProperty("rujukan")
    private Rujukan rujukan;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rujukan {

        @JsonProperty("diagnosa")
        private Diagnosa diagnosa;

        @JsonProperty("keluhan")
        private String keluhan;

        @JsonProperty("noKunjungan")
        private String noKunjungan;

        @JsonProperty("pelayanan")
        private Pelayanan pelayanan;

        @JsonProperty("peserta")
        private BpjsParticipantResponse.Peserta peserta;

        @JsonProperty("poliRujukan")
        private PoliRujukan poliRujukan;

        @JsonProperty("provPerujuk")
        private ProvPerujuk provPerujuk;

        @JsonProperty("tglKunjungan")
        private String tglKunjungan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Diagnosa {
        @JsonProperty("kode")
        private String kode;

        @JsonProperty("nama")
        private String nama;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pelayanan {
        @JsonProperty("kode")
        private String kode;

        @JsonProperty("nama")
        private String nama;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PoliRujukan {
        @JsonProperty("kode")
        private String kode;

        @JsonProperty("nama")
        private String nama;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProvPerujuk {
        @JsonProperty("kode")
        private String kode;

        @JsonProperty("nama")
        private String nama;
    }
}
