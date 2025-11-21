package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for cashier shift reports.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashierShiftReportResponse {

    private UUID shiftId;
    private UUID cashierId;
    private String cashierName;
    private UUID cashRegisterId;
    private LocalDateTime shiftStartTime;
    private LocalDateTime shiftEndTime;

    // Total statistics
    private Integer totalPayments;
    private BigDecimal totalAmount;
    private BigDecimal totalRefunds;
    private BigDecimal netAmount;

    // Breakdown by payment method
    private Map<PaymentMethod, PaymentMethodSummary> paymentMethodBreakdown;

    // Payment list
    private List<PaymentResponse> payments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodSummary {
        private PaymentMethod method;
        private Integer count;
        private BigDecimal amount;
    }
}
