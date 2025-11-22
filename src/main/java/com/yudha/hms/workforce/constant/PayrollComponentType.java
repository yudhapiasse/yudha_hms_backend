package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum PayrollComponentType {
    EARNING("Earning", "Penghasilan"),
    ALLOWANCE("Allowance", "Tunjangan"),
    DEDUCTION("Deduction", "Potongan"),
    TAX("Tax", "Pajak"),
    INSURANCE("Insurance", "Asuransi"),
    BENEFIT("Benefit", "Benefit"),
    OVERTIME("Overtime", "Lembur"),
    BONUS("Bonus", "Bonus");

    private final String englishName;
    private final String indonesianName;

    PayrollComponentType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
