package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.ParameterDataType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lab Test Parameter Entity.
 *
 * Test parameters and normal ranges with support for age and gender-specific ranges.
 * Supports numeric, text, boolean, and option-based parameters.
 * Includes delta check configuration for detecting unusual changes from previous results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_test_parameter", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_test_parameter_test", columnList = "lab_test_id"),
        @Index(name = "idx_lab_test_parameter_code", columnList = "parameter_code"),
        @Index(name = "idx_lab_test_parameter_name", columnList = "parameter_name"),
        @Index(name = "idx_lab_test_parameter_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabTestParameter extends SoftDeletableEntity {

    /**
     * Lab test reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_test_id", nullable = false)
    private LabTest labTest;

    /**
     * Parameter code
     */
    @Column(name = "parameter_code", nullable = false, length = 50)
    private String parameterCode;

    /**
     * Parameter name
     */
    @Column(name = "parameter_name", nullable = false, length = 200)
    private String parameterName;

    /**
     * Parameter short name
     */
    @Column(name = "parameter_short_name", length = 100)
    private String parameterShortName;

    // ========== Parameter Properties ==========

    /**
     * Data type (NUMERIC, TEXT, BOOLEAN, OPTION)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 50)
    private ParameterDataType dataType;

    /**
     * Unit of measurement
     */
    @Column(name = "unit", length = 50)
    private String unit;

    /**
     * Display order
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * Is mandatory parameter
     */
    @Column(name = "is_mandatory")
    @Builder.Default
    private Boolean isMandatory = true;

    // ========== Normal Range (General) ==========

    /**
     * Normal range low value
     */
    @Column(name = "normal_range_low", precision = 15, scale = 4)
    private BigDecimal normalRangeLow;

    /**
     * Normal range high value
     */
    @Column(name = "normal_range_high", precision = 15, scale = 4)
    private BigDecimal normalRangeHigh;

    /**
     * Normal range as text (e.g., "12-16 g/dL")
     */
    @Column(name = "normal_range_text", length = 500)
    private String normalRangeText;

    // ========== Age & Gender Specific Ranges ==========

    /**
     * Age and gender-specific ranges stored as JSONB
     * Format: [{"ageMin": 0, "ageMax": 1, "gender": "MALE", "low": 10.0, "high": 14.0}, ...]
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "age_gender_ranges")
    private String ageGenderRanges;

    // ========== Critical Values ==========

    /**
     * Critical low value
     */
    @Column(name = "critical_low", precision = 15, scale = 4)
    private BigDecimal criticalLow;

    /**
     * Critical high value
     */
    @Column(name = "critical_high", precision = 15, scale = 4)
    private BigDecimal criticalHigh;

    /**
     * Panic low value (more critical than critical)
     */
    @Column(name = "panic_low", precision = 15, scale = 4)
    private BigDecimal panicLow;

    /**
     * Panic high value (more critical than critical)
     */
    @Column(name = "panic_high", precision = 15, scale = 4)
    private BigDecimal panicHigh;

    // ========== For Option-Based Parameters ==========

    /**
     * Allowed values for OPTION data type (e.g., ["A", "B", "AB", "O"] for blood group)
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "allowed_values")
    private List<String> allowedValues;

    // ========== Delta Check ==========

    /**
     * Delta check enabled
     */
    @Column(name = "delta_check_enabled")
    @Builder.Default
    private Boolean deltaCheckEnabled = false;

    /**
     * Delta check percentage (e.g., 50 for 50% change)
     */
    @Column(name = "delta_check_percentage", precision = 5, scale = 2)
    private BigDecimal deltaCheckPercentage;

    /**
     * Delta check absolute value
     */
    @Column(name = "delta_check_absolute", precision = 15, scale = 4)
    private BigDecimal deltaCheckAbsolute;

    // ========== Calculated Parameters ==========

    /**
     * Is calculated parameter (calculated from other parameters)
     */
    @Column(name = "is_calculated")
    @Builder.Default
    private Boolean isCalculated = false;

    /**
     * Calculation formula
     */
    @Column(name = "calculation_formula", columnDefinition = "TEXT")
    private String calculationFormula;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // ========== Helper Methods ==========

    /**
     * Check if parameter is numeric
     */
    public boolean isNumeric() {
        return dataType == ParameterDataType.NUMERIC;
    }

    /**
     * Check if parameter has critical values
     */
    public boolean hasCriticalValues() {
        return criticalLow != null || criticalHigh != null;
    }

    /**
     * Check if parameter has panic values
     */
    public boolean hasPanicValues() {
        return panicLow != null || panicHigh != null;
    }

    /**
     * Check if delta check is enabled
     */
    public boolean isDeltaCheckEnabled() {
        return Boolean.TRUE.equals(deltaCheckEnabled);
    }

    /**
     * Check if value is within normal range
     */
    public boolean isWithinNormalRange(BigDecimal value) {
        if (!isNumeric() || value == null) {
            return false;
        }

        boolean aboveLow = normalRangeLow == null || value.compareTo(normalRangeLow) >= 0;
        boolean belowHigh = normalRangeHigh == null || value.compareTo(normalRangeHigh) <= 0;

        return aboveLow && belowHigh;
    }

    /**
     * Check if value is critical
     */
    public boolean isCriticalValue(BigDecimal value) {
        if (!isNumeric() || value == null) {
            return false;
        }

        boolean criticallyLow = criticalLow != null && value.compareTo(criticalLow) < 0;
        boolean criticallyHigh = criticalHigh != null && value.compareTo(criticalHigh) > 0;

        return criticallyLow || criticallyHigh;
    }

    /**
     * Check if value is panic level
     */
    public boolean isPanicValue(BigDecimal value) {
        if (!isNumeric() || value == null) {
            return false;
        }

        boolean panicLowLevel = panicLow != null && value.compareTo(panicLow) < 0;
        boolean panicHighLevel = panicHigh != null && value.compareTo(panicHigh) > 0;

        return panicLowLevel || panicHighLevel;
    }
}
