package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

/**
 * ICD-10 Master Data Entity.
 *
 * Master data for ICD-10 diagnosis codes with Indonesian translations.
 * Used as reference data for diagnosis coding.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "icd10_codes", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_icd10_code", columnList = "code", unique = true),
        @Index(name = "idx_icd10_category", columnList = "category_code"),
        @Index(name = "idx_icd10_chapter", columnList = "chapter_code"),
        @Index(name = "idx_icd10_active", columnList = "is_active"),
        @Index(name = "idx_icd10_search", columnList = "code, description_en, description_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("ICD-10 diagnosis codes master data with Indonesian translations")
public class ICD10Code extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== ICD-10 Code Structure ==========
    @Column(name = "code", nullable = false, unique = true, length = 10)
    @NotBlank(message = "ICD-10 code is required")
    private String code; // e.g., A00.0, E11.9

    @Column(name = "description_en", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "English description is required")
    private String descriptionEn; // English description

    @Column(name = "description_id", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Indonesian description is required")
    private String descriptionId; // Indonesian translation

    // ========== ICD-10 Hierarchy ==========
    @Column(name = "chapter_code", length = 5)
    private String chapterCode; // Chapter (e.g., I for "Certain infectious and parasitic diseases")

    @Column(name = "chapter_name_en", length = 500)
    private String chapterNameEn;

    @Column(name = "chapter_name_id", length = 500)
    private String chapterNameId;

    @Column(name = "category_code", length = 5)
    private String categoryCode; // Category (e.g., A00-A09)

    @Column(name = "category_name_en", length = 500)
    private String categoryNameEn;

    @Column(name = "category_name_id", length = 500)
    private String categoryNameId;

    // ========== Classification ==========
    @Column(name = "is_three_character", nullable = false)
    @Builder.Default
    private Boolean isThreeCharacter = false; // True if code is 3-char (e.g., A00)

    @Column(name = "is_four_character", nullable = false)
    @Builder.Default
    private Boolean isFourCharacter = false; // True if code is 4-char (e.g., A00.0)

    @Column(name = "code_type", length = 20)
    @Builder.Default
    private String codeType = "DIAGNOSIS"; // DIAGNOSIS, SYMPTOM, PROCEDURE

    // ========== Usage Tracking ==========
    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Long usageCount = 0L; // Track how often this code is used

    @Column(name = "is_common", nullable = false)
    @Builder.Default
    private Boolean isCommon = false; // Flag for commonly used codes

    // ========== Insurance and Billing ==========
    @Column(name = "is_billable", nullable = false)
    @Builder.Default
    private Boolean isBillable = true; // Can be billed to insurance

    @Column(name = "requires_additional_info", nullable = false)
    @Builder.Default
    private Boolean requiresAdditionalInfo = false; // Requires extra documentation

    @Column(name = "insurance_notes", columnDefinition = "TEXT")
    private String insuranceNotes; // Special notes for insurance claims

    // ========== Status ==========
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true; // Active/deprecated status

    @Column(name = "effective_date")
    private java.time.LocalDate effectiveDate; // When code became effective

    @Column(name = "deprecated_date")
    private java.time.LocalDate deprecatedDate; // When code was deprecated

    @Column(name = "replaced_by_code", length = 10)
    private String replacedByCode; // If deprecated, what code replaces it

    // ========== Search and Display ==========
    @Column(name = "search_terms", columnDefinition = "TEXT")
    private String searchTerms; // Additional search keywords

    @Column(name = "short_description_id", length = 255)
    private String shortDescriptionId; // Abbreviated Indonesian description

    // ========== Notes ==========
    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes; // Clinical usage notes

    @Column(name = "coding_notes", columnDefinition = "TEXT")
    private String codingNotes; // Coding guidelines

    // ========== Business Methods ==========

    /**
     * Increment usage count when diagnosis is used.
     */
    public void incrementUsage() {
        this.usageCount++;
    }

    /**
     * Mark as common diagnosis if usage exceeds threshold.
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
}
