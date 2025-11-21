package com.yudha.hms.laboratory.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lab Panel Request DTO.
 *
 * Used for creating and updating lab test panels (packages of tests).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabPanelRequest {

    /**
     * Panel code (unique identifier)
     */
    @NotBlank(message = "Kode panel harus diisi")
    @Size(max = 50, message = "Kode panel maksimal 50 karakter")
    private String panelCode;

    /**
     * Panel name
     */
    @NotBlank(message = "Nama panel harus diisi")
    @Size(max = 200, message = "Nama panel maksimal 200 karakter")
    private String name;

    /**
     * Short name
     */
    @Size(max = 100, message = "Nama singkat maksimal 100 karakter")
    private String shortName;

    /**
     * Panel description
     */
    private String description;

    /**
     * Category ID
     */
    @NotNull(message = "Kategori panel harus dipilih")
    private UUID categoryId;

    // ========== Pricing ==========

    /**
     * Package price (total for all tests)
     */
    @NotNull(message = "Harga paket harus diisi")
    @DecimalMin(value = "0.0", inclusive = false, message = "Harga paket harus lebih dari 0")
    private BigDecimal packagePrice;

    /**
     * BPJS package tariff
     */
    @DecimalMin(value = "0.0", message = "Tarif BPJS tidak boleh negatif")
    private BigDecimal bpjsPackageTariff;

    /**
     * Discount percentage (compared to individual test prices)
     */
    @DecimalMin(value = "0.0", message = "Persentase diskon tidak boleh negatif")
    @DecimalMax(value = "100.0", message = "Persentase diskon maksimal 100")
    private BigDecimal discountPercentage;

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
     * Is popular panel (for quick access)
     */
    @Builder.Default
    private Boolean isPopular = false;

    /**
     * Display order
     */
    @Min(value = 0, message = "Urutan tampil tidak boleh negatif")
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

    // ========== Additional Information ==========

    /**
     * Notes
     */
    private String notes;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean active = true;
}
