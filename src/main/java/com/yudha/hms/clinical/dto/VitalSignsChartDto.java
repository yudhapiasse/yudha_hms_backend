package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for vital signs charting/graphing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignsChartDto {

    private LocalDateTime measurementTime;

    // Basic vitals (for charting)
    private Integer systolicBp;

    private Integer diastolicBp;

    private Integer heartRate;

    private Integer respiratoryRate;

    private BigDecimal temperature;

    private Integer spo2;

    private Integer painScore;

    private Integer gcsTotal;

    // Flags for visual indicators
    private Boolean isAbnormal;

    private Boolean requiresNotification;
}
