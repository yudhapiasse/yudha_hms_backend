package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum OnCallType {
    PRIMARY("Primary On-Call", "Jaga Utama"),
    BACKUP("Backup On-Call", "Jaga Cadangan"),
    TERTIARY("Tertiary On-Call", "Jaga Tertier");

    private final String englishName;
    private final String indonesianName;

    OnCallType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
