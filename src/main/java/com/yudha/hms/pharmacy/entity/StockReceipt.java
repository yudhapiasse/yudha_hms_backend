package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.StockReceiptStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stock Receipt Entity.
 *
 * Represents stock receipts from suppliers with inspection workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "stock_receipt", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_receipt_number", columnList = "receipt_number", unique = true),
        @Index(name = "idx_receipt_supplier", columnList = "supplier_id"),
        @Index(name = "idx_receipt_location", columnList = "location_id"),
        @Index(name = "idx_receipt_status", columnList = "status"),
        @Index(name = "idx_receipt_date", columnList = "receipt_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockReceipt extends SoftDeletableEntity {

    @Column(name = "receipt_number", nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "supplier_name", length = 200)
    private String supplierName;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "purchase_order_number", length = 50)
    private String purchaseOrderNumber;

    @Column(name = "supplier_invoice_number", length = 50)
    private String supplierInvoiceNumber;

    @Column(name = "supplier_delivery_note", length = 50)
    private String supplierDeliveryNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private StockReceiptStatus status = StockReceiptStatus.DRAFT;

    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "total_quantity", precision = 15, scale = 2)
    private BigDecimal totalQuantity;

    @Column(name = "total_value", precision = 15, scale = 2)
    private BigDecimal totalValue;

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

    @Column(name = "delivered_by", length = 200)
    private String deliveredBy;

    @Column(name = "received_by_id")
    private UUID receivedById;

    @Column(name = "received_by_name", length = 200)
    private String receivedByName;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "inspected_by_id")
    private UUID inspectedById;

    @Column(name = "inspected_by_name", length = 200)
    private String inspectedByName;

    @Column(name = "inspected_at")
    private LocalDateTime inspectedAt;

    @Column(name = "inspection_notes", columnDefinition = "TEXT")
    private String inspectionNotes;

    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approved_by_name", length = 200)
    private String approvedByName;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockReceiptItem> items = new ArrayList<>();

    /**
     * Mark as received
     */
    public void markReceived(UUID receivedBy, String receivedByName) {
        if (!status.isEditable()) {
            throw new IllegalStateException("Cannot receive in current status: " + status);
        }
        this.status = StockReceiptStatus.RECEIVED;
        this.receivedById = receivedBy;
        this.receivedByName = receivedByName;
        this.receivedAt = LocalDateTime.now();
        this.actualDeliveryDate = LocalDate.now();
    }

    /**
     * Mark as inspected
     */
    public void markInspected(UUID inspectedBy, String inspectedByName, String notes) {
        if (!status.canBeInspected()) {
            throw new IllegalStateException("Cannot inspect in current status: " + status);
        }
        this.status = StockReceiptStatus.INSPECTED;
        this.inspectedById = inspectedBy;
        this.inspectedByName = inspectedByName;
        this.inspectedAt = LocalDateTime.now();
        this.inspectionNotes = notes;
    }

    /**
     * Approve receipt
     */
    public void approve(UUID approvedBy, String approvedByName) {
        if (!status.canBeApproved()) {
            throw new IllegalStateException("Cannot approve in current status: " + status);
        }
        this.status = StockReceiptStatus.APPROVED;
        this.approvedById = approvedBy;
        this.approvedByName = approvedByName;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Reject receipt
     */
    public void reject(String reason) {
        if (status.isFinal()) {
            throw new IllegalStateException("Cannot reject in final status: " + status);
        }
        this.status = StockReceiptStatus.REJECTED;
        this.rejectionReason = reason;
    }

    /**
     * Calculate totals from items
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            this.totalItems = items.size();
            this.totalQuantity = items.stream()
                    .map(StockReceiptItem::getQuantityReceived)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalValue = items.stream()
                    .map(item -> item.getUnitPrice().multiply(item.getQuantityReceived()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate final amount
            BigDecimal subtotal = totalValue;
            if (discountAmount != null) {
                subtotal = subtotal.subtract(discountAmount);
            }
            if (taxAmount != null) {
                subtotal = subtotal.add(taxAmount);
            }
            this.totalAmount = subtotal;
        }
    }
}
