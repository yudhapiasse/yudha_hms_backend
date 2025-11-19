package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.ArrivalMode;
import com.yudha.hms.registration.entity.EmergencyStatus;
import com.yudha.hms.registration.entity.TriageLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for emergency registration response.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyRegistrationResponse {

    private UUID id;
    private String emergencyNumber;

    // Patient Information
    private UUID patientId;
    private String patientName;
    private String patientMrn;
    private Boolean isUnknownPatient;
    private String unknownPatientIdentifier;
    private String temporaryName;
    private Integer estimatedAge;
    private String estimatedGender;

    // Registration Details
    private LocalDateTime registrationDate;
    private LocalDateTime registrationTime;
    private ArrivalMode arrivalMode;
    private LocalDateTime arrivalTime;

    // Ambulance Details
    private String ambulanceType;
    private String ambulanceNumber;
    private String ambulanceOrigin;
    private String paramedicName;

    // Chief Complaint
    private String chiefComplaint;
    private String presentingProblem;
    private String symptoms;

    // Triage Information
    private TriageLevel triageLevel;
    private String triageLevelColor; // For UI display
    private Integer triagePriority;
    private LocalDateTime triageTime;
    private String triagedByName;

    // Vital Signs
    private String vitalSigns; // Formatted string: "BP: 120/80, HR: 75, RR: 18, Temp: 36.5°C, SpO2: 98%"
    private Integer initialGcsScore;
    private Integer initialPainScore;

    // Police Case
    private Boolean isPoliceCase;
    private String policeCaseType;
    private String policeReportNumber;
    private String policeOfficerName;

    // Trauma Details
    private Boolean isTraumaCase;
    private String traumaType;
    private String accidentLocation;

    // Medical Team
    private String attendingDoctorName;
    private String assignedNurseName;

    // ER Placement
    private String erZone;
    private String erBedNumber;
    private String erLocation; // Formatted: "RED ZONE - Bed 3"

    // Status
    private EmergencyStatus status;
    private String statusDisplay;
    private String disposition;
    private LocalDateTime dispositionTime;

    // Inpatient Conversion
    private Boolean convertedToInpatient;
    private UUID inpatientAdmissionId;
    private String inpatientAdmissionNumber;
    private LocalDateTime conversionTime;

    // Timing Metrics
    private Integer doorToTriageMinutes;
    private Integer doorToDoctorMinutes;
    private Integer totalErTimeMinutes;
    private String waitTimeDisplay; // "25 minutes in ER"

    // Priority Flags
    private Boolean isCritical;
    private Boolean requiresIsolation;
    private String isolationReason;

    // Companion
    private String companionName;
    private String companionPhone;

    // Payment
    private String paymentMethod;
    private String insuranceName;

    // Triage Assessments
    private List<TriageAssessmentSummary> triageAssessments;

    // Latest triage assessment summary
    private TriageAssessmentSummary latestTriage;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // ========== Nested DTO for Triage Summary ==========
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TriageAssessmentSummary {
        private UUID id;
        private LocalDateTime triageTime;
        private String triagedByName;
        private Integer esiLevel;
        private String esiLevelDescription; // "Level 2 - Emergent"
        private Integer gcsTotal;
        private Integer painScore;
        private Boolean hasRedFlags;
        private String triageNotes;
        private Boolean isRetriage;
    }
}
