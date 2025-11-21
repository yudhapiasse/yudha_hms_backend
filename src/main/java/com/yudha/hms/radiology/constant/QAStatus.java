package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * Quality Assurance Status Enum.
 *
 * Represents QA status for radiology studies.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum QAStatus {

    PENDING("Pending QA", "Menunggu QA"),
    PASSED("QA Passed", "QA Lulus"),
    FAILED("QA Failed", "QA Gagal"),
    REQUIRES_REVIEW("Requires Review", "Perlu Review"),
    REACQUISITION_REQUIRED("Reacquisition Required", "Perlu Akuisisi Ulang");

    private final String englishName;
    private final String indonesianName;

    QAStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
