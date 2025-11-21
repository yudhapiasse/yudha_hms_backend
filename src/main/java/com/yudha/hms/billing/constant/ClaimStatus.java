package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Claim Status enumeration for Insurance Claim Management.
 *
 * Tracks the status of insurance claims throughout the claim lifecycle:
 * - DRAFT: Claim being prepared
 * - SUBMITTED: Claim submitted to insurance company
 * - UNDER_REVIEW: Claim being reviewed by insurance
 * - APPROVED: Claim approved, awaiting payment
 * - PARTIALLY_APPROVED: Claim partially approved
 * - REJECTED: Claim rejected
 * - PAID: Claim payment received
 * - APPEALED: Claim rejection appealed
 * - CANCELLED: Claim cancelled
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum ClaimStatus {
    DRAFT("Draft", "DRAFT", "Claim being prepared"),
    SUBMITTED("Diajukan", "SUBMITTED", "Claim submitted to insurance"),
    UNDER_REVIEW("Sedang Ditinjau", "UNDER_REVIEW", "Claim under review"),
    APPROVED("Disetujui", "APPROVED", "Claim approved"),
    PARTIALLY_APPROVED("Disetujui Sebagian", "PARTIAL_APPROVED", "Claim partially approved"),
    REJECTED("Ditolak", "REJECTED", "Claim rejected"),
    PAID("Dibayar", "PAID", "Claim payment received"),
    APPEALED("Banding", "APPEALED", "Claim appealed"),
    CANCELLED("Dibatalkan", "CANCELLED", "Claim cancelled");

    private final String displayName;
    private final String code;
    private final String description;

    ClaimStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Get ClaimStatus from code
     *
     * @param code status code
     * @return ClaimStatus enum
     */
    public static ClaimStatus fromCode(String code) {
        for (ClaimStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown claim status code: " + code);
    }

    /**
     * Check if claim is editable
     *
     * @return true if editable
     */
    public boolean isEditable() {
        return this == DRAFT;
    }

    /**
     * Check if claim can be submitted
     *
     * @return true if can be submitted
     */
    public boolean canBeSubmitted() {
        return this == DRAFT;
    }

    /**
     * Check if claim is final
     *
     * @return true if final
     */
    public boolean isFinal() {
        return this == PAID || this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if claim can be appealed
     *
     * @return true if can be appealed
     */
    public boolean canBeAppealed() {
        return this == REJECTED || this == PARTIALLY_APPROVED;
    }
}
