package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.ClaimStatus;
import com.yudha.hms.billing.constant.ClaimType;
import com.yudha.hms.billing.constant.RejectionReason;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Insurance Claim Entity.
 *
 * Represents an insurance claim for patient medical services.
 * Tracks the complete claim lifecycle from submission to payment.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "insurance_claim", schema = "billing_schema", indexes = {
        @Index(name = "idx_claim_number", columnList = "claim_number", unique = true),
        @Index(name = "idx_claim_patient", columnList = "patient_id"),
        @Index(name = "idx_claim_insurance", columnList = "insurance_company_id"),
        @Index(name = "idx_claim_invoice", columnList = "invoice_id"),
        @Index(name = "idx_claim_status", columnList = "status"),
        @Index(name = "idx_claim_submission_date", columnList = "submission_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsuranceClaim extends SoftDeletableEntity {

    /**
     * Claim number (unique identifier)
     */
    @Column(name = "claim_number", nullable = false, unique = true, length = 50)
    private String claimNumber;

    /**
     * Insurance company
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_company_id", nullable = false)
    private InsuranceCompany insuranceCompany;

    /**
     * Related invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    /**
     * Patient ID
     */
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    /**
     * Patient MRN
     */
    @Column(name = "patient_mrn", nullable = false, length = 50)
    private String patientMrn;

    /**
     * Patient name
     */
    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    /**
     * Policy number
     */
    @Column(name = "policy_number", nullable = false, length = 100)
    private String policyNumber;

    /**
     * Policy holder name
     */
    @Column(name = "policy_holder_name", length = 200)
    private String policyHolderName;

    /**
     * Relationship to policy holder (e.g., "Self", "Spouse", "Child")
     */
    @Column(name = "relationship_to_holder", length = 50)
    private String relationshipToHolder;

    /**
     * Claim type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false, length = 50)
    private ClaimType claimType;

    /**
     * Service start date
     */
    @Column(name = "service_start_date", nullable = false)
    private LocalDate serviceStartDate;

    /**
     * Service end date
     */
    @Column(name = "service_end_date", nullable = false)
    private LocalDate serviceEndDate;

    /**
     * Diagnosis codes (ICD-10) - comma separated
     */
    @Column(name = "diagnosis_codes", length = 500)
    private String diagnosisCodes;

    /**
     * Primary diagnosis description
     */
    @Column(name = "primary_diagnosis", length = 500)
    private String primaryDiagnosis;

    /**
     * Procedure codes (ICD-9) - comma separated
     */
    @Column(name = "procedure_codes", length = 500)
    private String procedureCodes;

    /**
     * Treating physician ID
     */
    @Column(name = "treating_physician_id")
    private UUID treatingPhysicianId;

    /**
     * Treating physician name
     */
    @Column(name = "treating_physician_name", length = 200)
    private String treatingPhysicianName;

    /**
     * Claim amount requested
     */
    @Column(name = "claim_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal claimAmount;

    /**
     * Approved amount
     */
    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    /**
     * Paid amount
     */
    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    /**
     * Patient responsibility amount
     */
    @Column(name = "patient_responsibility", precision = 15, scale = 2)
    private BigDecimal patientResponsibility;

    /**
     * Coverage percentage
     */
    @Column(name = "coverage_percentage", precision = 5, scale = 2)
    private BigDecimal coveragePercentage;

    /**
     * Claim status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.DRAFT;

    /**
     * Submission date
     */
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    /**
     * Submitted by
     */
    @Column(name = "submitted_by", length = 100)
    private String submittedBy;

    /**
     * Review start date
     */
    @Column(name = "review_start_date")
    private LocalDateTime reviewStartDate;

    /**
     * Approval date
     */
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    /**
     * Approved by (insurance reviewer name)
     */
    @Column(name = "approved_by", length = 200)
    private String approvedBy;

    /**
     * Payment date
     */
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    /**
     * Payment reference number
     */
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    /**
     * Rejection date
     */
    @Column(name = "rejection_date")
    private LocalDateTime rejectionDate;

    /**
     * Rejection reason
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "rejection_reason", length = 50)
    private RejectionReason rejectionReason;

    /**
     * Rejection notes
     */
    @Column(name = "rejection_notes", columnDefinition = "TEXT")
    private String rejectionNotes;

    /**
     * Appeal date
     */
    @Column(name = "appeal_date")
    private LocalDateTime appealDate;

    /**
     * Appeal reason
     */
    @Column(name = "appeal_reason", columnDefinition = "TEXT")
    private String appealReason;

    /**
     * Pre-authorization number
     */
    @Column(name = "pre_authorization_number", length = 100)
    private String preAuthorizationNumber;

    /**
     * Pre-authorization date
     */
    @Column(name = "pre_authorization_date")
    private LocalDate preAuthorizationDate;

    /**
     * Requires Coordination of Benefits (COB)
     */
    @Column(name = "requires_cob")
    private Boolean requiresCob;

    /**
     * Primary insurance claim number (for COB cases)
     */
    @Column(name = "primary_claim_number", length = 50)
    private String primaryClaimNumber;

    /**
     * Primary insurance company name (for COB cases)
     */
    @Column(name = "primary_insurance_company", length = 200)
    private String primaryInsuranceCompany;

    /**
     * Primary insurance paid amount (for COB cases)
     */
    @Column(name = "primary_insurance_paid", precision = 15, scale = 2)
    private BigDecimal primaryInsurancePaid;

    /**
     * Insurance reviewer name
     */
    @Column(name = "reviewer_name", length = 200)
    private String reviewerName;

    /**
     * Insurance reviewer phone
     */
    @Column(name = "reviewer_phone", length = 50)
    private String reviewerPhone;

    /**
     * Insurance reviewer email
     */
    @Column(name = "reviewer_email", length = 100)
    private String reviewerEmail;

    /**
     * Special notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Internal notes (not visible in claim form)
     */
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;

    /**
     * Claim items (line items)
     */
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ClaimItem> items = new ArrayList<>();

    /**
     * Supporting documents
     */
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ClaimDocument> documents = new ArrayList<>();

    /**
     * Add claim item
     *
     * @param item claim item
     */
    public void addItem(ClaimItem item) {
        items.add(item);
        item.setClaim(this);
    }

    /**
     * Remove claim item
     *
     * @param item claim item
     */
    public void removeItem(ClaimItem item) {
        items.remove(item);
        item.setClaim(null);
    }

    /**
     * Add document
     *
     * @param document claim document
     */
    public void addDocument(ClaimDocument document) {
        documents.add(document);
        document.setClaim(this);
    }

    /**
     * Remove document
     *
     * @param document claim document
     */
    public void removeDocument(ClaimDocument document) {
        documents.remove(document);
        document.setClaim(null);
    }

    /**
     * Helper method to calculate total claim amount from items
     */
    public void calculateClaimAmount() {
        this.claimAmount = items.stream()
                .map(ClaimItem::getClaimAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Helper method to check if all mandatory documents are uploaded
     *
     * @return true if all mandatory documents present
     */
    public boolean hasMandatoryDocuments() {
        // Check if basic mandatory documents are present
        return documents.stream()
                .map(ClaimDocument::getDocumentType)
                .anyMatch(type -> type.isMandatory());
    }

    /**
     * Helper method to submit claim
     *
     * @param submittedBy user submitting the claim
     */
    public void submit(String submittedBy) {
        if (status != ClaimStatus.DRAFT) {
            throw new IllegalStateException("Only draft claims can be submitted");
        }
        this.status = ClaimStatus.SUBMITTED;
        this.submissionDate = LocalDateTime.now();
        this.submittedBy = submittedBy;
    }

    /**
     * Helper method to approve claim
     *
     * @param approvedAmount approved amount
     * @param approvedBy user approving
     */
    public void approve(BigDecimal approvedAmount, String approvedBy) {
        if (status != ClaimStatus.UNDER_REVIEW && status != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted or under-review claims can be approved");
        }
        this.status = ClaimStatus.APPROVED;
        this.approvedAmount = approvedAmount;
        this.approvalDate = LocalDateTime.now();
        this.approvedBy = approvedBy;

        // Calculate patient responsibility
        if (this.claimAmount != null && approvedAmount != null) {
            this.patientResponsibility = this.claimAmount.subtract(approvedAmount);
        }
    }

    /**
     * Helper method to reject claim
     *
     * @param reason rejection reason
     * @param notes rejection notes
     */
    public void reject(RejectionReason reason, String notes) {
        if (status != ClaimStatus.UNDER_REVIEW && status != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException("Only submitted or under-review claims can be rejected");
        }
        this.status = ClaimStatus.REJECTED;
        this.rejectionDate = LocalDateTime.now();
        this.rejectionReason = reason;
        this.rejectionNotes = notes;
    }

    /**
     * Helper method to record payment
     *
     * @param paidAmount amount paid
     * @param paymentRef payment reference
     */
    public void recordPayment(BigDecimal paidAmount, String paymentRef) {
        if (status != ClaimStatus.APPROVED && status != ClaimStatus.PARTIALLY_APPROVED) {
            throw new IllegalStateException("Only approved claims can have payments recorded");
        }
        this.status = ClaimStatus.PAID;
        this.paidAmount = paidAmount;
        this.paymentDate = LocalDateTime.now();
        this.paymentReference = paymentRef;
    }
}
