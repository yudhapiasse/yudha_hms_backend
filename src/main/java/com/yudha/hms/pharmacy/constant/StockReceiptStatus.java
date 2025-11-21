package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Stock Receipt Status enumeration.
 *
 * Tracks the status of stock receipts from suppliers.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum StockReceiptStatus {
    DRAFT("Draf", "DRAFT", "Sedang dibuat"),
    PENDING("Menunggu", "PENDING", "Menunggu kedatangan barang"),
    RECEIVED("Diterima", "RECEIVED", "Barang sudah diterima"),
    INSPECTED("Diperiksa", "INSPECTED", "Sudah dilakukan pemeriksaan"),
    APPROVED("Disetujui", "APPROVED", "Disetujui dan masuk ke stok"),
    REJECTED("Ditolak", "REJECTED", "Ditolak, tidak masuk stok"),
    PARTIALLY_RECEIVED("Diterima Sebagian", "PARTIAL", "Sebagian barang diterima"),
    CANCELLED("Dibatalkan", "CANCELLED", "Dibatalkan");

    private final String displayName;
    private final String code;
    private final String description;

    StockReceiptStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static StockReceiptStatus fromCode(String code) {
        for (StockReceiptStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown receipt status code: " + code);
    }

    /**
     * Check if receipt can be edited
     */
    public boolean isEditable() {
        return this == DRAFT || this == PENDING;
    }

    /**
     * Check if receipt can be inspected
     */
    public boolean canBeInspected() {
        return this == RECEIVED || this == PARTIALLY_RECEIVED;
    }

    /**
     * Check if receipt can be approved
     */
    public boolean canBeApproved() {
        return this == INSPECTED;
    }

    /**
     * Check if receipt is in final state
     */
    public boolean isFinal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if receipt affects inventory
     */
    public boolean affectsInventory() {
        return this == APPROVED;
    }
}
