package com.yudha.hms.laboratory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Panel Test Item Request DTO.
 *
 * Used for adding tests to a panel.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanelTestItemRequest {

    /**
     * Panel ID
     */
    @NotNull(message = "Panel ID harus diisi")
    private UUID panelId;

    /**
     * Test ID
     */
    @NotNull(message = "Test ID harus diisi")
    private UUID testId;

    /**
     * Is mandatory test in panel
     */
    @Builder.Default
    private Boolean isMandatory = true;

    /**
     * Display order within panel
     */
    @Min(value = 0, message = "Urutan tampil tidak boleh negatif")
    private Integer displayOrder;

    /**
     * Notes
     */
    private String notes;
}
