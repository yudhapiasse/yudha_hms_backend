package com.yudha.hms.registration.entity;

import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Emergency Intervention Entity.
 *
 * Tracks critical interventions, procedures, and treatments during emergency care:
 * - Resuscitation events (CPR, defibrillation)
 * - Airway management (intubation, tracheostomy)
 * - Vascular access (central lines, arterial lines)
 * - Emergency procedures (chest tube, etc.)
 * - Emergency medications
 * - Blood transfusions
 * - Cardiac interventions (cardioversion, pacing)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "emergency_intervention", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_intervention_emergency", columnList = "emergency_registration_id"),
        @Index(name = "idx_intervention_encounter", columnList = "encounter_id"),
        @Index(name = "idx_intervention_time", columnList = "intervention_time"),
        @Index(name = "idx_intervention_type", columnList = "intervention_type"),
        @Index(name = "idx_intervention_performer", columnList = "performed_by_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Emergency interventions and critical procedures tracking")
public class EmergencyIntervention extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Links ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_registration_id", nullable = false)
    @NotNull
    private EmergencyRegistration emergencyRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull
    private Encounter encounter;

    // ========== Intervention Metadata ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "intervention_type", nullable = false, length = 50)
    @NotNull
    private InterventionType interventionType;

    @Column(name = "intervention_name", nullable = false, length = 200)
    @NotBlank
    private String interventionName;

    @Column(name = "intervention_time", nullable = false)
    @NotNull
    @Builder.Default
    private LocalDateTime interventionTime = LocalDateTime.now();

    @Column(name = "performed_by_id")
    private UUID performedById;

    @Column(name = "performed_by_name", nullable = false, length = 200)
    @NotBlank
    private String performedByName;

    @Column(name = "performed_by_role", length = 50)
    private String performedByRole; // DOCTOR, NURSE, PARAMEDIC

    // ========== Resuscitation Specific ==========
    @Column(name = "is_resuscitation", nullable = false)
    @Builder.Default
    private Boolean isResuscitation = false;

    @Column(name = "resuscitation_start_time")
    private LocalDateTime resuscitationStartTime;

    @Column(name = "resuscitation_end_time")
    private LocalDateTime resuscitationEndTime;

    @Column(name = "resuscitation_duration_minutes")
    private Integer resuscitationDurationMinutes;

    @Column(name = "rosc_achieved")
    private Boolean roscAchieved; // Return of Spontaneous Circulation

    @Column(name = "rosc_time")
    private LocalDateTime roscTime;

    @Column(name = "cpr_quality_score")
    @Min(0)
    @Max(100)
    private Integer cprQualityScore;

    @Column(name = "defibrillation_attempts")
    @Min(0)
    private Integer defibrillationAttempts;

    @Column(name = "epinephrine_doses")
    @Min(0)
    private Integer epinephrineDoses;

    // ========== Airway Management Specific ==========
    @Column(name = "airway_type", length = 50)
    private String airwayType; // INTUBATION, TRACHEOSTOMY, CRICOTHYROIDOTOMY, LMA

    @Column(name = "tube_size", length = 20)
    private String tubeSize;

    @Column(name = "insertion_attempts")
    @Min(0)
    private Integer insertionAttempts;

    @Column(name = "airway_secured")
    private Boolean airwaySecured;

    // ========== Procedure Specific ==========
    @Column(name = "procedure_code", length = 50)
    private String procedureCode;

    @Column(name = "procedure_site", length = 100)
    private String procedureSite;

    @Column(name = "procedure_approach", length = 50)
    private String procedureApproach; // PERCUTANEOUS, SURGICAL

    @Column(name = "complications", columnDefinition = "TEXT")
    private String complications;

    @Column(name = "procedure_outcome", length = 50)
    private String procedureOutcome; // SUCCESS, FAILED, PARTIAL

    // ========== Medication Specific ==========
    @Column(name = "medication_name", length = 200)
    private String medicationName;

    @Column(name = "medication_dose", length = 100)
    private String medicationDose;

    @Column(name = "medication_route", length = 50)
    private String medicationRoute; // IV, IM, PO, SUBLINGUAL

    @Column(name = "medication_frequency", length = 100)
    private String medicationFrequency;

    // ========== Transfusion Specific ==========
    @Column(name = "blood_product_type", length = 50)
    private String bloodProductType; // PRBC, FFP, PLATELETS, CRYOPRECIPITATE

    @Column(name = "units_transfused")
    @Min(0)
    private Integer unitsTransfused;

    @Column(name = "transfusion_reaction")
    private Boolean transfusionReaction;

    @Column(name = "cross_match_required")
    private Boolean crossMatchRequired;

    // ========== Common Fields ==========
    @Column(name = "indication", columnDefinition = "TEXT", nullable = false)
    @NotBlank
    private String indication;

    @Column(name = "urgency", length = 20)
    @Builder.Default
    private String urgency = "ROUTINE"; // EMERGENCY, URGENT, ROUTINE

    @Column(name = "outcome", length = 50)
    private String outcome;

    @Column(name = "outcome_notes", columnDefinition = "TEXT")
    private String outcomeNotes;

    @Column(name = "complications_occurred", nullable = false)
    @Builder.Default
    private Boolean complicationsOccurred = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Location ==========
    @Column(name = "location", length = 100)
    private String location; // RED_ZONE, RESUS_ROOM, etc.

    @Column(name = "bed_number", length = 20)
    private String bedNumber;

    // ========== Business Methods ==========

    /**
     * Mark intervention as completed with outcome.
     */
    public void complete(String outcome, String outcomeNotes) {
        this.outcome = outcome;
        this.outcomeNotes = outcomeNotes;

        // If resuscitation, calculate duration
        if (Boolean.TRUE.equals(isResuscitation) && resuscitationEndTime == null) {
            this.resuscitationEndTime = LocalDateTime.now();
            calculateResuscitationDuration();
        }
    }

    /**
     * Record ROSC (Return of Spontaneous Circulation) achievement.
     */
    public void recordROSC() {
        if (!Boolean.TRUE.equals(isResuscitation)) {
            throw new IllegalStateException("Cannot record ROSC for non-resuscitation intervention");
        }
        this.roscAchieved = true;
        this.roscTime = LocalDateTime.now();
    }

    /**
     * Record complication.
     */
    public void recordComplication(String complicationDetails) {
        this.complicationsOccurred = true;
        if (this.complications == null) {
            this.complications = complicationDetails;
        } else {
            this.complications = this.complications + "; " + complicationDetails;
        }
    }

    /**
     * Calculate resuscitation duration.
     */
    public void calculateResuscitationDuration() {
        if (resuscitationStartTime != null && resuscitationEndTime != null) {
            Duration duration = Duration.between(resuscitationStartTime, resuscitationEndTime);
            this.resuscitationDurationMinutes = (int) duration.toMinutes();
        }
    }

    /**
     * Check if intervention is critical.
     */
    public boolean isCriticalIntervention() {
        return interventionType == InterventionType.RESUSCITATION ||
               interventionType == InterventionType.AIRWAY_MANAGEMENT ||
               interventionType == InterventionType.CHEST_TUBE ||
               interventionType == InterventionType.CARDIOVERSION ||
               interventionType == InterventionType.DEFIBRILLATION;
    }

    /**
     * Check if intervention requires supervision.
     */
    public boolean requiresSupervision() {
        return interventionType == InterventionType.RESUSCITATION ||
               interventionType == InterventionType.AIRWAY_MANAGEMENT ||
               interventionType == InterventionType.CENTRAL_LINE ||
               interventionType == InterventionType.CHEST_TUBE;
    }

    /**
     * Get intervention duration in minutes (for procedures).
     */
    public Integer getInterventionDurationMinutes() {
        if (isResuscitation && resuscitationDurationMinutes != null) {
            return resuscitationDurationMinutes;
        }
        return null;
    }

    /**
     * Check if resuscitation was successful.
     */
    public boolean isResuscitationSuccessful() {
        return Boolean.TRUE.equals(isResuscitation) && Boolean.TRUE.equals(roscAchieved);
    }

    /**
     * Get display name for intervention.
     */
    public String getDisplayName() {
        if (interventionName != null && !interventionName.isEmpty()) {
            return interventionName;
        }
        return interventionType != null ? interventionType.getDisplayName() : "Unknown Intervention";
    }
}
