package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.OvertimeStatus;
import com.yudha.hms.workforce.constant.OvertimeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "overtime_record", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeRecord extends SoftDeletableEntity {

    @Column(name = "overtime_number", length = 50, nullable = false, unique = true)
    private String overtimeNumber;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "attendance_record_id")
    private UUID attendanceRecordId;

    @Column(name = "overtime_date", nullable = false)
    private LocalDate overtimeDate;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "total_hours", precision = 5, scale = 2, nullable = false)
    private BigDecimal totalHours;

    @Column(name = "break_hours", precision = 4, scale = 2)
    private BigDecimal breakHours = BigDecimal.ZERO;

    @Column(name = "effective_overtime_hours", precision = 5, scale = 2, nullable = false)
    private BigDecimal effectiveOvertimeHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "overtime_type", length = 30, nullable = false)
    private OvertimeType overtimeType;

    @Column(name = "base_rate", precision = 15, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "overtime_multiplier", precision = 4, scale = 2)
    private BigDecimal overtimeMultiplier;

    @Column(name = "total_overtime_pay", precision = 15, scale = 2)
    private BigDecimal totalOvertimePay;

    @Column(name = "overtime_reason", columnDefinition = "TEXT", nullable = false)
    private String overtimeReason;

    @Column(name = "work_description", columnDefinition = "TEXT")
    private String workDescription;

    @Column(name = "project_code", length = 50)
    private String projectCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OvertimeStatus status = OvertimeStatus.PENDING;

    @Column(name = "requested_by")
    private UUID requestedBy;

    @Column(name = "supervisor_approved")
    private Boolean supervisorApproved = false;

    @Column(name = "supervisor_id")
    private UUID supervisorId;

    @Column(name = "supervisor_comments", columnDefinition = "TEXT")
    private String supervisorComments;

    @Column(name = "supervisor_approved_at")
    private LocalDateTime supervisorApprovedAt;

    @Column(name = "hrd_approved")
    private Boolean hrdApproved = false;

    @Column(name = "hrd_approver_id")
    private UUID hrdApproverId;

    @Column(name = "hrd_comments", columnDefinition = "TEXT")
    private String hrdComments;

    @Column(name = "hrd_approved_at")
    private LocalDateTime hrdApprovedAt;

    @Column(name = "finance_approved")
    private Boolean financeApproved = false;

    @Column(name = "finance_approver_id")
    private UUID financeApproverId;

    @Column(name = "finance_approved_at")
    private LocalDateTime financeApprovedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "paid")
    private Boolean paid = false;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    // Indonesian labor law limits
    @Column(name = "exceeds_daily_limit")
    private Boolean exceedsDailyLimit = false;

    @Column(name = "exceeds_weekly_limit")
    private Boolean exceedsWeeklyLimit = false;

    @Column(name = "compliance_notes", columnDefinition = "TEXT")
    private String complianceNotes;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
