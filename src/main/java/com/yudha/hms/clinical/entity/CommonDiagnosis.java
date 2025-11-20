package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

/**
 * Common Diagnosis Entity.
 *
 * Tracks frequently used diagnoses per department/polyclinic for quick selection.
 * Automatically updated based on usage patterns.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "common_diagnoses", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_common_diagnosis_dept", columnList = "department_code"),
        @Index(name = "idx_common_diagnosis_icd10", columnList = "icd10_code_id"),
        @Index(name = "idx_common_diagnosis_rank", columnList = "department_code, rank_order"),
        @Index(name = "idx_common_diagnosis_active", columnList = "is_active"),
        @Index(name = "idx_common_diagnosis_usage", columnList = "usage_count DESC")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_common_diagnosis_dept_icd10",
            columnNames = {"department_code", "icd10_code_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Common diagnoses per department for quick selection")
public class CommonDiagnosis extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Department/Polyclinic ==========
    @Column(name = "department_code", nullable = false, length = 20)
    @NotBlank(message = "Department code is required")
    private String departmentCode; // e.g., POLI_UMUM, POLI_GIGI, IGD

    @Column(name = "department_name", nullable = false, length = 200)
    @NotBlank(message = "Department name is required")
    private String departmentName; // Indonesian department name

    // ========== ICD-10 Code Reference ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icd10_code_id", nullable = false)
    @NotNull(message = "ICD-10 code is required")
    private ICD10Code icd10Code;

    // Denormalized for quick display
    @Column(name = "icd10_code", nullable = false, length = 10)
    @NotBlank(message = "ICD-10 code is required")
    private String icd10CodeValue;

    @Column(name = "icd10_description_id", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "ICD-10 Indonesian description is required")
    private String icd10DescriptionId;

    // ========== Ranking ==========
    @Column(name = "rank_order", nullable = false)
    @NotNull(message = "Rank order is required")
    @Positive(message = "Rank must be positive")
    @Builder.Default
    private Integer rankOrder = 1; // 1-10 for top 10 diagnoses

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Long usageCount = 0L; // Times this diagnosis was used in this dept

    @Column(name = "usage_percentage")
    private Double usagePercentage; // % of total diagnoses in this dept

    // ========== Configuration ==========
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true; // Enabled for quick selection

    @Column(name = "is_pinned", nullable = false)
    @Builder.Default
    private Boolean isPinned = false; // Manually pinned by admin

    @Column(name = "auto_calculated", nullable = false)
    @Builder.Default
    private Boolean autoCalculated = true; // True if calculated from usage stats

    // ========== Statistics ==========
    @Column(name = "last_used_date")
    private java.time.LocalDate lastUsedDate; // Last time diagnosis was used

    @Column(name = "last_recalculated_date")
    private java.time.LocalDate lastRecalculatedDate; // Last stats update

    @Column(name = "trend", length = 20)
    private String trend; // UP, DOWN, STABLE - usage trend

    // ========== Display Configuration ==========
    @Column(name = "display_order")
    private Integer displayOrder; // Custom display order (optional)

    @Column(name = "display_color", length = 10)
    private String displayColor; // UI color code (optional)

    @Column(name = "display_icon", length = 50)
    private String displayIcon; // UI icon (optional)

    // ========== Notes ==========
    @Column(name = "usage_notes", columnDefinition = "TEXT")
    private String usageNotes; // Why this is common in this department

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes; // Admin configuration notes

    // ========== Business Methods ==========

    /**
     * Increment usage count.
     */
    public void incrementUsage() {
        this.usageCount++;
        this.lastUsedDate = java.time.LocalDate.now();
    }

    /**
     * Update rank based on usage.
     */
    public void updateRank(int newRank) {
        this.rankOrder = newRank;
    }

    /**
     * Calculate usage percentage.
     */
    public void calculatePercentage(long totalDiagnoses) {
        if (totalDiagnoses > 0) {
            this.usagePercentage = (usageCount * 100.0) / totalDiagnoses;
        }
    }

    /**
     * Mark as manually pinned.
     */
    public void pin() {
        this.isPinned = true;
        this.autoCalculated = false;
    }

    /**
     * Unpin and allow auto-calculation.
     */
    public void unpin() {
        this.isPinned = false;
        this.autoCalculated = true;
    }

    /**
     * Check if in top 10.
     */
    public boolean isInTopTen() {
        return rankOrder != null && rankOrder <= 10;
    }

    /**
     * Update trend based on previous usage.
     */
    public void updateTrend(long previousUsageCount) {
        if (previousUsageCount < this.usageCount) {
            this.trend = "UP";
        } else if (previousUsageCount > this.usageCount) {
            this.trend = "DOWN";
        } else {
            this.trend = "STABLE";
        }
    }

    /**
     * Get display text for UI.
     */
    public String getDisplayText() {
        return String.format("#%d - %s - %s", rankOrder, icd10CodeValue, icd10DescriptionId);
    }
}
