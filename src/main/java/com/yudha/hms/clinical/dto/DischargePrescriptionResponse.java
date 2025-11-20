package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Discharge Prescription Response DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DischargePrescriptionResponse {

    private UUID id;
    private UUID dischargeSummaryId;

    // Medication Information
    private UUID medicationId;
    private String medicationName;
    private String genericName;
    private String medicationCategory;

    // Dosing Instructions
    private String dosage;
    private String route;
    private String frequency;
    private String duration;
    private Integer quantity;
    private String unit;

    // Administration Instructions
    private String timing;
    private String specialInstructions;
    private String foodInteraction;

    // Purpose and Warnings
    private String purpose;
    private String sideEffects;
    private String warnings;

    // Prescription Details
    private Boolean isNewMedication;
    private Boolean isChangedMedication;
    private String changeNotes;
    private Integer refillsAllowed;

    // Pharmacy Instructions
    private String pharmacyNotes;
    private Boolean substitutionAllowed;

    // Prescriber
    private UUID prescriberId;
    private String prescriberName;

    // Status
    private String prescriptionStatus;
    private String discontinuedReason;

    // Display
    private Integer displayOrder;

    // Computed
    private String fullDosageInstructions;
    private Boolean isCriticalMedication;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
