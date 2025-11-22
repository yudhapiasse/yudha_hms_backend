package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_shift_assignment", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeShiftAssignment extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "shift_rotation_id")
    private UUID shiftRotationId;

    @Column(name = "fixed_shift_pattern_id")
    private UUID fixedShiftPatternId;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "rotation_start_date")
    private LocalDate rotationStartDate;

    @Column(name = "current_day_in_cycle")
    private Integer currentDayInCycle;

    @Column(name = "is_current")
    private Boolean isCurrent = true;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
