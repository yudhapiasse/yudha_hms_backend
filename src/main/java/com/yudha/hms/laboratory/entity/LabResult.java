package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.EntryMethod;
import com.yudha.hms.laboratory.constant.ResultStatus;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Result Entity.
 *
 * Laboratory test results with validation workflow.
 * Supports manual entry, LIS interface, and imported results.
 * Includes pathologist review, delta checks, and panic value handling.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_result", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_result_number", columnList = "result_number", unique = true),
        @Index(name = "idx_lab_result_order", columnList = "order_id"),
        @Index(name = "idx_lab_result_order_item", columnList = "order_item_id"),
        @Index(name = "idx_lab_result_specimen", columnList = "specimen_id"),
        @Index(name = "idx_lab_result_test", columnList = "test_id"),
        @Index(name = "idx_lab_result_status", columnList = "status"),
        @Index(name = "idx_lab_result_entered_at", columnList = "entered_at"),
        @Index(name = "idx_lab_result_validated_at", columnList = "validated_at"),
        @Index(name = "idx_lab_result_panic", columnList = "has_panic_values"),
        @Index(name = "idx_lab_result_lis", columnList = "lis_result_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabResult extends BaseEntity {

    /**
     * Result number (unique identifier)
     */
    @Column(name = "result_number", nullable = false, unique = true, length = 50)
    private String resultNumber;

    // ========== Order Reference ==========

    /**
     * Order reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder order;

    /**
     * Order item reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private LabOrderItem orderItem;

    /**
     * Specimen reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specimen_id", nullable = false)
    private Specimen specimen;

    // ========== Test Information (Denormalized) ==========

    /**
     * Test reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private LabTest test;

    /**
     * Test name (denormalized)
     */
    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    /**
     * Test code (denormalized)
     */
    @Column(name = "test_code", nullable = false, length = 50)
    private String testCode;

    // ========== Result Status ==========

    /**
     * Result status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ResultStatus status = ResultStatus.PENDING;

    // ========== Entry Information ==========

    /**
     * Entered timestamp
     */
    @Column(name = "entered_at")
    private LocalDateTime enteredAt;

    /**
     * Entered by user ID
     */
    @Column(name = "entered_by")
    private UUID enteredBy;

    /**
     * Entry method (MANUAL, INTERFACE, IMPORTED)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_method", length = 50)
    private EntryMethod entryMethod;

    // ========== Validation ==========

    /**
     * Validated timestamp
     */
    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    /**
     * Validated by user ID
     */
    @Column(name = "validated_by")
    private UUID validatedBy;

    /**
     * Validation notes
     */
    @Column(name = "validation_notes", columnDefinition = "TEXT")
    private String validationNotes;

    // ========== Pathologist Review ==========

    /**
     * Requires pathologist review
     */
    @Column(name = "requires_pathologist_review")
    @Builder.Default
    private Boolean requiresPathologistReview = false;

    /**
     * Reviewed by pathologist
     */
    @Column(name = "reviewed_by_pathologist")
    @Builder.Default
    private Boolean reviewedByPathologist = false;

    /**
     * Pathologist ID
     */
    @Column(name = "pathologist_id")
    private UUID pathologistId;

    /**
     * Pathologist reviewed timestamp
     */
    @Column(name = "pathologist_reviewed_at")
    private LocalDateTime pathologistReviewedAt;

    /**
     * Pathologist comments
     */
    @Column(name = "pathologist_comments", columnDefinition = "TEXT")
    private String pathologistComments;

    // ========== Result Interpretation ==========

    /**
     * Overall interpretation (NORMAL, ABNORMAL, CRITICAL)
     */
    @Column(name = "overall_interpretation", length = 50)
    private String overallInterpretation;

    /**
     * Clinical significance
     */
    @Column(name = "clinical_significance", columnDefinition = "TEXT")
    private String clinicalSignificance;

    /**
     * Recommendations
     */
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    // ========== Delta Check ==========

    /**
     * Delta check performed
     */
    @Column(name = "delta_check_performed")
    @Builder.Default
    private Boolean deltaCheckPerformed = false;

    /**
     * Delta check flagged (unusual change detected)
     */
    @Column(name = "delta_check_flagged")
    @Builder.Default
    private Boolean deltaCheckFlagged = false;

    /**
     * Delta check notes
     */
    @Column(name = "delta_check_notes", columnDefinition = "TEXT")
    private String deltaCheckNotes;

    /**
     * Previous result ID (for delta check comparison)
     */
    @Column(name = "previous_result_id")
    private UUID previousResultId;

    // ========== Panic/Critical Value ==========

    /**
     * Has panic values
     */
    @Column(name = "has_panic_values")
    @Builder.Default
    private Boolean hasPanicValues = false;

    /**
     * Panic value notified
     */
    @Column(name = "panic_value_notified")
    @Builder.Default
    private Boolean panicValueNotified = false;

    /**
     * Panic value notified timestamp
     */
    @Column(name = "panic_value_notified_at")
    private LocalDateTime panicValueNotifiedAt;

    /**
     * Panic value notified to (doctor/nurse name)
     */
    @Column(name = "panic_value_notified_to", length = 200)
    private String panicValueNotifiedTo;

    // ========== Amendment ==========

    /**
     * Is amended
     */
    @Column(name = "is_amended")
    @Builder.Default
    private Boolean isAmended = false;

    /**
     * Amended timestamp
     */
    @Column(name = "amended_at")
    private LocalDateTime amendedAt;

    /**
     * Amended by user ID
     */
    @Column(name = "amended_by")
    private UUID amendedBy;

    /**
     * Amendment reason
     */
    @Column(name = "amendment_reason", columnDefinition = "TEXT")
    private String amendmentReason;

    /**
     * Original result ID (if this is an amended version)
     */
    @Column(name = "original_result_id")
    private UUID originalResultId;

    // ========== LIS Interface ==========

    /**
     * LIS result ID (from laboratory information system)
     */
    @Column(name = "lis_result_id", length = 100)
    private String lisResultId;

    /**
     * LIS imported timestamp
     */
    @Column(name = "lis_imported_at")
    private LocalDateTime lisImportedAt;

    // ========== QC Information ==========

    /**
     * QC result ID
     */
    @Column(name = "qc_result_id")
    private UUID qcResultId;

    /**
     * QC status
     */
    @Column(name = "qc_status", length = 50)
    private String qcStatus;

    // ========== Report ==========

    /**
     * Report generated
     */
    @Column(name = "report_generated")
    @Builder.Default
    private Boolean reportGenerated = false;

    /**
     * Report generated timestamp
     */
    @Column(name = "report_generated_at")
    private LocalDateTime reportGeneratedAt;

    /**
     * Report sent to clinical
     */
    @Column(name = "report_sent_to_clinical")
    @Builder.Default
    private Boolean reportSentToClinical = false;

    /**
     * Report sent timestamp
     */
    @Column(name = "report_sent_at")
    private LocalDateTime reportSentAt;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Helper Methods ==========

    /**
     * Check if result is final
     */
    public boolean isFinal() {
        return status.isFinal();
    }

    /**
     * Check if result can be amended
     */
    public boolean canBeAmended() {
        return status.canBeAmended();
    }

    /**
     * Check if result requires pathologist review
     */
    public boolean needsPathologistReview() {
        return Boolean.TRUE.equals(requiresPathologistReview) &&
               Boolean.FALSE.equals(reviewedByPathologist);
    }

    /**
     * Check if result has panic values
     */
    public boolean hasPanicValues() {
        return Boolean.TRUE.equals(hasPanicValues);
    }

    /**
     * Check if delta check was flagged
     */
    public boolean isDeltaCheckFlagged() {
        return Boolean.TRUE.equals(deltaCheckFlagged);
    }

    /**
     * Check if result has been amended
     */
    public boolean isAmended() {
        return Boolean.TRUE.equals(isAmended);
    }

    /**
     * Check if report has been generated
     */
    public boolean hasReportGenerated() {
        return Boolean.TRUE.equals(reportGenerated);
    }
}
