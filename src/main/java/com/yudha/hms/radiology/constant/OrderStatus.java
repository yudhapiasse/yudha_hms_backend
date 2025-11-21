package com.yudha.hms.radiology.constant;

/**
 * Radiology Order Status Enumeration.
 *
 * Status of radiology orders throughout the workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum OrderStatus {
    /**
     * Order created, awaiting scheduling
     */
    PENDING("Pending", "Menunggu", "Order created, awaiting scheduling"),

    /**
     * Examination scheduled
     */
    SCHEDULED("Scheduled", "Terjadwal", "Examination scheduled"),

    /**
     * Patient arrived, examination in progress
     */
    IN_PROGRESS("In Progress", "Dalam Proses", "Examination in progress"),

    /**
     * Examination completed, awaiting report
     */
    COMPLETED("Completed", "Selesai", "Examination completed, report available"),

    /**
     * Order cancelled
     */
    CANCELLED("Cancelled", "Dibatalkan", "Order cancelled");

    private final String displayName;
    private final String displayNameId;
    private final String description;

    OrderStatus(String displayName, String displayNameId, String description) {
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

    /**
     * Check if this status allows scheduling
     */
    public boolean canBeScheduled() {
        return this == PENDING;
    }
}
