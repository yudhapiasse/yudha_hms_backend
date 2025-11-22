package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum ReportComplexity {

    SIMPLE("Simple", "Sederhana"),
    MODERATE("Moderate", "Menengah"),
    COMPLEX("Complex", "Kompleks");

    private final String englishName;
    private final String indonesianName;

    ReportComplexity(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
