package com.yudha.hms.laboratory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Result Parameter Entry Request DTO.
 *
 * Used for entering individual parameter values within a test result.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultParameterEntryRequest {

    /**
     * Test parameter ID
     */
    @NotNull(message = "ID parameter test harus diisi")
    private UUID testParameterId;

    /**
     * Result value (generic string representation)
     */
    @Size(max = 1000, message = "Nilai hasil maksimal 1000 karakter")
    private String resultValue;

    /**
     * Numeric value (for numeric parameters)
     */
    private BigDecimal numericValue;

    /**
     * Text value (for text/option parameters)
     */
    private String textValue;

    /**
     * Unit of measurement
     */
    @Size(max = 50, message = "Unit maksimal 50 karakter")
    private String unit;

    /**
     * Interpretation flag
     */
    private String interpretationFlag;

    /**
     * Notes for this parameter
     */
    private String notes;
}
