package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yudha.hms.workforce.constant.OvertimeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Request DTO for creating or updating an overtime record.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeRecordRequestDto {

    /**
     * Employee ID
     */
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    /**
     * Department ID
     */
    private UUID departmentId;

    /**
     * Overtime date
     */
    @NotNull(message = "Overtime date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate overtimeDate;

    /**
     * Overtime type (WEEKDAY, WEEKEND, HOLIDAY)
     */
    @NotNull(message = "Overtime type is required")
    private OvertimeType overtimeType;

    /**
     * Start time
     */
    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    /**
     * End time
     */
    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    /**
     * Break duration in hours (default 0)
     */
    private BigDecimal breakDurationHours;

    /**
     * Supervisor ID (for approval)
     */
    private UUID supervisorId;

    /**
     * Task/project description
     */
    private String taskDescription;

    /**
     * Notes or remarks
     */
    private String notes;
}
