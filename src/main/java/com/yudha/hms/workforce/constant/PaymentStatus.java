package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pending", "Menunggu"),
    PROCESSING("Processing", "Sedang Diproses"),
    PAID("Paid", "Dibayar"),
    FAILED("Failed", "Gagal"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    PaymentStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
