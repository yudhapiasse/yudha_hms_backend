package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Stock Batch Entity.
 *
 * Tracks individual batches/lots of drugs for FIFO/FEFO inventory management.
 * Critical for expiry tracking and traceability.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "stock_batch", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_batch_drug", columnList = "drug_id"),
        @Index(name = "idx_batch_location", columnList = "location_id"),
        @Index(name = "idx_batch_number", columnList = "batch_number"),
        @Index(name = "idx_batch_expiry", columnList = "expiry_date"),
        @Index(name = "idx_batch_active", columnList = "active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_batch_drug_location_batch",
                         columnNames = {"drug_id", "location_id", "batch_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockBatch extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "batch_number", nullable = false, length = 50)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(name = "quantity_on_hand", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal quantityOnHand = BigDecimal.ZERO;

    @Column(name = "quantity_available", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal quantityAvailable = BigDecimal.ZERO;

    @Column(name = "quantity_reserved", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal quantityReserved = BigDecimal.ZERO;

    @Column(name = "quantity_quarantined", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal quantityQuarantined = BigDecimal.ZERO;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "supplier_name", length = 200)
    private String supplierName;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @Column(name = "is_quarantined")
    @Builder.Default
    private Boolean isQuarantined = false;

    @Column(name = "quarantine_reason", columnDefinition = "TEXT")
    private String quarantineReason;

    @Column(name = "is_expired")
    @Builder.Default
    private Boolean isExpired = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Add quantity to batch
     */
    public void addQuantity(BigDecimal quantity) {
        this.quantityOnHand = this.quantityOnHand.add(quantity);
        this.quantityAvailable = this.quantityAvailable.add(quantity);
    }

    /**
     * Remove quantity from batch (FIFO/FEFO)
     */
    public void removeQuantity(BigDecimal quantity) {
        if (quantityAvailable.compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient available quantity in batch");
        }
        this.quantityOnHand = this.quantityOnHand.subtract(quantity);
        this.quantityAvailable = this.quantityAvailable.subtract(quantity);
    }

    /**
     * Reserve quantity
     */
    public void reserveQuantity(BigDecimal quantity) {
        if (quantityAvailable.compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient available quantity to reserve");
        }
        this.quantityAvailable = this.quantityAvailable.subtract(quantity);
        this.quantityReserved = this.quantityReserved.add(quantity);
    }

    /**
     * Release reserved quantity
     */
    public void releaseReservation(BigDecimal quantity) {
        if (quantityReserved.compareTo(quantity) < 0) {
            throw new IllegalStateException("Cannot release more than reserved");
        }
        this.quantityReserved = this.quantityReserved.subtract(quantity);
        this.quantityAvailable = this.quantityAvailable.add(quantity);
    }

    /**
     * Quarantine batch
     */
    public void quarantine(String reason) {
        this.isQuarantined = true;
        this.quarantineReason = reason;
        this.quantityQuarantined = this.quantityAvailable;
        this.quantityAvailable = BigDecimal.ZERO;
    }

    /**
     * Release from quarantine
     */
    public void releaseFromQuarantine() {
        this.isQuarantined = false;
        this.quantityAvailable = this.quantityQuarantined;
        this.quantityQuarantined = BigDecimal.ZERO;
        this.quarantineReason = null;
    }

    /**
     * Check if batch is empty
     */
    public boolean isEmpty() {
        return quantityOnHand.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Check if batch has available stock
     */
    public boolean hasAvailableStock() {
        return quantityAvailable.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if batch is expired
     */
    public boolean checkExpired() {
        if (expiryDate == null) return false;
        boolean expired = expiryDate.isBefore(LocalDate.now());
        this.isExpired = expired;
        return expired;
    }

    /**
     * Get days until expiry
     */
    public long getDaysUntilExpiry() {
        if (expiryDate == null) return Long.MAX_VALUE;
        return LocalDate.now().until(expiryDate, java.time.temporal.ChronoUnit.DAYS);
    }

    /**
     * Check if expiry is approaching (within days)
     */
    public boolean isExpiryApproaching(int days) {
        return getDaysUntilExpiry() <= days && getDaysUntilExpiry() > 0;
    }

    /**
     * Get batch priority for FEFO (First Expiry First Out)
     * Lower number = higher priority
     */
    public long getFefoPriority() {
        return getDaysUntilExpiry();
    }

    /**
     * Update calculated fields
     */
    @PrePersist
    @PreUpdate
    protected void updateCalculations() {
        checkExpired();
        if (quantityOnHand.compareTo(BigDecimal.ZERO) == 0) {
            this.active = false;
        }
    }
}
