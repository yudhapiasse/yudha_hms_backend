package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum TranscriptionStatus {

    PENDING("Pending", "Menunggu"),
    PROCESSING("Processing", "Memproses"),
    COMPLETED("Completed", "Selesai"),
    FAILED("Failed", "Gagal"),
    EDITED("Edited", "Diedit");

    private final String englishName;
    private final String indonesianName;

    TranscriptionStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
