package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
    PRESENT("Present", "Hadir"),
    LATE("Late", "Terlambat"),
    EARLY_LEAVE("Early Leave", "Pulang Cepat"),
    ABSENT("Absent", "Tidak Hadir"),
    LEAVE("On Leave", "Cuti"),
    SICK("Sick Leave", "Sakit"),
    HOLIDAY("Holiday", "Hari Libur"),
    OFF_DUTY("Off Duty", "Libur");

    private final String englishName;
    private final String indonesianName;

    AttendanceStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
