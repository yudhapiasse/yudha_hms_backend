package com.yudha.hms.radiology.constant;

/**
 * Radiology Order Priority Enumeration.
 *
 * Priority levels for radiology examination orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum OrderPriority {
    /**
     * Routine priority - normal scheduling
     */
    ROUTINE("Routine", "Rutin", "Normal scheduling, no urgency"),

    /**
     * Urgent priority - needs to be done soon
     */
    URGENT("Urgent", "Mendesak", "Needs to be done within hours"),

    /**
     * Emergency priority - immediate attention required
     */
    EMERGENCY("Emergency", "Darurat", "Immediate attention required, life-threatening");

    private final String displayName;
    private final String displayNameId;
    private final String description;

    OrderPriority(String displayName, String displayNameId, String description) {
        this.displayName = displayName;
        this.displayNameId = displayNameId;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameId() {
        return displayNameId;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get expected completion time in hours
     */
    public int getExpectedCompletionHours() {
        return switch (this) {
            case ROUTINE -> 24;
            case URGENT -> 4;
            case EMERGENCY -> 1;
        };
    }
}
