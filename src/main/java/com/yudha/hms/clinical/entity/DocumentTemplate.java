package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

/**
 * Document Template Entity.
 *
 * Defines templates for various clinical documents (Surat Keterangan Sehat,
 * Surat Sakit, Resume Medis, etc.) with support for auto-population of patient data.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "document_templates", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_doc_template_type", columnList = "document_type"),
        @Index(name = "idx_doc_template_code", columnList = "template_code", unique = true),
        @Index(name = "idx_doc_template_category", columnList = "category"),
        @Index(name = "idx_doc_template_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Templates for clinical documents with auto-population support")
public class DocumentTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Template Identification ==========
    @Column(name = "template_code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Template code is required")
    private String templateCode; // e.g., SURAT_SEHAT_001

    @Column(name = "template_name", nullable = false, length = 300)
    @NotBlank(message = "Template name is required")
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Column(name = "category", length = 50)
    private String category; // CERTIFICATE, LETTER, REPORT, CONSENT, PRESCRIPTION

    // ========== Template Content ==========
    @Column(name = "template_content", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Template content is required")
    private String templateContent; // HTML template with placeholders

    @Column(name = "header_template", columnDefinition = "TEXT")
    private String headerTemplate; // Hospital header/letterhead

    @Column(name = "footer_template", columnDefinition = "TEXT")
    private String footerTemplate; // Hospital footer

    @Column(name = "css_styles", columnDefinition = "TEXT")
    private String cssStyles; // CSS for PDF generation

    // ========== Placeholders and Auto-Population ==========
    @Column(name = "available_placeholders", columnDefinition = "TEXT")
    private String availablePlaceholders; // JSON array of available placeholders

    @Column(name = "required_fields", columnDefinition = "TEXT")
    private String requiredFields; // JSON array of required fields

    @Column(name = "auto_populate_fields", columnDefinition = "TEXT")
    private String autoPopulateFields; // JSON mapping for auto-population

    // ========== Signature Configuration ==========
    @Column(name = "requires_doctor_signature")
    @Builder.Default
    private Boolean requiresDoctorSignature = true;

    @Column(name = "requires_patient_signature")
    @Builder.Default
    private Boolean requiresPatientSignature = false;

    @Column(name = "requires_witness_signature")
    @Builder.Default
    private Boolean requiresWitnessSignature = false;

    @Column(name = "requires_hospital_stamp")
    @Builder.Default
    private Boolean requiresHospitalStamp = true;

    @Column(name = "signature_placeholder_positions", columnDefinition = "TEXT")
    private String signaturePlaceholderPositions; // JSON with X,Y coordinates

    // ========== Document Properties ==========
    @Column(name = "page_size", length = 20)
    @Builder.Default
    private String pageSize = "A4"; // A4, Letter, Legal

    @Column(name = "page_orientation", length = 20)
    @Builder.Default
    private String pageOrientation = "PORTRAIT"; // PORTRAIT, LANDSCAPE

    @Column(name = "number_of_copies")
    @Builder.Default
    private Integer numberOfCopies = 1;

    @Column(name = "watermark_text", length = 200)
    private String watermarkText;

    @Column(name = "include_barcode")
    @Builder.Default
    private Boolean includeBarcode = false;

    @Column(name = "include_qr_code")
    @Builder.Default
    private Boolean includeQrCode = false;

    // ========== Language and Localization ==========
    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "id_ID"; // id_ID, en_US

    @Column(name = "is_bilingual")
    @Builder.Default
    private Boolean isBilingual = false;

    // ========== Validity and Legal ==========
    @Column(name = "has_expiry")
    @Builder.Default
    private Boolean hasExpiry = false;

    @Column(name = "default_validity_days")
    private Integer defaultValidityDays;

    @Column(name = "legal_disclaimer", columnDefinition = "TEXT")
    private String legalDisclaimer;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    // ========== Usage and Status ==========
    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Long usageCount = 0L;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "is_official")
    @Builder.Default
    private Boolean isOfficial = false;

    // ========== Approval and Review ==========
    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approved_by_name", length = 200)
    private String approvedByName;

    @Column(name = "approved_at")
    private java.time.LocalDateTime approvedAt;

    @Column(name = "last_reviewed_date")
    private java.time.LocalDate lastReviewedDate;

    @Column(name = "review_frequency_months")
    private Integer reviewFrequencyMonths;

    // ========== Metadata ==========
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Document Type Enum ==========
    public enum DocumentType {
        // Health Certificates
        SURAT_KETERANGAN_SEHAT("Surat Keterangan Sehat", "Health Certificate"),
        SURAT_KETERANGAN_SAKIT("Surat Keterangan Sakit", "Sick Leave Certificate"),
        SURAT_KETERANGAN_RAWAT_INAP("Surat Keterangan Rawat Inap", "Hospitalization Certificate"),
        SURAT_KETERANGAN_SEHAT_KERJA("Surat Keterangan Sehat untuk Bekerja", "Fitness for Work Certificate"),

        // Medical Reports
        RESUME_MEDIS("Resume Medis", "Medical Resume"),
        SURAT_RUJUKAN("Surat Rujukan", "Referral Letter"),
        HASIL_LABORATORIUM("Hasil Laboratorium", "Laboratory Results"),
        HASIL_RADIOLOGI("Hasil Radiologi", "Radiology Results"),

        // Procedures and Consent
        INFORMED_CONSENT("Informed Consent", "Informed Consent"),
        PERSETUJUAN_TINDAKAN("Persetujuan Tindakan Medis", "Medical Procedure Consent"),
        LAPORAN_OPERASI("Laporan Operasi", "Operative Report"),

        // Prescriptions
        RESEP_OBAT("Resep Obat", "Prescription"),
        RESEP_OBAT_NARKOTIKA("Resep Obat Narkotika", "Narcotic Prescription"),

        // Administrative
        SURAT_KONTROL("Surat Kontrol", "Follow-up Control Letter"),
        SURAT_KETERANGAN_MENINGGAL("Surat Keterangan Kematian", "Death Certificate"),
        SURAT_PENGANTAR_PEMERIKSAAN("Surat Pengantar Pemeriksaan", "Examination Referral Letter"),

        // Insurance and Claims
        SURAT_ELIGIBILITAS_PESERTA("Surat Eligibilitas Peserta", "Insurance Eligibility Letter"),
        FORMULIR_SEP("Formulir SEP", "SEP Form (Insurance Authorization)"),

        // Birth and Death
        SURAT_KETERANGAN_LAHIR("Surat Keterangan Lahir", "Birth Certificate"),
        SURAT_KETERANGAN_LAHIR_MATI("Surat Keterangan Lahir Mati", "Stillbirth Certificate"),

        // Other
        SURAT_KETERANGAN_BEBAS_NARKOBA("Surat Keterangan Bebas Narkoba", "Drug-Free Certificate"),
        SURAT_KETERANGAN_BUTA_WARNA("Surat Keterangan Buta Warna", "Color Blindness Certificate"),
        SURAT_PENGANTAR_VISUM("Surat Pengantar Visum", "Medical Examination Request");

        private final String indonesianName;
        private final String englishName;

        DocumentType(String indonesianName, String englishName) {
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

    // ========== Business Methods ==========

    /**
     * Increment usage count.
     */
    public void incrementUsage() {
        this.usageCount++;
    }

    /**
     * Check if template needs review.
     */
    public boolean needsReview() {
        if (reviewFrequencyMonths == null || lastReviewedDate == null) {
            return false;
        }
        java.time.LocalDate nextReviewDate = lastReviewedDate.plusMonths(reviewFrequencyMonths);
        return java.time.LocalDate.now().isAfter(nextReviewDate);
    }

    /**
     * Approve template.
     */
    public void approve(UUID approverId, String approverName) {
        this.approvedById = approverId;
        this.approvedByName = approverName;
        this.approvedAt = java.time.LocalDateTime.now();
        this.isOfficial = true;
    }

    /**
     * Check if template is ready for use.
     */
    public boolean isReadyForUse() {
        return isActive && templateContent != null && !templateContent.isEmpty();
    }
}
