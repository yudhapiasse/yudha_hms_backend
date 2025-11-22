package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum SubstitutionReason {
    SICK("Sick", "Sakit"),
    EMERGENCY("Emergency", "Darurat"),
    LEAVE("On Leave", "Cuti"),
    SWAP("Shift Swap", "Tukar Shift"),
    OTHER("Other", "Lainnya");

    private final String englishName;
    private final String indonesianName;

    SubstitutionReason(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
