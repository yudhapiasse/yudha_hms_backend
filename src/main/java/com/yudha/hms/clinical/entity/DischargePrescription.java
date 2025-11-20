package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Discharge Prescription Entity.
 *
 * Represents medications prescribed at discharge with dosing instructions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "discharge_prescription", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_rx_summary", columnList = "discharge_summary_id"),
        @Index(name = "idx_rx_medication", columnList = "medication_name")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DischargePrescription extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Reference ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discharge_summary_id", nullable = false)
    private DischargeSummary dischargeSummary;

    // ========== Medication Information ==========

    @Column(name = "medication_id")
    private UUID medicationId; // Reference to medication master if exists

    @Column(name = "medication_name", nullable = false, length = 200)
    private String medicationName;

    @Column(name = "generic_name", length = 200)
    private String genericName;

    @Column(name = "medication_category", length = 100)
    private String medicationCategory;

    // ========== Dosing Instructions ==========

    @Column(name = "dosage", nullable = false, length = 100)
    private String dosage; // e.g., "500 mg", "10 ml"

    @Column(name = "route", nullable = false, length = 50)
    private String route; // ORAL, IV, IM, TOPICAL, etc.

    @Column(name = "frequency", nullable = false, length = 100)
    private String frequency; // e.g., "3x daily", "Every 8 hours", "PRN"

    @Column(name = "duration", length = 100)
    private String duration; // e.g., "7 days", "14 days", "Until finished"

    @Column(name = "quantity")
    private Integer quantity; // Total quantity to dispense

    @Column(name = "unit", length = 50)
    private String unit; // tablets, capsules, ml, etc.

    // ========== Administration Instructions ==========

    @Column(name = "timing", length = 200)
    private String timing; // e.g., "Before meals", "At bedtime"

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "food_interaction", length = 200)
    private String foodInteraction; // e.g., "Take with food", "Avoid dairy"

    // ========== Purpose and Warnings ==========

    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose; // Why this medication is prescribed

    @Column(name = "side_effects", columnDefinition = "TEXT")
    private String sideEffects; // Common side effects to watch for

    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings; // Important warnings

    // ========== Prescription Details ==========

    @Column(name = "is_new_medication")
    @Builder.Default
    private Boolean isNewMedication = false; // New vs. continuation

    @Column(name = "is_changed_medication")
    @Builder.Default
    private Boolean isChangedMedication = false; // Dosage or frequency changed

    @Column(name = "change_notes", columnDefinition = "TEXT")
    private String changeNotes; // Notes about changes

    @Column(name = "refills_allowed")
    private Integer refillsAllowed;

    // ========== Pharmacy Instructions ==========

    @Column(name = "pharmacy_notes", columnDefinition = "TEXT")
    private String pharmacyNotes;

    @Column(name = "substitution_allowed")
    @Builder.Default
    private Boolean substitutionAllowed = true;

    // ========== Prescriber ==========

    @Column(name = "prescriber_id")
    private UUID prescriberId;

    @Column(name = "prescriber_name", length = 200)
    private String prescriberName;

    // ========== Status ==========

    @Column(name = "prescription_status", length = 30)
    @Builder.Default
    private String prescriptionStatus = "ACTIVE"; // ACTIVE, DISCONTINUED, COMPLETED

    @Column(name = "discontinued_reason", columnDefinition = "TEXT")
    private String discontinuedReason;

    // ========== Display Order ==========

    @Column(name = "display_order")
    private Integer displayOrder;

    // ========== Business Methods ==========

    public boolean isCriticalMedication() {
        return medicationCategory != null &&
               (medicationCategory.contains("ANTIBIOTIC") ||
                medicationCategory.contains("CARDIAC") ||
                medicationCategory.contains("ANTICOAGULANT"));
    }

    public String getFullDosageInstructions() {
        StringBuilder sb = new StringBuilder();
        sb.append(dosage).append(" ");
        sb.append(route).append(", ");
        sb.append(frequency);

        if (timing != null && !timing.isEmpty()) {
            sb.append(" - ").append(timing);
        }

        if (duration != null && !duration.isEmpty()) {
            sb.append(" for ").append(duration);
        }

        return sb.toString();
    }
}
