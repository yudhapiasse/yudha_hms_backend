package com.yudha.hms.laboratory.constant;

/**
 * Result Validation Level Enumeration.
 *
 * Multi-step validation workflow levels.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ValidationLevel {
    /**
     * Laboratory technician validation
     */
    TECHNICIAN("Technician", "Laboratory technician validation", 1),

    /**
     * Senior technician validation
     */
    SENIOR_TECH("Senior Technician", "Senior technician validation", 2),

    /**
     * Pathologist validation
     */
    PATHOLOGIST("Pathologist", "Pathologist validation", 3),

    /**
     * Clinical reviewer validation
     */
    CLINICAL_REVIEWER("Clinical Reviewer", "Clinical reviewer validation", 4);

    private final String displayName;
    private final String description;
    private final int level;

    ValidationLevel(String displayName, String description, int level) {
        this.displayName = displayName;
        this.description = description;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }
}
