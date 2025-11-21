package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Payment Method enumeration for Indonesian Hospital Management System.
 *
 * Supports various payment methods commonly used in Indonesian healthcare:
 * - Cash payment
 * - Card payments (Debit/Credit)
 * - Digital payments (QRIS, e-Wallets)
 * - Bank transfers
 * - Insurance (BPJS and private)
 *
 * Indonesian-specific features:
 * - QRIS (QR Code Indonesian Standard)
 * - E-Wallets (OVO, GoPay, Dana, LinkAja)
 * - BPJS Kesehatan integration
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum PaymentMethod {
    CASH("Tunai", "CASH", "Cash payment"),
    DEBIT("Kartu Debit", "DEBIT", "Debit card payment"),
    CREDIT("Kartu Kredit", "CREDIT", "Credit card payment"),
    QRIS("QRIS", "QRIS", "QR Code Indonesian Standard"),
    E_WALLET_OVO("OVO", "OVO", "OVO e-Wallet"),
    E_WALLET_GOPAY("GoPay", "GOPAY", "GoPay e-Wallet"),
    E_WALLET_DANA("Dana", "DANA", "Dana e-Wallet"),
    E_WALLET_LINKAJA("LinkAja", "LINKAJA", "LinkAja e-Wallet"),
    E_WALLET_SHOPEEPAY("ShopeePay", "SHOPEEPAY", "ShopeePay e-Wallet"),
    BANK_TRANSFER("Transfer Bank", "TRANSFER", "Bank transfer/Virtual Account"),
    BPJS("BPJS Kesehatan", "BPJS", "National health insurance"),
    INSURANCE("Asuransi Swasta", "INSURANCE", "Private insurance company");

    private final String displayName;
    private final String code;
    private final String description;

    PaymentMethod(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Get PaymentMethod from code
     *
     * @param code payment method code
     * @return PaymentMethod enum
     */
    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.code.equalsIgnoreCase(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method code: " + code);
    }

    /**
     * Check if this payment method is an e-wallet
     *
     * @return true if e-wallet
     */
    public boolean isEWallet() {
        return this == E_WALLET_OVO ||
               this == E_WALLET_GOPAY ||
               this == E_WALLET_DANA ||
               this == E_WALLET_LINKAJA ||
               this == E_WALLET_SHOPEEPAY;
    }

    /**
     * Check if this payment method requires gateway integration
     *
     * @return true if requires gateway
     */
    public boolean requiresGateway() {
        return this == DEBIT ||
               this == CREDIT ||
               this == QRIS ||
               isEWallet() ||
               this == BANK_TRANSFER;
    }

    /**
     * Check if this payment method is insurance-based
     *
     * @return true if insurance
     */
    public boolean isInsurance() {
        return this == BPJS || this == INSURANCE;
    }
}
