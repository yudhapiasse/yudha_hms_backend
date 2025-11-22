package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum TransferBatchStatus {
    GENERATED("Generated", "Dibuat"),
    VALIDATED("Validated", "Divalidasi"),
    SUBMITTED("Submitted", "Disubmit"),
    PROCESSING("Processing", "Sedang Diproses"),
    COMPLETED("Completed", "Selesai"),
    FAILED("Failed", "Gagal");

    private final String englishName;
    private final String indonesianName;

    TransferBatchStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
