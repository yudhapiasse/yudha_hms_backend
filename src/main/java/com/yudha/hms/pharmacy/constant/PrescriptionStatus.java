package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Prescription Status enumeration.
 *
 * Tracks the lifecycle of a prescription from entry to completion.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum PrescriptionStatus {
    DRAFT("Draf", "DRAFT", "Resep sedang diinput"),
    PENDING_VERIFICATION("Menunggu Verifikasi", "PENDING_VER", "Menunggu verifikasi apoteker"),
    VERIFIED("Terverifikasi", "VERIFIED", "Diverifikasi oleh apoteker"),
    REJECTED("Ditolak", "REJECTED", "Ditolak oleh apoteker"),
    DISPENSED("Telah Diserahkan", "DISPENSED", "Obat telah diserahkan ke pasien"),
    PARTIALLY_DISPENSED("Diserahkan Sebagian", "PARTIAL", "Beberapa item telah diserahkan"),
    CANCELLED("Dibatalkan", "CANCELLED", "Resep dibatalkan"),
    EXPIRED("Kadaluarsa", "EXPIRED", "Resep kadaluarsa");

    private final String displayName;
    private final String code;
    private final String description;

    PrescriptionStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static PrescriptionStatus fromCode(String code) {
        for (PrescriptionStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown prescription status code: " + code);
    }

    /**
     * Check if prescription can be edited
     */
    public boolean isEditable() {
        return this == DRAFT;
    }

    /**
     * Check if prescription can be verified
     */
    public boolean canBeVerified() {
        return this == PENDING_VERIFICATION;
    }

    /**
     * Check if prescription can be dispensed
     */
    public boolean canBeDispensed() {
        return this == VERIFIED || this == PARTIALLY_DISPENSED;
    }

    /**
     * Check if prescription is in final state
     */
    public boolean isFinal() {
        return this == DISPENSED || this == CANCELLED || this == EXPIRED || this == REJECTED;
    }

    /**
     * Check if prescription is active
     */
    public boolean isActive() {
        return !isFinal();
    }
}
