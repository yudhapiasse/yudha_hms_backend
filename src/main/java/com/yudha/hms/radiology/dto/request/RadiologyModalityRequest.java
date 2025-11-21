package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Radiology Modality Request DTO.
 *
 * Used for creating and updating radiology modalities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyModalityRequest {

    /**
     * Modality code (unique identifier)
     */
    @NotBlank(message = "Kode modalitas harus diisi")
    @Size(max = 50, message = "Kode modalitas maksimal 50 karakter")
    private String code;

    /**
     * Modality name
     */
    @NotBlank(message = "Nama modalitas harus diisi")
    @Size(max = 200, message = "Nama modalitas maksimal 200 karakter")
    private String name;

    /**
     * Modality description
     */
    private String description;

    /**
     * Whether this modality requires radiation exposure
     */
    @Builder.Default
    private Boolean requiresRadiation = false;

    /**
     * Average duration in minutes
     */
    private Integer averageDurationMinutes;

    /**
     * Display order for UI
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
    private Boolean isActive = true;
}
