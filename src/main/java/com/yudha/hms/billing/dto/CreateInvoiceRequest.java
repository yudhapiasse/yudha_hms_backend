package com.yudha.hms.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating invoices.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    private UUID encounterId;

    private String encounterType;

    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private LocalDate servicePeriodStart;

    private LocalDate servicePeriodEnd;

    @Valid
    private List<InvoiceItemRequest> items;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discountAmount;

    private BigDecimal discountPercentage;

    private String discountReason;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal taxAmount;

    private BigDecimal taxPercentage;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal depositDeduction;

    private String paymentType;

    private UUID insuranceCompanyId;

    private String insuranceClaimNumber;

    private String bpjsSepNumber;

    private UUID companyId;

    private String paymentTerms;

    private String notes;
}
