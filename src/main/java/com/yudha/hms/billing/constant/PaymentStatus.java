package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Payment Status enumeration for Hospital Billing System.
 *
 * Tracks the status of payment transactions:
 * - PENDING: Payment initiated, awaiting confirmation
 * - PROCESSING: Payment being processed by gateway
 * - COMPLETED: Payment successful and confirmed
 * - FAILED: Payment failed
 * - CANCELLED: Payment cancelled
 * - REFUNDED: Payment refunded
 * - PARTIALLY_REFUNDED: Partial refund processed
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum PaymentStatus {
    PENDING("Menunggu", "PENDING", "Payment pending confirmation"),
    PROCESSING("Diproses", "PROCESSING", "Payment being processed"),
    COMPLETED("Berhasil", "COMPLETED", "Payment completed successfully"),
    FAILED("Gagal", "FAILED", "Payment failed"),
    CANCELLED("Dibatalkan", "CANCELLED", "Payment cancelled"),
    REFUNDED("Dikembalikan", "REFUNDED", "Payment fully refunded"),
    PARTIALLY_REFUNDED("Dikembalikan Sebagian", "PARTIAL_REFUND", "Payment partially refunded");

    private final String displayName;
    private final String code;
    private final String description;

    PaymentStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Get PaymentStatus from code
     *
     * @param code status code
     * @return PaymentStatus enum
     */
    public static PaymentStatus fromCode(String code) {
        for (PaymentStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status code: " + code);
    }

    /**
     * Check if payment is successful
     *
     * @return true if successful
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Check if payment is in progress
     *
     * @return true if in progress
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }

    /**
     * Check if payment can be cancelled
     *
     * @return true if cancellable
     */
    public boolean isCancellable() {
        return this == PENDING || this == PROCESSING;
    }

    /**
     * Check if payment can be refunded
     *
     * @return true if refundable
     */
    public boolean isRefundable() {
        return this == COMPLETED;
    }
}
