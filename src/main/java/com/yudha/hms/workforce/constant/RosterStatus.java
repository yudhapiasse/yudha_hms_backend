package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum RosterStatus {
    SCHEDULED("Scheduled", "Dijadwalkan"),
    CONFIRMED("Confirmed", "Dikonfirmasi"),
    CANCELLED("Cancelled", "Dibatalkan"),
    COMPLETED("Completed", "Selesai");

    private final String englishName;
    private final String indonesianName;

    RosterStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
