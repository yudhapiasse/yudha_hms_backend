package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Formularium Status enumeration for Drug Management.
 *
 * Defines BPJS formularium approval status:
 * - APPROVED: Approved by BPJS formularium
 * - NOT_APPROVED: Not approved by BPJS
 * - RESTRICTED: Restricted use (requires approval)
 * - UNDER_REVIEW: Under review for formularium inclusion
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum FormulariumStatus {
    APPROVED("Disetujui", "APPROVED", "Approved by BPJS formularium"),
    NOT_APPROVED("Tidak Disetujui", "NOT_APPROVED", "Not approved by BPJS"),
    RESTRICTED("Terbatas", "RESTRICTED", "Restricted use (requires approval)"),
    UNDER_REVIEW("Sedang Ditinjau", "UNDER_REVIEW", "Under review for formularium inclusion");

    private final String displayName;
    private final String code;
    private final String description;

    FormulariumStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static FormulariumStatus fromCode(String code) {
        for (FormulariumStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown formularium status code: " + code);
    }

    /**
     * Check if drug is covered by BPJS
     */
    public boolean isBpjsCovered() {
        return this == APPROVED || this == RESTRICTED;
    }

    /**
     * Check if requires special approval
     */
    public boolean requiresApproval() {
        return this == RESTRICTED;
    }
}
