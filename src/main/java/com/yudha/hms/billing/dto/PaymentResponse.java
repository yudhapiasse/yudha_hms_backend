package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.PaymentMethod;
import com.yudha.hms.billing.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for payment responses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private String paymentNumber;
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID patientId;
    private String patientMrn;
    private String patientName;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private PaymentStatus status;

    // Payment gateway information
    private String gatewayTransactionId;
    private String gatewayName;
    private String authorizationCode;

    // Card/Bank information
    private String cardLast4;
    private String cardType;
    private String bankName;
    private String accountLast4;

    // Cash payment
    private BigDecimal cashTendered;
    private BigDecimal changeAmount;

    // Cashier information
    private UUID cashierId;
    private String cashierName;
    private UUID cashRegisterId;
    private UUID shiftId;

    // Receipt information
    private String receiptNumber;
    private Boolean receiptPrinted;
    private Integer receiptPrintCount;
    private LocalDateTime lastReceiptPrintDate;

    // Refund information
    private Boolean refunded;
    private BigDecimal refundAmount;
    private LocalDateTime refundDate;
    private String refundReason;
    private String refundProcessedBy;
    private UUID originalPaymentId;

    // Additional information
    private String referenceNumber;
    private String notes;
    private LocalDateTime confirmedDate;
    private String confirmedBy;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
