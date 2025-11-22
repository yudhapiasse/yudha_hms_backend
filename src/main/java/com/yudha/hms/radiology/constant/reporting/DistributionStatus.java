package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum DistributionStatus {

    PENDING("Pending", "Menunggu"),
    SENT("Sent", "Terkirim"),
    DELIVERED("Delivered", "Tersampaikan"),
    FAILED("Failed", "Gagal"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    DistributionStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
