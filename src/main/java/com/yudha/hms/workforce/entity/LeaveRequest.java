package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.ApprovalStatus;
import com.yudha.hms.workforce.constant.LeaveRequestStatus;
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
@Table(name = "leave_request", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest extends SoftDeletableEntity {

    @Column(name = "request_number", length = 50, nullable = false, unique = true)
    private String requestNumber;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "leave_type_id", nullable = false)
    private UUID leaveTypeId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days", precision = 4, scale = 1, nullable = false)
    private BigDecimal totalDays;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "emergency_contact_during_leave", length = 200)
    private String emergencyContactDuringLeave;

    @Column(name = "emergency_phone_during_leave", length = 20)
    private String emergencyPhoneDuringLeave;

    @Column(name = "medical_certificate_url", length = 500)
    private String medicalCertificateUrl;

    @Column(name = "supporting_document_url", length = 500)
    private String supportingDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private LeaveRequestStatus status = LeaveRequestStatus.PENDING;

    // Multi-level approval
    @Column(name = "immediate_supervisor_id")
    private UUID immediateSupervisorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "immediate_supervisor_status", length = 30)
    private ApprovalStatus immediateSupervisorStatus;

    @Column(name = "immediate_supervisor_comments", columnDefinition = "TEXT")
    private String immediateSupervisorComments;

    @Column(name = "immediate_supervisor_action_at")
    private LocalDateTime immediateSupervisorActionAt;

    @Column(name = "hrd_approver_id")
    private UUID hrdApproverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "hrd_status", length = 30)
    private ApprovalStatus hrdStatus;

    @Column(name = "hrd_comments", columnDefinition = "TEXT")
    private String hrdComments;

    @Column(name = "hrd_action_at")
    private LocalDateTime hrdActionAt;

    @Column(name = "final_approver_id")
    private UUID finalApproverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_status", length = 30)
    private ApprovalStatus finalStatus;

    @Column(name = "final_comments", columnDefinition = "TEXT")
    private String finalComments;

    @Column(name = "final_action_at")
    private LocalDateTime finalActionAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Substitute coverage
    @Column(name = "substitute_employee_id")
    private UUID substituteEmployeeId;

    @Column(name = "substitute_confirmed")
    private Boolean substituteConfirmed = false;

    @Column(name = "substitute_notes", columnDefinition = "TEXT")
    private String substituteNotes;

    // Balance verification
    @Column(name = "balance_before_request", precision = 5, scale = 2)
    private BigDecimal balanceBeforeRequest;

    @Column(name = "balance_after_request", precision = 5, scale = 2)
    private BigDecimal balanceAfterRequest;

    // Actual leave taken
    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "actual_days_taken", precision = 4, scale = 1)
    private BigDecimal actualDaysTaken;

    // Cancellation
    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
