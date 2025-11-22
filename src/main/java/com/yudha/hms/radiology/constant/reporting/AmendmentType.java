package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum AmendmentType {

    ADDENDUM("Addendum", "Addendum"),
    CORRECTION("Correction", "Koreksi"),
    SUPPLEMENTAL("Supplemental", "Suplemen");

    private final String englishName;
    private final String indonesianName;

    AmendmentType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
