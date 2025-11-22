package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum SubstitutionRequestType {
    REPLACEMENT("One-Way Replacement", "Penggantian Satu Arah"),
    SWAP("Shift Swap (Reciprocal)", "Tukar Shift (Timbal Balik)");

    private final String englishName;
    private final String indonesianName;

    SubstitutionRequestType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
