package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Vital Signs Entity.
 *
 * Comprehensive vital signs monitoring for inpatient care.
 * Includes basic vitals, GCS, pain assessment, and fluid balance.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "vital_signs", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_vital_signs_encounter", columnList = "encounter_id"),
        @Index(name = "idx_vital_signs_patient", columnList = "patient_id"),
        @Index(name = "idx_vital_signs_time", columnList = "measurement_time"),
        @Index(name = "idx_vital_signs_abnormal", columnList = "is_abnormal"),
        @Index(name = "idx_vital_signs_notification", columnList = "requires_notification")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Vital signs monitoring for inpatient care")
public class VitalSigns extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Timing ==========
    @Column(name = "measurement_time", nullable = false)
    @NotNull(message = "Measurement time is required")
    @Builder.Default
    private LocalDateTime measurementTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "shift", length = 20)
    private Shift shift;

    @Column(name = "measurement_type", length = 30)
    @Builder.Default
    private String measurementType = "ROUTINE"; // ROUTINE, ADMISSION, PRE_OP, POST_OP, STAT

    // ========== Basic Vital Signs ==========
    @Column(name = "systolic_bp")
    private Integer systolicBp; // mmHg (normal: 90-120)

    @Column(name = "diastolic_bp")
    private Integer diastolicBp; // mmHg (normal: 60-80)

    @Column(name = "heart_rate")
    private Integer heartRate; // bpm (normal: 60-100)

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate; // breaths/min (normal: 12-20)

    @Column(name = "temperature", precision = 4, scale = 2)
    private BigDecimal temperature; // Celsius (normal: 36.5-37.5)

    @Column(name = "temperature_route", length = 20)
    private String temperatureRoute; // ORAL, AXILLARY, RECTAL, TYMPANIC

    @Column(name = "spo2")
    private Integer spo2; // % (normal: 95-100)

    @Column(name = "oxygen_therapy")
    @Builder.Default
    private Boolean oxygenTherapy = false;

    @Column(name = "oxygen_flow_rate", precision = 4, scale = 2)
    private BigDecimal oxygenFlowRate; // L/min

    @Column(name = "oxygen_delivery_method", length = 50)
    private String oxygenDeliveryMethod; // NASAL_CANNULA, FACE_MASK, VENTILATOR

    // ========== Physical Measurements ==========
    @Column(name = "weight", precision = 6, scale = 2)
    private BigDecimal weight; // kg

    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height; // cm

    @Column(name = "bmi", precision = 4, scale = 2)
    private BigDecimal bmi; // Calculated

    @Column(name = "head_circumference", precision = 4, scale = 2)
    private BigDecimal headCircumference; // cm (for pediatrics)

    // ========== Glasgow Coma Scale ==========
    @Column(name = "gcs_eye")
    private Integer gcsEye; // 1-4

    @Column(name = "gcs_verbal")
    private Integer gcsVerbal; // 1-5

    @Column(name = "gcs_motor")
    private Integer gcsMotor; // 1-6

    @Column(name = "gcs_total")
    private Integer gcsTotal; // 3-15

    // ========== Pain Assessment ==========
    @Column(name = "pain_score")
    private Integer painScore; // 0-10

    @Column(name = "pain_location", length = 200)
    private String painLocation;

    @Column(name = "pain_quality", length = 200)
    private String painQuality; // SHARP, DULL, BURNING, CRAMPING

    // ========== Fluid Balance ==========
    @Column(name = "fluid_intake_ml")
    private Integer fluidIntakeMl;

    @Column(name = "fluid_output_ml")
    private Integer fluidOutputMl;

    @Column(name = "fluid_balance_ml")
    private Integer fluidBalanceMl;

    @Column(name = "urine_output_ml")
    private Integer urineOutputMl;

    // ========== Blood Glucose ==========
    @Column(name = "blood_glucose", precision = 5, scale = 2)
    private BigDecimal bloodGlucose;

    @Column(name = "blood_glucose_unit", length = 10)
    private String bloodGlucoseUnit; // MG_DL, MMOL_L

    // ========== Additional Parameters ==========
    @Column(name = "mean_arterial_pressure")
    private Integer meanArterialPressure; // MAP

    @Column(name = "peripheral_pulse", length = 50)
    private String peripheralPulse; // STRONG, WEAK, ABSENT

    @Column(name = "capillary_refill_time", precision = 3, scale = 1)
    private BigDecimal capillaryRefillTime; // seconds

    @Column(name = "pupil_reaction", length = 50)
    private String pupilReaction; // NORMAL, SLUGGISH, NON_REACTIVE

    // ========== Alerts and Flags ==========
    @Column(name = "is_abnormal")
    @Builder.Default
    private Boolean isAbnormal = false;

    @Column(name = "abnormal_flags", columnDefinition = "TEXT")
    private String abnormalFlags; // Comma-separated list

    @Column(name = "requires_notification")
    @Builder.Default
    private Boolean requiresNotification = false;

    @Column(name = "notification_sent")
    @Builder.Default
    private Boolean notificationSent = false;

    @Column(name = "notified_provider_id")
    private UUID notifiedProviderId;

    // ========== Clinical Notes ==========
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "alerts", columnDefinition = "TEXT")
    private String alerts;

    // ========== Provider Information ==========
    @Column(name = "recorded_by_id")
    private UUID recordedById;

    @Column(name = "recorded_by_name", length = 200)
    private String recordedByName;

    @Column(name = "recorded_by_role", length = 50)
    private String recordedByRole; // NURSE, DOCTOR, NURSING_ASSISTANT

    // ========== Location ==========
    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "bed_number", length = 50)
    private String bedNumber;

    // ========== Business Methods ==========

    /**
     * Calculate BMI if weight and height are available.
     */
    public void calculateBmi() {
        if (weight != null && height != null && height.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightInMeters = height.divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal heightSquared = heightInMeters.multiply(heightInMeters);
            this.bmi = weight.divide(heightSquared, 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Calculate Mean Arterial Pressure (MAP).
     */
    public void calculateMap() {
        if (systolicBp != null && diastolicBp != null) {
            this.meanArterialPressure = diastolicBp + ((systolicBp - diastolicBp) / 3);
        }
    }

    /**
     * Calculate total GCS score.
     */
    public void calculateGcsTotal() {
        if (gcsEye != null && gcsVerbal != null && gcsMotor != null) {
            this.gcsTotal = gcsEye + gcsVerbal + gcsMotor;
        }
    }

    /**
     * Calculate fluid balance.
     */
    public void calculateFluidBalance() {
        if (fluidIntakeMl != null && fluidOutputMl != null) {
            this.fluidBalanceMl = fluidIntakeMl - fluidOutputMl;
        }
    }

    /**
     * Check if vital signs are within normal limits.
     */
    public boolean isWithinNormalLimits() {
        boolean normal = true;
        StringBuilder abnormalities = new StringBuilder();

        if (systolicBp != null && (systolicBp < 90 || systolicBp > 140)) {
            normal = false;
            abnormalities.append("BP,");
        }

        if (heartRate != null && (heartRate < 60 || heartRate > 100)) {
            normal = false;
            abnormalities.append("HR,");
        }

        if (respiratoryRate != null && (respiratoryRate < 12 || respiratoryRate > 20)) {
            normal = false;
            abnormalities.append("RR,");
        }

        if (temperature != null && (temperature.compareTo(new BigDecimal("36.0")) < 0 ||
            temperature.compareTo(new BigDecimal("38.0")) > 0)) {
            normal = false;
            abnormalities.append("TEMP,");
        }

        if (spo2 != null && spo2 < 95) {
            normal = false;
            abnormalities.append("SPO2,");
        }

        if (gcsTotal != null && gcsTotal < 15) {
            normal = false;
            abnormalities.append("GCS,");
        }

        this.isAbnormal = !normal;
        if (!normal) {
            this.abnormalFlags = abnormalities.toString();
        }

        return normal;
    }

    /**
     * Check if requires urgent notification.
     */
    public boolean requiresUrgentNotification() {
        if (systolicBp != null && (systolicBp < 80 || systolicBp > 180)) return true;
        if (heartRate != null && (heartRate < 40 || heartRate > 140)) return true;
        if (respiratoryRate != null && (respiratoryRate < 8 || respiratoryRate > 30)) return true;
        if (spo2 != null && spo2 < 90) return true;
        if (gcsTotal != null && gcsTotal < 8) return true;
        return false;
    }

    /**
     * Get GCS severity assessment.
     */
    public String getGcsSeverity() {
        if (gcsTotal == null) return "NOT_ASSESSED";
        if (gcsTotal <= 8) return "SEVERE";
        if (gcsTotal <= 12) return "MODERATE";
        return "MILD";
    }
}
