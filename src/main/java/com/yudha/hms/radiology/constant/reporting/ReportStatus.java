package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum ReportStatus {

    DRAFT("Draft", "Draf"),
    PRELIMINARY("Preliminary", "Preliminer"),
    VERIFIED("Verified", "Terverifikasi"),
    AMENDED("Amended", "Diamendemen"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    ReportStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
