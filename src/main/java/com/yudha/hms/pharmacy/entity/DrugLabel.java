package com.yudha.hms.pharmacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Drug Label Entity.
 *
 * Stores generated drug labels with instructions for patient medication.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "drug_label", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_label_dispensing", columnList = "dispensing_id"),
        @Index(name = "idx_label_dispensing_item", columnList = "dispensing_item_id"),
        @Index(name = "idx_label_patient", columnList = "patient_id"),
        @Index(name = "idx_label_printed_at", columnList = "printed_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_id", nullable = false)
    private Dispensing dispensing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_item_id", nullable = false)
    private DispensingItem dispensingItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", length = 200)
    private String patientName;

    @Column(name = "patient_age")
    private Integer patientAge;

    @Column(name = "patient_weight")
    private Double patientWeight;

    // Label content
    @Column(name = "drug_name", nullable = false, length = 500)
    private String drugName;

    @Column(name = "drug_strength", length = 100)
    private String drugStrength;

    @Column(name = "drug_form", length = 100)
    private String drugForm;

    @Column(name = "quantity_dispensed", length = 50)
    private String quantityDispensed;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "expiry_date", length = 50)
    private String expiryDate;

    @Column(name = "dosage_instruction", columnDefinition = "TEXT")
    private String dosageInstruction;

    @Column(name = "frequency", length = 200)
    private String frequency;

    @Column(name = "duration", length = 100)
    private String duration;

    @Column(name = "route", length = 50)
    private String route;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // Warnings and precautions
    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings;

    @Column(name = "precautions", columnDefinition = "TEXT")
    private String precautions;

    @Column(name = "storage_instructions", columnDefinition = "TEXT")
    private String storageInstructions;

    @Column(name = "side_effects", columnDefinition = "TEXT")
    private String sideEffects;

    // Prescriber information
    @Column(name = "prescriber_name", length = 200)
    private String prescriberName;

    @Column(name = "prescription_date")
    private LocalDateTime prescriptionDate;

    @Column(name = "prescription_number", length = 50)
    private String prescriptionNumber;

    // Pharmacy information
    @Column(name = "pharmacy_name", length = 200)
    private String pharmacyName;

    @Column(name = "pharmacy_address", columnDefinition = "TEXT")
    private String pharmacyAddress;

    @Column(name = "pharmacy_phone", length = 50)
    private String pharmacyPhone;

    @Column(name = "pharmacist_name", length = 200)
    private String pharmacistName;

    // Label metadata
    @Column(name = "label_format", length = 50)
    private String labelFormat = "STANDARD";

    @Column(name = "label_size", length = 50)
    private String labelSize;

    @Column(name = "barcode_data", length = 200)
    private String barcodeData;

    @Column(name = "qr_code_data", columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(name = "language", length = 10)
    private String language = "id";

    @Column(name = "print_count")
    private Integer printCount = 0;

    @Column(name = "printed_at")
    private LocalDateTime printedAt;

    @Column(name = "printed_by_id")
    private UUID printedById;

    @Column(name = "printed_by_name", length = 200)
    private String printedByName;

    @Column(name = "template_name", length = 100)
    private String templateName;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    // Business methods

    /**
     * Record label printing
     */
    public void recordPrint(UUID printedBy, String printedByName) {
        this.printCount = (this.printCount == null ? 0 : this.printCount) + 1;
        this.printedById = printedBy;
        this.printedByName = printedByName;
        this.printedAt = LocalDateTime.now();
    }

    /**
     * Get full dosage instruction for label
     */
    public String getFullInstruction() {
        StringBuilder instruction = new StringBuilder();

        if (dosageInstruction != null && !dosageInstruction.isEmpty()) {
            instruction.append(dosageInstruction);
        }

        if (frequency != null && !frequency.isEmpty()) {
            if (instruction.length() > 0) instruction.append(" - ");
            instruction.append(frequency);
        }

        if (duration != null && !duration.isEmpty()) {
            if (instruction.length() > 0) instruction.append(" - ");
            instruction.append("Durasi: ").append(duration);
        }

        if (route != null && !route.isEmpty()) {
            if (instruction.length() > 0) instruction.append(" - ");
            instruction.append("Cara: ").append(route);
        }

        return instruction.toString();
    }

    /**
     * Check if label needs warning indicators
     */
    public boolean hasWarnings() {
        return (warnings != null && !warnings.isEmpty())
            || (precautions != null && !precautions.isEmpty());
    }

    /**
     * Check if this is a reprint
     */
    public boolean isReprint() {
        return printCount != null && printCount > 1;
    }
}
