package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Priority Enum.
 * Defines the priority level of an encounter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum Priority {
    ROUTINE("Routine", "Rutin"),
    URGENT("Urgent", "Mendesak"),
    EMERGENCY("Emergency", "Darurat"),
    STAT("STAT", "Segera");

    private final String displayName;
    private final String indonesianName;

    Priority(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}