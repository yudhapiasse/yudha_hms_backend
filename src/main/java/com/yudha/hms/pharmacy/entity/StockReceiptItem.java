package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Stock Receipt Item Entity.
 *
 * Individual items in a stock receipt.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "stock_receipt_item", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_receipt_item_receipt", columnList = "receipt_id"),
        @Index(name = "idx_receipt_item_drug", columnList = "drug_id"),
        @Index(name = "idx_receipt_item_batch", columnList = "batch_number")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockReceiptItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id", nullable = false)
    private StockReceipt stockReceipt;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;

    @Column(name = "quantity_ordered", precision = 10, scale = 2)
    private BigDecimal quantityOrdered;

    @Column(name = "quantity_received", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantityReceived;

    @Column(name = "quantity_accepted", precision = 10, scale = 2)
    private BigDecimal quantityAccepted;

    @Column(name = "quantity_rejected", precision = 10, scale = 2)
    private BigDecimal quantityRejected;

    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "inspection_passed")
    private Boolean inspectionPassed;

    @Column(name = "inspection_notes", columnDefinition = "TEXT")
    private String inspectionNotes;

    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Calculate total amount
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotals() {
        if (quantityReceived != null && unitPrice != null) {
            BigDecimal subtotal = unitPrice.multiply(quantityReceived);

            if (discountAmount != null) {
                subtotal = subtotal.subtract(discountAmount);
            }
            if (taxAmount != null) {
                subtotal = subtotal.add(taxAmount);
            }

            this.totalAmount = subtotal;
        }

        if (quantityAccepted == null && quantityReceived != null) {
            this.quantityAccepted = quantityReceived;
        }
        if (quantityRejected == null) {
            this.quantityRejected = BigDecimal.ZERO;
        }
    }

    /**
     * Check if item is fully accepted
     */
    public boolean isFullyAccepted() {
        return quantityAccepted != null && quantityReceived != null &&
               quantityAccepted.compareTo(quantityReceived) == 0;
    }

    /**
     * Check if item has rejections
     */
    public boolean hasRejections() {
        return quantityRejected != null && quantityRejected.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if expiry date is approaching (within 90 days)
     */
    public boolean isExpiryApproaching() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now().plusDays(90));
    }

    /**
     * Check if already expired
     */
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now());
    }
}
