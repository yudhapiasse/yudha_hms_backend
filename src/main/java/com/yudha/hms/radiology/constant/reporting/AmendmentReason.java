package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum AmendmentReason {

    ERROR_CORRECTION("Error Correction", "Koreksi Kesalahan"),
    ADDITIONAL_FINDINGS("Additional Findings", "Temuan Tambahan"),
    CLARIFICATION("Clarification", "Klarifikasi"),
    TECHNICAL_ISSUE("Technical Issue", "Masalah Teknis");

    private final String englishName;
    private final String indonesianName;

    AmendmentReason(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
