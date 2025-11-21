package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.TariffType;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Claim Item Entity.
 *
 * Represents individual line items in an insurance claim.
 * Each item corresponds to a specific service, procedure, or charge.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "claim_item", schema = "billing_schema", indexes = {
        @Index(name = "idx_claim_item_claim", columnList = "claim_id"),
        @Index(name = "idx_claim_item_service_date", columnList = "service_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClaimItem extends BaseEntity {

    /**
     * Parent insurance claim
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private InsuranceClaim claim;

    /**
     * Line number (sequence in claim)
     */
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    /**
     * Service date
     */
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    /**
     * Related invoice item ID
     */
    @Column(name = "invoice_item_id")
    private UUID invoiceItemId;

    /**
     * Item type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 50)
    private TariffType itemType;

    /**
     * Item code (procedure code, drug code, etc.)
     */
    @Column(name = "item_code", length = 50)
    private String itemCode;

    /**
     * Item description
     */
    @Column(name = "item_description", nullable = false, length = 500)
    private String itemDescription;

    /**
     * Diagnosis code (ICD-10)
     */
    @Column(name = "diagnosis_code", length = 20)
    private String diagnosisCode;

    /**
     * Procedure code (ICD-9)
     */
    @Column(name = "procedure_code", length = 20)
    private String procedureCode;

    /**
     * Quantity
     */
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    /**
     * Unit of measurement
     */
    @Column(name = "unit", length = 50)
    private String unit;

    /**
     * Unit price
     */
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Total price (quantity * unit price)
     */
    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Claim amount requested
     */
    @Column(name = "claim_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal claimAmount;

    /**
     * Approved amount
     */
    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    /**
     * Rejection reason for this item
     */
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    /**
     * Performing physician/provider ID
     */
    @Column(name = "provider_id")
    private UUID providerId;

    /**
     * Performing physician/provider name
     */
    @Column(name = "provider_name", length = 200)
    private String providerName;

    /**
     * Department where service was performed
     */
    @Column(name = "department_name", length = 200)
    private String departmentName;

    /**
     * Tooth number/code (for dental claims)
     */
    @Column(name = "tooth_code", length = 20)
    private String toothCode;

    /**
     * Body site/location (for surgical claims)
     */
    @Column(name = "body_site", length = 100)
    private String bodySite;

    /**
     * Notes for this item
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Pre-persist callback to calculate totals
     */
    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(quantity);
        }

        if (claimAmount == null && totalPrice != null) {
            claimAmount = totalPrice;
        }
    }
}
