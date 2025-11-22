package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum PositionLevel {
    STAFF("Staff", "Staf"),
    SUPERVISOR("Supervisor", "Supervisor"),
    MANAGER("Manager", "Manajer"),
    DIRECTOR("Director", "Direktur"),
    C_LEVEL("C-Level Executive", "Eksekutif Level C");

    private final String englishName;
    private final String indonesianName;

    PositionLevel(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
