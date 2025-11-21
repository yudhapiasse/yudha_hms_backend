package com.yudha.hms.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Drug Category Response DTO.
 *
 * Response object for drug category information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugCategoryResponse {

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
     * Description
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
     * Full category path
     */
    private String fullPath;

    /**
     * Hierarchy level
     */
    private Integer level;

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Active status
     */
    private Boolean active;

    /**
     * Created timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
