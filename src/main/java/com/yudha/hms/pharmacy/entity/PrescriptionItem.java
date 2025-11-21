package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.DosageFrequency;
import com.yudha.hms.pharmacy.constant.RouteOfAdministration;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Prescription Item Entity.
 *
 * Individual medication items within a prescription.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "prescription_item", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_prescription_item_prescription", columnList = "prescription_id"),
        @Index(name = "idx_prescription_item_drug", columnList = "drug_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PrescriptionItem extends BaseEntity {

    /**
     * Prescription reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    /**
     * Line number
     */
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    /**
     * Drug reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    /**
     * Drug code (denormalized)
     */
    @Column(name = "drug_code", length = 50)
    private String drugCode;

    /**
     * Drug name (denormalized)
     */
    @Column(name = "drug_name", length = 200)
    private String drugName;

    /**
     * Strength
     */
    @Column(name = "strength", length = 100)
    private String strength;

    /**
     * Dosage form
     */
    @Column(name = "dosage_form", length = 100)
    private String dosageForm;

    /**
     * Dose quantity
     */
    @Column(name = "dose_quantity", precision = 10, scale = 2)
    private BigDecimal doseQuantity;

    /**
     * Dose unit
     */
    @Column(name = "dose_unit", length = 50)
    private String doseUnit;

    /**
     * Dosage frequency
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 50)
    private DosageFrequency frequency;

    /**
     * Custom frequency instructions
     */
    @Column(name = "custom_frequency", length = 200)
    private String customFrequency;

    /**
     * Route of administration
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "route", nullable = false, length = 50)
    private RouteOfAdministration route;

    /**
     * Duration in days
     */
    @Column(name = "duration_days")
    private Integer durationDays;

    /**
     * Quantity to dispense
     */
    @Column(name = "quantity_to_dispense", precision = 10, scale = 2)
    private BigDecimal quantityToDispense;

    /**
     * Quantity dispensed
     */
    @Column(name = "quantity_dispensed", precision = 10, scale = 2)
    private BigDecimal quantityDispensed;

    /**
     * Unit price
     */
    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Total price
     */
    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Administration instructions
     */
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    /**
     * Special instructions (e.g., "Take with food", "Avoid alcohol")
     */
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    /**
     * Is PRN (as needed)
     */
    @Column(name = "is_prn")
    private Boolean isPrn;

    /**
     * PRN indication
     */
    @Column(name = "prn_indication", length = 200)
    private String prnIndication;

    /**
     * Is substitution allowed
     */
    @Column(name = "substitution_allowed")
    @Builder.Default
    private Boolean substitutionAllowed = true;

    /**
     * Substituted drug ID
     */
    @Column(name = "substituted_drug_id")
    private UUID substitutedDrugId;

    /**
     * Substituted drug name
     */
    @Column(name = "substituted_drug_name", length = 200)
    private String substitutedDrugName;

    /**
     * Substitution reason
     */
    @Column(name = "substitution_reason", length = 500)
    private String substitutionReason;

    /**
     * Is controlled drug
     */
    @Column(name = "is_controlled")
    private Boolean isControlled;

    /**
     * Is high alert medication
     */
    @Column(name = "is_high_alert")
    private Boolean isHighAlert;

    /**
     * Interaction warnings for this item
     */
    @Column(name = "interaction_warnings", columnDefinition = "TEXT")
    private String interactionWarnings;

    /**
     * Label printed
     */
    @Column(name = "label_printed")
    @Builder.Default
    private Boolean labelPrinted = false;

    /**
     * Calculate total daily dose
     */
    public BigDecimal calculateDailyDose() {
        if (doseQuantity == null || frequency == null) {
            return BigDecimal.ZERO;
        }
        return doseQuantity.multiply(BigDecimal.valueOf(frequency.getDailyDoseMultiplier()));
    }

    /**
     * Calculate total quantity needed for duration
     */
    public BigDecimal calculateTotalQuantity() {
        if (doseQuantity == null || frequency == null || durationDays == null) {
            return quantityToDispense != null ? quantityToDispense : BigDecimal.ZERO;
        }
        return calculateDailyDose().multiply(BigDecimal.valueOf(durationDays));
    }

    /**
     * Check if item is fully dispensed
     */
    public boolean isFullyDispensed() {
        if (quantityToDispense == null || quantityDispensed == null) {
            return false;
        }
        return quantityDispensed.compareTo(quantityToDispense) >= 0;
    }

    /**
     * Check if item has been substituted
     */
    public boolean isSubstituted() {
        return substitutedDrugId != null;
    }

    /**
     * Pre-persist calculation
     */
    @PrePersist
    @PreUpdate
    protected void calculateFields() {
        // Calculate total price
        if (unitPrice != null && quantityToDispense != null) {
            totalPrice = unitPrice.multiply(quantityToDispense);
        }

        // Set PRN flag based on frequency
        if (frequency != null) {
            isPrn = frequency.isAsNeeded();
        }

        // Set flags from drug
        if (drug != null) {
            isControlled = drug.isControlledSubstance();
            isHighAlert = drug.getIsHighAlert();
        }
    }
}
