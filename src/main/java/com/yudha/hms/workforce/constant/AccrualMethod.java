package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum AccrualMethod {
    YEARLY("Yearly Accrual", "Per Tahun"),
    MONTHLY("Monthly Accrual", "Per Bulan"),
    WORKED_DAYS("Based on Worked Days", "Berdasarkan Hari Kerja");

    private final String englishName;
    private final String indonesianName;

    AccrualMethod(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
