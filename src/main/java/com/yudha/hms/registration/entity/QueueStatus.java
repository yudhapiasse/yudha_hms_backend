package com.yudha.hms.registration.entity;

import lombok.Getter;

/**
 * Queue Status Enum.
 * Tracks the current status of a patient in the queue system.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum QueueStatus {
    WAITING("Waiting", "Menunggu", "Patient checked in and waiting to be called"),
    CALLED("Called", "Dipanggil", "Patient has been called but not yet responded"),
    SERVING("Serving", "Sedang Dilayani", "Patient is currently being served/in consultation"),
    COMPLETED("Completed", "Selesai", "Service completed"),
    SKIPPED("Skipped", "Dilewati", "Patient was skipped (not present when called)"),
    CANCELLED("Cancelled", "Dibatalkan", "Queue cancelled by patient or system");

    private final String displayName;
    private final String indonesianName;
    private final String description;

    QueueStatus(String displayName, String indonesianName, String description) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
    }

    /**
     * Check if this status represents an active queue.
     */
    public boolean isActive() {
        return this == WAITING || this == CALLED || this == SERVING;
    }

    /**
     * Check if this status represents a completed queue.
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Check if patient can be called.
     */
    public boolean canBeCalled() {
        return this == WAITING || this == SKIPPED;
    }

    /**
     * Check if patient can start being served.
     */
    public boolean canStartServing() {
        return this == CALLED;
    }

    /**
     * Check if this is a final status (no further transitions).
     */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
