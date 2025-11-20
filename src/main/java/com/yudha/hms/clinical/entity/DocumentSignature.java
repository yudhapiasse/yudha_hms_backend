package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Document Signature Entity.
 *
 * Tracks digital signatures on clinical documents with support for
 * multiple signers and signature verification.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "document_signatures", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_doc_signature_document", columnList = "document_id"),
        @Index(name = "idx_doc_signature_signer", columnList = "signer_id"),
        @Index(name = "idx_doc_signature_status", columnList = "signature_status"),
        @Index(name = "idx_doc_signature_role", columnList = "signer_role"),
        @Index(name = "idx_doc_signature_date", columnList = "signed_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Digital signatures for clinical documents")
public class DocumentSignature extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Document Reference ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @NotNull(message = "Document is required")
    private ClinicalDocument document;

    // ========== Signer Information ==========
    @Column(name = "signer_id", nullable = false)
    @NotNull(message = "Signer ID is required")
    private UUID signerId;

    @Column(name = "signer_name", nullable = false, length = 200)
    @NotBlank(message = "Signer name is required")
    private String signerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "signer_role", nullable = false, length = 50)
    @NotNull(message = "Signer role is required")
    private SignerRole signerRole;

    @Column(name = "signer_title", length = 100)
    private String signerTitle; // Dr., Sp.PD, etc.

    @Column(name = "signer_license_number", length = 50)
    private String signerLicenseNumber; // SIP, STR number

    @Column(name = "signer_specialization", length = 100)
    private String signerSpecialization;

    // ========== Signature Details ==========
    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData; // Base64 encoded signature image or digital signature

    @Column(name = "signature_method", length = 50)
    @Builder.Default
    private String signatureMethod = "DIGITAL"; // DIGITAL, HANDWRITTEN_SCAN, BIOMETRIC, PKI

    @Column(name = "signature_location", length = 200)
    private String signatureLocation; // Where signature was collected (IP, device)

    @Column(name = "signature_device", length = 100)
    private String signatureDevice; // Device used for signing

    // ========== Signature Status ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "signature_status", nullable = false, length = 30)
    @NotNull(message = "Signature status is required")
    @Builder.Default
    private SignatureStatus signatureStatus = SignatureStatus.PENDING;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "sequence_number")
    private Integer sequenceNumber; // Order of signature collection

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = true;

    // ========== PKI and Cryptographic Verification ==========
    @Column(name = "certificate_serial_number", length = 100)
    private String certificateSerialNumber;

    @Column(name = "certificate_issuer", length = 200)
    private String certificateIssuer;

    @Column(name = "certificate_valid_from")
    private LocalDateTime certificateValidFrom;

    @Column(name = "certificate_valid_until")
    private LocalDateTime certificateValidUntil;

    @Column(name = "signature_hash", length = 256)
    private String signatureHash; // Hash of signed content

    @Column(name = "signature_algorithm", length = 50)
    private String signatureAlgorithm; // RSA, ECDSA, etc.

    // ========== Verification ==========
    @Column(name = "verified")
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verification_method", length = 50)
    private String verificationMethod;

    @Column(name = "verification_result", columnDefinition = "TEXT")
    private String verificationResult;

    // ========== Rejection and Revocation ==========
    @Column(name = "rejected")
    @Builder.Default
    private Boolean rejected = false;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "revoked")
    @Builder.Default
    private Boolean revoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revocation_reason", columnDefinition = "TEXT")
    private String revocationReason;

    // ========== Consent and Authorization ==========
    @Column(name = "consent_given")
    @Builder.Default
    private Boolean consentGiven = false;

    @Column(name = "consent_statement", columnDefinition = "TEXT")
    private String consentStatement;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // ========== Placeholder Position (for PDF) ==========
    @Column(name = "placeholder_x")
    private Integer placeholderX;

    @Column(name = "placeholder_y")
    private Integer placeholderY;

    @Column(name = "placeholder_page")
    private Integer placeholderPage;

    // ========== Metadata ==========
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Enumerations ==========

    public enum SignerRole {
        DOCTOR("Dokter", "Doctor"),
        PATIENT("Pasien", "Patient"),
        GUARDIAN("Wali/Keluarga", "Guardian/Family"),
        WITNESS("Saksi", "Witness"),
        NURSE("Perawat", "Nurse"),
        ADMINISTRATOR("Administrator", "Administrator"),
        HOSPITAL_DIRECTOR("Direktur RS", "Hospital Director"),
        HEAD_OF_DEPARTMENT("Kepala Bagian", "Head of Department");

        private final String indonesianName;
        private final String englishName;

        SignerRole(String indonesianName, String englishName) {
            this.indonesianName = indonesianName;
            this.englishName = englishName;
        }

        public String getIndonesianName() {
            return indonesianName;
        }

        public String getEnglishName() {
            return englishName;
        }
    }

    public enum SignatureStatus {
        PENDING,        // Awaiting signature
        REQUESTED,      // Signature request sent
        SIGNED,         // Signature collected
        VERIFIED,       // Signature verified
        REJECTED,       // Signature rejected
        REVOKED,        // Signature revoked
        EXPIRED         // Signature expired
    }

    // ========== Business Methods ==========

    /**
     * Sign the document.
     */
    public void sign(String signatureData, String method, String location) {
        this.signatureData = signatureData;
        this.signatureMethod = method;
        this.signatureLocation = location;
        this.signatureStatus = SignatureStatus.SIGNED;
        this.signedAt = LocalDateTime.now();
    }

    /**
     * Verify the signature.
     */
    public void verify(String method, String result) {
        this.verified = true;
        this.verifiedAt = LocalDateTime.now();
        this.verificationMethod = method;
        this.verificationResult = result;
        this.signatureStatus = SignatureStatus.VERIFIED;
    }

    /**
     * Reject the signature.
     */
    public void reject(String reason) {
        this.rejected = true;
        this.rejectedAt = LocalDateTime.now();
        this.rejectionReason = reason;
        this.signatureStatus = SignatureStatus.REJECTED;
    }

    /**
     * Revoke the signature.
     */
    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
        this.revocationReason = reason;
        this.signatureStatus = SignatureStatus.REVOKED;
    }

    /**
     * Check if signature is valid.
     */
    public boolean isValid() {
        return signatureStatus == SignatureStatus.SIGNED ||
               signatureStatus == SignatureStatus.VERIFIED;
    }

    /**
     * Check if certificate is valid.
     */
    public boolean isCertificateValid() {
        if (certificateValidFrom == null || certificateValidUntil == null) {
            return true; // No certificate validation required
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(certificateValidFrom) && now.isBefore(certificateValidUntil);
    }
}
