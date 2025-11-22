package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum LeaveRequestStatus {
    PENDING("Pending Approval", "Menunggu Persetujuan"),
    APPROVED("Approved", "Disetujui"),
    REJECTED("Rejected", "Ditolak"),
    CANCELLED("Cancelled", "Dibatalkan"),
    WITHDRAWN("Withdrawn", "Ditarik Kembali");

    private final String englishName;
    private final String indonesianName;

    LeaveRequestStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
