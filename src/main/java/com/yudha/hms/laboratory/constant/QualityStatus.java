package com.yudha.hms.laboratory.constant;

/**
 * Specimen Quality Status Enumeration.
 *
 * Quality assessment status for specimens.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum QualityStatus {
    /**
     * Specimen is acceptable for testing
     */
    ACCEPTABLE("Acceptable", "Specimen is acceptable for testing"),

    /**
     * Specimen is rejected and cannot be tested
     */
    REJECTED("Rejected", "Specimen is rejected and cannot be tested"),

    /**
     * Specimen quality is compromised but may be testable
     */
    COMPROMISED("Compromised", "Specimen quality is compromised but may be testable with limitations");

    private final String displayName;
    private final String description;

    QualityStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if specimen can be tested
     */
    public boolean canBeTested() {
        return this != REJECTED;
    }
}
