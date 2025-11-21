package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.ClaimStatus;
import com.yudha.hms.billing.constant.ClaimType;
import com.yudha.hms.billing.constant.DocumentType;
import com.yudha.hms.billing.constant.RejectionReason;
import com.yudha.hms.billing.constant.TariffType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for insurance claim responses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceClaimResponse {

    private UUID id;
    private String claimNumber;

    // Insurance company information
    private UUID insuranceCompanyId;
    private String insuranceCompanyName;

    // Invoice information
    private UUID invoiceId;
    private String invoiceNumber;

    // Patient information
    private UUID patientId;
    private String patientMrn;
    private String patientName;

    // Policy information
    private String policyNumber;
    private String policyHolderName;
    private String relationshipToHolder;

    // Claim details
    private ClaimType claimType;
    private LocalDate serviceStartDate;
    private LocalDate serviceEndDate;

    // Diagnosis and procedure
    private String diagnosisCodes;
    private String primaryDiagnosis;
    private String procedureCodes;

    // Provider information
    private UUID treatingPhysicianId;
    private String treatingPhysicianName;

    // Financial information
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private BigDecimal paidAmount;
    private BigDecimal patientResponsibility;
    private BigDecimal coveragePercentage;

    // Status and dates
    private ClaimStatus status;
    private LocalDateTime submissionDate;
    private String submittedBy;
    private LocalDateTime reviewStartDate;
    private LocalDateTime approvalDate;
    private String approvedBy;
    private LocalDateTime paymentDate;
    private String paymentReference;

    // Rejection information
    private LocalDateTime rejectionDate;
    private RejectionReason rejectionReason;
    private String rejectionNotes;

    // Appeal information
    private LocalDateTime appealDate;
    private String appealReason;

    // Pre-authorization
    private String preAuthorizationNumber;
    private LocalDate preAuthorizationDate;

    // Coordination of Benefits (COB)
    private Boolean requiresCob;
    private String primaryClaimNumber;
    private String primaryInsuranceCompany;
    private BigDecimal primaryInsurancePaid;

    // Reviewer information
    private String reviewerName;
    private String reviewerPhone;
    private String reviewerEmail;

    // Notes
    private String notes;
    private String internalNotes;

    // Items and documents
    private List<ClaimItemResponse> items;
    private List<ClaimDocumentResponse> documents;

    // Audit fields
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * Nested DTO for claim items
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimItemResponse {
        private UUID id;
        private Integer lineNumber;
        private LocalDate serviceDate;
        private TariffType itemType;
        private String itemCode;
        private String itemDescription;
        private String diagnosisCode;
        private String procedureCode;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private BigDecimal claimAmount;
        private BigDecimal approvedAmount;
        private String rejectionReason;
        private String providerName;
        private String departmentName;
        private String notes;
    }

    /**
     * Nested DTO for claim documents
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimDocumentResponse {
        private UUID id;
        private DocumentType documentType;
        private String documentName;
        private String fileName;
        private String fileUrl;
        private Long fileSize;
        private String mimeType;
        private String description;
        private LocalDateTime uploadDate;
        private String uploadedBy;
        private Boolean verified;
        private String verifiedBy;
        private LocalDateTime verificationDate;
    }
}
