package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Procedure Monitoring Entity.
 * Tracks post-procedure patient monitoring and recovery.
 *
 * @author HMS Development Team
 * @since 2025-01-20
 */
@Entity
@Table(name = "procedure_monitoring", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_proc_monitor_procedure", columnList = "encounter_procedure_id"),
        @Index(name = "idx_proc_monitor_patient", columnList = "patient_id"),
        @Index(name = "idx_proc_monitor_time", columnList = "monitoring_time"),
        @Index(name = "idx_proc_monitor_interval", columnList = "monitoring_interval")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Post-procedure patient monitoring and recovery tracking")
public class ProcedureMonitoring extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_procedure_id", nullable = false)
    @NotNull
    private EncounterProcedure encounterProcedure;

    @Column(name = "patient_id", nullable = false)
    @NotNull
    private UUID patientId;

    @Column(name = "monitoring_time", nullable = false)
    @NotNull
    private LocalDateTime monitoringTime;

    @Column(name = "monitoring_interval", length = 20)
    private String monitoringInterval; // POST_OP_15MIN, POST_OP_30MIN, POST_OP_1HR, POST_OP_2HR, etc.

    @Column(name = "minutes_post_procedure")
    private Integer minutesPostProcedure;

    // Vital Signs
    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "oxygen_saturation")
    private Integer oxygenSaturation;

    @Column(name = "pain_score")
    private Integer painScore; // 0-10 scale

    // Consciousness and Neurological
    @Column(name = "consciousness_level", length = 50)
    private String consciousnessLevel; // ALERT, DROWSY, CONFUSED, UNRESPONSIVE

    @Column(name = "glasgow_coma_score")
    private Integer glasgowComaScore;

    @Column(name = "pupils_equal")
    @Builder.Default
    private Boolean pupilsEqual = true;

    @Column(name = "pupils_reactive")
    @Builder.Default
    private Boolean pupilsReactive = true;

    // Respiratory
    @Column(name = "airway_patent")
    @Builder.Default
    private Boolean airwayPatent = true;

    @Column(name = "breathing_adequacy", length = 50)
    private String breathingAdequacy; // ADEQUATE, SHALLOW, LABORED

    @Column(name = "oxygen_support", length = 50)
    private String oxygenSupport; // ROOM_AIR, NASAL_CANNULA, MASK

    @Column(name = "oxygen_flow_rate")
    private Integer oxygenFlowRate; // L/min

    // Circulation
    @Column(name = "peripheral_pulses", length = 100)
    private String peripheralPulses; // STRONG, WEAK, ABSENT

    @Column(name = "capillary_refill", length = 50)
    private String capillaryRefill; // <2SEC, >2SEC

    @Column(name = "skin_color", length = 50)
    private String skinColor; // PINK, PALE, CYANOTIC

    @Column(name = "skin_temperature", length = 50)
    private String skinTemperature; // WARM, COOL, COLD

    // Wound/Surgical Site
    @Column(name = "dressing_intact")
    @Builder.Default
    private Boolean dressingIntact = true;

    @Column(name = "bleeding_status", length = 50)
    private String bleedingStatus; // NONE, MINIMAL, MODERATE, SEVERE

    @Column(name = "drainage_amount", length = 50)
    private String drainageAmount; // NONE, MINIMAL, MODERATE, LARGE

    @Column(name = "drainage_type", length = 50)
    private String drainageType; // SEROUS, SANGUINEOUS, SEROSANGUINEOUS

    @Column(name = "swelling", length = 50)
    private String swelling; // NONE, MILD, MODERATE, SEVERE

    @Column(name = "signs_of_infection")
    @Builder.Default
    private Boolean signsOfInfection = false;

    // Gastrointestinal
    @Column(name = "nausea_present")
    @Builder.Default
    private Boolean nauseaPresent = false;

    @Column(name = "vomiting_present")
    @Builder.Default
    private Boolean vomitingPresent = false;

    @Column(name = "bowel_sounds", length = 50)
    private String bowelSounds; // PRESENT, ABSENT, HYPERACTIVE

    @Column(name = "npo_status", length = 50)
    private String npoStatus; // NPO, CLEAR_LIQUIDS, FULL_DIET

    // Genitourinary
    @Column(name = "urine_output_ml")
    private Integer urineOutputMl;

    @Column(name = "voided_spontaneously")
    @Builder.Default
    private Boolean voidedSpontaneously = false;

    @Column(name = "catheter_in_place")
    @Builder.Default
    private Boolean catheterInPlace = false;

    // Mobility
    @Column(name = "mobility_status", length = 50)
    private String mobilityStatus; // BED_REST, CHAIR, AMBULATING

    @Column(name = "movement_of_extremities", length = 100)
    private String movementOfExtremities;

    // Medications
    @Column(name = "pain_medication_given")
    @Builder.Default
    private Boolean painMedicationGiven = false;

    @Column(name = "antiemetic_given")
    @Builder.Default
    private Boolean antiemeticGiven = false;

    @Column(name = "medications_administered", columnDefinition = "TEXT")
    private String medicationsAdministered; // JSON array

    // Overall Assessment
    @Column(name = "recovery_status", length = 50)
    @Builder.Default
    private String recoveryStatus = "RECOVERING"; // RECOVERING, STABLE, CONCERNS, CRITICAL

    @Column(name = "complications_noted")
    @Builder.Default
    private Boolean complicationsNoted = false;

    @Column(name = "complications_description", columnDefinition = "TEXT")
    private String complicationsDescription;

    @Column(name = "interventions_required", columnDefinition = "TEXT")
    private String interventionsRequired;

    // Discharge Readiness
    @Column(name = "ready_for_discharge")
    @Builder.Default
    private Boolean readyForDischarge = false;

    @Column(name = "discharge_criteria_met")
    @Builder.Default
    private Boolean dischargeCriteriaMet = false;

    // Staff
    @Column(name = "monitored_by_id")
    private UUID monitoredById;

    @Column(name = "monitored_by_name", length = 200)
    private String monitoredByName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Business Methods
    public boolean hasAbnormalVitals() {
        return (systolicBp != null && (systolicBp < 90 || systolicBp > 180)) ||
               (heartRate != null && (heartRate < 50 || heartRate > 120)) ||
               (oxygenSaturation != null && oxygenSaturation < 90) ||
               (respiratoryRate != null && (respiratoryRate < 10 || respiratoryRate > 30));
    }

    public boolean requiresIntervention() {
        return hasAbnormalVitals() || complicationsNoted || !airwayPatent ||
               (painScore != null && painScore >= 7);
    }
}
