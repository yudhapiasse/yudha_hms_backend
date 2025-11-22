package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.ShiftType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "shift_pattern", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftPattern extends SoftDeletableEntity {

    @Column(name = "shift_code", length = 50, nullable = false, unique = true)
    private String shiftCode;

    @Column(name = "shift_name", length = 100, nullable = false)
    private String shiftName;

    @Column(name = "shift_name_id", length = 100, nullable = false)
    private String shiftNameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", length = 30, nullable = false)
    private ShiftType shiftType;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_hours", precision = 4, scale = 2, nullable = false)
    private BigDecimal durationHours;

    @Column(name = "is_overnight")
    private Boolean isOvernight = false;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes = 60;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalTime breakEndTime;

    @Column(name = "effective_hours", precision = 4, scale = 2, nullable = false)
    private BigDecimal effectiveHours;

    @Column(name = "overtime_threshold_minutes")
    private Integer overtimeThresholdMinutes;

    @Column(name = "overtime_multiplier", precision = 4, scale = 2)
    private BigDecimal overtimeMultiplier = BigDecimal.valueOf(1.5);

    @Column(name = "holiday_multiplier", precision = 4, scale = 2)
    private BigDecimal holidayMultiplier = BigDecimal.valueOf(2.0);

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "color_code", length = 20)
    private String colorCode;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
