package com.yudha.hms.laboratory.constant;

/**
 * Lab Order Status Enumeration.
 *
 * Status of laboratory orders throughout the workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum OrderStatus {
    /**
     * Order created, awaiting sample collection
     */
    PENDING("Pending", "Order created, awaiting sample collection"),

    /**
     * Sample collection scheduled
     */
    SCHEDULED("Scheduled", "Sample collection scheduled"),

    /**
     * Sample collected from patient
     */
    COLLECTED("Collected", "Sample collected from patient"),

    /**
     * Sample received by laboratory
     */
    RECEIVED("Received", "Sample received by laboratory"),

    /**
     * Testing in progress
     */
    IN_PROGRESS("In Progress", "Testing in progress"),

    /**
     * Testing completed, results available
     */
    COMPLETED("Completed", "Testing completed, results available"),

    /**
     * Order cancelled
     */
    CANCELLED("Cancelled", "Order cancelled");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
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
     * Check if this status is terminal (no further processing)
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    /**
     * Check if this status allows cancellation
     */
    public boolean canBeCancelled() {
        return this != COMPLETED && this != CANCELLED;
    }
}