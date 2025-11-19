package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Discharge Summary Entity.
 *
 * Comprehensive discharge documentation for patient encounters.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "discharge_summary", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_discharge_number", columnList = "discharge_number"),
        @Index(name = "idx_discharge_encounter", columnList = "encounter_id"),
        @Index(name = "idx_discharge_patient", columnList = "patient_id"),
        @Index(name = "idx_discharge_date", columnList = "discharge_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Comprehensive discharge summaries")
public class DischargeSummary extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "discharge_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Discharge number is required")
    private String dischargeNumber; // DIS-20250119-0001

    // ========== References ==========
    @Column(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter ID is required")
    private UUID encounterId;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Discharge Details ==========
    @Column(name = "discharge_date", nullable = false)
    @NotNull(message = "Discharge date is required")
    private LocalDateTime dischargeDate;

    @Column(name = "discharge_time")
    private LocalTime dischargeTime;

    @Column(name = "discharge_type", nullable = false, length = 30)
    @NotBlank(message = "Discharge type is required")
    private String dischargeType; // ROUTINE, AGAINST_MEDICAL_ADVICE, TRANSFER, DECEASED

    @Column(name = "discharge_disposition", nullable = false, length = 50)
    @NotBlank(message = "Discharge disposition is required")
    private String dischargeDisposition;

    // ========== Clinical Summary ==========
    @Column(name = "admission_date")
    private LocalDateTime admissionDate;

    @Column(name = "length_of_stay_days")
    private Integer lengthOfStayDays;

    @Column(name = "reason_for_admission", columnDefinition = "TEXT")
    private String reasonForAdmission;

    @Column(name = "hospital_course", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Hospital course is required")
    private String hospitalCourse;

    @Column(name = "procedures_performed", columnDefinition = "TEXT")
    private String proceduresPerformed;

    // ========== Diagnoses ==========
    @Column(name = "primary_diagnosis_code", length = 10)
    private String primaryDiagnosisCode;

    @Column(name = "primary_diagnosis_text", columnDefinition = "TEXT")
    private String primaryDiagnosisText;

    @Column(name = "secondary_diagnoses", columnDefinition = "TEXT")
    private String secondaryDiagnoses;

    // ========== Final Condition ==========
    @Column(name = "condition_at_discharge", length = 50)
    private String conditionAtDischarge; // IMPROVED, STABLE, DETERIORATED, DECEASED

    @Column(name = "vital_signs_at_discharge", columnDefinition = "TEXT")
    private String vitalSignsAtDischarge;

    // ========== Medications ==========
    @Column(name = "discharge_medications", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Discharge medications are required")
    private String dischargeMedications;

    @Column(name = "medications_discontinued", columnDefinition = "TEXT")
    private String medicationsDiscontinued;

    @Column(name = "new_medications", columnDefinition = "TEXT")
    private String newMedications;

    // ========== Follow-up Care ==========
    @Column(name = "follow_up_instructions", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Follow-up instructions are required")
    private String followUpInstructions;

    @Column(name = "follow_up_appointment_date")
    private LocalDate followUpAppointmentDate;

    @Column(name = "follow_up_doctor", length = 200)
    private String followUpDoctor;

    @Column(name = "follow_up_department", length = 100)
    private String followUpDepartment;

    // ========== Instructions ==========
    @Column(name = "diet_instructions", columnDefinition = "TEXT")
    private String dietInstructions;

    @Column(name = "activity_restrictions", columnDefinition = "TEXT")
    private String activityRestrictions;

    @Column(name = "wound_care_instructions", columnDefinition = "TEXT")
    private String woundCareInstructions;

    @Column(name = "warning_signs", columnDefinition = "TEXT")
    private String warningSigns;

    @Column(name = "emergency_contact", columnDefinition = "TEXT")
    private String emergencyContact;

    // ========== Referrals ==========
    @Column(name = "referral_to", length = 200)
    private String referralTo;

    @Column(name = "referral_reason", columnDefinition = "TEXT")
    private String referralReason;

    // ========== Provider Information ==========
    @Column(name = "discharge_doctor_id")
    private UUID dischargeDoctorId;

    @Column(name = "discharge_doctor_name", length = 200)
    private String dischargeDoctorName;

    @Column(name = "attending_doctor_name", length = 200)
    private String attendingDoctorName;

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

    // ========== SATUSEHAT ==========
    @Column(name = "satusehat_submitted")
    @Builder.Default
    private Boolean satusehatSubmitted = false;

    @Column(name = "satusehat_submission_date")
    private LocalDateTime satusehatSubmissionDate;

    // ========== Document Generation ==========
    @Column(name = "document_generated")
    @Builder.Default
    private Boolean documentGenerated = false;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "document_generated_at")
    private LocalDateTime documentGeneratedAt;

    // ========== Business Methods ==========

    public void sign(UUID doctorId, String doctorName) {
        this.signed = true;
        this.signedAt = LocalDateTime.now();
        this.signedById = doctorId;
        this.signedByName = doctorName;
    }

    public boolean isReadyForDischarge() {
        return hospitalCourse != null && !hospitalCourse.isEmpty() &&
               dischargeMedications != null && !dischargeMedications.isEmpty() &&
               followUpInstructions != null && !followUpInstructions.isEmpty();
    }
}
