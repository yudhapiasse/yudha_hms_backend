package com.yudha.hms.registration.entity;

import lombok.Getter;

/**
 * Payment Method Enum.
 *
 * Defines payment methods for outpatient registration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum PaymentMethod {
    CASH("Cash", "Pembayaran tunai"),
    BPJS("BPJS", "BPJS Kesehatan"),
    INSURANCE("Insurance", "Asuransi swasta"),
    DEBIT("Debit Card", "Kartu debit"),
    CREDIT("Credit Card", "Kartu kredit"),
    TRANSFER("Bank Transfer", "Transfer bank");

    private final String displayName;
    private final String description;

    PaymentMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public boolean isBpjs() {
        return this == BPJS;
    }
}