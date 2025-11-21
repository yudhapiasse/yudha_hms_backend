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
 * Patient Result History Response DTO.
 *
 * Response for patient's historical results for trending analysis.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResultHistoryResponse {

    /**
     * Result ID
     */
    private UUID resultId;

    /**
     * Result number
     */
    private String resultNumber;

    /**
     * Result date
     */
    private LocalDateTime resultDate;

    // ========== Test Information ==========

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

    // ========== Parameter Information ==========

    /**
     * Parameter ID
     */
    private UUID parameterId;

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
     * Result value
     */
    private String resultValue;

    /**
     * Numeric value
     */
    private BigDecimal numericValue;

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

    // ========== Interpretation ==========

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

    // ========== Order Information ==========

    /**
     * Order ID
     */
    private UUID orderId;

    /**
     * Order number
     */
    private String orderNumber;

    /**
     * Ordering doctor name
     */
    private String orderingDoctorName;

    /**
     * Clinical indication
     */
    private String clinicalIndication;
}
