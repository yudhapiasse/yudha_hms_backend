package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clinical Document Entity.
 *
 * Represents generated clinical documents with versioning, audit trail,
 * and PDF generation support. Implements document lifecycle management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "clinical_documents", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_clinical_doc_number", columnList = "document_number", unique = true),
        @Index(name = "idx_clinical_doc_patient", columnList = "patient_id"),
        @Index(name = "idx_clinical_doc_encounter", columnList = "encounter_id"),
        @Index(name = "idx_clinical_doc_template", columnList = "template_id"),
        @Index(name = "idx_clinical_doc_type", columnList = "document_type"),
        @Index(name = "idx_clinical_doc_status", columnList = "document_status"),
        @Index(name = "idx_clinical_doc_date", columnList = "document_date"),
        @Index(name = "idx_clinical_doc_version", columnList = "parent_document_id, document_version")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Generated clinical documents with versioning and PDF support")
public class ClinicalDocument extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Document Identification ==========
    @Column(name = "document_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Document number is required")
    private String documentNumber; // DOC-SEHAT-20251120-0001

    @Column(name = "document_title", nullable = false, length = 300)
    @NotBlank(message = "Document title is required")
    private String documentTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    @NotNull(message = "Document type is required")
    private DocumentTemplate.DocumentType documentType;

    // ========== Template Reference ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    @NotNull(message = "Template is required")
    private DocumentTemplate template;

    // ========== Patient and Encounter References ==========
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    @NotBlank(message = "Patient name is required")
    private String patientName;

    @Column(name = "patient_medical_record_number", length = 50)
    private String patientMedicalRecordNumber;

    @Column(name = "encounter_id")
    private UUID encounterId;

    // ========== Document Content ==========
    @Column(name = "document_content", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Document content is required")
    private String documentContent; // HTML content with populated fields

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData; // JSON of all field values for regeneration

    // ========== Versioning ==========
    @Column(name = "document_version", nullable = false)
    @Builder.Default
    private Integer documentVersion = 1;

    @Column(name = "parent_document_id")
    private UUID parentDocumentId; // Link to previous version

    @Column(name = "is_latest_version", nullable = false)
    @Builder.Default
    private Boolean isLatestVersion = true;

    @Column(name = "revision_reason", columnDefinition = "TEXT")
    private String revisionReason;

    // ========== Document Dates ==========
    @Column(name = "document_date", nullable = false)
    @NotNull(message = "Document date is required")
    private LocalDate documentDate;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    // ========== Document Status ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "document_status", nullable = false, length = 30)
    @NotNull(message = "Document status is required")
    @Builder.Default
    private DocumentStatus documentStatus = DocumentStatus.DRAFT;

    // ========== PDF Generation ==========
    @Column(name = "pdf_generated")
    @Builder.Default
    private Boolean pdfGenerated = false;

    @Column(name = "pdf_file_path", length = 500)
    private String pdfFilePath;

    @Column(name = "pdf_file_size")
    private Long pdfFileSize; // in bytes

    @Column(name = "pdf_generated_at")
    private LocalDateTime pdfGeneratedAt;

    @Column(name = "pdf_download_count")
    @Builder.Default
    private Integer pdfDownloadCount = 0;

    // ========== Creator/Issuer Information ==========
    @Column(name = "issued_by_id", nullable = false)
    @NotNull(message = "Issuer ID is required")
    private UUID issuedById;

    @Column(name = "issued_by_name", nullable = false, length = 200)
    @NotBlank(message = "Issuer name is required")
    private String issuedByName;

    @Column(name = "issued_by_title", length = 100)
    private String issuedByTitle; // Dr., Sp.PD, etc.

    @Column(name = "issued_by_license_number", length = 50)
    private String issuedByLicenseNumber; // SIP number

    // ========== Digital Signatures ==========
    @Column(name = "requires_signatures")
    @Builder.Default
    private Boolean requiresSignatures = true;

    @Column(name = "all_signatures_collected")
    @Builder.Default
    private Boolean allSignaturesCollected = false;

    @Column(name = "signature_count")
    @Builder.Default
    private Integer signatureCount = 0;

    // ========== Verification and Validation ==========
    @Column(name = "verified")
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "verified_by_id")
    private UUID verifiedById;

    @Column(name = "verified_by_name", length = 200)
    private String verifiedByName;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verification_code", length = 50)
    private String verificationCode; // For QR code verification

    // ========== Printing and Distribution ==========
    @Column(name = "printed")
    @Builder.Default
    private Boolean printed = false;

    @Column(name = "print_count")
    @Builder.Default
    private Integer printCount = 0;

    @Column(name = "last_printed_at")
    private LocalDateTime lastPrintedAt;

    @Column(name = "sent_to_patient")
    @Builder.Default
    private Boolean sentToPatient = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivery_method", length = 50)
    private String deliveryMethod; // EMAIL, SMS, PRINT, PORTAL

    // ========== Cancellation and Void ==========
    @Column(name = "voided")
    @Builder.Default
    private Boolean voided = false;

    @Column(name = "voided_at")
    private LocalDateTime voidedAt;

    @Column(name = "voided_by_id")
    private UUID voidedById;

    @Column(name = "voided_by_name", length = 200)
    private String voidedByName;

    @Column(name = "void_reason", columnDefinition = "TEXT")
    private String voidReason;

    // ========== Security and Access ==========
    @Column(name = "is_confidential")
    @Builder.Default
    private Boolean isConfidential = false;

    @Column(name = "access_level", length = 20)
    @Builder.Default
    private String accessLevel = "NORMAL"; // PUBLIC, NORMAL, RESTRICTED, CONFIDENTIAL

    @Column(name = "watermark_applied")
    @Builder.Default
    private Boolean watermarkApplied = false;

    // ========== External References ==========
    @Column(name = "reference_number", length = 100)
    private String referenceNumber; // External reference (insurance, etc.)

    @Column(name = "related_documents", columnDefinition = "TEXT")
    private String relatedDocuments; // JSON array of related document IDs

    // ========== Metadata ==========
    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose; // Purpose of document issuance

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags for categorization

    // ========== Document Status Enum ==========
    public enum DocumentStatus {
        DRAFT,          // Being created
        PENDING_REVIEW, // Awaiting review
        PENDING_SIGNATURE, // Awaiting signatures
        APPROVED,       // Approved but not issued
        ISSUED,         // Issued to patient
        COMPLETED,      // Fully completed with all signatures
        VOIDED,         // Cancelled/voided
        EXPIRED,        // Past expiry date
        SUPERSEDED      // Replaced by newer version
    }

    // ========== Business Methods ==========

    /**
     * Create new version of this document.
     */
    public ClinicalDocument createNewVersion(String reason) {
        this.isLatestVersion = false;
        this.documentStatus = DocumentStatus.SUPERSEDED;

        return ClinicalDocument.builder()
                .parentDocumentId(this.id)
                .documentVersion(this.documentVersion + 1)
                .revisionReason(reason)
                .documentNumber(generateVersionedDocumentNumber())
                .documentTitle(this.documentTitle)
                .documentType(this.documentType)
                .template(this.template)
                .patientId(this.patientId)
                .patientName(this.patientName)
                .encounterId(this.encounterId)
                .issuedById(this.issuedById)
                .issuedByName(this.issuedByName)
                .documentStatus(DocumentStatus.DRAFT)
                .isLatestVersion(true)
                .build();
    }

    /**
     * Generate versioned document number.
     */
    private String generateVersionedDocumentNumber() {
        return this.documentNumber + "-V" + (this.documentVersion + 1);
    }

    /**
     * Mark as issued.
     */
    public void issue() {
        this.documentStatus = DocumentStatus.ISSUED;
        this.issuedDate = LocalDateTime.now();
    }

    /**
     * Void the document.
     */
    public void voidDocument(UUID userId, String userName, String reason) {
        this.voided = true;
        this.documentStatus = DocumentStatus.VOIDED;
        this.voidedAt = LocalDateTime.now();
        this.voidedById = userId;
        this.voidedByName = userName;
        this.voidReason = reason;
    }

    /**
     * Mark PDF as generated.
     */
    public void markPdfGenerated(String filePath, long fileSize) {
        this.pdfGenerated = true;
        this.pdfFilePath = filePath;
        this.pdfFileSize = fileSize;
        this.pdfGeneratedAt = LocalDateTime.now();
    }

    /**
     * Increment print count.
     */
    public void recordPrint() {
        this.printed = true;
        this.printCount++;
        this.lastPrintedAt = LocalDateTime.now();
    }

    /**
     * Increment PDF download count.
     */
    public void recordDownload() {
        this.pdfDownloadCount++;
    }

    /**
     * Check if document is expired.
     */
    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Check if document is valid.
     */
    public boolean isValid() {
        return documentStatus == DocumentStatus.ISSUED &&
               !voided &&
               !isExpired() &&
               (allSignaturesCollected || !requiresSignatures);
    }

    /**
     * Verify document.
     */
    public void verify(UUID verifierId, String verifierName, String code) {
        this.verified = true;
        this.verifiedById = verifierId;
        this.verifiedByName = verifierName;
        this.verifiedAt = LocalDateTime.now();
        this.verificationCode = code;
    }
}
