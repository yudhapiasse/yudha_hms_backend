package com.yudha.hms.laboratory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Lab Panel Response DTO.
 *
 * Response for lab panel (test package) information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabPanelResponse {

    /**
     * Panel ID
     */
    private UUID id;

    /**
     * Panel code
     */
    private String panelCode;

    /**
     * Panel name
     */
    private String name;

    /**
     * Short name
     */
    private String shortName;

    /**
     * Description
     */
    private String description;

    /**
     * Category ID
     */
    private UUID categoryId;

    /**
     * Category name
     */
    private String categoryName;

    // ========== Pricing ==========

    /**
     * Package price
     */
    private BigDecimal packagePrice;

    /**
     * BPJS package tariff
     */
    private BigDecimal bpjsPackageTariff;

    /**
     * Discount percentage
     */
    private BigDecimal discountPercentage;

    /**
     * Total individual price (sum of all tests)
     */
    private BigDecimal totalIndividualPrice;

    /**
     * Savings amount
     */
    private BigDecimal savingsAmount;

    // ========== Test Items ==========

    /**
     * Tests included in panel
     */
    private List<PanelTestItemResponse> testItems;

    /**
     * Total number of tests
     */
    private Integer totalTests;

    /**
     * Number of mandatory tests
     */
    private Integer mandatoryTests;

    // ========== Clinical Information ==========

    /**
     * Clinical indication
     */
    private String clinicalIndication;

    /**
     * Preparation instructions
     */
    private String preparationInstructions;

    // ========== Display Configuration ==========

    /**
     * Is popular
     */
    private Boolean isPopular;

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Icon name
     */
    private String icon;

    /**
     * Color code
     */
    private String color;

    /**
     * Notes
     */
    private String notes;

    /**
     * Active status
     */
    private Boolean active;

    /**
     * Created timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Created by user ID
     */
    private String createdBy;

    /**
     * Updated timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by user ID
     */
    private String updatedBy;
}
