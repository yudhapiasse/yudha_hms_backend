package com.yudha.hms.laboratory.entity;

import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Order Item Entity.
 *
 * Individual tests or panels in a laboratory order.
 * Links to either a test or a panel, tracks status, pricing, and results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_order_item", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_order_item_order", columnList = "order_id"),
        @Index(name = "idx_lab_order_item_test", columnList = "test_id"),
        @Index(name = "idx_lab_order_item_panel", columnList = "panel_id"),
        @Index(name = "idx_lab_order_item_status", columnList = "status"),
        @Index(name = "idx_lab_order_item_specimen", columnList = "specimen_id"),
        @Index(name = "idx_lab_order_item_result", columnList = "result_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabOrderItem extends BaseEntity {

    /**
     * Order reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder order;

    // ========== Test or Panel ==========

    /**
     * Test reference (if ordering individual test)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private LabTest test;

    /**
     * Panel reference (if ordering panel)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_id")
    private LabPanel panel;

    /**
     * Item type (TEST or PANEL)
     */
    @Column(name = "item_type", nullable = false, length = 50)
    private String itemType;

    // ========== Item Details (Denormalized for Performance) ==========

    /**
     * Test/Panel name (denormalized)
     */
    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    /**
     * Test/Panel code (denormalized)
     */
    @Column(name = "test_code", nullable = false, length = 50)
    private String testCode;

    // ========== Status ==========

    /**
     * Item status
     */
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "PENDING";

    // ========== Pricing ==========

    /**
     * Unit price
     */
    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    /**
     * Discount amount
     */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * Final price (after discount)
     */
    @Column(name = "final_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal finalPrice;

    // ========== Sample and Result Tracking ==========

    /**
     * Specimen ID (once sample is collected)
     */
    @Column(name = "specimen_id")
    private UUID specimenId;

    /**
     * Result ID (once result is available)
     */
    @Column(name = "result_id")
    private UUID resultId;

    /**
     * Result completed timestamp
     */
    @Column(name = "result_completed_at")
    private LocalDateTime resultCompletedAt;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Helper Methods ==========

    /**
     * Check if item is for a test (not panel)
     */
    public boolean isTest() {
        return "TEST".equals(itemType);
    }

    /**
     * Check if item is for a panel
     */
    public boolean isPanel() {
        return "PANEL".equals(itemType);
    }

    /**
     * Check if specimen has been collected
     */
    public boolean hasSpecimen() {
        return specimenId != null;
    }

    /**
     * Check if result is available
     */
    public boolean hasResult() {
        return resultId != null;
    }

    /**
     * Calculate final price
     */
    public void calculateFinalPrice() {
        if (unitPrice != null && discountAmount != null) {
            this.finalPrice = unitPrice.subtract(discountAmount);
            if (this.finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                this.finalPrice = BigDecimal.ZERO;
            }
        } else if (unitPrice != null) {
            this.finalPrice = unitPrice;
        }
    }
}
