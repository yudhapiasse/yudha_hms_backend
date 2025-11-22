package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum ComparisonChange {

    IMPROVED("Improved", "Membaik"),
    STABLE("Stable", "Stabil"),
    PROGRESSED("Progressed", "Berkembang"),
    MIXED("Mixed", "Campuran");

    private final String englishName;
    private final String indonesianName;

    ComparisonChange(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
