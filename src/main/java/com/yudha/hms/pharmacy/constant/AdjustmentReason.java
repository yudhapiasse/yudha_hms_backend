package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Adjustment Reason enumeration.
 *
 * Reasons for stock adjustments (increases or decreases).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum AdjustmentReason {
    DAMAGE("Kerusakan", "DMG", "Obat rusak/pecah"),
    EXPIRY("Kadaluarsa", "EXP", "Obat sudah kadaluarsa"),
    THEFT("Kehilangan", "THEFT", "Kehilangan/pencurian"),
    COUNT_DIFFERENCE("Selisih Stok Opname", "COUNT", "Selisih hasil stock opname"),
    SYSTEM_ERROR("Kesalahan Sistem", "SYS_ERR", "Koreksi kesalahan sistem"),
    PRODUCTION_LOSS("Kehilangan Produksi", "PROD_LOSS", "Kehilangan saat produksi/racik"),
    SAMPLING("Sampling", "SAMPLE", "Untuk sampling/demo"),
    QUALITY_ISSUE("Masalah Kualitas", "QUALITY", "Masalah kualitas produk"),
    RECALL("Penarikan Produk", "RECALL", "Penarikan produk dari pabrik"),
    DONATION("Donasi", "DONATE", "Donasi/bantuan"),
    INTERNAL_USE("Pemakaian Internal", "INTERNAL", "Pemakaian internal RS"),
    FOUND("Ditemukan", "FOUND", "Stok ditemukan/tidak tercatat"),
    OTHER("Lainnya", "OTHER", "Alasan lainnya");

    private final String displayName;
    private final String code;
    private final String description;

    AdjustmentReason(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static AdjustmentReason fromCode(String code) {
        for (AdjustmentReason reason : values()) {
            if (reason.code.equalsIgnoreCase(code)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown adjustment reason code: " + code);
    }

    /**
     * Check if reason indicates stock loss
     */
    public boolean indicatesLoss() {
        return this == DAMAGE || this == EXPIRY || this == THEFT ||
               this == PRODUCTION_LOSS || this == QUALITY_ISSUE || this == RECALL;
    }

    /**
     * Check if reason requires investigation
     */
    public boolean requiresInvestigation() {
        return this == THEFT || this == SYSTEM_ERROR || this == QUALITY_ISSUE || this == RECALL;
    }

    /**
     * Check if reason is acceptable for auditing
     */
    public boolean isAcceptableForAudit() {
        return this != OTHER || this == COUNT_DIFFERENCE;
    }
}
