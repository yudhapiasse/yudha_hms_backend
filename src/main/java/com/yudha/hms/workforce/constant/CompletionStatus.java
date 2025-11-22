package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum CompletionStatus {
    COMPLETED("Completed", "Selesai"),
    IN_PROGRESS("In Progress", "Sedang Berlangsung"),
    CANCELLED("Cancelled", "Dibatalkan"),
    FAILED("Failed", "Gagal"),
    POSTPONED("Postponed", "Ditunda");

    private final String englishName;
    private final String indonesianName;

    CompletionStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
