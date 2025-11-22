package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yudha.hms.workforce.constant.OvertimeStatus;
import com.yudha.hms.workforce.constant.OvertimeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for overtime record queries.
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
public class OvertimeRecordResponseDto {

    /**
     * Overtime record ID
     */
    private UUID id;

    /**
     * Overtime number
     */
    private String overtimeNumber;

    /**
     * Employee ID
     */
    private UUID employeeId;

    /**
     * Employee name (denormalized)
     */
    private String employeeName;

    /**
     * Employee number (denormalized)
     */
    private String employeeNumber;

    /**
     * Department ID
     */
    private UUID departmentId;

    /**
     * Department name (denormalized)
     */
    private String departmentName;

    /**
     * Overtime date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate overtimeDate;

    /**
     * Overtime type
     */
    private OvertimeType overtimeType;

    /**
     * Start time
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    /**
     * End time
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    /**
     * Break duration in hours
     */
    private BigDecimal breakDurationHours;

    /**
     * Total hours (including break)
     */
    private BigDecimal totalHours;

    /**
     * Effective overtime hours (excluding break)
     */
    private BigDecimal effectiveOvertimeHours;

    /**
     * Overtime multiplier applied
     */
    private BigDecimal overtimeMultiplier;

    /**
     * Overtime pay amount
     */
    private BigDecimal overtimePayAmount;

    /**
     * Overtime status
     */
    private OvertimeStatus status;

    /**
     * Supervisor ID
     */
    private UUID supervisorId;

    /**
     * Supervisor name (denormalized)
     */
    private String supervisorName;

    /**
     * Supervisor approved
     */
    private Boolean supervisorApproved;

    /**
     * Supervisor approval date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime supervisorApprovalDate;

    /**
     * Supervisor comments
     */
    private String supervisorComments;

    /**
     * Exceeds daily limit flag
     */
    private Boolean exceedsDailyLimit;

    /**
     * Exceeds weekly limit flag
     */
    private Boolean exceedsWeeklyLimit;

    /**
     * Task description
     */
    private String taskDescription;

    /**
     * Notes
     */
    private String notes;

    /**
     * Has been paid
     */
    private Boolean paid;

    /**
     * Payment date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;

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
