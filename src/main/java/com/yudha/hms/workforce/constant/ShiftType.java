package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum ShiftType {
    MORNING("Morning Shift", "Shift Pagi"),
    AFTERNOON("Afternoon Shift", "Shift Siang"),
    NIGHT("Night Shift", "Shift Malam"),
    OFF("Day Off", "Libur"),
    FLEXIBLE("Flexible Hours", "Jam Fleksibel");

    private final String englishName;
    private final String indonesianName;

    ShiftType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
