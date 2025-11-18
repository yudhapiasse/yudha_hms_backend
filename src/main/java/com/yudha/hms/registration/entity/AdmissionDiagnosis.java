package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Diagnosis associated with an inpatient admission.
 * Supports multiple diagnoses per admission with ICD-10 codes.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "admission_diagnosis", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_admission_diagnosis_admission", columnList = "admission_id"),
        @Index(name = "idx_admission_diagnosis_patient", columnList = "patient_id"),
        @Index(name = "idx_admission_diagnosis_icd10", columnList = "icd10_code"),
        @Index(name = "idx_admission_diagnosis_primary", columnList = "is_primary")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Diagnoses associated with inpatient admissions")
public class AdmissionDiagnosis extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id", nullable = false, foreignKey = @ForeignKey(name = "fk_diagnosis_admission"))
    @NotNull(message = "Admission is required")
    private InpatientAdmission admission;

    @Column(name = "patient_id", nullable = false)
    @NotNull
    private UUID patientId;

    // ICD-10 Code
    @Column(name = "icd10_id")
    private UUID icd10Id; // Reference to ICD-10 master data

    @Column(name = "icd10_code", nullable = false, length = 10)
    @NotBlank(message = "ICD-10 code is required")
    @Size(max = 10)
    private String icd10Code;

    @Column(name = "icd10_description", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "ICD-10 description is required")
    private String icd10Description;

    // Diagnosis type
    @Column(name = "diagnosis_type", nullable = false, length = 20)
    @NotBlank
    @Size(max = 20)
    private String diagnosisType; // PRIMARY, SECONDARY, COMPLICATION, COMORBIDITY

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    // Timing
    @Column(name = "diagnosed_at", nullable = false)
    @NotNull
    @Builder.Default
    private LocalDateTime diagnosedAt = LocalDateTime.now();

    // Clinical details
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Check if this is the primary diagnosis.
     *
     * @return true if primary diagnosis
     */
    public boolean isPrimaryDiagnosis() {
        return Boolean.TRUE.equals(isPrimary) || "PRIMARY".equals(diagnosisType);
    }

    /**
     * Check if this is a secondary diagnosis.
     *
     * @return true if secondary diagnosis
     */
    public boolean isSecondaryDiagnosis() {
        return "SECONDARY".equals(diagnosisType);
    }

    /**
     * Check if this is a complication.
     *
     * @return true if complication
     */
    public boolean isComplication() {
        return "COMPLICATION".equals(diagnosisType);
    }

    /**
     * Check if this is a comorbidity.
     *
     * @return true if comorbidity
     */
    public boolean isComorbidity() {
        return "COMORBIDITY".equals(diagnosisType);
    }

    /**
     * Get full diagnosis description with code.
     *
     * @return formatted diagnosis string
     */
    public String getFullDiagnosis() {
        return icd10Code + " - " + icd10Description;
    }
}
