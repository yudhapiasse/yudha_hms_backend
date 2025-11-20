package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Shift Enum for hospital work shifts.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum Shift {
    MORNING("Morning Shift", "Shift Pagi", 7, 15),
    AFTERNOON("Afternoon Shift", "Shift Siang", 15, 23),
    NIGHT("Night Shift", "Shift Malam", 23, 7);

    private final String displayName;
    private final String indonesianName;
    private final int startHour;
    private final int endHour;

    Shift(String displayName, String indonesianName, int startHour, int endHour) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    /**
     * Get current shift based on time.
     */
    public static Shift getCurrentShift() {
        int hour = java.time.LocalTime.now().getHour();

        if (hour >= 7 && hour < 15) {
            return MORNING;
        } else if (hour >= 15 && hour < 23) {
            return AFTERNOON;
        } else {
            return NIGHT;
        }
    }
}
