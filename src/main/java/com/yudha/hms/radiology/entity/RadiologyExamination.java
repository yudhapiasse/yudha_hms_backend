package com.yudha.hms.radiology.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Radiology Examination Entity.
 *
 * Examination catalog (like lab tests)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "radiology_examination", schema = "radiology_schema", indexes = {
        @Index(name = "idx_radiology_examination_code", columnList = "exam_code", unique = true),
        @Index(name = "idx_radiology_examination_name", columnList = "exam_name"),
        @Index(name = "idx_radiology_examination_modality", columnList = "modality_id"),
        @Index(name = "idx_radiology_examination_cpt_code", columnList = "cpt_code"),
        @Index(name = "idx_radiology_examination_body_part", columnList = "body_part"),
        @Index(name = "idx_radiology_examination_active", columnList = "is_active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RadiologyExamination extends SoftDeletableEntity {

    /**
     * Examination code
     */
    @Column(name = "exam_code", nullable = false, unique = true, length = 50)
    private String examCode;

    /**
     * Examination name
     */
    @Column(name = "exam_name", nullable = false, length = 200)
    private String examName;

    /**
     * Short name
     */
    @Column(name = "short_name", length = 100)
    private String shortName;

    /**
     * Modality reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modality_id", nullable = false)
    private RadiologyModality modality;

    /**
     * CPT code for procedure
     */
    @Column(name = "cpt_code", length = 20)
    private String cptCode;

    /**
     * ICD procedure code
     */
    @Column(name = "icd_procedure_code", length = 20)
    private String icdProcedureCode;

    /**
     * Preparation instructions
     */
    @Column(name = "preparation_instructions", columnDefinition = "TEXT")
    private String preparationInstructions;

    /**
     * Whether fasting is required
     */
    @Column(name = "fasting_required")
    @Builder.Default
    private Boolean fastingRequired = false;

    /**
     * Fasting duration in hours
     */
    @Column(name = "fasting_duration_hours")
    private Integer fastingDurationHours;

    /**
     * Whether contrast is required
     */
    @Column(name = "requires_contrast")
    @Builder.Default
    private Boolean requiresContrast = false;

    /**
     * Contrast type
     */
    @Column(name = "contrast_type", length = 50)
    private String contrastType;

    /**
     * Contrast volume in ml
     */
    @Column(name = "contrast_volume_ml", precision = 10, scale = 2)
    private BigDecimal contrastVolumeMl;

    /**
     * Examination duration in minutes
     */
    @Column(name = "exam_duration_minutes")
    private Integer examDurationMinutes;

    /**
     * Reporting time in minutes
     */
    @Column(name = "reporting_time_minutes")
    private Integer reportingTimeMinutes;

    /**
     * Base cost
     */
    @Column(name = "base_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseCost;

    /**
     * Contrast cost
     */
    @Column(name = "contrast_cost", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal contrastCost = BigDecimal.ZERO;

    /**
     * BPJS tariff
     */
    @Column(name = "bpjs_tariff", precision = 15, scale = 2)
    private BigDecimal bpjsTariff;

    /**
     * Body part examined
     */
    @Column(name = "body_part", length = 200)
    private String bodyPart;

    /**
     * Whether laterality is applicable
     */
    @Column(name = "laterality_applicable")
    @Builder.Default
    private Boolean lateralityApplicable = false;

    /**
     * Positioning notes
     */
    @Column(name = "positioning_notes", columnDefinition = "TEXT")
    private String positioningNotes;

    /**
     * Clinical indication
     */
    @Column(name = "clinical_indication", columnDefinition = "TEXT")
    private String clinicalIndication;

    /**
     * Interpretation guide
     */
    @Column(name = "interpretation_guide", columnDefinition = "TEXT")
    private String interpretationGuide;

    /**
     * Active status
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Whether approval is required
     */
    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;
}
