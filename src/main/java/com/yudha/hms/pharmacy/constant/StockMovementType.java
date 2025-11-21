package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Stock Movement Type enumeration.
 *
 * Defines all types of stock movements in pharmacy inventory.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum StockMovementType {
    RECEIPT("Penerimaan", "RCPT", "Penerimaan stok dari supplier", true),
    DISPENSING("Penyerahan", "DISP", "Penyerahan obat ke pasien", false),
    ADJUSTMENT_IN("Penyesuaian Masuk", "ADJ_IN", "Penyesuaian stok (penambahan)", true),
    ADJUSTMENT_OUT("Penyesuaian Keluar", "ADJ_OUT", "Penyesuaian stok (pengurangan)", false),
    TRANSFER_OUT("Transfer Keluar", "TRF_OUT", "Transfer ke lokasi lain", false),
    TRANSFER_IN("Transfer Masuk", "TRF_IN", "Penerimaan dari lokasi lain", true),
    RETURN_TO_SUPPLIER("Retur ke Supplier", "RTN_SUP", "Retur ke supplier", false),
    RETURN_FROM_PATIENT("Retur dari Pasien", "RTN_PAT", "Retur dari pasien", true),
    WASTAGE("Pembuangan", "WSTG", "Pembuangan obat rusak/kadaluarsa", false),
    EXPIRED("Kadaluarsa", "EXPR", "Obat kadaluarsa", false),
    PRODUCTION("Produksi", "PROD", "Produksi/racikan obat", false),
    SAMPLING("Sampling", "SMPL", "Untuk sampling/demo", false),
    INITIAL_STOCK("Stok Awal", "INIT", "Stok awal sistem", true);

    private final String displayName;
    private final String code;
    private final String description;
    private final boolean isInbound; // true for stock increase, false for decrease

    StockMovementType(String displayName, String code, String description, boolean isInbound) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.isInbound = isInbound;
    }

    public static StockMovementType fromCode(String code) {
        for (StockMovementType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown stock movement type code: " + code);
    }

    /**
     * Check if movement increases stock
     */
    public boolean increasesStock() {
        return isInbound;
    }

    /**
     * Check if movement decreases stock
     */
    public boolean decreasesStock() {
        return !isInbound;
    }

    /**
     * Check if movement is a transfer
     */
    public boolean isTransfer() {
        return this == TRANSFER_OUT || this == TRANSFER_IN;
    }

    /**
     * Check if movement is an adjustment
     */
    public boolean isAdjustment() {
        return this == ADJUSTMENT_IN || this == ADJUSTMENT_OUT;
    }

    /**
     * Check if movement is a return
     */
    public boolean isReturn() {
        return this == RETURN_TO_SUPPLIER || this == RETURN_FROM_PATIENT;
    }
}
