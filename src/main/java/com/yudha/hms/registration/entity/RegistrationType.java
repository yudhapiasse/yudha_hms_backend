package com.yudha.hms.registration.entity;

import lombok.Getter;

/**
 * Registration Type Enum.
 *
 * Defines types of outpatient registrations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum RegistrationType {
    WALK_IN("Walk-in", "Same-day registration without appointment"),
    APPOINTMENT("Appointment", "Pre-scheduled appointment booking");

    private final String displayName;
    private final String description;

    RegistrationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}