package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Detailed Triage Assessment using Emergency Severity Index (ESI).
 *
 * Comprehensive triage evaluation including:
 * - ESI level determination (1-5)
 * - Vital signs monitoring
 * - Neurological assessment (GCS)
 * - Pain assessment
 * - Resource needs prediction
 * - Red flags identification
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "triage_assessment", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_triage_emergency", columnList = "emergency_registration_id"),
        @Index(name = "idx_triage_time", columnList = "triage_time"),
        @Index(name = "idx_triage_esi", columnList = "esi_level")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Detailed triage assessments using ESI methodology")
public class TriageAssessment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Link to Emergency Registration ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_registration_id", nullable = false)
    @NotNull
    private EmergencyRegistration emergencyRegistration;

    // ========== Triage Metadata ==========
    @Column(name = "triage_time", nullable = false)
    @NotNull
    @Builder.Default
    private LocalDateTime triageTime = LocalDateTime.now();

    @Column(name = "triaged_by_id")
    private UUID triagedById;

    @Column(name = "triaged_by_name", nullable = false, length = 200)
    @NotBlank
    private String triagedByName;

    @Column(name = "triage_method", nullable = false, length = 30)
    @NotBlank
    @Builder.Default
    private String triageMethod = "ESI"; // ESI, ATS, CTAS, MTS

    // ========== ESI Level ==========
    @Column(name = "esi_level", nullable = false)
    @NotNull
    @Min(1)
    @Max(5)
    private Integer esiLevel;
    // 1 = Resuscitation, 2 = Emergent, 3 = Urgent, 4 = Less Urgent, 5 = Non-Urgent

    // ========== Vital Signs ==========
    @Column(name = "blood_pressure_systolic")
    @Min(0)
    @Max(300)
    private Integer bloodPressureSystolic;

    @Column(name = "blood_pressure_diastolic")
    @Min(0)
    @Max(200)
    private Integer bloodPressureDiastolic;

    @Column(name = "heart_rate")
    @Min(0)
    @Max(300)
    private Integer heartRate;

    @Column(name = "respiratory_rate")
    @Min(0)
    @Max(100)
    private Integer respiratoryRate;

    @Column(name = "temperature", precision = 4, scale = 1)
    @DecimalMin("30.0")
    @DecimalMax("45.0")
    private BigDecimal temperature;

    @Column(name = "oxygen_saturation")
    @Min(0)
    @Max(100)
    private Integer oxygenSaturation;

    @Column(name = "blood_glucose", precision = 5, scale = 1)
    @DecimalMin("0.0")
    @DecimalMax("999.9")
    private BigDecimal bloodGlucose;

    // ========== Neurological Assessment (GCS) ==========
    @Column(name = "gcs_eye_opening")
    @Min(1)
    @Max(4)
    private Integer gcsEyeOpening;

    @Column(name = "gcs_verbal_response")
    @Min(1)
    @Max(5)
    private Integer gcsVerbalResponse;

    @Column(name = "gcs_motor_response")
    @Min(1)
    @Max(6)
    private Integer gcsMotorResponse;

    @Column(name = "gcs_total")
    @Min(3)
    @Max(15)
    private Integer gcsTotal;

    @Column(name = "pupil_response", length = 30)
    private String pupilResponse; // EQUAL_REACTIVE, UNEQUAL, NON_REACTIVE

    @Column(name = "consciousness_level", length = 30)
    private String consciousnessLevel; // ALERT, VERBAL, PAIN, UNRESPONSIVE (AVPU)

    // ========== Pain Assessment ==========
    @Column(name = "pain_score")
    @Min(0)
    @Max(10)
    private Integer painScore;

    @Column(name = "pain_location", length = 200)
    private String painLocation;

    @Column(name = "pain_characteristics", length = 200)
    private String painCharacteristics;

    @Column(name = "pain_onset", length = 100)
    private String painOnset;

    // ========== Respiratory Assessment ==========
    @Column(name = "respiratory_distress")
    @Builder.Default
    private Boolean respiratoryDistress = false;

    @Column(name = "airway_status", length = 30)
    private String airwayStatus; // PATENT, COMPROMISED, OBSTRUCTED

    @Column(name = "breathing_pattern", length = 50)
    private String breathingPattern;

    @Column(name = "oxygen_therapy")
    @Builder.Default
    private Boolean oxygenTherapy = false;

    @Column(name = "oxygen_delivery_method", length = 50)
    private String oxygenDeliveryMethod;

    @Column(name = "oxygen_flow_rate", precision = 4, scale = 1)
    private BigDecimal oxygenFlowRate;

    // ========== Cardiovascular Assessment ==========
    @Column(name = "peripheral_pulses", length = 30)
    private String peripheralPulses; // STRONG, WEAK, ABSENT

    @Column(name = "capillary_refill_seconds", precision = 3, scale = 1)
    private BigDecimal capillaryRefillSeconds;

    @Column(name = "skin_color", length = 30)
    private String skinColor; // NORMAL, PALE, CYANOTIC, FLUSHED

    @Column(name = "skin_temperature", length = 30)
    private String skinTemperature; // WARM, COOL, COLD, HOT

    // ========== Chief Complaint & History ==========
    @Column(name = "chief_complaint", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String chiefComplaint;

    @Column(name = "history_present_illness", columnDefinition = "TEXT")
    private String historyPresentIllness;

    @Column(name = "symptom_onset")
    private LocalDateTime symptomOnset;

    @Column(name = "relevant_medical_history", columnDefinition = "TEXT")
    private String relevantMedicalHistory;

    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    // ========== Red Flags ==========
    @Column(name = "has_chest_pain")
    @Builder.Default
    private Boolean hasChestPain = false;

    @Column(name = "has_difficulty_breathing")
    @Builder.Default
    private Boolean hasDifficultyBreathing = false;

    @Column(name = "has_altered_consciousness")
    @Builder.Default
    private Boolean hasAlteredConsciousness = false;

    @Column(name = "has_severe_bleeding")
    @Builder.Default
    private Boolean hasSevereBleeding = false;

    @Column(name = "has_severe_pain")
    @Builder.Default
    private Boolean hasSeverePain = false;

    @Column(name = "has_seizures")
    @Builder.Default
    private Boolean hasSeizures = false;

    @Column(name = "has_poisoning")
    @Builder.Default
    private Boolean hasPoisoning = false;

    // ========== Resource Needs ==========
    @Column(name = "expected_resources_count")
    @Builder.Default
    private Integer expectedResourcesCount = 0;

    @Column(name = "needs_lab_work")
    @Builder.Default
    private Boolean needsLabWork = false;

    @Column(name = "needs_imaging")
    @Builder.Default
    private Boolean needsImaging = false;

    @Column(name = "needs_procedure")
    @Builder.Default
    private Boolean needsProcedure = false;

    @Column(name = "needs_specialist")
    @Builder.Default
    private Boolean needsSpecialist = false;

    // ========== Isolation ==========
    @Column(name = "requires_isolation")
    @Builder.Default
    private Boolean requiresIsolation = false;

    @Column(name = "isolation_type", length = 50)
    private String isolationType; // AIRBORNE, DROPLET, CONTACT

    @Column(name = "suspected_infection", length = 200)
    private String suspectedInfection;

    // ========== Triage Decision ==========
    @Column(name = "recommended_zone", length = 30)
    private String recommendedZone; // RED_ZONE, YELLOW_ZONE, GREEN_ZONE

    @Column(name = "triage_category", length = 30)
    private String triageCategory; // IMMEDIATE, URGENT, NON_URGENT

    @Column(name = "estimated_wait_time_minutes")
    private Integer estimatedWaitTimeMinutes;

    // ========== Notes ==========
    @Column(name = "triage_notes", columnDefinition = "TEXT")
    private String triageNotes;

    @Column(name = "nursing_interventions", columnDefinition = "TEXT")
    private String nursingInterventions;

    // ========== Re-triage ==========
    @Column(name = "is_retriage")
    @Builder.Default
    private Boolean isRetriage = false;

    @Column(name = "previous_triage_id")
    private UUID previousTriageId;

    @Column(name = "retriage_reason", columnDefinition = "TEXT")
    private String retriageReason;

    // ========== Business Methods ==========

    /**
     * Calculate total GCS score from components.
     */
    public void calculateGcsTotal() {
        if (gcsEyeOpening != null && gcsVerbalResponse != null && gcsMotorResponse != null) {
            this.gcsTotal = gcsEyeOpening + gcsVerbalResponse + gcsMotorResponse;
        }
    }

    /**
     * Determine if patient has critical red flags.
     */
    public boolean hasCriticalRedFlags() {
        return Boolean.TRUE.equals(hasChestPain) ||
               Boolean.TRUE.equals(hasDifficultyBreathing) ||
               Boolean.TRUE.equals(hasAlteredConsciousness) ||
               Boolean.TRUE.equals(hasSevereBleeding) ||
               Boolean.TRUE.equals(hasSeizures);
    }

    /**
     * Determine if vital signs are critical.
     */
    public boolean hasAbnormalVitals() {
        boolean abnormal = false;

        // Severe hypertension or hypotension
        if (bloodPressureSystolic != null) {
            abnormal = abnormal || bloodPressureSystolic < 90 || bloodPressureSystolic > 180;
        }

        // Tachycardia or bradycardia
        if (heartRate != null) {
            abnormal = abnormal || heartRate < 50 || heartRate > 120;
        }

        // Respiratory distress
        if (respiratoryRate != null) {
            abnormal = abnormal || respiratoryRate < 10 || respiratoryRate > 30;
        }

        // Hypoxia
        if (oxygenSaturation != null) {
            abnormal = abnormal || oxygenSaturation < 90;
        }

        // Fever or hypothermia
        if (temperature != null) {
            abnormal = abnormal || temperature.compareTo(BigDecimal.valueOf(35.5)) < 0 ||
                                   temperature.compareTo(BigDecimal.valueOf(38.5)) > 0;
        }

        // Low GCS
        if (gcsTotal != null) {
            abnormal = abnormal || gcsTotal < 13;
        }

        return abnormal;
    }

    /**
     * Determine recommended ESI level based on assessment.
     */
    public Integer determineESILevel() {
        // Level 1: Requires immediate life-saving intervention
        if (hasCriticalRedFlags() || (gcsTotal != null && gcsTotal < 8)) {
            return 1;
        }

        // Level 2: High risk, confused/lethargic, severe pain/distress
        if (hasAbnormalVitals() || Boolean.TRUE.equals(hasSeverePain) ||
            (gcsTotal != null && gcsTotal < 13)) {
            return 2;
        }

        // Level 3: Stable, needs multiple resources (2+)
        if (expectedResourcesCount >= 2) {
            return 3;
        }

        // Level 4: Needs one resource
        if (expectedResourcesCount == 1) {
            return 4;
        }

        // Level 5: No resources needed
        return 5;
    }
}
