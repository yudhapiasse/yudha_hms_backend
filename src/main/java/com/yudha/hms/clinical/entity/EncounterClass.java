package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * FHIR-compliant Encounter Class Enum.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum EncounterClass {
    AMBULATORY("Ambulatory", "Rawat Jalan"),
    INPATIENT("Inpatient", "Rawat Inap"),
    EMERGENCY("Emergency", "Gawat Darurat"),
    VIRTUAL("Virtual", "Telemedicine");

    private final String displayName;
    private final String indonesianName;

    EncounterClass(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}
