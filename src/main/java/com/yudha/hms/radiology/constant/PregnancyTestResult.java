package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * Pregnancy Test Result Enum.
 *
 * Results for pregnancy testing before radiology examinations with radiation.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum PregnancyTestResult {
    NEGATIVE("Negative", "Negatif"),
    POSITIVE("Positive", "Positif"),
    NOT_APPLICABLE("Not Applicable", "Tidak Berlaku"),
    NOT_TESTED("Not Tested", "Belum Dites");

    private final String englishName;
    private final String indonesianName;

    PregnancyTestResult(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }

    public String getDisplayName() {
        return englishName;
    }
}
