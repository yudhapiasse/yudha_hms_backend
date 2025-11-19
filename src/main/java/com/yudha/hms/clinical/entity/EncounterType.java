package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Encounter Type Enum.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum EncounterType {
    OUTPATIENT("Outpatient", "Rawat Jalan"),
    INPATIENT("Inpatient", "Rawat Inap"),
    EMERGENCY("Emergency", "Gawat Darurat");

    private final String displayName;
    private final String indonesianName;

    EncounterType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}
