package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ICD-9-CM Procedure Code Master Data Entity.
 *
 * Master data for ICD-9-CM procedure codes with Indonesian translations.
 * Used as reference data for procedure coding and billing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "icd9cm_codes", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_icd9cm_code", columnList = "code", unique = true),
        @Index(name = "idx_icd9cm_category", columnList = "category_code"),
        @Index(name = "idx_icd9cm_active", columnList = "is_active"),
        @Index(name = "idx_icd9cm_search", columnList = "code, description_en, description_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("ICD-9-CM procedure codes master data with Indonesian translations")
public class ICD9CMCode extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== ICD-9-CM Code Structure ==========
    @Column(name = "code", nullable = false, unique = true, length = 10)
    @NotBlank(message = "ICD-9-CM code is required")
    private String code; // e.g., 37.22, 45.23, 86.22

    @Column(name = "description_en", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "English description is required")
    private String descriptionEn; // English description

    @Column(name = "description_id", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Indonesian description is required")
    private String descriptionId; // Indonesian translation

    @Column(name = "short_description", length = 255)
    private String shortDescription; // Abbreviated description

    // ========== Classification ==========
    @Column(name = "category_code", length = 10)
    private String categoryCode; // Category (e.g., 35-39 for heart operations)

    @Column(name = "category_name_en", length = 500)
    private String categoryNameEn;

    @Column(name = "category_name_id", length = 500)
    private String categoryNameId;

    @Column(name = "procedure_type", length = 50)
    private String procedureType; // SURGICAL, DIAGNOSTIC, THERAPEUTIC

    @Column(name = "specialty", length = 100)
    private String specialty; // CARDIOLOGY, ORTHOPEDICS, GENERAL_SURGERY, etc.

    // ========== Clinical Details ==========
    @Column(name = "requires_anesthesia")
    @Builder.Default
    private Boolean requiresAnesthesia = false;

    @Column(name = "anesthesia_type_recommended", length = 50)
    private String anesthesiaTypeRecommended; // GENERAL, LOCAL, REGIONAL

    @Column(name = "avg_duration_minutes")
    private Integer avgDurationMinutes; // Average procedure duration

    @Column(name = "complexity_level", length = 20)
    private String complexityLevel; // LOW, MODERATE, HIGH, VERY_HIGH

    @Column(name = "body_system", length = 100)
    private String bodySystem; // CARDIOVASCULAR, RESPIRATORY, DIGESTIVE, etc.

    // ========== Usage Tracking ==========
    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Long usageCount = 0L;

    @Column(name = "is_common", nullable = false)
    @Builder.Default
    private Boolean isCommon = false;

    // ========== Billing and Coding ==========
    @Column(name = "is_billable", nullable = false)
    @Builder.Default
    private Boolean isBillable = true;

    @Column(name = "base_cost", precision = 15, scale = 2)
    private BigDecimal baseCost; // Standard cost/charge

    @Column(name = "requires_pre_authorization")
    @Builder.Default
    private Boolean requiresPreAuthorization = false;

    @Column(name = "insurance_coverage_notes", columnDefinition = "TEXT")
    private String insuranceCoverageNotes;

    // ========== Consent and Documentation ==========
    @Column(name = "requires_informed_consent")
    @Builder.Default
    private Boolean requiresInformedConsent = true;

    @Column(name = "consent_form_template", columnDefinition = "TEXT")
    private String consentFormTemplate; // JSON template for consent form

    @Column(name = "requires_pre_procedure_checklist")
    @Builder.Default
    private Boolean requiresPreProcedureChecklist = false;

    @Column(name = "checklist_template_id")
    private UUID checklistTemplateId; // Link to checklist template

    // ========== Operating Room Requirements ==========
    @Column(name = "requires_operating_room")
    @Builder.Default
    private Boolean requiresOperatingRoom = false;

    @Column(name = "room_setup_time_minutes")
    private Integer roomSetupTimeMinutes;

    @Column(name = "required_equipment", columnDefinition = "TEXT")
    private String requiredEquipment; // JSON array of equipment needed

    @Column(name = "required_staff_roles", columnDefinition = "TEXT")
    private String requiredStaffRoles; // JSON array of staff roles needed

    // ========== Status ==========
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "effective_date")
    private java.time.LocalDate effectiveDate;

    @Column(name = "deprecated_date")
    private java.time.LocalDate deprecatedDate;

    @Column(name = "replaced_by_code", length = 10)
    private String replacedByCode;

    // ========== Search and Display ==========
    @Column(name = "search_terms", columnDefinition = "TEXT")
    private String searchTerms; // Additional search keywords

    // ========== Notes ==========
    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(name = "coding_notes", columnDefinition = "TEXT")
    private String codingNotes;

    @Column(name = "safety_notes", columnDefinition = "TEXT")
    private String safetyNotes;

    // ========== Business Methods ==========

    /**
     * Increment usage count when procedure is performed.
     */
    public void incrementUsage() {
        this.usageCount++;
    }

    /**
     * Mark as common procedure if usage exceeds threshold.
     */
    public void markAsCommonIfPopular(long threshold) {
        if (this.usageCount >= threshold) {
            this.isCommon = true;
        }
    }

    /**
     * Get display text in Indonesian.
     */
    public String getDisplayTextId() {
        return code + " - " + descriptionId;
    }

    /**
     * Get display text in English.
     */
    public String getDisplayTextEn() {
        return code + " - " + descriptionEn;
    }

    /**
     * Check if code is deprecated.
     */
    public boolean isDeprecated() {
        return deprecatedDate != null && deprecatedDate.isBefore(java.time.LocalDate.now());
    }

    /**
     * Deprecate this code and set replacement.
     */
    public void deprecate(String replacementCode) {
        this.isActive = false;
        this.deprecatedDate = java.time.LocalDate.now();
        this.replacedByCode = replacementCode;
    }

    /**
     * Check if this is a major procedure.
     */
    public boolean isMajorProcedure() {
        return "HIGH".equals(complexityLevel) || "VERY_HIGH".equals(complexityLevel) ||
               (requiresOperatingRoom != null && requiresOperatingRoom);
    }

    /**
     * Estimate total procedure time including setup.
     */
    public int estimateTotalTimeMinutes() {
        int total = avgDurationMinutes != null ? avgDurationMinutes : 60;
        if (roomSetupTimeMinutes != null) {
            total += roomSetupTimeMinutes;
        }
        return total;
    }
}
