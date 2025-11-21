package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.PaymentMethod;
import com.yudha.hms.billing.constant.PaymentStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment Entity for Hospital Billing System.
 *
 * Represents a payment transaction for an invoice.
 * Supports multiple payment methods and partial payments.
 *
 * Features:
 * - Multiple payment methods (cash, card, e-wallet, QRIS, etc.)
 * - Partial payment support
 * - Payment gateway integration support
 * - Receipt generation
 * - Refund tracking
 * - Cash register integration
 *
 * Indonesian-specific features:
 * - QRIS payment support
 * - E-Wallet support (OVO, GoPay, Dana, etc.)
 * - BPJS claim tracking
 * - Insurance claim tracking
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "payment", schema = "billing_schema", indexes = {
    @Index(name = "idx_payment_number", columnList = "payment_number", unique = true),
    @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
    @Index(name = "idx_payment_patient", columnList = "patient_id"),
    @Index(name = "idx_payment_date", columnList = "payment_date"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_method", columnList = "payment_method")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends SoftDeletableEntity {

    // ========================================================================
    // BASIC INFORMATION
    // ========================================================================

    /**
     * Payment number (unique, auto-generated)
     * Format: PAY-YYYYMM-XXXXX (e.g., PAY-202501-00001)
     */
    @Column(name = "payment_number", length = 50, nullable = false, unique = true)
    @NotBlank(message = "Payment number is required")
    @Size(max = 50)
    private String paymentNumber;

    /**
     * Invoice reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @NotNull(message = "Invoice is required")
    private Invoice invoice;

    /**
     * Patient ID (for quick reference)
     */
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private UUID patientId;

    /**
     * Patient MRN (for quick reference)
     */
    @Column(name = "patient_mrn", length = 50)
    private String patientMrn;

    /**
     * Patient name (for quick reference)
     */
    @Column(name = "patient_name", length = 200)
    private String patientName;

    // ========================================================================
    // PAYMENT DETAILS
    // ========================================================================

    /**
     * Payment date and time
     */
    @Column(name = "payment_date", nullable = false)
    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    /**
     * Payment method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50, nullable = false)
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    /**
     * Payment amount
     */
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    @NotNull(message = "Status is required")
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    // ========================================================================
    // PAYMENT GATEWAY INFORMATION
    // ========================================================================

    /**
     * Payment gateway transaction ID
     */
    @Column(name = "gateway_transaction_id", length = 200)
    private String gatewayTransactionId;

    /**
     * Payment gateway name (Midtrans, Xendit, etc.)
     */
    @Column(name = "gateway_name", length = 100)
    private String gatewayName;

    /**
     * Payment gateway response
     */
    @Column(name = "gateway_response", length = 2000)
    private String gatewayResponse;

    /**
     * Payment authorization code
     */
    @Column(name = "authorization_code", length = 100)
    private String authorizationCode;

    // ========================================================================
    // CARD/BANK INFORMATION
    // ========================================================================

    /**
     * Card number (last 4 digits only for security)
     */
    @Column(name = "card_last4", length = 4)
    private String cardLast4;

    /**
     * Card type (Visa, Mastercard, etc.)
     */
    @Column(name = "card_type", length = 50)
    private String cardType;

    /**
     * Bank name
     */
    @Column(name = "bank_name", length = 100)
    private String bankName;

    /**
     * Account number (last 4 digits only)
     */
    @Column(name = "account_last4", length = 4)
    private String accountLast4;

    // ========================================================================
    // CASH PAYMENT INFORMATION
    // ========================================================================

    /**
     * Cash tendered (for cash payments)
     */
    @Column(name = "cash_tendered", precision = 15, scale = 2)
    private BigDecimal cashTendered;

    /**
     * Change amount (for cash payments)
     */
    @Column(name = "change_amount", precision = 15, scale = 2)
    private BigDecimal changeAmount;

    // ========================================================================
    // CASHIER INFORMATION
    // ========================================================================

    /**
     * Cashier ID (user who processed the payment)
     */
    @Column(name = "cashier_id")
    private UUID cashierId;

    /**
     * Cashier name (for quick reference)
     */
    @Column(name = "cashier_name", length = 200)
    private String cashierName;

    /**
     * Cash register ID
     */
    @Column(name = "cash_register_id")
    private UUID cashRegisterId;

    /**
     * Shift ID (for daily reconciliation)
     */
    @Column(name = "shift_id")
    private UUID shiftId;

    // ========================================================================
    // RECEIPT INFORMATION
    // ========================================================================

    /**
     * Receipt number
     */
    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    /**
     * Receipt printed flag
     */
    @Column(name = "is_receipt_printed")
    @Builder.Default
    private Boolean receiptPrinted = false;

    /**
     * Receipt print count
     */
    @Column(name = "receipt_print_count")
    @Builder.Default
    private Integer receiptPrintCount = 0;

    /**
     * Last receipt print date
     */
    @Column(name = "last_receipt_print_date")
    private LocalDateTime lastReceiptPrintDate;

    // ========================================================================
    // REFUND INFORMATION
    // ========================================================================

    /**
     * Refund flag
     */
    @Column(name = "is_refunded")
    @Builder.Default
    private Boolean refunded = false;

    /**
     * Refund amount
     */
    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount;

    /**
     * Refund date
     */
    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    /**
     * Refund reason
     */
    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    /**
     * Refund processed by (user)
     */
    @Column(name = "refund_processed_by", length = 100)
    private String refundProcessedBy;

    /**
     * Original payment reference (for refund transactions)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_payment_id")
    private Payment originalPayment;

    // ========================================================================
    // ADDITIONAL INFORMATION
    // ========================================================================

    /**
     * Reference number (check number, transfer reference, etc.)
     */
    @Column(name = "reference_number", length = 200)
    private String referenceNumber;

    /**
     * Notes
     */
    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * Confirmed date (when payment was confirmed/verified)
     */
    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDate;

    /**
     * Confirmed by (user who confirmed)
     */
    @Column(name = "confirmed_by", length = 100)
    private String confirmedBy;

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Mark as receipt printed
     */
    public void markReceiptAsPrinted() {
        receiptPrinted = true;
        receiptPrintCount = (receiptPrintCount != null ? receiptPrintCount : 0) + 1;
        lastReceiptPrintDate = LocalDateTime.now();
    }

    /**
     * Process refund
     *
     * @param refundAmt refund amount
     * @param reason refund reason
     * @param processedBy user processing refund
     */
    public void processRefund(BigDecimal refundAmt, String reason, String processedBy) {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }
        if (refundAmt.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed payment amount");
        }

        refunded = true;
        refundAmount = refundAmt;
        refundDate = LocalDateTime.now();
        refundReason = reason;
        refundProcessedBy = processedBy;

        // Update status
        if (refundAmt.compareTo(amount) == 0) {
            status = PaymentStatus.REFUNDED;
        } else {
            status = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }

    /**
     * Confirm payment
     *
     * @param confirmedByUser user confirming
     */
    public void confirmPayment(String confirmedByUser) {
        status = PaymentStatus.COMPLETED;
        confirmedDate = LocalDateTime.now();
        confirmedBy = confirmedByUser;
    }

    /**
     * Cancel payment
     */
    public void cancelPayment() {
        if (!status.isCancellable()) {
            throw new IllegalStateException("Payment cannot be cancelled in current status: " + status);
        }
        status = PaymentStatus.CANCELLED;
    }
}
