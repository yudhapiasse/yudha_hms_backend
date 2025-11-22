package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum ThrType {
    IDUL_FITRI("Idul Fitri", "Idul Fitri"),
    CHRISTMAS("Christmas", "Natal"),
    CHINESE_NEW_YEAR("Chinese New Year", "Tahun Baru Imlek"),
    OTHER("Other", "Lainnya");

    private final String englishName;
    private final String indonesianName;

    ThrType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
