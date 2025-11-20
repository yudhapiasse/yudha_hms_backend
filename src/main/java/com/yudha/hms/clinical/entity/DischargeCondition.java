package com.yudha.hms.clinical.entity;

/**
 * Discharge Condition Enum.
 *
 * Represents the patient's condition at the time of discharge.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public enum DischargeCondition {
    IMPROVED("Improved", "Membaik", "Patient's condition has improved"),
    STABLE("Stable", "Stabil", "Patient's condition is stable"),
    UNCHANGED("Unchanged", "Tidak Berubah", "Patient's condition remains the same"),
    DETERIORATED("Deteriorated", "Memburuk", "Patient's condition has worsened"),
    DECEASED("Deceased", "Meninggal", "Patient died during hospitalization"),
    UNKNOWN("Unknown", "Tidak Diketahui", "Condition at discharge is unknown");

    private final String displayName;
    private final String indonesianName;
    private final String description;

    DischargeCondition(String displayName, String indonesianName, String description) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPositiveOutcome() {
        return this == IMPROVED || this == STABLE;
    }

    public boolean isNegativeOutcome() {
        return this == DETERIORATED || this == DECEASED;
    }

    public boolean isDeceased() {
        return this == DECEASED;
    }
}
