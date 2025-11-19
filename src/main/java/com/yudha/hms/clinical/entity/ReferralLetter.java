package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Referral Letter Entity (Surat Rujukan).
 *
 * Manages referral letters to other facilities or specialists.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "referral_letter", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_referral_number", columnList = "referral_number"),
        @Index(name = "idx_referral_encounter", columnList = "encounter_id"),
        @Index(name = "idx_referral_patient", columnList = "patient_id"),
        @Index(name = "idx_referral_status", columnList = "referral_status"),
        @Index(name = "idx_referral_date", columnList = "referral_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Surat Rujukan - Referral letters")
public class ReferralLetter extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "referral_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Referral number is required")
    private String referralNumber; // REF-20250119-0001

    // ========== References ==========
    @Column(name = "encounter_id")
    private UUID encounterId;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Referral Type ==========
    @Column(name = "referral_type", nullable = false, length = 30)
    @NotBlank(message = "Referral type is required")
    private String referralType; // OUTPATIENT, INPATIENT, EMERGENCY, DIAGNOSTIC

    @Column(name = "referral_reason", nullable = false, length = 50)
    @NotBlank(message = "Referral reason is required")
    private String referralReason; // CONSULTATION, TREATMENT, INVESTIGATION, ADMISSION

    // ========== Source Information ==========
    @Column(name = "referring_facility", nullable = false, length = 200)
    @NotBlank(message = "Referring facility is required")
    private String referringFacility;

    @Column(name = "referring_department", length = 100)
    private String referringDepartment;

    @Column(name = "referring_doctor_id")
    private UUID referringDoctorId;

    @Column(name = "referring_doctor_name", nullable = false, length = 200)
    @NotBlank(message = "Referring doctor name is required")
    private String referringDoctorName;

    @Column(name = "referring_doctor_phone", length = 20)
    private String referringDoctorPhone;

    // ========== Destination Information ==========
    @Column(name = "referred_to_facility", nullable = false, length = 200)
    @NotBlank(message = "Referred to facility is required")
    private String referredToFacility;

    @Column(name = "referred_to_department", length = 100)
    private String referredToDepartment;

    @Column(name = "referred_to_doctor", length = 200)
    private String referredToDoctor;

    @Column(name = "referred_to_specialty", length = 100)
    private String referredToSpecialty;

    // ========== Timing ==========
    @Column(name = "referral_date", nullable = false)
    @NotNull(message = "Referral date is required")
    @Builder.Default
    private LocalDate referralDate = LocalDate.now();

    @Column(name = "referral_created_at", nullable = false)
    @NotNull(message = "Referral creation time is required")
    @Builder.Default
    private LocalDateTime referralCreatedAt = LocalDateTime.now();

    @Column(name = "valid_until")
    private LocalDate validUntil;

    // ========== Clinical Information ==========
    @Column(name = "chief_complaint", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    @Column(name = "clinical_summary", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Clinical summary is required")
    private String clinicalSummary;

    @Column(name = "relevant_history", columnDefinition = "TEXT")
    private String relevantHistory;

    // ========== Diagnoses ==========
    @Column(name = "primary_diagnosis_code", length = 10)
    private String primaryDiagnosisCode;

    @Column(name = "primary_diagnosis_text", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Primary diagnosis is required")
    private String primaryDiagnosisText;

    @Column(name = "secondary_diagnoses", columnDefinition = "TEXT")
    private String secondaryDiagnoses;

    // ========== Treatment ==========
    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(name = "treatments_given", columnDefinition = "TEXT")
    private String treatmentsGiven;

    // ========== Investigation Results ==========
    @Column(name = "lab_results_summary", columnDefinition = "TEXT")
    private String labResultsSummary;

    @Column(name = "imaging_results_summary", columnDefinition = "TEXT")
    private String imagingResultsSummary;

    @Column(name = "other_investigations", columnDefinition = "TEXT")
    private String otherInvestigations;

    @Column(name = "vital_signs", columnDefinition = "TEXT")
    private String vitalSigns;

    // ========== Reason for Referral ==========
    @Column(name = "reason_for_referral", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Reason for referral is required")
    private String reasonForReferral;

    @Column(name = "urgency_level", length = 20)
    private String urgencyLevel; // ROUTINE, URGENT, EMERGENCY

    // ========== Services Requested ==========
    @Column(name = "services_requested", columnDefinition = "TEXT")
    private String servicesRequested;

    @Column(name = "appointment_requested")
    @Builder.Default
    private Boolean appointmentRequested = false;

    @Column(name = "admission_requested")
    @Builder.Default
    private Boolean admissionRequested = false;

    // ========== Transport ==========
    @Column(name = "transport_required")
    @Builder.Default
    private Boolean transportRequired = false;

    @Column(name = "transport_mode", length = 50)
    private String transportMode; // AMBULANCE, PRIVATE, PUBLIC

    @Column(name = "patient_condition_for_transport", length = 50)
    private String patientConditionForTransport;

    // ========== Response ==========
    @Column(name = "referral_accepted")
    private Boolean referralAccepted;

    @Column(name = "acceptance_date")
    private LocalDateTime acceptanceDate;

    @Column(name = "accepted_by", length = 200)
    private String acceptedBy;

    @Column(name = "appointment_date")
    private LocalDateTime appointmentDate;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // ========== BPJS Specific ==========
    @Column(name = "is_bpjs_referral")
    @Builder.Default
    private Boolean isBpjsReferral = false;

    @Column(name = "bpjs_sep_number", length = 50)
    private String bpjsSepNumber;

    @Column(name = "bpjs_referral_code", length = 50)
    private String bpjsReferralCode;

    // ========== Digital Signature ==========
    @Column(name = "signed")
    @Builder.Default
    private Boolean signed = false;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "signed_by_id")
    private UUID signedById;

    @Column(name = "signed_by_name", length = 200)
    private String signedByName;

    @Column(name = "digital_signature", columnDefinition = "TEXT")
    private String digitalSignature;

    // ========== Document Generation ==========
    @Column(name = "document_generated")
    @Builder.Default
    private Boolean documentGenerated = false;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "document_pdf_base64", columnDefinition = "TEXT")
    private String documentPdfBase64;

    @Column(name = "document_generated_at")
    private LocalDateTime documentGeneratedAt;

    // ========== SATUSEHAT ==========
    @Column(name = "satusehat_submitted")
    @Builder.Default
    private Boolean satusehatSubmitted = false;

    @Column(name = "satusehat_submission_date")
    private LocalDateTime satusehatSubmissionDate;

    @Column(name = "satusehat_service_request_id", length = 100)
    private String satusehatServiceRequestId;

    // ========== Status ==========
    @Column(name = "referral_status", nullable = false, length = 20)
    @NotBlank(message = "Referral status is required")
    @Builder.Default
    private String referralStatus = "PENDING"; // PENDING, ACCEPTED, COMPLETED, REJECTED, CANCELLED

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Business Methods ==========

    public void sign(UUID doctorId, String doctorName, String signature) {
        this.signed = true;
        this.signedAt = LocalDateTime.now();
        this.signedById = doctorId;
        this.signedByName = doctorName;
        this.digitalSignature = signature;
    }

    public void accept(String acceptedByName, LocalDateTime appointmentDateTime) {
        this.referralAccepted = true;
        this.referralStatus = "ACCEPTED";
        this.acceptanceDate = LocalDateTime.now();
        this.acceptedBy = acceptedByName;
        this.appointmentDate = appointmentDateTime;
    }

    public void reject(String reason) {
        this.referralAccepted = false;
        this.referralStatus = "REJECTED";
        this.rejectionReason = reason;
    }

    public void complete() {
        this.referralStatus = "COMPLETED";
    }

    public boolean isPending() {
        return "PENDING".equals(referralStatus);
    }

    public boolean isAccepted() {
        return "ACCEPTED".equals(referralStatus);
    }
}
