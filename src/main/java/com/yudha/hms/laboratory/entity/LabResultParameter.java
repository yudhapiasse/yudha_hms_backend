package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.InterpretationFlag;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Lab Result Parameter Entity.
 *
 * Individual parameter results within a test.
 * Stores actual result values with reference ranges and interpretation flags.
 * Supports delta check and automatic flagging of abnormal values.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_result_parameter", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_result_parameter_result", columnList = "result_id"),
        @Index(name = "idx_lab_result_parameter_test_param", columnList = "test_parameter_id"),
        @Index(name = "idx_lab_result_parameter_abnormal", columnList = "is_abnormal"),
        @Index(name = "idx_lab_result_parameter_critical", columnList = "is_critical"),
        @Index(name = "idx_lab_result_parameter_delta", columnList = "delta_check_flagged")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabResultParameter extends BaseEntity {

    /**
     * Result reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private LabResult result;

    /**
     * Test parameter reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_parameter_id", nullable = false)
    private LabTestParameter testParameter;

    // ========== Parameter Details (Denormalized) ==========

    /**
     * Parameter code (denormalized)
     */
    @Column(name = "parameter_code", nullable = false, length = 50)
    private String parameterCode;

    /**
     * Parameter name (denormalized)
     */
    @Column(name = "parameter_name", nullable = false, length = 200)
    private String parameterName;

    // ========== Result Value ==========

    /**
     * Result value (as string for all types)
     */
    @Column(name = "result_value", length = 1000)
    private String resultValue;

    /**
     * Numeric value (for numeric results)
     */
    @Column(name = "numeric_value", precision = 15, scale = 4)
    private BigDecimal numericValue;

    /**
     * Text value (for text results)
     */
    @Column(name = "text_value", columnDefinition = "TEXT")
    private String textValue;

    /**
     * Unit of measurement
     */
    @Column(name = "unit", length = 50)
    private String unit;

    // ========== Reference Range (Copied at Time of Result) ==========

    /**
     * Reference range low value
     */
    @Column(name = "reference_range_low", precision = 15, scale = 4)
    private BigDecimal referenceRangeLow;

    /**
     * Reference range high value
     */
    @Column(name = "reference_range_high", precision = 15, scale = 4)
    private BigDecimal referenceRangeHigh;

    /**
     * Reference range as text
     */
    @Column(name = "reference_range_text", length = 500)
    private String referenceRangeText;

    // ========== Interpretation Flags ==========

    /**
     * Interpretation flag (NORMAL, LOW, HIGH, CRITICAL_LOW, CRITICAL_HIGH, ABNORMAL)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "interpretation_flag", length = 50)
    private InterpretationFlag interpretationFlag;

    /**
     * Is abnormal (automatic flag)
     */
    @Column(name = "is_abnormal")
    @Builder.Default
    private Boolean isAbnormal = false;

    /**
     * Is critical (automatic flag)
     */
    @Column(name = "is_critical")
    @Builder.Default
    private Boolean isCritical = false;

    // ========== Delta Check ==========

    /**
     * Delta check flagged
     */
    @Column(name = "delta_check_flagged")
    @Builder.Default
    private Boolean deltaCheckFlagged = false;

    /**
     * Previous value (for delta check comparison)
     */
    @Column(name = "previous_value", precision = 15, scale = 4)
    private BigDecimal previousValue;

    /**
     * Delta percentage change
     */
    @Column(name = "delta_percentage", precision = 10, scale = 2)
    private BigDecimal deltaPercentage;

    /**
     * Delta absolute change
     */
    @Column(name = "delta_absolute", precision = 15, scale = 4)
    private BigDecimal deltaAbsolute;

    // ========== Method and Equipment ==========

    /**
     * Test method used
     */
    @Column(name = "test_method", length = 200)
    private String testMethod;

    /**
     * Equipment ID
     */
    @Column(name = "equipment_id", length = 100)
    private String equipmentId;

    /**
     * Equipment name
     */
    @Column(name = "equipment_name", length = 200)
    private String equipmentName;

    // ========== QC Reference ==========

    /**
     * QC level used
     */
    @Column(name = "qc_level", length = 50)
    private String qcLevel;

    /**
     * QC within range
     */
    @Column(name = "qc_within_range")
    private Boolean qcWithinRange;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Helper Methods ==========

    /**
     * Check if result is abnormal
     */
    public boolean isAbnormal() {
        return Boolean.TRUE.equals(isAbnormal);
    }

    /**
     * Check if result is critical
     */
    public boolean isCritical() {
        return Boolean.TRUE.equals(isCritical);
    }

    /**
     * Check if delta check was flagged
     */
    public boolean isDeltaCheckFlagged() {
        return Boolean.TRUE.equals(deltaCheckFlagged);
    }

    /**
     * Check if result is within normal range
     */
    public boolean isWithinNormalRange() {
        if (numericValue == null || referenceRangeLow == null || referenceRangeHigh == null) {
            return false;
        }
        return numericValue.compareTo(referenceRangeLow) >= 0 &&
               numericValue.compareTo(referenceRangeHigh) <= 0;
    }

    /**
     * Calculate and set interpretation flag based on numeric value
     */
    public void calculateInterpretationFlag(BigDecimal criticalLow, BigDecimal criticalHigh) {
        if (numericValue == null) {
            this.interpretationFlag = InterpretationFlag.NORMAL;
            this.isAbnormal = false;
            this.isCritical = false;
            return;
        }

        // Check critical values first
        if (criticalLow != null && numericValue.compareTo(criticalLow) < 0) {
            this.interpretationFlag = InterpretationFlag.CRITICAL_LOW;
            this.isAbnormal = true;
            this.isCritical = true;
        } else if (criticalHigh != null && numericValue.compareTo(criticalHigh) > 0) {
            this.interpretationFlag = InterpretationFlag.CRITICAL_HIGH;
            this.isAbnormal = true;
            this.isCritical = true;
        }
        // Check normal range
        else if (referenceRangeLow != null && numericValue.compareTo(referenceRangeLow) < 0) {
            this.interpretationFlag = InterpretationFlag.LOW;
            this.isAbnormal = true;
            this.isCritical = false;
        } else if (referenceRangeHigh != null && numericValue.compareTo(referenceRangeHigh) > 0) {
            this.interpretationFlag = InterpretationFlag.HIGH;
            this.isAbnormal = true;
            this.isCritical = false;
        } else {
            this.interpretationFlag = InterpretationFlag.NORMAL;
            this.isAbnormal = false;
            this.isCritical = false;
        }
    }

    /**
     * Calculate delta check
     */
    public void calculateDeltaCheck(BigDecimal previousValue, BigDecimal deltaCheckPercentage, BigDecimal deltaCheckAbsolute) {
        if (numericValue == null || previousValue == null) {
            this.deltaCheckFlagged = false;
            return;
        }

        this.previousValue = previousValue;

        // Calculate percentage change
        if (previousValue.compareTo(BigDecimal.ZERO) != 0) {
            this.deltaPercentage = numericValue.subtract(previousValue)
                    .divide(previousValue, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .abs();
        }

        // Calculate absolute change
        this.deltaAbsolute = numericValue.subtract(previousValue).abs();

        // Check if flagged
        boolean percentageExceeded = deltaCheckPercentage != null &&
                                     this.deltaPercentage != null &&
                                     this.deltaPercentage.compareTo(deltaCheckPercentage) > 0;
        boolean absoluteExceeded = deltaCheckAbsolute != null &&
                                   this.deltaAbsolute.compareTo(deltaCheckAbsolute) > 0;

        this.deltaCheckFlagged = percentageExceeded || absoluteExceeded;
    }
}
