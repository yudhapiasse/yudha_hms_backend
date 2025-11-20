package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Referral Letter Response DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralLetterResponse {

    private UUID id;
    private String referralNumber;

    // References
    private UUID encounterId;
    private UUID patientId;

    // Referral Type and Status
    private String referralType;
    private String referralTypeDisplay;
    private String referralStatus;
    private String referralStatusDisplay;
    private String referralReason;
    private String urgency;
    private String urgencyDisplay;

    // Source Information
    private String referringFacility;
    private String referringDepartment;
    private UUID referringDoctorId;
    private String referringDoctorName;
    private String referringDoctorPhone;

    // Destination Information
    private String referredToFacility;
    private String referredToDepartment;
    private String referredToDoctor;
    private String referredToSpecialty;

    // Timing
    private LocalDate referralDate;
    private LocalDateTime referralCreatedAt;
    private LocalDate validUntil;
    private Boolean isExpired;

    // Clinical Information
    private String chiefComplaint;
    private String anamnesis;
    private String physicalExamination;
    private String clinicalSummary;
    private String relevantHistory;

    // Diagnoses
    private String primaryDiagnosisCode;
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
    private String reasonForReferral;

    // Services Requested
    private String servicesRequested;
    private Boolean appointmentRequested;
    private Boolean admissionRequested;

    // Transport
    private Boolean transportRequired;
    private String transportMode;
    private String patientConditionForTransport;

    // Response Information
    private Boolean referralAccepted;
    private LocalDateTime acceptanceDate;
    private String acceptedBy;
    private LocalDateTime appointmentDate;
    private String rejectionReason;

    // BPJS Specific
    private Boolean isBpjsReferral;
    private String bpjsSepNumber;
    private String bpjsReferralCode;

    // Digital Signature
    private Boolean signed;
    private LocalDateTime signedAt;
    private UUID signedById;
    private String signedByName;

    // Document Generation
    private Boolean documentGenerated;
    private String documentUrl;
    private LocalDateTime documentGeneratedAt;

    // QR Code
    private String qrCode;
    private String qrCodeUrl;

    // Integration Tracking
    private Boolean bpjsVclaimSubmitted;
    private LocalDateTime bpjsVclaimSubmissionDate;
    private String bpjsVclaimReferenceNumber;

    private Boolean pcareSubmitted;
    private LocalDateTime pcareSubmissionDate;
    private String pcareReferenceNumber;

    private Boolean satusehatSubmitted;
    private LocalDateTime satusehatSubmissionDate;
    private String satusehatServiceRequestId;

    // Additional Notes
    private String notes;

    // Computed Flags
    private Boolean isPending;
    private Boolean requiresVClaimIntegration;
    private Boolean canBeCancelled;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
