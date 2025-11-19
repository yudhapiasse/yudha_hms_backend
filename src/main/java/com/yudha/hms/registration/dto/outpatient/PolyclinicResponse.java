package com.yudha.hms.registration.dto.outpatient;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for polyclinic information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolyclinicResponse {

    // ========== Basic Information ==========
    private UUID id;
    private String code;
    private String name;
    private String description;

    // ========== Location ==========
    private String floorLocation;
    private String building;
    private String locationDisplay;
    private String phone;
    private String extension;

    // ========== Operating Hours ==========
    private List<String> operatingDays;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String operatingHoursDisplay;

    // ========== Capacity ==========
    private Integer maxPatientsPerDay;
    private Integer appointmentDurationMinutes;
    private Boolean allowWalkIn;
    private Boolean allowAppointments;

    // ========== Status ==========
    private Boolean isActive;
    private Boolean isEmergency;
    private Boolean isOpenToday;
    private Boolean isCurrentlyOpen;

    // ========== Fees ==========
    private BigDecimal baseRegistrationFee;

    // ========== Availability (for today) ==========
    private Integer totalRegistrationsToday;
    private Integer availableSlotsToday;
    private Integer currentQueueNumber;

    // ========== Helper Methods ==========

    /**
     * Get fee display string.
     */
    public String getFeeDisplay() {
        if (baseRegistrationFee == null || baseRegistrationFee.compareTo(BigDecimal.ZERO) == 0) {
            return "Free";
        }
        return String.format("Rp %,.0f", baseRegistrationFee);
    }

    /**
     * Get availability status.
     */
    public String getAvailabilityStatus() {
        if (!Boolean.TRUE.equals(isActive)) {
            return "Inactive";
        }
        if (!Boolean.TRUE.equals(isOpenToday)) {
            return "Closed Today";
        }
        if (!Boolean.TRUE.equals(isCurrentlyOpen)) {
            return "Currently Closed";
        }
        if (availableSlotsToday != null && availableSlotsToday <= 0) {
            return "Full";
        }
        return "Available";
    }
}