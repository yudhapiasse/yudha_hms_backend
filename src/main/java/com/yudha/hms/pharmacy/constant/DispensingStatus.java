package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Dispensing Status Enum.
 *
 * Represents the status of a dispensing transaction in the pharmacy workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum DispensingStatus {
    QUEUE("Antrean", "QUEUE", "Resep dalam antrean"),
    PREPARING("Sedang Disiapkan", "PREP", "Sedang menyiapkan obat"),
    VERIFICATION("Verifikasi", "VERIFY", "Menunggu verifikasi apoteker"),
    READY("Siap Diambil", "READY", "Obat siap untuk diambil pasien"),
    DISPENSED("Sudah Diserahkan", "DISP", "Obat sudah diserahkan ke pasien"),
    PARTIALLY_DISPENSED("Diserahkan Sebagian", "PARTIAL", "Beberapa item belum tersedia"),
    ON_HOLD("Ditunda", "HOLD", "Dispensing ditunda sementara"),
    CANCELLED("Dibatalkan", "CANCEL", "Dispensing dibatalkan"),
    RETURNED("Dikembalikan", "RETURN", "Obat dikembalikan");

    private final String displayName;
    private final String code;
    private final String description;

    DispensingStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Check if dispensing can be edited
     */
    public boolean isEditable() {
        return this == QUEUE || this == ON_HOLD;
    }

    /**
     * Check if dispensing can be prepared
     */
    public boolean canBePrepared() {
        return this == QUEUE;
    }

    /**
     * Check if dispensing can be verified
     */
    public boolean canBeVerified() {
        return this == PREPARING;
    }

    /**
     * Check if dispensing can be dispensed to patient
     */
    public boolean canBeDispensed() {
        return this == READY || this == VERIFICATION;
    }

    /**
     * Check if this is a final status
     */
    public boolean isFinal() {
        return this == DISPENSED || this == CANCELLED || this == RETURNED;
    }

    /**
     * Check if this is an active status
     */
    public boolean isActive() {
        return !isFinal() && this != ON_HOLD;
    }

    /**
     * Check if stock should be reduced
     */
    public boolean affectsInventory() {
        return this == DISPENSED || this == PARTIALLY_DISPENSED;
    }

    /**
     * Check if patient can pick up
     */
    public boolean isReadyForPickup() {
        return this == READY;
    }
}
