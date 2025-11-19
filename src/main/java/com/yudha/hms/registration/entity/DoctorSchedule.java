package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Doctor Schedule Entity.
 *
 * Represents doctor practice schedules per polyclinic.
 * Supports recurring weekly schedules and temporary date-ranged schedules.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "doctor_schedule", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_doctor_schedule_doctor", columnList = "doctor_id"),
        @Index(name = "idx_doctor_schedule_polyclinic", columnList = "polyclinic_id"),
        @Index(name = "idx_doctor_schedule_day", columnList = "day_of_week"),
        @Index(name = "idx_doctor_schedule_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Doctor practice schedules per polyclinic")
public class DoctorSchedule extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polyclinic_id", nullable = false)
    @NotNull(message = "Polyclinic is required")
    private Polyclinic polyclinic;

    // ========== Schedule Timing ==========
    @Column(name = "day_of_week", nullable = false, length = 20)
    @NotBlank(message = "Day of week is required")
    private String dayOfWeek; // MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY

    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull(message = "End time is required")
    private LocalTime endTime;

    // ========== Date Range (Optional - for temporary schedules) ==========
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // ========== Capacity ==========
    @Column(name = "max_patients")
    @Builder.Default
    private Integer maxPatients = 20;

    @Column(name = "appointment_duration_minutes")
    @Builder.Default
    private Integer appointmentDurationMinutes = 15;

    // ========== Status ==========
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // ========== Notes ==========
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Business Methods ==========

    /**
     * Check if this schedule is valid for a specific date.
     */
    public boolean isValidForDate(LocalDate date) {
        if (!Boolean.TRUE.equals(isActive)) {
            return false;
        }

        // Check day of week matches
        DayOfWeek scheduleDayOfWeek = DayOfWeek.valueOf(dayOfWeek);
        if (!date.getDayOfWeek().equals(scheduleDayOfWeek)) {
            return false;
        }

        // Check effective date
        if (effectiveDate != null && date.isBefore(effectiveDate)) {
            return false;
        }

        // Check expiry date
        if (expiryDate != null && date.isAfter(expiryDate)) {
            return false;
        }

        return true;
    }

    /**
     * Check if a specific time is within this schedule's hours.
     */
    public boolean isTimeWithinSchedule(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }

    /**
     * Check if schedule is currently active (today + current time).
     */
    public boolean isCurrentlyActive() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return isValidForDate(today) && isTimeWithinSchedule(now);
    }

    /**
     * Get schedule display string.
     */
    public String getScheduleDisplay() {
        return String.format("%s: %s - %s",
            dayOfWeek,
            startTime.toString(),
            endTime.toString()
        );
    }

    /**
     * Calculate number of time slots available in this schedule.
     */
    public int calculateTotalTimeSlots() {
        if (startTime == null || endTime == null || appointmentDurationMinutes == null || appointmentDurationMinutes <= 0) {
            return 0;
        }

        long totalMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return (int) (totalMinutes / appointmentDurationMinutes);
    }

    /**
     * Check if this is a temporary schedule.
     */
    public boolean isTemporarySchedule() {
        return effectiveDate != null || expiryDate != null;
    }

    /**
     * Get day of week as enum.
     */
    public DayOfWeek getDayOfWeekEnum() {
        return DayOfWeek.valueOf(dayOfWeek);
    }
}
