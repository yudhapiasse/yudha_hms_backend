package com.yudha.hms.laboratory.constant;

/**
 * Alert Severity Enumeration.
 *
 * Severity levels for critical value alerts.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum AlertSeverity {
    /**
     * Low severity
     */
    LOW("Low", "Low severity", 1),

    /**
     * Medium severity
     */
    MEDIUM("Medium", "Medium severity", 2),

    /**
     * High severity
     */
    HIGH("High", "High severity", 3),

    /**
     * Critical severity
     */
    CRITICAL("Critical", "Critical severity", 4);

    private final String displayName;
    private final String description;
    private final int level;

    AlertSeverity(String displayName, String description, int level) {
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
