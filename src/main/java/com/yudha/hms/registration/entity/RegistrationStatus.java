package com.yudha.hms.registration.entity;

import lombok.Getter;

/**
 * Registration Status Enum.
 *
 * Defines the lifecycle status of outpatient registrations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum RegistrationStatus {
    REGISTERED("Registered", "Patient registered, not yet checked in", 1),
    WAITING("Waiting", "Patient checked in, waiting for consultation", 2),
    IN_CONSULTATION("In Consultation", "Patient currently with doctor", 3),
    COMPLETED("Completed", "Consultation completed", 4),
    CANCELLED("Cancelled", "Registration cancelled", 5);

    private final String displayName;
    private final String description;
    private final int order;

    RegistrationStatus(String displayName, String description, int order) {
        this.displayName = displayName;
        this.description = description;
        this.order = order;
    }

    public boolean isActive() {
        return this == REGISTERED || this == WAITING || this == IN_CONSULTATION;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }
}