package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.DocumentType;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Claim Document Entity.
 *
 * Represents supporting documents attached to insurance claims.
 * Documents are stored as file paths or URLs to external storage.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "claim_document", schema = "billing_schema", indexes = {
        @Index(name = "idx_claim_document_claim", columnList = "claim_id"),
        @Index(name = "idx_claim_document_type", columnList = "document_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClaimDocument extends BaseEntity {

    /**
     * Parent insurance claim
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private InsuranceClaim claim;

    /**
     * Document type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    /**
     * Document name/title
     */
    @Column(name = "document_name", nullable = false, length = 200)
    private String documentName;

    /**
     * Original filename
     */
    @Column(name = "file_name", length = 255)
    private String fileName;

    /**
     * File path or storage key
     */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * File URL (if stored in external storage)
     */
    @Column(name = "file_url", length = 500)
    private String fileUrl;

    /**
     * File size in bytes
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * MIME type
     */
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * Document description
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Upload date
     */
    @Column(name = "upload_date", nullable = false)
    @Builder.Default
    private LocalDateTime uploadDate = LocalDateTime.now();

    /**
     * Uploaded by
     */
    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    /**
     * Document verified flag
     */
    @Column(name = "verified")
    private Boolean verified;

    /**
     * Verified by
     */
    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    /**
     * Verification date
     */
    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Helper method to verify document
     *
     * @param verifiedBy user verifying
     */
    public void verify(String verifiedBy) {
        this.verified = true;
        this.verifiedBy = verifiedBy;
        this.verificationDate = LocalDateTime.now();
    }

    /**
     * Helper method to check if file is an image
     *
     * @return true if image file
     */
    public boolean isImage() {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("image/");
    }

    /**
     * Helper method to check if file is a PDF
     *
     * @return true if PDF file
     */
    public boolean isPdf() {
        return "application/pdf".equalsIgnoreCase(mimeType);
    }

    /**
     * Helper method to get file extension
     *
     * @return file extension
     */
    public String getFileExtension() {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
