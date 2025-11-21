package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.InterpretationFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Result Parameter Response DTO.
 *
 * Response for individual parameter result.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultParameterResponse {

    /**
     * Parameter result ID
     */
    private UUID id;

    /**
     * Result ID
     */
    private UUID resultId;

    /**
     * Test parameter ID
     */
    private UUID testParameterId;

    // ========== Parameter Details ==========

    /**
     * Parameter code
     */
    private String parameterCode;

    /**
     * Parameter name
     */
    private String parameterName;

    // ========== Result Value ==========

    /**
     * Result value (string representation)
     */
    private String resultValue;

    /**
     * Numeric value
     */
    private BigDecimal numericValue;

    /**
     * Text value
     */
    private String textValue;

    /**
     * Unit
     */
    private String unit;

    // ========== Reference Range ==========

    /**
     * Reference range low
     */
    private BigDecimal referenceRangeLow;

    /**
     * Reference range high
     */
    private BigDecimal referenceRangeHigh;

    /**
     * Reference range text
     */
    private String referenceRangeText;

    // ========== Interpretation Flags ==========

    /**
     * Interpretation flag
     */
    private InterpretationFlag interpretationFlag;

    /**
     * Is abnormal
     */
    private Boolean isAbnormal;

    /**
     * Is critical
     */
    private Boolean isCritical;

    // ========== Delta Check ==========

    /**
     * Delta check flagged
     */
    private Boolean deltaCheckFlagged;

    /**
     * Previous value
     */
    private BigDecimal previousValue;

    /**
     * Delta percentage
     */
    private BigDecimal deltaPercentage;

    /**
     * Delta absolute
     */
    private BigDecimal deltaAbsolute;

    // ========== Method and Equipment ==========

    /**
     * Test method
     */
    private String testMethod;

    /**
     * Equipment ID
     */
    private String equipmentId;

    /**
     * Equipment name
     */
    private String equipmentName;

    // ========== QC Reference ==========

    /**
     * QC level
     */
    private String qcLevel;

    /**
     * QC within range
     */
    private Boolean qcWithinRange;

    /**
     * Notes
     */
    private String notes;

    // ========== Audit Fields ==========

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
