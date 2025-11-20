package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Medication Administration Record (MAR) Entity.
 *
 * Tracks all medication administrations during hospitalization including
 * scheduled medications, PRN, STAT orders, and adverse reactions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "medication_administration", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_mar_number", columnList = "mar_number"),
        @Index(name = "idx_mar_encounter", columnList = "encounter_id"),
        @Index(name = "idx_mar_patient", columnList = "patient_id"),
        @Index(name = "idx_mar_scheduled_time", columnList = "scheduled_date_time"),
        @Index(name = "idx_mar_admin_time", columnList = "actual_administration_date_time"),
        @Index(name = "idx_mar_status", columnList = "administration_status"),
        @Index(name = "idx_mar_adverse", columnList = "adverse_reaction"),
        @Index(name = "idx_mar_high_alert", columnList = "is_high_alert_medication")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Medication Administration Record (MAR) for inpatient care")
public class MedicationAdministration extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "mar_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "MAR number is required")
    private String marNumber; // MAR-20251120-0001

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @Column(name = "medication_order_id")
    private UUID medicationOrderId; // Link to pharmacy order

    // ========== Medication Identification ==========
    @Column(name = "medication_name", nullable = false, length = 200)
    @NotBlank(message = "Medication name is required")
    private String medicationName;

    @Column(name = "generic_name", length = 200)
    private String genericName;

    @Column(name = "brand_name", length = 200)
    private String brandName;

    @Column(name = "medication_code", length = 50)
    private String medicationCode; // Formulary code or NDC

    @Column(name = "medication_class", length = 100)
    private String medicationClass; // ANTIBIOTIC, ANALGESIC, etc.

    // ========== Dosage Information ==========
    @Column(name = "dose", nullable = false, length = 50)
    @NotBlank(message = "Dose is required")
    private String dose; // e.g., "500", "2"

    @Column(name = "dose_unit", nullable = false, length = 20)
    @NotBlank(message = "Dose unit is required")
    private String doseUnit; // mg, ml, unit, tablet, capsule

    @Column(name = "strength", length = 50)
    private String strength; // e.g., "500mg/tablet"

    @Column(name = "total_dose_description", length = 100)
    private String totalDoseDescription; // e.g., "500mg (1 tablet)"

    // ========== Route and Frequency ==========
    @Column(name = "route", nullable = false, length = 50)
    @NotBlank(message = "Route is required")
    private String route; // ORAL, IV, IM, SC, TOPICAL, RECTAL, SUBLINGUAL

    @Column(name = "frequency", nullable = false, length = 50)
    @NotBlank(message = "Frequency is required")
    private String frequency; // BID, TID, QID, Q4H, Q6H, PRN, STAT, ONCE

    @Column(name = "frequency_times_per_day")
    private Integer frequencyTimesPerDay;

    // ========== Schedule Information ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 20)
    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType; // SCHEDULED, PRN, STAT, ONE_TIME

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "scheduled_date_time")
    private LocalDateTime scheduledDateTime;

    // ========== Administration Details ==========
    @Column(name = "actual_administration_date_time")
    private LocalDateTime actualAdministrationDateTime;

    @Column(name = "administered")
    @Builder.Default
    private Boolean administered = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "administration_status", length = 20)
    @Builder.Default
    private AdministrationStatus administrationStatus = AdministrationStatus.PENDING;

    @Column(name = "administration_site", length = 100)
    private String administrationSite; // LEFT_ARM, RIGHT_ARM, ABDOMEN, etc.

    // ========== Provider Information ==========
    @Column(name = "administered_by_id")
    private UUID administeredById;

    @Column(name = "administered_by_name", length = 200)
    private String administeredByName;

    @Column(name = "administered_by_role", length = 50)
    private String administeredByRole; // NURSE, DOCTOR

    // ========== Verification/Witness ==========
    @Column(name = "requires_witness")
    @Builder.Default
    private Boolean requiresWitness = false;

    @Column(name = "witnessed_by_id")
    private UUID witnessedById;

    @Column(name = "witnessed_by_name", length = 200)
    private String witnessedByName;

    @Column(name = "witness_signature", length = 200)
    private String witnessSignature;

    // ========== Reasons for Not Giving ==========
    @Column(name = "not_given_reason", columnDefinition = "TEXT")
    private String notGivenReason;

    @Column(name = "hold_reason", columnDefinition = "TEXT")
    private String holdReason;

    @Column(name = "discontinue_reason", columnDefinition = "TEXT")
    private String discontinueReason;

    // ========== Patient Response ==========
    @Column(name = "patient_response", columnDefinition = "TEXT")
    private String patientResponse;

    @Column(name = "adverse_reaction")
    @Builder.Default
    private Boolean adverseReaction = false;

    @Column(name = "adverse_reaction_type", length = 50)
    private String adverseReactionType; // ALLERGIC, SIDE_EFFECT, OTHER

    @Column(name = "adverse_reaction_details", columnDefinition = "TEXT")
    private String adverseReactionDetails;

    @Column(name = "adverse_reaction_severity", length = 20)
    private String adverseReactionSeverity; // MILD, MODERATE, SEVERE

    @Column(name = "adverse_reaction_reported")
    @Builder.Default
    private Boolean adverseReactionReported = false;

    // ========== PRN Specific ==========
    @Column(name = "prn_reason", columnDefinition = "TEXT")
    private String prnReason;

    @Column(name = "prn_effectiveness", columnDefinition = "TEXT")
    private String prnEffectiveness;

    // ========== Prescriber Information ==========
    @Column(name = "prescribed_by_id")
    private UUID prescribedById;

    @Column(name = "prescribed_by_name", length = 200)
    private String prescribedByName;

    @Column(name = "prescription_date")
    private LocalDateTime prescriptionDate;

    // ========== IV Specific Information ==========
    @Column(name = "iv_solution", length = 200)
    private String ivSolution;

    @Column(name = "iv_volume_ml")
    private Integer ivVolumeMl;

    @Column(name = "iv_rate_ml_per_hour", precision = 6, scale = 2)
    private BigDecimal ivRateMlPerHour;

    @Column(name = "iv_duration_minutes")
    private Integer ivDurationMinutes;

    @Column(name = "iv_site_location", length = 100)
    private String ivSiteLocation;

    // ========== Notes ==========
    @Column(name = "administration_notes", columnDefinition = "TEXT")
    private String administrationNotes;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // ========== Alerts ==========
    @Column(name = "is_high_alert_medication")
    @Builder.Default
    private Boolean isHighAlertMedication = false;

    @Column(name = "high_alert_type", length = 50)
    private String highAlertType; // NARCOTIC, INSULIN, ANTICOAGULANT, etc.

    // ========== Business Methods ==========

    /**
     * Mark medication as administered.
     */
    public void markAsAdministered(UUID nurseId, String nurseName) {
        this.administered = true;
        this.administrationStatus = AdministrationStatus.GIVEN;
        this.actualAdministrationDateTime = LocalDateTime.now();
        this.administeredById = nurseId;
        this.administeredByName = nurseName;
    }

    /**
     * Mark medication as refused by patient.
     */
    public void markAsRefused(String reason) {
        this.administered = false;
        this.administrationStatus = AdministrationStatus.REFUSED;
        this.notGivenReason = reason;
    }

    /**
     * Hold medication.
     */
    public void hold(String reason) {
        this.administered = false;
        this.administrationStatus = AdministrationStatus.HELD;
        this.holdReason = reason;
    }

    /**
     * Mark as missed.
     */
    public void markAsMissed() {
        this.administered = false;
        this.administrationStatus = AdministrationStatus.MISSED;
    }

    /**
     * Discontinue medication.
     */
    public void discontinue(String reason) {
        this.administrationStatus = AdministrationStatus.DISCONTINUED;
        this.discontinueReason = reason;
    }

    /**
     * Report adverse reaction.
     */
    public void reportAdverseReaction(String type, String details, String severity) {
        this.adverseReaction = true;
        this.adverseReactionType = type;
        this.adverseReactionDetails = details;
        this.adverseReactionSeverity = severity;
        this.adverseReactionReported = true;
    }

    /**
     * Check if medication is due (within 1 hour of scheduled time).
     */
    public boolean isDue() {
        if (scheduledDateTime == null || administered) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueWindow = scheduledDateTime.plusHours(1);

        return !now.isBefore(scheduledDateTime) && now.isBefore(dueWindow);
    }

    /**
     * Check if medication is overdue.
     */
    public boolean isOverdue() {
        if (scheduledDateTime == null || administered) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime overdueTime = scheduledDateTime.plusHours(1);

        return now.isAfter(overdueTime);
    }

    /**
     * Check if medication needs witness verification.
     */
    public boolean needsWitnessVerification() {
        return Boolean.TRUE.equals(requiresWitness) && witnessedById == null;
    }

    /**
     * Add witness verification.
     */
    public void addWitness(UUID witnessId, String witnessName, String signature) {
        this.witnessedById = witnessId;
        this.witnessedByName = witnessName;
        this.witnessSignature = signature;
    }
}
