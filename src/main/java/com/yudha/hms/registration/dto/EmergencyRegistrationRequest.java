package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.ArrivalMode;
import com.yudha.hms.registration.entity.TriageLevel;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for emergency department registration request.
 * Supports fast-track registration for unknown/unconscious patients.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyRegistrationRequest {

    // ========== Patient Information ==========
    private UUID patientId; // Optional - null for unknown patients

    @Builder.Default
    private Boolean isUnknownPatient = false;

    private String temporaryName; // "Unknown Male #1", "Korban Kecelakaan #2"

    @Min(0)
    @Max(150)
    private Integer estimatedAge;

    private String estimatedGender; // MALE, FEMALE, UNKNOWN

    // ========== Registration Details ==========
    @NotNull(message = "Arrival mode is required")
    private ArrivalMode arrivalMode;

    private LocalDateTime arrivalTime;

    // ========== Ambulance Details (if arrival mode = AMBULANCE) ==========
    private String ambulanceType; // GOVERNMENT, PRIVATE, HOSPITAL
    private String ambulanceNumber;
    private String ambulanceOrigin;
    private String paramedicName;
    private String paramedicPhone;

    // ========== Chief Complaint (Required) ==========
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    private String presentingProblem;
    private String symptoms;
    private LocalDateTime onsetTime;
    private Integer durationMinutes;

    // ========== Initial Triage (can be done during registration or separately) ==========
    private TriageLevel triageLevel;
    private Integer triagePriority;

    // ========== Initial Vital Signs (Optional during registration) ==========
    private Integer initialBloodPressureSystolic;
    private Integer initialBloodPressureDiastolic;
    private Integer initialHeartRate;
    private Integer initialRespiratoryRate;
    private BigDecimal initialTemperature;
    private Integer initialOxygenSaturation;
    private Integer initialGcsScore;
    private Integer initialPainScore;

    // ========== Police Case (Optional) ==========
    @Builder.Default
    private Boolean isPoliceCase = false;

    private String policeCaseType; // ACCIDENT, VIOLENCE, ASSAULT, SUSPICIOUS_DEATH
    private String policeReportNumber;
    private String policeStation;
    private String policeOfficerName;
    private String policeOfficerContact;

    // ========== Trauma/Accident Details (Optional) ==========
    @Builder.Default
    private Boolean isTraumaCase = false;

    private String traumaType; // MOTOR_VEHICLE, FALL, BURN, PENETRATING, BLUNT
    private String accidentLocation;
    private LocalDateTime accidentTime;
    private String mechanismOfInjury;

    // ========== Companion/Guardian Information ==========
    private String companionName;
    private String companionRelationship;
    private String companionPhone;
    private String companionAddress;

    // ========== Referral Information (if arrival mode = REFERRAL) ==========
    private String referredFrom;
    private String referralDoctor;
    private String referralDiagnosis;
    private String referralLetterNumber;

    // ========== Payment Information ==========
    private String paymentMethod; // BPJS, CASH, INSURANCE, COMPANY, FREE
    private String insuranceName;
    private String insuranceNumber;
    private String guaranteeLetterNumber;

    // ========== Medical History (Brief) ==========
    private String medicalHistorySummary;
    private String currentMedications;
private String allergies;
    private String specialNeeds;

    // ========== Notes ==========
    private String registrationNotes;

    // ========== Priority Flags ==========
    @Builder.Default
    private Boolean isCritical = false;

    @Builder.Default
    private Boolean requiresIsolation = false;

    private String isolationReason;

    @Builder.Default
    private Boolean isInfectious = false;

    private String infectiousDisease;
}
