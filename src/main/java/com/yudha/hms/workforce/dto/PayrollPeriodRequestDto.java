package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yudha.hms.workforce.constant.ThrType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating or updating a payroll period.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPeriodRequestDto {

    /**
     * Period year (e.g., 2025)
     */
    @NotNull(message = "Period year is required")
    private Integer periodYear;

    /**
     * Period month (1-12)
     */
    @NotNull(message = "Period month is required")
    private Integer periodMonth;

    /**
     * Period name/description
     */
    private String periodName;

    /**
     * Start date of the payroll period
     */
    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * End date of the payroll period
     */
    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * Payment date (when salaries will be paid)
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    /**
     * Cut-off date for attendance and other data
     */
    @NotNull(message = "Cut-off date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cutOffDate;

    /**
     * Is this a THR (holiday allowance) period?
     */
    private Boolean isThrPeriod;

    /**
     * THR type if this is a THR period
     */
    private ThrType thrType;

    /**
     * Notes or remarks
     */
    private String notes;
}
