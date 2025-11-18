package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.RoomClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for room and bed availability information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityDto {

    private UUID roomId;
    private String roomNumber;
    private String roomName;
    private RoomClass roomClass;
    private String roomType;

    // Location
    private String building;
    private String floor;
    private String wing;

    // Capacity
    private Integer totalBeds;
    private Integer availableBeds;
    private Integer occupiedBeds;

    // Pricing
    private BigDecimal baseRoomRate;

    // Facilities
    private Boolean hasAc;
    private Boolean hasTv;
    private Boolean hasBathroom;
    private Boolean hasWifi;
    private Boolean hasRefrigerator;
    private Boolean hasSofaBed;

    // Status
    private Boolean isAvailable;
    private Boolean isActive;

    // Available beds in this room
    private List<BedInfo> availableBedsList;

    // Calculated deposit (3 days default)
    private BigDecimal estimatedDeposit;

    /**
     * Inner class for bed information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BedInfo {
        private UUID bedId;
        private String bedNumber;
        private String bedType;
        private String bedPosition;
        private Boolean hasMonitor;
        private Boolean hasVentilator;
        private Boolean hasOxygen;
        private Boolean isAvailable;
    }

    /**
     * Check if room has any available beds.
     *
     * @return true if beds available
     */
    public boolean hasAvailability() {
        return availableBeds != null && availableBeds > 0 && Boolean.TRUE.equals(isAvailable);
    }

    /**
     * Get full room location.
     *
     * @return formatted location string
     */
    public String getFullLocation() {
        StringBuilder location = new StringBuilder();
        if (building != null) {
            location.append(building);
        }
        if (floor != null) {
            if (location.length() > 0) location.append(" - ");
            location.append("Lt. ").append(floor);
        }
        if (wing != null) {
            if (location.length() > 0) location.append(" - ");
            location.append(wing);
        }
        if (roomNumber != null) {
            if (location.length() > 0) location.append(" - ");
            location.append(roomNumber);
        }
        return location.toString();
    }

    /**
     * Calculate deposit for given number of days.
     *
     * @param days number of days
     * @return calculated deposit
     */
    public BigDecimal calculateDeposit(int days) {
        if (baseRoomRate == null) {
            return BigDecimal.ZERO;
        }
        return baseRoomRate.multiply(BigDecimal.valueOf(days));
    }
}
