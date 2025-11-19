package com.yudha.hms.registration.dto.outpatient;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for doctor availability with time slots.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailabilityResponse {

    // ========== Doctor Information ==========
    private UUID doctorId;
    private String doctorName;
    private String doctorTitle;
    private String specialization;

    // ========== Polyclinic Information ==========
    private UUID polyclinicId;
    private String polyclinicName;

    // ========== Schedule Information ==========
    private UUID scheduleId;
    private LocalDate date;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    // ========== Availability ==========
    private Boolean isAvailable;
    private Integer totalSlots;
    private Integer availableSlots;
    private Integer bookedSlots;

    // ========== Time Slots ==========
    @Builder.Default
    private List<TimeSlotInfo> timeSlots = new ArrayList<>();

    // ========== Nested Class for Time Slot Info ==========
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeSlotInfo {
        private LocalTime startTime;
        private LocalTime endTime;
        private Boolean isAvailable;
        private Integer slotsRemaining;
        private String status; // "Available", "Booked", "Full"
    }

    /**
     * Get availability percentage.
     */
    public double getAvailabilityPercentage() {
        if (totalSlots == null || totalSlots == 0) {
            return 0.0;
        }
        return (availableSlots.doubleValue() / totalSlots) * 100;
    }

    /**
     * Check if fully booked.
     */
    public boolean isFullyBooked() {
        return availableSlots != null && availableSlots == 0;
    }
}