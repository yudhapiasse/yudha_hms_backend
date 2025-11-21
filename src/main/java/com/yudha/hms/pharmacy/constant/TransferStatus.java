package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Transfer Status enumeration.
 *
 * Status of stock transfers between pharmacy locations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum TransferStatus {
    DRAFT("Draf", "DRAFT", "Sedang dibuat"),
    PENDING("Menunggu", "PENDING", "Menunggu persetujuan"),
    APPROVED("Disetujui", "APPROVED", "Disetujui untuk dikirim"),
    IN_TRANSIT("Dalam Perjalanan", "TRANSIT", "Sedang dalam perjalanan"),
    RECEIVED("Diterima", "RECEIVED", "Sudah diterima"),
    PARTIALLY_RECEIVED("Diterima Sebagian", "PARTIAL", "Sebagian barang diterima"),
    REJECTED("Ditolak", "REJECTED", "Ditolak"),
    CANCELLED("Dibatalkan", "CANCELLED", "Dibatalkan");

    private final String displayName;
    private final String code;
    private final String description;

    TransferStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static TransferStatus fromCode(String code) {
        for (TransferStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown transfer status code: " + code);
    }

    /**
     * Check if transfer can be edited
     */
    public boolean isEditable() {
        return this == DRAFT || this == PENDING;
    }

    /**
     * Check if transfer can be approved
     */
    public boolean canBeApproved() {
        return this == PENDING;
    }

    /**
     * Check if transfer can be sent
     */
    public boolean canBeSent() {
        return this == APPROVED;
    }

    /**
     * Check if transfer can be received
     */
    public boolean canBeReceived() {
        return this == IN_TRANSIT || this == PARTIALLY_RECEIVED;
    }

    /**
     * Check if transfer is in final state
     */
    public boolean isFinal() {
        return this == RECEIVED || this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if transfer affects source inventory
     */
    public boolean affectsSourceInventory() {
        return this == APPROVED || this == IN_TRANSIT || this == RECEIVED ||
               this == PARTIALLY_RECEIVED;
    }

    /**
     * Check if transfer affects destination inventory
     */
    public boolean affectsDestinationInventory() {
        return this == RECEIVED || this == PARTIALLY_RECEIVED;
    }
}
