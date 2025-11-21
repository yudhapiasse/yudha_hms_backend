package com.yudha.hms.laboratory.constant;

/**
 * Validation Status Enumeration.
 *
 * Status of validation decision.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ValidationStatus {
    /**
     * Result approved
     */
    APPROVED("Approved", "Result approved"),

    /**
     * Result rejected
     */
    REJECTED("Rejected", "Result rejected"),

    /**
     * Result needs further review
     */
    NEEDS_REVIEW("Needs Review", "Result needs further review"),

    /**
     * Test needs to be repeated
     */
    NEEDS_REPEAT("Needs Repeat", "Test needs to be repeated");

    private final String displayName;
    private final String description;

    ValidationStatus(String displayName, String description) {
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
     * Check if validation is complete
     */
    public boolean isComplete() {
        return this == APPROVED || this == REJECTED;
    }
}
