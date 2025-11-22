package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Male", "Laki-laki"),
    FEMALE("Female", "Perempuan");

    private final String englishName;
    private final String indonesianName;

    Gender(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
