package com.yudha.hms.laboratory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Lab Test Category Request DTO.
 *
 * Used for creating and updating lab test categories.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestCategoryRequest {

    /**
     * Category code (unique identifier)
     */
    @NotBlank(message = "Kode kategori harus diisi")
    @Size(max = 50, message = "Kode kategori maksimal 50 karakter")
    private String code;

    /**
     * Category name
     */
    @NotBlank(message = "Nama kategori harus diisi")
    @Size(max = 200, message = "Nama kategori maksimal 200 karakter")
    private String name;

    /**
     * Category description
     */
    private String description;

    /**
     * Parent category ID (for hierarchical structure)
     */
    private UUID parentId;

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Icon name (for UI)
     */
    @Size(max = 100, message = "Nama icon maksimal 100 karakter")
    private String icon;

    /**
     * Color code (for UI)
     */
    @Size(max = 50, message = "Kode warna maksimal 50 karakter")
    private String color;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean active = true;
}
