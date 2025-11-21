package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Verification Status enumeration.
 *
 * Status of pharmacist verification for prescriptions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum VerificationStatus {
    PENDING("Menunggu", "PENDING", "Menunggu verifikasi"),
    APPROVED("Disetujui", "APPROVED", "Diverifikasi dan disetujui"),
    APPROVED_WITH_CHANGES("Disetujui dengan Perubahan", "APPROVED_CHG", "Disetujui dengan modifikasi"),
    REJECTED("Ditolak", "REJECTED", "Ditolak oleh apoteker"),
    REQUIRES_CLARIFICATION("Perlu Klarifikasi", "REQ_CLAR", "Perlu klarifikasi dokter");

    private final String displayName;
    private final String code;
    private final String description;

    VerificationStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static VerificationStatus fromCode(String code) {
        for (VerificationStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown verification status code: " + code);
    }

    /**
     * Check if verification is complete
     */
    public boolean isComplete() {
        return this == APPROVED || this == APPROVED_WITH_CHANGES || this == REJECTED;
    }

    /**
     * Check if prescription can proceed
     */
    public boolean canProceed() {
        return this == APPROVED || this == APPROVED_WITH_CHANGES;
    }

    /**
     * Check if requires action from doctor
     */
    public boolean requiresDoctorAction() {
        return this == REQUIRES_CLARIFICATION || this == REJECTED;
    }
}
