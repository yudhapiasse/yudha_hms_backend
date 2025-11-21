package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.TariffType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating/updating invoice items.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemRequest {

    private LocalDate serviceDate;

    @NotNull(message = "Tariff ID is required")
    private UUID tariffId;

    private TariffType itemType;

    private String itemCode;

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String itemDescription;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String unit;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be non-negative")
    private BigDecimal unitPrice;

    private BigDecimal discountAmount;

    private BigDecimal discountPercentage;

    private UUID departmentId;

    private String departmentName;

    private UUID practitionerId;

    private String practitionerName;

    private UUID sourceReferenceId;

    private String sourceReferenceType;

    private UUID packageDealId;

    private Boolean covered;

    private BigDecimal coveragePercentage;

    private String notes;
}
