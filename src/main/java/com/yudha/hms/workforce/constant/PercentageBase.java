package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum PercentageBase {
    BASIC_SALARY("Basic Salary", "Gaji Pokok"),
    GROSS_SALARY("Gross Salary", "Gaji Kotor"),
    TAXABLE_INCOME("Taxable Income", "Penghasilan Kena Pajak"),
    TOTAL_ALLOWANCE("Total Allowance", "Total Tunjangan");

    private final String englishName;
    private final String indonesianName;

    PercentageBase(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
