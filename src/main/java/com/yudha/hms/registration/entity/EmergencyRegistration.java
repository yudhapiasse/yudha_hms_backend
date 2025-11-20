package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Emergency Department (IGD) Registration Entity.
 *
 * Comprehensive emergency patient registration supporting:
 * - Fast-track registration for unknown/unconscious patients
 * - Triage level assignment and prioritization
 * - Police case tracking
 * - Ambulance arrival details
 * - Auto-conversion to inpatient admission
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "emergency_registration", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_emergency_number", columnList = "emergency_number"),
        @Index(name = "idx_emergency_patient", columnList = "patient_id"),
        @Index(name = "idx_emergency_status", columnList = "status"),
        @Index(name = "idx_emergency_triage_level", columnList = "triage_level"),
        @Index(name = "idx_emergency_priority", columnList = "triage_priority, is_critical")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Emergency department patient registrations")
public class EmergencyRegistration extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Registration Number ==========
    @Column(name = "emergency_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Emergency number is required")
    private String emergencyNumber; // ER-20250119-0001

    // ========== Patient Information ==========
    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "is_unknown_patient", nullable = false)
    @Builder.Default
    private Boolean isUnknownPatient = false;

    @Column(name = "unknown_patient_identifier", length = 100)
    private String unknownPatientIdentifier; // UNKNOWN-20250119-001

    @Column(name = "temporary_name", length = 200)
    private String temporaryName; // "Unknown Male #1"

    @Column(name = "estimated_age")
    @Min(0)
    @Max(150)
    private Integer estimatedAge;

    @Column(name = "estimated_gender", length = 10)
    private String estimatedGender; // MALE, FEMALE, UNKNOWN

    // ========== Registration Details ==========
    @Column(name = "registration_date", nullable = false)
    @NotNull
    @Builder.Default
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "registration_time", nullable = false)
    @NotNull
    @Builder.Default
    private LocalDateTime registrationTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "arrival_mode", nullable = false, length = 30)
    @NotNull
    private ArrivalMode arrivalMode;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    // ========== Ambulance Details ==========
    @Column(name = "ambulance_type", length = 30)
    private String ambulanceType; // GOVERNMENT, PRIVATE, HOSPITAL

    @Column(name = "ambulance_number", length = 50)
    private String ambulanceNumber;

    @Column(name = "ambulance_origin", length = 200)
    private String ambulanceOrigin;

    @Column(name = "paramedic_name", length = 200)
    private String paramedicName;

    @Column(name = "paramedic_phone", length = 20)
    private String paramedicPhone;

    // ========== Chief Complaint ==========
    @Column(name = "chief_complaint", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    @Column(name = "presenting_problem", columnDefinition = "TEXT")
    private String presentingProblem;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "onset_time")
    private LocalDateTime onsetTime;

    @Column(name = "duration_minutes")
    @Min(0)
    private Integer durationMinutes;

    // ========== Triage Information ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "triage_level", nullable = false, length = 20)
    @NotNull
    private TriageLevel triageLevel;

    @Column(name = "triage_priority", nullable = false)
    @NotNull
    @Min(1)
    @Max(5)
    @Builder.Default
    private Integer triagePriority = 3;

    @Column(name = "triage_time")
    private LocalDateTime triageTime;

    @Column(name = "triaged_by_id")
    private UUID triaged_byId;

    @Column(name = "triaged_by_name", length = 200)
    private String triaged_byName;

    // ========== Initial Vital Signs ==========
    @Column(name = "initial_blood_pressure_systolic")
    @Min(0)
    @Max(300)
    private Integer initialBloodPressureSystolic;

    @Column(name = "initial_blood_pressure_diastolic")
    @Min(0)
    @Max(200)
    private Integer initialBloodPressureDiastolic;

    @Column(name = "initial_heart_rate")
    @Min(0)
    @Max(300)
    private Integer initialHeartRate;

    @Column(name = "initial_respiratory_rate")
    @Min(0)
    @Max(100)
    private Integer initialRespiratoryRate;

    @Column(name = "initial_temperature", precision = 4, scale = 1)
    @DecimalMin("30.0")
    @DecimalMax("45.0")
    private BigDecimal initialTemperature;

    @Column(name = "initial_oxygen_saturation")
    @Min(0)
    @Max(100)
    private Integer initialOxygenSaturation;

    @Column(name = "initial_gcs_score")
    @Min(3)
    @Max(15)
    private Integer initialGcsScore; // Glasgow Coma Scale

    @Column(name = "initial_pain_score")
    @Min(0)
    @Max(10)
    private Integer initialPainScore;

    // ========== Police Case ==========
    @Column(name = "is_police_case", nullable = false)
    @Builder.Default
    private Boolean isPoliceCase = false;

    @Column(name = "police_case_type", length = 30)
    private String policeCaseType; // ACCIDENT, VIOLENCE, ASSAULT, etc.

    @Column(name = "police_report_number", length = 100)
    private String policeReportNumber;

    @Column(name = "police_station", length = 200)
    private String policeStation;

    @Column(name = "police_officer_name", length = 200)
    private String policeOfficerName;

    @Column(name = "police_officer_contact", length = 20)
    private String policeOfficerContact;

    // ========== Trauma Details ==========
    @Column(name = "is_trauma_case", nullable = false)
    @Builder.Default
    private Boolean isTraumaCase = false;

    @Column(name = "trauma_type", length = 50)
    private String traumaType;

    @Column(name = "accident_location", columnDefinition = "TEXT")
    private String accidentLocation;

    @Column(name = "accident_time")
    private LocalDateTime accidentTime;

    @Column(name = "mechanism_of_injury", columnDefinition = "TEXT")
    private String mechanismOfInjury;

    // ========== Medical Team ==========
    @Column(name = "attending_doctor_id")
    private UUID attendingDoctorId;

    @Column(name = "attending_doctor_name", length = 200)
    private String attendingDoctorName;

    @Column(name = "assigned_nurse_id")
    private UUID assignedNurseId;

    @Column(name = "assigned_nurse_name", length = 200)
    private String assignedNurseName;

    // ========== ER Placement ==========
    @Column(name = "er_zone", length = 30)
    private String erZone; // RED_ZONE, YELLOW_ZONE, GREEN_ZONE, RESUS_ROOM

    @Column(name = "er_bed_number", length = 20)
    private String erBedNumber;

    // ========== Status & Disposition ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull
    @Builder.Default
    private EmergencyStatus status = EmergencyStatus.REGISTERED;

    @Column(name = "disposition", length = 30)
    private String disposition; // ADMITTED_INPATIENT, DISCHARGED_HOME, etc.

    @Column(name = "disposition_time")
    private LocalDateTime dispositionTime;

    @Column(name = "disposition_notes", columnDefinition = "TEXT")
    private String dispositionNotes;

    // ========== Inpatient Conversion ==========
    @Column(name = "converted_to_inpatient", nullable = false)
    @Builder.Default
    private Boolean convertedToInpatient = false;

    @Column(name = "inpatient_admission_id")
    private UUID inpatientAdmissionId;

    @Column(name = "conversion_time")
    private LocalDateTime conversionTime;

    // ========== Encounter Integration ==========
    @Column(name = "encounter_id")
    private UUID encounterId;

    // ========== Enhanced Timing Metrics ==========
    @Column(name = "arrival_acknowledged_at")
    private LocalDateTime arrivalAcknowledgedAt;

    @Column(name = "arrival_acknowledged_by", length = 200)
    private String arrivalAcknowledgedBy;

    @Column(name = "treatment_start_time")
    private LocalDateTime treatmentStartTime;

    @Column(name = "treatment_started_by", length = 200)
    private String treatmentStartedBy;

    // ========== Timing Metrics ==========
    @Column(name = "door_to_triage_minutes")
    private Integer doorToTriageMinutes;

    @Column(name = "door_to_doctor_minutes")
    private Integer doorToDoctorMinutes;

    @Column(name = "total_er_time_minutes")
    private Integer totalErTimeMinutes;

    // ========== Priority Flags ==========
    @Column(name = "is_critical", nullable = false)
    @Builder.Default
    private Boolean isCritical = false;

    @Column(name = "requires_isolation", nullable = false)
    @Builder.Default
    private Boolean requiresIsolation = false;

    @Column(name = "isolation_reason", length = 100)
    private String isolationReason;

    @Column(name = "is_infectious", nullable = false)
    @Builder.Default
    private Boolean isInfectious = false;

    @Column(name = "infectious_disease", length = 100)
    private String infectiousDisease;

    // ========== Companion Information ==========
    @Column(name = "companion_name", length = 200)
    private String companionName;

    @Column(name = "companion_relationship", length = 100)
    private String companionRelationship;

    @Column(name = "companion_phone", length = 20)
    private String companionPhone;

    @Column(name = "companion_address", columnDefinition = "TEXT")
    private String companionAddress;

    // ========== Referral Information ==========
    @Column(name = "referred_from", length = 200)
    private String referredFrom;

    @Column(name = "referral_doctor", length = 200)
    private String referralDoctor;

    @Column(name = "referral_diagnosis", columnDefinition = "TEXT")
    private String referralDiagnosis;

    @Column(name = "referral_letter_number", length = 100)
    private String referralLetterNumber;

    // ========== Payment ==========
    @Column(name = "payment_method", length = 30)
    private String paymentMethod; // BPJS, CASH, INSURANCE

    @Column(name = "insurance_name", length = 200)
    private String insuranceName;

    @Column(name = "insurance_number", length = 100)
    private String insuranceNumber;

    @Column(name = "guarantee_letter_number", length = 100)
    private String guaranteeLetterNumber;

    // ========== Notes ==========
    @Column(name = "medical_history_summary", columnDefinition = "TEXT")
    private String medicalHistorySummary;

    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "special_needs", columnDefinition = "TEXT")
    private String specialNeeds;

    @Column(name = "registration_notes", columnDefinition = "TEXT")
    private String registrationNotes;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    // ========== Soft Delete Fields ==========
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    // ========== Relationships ==========
    @OneToMany(mappedBy = "emergencyRegistration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TriageAssessment> triageAssessments = new ArrayList<>();

    // ========== Business Methods ==========

    /**
     * Acknowledge patient arrival.
     * Transitions status from REGISTERED to ARRIVED.
     */
    public void acknowledgeArrival(String acknowledgedBy) {
        if (this.status != EmergencyStatus.REGISTERED) {
            throw new IllegalStateException("Cannot acknowledge arrival when status is: " + this.status);
        }
        this.arrivalAcknowledgedAt = LocalDateTime.now();
        this.arrivalAcknowledgedBy = acknowledgedBy;
        this.status = EmergencyStatus.ARRIVED;
    }

    /**
     * Start emergency treatment.
     * Transitions status to IN_TREATMENT.
     */
    public void startTreatment(String startedBy) {
        if (this.status != EmergencyStatus.TRIAGED && this.status != EmergencyStatus.ARRIVED) {
            throw new IllegalStateException("Cannot start treatment when status is: " + this.status);
        }
        this.treatmentStartTime = LocalDateTime.now();
        this.treatmentStartedBy = startedBy;
        this.status = EmergencyStatus.IN_TREATMENT;

        // Calculate door-to-doctor time
        if (arrivalTime != null) {
            Duration duration = Duration.between(arrivalTime, treatmentStartTime);
            this.doorToDoctorMinutes = (int) duration.toMinutes();
        }
    }

    /**
     * Mark patient as triaged with level and priority.
     */
    public void performTriage(TriageLevel level, Integer priority, UUID nurseId, String nurseName) {
        this.triageLevel = level;
        this.triagePriority = priority;
        this.triageTime = LocalDateTime.now();
        this.triaged_byId = nurseId;
        this.triaged_byName = nurseName;
        this.status = EmergencyStatus.TRIAGED;
        this.isCritical = level.isCritical();
    }

    /**
     * Convert to inpatient admission.
     */
    public void convertToInpatient(UUID admissionId) {
        this.convertedToInpatient = true;
        this.inpatientAdmissionId = admissionId;
        this.conversionTime = LocalDateTime.now();
        this.status = EmergencyStatus.ADMITTED;
        this.disposition = "ADMITTED_INPATIENT";
        this.dispositionTime = LocalDateTime.now();
    }

    /**
     * Discharge patient from ER.
     */
    public void discharge(String dispositionType, String notes) {
        this.status = EmergencyStatus.DISCHARGED;
        this.disposition = dispositionType;
        this.dispositionTime = LocalDateTime.now();
        this.dispositionNotes = notes;
        calculateTotalErTime();
    }

    /**
     * Calculate door-to-triage time.
     */
    public void calculateDoorToTriageTime() {
        if (arrivalTime != null && triageTime != null) {
            Duration duration = Duration.between(arrivalTime, triageTime);
            this.doorToTriageMinutes = (int) duration.toMinutes();
        }
    }

    /**
     * Calculate total ER time.
     */
    public void calculateTotalErTime() {
        if (arrivalTime != null && dispositionTime != null) {
            Duration duration = Duration.between(arrivalTime, dispositionTime);
            this.totalErTimeMinutes = (int) duration.toMinutes();
        }
    }

    /**
     * Check if this is a critical case.
     */
    public boolean isCriticalCase() {
        return Boolean.TRUE.equals(isCritical) || triageLevel == TriageLevel.RED;
    }

    /**
     * Check if patient identity is known.
     */
    public boolean isPatientIdentified() {
        return !Boolean.TRUE.equals(isUnknownPatient) && patientId != null;
    }

    /**
     * Get display name for patient.
     */
    public String getPatientDisplayName() {
        if (isPatientIdentified()) {
            return "Patient ID: " + patientId;
        }
        return temporaryName != null ? temporaryName : unknownPatientIdentifier;
    }
}
