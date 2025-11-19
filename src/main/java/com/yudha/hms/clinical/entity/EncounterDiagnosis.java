package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Diagnosis Entity.
 *
 * Represents diagnoses associated with encounters (ICD-10 coded).
 * Many-to-many relationship between encounters and diagnoses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "encounter_diagnoses", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_encounter_diagnoses_encounter", columnList = "encounter_id"),
        @Index(name = "idx_encounter_diagnoses_diagnosis", columnList = "diagnosis_id"),
        @Index(name = "idx_encounter_diagnoses_code", columnList = "diagnosis_code"),
        @Index(name = "idx_encounter_diagnoses_type", columnList = "diagnosis_type"),
        @Index(name = "idx_encounter_diagnoses_status", columnList = "clinical_status"),
        @Index(name = "idx_encounter_diagnoses_rank", columnList = "encounter_id, rank")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Diagnoses associated with encounters (ICD-10 coded)")
public class EncounterDiagnosis extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Relationships ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "diagnosis_id")
    private UUID diagnosisId; // FK to icd10_codes table (master data)

    // ========== Diagnosis Information ==========
    @Column(name = "diagnosis_code", nullable = false, length = 10)
    @NotBlank(message = "Diagnosis code is required")
    private String diagnosisCode; // ICD-10 code (e.g., A00.0)

    @Column(name = "diagnosis_text", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Diagnosis text is required")
    private String diagnosisText; // Description of the diagnosis

    // ========== Diagnosis Classification ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "diagnosis_type", nullable = false, length = 30)
    @NotNull(message = "Diagnosis type is required")
    private DiagnosisType diagnosisType; // PRIMARY, SECONDARY, ADMISSION, DISCHARGE

    @Enumerated(EnumType.STRING)
    @Column(name = "clinical_status", nullable = false, length = 20)
    @NotNull(message = "Clinical status is required")
    @Builder.Default
    private ClinicalStatus clinicalStatus = ClinicalStatus.ACTIVE;

    // ========== Priority/Ranking ==========
    @Column(name = "rank", nullable = false)
    @NotNull(message = "Rank is required")
    @Positive(message = "Rank must be positive")
    @Builder.Default
    private Integer rank = 1; // Order of importance (1 = highest priority)

    // ========== Verification ==========
    @Column(name = "verification_status", length = 30)
    @Builder.Default
    private String verificationStatus = "PROVISIONAL"; // PROVISIONAL, CONFIRMED, DIFFERENTIAL, REFUTED

    // ========== Clinical Details ==========
    @Column(name = "onset_date")
    private LocalDate onsetDate; // When the condition started

    @Column(name = "recorded_date", nullable = false)
    @Builder.Default
    private LocalDateTime recordedDate = LocalDateTime.now(); // When this diagnosis was recorded

    @Column(name = "severity", length = 20)
    private String severity; // MILD, MODERATE, SEVERE, CRITICAL

    // ========== Provider Information ==========
    @Column(name = "diagnosed_by_id")
    private UUID diagnosedById; // Practitioner who made the diagnosis

    @Column(name = "diagnosed_by_name", length = 200)
    private String diagnosedByName;

    // ========== Notes ==========
    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    // ========== Business Methods ==========

    /**
     * Mark diagnosis as resolved.
     */
    public void markAsResolved() {
        this.clinicalStatus = ClinicalStatus.RESOLVED;
    }

    /**
     * Confirm the diagnosis.
     */
    public void confirm() {
        this.verificationStatus = "CONFIRMED";
    }

    /**
     * Mark as primary diagnosis (rank 1).
     */
    public void markAsPrimary() {
        this.diagnosisType = DiagnosisType.PRIMARY;
        this.rank = 1;
    }

    /**
     * Check if this is the primary diagnosis.
     */
    public boolean isPrimary() {
        return diagnosisType == DiagnosisType.PRIMARY || rank == 1;
    }

    /**
     * Check if diagnosis is still active.
     */
    public boolean isActive() {
        return clinicalStatus == ClinicalStatus.ACTIVE;
    }
}
