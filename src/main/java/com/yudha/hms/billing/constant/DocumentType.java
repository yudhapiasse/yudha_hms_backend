package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Document Type enumeration for Insurance Claim Management.
 *
 * Types of supporting documents for insurance claims:
 * - CLAIM_FORM: Insurance claim form
 * - INVOICE: Hospital invoice
 * - RECEIPT: Payment receipt
 * - MEDICAL_RECORD: Medical record/chart
 * - DIAGNOSIS_REPORT: Diagnosis report
 * - LAB_RESULT: Laboratory test results
 * - RADIOLOGY_REPORT: Radiology examination report
 * - PRESCRIPTION: Medication prescription
 * - SURGERY_REPORT: Surgical operation report
 * - DISCHARGE_SUMMARY: Hospital discharge summary
 * - REFERRAL_LETTER: Referral letter from referring physician
 * - CONSENT_FORM: Patient consent form
 * - INSURANCE_CARD: Copy of insurance card
 * - ID_CARD: Copy of patient ID card
 * - AUTHORIZATION: Pre-authorization or approval letter
 * - OTHER: Other supporting documents
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum DocumentType {
    CLAIM_FORM("Formulir Klaim", "CLAIM_FORM", "Insurance claim form", true),
    INVOICE("Faktur", "INVOICE", "Hospital invoice", true),
    RECEIPT("Kwitansi", "RECEIPT", "Payment receipt", true),
    MEDICAL_RECORD("Rekam Medis", "MEDICAL_RECORD", "Medical record/chart", false),
    DIAGNOSIS_REPORT("Laporan Diagnosis", "DIAGNOSIS", "Diagnosis report", true),
    LAB_RESULT("Hasil Lab", "LAB_RESULT", "Laboratory test results", false),
    RADIOLOGY_REPORT("Hasil Radiologi", "RADIOLOGY", "Radiology examination report", false),
    PRESCRIPTION("Resep", "PRESCRIPTION", "Medication prescription", false),
    SURGERY_REPORT("Laporan Operasi", "SURGERY_REPORT", "Surgical operation report", false),
    DISCHARGE_SUMMARY("Resume Keluar", "DISCHARGE_SUMMARY", "Hospital discharge summary", false),
    REFERRAL_LETTER("Surat Rujukan", "REFERRAL", "Referral letter", false),
    CONSENT_FORM("Form Persetujuan", "CONSENT", "Patient consent form", false),
    INSURANCE_CARD("Kartu Asuransi", "INSURANCE_CARD", "Copy of insurance card", true),
    ID_CARD("KTP", "ID_CARD", "Copy of patient ID card", true),
    AUTHORIZATION("Surat Autorisasi", "AUTHORIZATION", "Pre-authorization letter", false),
    POLICE_REPORT("Laporan Polisi", "POLICE_REPORT", "Police report for accident cases", false),
    DEATH_CERTIFICATE("Surat Kematian", "DEATH_CERT", "Death certificate", false),
    OTHER("Lainnya", "OTHER", "Other supporting documents", false);

    private final String displayName;
    private final String code;
    private final String description;
    private final boolean mandatory;

    DocumentType(String displayName, String code, String description, boolean mandatory) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.mandatory = mandatory;
    }

    /**
     * Get DocumentType from code
     *
     * @param code document type code
     * @return DocumentType enum
     */
    public static DocumentType fromCode(String code) {
        for (DocumentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown document type code: " + code);
    }

    /**
     * Check if document is medical record related
     *
     * @return true if medical record
     */
    public boolean isMedicalRecord() {
        return this == MEDICAL_RECORD ||
               this == DIAGNOSIS_REPORT ||
               this == LAB_RESULT ||
               this == RADIOLOGY_REPORT ||
               this == SURGERY_REPORT ||
               this == DISCHARGE_SUMMARY;
    }

    /**
     * Check if document is financial
     *
     * @return true if financial document
     */
    public boolean isFinancialDocument() {
        return this == INVOICE || this == RECEIPT;
    }

    /**
     * Check if document is identification
     *
     * @return true if identification document
     */
    public boolean isIdentificationDocument() {
        return this == INSURANCE_CARD || this == ID_CARD;
    }
}
