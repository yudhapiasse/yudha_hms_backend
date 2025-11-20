package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Procedure Template Entity.
 *
 * Templates for common procedures organized by specialty.
 * Provides standardized procedure definitions to speed up documentation.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "procedure_templates", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_procedure_template_specialty", columnList = "specialty"),
        @Index(name = "idx_procedure_template_icd9", columnList = "icd9cm_code_id"),
        @Index(name = "idx_procedure_template_active", columnList = "is_active"),
        @Index(name = "idx_procedure_template_name", columnList = "template_name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Procedure templates per specialty for standardized documentation")
public class ProcedureTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Template Identification ==========
    @Column(name = "template_name", nullable = false, length = 300)
    @NotBlank(message = "Template name is required")
    private String templateName;

    @Column(name = "template_code", unique = true, length = 50)
    private String templateCode; // e.g., ORTHO_HIP_REPLACEMENT

    @Column(name = "specialty", nullable = false, length = 100)
    @NotBlank(message = "Specialty is required")
    private String specialty; // CARDIOLOGY, ORTHOPEDICS, GENERAL_SURGERY, ENT, etc.

    @Column(name = "department_code", length = 20)
    private String departmentCode; // Department this template belongs to

    // ========== ICD-9-CM Code Reference ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icd9cm_code_id")
    private ICD9CMCode icd9cmCode;

    @Column(name = "procedure_code", length = 10)
    private String procedureCode; // Denormalized ICD-9-CM code

    // ========== Procedure Definition ==========
    @Column(name = "procedure_name", nullable = false, length = 300)
    @NotBlank(message = "Procedure name is required")
    private String procedureName;

    @Column(name = "procedure_description", columnDefinition = "TEXT")
    private String procedureDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "procedure_type", length = 50)
    private EncounterProcedure.ProcedureType procedureType;

    @Enumerated(EnumType.STRING)
    @Column(name = "procedure_category", length = 50)
    private EncounterProcedure.ProcedureCategory procedureCategory;

    // ========== Template Content ==========
    @Column(name = "indication_template", columnDefinition = "TEXT")
    private String indicationTemplate; // Common indications

    @Column(name = "technique_template", columnDefinition = "TEXT")
    private String techniqueTemplate; // Standard technique description

    @Column(name = "findings_template", columnDefinition = "TEXT")
    private String findingsTemplate; // Expected findings format

    @Column(name = "complications_list", columnDefinition = "TEXT")
    private String complicationsList; // JSON array of possible complications

    // ========== Clinical Parameters ==========
    @Column(name = "typical_duration_minutes")
    private Integer typicalDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "anesthesia_type", length = 50)
    private EncounterProcedure.AnesthesiaType anesthesiaType;

    @Column(name = "body_site", length = 200)
    private String bodySite;

    @Column(name = "typical_blood_loss_ml")
    private Integer typicalBloodLossMl;

    // ========== Requirements ==========
    @Column(name = "requires_operating_room")
    @Builder.Default
    private Boolean requiresOperatingRoom = false;

    @Column(name = "requires_informed_consent")
    @Builder.Default
    private Boolean requiresInformedConsent = true;

    @Column(name = "requires_pre_procedure_checklist")
    @Builder.Default
    private Boolean requiresPreProcedureChecklist = false;

    @Column(name = "checklist_template_id")
    private UUID checklistTemplateId;

    // ========== Staff and Equipment ==========
    @Column(name = "required_staff_roles", columnDefinition = "TEXT")
    private String requiredStaffRoles; // JSON array: ["SURGEON", "ANESTHESIOLOGIST", "NURSE"]

    @Column(name = "required_equipment", columnDefinition = "TEXT")
    private String requiredEquipment; // JSON array of equipment needed

    @Column(name = "required_supplies", columnDefinition = "TEXT")
    private String requiredSupplies; // JSON array of supplies

    @Column(name = "required_instruments", columnDefinition = "TEXT")
    private String requiredInstruments; // JSON array of surgical instruments

    // ========== Post-Procedure Care ==========
    @Column(name = "post_procedure_instructions", columnDefinition = "TEXT")
    private String postProcedureInstructions;

    @Column(name = "recovery_monitoring", columnDefinition = "TEXT")
    private String recoveryMonitoring; // What to monitor post-procedure

    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_days_after")
    private Integer followUpDaysAfter;

    @Column(name = "discharge_criteria", columnDefinition = "TEXT")
    private String dischargeCriteria;

    // ========== Billing ==========
    @Column(name = "estimated_cost", precision = 15, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "billing_notes", columnDefinition = "TEXT")
    private String billingNotes;

    // ========== Documentation Templates ==========
    @Column(name = "consent_form_template", columnDefinition = "TEXT")
    private String consentFormTemplate; // HTML or JSON template

    @Column(name = "operative_report_template", columnDefinition = "TEXT")
    private String operativeReportTemplate; // Template for procedure report

    @Column(name = "discharge_summary_template", columnDefinition = "TEXT")
    private String dischargeSummaryTemplate;

    // ========== Quality and Safety ==========
    @Column(name = "timeout_required")
    @Builder.Default
    private Boolean timeoutRequired = false;

    @Column(name = "site_marking_required")
    @Builder.Default
    private Boolean siteMarkingRequired = false;

    @Column(name = "safety_checklist", columnDefinition = "TEXT")
    private String safetyChecklist; // JSON array of safety items

    @Column(name = "contraindications", columnDefinition = "TEXT")
    private String contraindications; // List of contraindications

    @Column(name = "precautions", columnDefinition = "TEXT")
    private String precautions; // Special precautions

    // ========== Usage and Status ==========
    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Long usageCount = 0L;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false; // Default template for this procedure type

    @Column(name = "is_hospital_standard")
    @Builder.Default
    private Boolean isHospitalStandard = false; // Hospital-wide standard

    // ========== Metadata ==========
    @Column(name = "clinical_guidelines_reference", columnDefinition = "TEXT")
    private String clinicalGuidelinesReference;

    @Column(name = "evidence_level", length = 20)
    private String evidenceLevel; // A, B, C based on evidence strength

    @Column(name = "last_reviewed_date")
    private java.time.LocalDate lastReviewedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Business Methods ==========

    /**
     * Increment usage count.
     */
    public void incrementUsage() {
        this.usageCount++;
    }

    /**
     * Check if template is ready for use.
     */
    public boolean isReadyForUse() {
        return isActive &&
               procedureName != null &&
               techniqueTemplate != null &&
               (requiresInformedConsent == null || !requiresInformedConsent || consentFormTemplate != null);
    }

    /**
     * Calculate estimated total time including setup.
     */
    public int getEstimatedTotalTimeMinutes() {
        int base = typicalDurationMinutes != null ? typicalDurationMinutes : 60;
        if (requiresOperatingRoom != null && requiresOperatingRoom) {
            base += 30; // Add setup time
        }
        return base;
    }

    /**
     * Check if this is a major procedure.
     */
    public boolean isMajorProcedure() {
        return procedureCategory == EncounterProcedure.ProcedureCategory.MAJOR ||
               (requiresOperatingRoom != null && requiresOperatingRoom);
    }
}
