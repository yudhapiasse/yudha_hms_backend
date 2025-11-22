package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum LoanStatus {
    PENDING("Pending", "Menunggu"),
    APPROVED("Approved", "Disetujui"),
    REJECTED("Rejected", "Ditolak"),
    ACTIVE("Active", "Aktif"),
    COMPLETED("Completed", "Selesai"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    LoanStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
