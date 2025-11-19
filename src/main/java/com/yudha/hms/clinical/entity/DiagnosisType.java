package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Diagnosis Type Enum.
 * Defines the type/classification of a diagnosis in an encounter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum DiagnosisType {
    PRIMARY("Primary", "Primer"),
    SECONDARY("Secondary", "Sekunder"),
    ADMISSION("Admission", "Masuk"),
    DISCHARGE("Discharge", "Keluar"),
    DIFFERENTIAL("Differential", "Diferensial"),
    WORKING("Working", "Kerja");

    private final String displayName;
    private final String indonesianName;

    DiagnosisType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}