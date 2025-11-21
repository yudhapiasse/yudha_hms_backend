package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.PrescriptionStatus;
import com.yudha.hms.pharmacy.constant.PrescriptionType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Prescription Entity.
 *
 * E-Prescribing system for medication orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "prescription", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_prescription_number", columnList = "prescription_number", unique = true),
        @Index(name = "idx_prescription_patient", columnList = "patient_id"),
        @Index(name = "idx_prescription_encounter", columnList = "encounter_id"),
        @Index(name = "idx_prescription_doctor", columnList = "doctor_id"),
        @Index(name = "idx_prescription_status", columnList = "status"),
        @Index(name = "idx_prescription_date", columnList = "prescription_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Prescription extends SoftDeletableEntity {

    /**
     * Prescription number (unique identifier)
     */
    @Column(name = "prescription_number", nullable = false, unique = true, length = 50)
    private String prescriptionNumber;

    /**
     * Patient ID
     */
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    /**
     * Patient name (denormalized)
     */
    @Column(name = "patient_name", length = 200)
    private String patientName;

    /**
     * Encounter ID
     */
    @Column(name = "encounter_id")
    private UUID encounterId;

    /**
     * Doctor ID (prescriber)
     */
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    /**
     * Doctor name (denormalized)
     */
    @Column(name = "doctor_name", length = 200)
    private String doctorName;

    /**
     * Prescription date
     */
    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    /**
     * Prescription type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "prescription_type", nullable = false, length = 50)
    private PrescriptionType prescriptionType;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PrescriptionStatus status;

    /**
     * Valid until date
     */
    @Column(name = "valid_until")
    private LocalDate validUntil;

    /**
     * Diagnosis
     */
    @Column(name = "diagnosis", length = 500)
    private String diagnosis;

    /**
     * ICD-10 codes
     */
    @Column(name = "icd10_codes", length = 500)
    private String icd10Codes;

    /**
     * Special instructions
     */
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    /**
     * Allergies noted
     */
    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    /**
     * Has drug interactions
     */
    @Column(name = "has_interactions")
    private Boolean hasInteractions;

    /**
     * Interaction warnings
     */
    @Column(name = "interaction_warnings", columnDefinition = "TEXT")
    private String interactionWarnings;

    /**
     * Submitted to pharmacist at
     */
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    /**
     * Verified at
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * Verified by pharmacist ID
     */
    @Column(name = "verified_by")
    private UUID verifiedBy;

    /**
     * Verified by pharmacist name
     */
    @Column(name = "verified_by_name", length = 200)
    private String verifiedByName;

    /**
     * Dispensed at
     */
    @Column(name = "dispensed_at")
    private LocalDateTime dispensedAt;

    /**
     * Dispensed by pharmacist ID
     */
    @Column(name = "dispensed_by")
    private UUID dispensedBy;

    /**
     * Dispensed by pharmacist name
     */
    @Column(name = "dispensed_by_name", length = 200)
    private String dispensedByName;

    /**
     * Is controlled drug prescription
     */
    @Column(name = "is_controlled")
    private Boolean isControlled;

    /**
     * Requires special authorization
     */
    @Column(name = "requires_authorization")
    private Boolean requiresAuthorization;

    /**
     * Authorization number
     */
    @Column(name = "authorization_number", length = 100)
    private String authorizationNumber;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Prescription items
     */
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PrescriptionItem> items = new ArrayList<>();

    /**
     * Verification records
     */
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PrescriptionVerification> verifications = new ArrayList<>();

    /**
     * Submit prescription for verification
     */
    public void submit() {
        if (this.status != PrescriptionStatus.DRAFT) {
            throw new IllegalStateException("Only draft prescriptions can be submitted");
        }
        this.status = PrescriptionStatus.PENDING_VERIFICATION;
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Verify prescription
     */
    public void verify(UUID pharmacistId, String pharmacistName) {
        if (!this.status.canBeVerified()) {
            throw new IllegalStateException("Prescription cannot be verified in current status: " + this.status);
        }
        this.status = PrescriptionStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
        this.verifiedBy = pharmacistId;
        this.verifiedByName = pharmacistName;
    }

    /**
     * Reject prescription
     */
    public void reject(String reason) {
        this.status = PrescriptionStatus.REJECTED;
        this.notes = (this.notes != null ? this.notes + "\n" : "") + "Rejected: " + reason;
    }

    /**
     * Mark as dispensed
     */
    public void markDispensed(UUID pharmacistId, String pharmacistName) {
        if (!this.status.canBeDispensed()) {
            throw new IllegalStateException("Prescription cannot be dispensed in current status: " + this.status);
        }
        this.status = PrescriptionStatus.DISPENSED;
        this.dispensedAt = LocalDateTime.now();
        this.dispensedBy = pharmacistId;
        this.dispensedByName = pharmacistName;
    }

    /**
     * Check if prescription is expired
     */
    public boolean isExpired() {
        if (validUntil == null) {
            return false;
        }
        return LocalDate.now().isAfter(validUntil);
    }

    /**
     * Check if prescription contains controlled drugs
     */
    public boolean hasControlledDrugs() {
        return isControlled != null && isControlled;
    }

    /**
     * Add prescription item
     */
    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }

    /**
     * Remove prescription item
     */
    public void removeItem(PrescriptionItem item) {
        items.remove(item);
        item.setPrescription(null);
    }
}
