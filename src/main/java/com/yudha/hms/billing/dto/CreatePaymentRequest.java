package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating payments.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Invoice ID is required")
    private UUID invoiceId;

    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    // Payment gateway information
    private String gatewayTransactionId;
    private String gatewayName;
    private String authorizationCode;

    // Card/Bank information
    private String cardLast4;
    private String cardType;
    private String bankName;
    private String accountLast4;

    // Cash payment specific
    private BigDecimal cashTendered;

    // Cashier information
    private UUID cashierId;
    private String cashierName;
    private UUID cashRegisterId;
    private UUID shiftId;

    // Reference information
    private String referenceNumber;
    private String notes;
}
