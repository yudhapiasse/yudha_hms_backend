package com.yudha.hms.integration.bpjs.dto.apotek;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Drug Dispensing Request (Non-Racikan).
 *
 * Request to save non-compounded drug dispensing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugDispensingRequest {

    /**
     * Pharmacy number (No Apotik from prescription response).
     */
    @JsonProperty("NOSJP")
    private String nosjp;

    /**
     * Prescription number.
     */
    @JsonProperty("NORESEP")
    private String noresep;

    /**
     * Drug code.
     */
    @JsonProperty("KDOBT")
    private String kdobt;

    /**
     * Drug name.
     */
    @JsonProperty("NMOBAT")
    private String nmobat;

    /**
     * Signa 1 (frequency per day).
     */
    @JsonProperty("SIGNA1OBT")
    private Integer signa1obt;

    /**
     * Signa 2 (quantity per dose).
     */
    @JsonProperty("SIGNA2OBT")
    private Integer signa2obt;

    /**
     * Drug quantity.
     */
    @JsonProperty("JMLOBT")
    private Integer jmlobt;

    /**
     * Days of treatment (Jumlah Hari Obat).
     */
    @JsonProperty("JHO")
    private Integer jho;

    /**
     * Special notes.
     */
    @JsonProperty("CatKhsObt")
    private String catKhsObt;
}
