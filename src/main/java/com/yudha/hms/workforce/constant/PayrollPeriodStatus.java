package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum PayrollPeriodStatus {
    DRAFT("Draft", "Draft"),
    PROCESSING("Processing", "Sedang Diproses"),
    COMPLETED("Completed", "Selesai"),
    APPROVED("Approved", "Disetujui"),
    PAID("Paid", "Dibayar"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    PayrollPeriodStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
