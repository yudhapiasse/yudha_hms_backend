package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Medication Schedule Type Enum.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum ScheduleType {
    SCHEDULED("Scheduled", "Terjadwal"),
    PRN("PRN (As Needed)", "PRN (Bila Perlu)"),
    STAT("STAT (Immediate)", "STAT (Segera)"),
    ONE_TIME("One Time", "Satu Kali");

    private final String displayName;
    private final String indonesianName;

    ScheduleType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}
