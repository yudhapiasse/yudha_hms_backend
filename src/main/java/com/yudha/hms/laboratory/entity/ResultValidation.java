package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.ValidationLevel;
import com.yudha.hms.laboratory.constant.ValidationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Result Validation Entity.
 *
 * Multi-step result validation workflow.
 * Tracks validation by technicians, senior techs, pathologists, and clinical reviewers.
 * Supports approval, rejection, and repeat test workflows.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "result_validation", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_result_validation_result", columnList = "result_id"),
        @Index(name = "idx_result_validation_level", columnList = "validation_level"),
        @Index(name = "idx_result_validation_status", columnList = "validation_status"),
        @Index(name = "idx_result_validation_validated_at", columnList = "validated_at"),
        @Index(name = "idx_result_validation_validator", columnList = "validated_by")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultValidation {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Result reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private LabResult result;

    // ========== Validation Step ==========

    /**
     * Validation level (TECHNICIAN, SENIOR_TECH, PATHOLOGIST, CLINICAL_REVIEWER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_level", nullable = false, length = 50)
    private ValidationLevel validationLevel;

    /**
     * Validation step number
     */
    @Column(name = "validation_step", nullable = false)
    private Integer validationStep;

    // ========== Validator Information ==========

    /**
     * Validated by user ID
     */
    @Column(name = "validated_by", nullable = false)
    private UUID validatedBy;

    /**
     * Validator name
     */
    @Column(name = "validator_name", nullable = false, length = 200)
    private String validatorName;

    /**
     * Validated timestamp
     */
    @Column(name = "validated_at", nullable = false)
    @Builder.Default
    private LocalDateTime validatedAt = LocalDateTime.now();

    // ========== Validation Decision ==========

    /**
     * Validation status (APPROVED, REJECTED, NEEDS_REVIEW, NEEDS_REPEAT)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false, length = 50)
    private ValidationStatus validationStatus;

    /**
     * Validation notes
     */
    @Column(name = "validation_notes", columnDefinition = "TEXT")
    private String validationNotes;

    // ========== Issues Identified ==========

    /**
     * Issues identified during validation
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "issues_identified")
    private List<String> issuesIdentified;

    /**
     * Corrective action taken
     */
    @Column(name = "corrective_action", columnDefinition = "TEXT")
    private String correctiveAction;

    // ========== Digital Signature ==========

    /**
     * Digital signature data
     */
    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData;

    // ========== Audit Fields ==========

    /**
     * Created timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Version for optimistic locking
     */
    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ========== Helper Methods ==========

    /**
     * Check if validation is approved
     */
    public boolean isApproved() {
        return validationStatus == ValidationStatus.APPROVED;
    }

    /**
     * Check if validation is rejected
     */
    public boolean isRejected() {
        return validationStatus == ValidationStatus.REJECTED;
    }

    /**
     * Check if result needs review
     */
    public boolean needsReview() {
        return validationStatus == ValidationStatus.NEEDS_REVIEW;
    }

    /**
     * Check if test needs to be repeated
     */
    public boolean needsRepeat() {
        return validationStatus == ValidationStatus.NEEDS_REPEAT;
    }

    /**
     * Check if validation is complete
     */
    public boolean isComplete() {
        return validationStatus.isComplete();
    }
}
