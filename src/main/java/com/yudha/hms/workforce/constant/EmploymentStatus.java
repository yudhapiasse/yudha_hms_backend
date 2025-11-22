package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum EmploymentStatus {
    PERMANENT("Permanent Employee", "Karyawan Tetap"),
    CONTRACT("Contract Employee", "Karyawan Kontrak"),
    OUTSOURCE("Outsourced Employee", "Karyawan Outsource"),
    PROBATION("Probationary Employee", "Karyawan Masa Percobaan"),
    INTERN("Intern", "Magang"),
    TEMPORARY("Temporary Employee", "Karyawan Temporer");

    private final String englishName;
    private final String indonesianName;

    EmploymentStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
