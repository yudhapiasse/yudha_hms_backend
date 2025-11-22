package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum OvertimeStatus {
    PENDING("Pending Approval", "Menunggu Persetujuan"),
    APPROVED("Approved", "Disetujui"),
    REJECTED("Rejected", "Ditolak"),
    PAID("Paid", "Sudah Dibayar");

    private final String englishName;
    private final String indonesianName;

    OvertimeStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
