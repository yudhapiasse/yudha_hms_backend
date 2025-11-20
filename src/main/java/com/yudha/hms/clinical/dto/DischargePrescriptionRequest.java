package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Discharge Prescription Request DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DischargePrescriptionRequest {

    private UUID medicationId;

    @NotBlank(message = "Medication name is required")
    private String medicationName;

    private String genericName;
    private String medicationCategory;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotBlank(message = "Route is required")
    private String route;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    private String duration;
    private Integer quantity;
    private String unit;

    private String timing;
    private String specialInstructions;
    private String foodInteraction;

    private String purpose;
    private String sideEffects;
    private String warnings;

    private Boolean isNewMedication;
    private Boolean isChangedMedication;
    private String changeNotes;

    private Integer refillsAllowed;
    private String pharmacyNotes;
    private Boolean substitutionAllowed;

    private UUID prescriberId;
    private String prescriberName;

    private Integer displayOrder;
}
