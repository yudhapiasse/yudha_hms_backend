package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Discharge Summary Request DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DischargeSummaryRequest {

    @NotNull(message = "Encounter ID is required")
    private UUID encounterId;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Discharge date is required")
    private LocalDateTime dischargeDate;

    @NotBlank(message = "Discharge disposition is required")
    private String dischargeDisposition; // Will be converted to enum

    @NotBlank(message = "Discharge condition is required")
    private String dischargeCondition; // Will be converted to enum

    // Clinical Summary
    private LocalDateTime admissionDate;
    private String reasonForAdmission;

    @NotBlank(message = "Hospital course is required")
    private String hospitalCourse;

    private String proceduresPerformed;

    // Diagnoses
    private String primaryDiagnosisCode;
    private String primaryDiagnosisText;
    private String secondaryDiagnoses;

    private String vitalSignsAtDischarge;

    // Medications
    @NotBlank(message = "Discharge medications are required")
    private String dischargeMedications;

    private String medicationsDiscontinued;
    private String newMedications;

    // Follow-up Care
    @NotBlank(message = "Follow-up instructions are required")
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

    // Related Data
    private List<DischargePrescriptionRequest> prescriptions;
    private List<DischargeInstructionRequest> instructions;
}
