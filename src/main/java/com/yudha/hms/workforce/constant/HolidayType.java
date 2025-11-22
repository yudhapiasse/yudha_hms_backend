package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum HolidayType {
    NATIONAL("National Holiday", "Hari Libur Nasional"),
    RELIGIOUS("Religious Holiday", "Hari Besar Keagamaan"),
    REGIONAL("Regional Holiday", "Hari Libur Daerah"),
    JOINT_LEAVE("Joint Leave", "Cuti Bersama");

    private final String englishName;
    private final String indonesianName;

    HolidayType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
