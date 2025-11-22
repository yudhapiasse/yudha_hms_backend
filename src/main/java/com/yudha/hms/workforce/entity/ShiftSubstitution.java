package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.SubstitutionReason;
import com.yudha.hms.workforce.constant.SubstitutionRequestType;
import com.yudha.hms.workforce.constant.SubstitutionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shift_substitution", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSubstitution extends SoftDeletableEntity {

    @Column(name = "request_number", length = 50, nullable = false, unique = true)
    private String requestNumber;

    @Column(name = "original_roster_id", nullable = false)
    private UUID originalRosterId;

    @Column(name = "original_employee_id", nullable = false)
    private UUID originalEmployeeId;

    @Column(name = "roster_date", nullable = false)
    private LocalDate rosterDate;

    @Column(name = "shift_pattern_id", nullable = false)
    private UUID shiftPatternId;

    @Column(name = "substitute_employee_id", nullable = false)
    private UUID substituteEmployeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "substitution_reason", length = 30, nullable = false)
    private SubstitutionReason substitutionReason;

    @Column(name = "reason_details", columnDefinition = "TEXT")
    private String reasonDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", length = 30, nullable = false)
    private SubstitutionRequestType requestType;

    @Column(name = "swap_roster_id")
    private UUID swapRosterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private SubstitutionStatus status = SubstitutionStatus.PENDING;

    @Column(name = "requested_by")
    private UUID requestedBy;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_comments", columnDefinition = "TEXT")
    private String approvalComments;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "substitute_confirmed")
    private Boolean substituteConfirmed = false;

    @Column(name = "substitute_confirmed_at")
    private LocalDateTime substituteConfirmedAt;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
