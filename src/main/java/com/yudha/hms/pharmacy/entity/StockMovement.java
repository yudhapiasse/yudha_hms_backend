package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.StockMovementType;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stock Movement Entity.
 *
 * Records all stock movements for complete inventory traceability.
 * Supports FIFO/FEFO tracking through batch references.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "stock_movement", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_movement_drug", columnList = "drug_id"),
        @Index(name = "idx_movement_batch", columnList = "batch_id"),
        @Index(name = "idx_movement_location", columnList = "location_id"),
        @Index(name = "idx_movement_type", columnList = "movement_type"),
        @Index(name = "idx_movement_date", columnList = "movement_date"),
        @Index(name = "idx_movement_reference", columnList = "reference_type, reference_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockMovement extends BaseEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 50)
    private StockMovementType movementType;

    @Column(name = "quantity", precision = 15, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "quantity_before", precision = 15, scale = 2)
    private BigDecimal quantityBefore;

    @Column(name = "quantity_after", precision = 15, scale = 2)
    private BigDecimal quantityAfter;

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    @Column(name = "reference_type", length = 50)
    private String referenceType; // e.g., "RECEIPT", "PRESCRIPTION", "ADJUSTMENT", "TRANSFER"

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reference_number", length = 50)
    private String referenceNumber;

    @Column(name = "performed_by_id")
    private UUID performedById;

    @Column(name = "performed_by_name", length = 200)
    private String performedByName;

    @Column(name = "from_location_id")
    private UUID fromLocationId;

    @Column(name = "from_location_name", length = 200)
    private String fromLocationName;

    @Column(name = "to_location_id")
    private UUID toLocationId;

    @Column(name = "to_location_name", length = 200)
    private String toLocationName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Calculate totals
     */
    @PrePersist
    @PreUpdate
    protected void calculateTotals() {
        if (quantity != null && unitCost != null) {
            this.totalCost = quantity.multiply(unitCost);
        }
        if (movementDate == null) {
            this.movementDate = LocalDateTime.now();
        }
    }

    /**
     * Check if movement increases stock
     */
    public boolean isInbound() {
        return movementType.increasesStock();
    }

    /**
     * Check if movement decreases stock
     */
    public boolean isOutbound() {
        return movementType.decreasesStock();
    }

    /**
     * Get effective quantity (positive for inbound, negative for outbound)
     */
    public BigDecimal getEffectiveQuantity() {
        return isInbound() ? quantity : quantity.negate();
    }
}
