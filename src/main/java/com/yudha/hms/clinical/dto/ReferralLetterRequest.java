package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Referral Letter Request DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralLetterRequest {

    // References
    private UUID encounterId;

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // Referral Type and Reason
    @NotBlank(message = "Referral type is required")
    private String referralType; // INTERNAL, EXTERNAL, BPJS_SPECIALIST, BPJS_ADVANCED, EMERGENCY, BACK_REFERRAL

    @NotBlank(message = "Referral reason is required")
    private String referralReason; // CONSULTATION, TREATMENT, INVESTIGATION, ADMISSION

    @NotBlank(message = "Urgency level is required")
    private String urgency; // ROUTINE, URGENT, EMERGENCY

    // Source Information
    @NotBlank(message = "Referring facility is required")
    private String referringFacility;

    private String referringDepartment;
    private UUID referringDoctorId;

    @NotBlank(message = "Referring doctor name is required")
    private String referringDoctorName;

    private String referringDoctorPhone;

    // Destination Information
    @NotBlank(message = "Referred to facility is required")
    private String referredToFacility;

    private String referredToDepartment;
    private String referredToDoctor;
    private String referredToSpecialty;

    // Timing
    private LocalDate referralDate;
    private LocalDate validUntil;

    // Clinical Information
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    @NotBlank(message = "Anamnesis is required")
    private String anamnesis;

    @NotBlank(message = "Physical examination is required")
    private String physicalExamination;

    @NotBlank(message = "Clinical summary is required")
    private String clinicalSummary;

    private String relevantHistory;

    // Diagnoses
    private String primaryDiagnosisCode;

    @NotBlank(message = "Primary diagnosis is required")
    private String primaryDiagnosisText;

    private String secondaryDiagnoses;

    // Treatment
    private String currentMedications;
    private String treatmentsGiven;

    // Investigation Results
    private String labResultsSummary;
    private String imagingResultsSummary;
    private String otherInvestigations;
    private String vitalSigns;

    // Reason for Referral
    @NotBlank(message = "Reason for referral is required")
    private String reasonForReferral;

    // Services Requested
    private String servicesRequested;
    private Boolean appointmentRequested;
    private Boolean admissionRequested;

    // Transport
    private Boolean transportRequired;
    private String transportMode; // AMBULANCE, PRIVATE, PUBLIC
    private String patientConditionForTransport;

    // BPJS Specific
    private Boolean isBpjsReferral;
    private String bpjsSepNumber;
    private String bpjsReferralCode;

    // Additional Notes
    private String notes;
}
