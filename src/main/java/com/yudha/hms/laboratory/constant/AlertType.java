package com.yudha.hms.laboratory.constant;

/**
 * Critical Value Alert Type Enumeration.
 *
 * Types of critical value alerts.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum AlertType {
    /**
     * Panic value alert (life-threatening)
     */
    PANIC_VALUE("Panic Value", "Panic value alert (life-threatening)"),

    /**
     * Critical value alert (requires immediate attention)
     */
    CRITICAL_VALUE("Critical Value", "Critical value alert (requires immediate attention)"),

    /**
     * Delta check alert (unusual change from previous result)
     */
    DELTA_CHECK("Delta Check", "Delta check alert (unusual change from previous result)");

    private final String displayName;
    private final String description;

    AlertType(String displayName, String description) {
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
