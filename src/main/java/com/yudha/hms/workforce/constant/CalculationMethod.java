package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum CalculationMethod {
    FIXED("Fixed Amount", "Jumlah Tetap"),
    PERCENTAGE("Percentage", "Persentase"),
    FORMULA("Formula Based", "Berdasarkan Formula");

    private final String englishName;
    private final String indonesianName;

    CalculationMethod(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
