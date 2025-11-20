package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Discharge Summary Response DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DischargeSummaryResponse {

    private UUID id;
    private String dischargeNumber;
    private UUID encounterId;
    private UUID patientId;

    // Discharge Details
    private LocalDateTime dischargeDate;
    private LocalTime dischargeTime;
    private String dischargeDisposition;
    private String dischargeDispositionDisplay;
    private String dischargeCondition;
    private String dischargeConditionDisplay;

    // Clinical Summary
    private LocalDateTime admissionDate;
    private Integer lengthOfStayDays;
    private String reasonForAdmission;
    private String hospitalCourse;
    private String proceduresPerformed;

    // Diagnoses
    private String primaryDiagnosisCode;
    private String primaryDiagnosisText;
    private String secondaryDiagnoses;

    private String vitalSignsAtDischarge;

    // Medications
    private String dischargeMedications;
    private String medicationsDiscontinued;
    private String newMedications;

    // Follow-up Care
    private String followUpInstructions;
    private LocalDate followUpAppointmentDate;
    private String followUpDoctor;
    private String followUpDepartment;

    // Instructions
    private String dietInstructions;
    private String activityRestrictions;
    private String woundCareInstructions;
    private String warningSigns;
    private String emergencyContact;

    // Referrals
    private String referralTo;
    private String referralReason;

    // Provider Information
    private UUID dischargeDoctorId;
    private String dischargeDoctorName;
    private String attendingDoctorName;

    // Digital Signature
    private Boolean signed;
    private LocalDateTime signedAt;
    private UUID signedById;
    private String signedByName;

    // SATUSEHAT
    private Boolean satusehatSubmitted;
    private LocalDateTime satusehatSubmissionDate;

    // Document Generation
    private Boolean documentGenerated;
    private String documentUrl;
    private LocalDateTime documentGeneratedAt;

    // Related Data
    private List<DischargePrescriptionResponse> prescriptions;
    private List<DischargeInstructionResponse> instructions;

    // Readiness Assessment
    private DischargeReadinessResponse readinessAssessment;

    // Computed Flags
    private Boolean isReadyForDischarge;
    private Boolean hasAllDocuments;
    private Boolean needsFollowUp;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
