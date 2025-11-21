package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.VerificationStatus;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Prescription Verification Entity.
 *
 * Tracks pharmacist verification of prescriptions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "prescription_verification", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_prescription_verification_prescription", columnList = "prescription_id"),
        @Index(name = "idx_prescription_verification_pharmacist", columnList = "pharmacist_id"),
        @Index(name = "idx_prescription_verification_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PrescriptionVerification extends BaseEntity {

    /**
     * Prescription reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    /**
     * Pharmacist ID
     */
    @Column(name = "pharmacist_id", nullable = false)
    private UUID pharmacistId;

    /**
     * Pharmacist name
     */
    @Column(name = "pharmacist_name", length = 200)
    private String pharmacistName;

    /**
     * Verification status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private VerificationStatus status;

    /**
     * Verified at
     */
    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    /**
     * Drug interaction check performed
     */
    @Column(name = "interaction_check_performed")
    @Builder.Default
    private Boolean interactionCheckPerformed = false;

    /**
     * Interactions found
     */
    @Column(name = "interactions_found")
    private Boolean interactionsFound;

    /**
     * Interaction details
     */
    @Column(name = "interaction_details", columnDefinition = "TEXT")
    private String interactionDetails;

    /**
     * Dosage validation performed
     */
    @Column(name = "dosage_validation_performed")
    @Builder.Default
    private Boolean dosageValidationPerformed = false;

    /**
     * Dosage issues found
     */
    @Column(name = "dosage_issues_found")
    private Boolean dosageIssuesFound;

    /**
     * Dosage issues details
     */
    @Column(name = "dosage_issues", columnDefinition = "TEXT")
    private String dosageIssues;

    /**
     * Allergy check performed
     */
    @Column(name = "allergy_check_performed")
    @Builder.Default
    private Boolean allergyCheckPerformed = false;

    /**
     * Allergies found
     */
    @Column(name = "allergies_found")
    private Boolean allergiesFound;

    /**
     * Allergy details
     */
    @Column(name = "allergy_details", columnDefinition = "TEXT")
    private String allergyDetails;

    /**
     * Changes made during verification
     */
    @Column(name = "changes_made", columnDefinition = "TEXT")
    private String changesMade;

    /**
     * Rejection reason
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * Clarification needed
     */
    @Column(name = "clarification_needed", columnDefinition = "TEXT")
    private String clarificationNeeded;

    /**
     * Pharmacist comments
     */
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    /**
     * Verification checklist completed
     */
    @Column(name = "checklist_completed")
    @Builder.Default
    private Boolean checklistCompleted = false;

    /**
     * Dual verification required
     */
    @Column(name = "dual_verification_required")
    private Boolean dualVerificationRequired;

    /**
     * Second pharmacist ID
     */
    @Column(name = "second_pharmacist_id")
    private UUID secondPharmacistId;

    /**
     * Second pharmacist name
     */
    @Column(name = "second_pharmacist_name", length = 200)
    private String secondPharmacistName;

    /**
     * Second verification at
     */
    @Column(name = "second_verification_at")
    private LocalDateTime secondVerificationAt;

    /**
     * Check if verification found issues
     */
    public boolean hasIssues() {
        return (interactionsFound != null && interactionsFound) ||
               (dosageIssuesFound != null && dosageIssuesFound) ||
               (allergiesFound != null && allergiesFound);
    }

    /**
     * Check if verification is complete
     */
    public boolean isComplete() {
        return status != null && status.isComplete();
    }

    /**
     * Check if requires dual verification
     */
    public boolean requiresDualVerification() {
        return dualVerificationRequired != null && dualVerificationRequired &&
               (secondPharmacistId == null || secondVerificationAt == null);
    }

    /**
     * Check if all checks performed
     */
    public boolean allChecksPerformed() {
        return interactionCheckPerformed && dosageValidationPerformed && allergyCheckPerformed;
    }
}
