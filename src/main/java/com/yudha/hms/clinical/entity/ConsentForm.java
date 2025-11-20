package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Consent Form Entity.
 * Tracks informed consent for procedures.
 *
 * @author HMS Development Team
 * @since 2025-01-20
 */
@Entity
@Table(name = "consent_forms", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_consent_procedure", columnList = "encounter_procedure_id"),
        @Index(name = "idx_consent_patient", columnList = "patient_id"),
        @Index(name = "idx_consent_status", columnList = "consent_status"),
        @Index(name = "idx_consent_date", columnList = "consent_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Informed consent forms for procedures")
public class ConsentForm extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "consent_number", unique = true, length = 50)
    private String consentNumber; // CONSENT-20251120-0001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_procedure_id")
    private EncounterProcedure encounterProcedure;

    @Column(name = "patient_id", nullable = false)
    @NotNull
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    @NotBlank
    private String patientName;

    @Column(name = "procedure_name", nullable = false, length = 300)
    @NotBlank
    private String procedureName;

    @Column(name = "procedure_description", columnDefinition = "TEXT")
    private String procedureDescription;

    // Risks and Benefits
    @Column(name = "risks_explained", columnDefinition = "TEXT")
    private String risksExplained;

    @Column(name = "benefits_explained", columnDefinition = "TEXT")
    private String benefitsExplained;

    @Column(name = "alternatives_explained", columnDefinition = "TEXT")
    private String alternativesExplained;

    // Consent Details
    @Column(name = "consent_given")
    @Builder.Default
    private Boolean consentGiven = false;

    @Column(name = "consent_date")
    private LocalDateTime consentDate;

    @Column(name = "consent_status", length = 20)
    @Builder.Default
    private String consentStatus = "PENDING"; // PENDING, OBTAINED, REFUSED, WITHDRAWN

    // Patient Understanding
    @Column(name = "patient_understands")
    @Builder.Default
    private Boolean patientUnderstands = false;

    @Column(name = "questions_answered")
    @Builder.Default
    private Boolean questionsAnswered = false;

    @Column(name = "interpreter_used")
    @Builder.Default
    private Boolean interpreterUsed = false;

    @Column(name = "interpreter_name", length = 200)
    private String interpreterName;

    // Signatures
    @Column(name = "patient_signature", columnDefinition = "TEXT")
    private String patientSignature;

    @Column(name = "patient_signed_at")
    private LocalDateTime patientSignedAt;

    @Column(name = "guardian_name", length = 200)
    private String guardianName; // If patient is minor or incapacitated

    @Column(name = "guardian_relationship", length = 100)
    private String guardianRelationship;

    @Column(name = "guardian_signature", columnDefinition = "TEXT")
    private String guardianSignature;

    @Column(name = "guardian_signed_at")
    private LocalDateTime guardianSignedAt;

    @Column(name = "witness_name", length = 200)
    private String witnessName;

    @Column(name = "witness_signature", columnDefinition = "TEXT")
    private String witnessSignature;

    @Column(name = "witness_signed_at")
    private LocalDateTime witnessSignedAt;

    @Column(name = "physician_id", nullable = false)
    @NotNull
    private UUID physicianId;

    @Column(name = "physician_name", nullable = false, length = 200)
    @NotBlank
    private String physicianName;

    @Column(name = "physician_signature", columnDefinition = "TEXT")
    private String physicianSignature;

    @Column(name = "physician_signed_at")
    private LocalDateTime physicianSignedAt;

    // Document
    @Column(name = "form_template_id")
    private UUID formTemplateId;

    @Column(name = "form_content", columnDefinition = "TEXT")
    private String formContent; // HTML/JSON of complete form

    @Column(name = "document_id")
    private UUID documentId; // Link to stored PDF

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Withdrawal
    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "withdrawal_reason", columnDefinition = "TEXT")
    private String withdrawalReason;

    public void obtain(String patientSig, UUID physicianId, String physicianName, String physicianSig) {
        this.consentGiven = true;
        this.consentStatus = "OBTAINED";
        this.consentDate = LocalDateTime.now();
        this.patientSignature = patientSig;
        this.patientSignedAt = LocalDateTime.now();
        this.physicianId = physicianId;
        this.physicianName = physicianName;
        this.physicianSignature = physicianSig;
        this.physicianSignedAt = LocalDateTime.now();
    }

    public void withdraw(String reason) {
        this.consentStatus = "WITHDRAWN";
        this.withdrawnAt = LocalDateTime.now();
        this.withdrawalReason = reason;
    }
}
