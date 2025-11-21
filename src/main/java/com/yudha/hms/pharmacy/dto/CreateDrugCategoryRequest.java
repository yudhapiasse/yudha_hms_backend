package com.yudha.hms.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Create Drug Category Request DTO.
 *
 * Request object for creating new drug category.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDrugCategoryRequest {

    /**
     * Category code
     */
    @NotBlank(message = "Category code is required")
    @Size(max = 50, message = "Category code must not exceed 50 characters")
    private String code;

    /**
     * Category name
     */
    @NotBlank(message = "Category name is required")
    @Size(max = 200, message = "Category name must not exceed 200 characters")
    private String name;

    /**
     * Description
     */
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    /**
     * Parent category ID
     */
    private UUID parentId;

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean active = true;
}
