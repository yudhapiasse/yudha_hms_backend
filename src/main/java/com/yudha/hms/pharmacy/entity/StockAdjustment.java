package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.AdjustmentReason;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stock Adjustment Entity.
 *
 * Records manual stock adjustments (increases or decreases).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "stock_adjustment", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_adjustment_number", columnList = "adjustment_number", unique = true),
        @Index(name = "idx_adjustment_drug", columnList = "drug_id"),
        @Index(name = "idx_adjustment_batch", columnList = "batch_id"),
        @Index(name = "idx_adjustment_location", columnList = "location_id"),
        @Index(name = "idx_adjustment_date", columnList = "adjustment_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockAdjustment extends SoftDeletableEntity {

    @Column(name = "adjustment_number", nullable = false, unique = true, length = 50)
    private String adjustmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private StockBatch batch;

    @Column(name = "location_id", nullable = false)
    private UUID locationId;

    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "adjustment_date", nullable = false)
    private LocalDate adjustmentDate;

    @Column(name = "quantity_before", precision = 15, scale = 2)
    private BigDecimal quantityBefore;

    @Column(name = "quantity_adjusted", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantityAdjusted;

    @Column(name = "quantity_after", precision = 15, scale = 2)
    private BigDecimal quantityAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private AdjustmentReason reason;

    @Column(name = "reason_details", columnDefinition = "TEXT")
    private String reasonDetails;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "adjusted_by_id", nullable = false)
    private UUID adjustedById;

    @Column(name = "adjusted_by_name", length = 200)
    private String adjustedByName;

    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approved_by_name", length = 200)
    private String approvedByName;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "is_approved")
    @Builder.Default
    private Boolean isApproved = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Approve adjustment
     */
    public void approve(UUID approvedBy, String approvedByName) {
        this.isApproved = true;
        this.approvedById = approvedBy;
        this.approvedByName = approvedByName;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * Check if adjustment is an increase
     */
    public boolean isIncrease() {
        return quantityAdjusted.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if adjustment is a decrease
     */
    public boolean isDecrease() {
        return quantityAdjusted.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Calculate totals
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotals() {
        if (quantityAdjusted != null && unitCost != null) {
            this.totalCost = quantityAdjusted.abs().multiply(unitCost);
        }
        if (quantityBefore != null && quantityAdjusted != null) {
            this.quantityAfter = quantityBefore.add(quantityAdjusted);
        }
    }
}
