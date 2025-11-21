package com.yudha.hms.laboratory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Test Category Response DTO.
 *
 * Response for lab test category information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestCategoryResponse {

    /**
     * Category ID
     */
    private UUID id;

    /**
     * Category code
     */
    private String code;

    /**
     * Category name
     */
    private String name;

    /**
     * Category description
     */
    private String description;

    /**
     * Parent category ID
     */
    private UUID parentId;

    /**
     * Parent category name
     */
    private String parentName;

    /**
     * Hierarchy level (0 = root)
     */
    private Integer level;

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Icon name (for UI)
     */
    private String icon;

    /**
     * Color code (for UI)
     */
    private String color;

    /**
     * Number of tests in this category
     */
    private Long testCount;

    /**
     * Number of active tests in this category
     */
    private Long activeTestCount;

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
