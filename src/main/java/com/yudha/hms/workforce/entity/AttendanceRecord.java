package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.AttendanceStatus;
import com.yudha.hms.workforce.constant.CheckInMethod;
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
@Table(name = "attendance_record", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "duty_roster_id")
    private UUID dutyRosterId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    // Check-in
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_method", length = 30)
    private CheckInMethod checkInMethod;

    @Column(name = "check_in_device_id", length = 100)
    private String checkInDeviceId;

    @Column(name = "check_in_location", length = 200)
    private String checkInLocation;

    @Column(name = "check_in_latitude", precision = 10, scale = 8)
    private BigDecimal checkInLatitude;

    @Column(name = "check_in_longitude", precision = 11, scale = 8)
    private BigDecimal checkInLongitude;

    @Column(name = "check_in_photo_url", length = 500)
    private String checkInPhotoUrl;

    // Check-out
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_out_method", length = 30)
    private CheckInMethod checkOutMethod;

    @Column(name = "check_out_device_id", length = 100)
    private String checkOutDeviceId;

    @Column(name = "check_out_location", length = 200)
    private String checkOutLocation;

    @Column(name = "check_out_latitude", precision = 10, scale = 8)
    private BigDecimal checkOutLatitude;

    @Column(name = "check_out_longitude", precision = 11, scale = 8)
    private BigDecimal checkOutLongitude;

    @Column(name = "check_out_photo_url", length = 500)
    private String checkOutPhotoUrl;

    // Calculated durations
    @Column(name = "working_hours", precision = 5, scale = 2)
    private BigDecimal workingHours;

    @Column(name = "break_hours", precision = 4, scale = 2)
    private BigDecimal breakHours;

    @Column(name = "effective_hours", precision = 5, scale = 2)
    private BigDecimal effectiveHours;

    // Overtime
    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "overtime_approved")
    private Boolean overtimeApproved = false;

    @Column(name = "overtime_approved_by")
    private UUID overtimeApprovedBy;

    @Column(name = "overtime_approved_at")
    private LocalDateTime overtimeApprovedAt;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", length = 30, nullable = false)
    private AttendanceStatus attendanceStatus;

    @Column(name = "is_late")
    private Boolean isLate = false;

    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    @Column(name = "is_early_leave")
    private Boolean isEarlyLeave = false;

    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes = 0;

    // Validation
    @Column(name = "is_valid")
    private Boolean isValid = true;

    @Column(name = "validation_notes", columnDefinition = "TEXT")
    private String validationNotes;

    @Column(name = "validated_by")
    private UUID validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    // Special cases
    @Column(name = "is_holiday_work")
    private Boolean isHolidayWork = false;

    @Column(name = "is_weekend_work")
    private Boolean isWeekendWork = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
