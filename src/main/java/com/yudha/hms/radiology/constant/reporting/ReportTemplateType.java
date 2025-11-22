package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum ReportTemplateType {

    STRUCTURED("Structured Report", "Laporan Terstruktur"),
    SEMI_STRUCTURED("Semi-Structured Report", "Laporan Semi-Terstruktur"),
    FREE_TEXT("Free Text Report", "Laporan Teks Bebas");

    private final String englishName;
    private final String indonesianName;

    ReportTemplateType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
