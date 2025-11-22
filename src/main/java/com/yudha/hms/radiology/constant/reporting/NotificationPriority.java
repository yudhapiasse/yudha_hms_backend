package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum NotificationPriority {

    STAT("STAT", "Segera"),
    HIGH("High", "Tinggi"),
    ROUTINE("Routine", "Rutin");

    private final String englishName;
    private final String indonesianName;

    NotificationPriority(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
