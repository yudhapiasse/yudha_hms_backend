package com.yudha.hms.integration.bpjs.dto.vclaim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS Monitoring Kunjungan Response DTO.
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
public class MonitoringKunjunganResponse {

    @JsonProperty("sep")
    private List<SepKunjungan> sep;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SepKunjungan {

        @JsonProperty("noSep")
        private String noSep;

        @JsonProperty("noKartu")
        private String noKartu;

        @JsonProperty("nama")
        private String nama;

        @JsonProperty("noRujukan")
        private String noRujukan;

        @JsonProperty("diagnosa")
        private String diagnosa;

        @JsonProperty("jnsPelayanan")
        private String jnsPelayanan;

        @JsonProperty("kelasRawat")
        private String kelasRawat;

        @JsonProperty("poli")
        private String poli;

        @JsonProperty("tglSep")
        private String tglSep;

        @JsonProperty("tglPlgSep")
        private String tglPlgSep;
    }
}
