package com.yudha.hms.radiology.constant.reporting;

import lombok.Getter;

@Getter
public enum DistributionMethod {

    EMAIL("Email", "Email"),
    FAX("Fax", "Fax"),
    PORTAL("Portal", "Portal"),
    PRINT("Print", "Cetak"),
    HL7("HL7", "HL7"),
    API("API", "API");

    private final String englishName;
    private final String indonesianName;

    DistributionMethod(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
