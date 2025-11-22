package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
    PENDING("Pending", "Menunggu"),
    APPROVED("Approved", "Disetujui"),
    REJECTED("Rejected", "Ditolak");

    private final String englishName;
    private final String indonesianName;

    ApprovalStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
