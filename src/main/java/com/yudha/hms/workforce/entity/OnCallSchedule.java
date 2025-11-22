package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.OnCallType;
import com.yudha.hms.workforce.constant.RosterStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "on_call_schedule", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnCallSchedule extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "on_call_date", nullable = false)
    private LocalDate onCallDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "on_call_type", length = 30, nullable = false)
    private OnCallType onCallType;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "required_response_time_minutes")
    private Integer requiredResponseTimeMinutes;

    @Column(name = "must_be_on_premises")
    private Boolean mustBeOnPremises = false;

    @Column(name = "on_call_rate", precision = 15, scale = 2)
    private BigDecimal onCallRate;

    @Column(name = "call_out_rate", precision = 15, scale = 2)
    private BigDecimal callOutRate;

    @Column(name = "minimum_call_out_hours", precision = 4, scale = 2)
    private BigDecimal minimumCallOutHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private RosterStatus status = RosterStatus.SCHEDULED;

    @Column(name = "was_called_out")
    private Boolean wasCalledOut = false;

    @Column(name = "call_out_time")
    private LocalDateTime callOutTime;

    @Column(name = "call_out_reason", columnDefinition = "TEXT")
    private String callOutReason;

    @Column(name = "response_time_minutes")
    private Integer responseTimeMinutes;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "total_on_call_hours", precision = 5, scale = 2)
    private BigDecimal totalOnCallHours;

    @Column(name = "total_call_out_hours", precision = 5, scale = 2)
    private BigDecimal totalCallOutHours;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
