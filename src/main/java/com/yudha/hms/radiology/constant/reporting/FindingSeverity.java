package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum FindingSeverity {

    URGENT("Urgent", "Mendesak"),
    HIGH("High", "Tinggi"),
    MODERATE("Moderate", "Sedang");

    private final String englishName;
    private final String indonesianName;

    FindingSeverity(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
