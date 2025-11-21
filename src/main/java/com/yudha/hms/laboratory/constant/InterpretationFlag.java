package com.yudha.hms.laboratory.constant;

/**
 * Result Interpretation Flag Enumeration.
 *
 * Interpretation flags for laboratory result parameters.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum InterpretationFlag {
    /**
     * Result is within normal range
     */
    NORMAL("Normal", "Result is within normal range"),

    /**
     * Result is below normal range
     */
    LOW("Low", "Result is below normal range"),

    /**
     * Result is above normal range
     */
    HIGH("High", "Result is above normal range"),

    /**
     * Result is critically below normal range
     */
    CRITICAL_LOW("Critical Low", "Result is critically below normal range"),

    /**
     * Result is critically above normal range
     */
    CRITICAL_HIGH("Critical High", "Result is critically above normal range"),

    /**
     * Result is abnormal but not high/low
     */
    ABNORMAL("Abnormal", "Result is abnormal but not high/low");

    private final String displayName;
    private final String description;

    InterpretationFlag(String displayName, String description) {
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
     * Check if result is critical
     */
    public boolean isCritical() {
        return this == CRITICAL_LOW || this == CRITICAL_HIGH;
    }

    /**
     * Check if result is abnormal
     */
    public boolean isAbnormal() {
        return this != NORMAL;
    }
}
