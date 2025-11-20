package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

/**
 * Clinical Note Template Entity.
 *
 * Predefined templates for common clinical scenarios to standardize documentation
 * and improve efficiency. Supports SOAP format and customizable fields.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "clinical_note_templates", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_template_code", columnList = "template_code"),
        @Index(name = "idx_template_specialty", columnList = "specialty"),
        @Index(name = "idx_template_category", columnList = "category"),
        @Index(name = "idx_template_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Predefined templates for clinical documentation")
public class ClinicalNoteTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "template_code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Template code is required")
    private String templateCode; // e.g., SOAP_HYPERTENSION, SOAP_DIABETES, PROC_WOUND_CARE

    @Column(name = "template_name", nullable = false, length = 200)
    @NotBlank(message = "Template name is required")
    private String templateName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ========== Classification ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", nullable = false, length = 50)
    @NotNull(message = "Template type is required")
    private TemplateType templateType; // SOAP_NOTE, PROCEDURE, PHYSICAL_EXAM, DISCHARGE, REFERRAL

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    @NotNull(message = "Category is required")
    private TemplateCategory category; // DIAGNOSIS_SPECIFIC, SPECIALTY_SPECIFIC, GENERAL, PROCEDURE

    @Column(name = "specialty", length = 100)
    private String specialty; // CARDIOLOGY, NEUROLOGY, GENERAL_MEDICINE, SURGERY, etc.

    @Column(name = "diagnosis_codes", columnDefinition = "TEXT")
    private String diagnosisCodes; // JSON array of ICD-10 codes this template is for

    @Column(name = "procedure_codes", columnDefinition = "TEXT")
    private String procedureCodes; // JSON array of ICD-9-CM codes this template is for

    // ========== SOAP Template Content ==========
    @Column(name = "subjective_template", columnDefinition = "TEXT")
    private String subjectiveTemplate; // Template text with placeholders

    @Column(name = "objective_template", columnDefinition = "TEXT")
    private String objectiveTemplate;

    @Column(name = "assessment_template", columnDefinition = "TEXT")
    private String assessmentTemplate;

    @Column(name = "plan_template", columnDefinition = "TEXT")
    private String planTemplate;

    // ========== Physical Exam Template ==========
    @Column(name = "physical_exam_template", columnDefinition = "TEXT")
    private String physicalExamTemplate; // JSON structure of physical exam fields

    // ========== Procedure Template ==========
    @Column(name = "procedure_template", columnDefinition = "TEXT")
    private String procedureTemplate; // Standardized procedure note structure

    @Column(name = "indication_template", columnDefinition = "TEXT")
    private String indicationTemplate;

    @Column(name = "technique_template", columnDefinition = "TEXT")
    private String techniqueTemplate;

    @Column(name = "findings_template", columnDefinition = "TEXT")
    private String findingsTemplate;

    // ========== Custom Fields and Placeholders ==========
    @Column(name = "custom_fields", columnDefinition = "TEXT")
    private String customFields; // JSON array of custom field definitions

    @Column(name = "required_fields", columnDefinition = "TEXT")
    private String requiredFields; // JSON array of required field names

    @Column(name = "field_validations", columnDefinition = "TEXT")
    private String fieldValidations; // JSON object of field validation rules

    // ========== Common Clinical Scenarios ==========
    @Column(name = "common_medications", columnDefinition = "TEXT")
    private String commonMedications; // JSON array of typical medications for this condition

    @Column(name = "common_orders", columnDefinition = "TEXT")
    private String commonOrders; // JSON array of typical lab/imaging orders

    @Column(name = "common_diagnoses", columnDefinition = "TEXT")
    private String commonDiagnoses; // JSON array of differential diagnoses

    @Column(name = "clinical_guidelines", columnDefinition = "TEXT")
    private String clinicalGuidelines; // Evidence-based guidelines for this condition

    @Column(name = "warning_signs", columnDefinition = "TEXT")
    private String warningSigns; // Red flags to watch for

    // ========== Instructions and Help ==========
    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions;

    @Column(name = "examples", columnDefinition = "TEXT")
    private String examples; // Example filled-out notes

    @Column(name = "tips", columnDefinition = "TEXT")
    private String tips; // Clinical documentation tips

    // ========== Version Control ==========
    @Column(name = "template_version", nullable = false)
    @Builder.Default
    private Integer templateVersion = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "supersedes_template_id")
    private UUID supersedesTemplateId; // Previous version this replaces

    // ========== Usage Statistics ==========
    @Column(name = "usage_count")
    @Builder.Default
    private Long usageCount = 0L;

    @Column(name = "last_used_at")
    private java.time.LocalDateTime lastUsedAt;

    // ========== Access Control ==========
    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    @Column(name = "created_by_id")
    private UUID createdById;

    @Column(name = "created_by_name", length = 200)
    private String createdByName;

    @Column(name = "department_id")
    private UUID departmentId; // Department-specific template

    @Column(name = "facility_id")
    private UUID facilityId; // Facility-specific template

    // ========== Approval/Review ==========
    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;

    @Column(name = "approved")
    @Builder.Default
    private Boolean approved = false;

    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approved_by_name", length = 200)
    private String approvedByName;

    @Column(name = "approved_at")
    private java.time.LocalDateTime approvedAt;

    // ========== Enumerations ==========

    public enum TemplateType {
        SOAP_NOTE,
        PROCEDURE_NOTE,
        PHYSICAL_EXAM,
        DISCHARGE_SUMMARY,
        REFERRAL_LETTER,
        CONSULTATION,
        ADMISSION_NOTE,
        PROGRESS_NOTE,
        OPERATIVE_NOTE
    }

    public enum TemplateCategory {
        DIAGNOSIS_SPECIFIC,      // For specific diagnoses (e.g., Hypertension, Diabetes)
        SPECIALTY_SPECIFIC,       // For specific specialties (e.g., Cardiology, Neurology)
        PROCEDURE_SPECIFIC,       // For specific procedures (e.g., Wound care, IV insertion)
        GENERAL,                  // General templates usable across specialties
        EMERGENCY,                // Emergency department specific
        OUTPATIENT,               // Outpatient clinic specific
        INPATIENT                 // Inpatient/hospitalized patient specific
    }

    // ========== Business Methods ==========

    /**
     * Increment usage count.
     */
    public void recordUsage() {
        this.usageCount++;
        this.lastUsedAt = java.time.LocalDateTime.now();
    }

    /**
     * Approve the template.
     */
    public void approve(UUID approverId, String approverName) {
        this.approved = true;
        this.approvedById = approverId;
        this.approvedByName = approverName;
        this.approvedAt = java.time.LocalDateTime.now();
    }

    /**
     * Deactivate the template.
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Create new version of template.
     */
    public ClinicalNoteTemplate createNewVersion() {
        ClinicalNoteTemplate newVersion = new ClinicalNoteTemplate();
        newVersion.setTemplateCode(this.templateCode);
        newVersion.setTemplateName(this.templateName);
        newVersion.setDescription(this.description);
        newVersion.setTemplateType(this.templateType);
        newVersion.setCategory(this.category);
        newVersion.setSpecialty(this.specialty);
        newVersion.setTemplateVersion(this.templateVersion + 1);
        newVersion.setSupersedesTemplateId(this.id);
        return newVersion;
    }

    /**
     * Check if template is ready for use.
     */
    public boolean isReadyForUse() {
        if (!isActive) {
            return false;
        }
        if (requiresApproval && !approved) {
            return false;
        }
        return true;
    }

    /**
     * Check if template has SOAP components.
     */
    public boolean hasSOAPComponents() {
        return subjectiveTemplate != null ||
               objectiveTemplate != null ||
               assessmentTemplate != null ||
               planTemplate != null;
    }
}
