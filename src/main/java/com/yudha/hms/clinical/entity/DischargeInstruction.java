package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Discharge Instruction Entity.
 *
 * Represents specific care instructions for patient after discharge.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "discharge_instruction", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_instruction_summary", columnList = "discharge_summary_id"),
        @Index(name = "idx_instruction_category", columnList = "instruction_category")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DischargeInstruction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Reference ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discharge_summary_id", nullable = false)
    private DischargeSummary dischargeSummary;

    // ========== Instruction Details ==========

    @Column(name = "instruction_category", nullable = false, length = 50)
    private String instructionCategory; // WOUND_CARE, DIET, ACTIVITY, PHYSICAL_THERAPY, MEDICATION, GENERAL

    @Column(name = "instruction_title", nullable = false, length = 200)
    private String instructionTitle;

    @Column(name = "instruction_details", columnDefinition = "TEXT", nullable = false)
    private String instructionDetails;

    @Column(name = "instruction_frequency", length = 100)
    private String instructionFrequency; // e.g., "Daily", "3 times per day", "As needed"

    @Column(name = "instruction_duration", length = 100)
    private String instructionDuration; // e.g., "Until healed", "For 2 weeks"

    // ========== Specific Instructions ==========

    @Column(name = "do_instructions", columnDefinition = "TEXT")
    private String doInstructions; // Things patient should do

    @Column(name = "dont_instructions", columnDefinition = "TEXT")
    private String dontInstructions; // Things patient should avoid

    @Column(name = "when_to_call_doctor", columnDefinition = "TEXT")
    private String whenToCallDoctor; // Warning signs

    // ========== Media/Resources ==========

    @Column(name = "has_video_tutorial")
    @Builder.Default
    private Boolean hasVideoTutorial = false;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "has_printed_material")
    @Builder.Default
    private Boolean hasPrintedMaterial = false;

    @Column(name = "printed_material_url", length = 500)
    private String printedMaterialUrl;

    @Column(name = "diagram_url", length = 500)
    private String diagramUrl;

    // ========== Patient Education ==========

    @Column(name = "patient_educated")
    @Builder.Default
    private Boolean patientEducated = false;

    @Column(name = "patient_demonstrates_understanding")
    @Builder.Default
    private Boolean patientDemonstratesUnderstanding = false;

    @Column(name = "education_notes", columnDefinition = "TEXT")
    private String educationNotes;

    @Column(name = "educator_name", length = 200)
    private String educatorName;

    // ========== Priority ==========

    @Column(name = "is_critical_instruction")
    @Builder.Default
    private Boolean isCriticalInstruction = false;

    @Column(name = "display_order")
    private Integer displayOrder;

    // ========== Additional Notes ==========

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    // ========== Business Methods ==========

    public boolean isWoundCare() {
        return "WOUND_CARE".equals(instructionCategory);
    }

    public boolean isDietInstruction() {
        return "DIET".equals(instructionCategory);
    }

    public boolean isActivityInstruction() {
        return "ACTIVITY".equals(instructionCategory);
    }

    public boolean isPhysicalTherapy() {
        return "PHYSICAL_THERAPY".equals(instructionCategory);
    }

    public void markAsEducated(String educatorName, boolean understanding) {
        this.patientEducated = true;
        this.patientDemonstratesUnderstanding = understanding;
        this.educatorName = educatorName;
    }
}
