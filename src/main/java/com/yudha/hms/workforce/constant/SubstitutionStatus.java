package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum SubstitutionStatus {
    PENDING("Pending", "Menunggu"),
    APPROVED("Approved", "Disetujui"),
    REJECTED("Rejected", "Ditolak"),
    COMPLETED("Completed", "Selesai"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    SubstitutionStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
