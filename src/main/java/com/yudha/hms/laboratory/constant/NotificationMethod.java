package com.yudha.hms.laboratory.constant;

/**
 * Notification Method Enumeration.
 *
 * Methods for notifying critical values.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum NotificationMethod {
    /**
     * Phone call notification
     */
    PHONE("Phone", "Phone call notification"),

    /**
     * SMS notification
     */
    SMS("SMS", "SMS notification"),

    /**
     * Email notification
     */
    EMAIL("Email", "Email notification"),

    /**
     * In-person notification
     */
    IN_PERSON("In Person", "In-person notification"),

    /**
     * System alert notification
     */
    SYSTEM_ALERT("System Alert", "System alert notification");

    private final String displayName;
    private final String description;

    NotificationMethod(String displayName, String description) {
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
