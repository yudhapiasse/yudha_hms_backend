package com.yudha.hms.pharmacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dispensing Item Entity.
 *
 * Represents individual drug items in a dispensing transaction with batch tracking.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "dispensing_item", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_dispensing_item_dispensing", columnList = "dispensing_id"),
        @Index(name = "idx_dispensing_item_drug", columnList = "drug_id"),
        @Index(name = "idx_dispensing_item_batch", columnList = "batch_id"),
        @Index(name = "idx_dispensing_item_prescription_item", columnList = "prescription_item_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispensingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_id", nullable = false)
    private Dispensing dispensing;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_item_id")
    private PrescriptionItem prescriptionItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "quantity_prescribed", precision = 10, scale = 2)
    private BigDecimal quantityPrescribed;

    @Column(name = "quantity_dispensed", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityDispensed = BigDecimal.ZERO;

    @Column(name = "quantity_returned", precision = 10, scale = 2)
    private BigDecimal quantityReturned = BigDecimal.ZERO;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 15, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "dosage_instruction", columnDefinition = "TEXT")
    private String dosageInstruction;

    @Column(name = "frequency", length = 100)
    private String frequency;

    @Column(name = "duration", length = 100)
    private String duration;

    @Column(name = "route", length = 50)
    private String route;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "barcode_verified")
    private Boolean barcodeVerified = false;

    @Column(name = "barcode_scanned_at")
    private LocalDateTime barcodeScannedAt;

    @Column(name = "substituted")
    private Boolean substituted = false;

    @Column(name = "substitution_reason", columnDefinition = "TEXT")
    private String substitutionReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    // Business methods

    /**
     * Check if fully dispensed
     */
    public boolean isFullyDispensed() {
        if (quantityPrescribed == null) {
            return true;
        }
        return quantityDispensed.compareTo(quantityPrescribed) >= 0;
    }

    /**
     * Check if partially dispensed
     */
    public boolean isPartiallyDispensed() {
        if (quantityPrescribed == null) {
            return false;
        }
        return quantityDispensed.compareTo(BigDecimal.ZERO) > 0
            && quantityDispensed.compareTo(quantityPrescribed) < 0;
    }

    /**
     * Check if item has been returned
     */
    public boolean hasReturns() {
        return quantityReturned != null && quantityReturned.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Get net dispensed quantity after returns
     */
    public BigDecimal getNetDispensedQuantity() {
        BigDecimal returned = quantityReturned != null ? quantityReturned : BigDecimal.ZERO;
        return quantityDispensed.subtract(returned);
    }

    /**
     * Scan barcode for verification
     */
    public void scanBarcode() {
        this.barcodeVerified = true;
        this.barcodeScannedAt = LocalDateTime.now();
    }

    /**
     * Mark as substituted
     */
    public void markAsSubstituted(String reason) {
        this.substituted = true;
        this.substitutionReason = reason;
    }

    /**
     * Process return
     */
    public void processReturn(BigDecimal returnQuantity) {
        if (returnQuantity.compareTo(quantityDispensed) > 0) {
            throw new IllegalArgumentException("Return quantity cannot exceed dispensed quantity");
        }
        this.quantityReturned = (this.quantityReturned != null ? this.quantityReturned : BigDecimal.ZERO)
                .add(returnQuantity);
    }

    /**
     * Calculate total amount
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotal() {
        BigDecimal subtotal = unitPrice.multiply(quantityDispensed);
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        this.totalAmount = subtotal.subtract(discount).add(tax);
    }
}
