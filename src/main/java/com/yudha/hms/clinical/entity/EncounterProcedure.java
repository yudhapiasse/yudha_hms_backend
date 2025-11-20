package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Procedure Entity.
 *
 * Documents procedures performed during encounters with ICD-9-CM coding.
 * Tracks procedural details, providers, outcomes, and billing information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "encounter_procedures", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_procedure_encounter", columnList = "encounter_id"),
        @Index(name = "idx_procedure_patient", columnList = "patient_id"),
        @Index(name = "idx_procedure_code", columnList = "procedure_code"),
        @Index(name = "idx_procedure_date", columnList = "procedure_date"),
        @Index(name = "idx_procedure_provider", columnList = "primary_provider_id"),
        @Index(name = "idx_procedure_status", columnList = "procedure_status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Procedures performed during encounters with ICD-9-CM coding")
public class EncounterProcedure extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "procedure_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Procedure number is required")
    private String procedureNumber; // PROC-20251120-0001

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Procedure Coding (ICD-9-CM) ==========
    @Column(name = "procedure_code", nullable = false, length = 10)
    @NotBlank(message = "Procedure code is required")
    private String procedureCode; // ICD-9-CM procedure code (e.g., 37.22, 45.23)

    @Column(name = "procedure_description", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Procedure description is required")
    private String procedureDescription; // Full description of the procedure

    @Column(name = "procedure_name", nullable = false, length = 300)
    @NotBlank(message = "Procedure name is required")
    private String procedureName; // Short name/title

    // ========== Procedure Classification ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "procedure_type", nullable = false, length = 50)
    @NotNull(message = "Procedure type is required")
    private ProcedureType procedureType; // DIAGNOSTIC, THERAPEUTIC, SURGICAL, INTERVENTIONAL

    @Enumerated(EnumType.STRING)
    @Column(name = "procedure_category", length = 50)
    private ProcedureCategory procedureCategory; // MINOR, MAJOR, EMERGENCY, ELECTIVE

    @Column(name = "body_site", length = 200)
    private String bodySite; // Anatomical location

    @Column(name = "laterality", length = 20)
    private String laterality; // LEFT, RIGHT, BILATERAL

    // ========== Timing ==========
    @Column(name = "procedure_date", nullable = false)
    @NotNull(message = "Procedure date is required")
    private LocalDateTime procedureDate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // ========== Status and Outcome ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "procedure_status", nullable = false, length = 30)
    @NotNull(message = "Procedure status is required")
    @Builder.Default
    private ProcedureStatus procedureStatus = ProcedureStatus.PLANNED;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", length = 30)
    private ProcedureOutcome outcome; // SUCCESSFUL, COMPLICATED, ABORTED, FAILED

    @Column(name = "outcome_notes", columnDefinition = "TEXT")
    private String outcomeNotes;

    // ========== Providers ==========
    @Column(name = "primary_provider_id", nullable = false)
    @NotNull(message = "Primary provider is required")
    private UUID primaryProviderId;

    @Column(name = "primary_provider_name", nullable = false, length = 200)
    @NotBlank(message = "Primary provider name is required")
    private String primaryProviderName;

    @Column(name = "assisting_providers", columnDefinition = "TEXT")
    private String assistingProviders; // JSON array of provider IDs and names

    @Column(name = "anesthesiologist_id")
    private UUID anesthesiologistId;

    @Column(name = "anesthesiologist_name", length = 200)
    private String anesthesiologistName;

    // ========== Clinical Details ==========
    @Column(name = "indication", columnDefinition = "TEXT")
    private String indication; // Reason for procedure

    @Column(name = "technique", columnDefinition = "TEXT")
    private String technique; // How procedure was performed

    @Column(name = "findings", columnDefinition = "TEXT")
    private String findings; // What was found/observed

    @Column(name = "specimens_collected", columnDefinition = "TEXT")
    private String specimensCollected; // Pathology specimens

    @Column(name = "complications", columnDefinition = "TEXT")
    private String complications; // Any complications encountered

    @Column(name = "blood_loss_ml")
    private Integer bloodLossMl;

    // ========== Anesthesia ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "anesthesia_type", length = 50)
    private AnesthesiaType anesthesiaType; // LOCAL, REGIONAL, GENERAL, SEDATION

    @Column(name = "anesthesia_notes", columnDefinition = "TEXT")
    private String anesthesiaNotes;

    // ========== Location ==========
    @Column(name = "location_name", length = 200)
    private String locationName; // Operating room, bedside, clinic, etc.

    @Column(name = "room_number", length = 50)
    private String roomNumber;

    // ========== Consent and Documentation ==========
    @Column(name = "consent_obtained")
    @Builder.Default
    private Boolean consentObtained = false;

    @Column(name = "consent_form_id", length = 100)
    private String consentFormId;

    @Column(name = "consent_date")
    private LocalDateTime consentDate;

    @Column(name = "pre_procedure_checklist_completed")
    @Builder.Default
    private Boolean preProcedureChecklistCompleted = false;

    @Column(name = "post_procedure_checklist_completed")
    @Builder.Default
    private Boolean postProcedureChecklistCompleted = false;

    // ========== Post-Procedure Care ==========
    @Column(name = "post_procedure_instructions", columnDefinition = "TEXT")
    private String postProcedureInstructions;

    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "recovery_notes", columnDefinition = "TEXT")
    private String recoveryNotes;

    // ========== Billing and Coding ==========
    @Column(name = "billable")
    @Builder.Default
    private Boolean billable = true;

    @Column(name = "charge_amount", precision = 15, scale = 2)
    private BigDecimal chargeAmount;

    @Column(name = "modifier_codes", length = 100)
    private String modifierCodes; // CPT modifiers if applicable

    @Column(name = "billed")
    @Builder.Default
    private Boolean billed = false;

    @Column(name = "billed_at")
    private LocalDateTime billedAt;

    // ========== Report and Documentation ==========
    @Column(name = "procedure_report", columnDefinition = "TEXT")
    private String procedureReport; // Full operative/procedure report

    @Column(name = "report_dictated")
    @Builder.Default
    private Boolean reportDictated = false;

    @Column(name = "report_signed")
    @Builder.Default
    private Boolean reportSigned = false;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "digital_signature", columnDefinition = "TEXT")
    private String digitalSignature;

    // ========== Images and Attachments ==========
    @Column(name = "images_captured")
    @Builder.Default
    private Boolean imagesCaptured = false;

    @Column(name = "image_ids", columnDefinition = "TEXT")
    private String imageIds; // JSON array of image document IDs

    @Column(name = "video_recorded")
    @Builder.Default
    private Boolean videoRecorded = false;

    @Column(name = "video_ids", columnDefinition = "TEXT")
    private String videoIds;

    // ========== Quality and Safety ==========
    @Column(name = "timeout_performed")
    @Builder.Default
    private Boolean timeoutPerformed = false;

    @Column(name = "site_marking_verified")
    @Builder.Default
    private Boolean siteMarkingVerified = false;

    @Column(name = "equipment_used", columnDefinition = "TEXT")
    private String equipmentUsed;

    @Column(name = "implants_used", columnDefinition = "TEXT")
    private String implantsUsed;

    // ========== Enumerations ==========

    public enum ProcedureType {
        DIAGNOSTIC,
        THERAPEUTIC,
        SURGICAL,
        INTERVENTIONAL,
        ENDOSCOPIC,
        MINIMALLY_INVASIVE,
        IMAGING_GUIDED
    }

    public enum ProcedureCategory {
        MINOR,
        MAJOR,
        EMERGENCY,
        ELECTIVE,
        URGENT
    }

    public enum ProcedureStatus {
        PLANNED,
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        ABORTED,
        FAILED
    }

    public enum ProcedureOutcome {
        SUCCESSFUL,
        SUCCESSFUL_WITH_COMPLICATIONS,
        COMPLICATED,
        ABORTED,
        FAILED,
        PENDING_RESULT
    }

    public enum AnesthesiaType {
        NONE,
        LOCAL,
        REGIONAL,
        SPINAL,
        EPIDURAL,
        GENERAL,
        CONSCIOUS_SEDATION,
        MAC // Monitored Anesthesia Care
    }

    // ========== Business Methods ==========

    /**
     * Calculate procedure duration.
     */
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    /**
     * Mark procedure as completed.
     */
    public void complete(ProcedureOutcome outcome, String notes) {
        this.procedureStatus = ProcedureStatus.COMPLETED;
        this.outcome = outcome;
        this.outcomeNotes = notes;
        this.endTime = LocalDateTime.now();
        calculateDuration();
    }

    /**
     * Sign the procedure report.
     */
    public void signReport(String signature) {
        this.reportSigned = true;
        this.signedAt = LocalDateTime.now();
        this.digitalSignature = signature;
    }

    /**
     * Check if procedure is complete and documented.
     */
    public boolean isFullyDocumented() {
        return procedureStatus == ProcedureStatus.COMPLETED &&
               procedureReport != null &&
               reportSigned &&
               postProcedureChecklistCompleted;
    }

    /**
     * Check if procedure had complications.
     */
    public boolean hadComplications() {
        return complications != null && !complications.isEmpty();
    }
}
