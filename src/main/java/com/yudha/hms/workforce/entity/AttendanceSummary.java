package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_summary", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummary extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "summary_year", nullable = false)
    private Integer summaryYear;

    @Column(name = "summary_month", nullable = false)
    private Integer summaryMonth;

    // Attendance statistics
    @Column(name = "total_working_days", nullable = false)
    private Integer totalWorkingDays;

    @Column(name = "days_present")
    private Integer daysPresent = 0;

    @Column(name = "days_absent")
    private Integer daysAbsent = 0;

    @Column(name = "days_late")
    private Integer daysLate = 0;

    @Column(name = "days_early_leave")
    private Integer daysEarlyLeave = 0;

    @Column(name = "days_on_leave")
    private Integer daysOnLeave = 0;

    @Column(name = "days_sick")
    private Integer daysSick = 0;

    @Column(name = "days_holiday")
    private Integer daysHoliday = 0;

    @Column(name = "days_off")
    private Integer daysOff = 0;

    // Hours
    @Column(name = "total_scheduled_hours", precision = 7, scale = 2)
    private BigDecimal totalScheduledHours = BigDecimal.ZERO;

    @Column(name = "total_actual_hours", precision = 7, scale = 2)
    private BigDecimal totalActualHours = BigDecimal.ZERO;

    @Column(name = "total_overtime_hours", precision = 7, scale = 2)
    private BigDecimal totalOvertimeHours = BigDecimal.ZERO;

    @Column(name = "total_late_minutes")
    private Integer totalLateMinutes = 0;

    @Column(name = "total_early_leave_minutes")
    private Integer totalEarlyLeaveMinutes = 0;

    // Percentages
    @Column(name = "attendance_rate", precision = 5, scale = 2)
    private BigDecimal attendanceRate;

    @Column(name = "punctuality_rate", precision = 5, scale = 2)
    private BigDecimal punctualityRate;

    // Leave summary
    @Column(name = "annual_leave_taken", precision = 4, scale = 1)
    private BigDecimal annualLeaveTaken = BigDecimal.ZERO;

    @Column(name = "sick_leave_taken", precision = 4, scale = 1)
    private BigDecimal sickLeaveTaken = BigDecimal.ZERO;

    @Column(name = "other_leave_taken", precision = 4, scale = 1)
    private BigDecimal otherLeaveTaken = BigDecimal.ZERO;

    // Calculated
    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @Column(name = "finalized")
    private Boolean finalized = false;

    @Column(name = "finalized_by")
    private UUID finalizedBy;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
