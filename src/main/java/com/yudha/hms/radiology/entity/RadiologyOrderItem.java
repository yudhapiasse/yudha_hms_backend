package com.yudha.hms.radiology.entity;

import com.yudha.hms.radiology.constant.Laterality;
import com.yudha.hms.radiology.constant.OrderStatus;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Radiology Order Item Entity.
 *
 * Order items (similar to lab order items)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "radiology_order_item", schema = "radiology_schema", indexes = {
        @Index(name = "idx_radiology_order_item_order", columnList = "order_id"),
        @Index(name = "idx_radiology_order_item_examination", columnList = "examination_id"),
        @Index(name = "idx_radiology_order_item_status", columnList = "status"),
        @Index(name = "idx_radiology_order_item_result", columnList = "result_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RadiologyOrderItem extends BaseEntity {

    /**
     * Order reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private RadiologyOrder order;

    /**
     * Examination reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false)
    private RadiologyExamination examination;

    /**
     * Exam code (denormalized for performance)
     */
    @Column(name = "exam_code", nullable = false, length = 50)
    private String examCode;

    /**
     * Exam name (denormalized for performance)
     */
    @Column(name = "exam_name", nullable = false, length = 200)
    private String examName;

    /**
     * Laterality (for applicable examinations)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "laterality", length = 50)
    private Laterality laterality;

    /**
     * Quantity
     */
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Unit price
     */
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Discount amount
     */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * Final price
     */
    @Column(name = "final_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalPrice;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Result reference
     */
    @Column(name = "result_id")
    private UUID resultId;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
