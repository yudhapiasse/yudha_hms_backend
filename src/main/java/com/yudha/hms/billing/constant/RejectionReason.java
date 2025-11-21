package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Rejection Reason enumeration for Insurance Claim Management.
 *
 * Common reasons for claim rejection:
 * - INCOMPLETE_DOCUMENTATION: Missing or incomplete documents
 * - COVERAGE_EXPIRED: Insurance coverage expired
 * - SERVICE_NOT_COVERED: Service not covered by policy
 * - PRE_AUTHORIZATION_REQUIRED: Pre-authorization was required but not obtained
 * - DUPLICATE_CLAIM: Duplicate claim submission
 * - EXCEEDS_LIMIT: Claim exceeds coverage limit
 * - INCORRECT_INFORMATION: Incorrect patient or policy information
 * - LATE_SUBMISSION: Claim submitted after deadline
 * - NON_NETWORK_PROVIDER: Provider not in insurance network
 * - MEDICAL_NECESSITY: Medical necessity not established
 * - CODING_ERROR: Incorrect diagnosis or procedure codes
 * - OTHER: Other reasons
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum RejectionReason {
    INCOMPLETE_DOCUMENTATION("Dokumentasi Tidak Lengkap", "INCOMPLETE_DOC",
            "Missing or incomplete documentation"),
    COVERAGE_EXPIRED("Polis Kadaluarsa", "COVERAGE_EXPIRED",
            "Insurance coverage expired"),
    SERVICE_NOT_COVERED("Layanan Tidak Ditanggung", "NOT_COVERED",
            "Service not covered by policy"),
    PRE_AUTHORIZATION_REQUIRED("Memerlukan Pre-Autorisasi", "PRE_AUTH_REQUIRED",
            "Pre-authorization required but not obtained"),
    DUPLICATE_CLAIM("Klaim Duplikat", "DUPLICATE",
            "Duplicate claim submission"),
    EXCEEDS_LIMIT("Melebihi Limit", "EXCEEDS_LIMIT",
            "Claim amount exceeds coverage limit"),
    INCORRECT_INFORMATION("Informasi Salah", "INCORRECT_INFO",
            "Incorrect patient or policy information"),
    LATE_SUBMISSION("Terlambat Diajukan", "LATE_SUBMISSION",
            "Claim submitted after deadline"),
    NON_NETWORK_PROVIDER("Provider Diluar Jaringan", "NON_NETWORK",
            "Provider not in insurance network"),
    MEDICAL_NECESSITY("Tidak Medis Diperlukan", "NOT_MEDICALLY_NECESSARY",
            "Medical necessity not established"),
    CODING_ERROR("Kesalahan Kode", "CODING_ERROR",
            "Incorrect diagnosis or procedure codes"),
    POLICY_LIMITATIONS("Batasan Polis", "POLICY_LIMIT",
            "Policy limitations or exclusions apply"),
    COORDINATION_OF_BENEFITS("Koordinasi Manfaat", "COB_ISSUE",
            "Coordination of benefits issue"),
    OTHER("Lainnya", "OTHER",
            "Other rejection reason");

    private final String displayName;
    private final String code;
    private final String description;

    RejectionReason(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Get RejectionReason from code
     *
     * @param code rejection reason code
     * @return RejectionReason enum
     */
    public static RejectionReason fromCode(String code) {
        for (RejectionReason reason : values()) {
            if (reason.code.equalsIgnoreCase(code)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown rejection reason code: " + code);
    }

    /**
     * Check if rejection can be corrected and resubmitted
     *
     * @return true if correctable
     */
    public boolean isCorrectable() {
        return this == INCOMPLETE_DOCUMENTATION ||
               this == INCORRECT_INFORMATION ||
               this == CODING_ERROR ||
               this == PRE_AUTHORIZATION_REQUIRED;
    }

    /**
     * Check if rejection is related to documentation
     *
     * @return true if documentation-related
     */
    public boolean isDocumentationRelated() {
        return this == INCOMPLETE_DOCUMENTATION || this == LATE_SUBMISSION;
    }

    /**
     * Check if rejection is related to coverage
     *
     * @return true if coverage-related
     */
    public boolean isCoverageRelated() {
        return this == COVERAGE_EXPIRED ||
               this == SERVICE_NOT_COVERED ||
               this == EXCEEDS_LIMIT ||
               this == POLICY_LIMITATIONS;
    }
}
