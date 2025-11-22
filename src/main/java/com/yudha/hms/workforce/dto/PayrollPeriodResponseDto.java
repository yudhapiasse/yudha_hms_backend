package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yudha.hms.workforce.constant.PayrollPeriodStatus;
import com.yudha.hms.workforce.constant.ThrType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for payroll period queries.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayrollPeriodResponseDto {

    /**
     * Payroll period ID
     */
    private UUID id;

    /**
     * Period year
     */
    private Integer periodYear;

    /**
     * Period month
     */
    private Integer periodMonth;

    /**
     * Period code (e.g., "2025-01")
     */
    private String periodCode;

    /**
     * Period name
     */
    private String periodName;

    /**
     * Start date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * End date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * Payment date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    /**
     * Cut-off date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cutOffDate;

    /**
     * Payroll period status
     */
    private PayrollPeriodStatus status;

    /**
     * Is this a THR period?
     */
    private Boolean isThrPeriod;

    /**
     * THR type if applicable
     */
    private ThrType thrType;

    /**
     * Processing started timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processingStartedAt;

    /**
     * Processing completed timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processingCompletedAt;

    /**
     * Approved by user ID
     */
    private UUID approvedBy;

    /**
     * Approval timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

    /**
     * Notes
     */
    private String notes;

    /**
     * Created at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
