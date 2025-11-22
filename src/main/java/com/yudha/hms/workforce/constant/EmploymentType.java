package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum EmploymentType {
    FULL_TIME("Full Time", "Penuh Waktu"),
    PART_TIME("Part Time", "Paruh Waktu"),
    SHIFT("Shift Work", "Kerja Shift"),
    ON_CALL("On Call", "On Call");

    private final String englishName;
    private final String indonesianName;

    EmploymentType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
