package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Invoice Status enumeration for Hospital Billing System.
 *
 * Tracks the lifecycle of invoices from draft to completion:
 * - DRAFT: Invoice being prepared, not yet issued
 * - ISSUED: Invoice issued to patient, awaiting payment
 * - PARTIALLY_PAID: Partial payment received
 * - PAID: Fully paid
 * - OVERDUE: Payment past due date
 * - CANCELLED: Invoice cancelled
 * - VOID: Invoice voided (for corrections)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum InvoiceStatus {
    DRAFT("Draft", "DRAFT", "Invoice being prepared"),
    ISSUED("Diterbitkan", "ISSUED", "Invoice issued, awaiting payment"),
    PARTIALLY_PAID("Sebagian Lunas", "PARTIAL", "Partial payment received"),
    PAID("Lunas", "PAID", "Fully paid"),
    OVERDUE("Jatuh Tempo", "OVERDUE", "Payment overdue"),
    CANCELLED("Dibatalkan", "CANCELLED", "Invoice cancelled"),
    VOID("Batal/Koreksi", "VOID", "Invoice voided for correction");

    private final String displayName;
    private final String code;
    private final String description;

    InvoiceStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Get InvoiceStatus from code
     *
     * @param code status code
     * @return InvoiceStatus enum
     */
    public static InvoiceStatus fromCode(String code) {
        for (InvoiceStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown invoice status code: " + code);
    }

    /**
     * Check if invoice can be edited
     *
     * @return true if editable
     */
    public boolean isEditable() {
        return this == DRAFT;
    }

    /**
     * Check if invoice can accept payments
     *
     * @return true if can accept payment
     */
    public boolean canAcceptPayment() {
        return this == ISSUED || this == PARTIALLY_PAID || this == OVERDUE;
    }

    /**
     * Check if invoice is in final state
     *
     * @return true if final
     */
    public boolean isFinal() {
        return this == PAID || this == CANCELLED || this == VOID;
    }
}
