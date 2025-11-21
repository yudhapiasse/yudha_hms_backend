package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.ParameterDataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Test Parameter Response DTO.
 *
 * Response for lab test parameter information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestParameterResponse {

    /**
     * Parameter ID
     */
    private UUID id;

    /**
     * Test ID
     */
    private UUID testId;

    /**
     * Test code
     */
    private String testCode;

    /**
     * Test name
     */
    private String testName;

    /**
     * Parameter code
     */
    private String parameterCode;

    /**
     * Parameter name
     */
    private String parameterName;

    /**
     * Data type
     */
    private ParameterDataType dataType;

    /**
     * Unit of measurement
     */
    private String unit;

    // ========== Normal Range ==========

    /**
     * Normal range minimum
     */
    private BigDecimal normalRangeMin;

    /**
     * Normal range maximum
     */
    private BigDecimal normalRangeMax;

    /**
     * Normal range text
     */
    private String normalRangeText;

    // ========== Critical Values ==========

    /**
     * Critical low value
     */
    private BigDecimal criticalLowValue;

    /**
     * Critical high value
     */
    private BigDecimal criticalHighValue;

    /**
     * Panic low value
     */
    private BigDecimal panicLowValue;

    /**
     * Panic high value
     */
    private BigDecimal panicHighValue;

    // ========== Delta Check ==========

    /**
     * Delta check enabled
     */
    private Boolean deltaCheckEnabled;

    /**
     * Delta check percentage
     */
    private BigDecimal deltaCheckPercentage;

    /**
     * Delta check absolute
     */
    private BigDecimal deltaCheckAbsolute;

    /**
     * Delta check time window hours
     */
    private Integer deltaCheckTimeWindowHours;

    // ========== Display Configuration ==========

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Decimal places
     */
    private Integer decimalPlaces;

    // ========== Result Entry Configuration ==========

    /**
     * Is mandatory
     */
    private Boolean isMandatory;

    /**
     * Is calculated
     */
    private Boolean isCalculated;

    /**
     * Calculation formula
     */
    private String calculationFormula;

    /**
     * Possible values
     */
    private String possibleValues;

    /**
     * Default value
     */
    private String defaultValue;

    /**
     * Notes
     */
    private String notes;

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
