package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.InvoiceStatus;
import com.yudha.hms.billing.constant.TariffType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for invoice responses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private UUID id;
    private String invoiceNumber;
    private UUID patientId;
    private String patientMrn;
    private String patientName;
    private UUID encounterId;
    private String encounterType;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private LocalDate servicePeriodStart;
    private LocalDate servicePeriodEnd;
    private InvoiceStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal discountPercentage;
    private String discountReason;
    private BigDecimal taxAmount;
    private BigDecimal taxPercentage;
    private BigDecimal total;
    private BigDecimal depositDeduction;
    private BigDecimal paidAmount;
    private BigDecimal outstandingBalance;
    private String paymentType;
    private UUID insuranceCompanyId;
    private String insuranceClaimNumber;
    private String bpjsSepNumber;
    private UUID companyId;
    private String paymentTerms;
    private String notes;
    private Boolean printed;
    private Integer printCount;
    private LocalDateTime lastPrintedDate;
    private List<InvoiceItemResponse> items;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemResponse {
        private UUID id;
        private Integer lineNumber;
        private LocalDate serviceDate;
        private UUID tariffId;
        private TariffType itemType;
        private String itemCode;
        private String itemName;
        private String itemDescription;
        private Integer quantity;
        private String unit;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private BigDecimal discountAmount;
        private BigDecimal discountPercentage;
        private BigDecimal netAmount;
        private BigDecimal taxAmount;
        private UUID departmentId;
        private String departmentName;
        private UUID practitionerId;
        private String practitionerName;
        private UUID sourceReferenceId;
        private String sourceReferenceType;
        private UUID packageDealId;
        private Boolean covered;
        private BigDecimal coveragePercentage;
        private BigDecimal coveredAmount;
        private BigDecimal patientResponsibility;
        private String notes;
    }
}
