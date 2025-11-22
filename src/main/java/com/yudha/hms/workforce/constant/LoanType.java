package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum LoanType {
    EMPLOYEE_LOAN("Employee Loan", "Pinjaman Karyawan"),
    SALARY_ADVANCE("Salary Advance", "Kasbon"),
    EMERGENCY_LOAN("Emergency Loan", "Pinjaman Darurat"),
    OTHER("Other", "Lainnya");

    private final String englishName;
    private final String indonesianName;

    LoanType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
