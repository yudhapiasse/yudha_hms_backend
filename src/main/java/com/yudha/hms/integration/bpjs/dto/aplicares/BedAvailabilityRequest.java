package com.yudha.hms.integration.bpjs.dto.aplicares;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Aplicares Bed Availability Request.
 *
 * Used for creating/updating bed availability information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BedAvailabilityRequest {

    /**
     * Room class code according to BPJS mapping.
     * Examples: VIP, VVIP, KL1 (Kelas 1), KL2, KL3, ICU, ICCU, ISO, PICU, NICU, HCU
     */
    @JsonProperty("kodekelas")
    private String kodekelas;

    /**
     * Hospital room code (internal).
     */
    @JsonProperty("koderuang")
    private String koderuang;

    /**
     * Room name.
     */
    @JsonProperty("namaruang")
    private String namaruang;

    /**
     * Total room capacity.
     */
    @JsonProperty("kapasitas")
    private String kapasitas;

    /**
     * Total available beds.
     */
    @JsonProperty("tersedia")
    private String tersedia;

    /**
     * Available beds for male patients.
     */
    @JsonProperty("tersediapria")
    private String tersediapria;

    /**
     * Available beds for female patients.
     */
    @JsonProperty("tersediawanita")
    private String tersediawanita;

    /**
     * Available beds for male or female patients (unisex).
     */
    @JsonProperty("tersediapriawanita")
    private String tersediapriawanita;
}
