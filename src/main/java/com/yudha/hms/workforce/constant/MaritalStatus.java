package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum MaritalStatus {
    SINGLE("Single", "Belum Menikah"),
    MARRIED("Married", "Menikah"),
    DIVORCED("Divorced", "Cerai"),
    WIDOWED("Widowed", "Janda/Duda");

    private final String englishName;
    private final String indonesianName;

    MaritalStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
