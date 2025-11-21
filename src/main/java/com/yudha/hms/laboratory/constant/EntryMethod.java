package com.yudha.hms.laboratory.constant;

/**
 * Result Entry Method Enumeration.
 *
 * Methods for entering laboratory results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum EntryMethod {
    /**
     * Manual entry by technician
     */
    MANUAL("Manual", "Manual entry by technician"),

    /**
     * LIS interface (automated from equipment)
     */
    INTERFACE("Interface", "LIS interface (automated from equipment)"),

    /**
     * Imported from external system
     */
    IMPORTED("Imported", "Imported from external system");

    private final String displayName;
    private final String description;

    EntryMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
