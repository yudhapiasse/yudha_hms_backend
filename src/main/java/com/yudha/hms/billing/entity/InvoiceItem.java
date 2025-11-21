package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.TariffType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Invoice Item Entity for Hospital Billing System.
 *
 * Represents individual line items/charges in an invoice.
 * Each item corresponds to a service, procedure, medication, or other billable item.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "invoice_item", schema = "billing_schema", indexes = {
    @Index(name = "idx_invoice_item_invoice", columnList = "invoice_id"),
    @Index(name = "idx_invoice_item_tariff", columnList = "tariff_id"),
    @Index(name = "idx_invoice_item_date", columnList = "service_date"),
    @Index(name = "idx_invoice_item_type", columnList = "item_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem extends SoftDeletableEntity {

    /**
     * Invoice reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @NotNull(message = "Invoice is required")
    private Invoice invoice;

    /**
     * Line number (for ordering)
     */
    @Column(name = "line_number", nullable = false)
    @NotNull(message = "Line number is required")
    @Min(value = 1)
    private Integer lineNumber;

    /**
     * Service date
     */
    @Column(name = "service_date")
    private LocalDate serviceDate;

    /**
     * Tariff reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    /**
     * Item type (ROOM, DOCTOR_FEE, PROCEDURE, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", length = 50)
    private TariffType itemType;

    /**
     * Item code
     */
    @Column(name = "item_code", length = 100)
    private String itemCode;

    /**
     * Item name/description
     */
    @Column(name = "item_name", length = 500, nullable = false)
    @NotNull(message = "Item name is required")
    @Size(max = 500)
    private String itemName;

    /**
     * Item description details
     */
    @Column(name = "item_description", length = 1000)
    private String itemDescription;

    /**
     * Quantity
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1)
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Unit of measurement
     */
    @Column(name = "unit", length = 50)
    private String unit;

    /**
     * Unit price
     */
    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal unitPrice;

    /**
     * Total price before discount (quantity * unitPrice)
     */
    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal totalPrice;

    /**
     * Discount amount for this item
     */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * Discount percentage
     */
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    /**
     * Net amount after discount
     */
    @Column(name = "net_amount", precision = 15, scale = 2)
    private BigDecimal netAmount;

    /**
     * Tax amount for this item
     */
    @Column(name = "tax_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /**
     * Department that provided the service
     */
    @Column(name = "department_id")
    private UUID departmentId;

    /**
     * Department name (for quick reference)
     */
    @Column(name = "department_name", length = 200)
    private String departmentName;

    /**
     * Practitioner who performed/ordered the service
     */
    @Column(name = "practitioner_id")
    private UUID practitionerId;

    /**
     * Practitioner name (for quick reference)
     */
    @Column(name = "practitioner_name", length = 200)
    private String practitionerName;

    /**
     * Reference to source record (e.g., OrderID, PrescriptionID, ProcedureID)
     */
    @Column(name = "source_reference_id")
    private UUID sourceReferenceId;

    /**
     * Source reference type (ORDER, PRESCRIPTION, PROCEDURE, etc.)
     */
    @Column(name = "source_reference_type", length = 50)
    private String sourceReferenceType;

    /**
     * Package deal reference (if this item is part of a package)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_deal_id")
    private PackageDeal packageDeal;

    /**
     * Flag indicating if covered by insurance/BPJS
     */
    @Column(name = "is_covered")
    @Builder.Default
    private Boolean covered = false;

    /**
     * Coverage percentage (for partial coverage)
     */
    @Column(name = "coverage_percentage", precision = 5, scale = 2)
    private BigDecimal coveragePercentage;

    /**
     * Amount covered by insurance/BPJS
     */
    @Column(name = "covered_amount", precision = 15, scale = 2)
    private BigDecimal coveredAmount;

    /**
     * Patient responsibility amount
     */
    @Column(name = "patient_responsibility", precision = 15, scale = 2)
    private BigDecimal patientResponsibility;

    /**
     * Notes
     */
    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * Calculate total price
     */
    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }

        // Calculate net amount after discount
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        netAmount = totalPrice.subtract(discount);

        // Calculate patient responsibility
        BigDecimal covered = coveredAmount != null ? coveredAmount : BigDecimal.ZERO;
        patientResponsibility = netAmount.subtract(covered);
    }
}
