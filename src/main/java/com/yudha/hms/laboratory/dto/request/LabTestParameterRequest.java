package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.ParameterDataType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lab Test Parameter Request DTO.
 *
 * Used for creating and updating test parameters that define
 * individual measurable components of a lab test.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestParameterRequest {

    /**
     * Test ID this parameter belongs to
     */
    @NotNull(message = "Test ID harus diisi")
    private UUID testId;

    /**
     * Parameter code
     */
    @NotBlank(message = "Kode parameter harus diisi")
    @Size(max = 50, message = "Kode parameter maksimal 50 karakter")
    private String parameterCode;

    /**
     * Parameter name
     */
    @NotBlank(message = "Nama parameter harus diisi")
    @Size(max = 200, message = "Nama parameter maksimal 200 karakter")
    private String parameterName;

    /**
     * Data type (NUMERIC, TEXT, OPTION)
     */
    @NotNull(message = "Tipe data harus dipilih")
    private ParameterDataType dataType;

    /**
     * Unit of measurement
     */
    @Size(max = 50, message = "Unit maksimal 50 karakter")
    private String unit;

    // ========== Normal Range ==========

    /**
     * Normal range minimum value
     */
    private BigDecimal normalRangeMin;

    /**
     * Normal range maximum value
     */
    private BigDecimal normalRangeMax;

    /**
     * Normal range text (for age/gender specific ranges)
     */
    private String normalRangeText;

    // ========== Critical Values ==========

    /**
     * Critical low value (requires immediate attention)
     */
    private BigDecimal criticalLowValue;

    /**
     * Critical high value (requires immediate attention)
     */
    private BigDecimal criticalHighValue;

    /**
     * Panic low value (life-threatening)
     */
    private BigDecimal panicLowValue;

    /**
     * Panic high value (life-threatening)
     */
    private BigDecimal panicHighValue;

    // ========== Delta Check ==========

    /**
     * Delta check enabled (compare with previous results)
     */
    @Builder.Default
    private Boolean deltaCheckEnabled = false;

    /**
     * Delta check percentage threshold
     */
    @DecimalMin(value = "0.0", message = "Persentase delta check tidak boleh negatif")
    @DecimalMax(value = "100.0", message = "Persentase delta check maksimal 100")
    private BigDecimal deltaCheckPercentage;

    /**
     * Delta check absolute difference threshold
     */
    @DecimalMin(value = "0.0", message = "Nilai absolut delta check tidak boleh negatif")
    private BigDecimal deltaCheckAbsolute;

    /**
     * Delta check time window in hours
     */
    @Min(value = 0, message = "Jendela waktu delta check tidak boleh negatif")
    private Integer deltaCheckTimeWindowHours;

    // ========== Display Configuration ==========

    /**
     * Display order
     */
    @Min(value = 0, message = "Urutan tampil tidak boleh negatif")
    private Integer displayOrder;

    /**
     * Decimal places for display
     */
    @Min(value = 0, message = "Jumlah desimal tidak boleh negatif")
    @Max(value = 10, message = "Jumlah desimal maksimal 10")
    private Integer decimalPlaces;

    // ========== Result Entry Configuration ==========

    /**
     * Is mandatory parameter
     */
    @Builder.Default
    private Boolean isMandatory = true;

    /**
     * Is calculated parameter (derived from other parameters)
     */
    @Builder.Default
    private Boolean isCalculated = false;

    /**
     * Calculation formula (if calculated)
     */
    private String calculationFormula;

    /**
     * Possible values (for option-type parameters)
     */
    private String possibleValues;

    /**
     * Default value
     */
    private String defaultValue;

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
