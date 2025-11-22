package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
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
@Table(name = "duty_roster", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DutyRoster extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "roster_date", nullable = false)
    private LocalDate rosterDate;

    @Column(name = "shift_pattern_id", nullable = false)
    private UUID shiftPatternId;

    @Column(name = "scheduled_start_time", nullable = false)
    private LocalTime scheduledStartTime;

    @Column(name = "scheduled_end_time", nullable = false)
    private LocalTime scheduledEndTime;

    @Column(name = "scheduled_hours", precision = 4, scale = 2, nullable = false)
    private BigDecimal scheduledHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private RosterStatus status = RosterStatus.SCHEDULED;

    @Column(name = "assigned_location", length = 200)
    private String assignedLocation;

    @Column(name = "assigned_role", length = 100)
    private String assignedRole;

    @Column(name = "approved")
    private Boolean approved = false;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "published")
    private Boolean published = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
