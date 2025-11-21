package com.yudha.hms.laboratory.constant;

/**
 * Lab Order Priority Enumeration.
 *
 * Priority levels for laboratory test orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum OrderPriority {
    /**
     * Routine priority - normal processing
     */
    ROUTINE("Routine", "Normal priority processing", 1440), // 24 hours

    /**
     * Urgent priority - expedited processing
     */
    URGENT("Urgent", "Expedited priority processing", 240), // 4 hours

    /**
     * CITO (immediate) priority - emergency processing
     */
    CITO("CITO", "Immediate emergency processing", 60); // 1 hour

    private final String displayName;
    private final String description;
    private final int expectedTatMinutes;

    OrderPriority(String displayName, String description, int expectedTatMinutes) {
        this.displayName = displayName;
        this.description = description;
        this.expectedTatMinutes = expectedTatMinutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getExpectedTatMinutes() {
        return expectedTatMinutes;
    }
}