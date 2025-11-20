package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Prescription Item Response DTO.
 *
 * Represents medication prescriptions linked to an encounter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItemResponse {

    private UUID id;
    private String prescriptionNumber;
    private String medicationName;
    private String medicationCode;
    private String dosage;
    private String frequency;
    private String route; // ORAL, IV, IM, TOPICAL
    private Integer duration;
    private String durationUnit; // DAYS, WEEKS, MONTHS
    private String quantity;
    private LocalDateTime prescribedDate;
    private String prescribedByName;
    private Boolean validated;
    private Boolean dispensed;
    private LocalDateTime dispensedDate;
    private Boolean stockAvailable;
    private String notes;
}
