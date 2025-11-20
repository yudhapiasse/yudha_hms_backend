package com.yudha.hms.integration.bpjs.dto.apotek;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Prescription Save Request.
 *
 * Request to save prescription (resep) in BPJS Apotek system.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {

    /**
     * SEP/visit number (Nomor SEP/Kunjungan).
     */
    @JsonProperty("REFASALSJP")
    private String refasalsjp;

    /**
     * Prescription date and time (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("TGLSJP")
    private String tglsjp;

    /**
     * Polyclinic code.
     */
    @JsonProperty("POLIRSP")
    private String polirsp;

    /**
     * Drug type code:
     * 1 = PRB (Program Rujuk Balik)
     * 2 = Chronic (not stable)
     * 3 = Chemotherapy
     */
    @JsonProperty("KDJNSOBAT")
    private String kdjnsobat;

    /**
     * Prescription number (hospital internal).
     */
    @JsonProperty("NORESEP")
    private String noresep;

    /**
     * User ID who created prescription.
     */
    @JsonProperty("IDUSERSJP")
    private String idusersjp;

    /**
     * Prescription date (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("TGLRSP")
    private String tglrsp;

    /**
     * Dispensing date (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("TGLPELRSP")
    private String tglpelrsp;

    /**
     * Doctor code.
     */
    @JsonProperty("KdDokter")
    private String kdDokter;

    /**
     * Iteration flag:
     * 0 = Non-iteration
     * 1 = Iteration (repeat prescription)
     */
    @JsonProperty("iterasi")
    private String iterasi;
}
