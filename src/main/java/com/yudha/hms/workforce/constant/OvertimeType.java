package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum OvertimeType {
    WEEKDAY("Weekday Overtime (1.5x first hour, 2x subsequent)", "Lembur Hari Kerja (1.5x jam pertama, 2x berikutnya)"),
    WEEKEND("Weekend Overtime (2x)", "Lembur Akhir Pekan (2x)"),
    HOLIDAY("Holiday Overtime (2x)", "Lembur Hari Libur (2x)"),
    AFTER_HOURS("After Hours", "Setelah Jam Kerja");

    private final String englishName;
    private final String indonesianName;

    OvertimeType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
