package com.yudha.hms.laboratory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Order Item Response DTO.
 *
 * Response for individual test or panel within an order.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabOrderItemResponse {

    /**
     * Item ID
     */
    private UUID id;

    /**
     * Order ID
     */
    private UUID orderId;

    // ========== Test or Panel ==========

    /**
     * Test ID (if item is a test)
     */
    private UUID testId;

    /**
     * Panel ID (if item is a panel)
     */
    private UUID panelId;

    /**
     * Item type (TEST or PANEL)
     */
    private String itemType;

    /**
     * Test/Panel code
     */
    private String testCode;

    /**
     * Test/Panel name
     */
    private String testName;

    // ========== Status ==========

    /**
     * Item status
     */
    private String status;

    // ========== Pricing ==========

    /**
     * Unit price
     */
    private BigDecimal unitPrice;

    /**
     * Discount amount
     */
    private BigDecimal discountAmount;

    /**
     * Final price
     */
    private BigDecimal finalPrice;

    // ========== Sample and Result Tracking ==========

    /**
     * Specimen ID
     */
    private UUID specimenId;

    /**
     * Specimen number
     */
    private String specimenNumber;

    /**
     * Result ID
     */
    private UUID resultId;

    /**
     * Result status
     */
    private String resultStatus;

    /**
     * Result completed at
     */
    private LocalDateTime resultCompletedAt;

    /**
     * Notes
     */
    private String notes;

    // ========== Audit Fields ==========

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
