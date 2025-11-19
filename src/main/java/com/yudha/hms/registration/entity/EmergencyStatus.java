package com.yudha.hms.registration.entity;

/**
 * Emergency registration status.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public enum EmergencyStatus {
    REGISTERED("Registered"),
    TRIAGED("Triaged"),
    IN_TREATMENT("In Treatment"),
    WAITING_RESULTS("Waiting for Results"),
    ADMITTED("Admitted to Inpatient"),
    DISCHARGED("Discharged"),
    LEFT_WITHOUT_TREATMENT("Left Without Treatment"),
    TRANSFERRED("Transferred"),
    DECEASED("Deceased");

    private final String displayName;

    EmergencyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == REGISTERED || this == TRIAGED ||
               this == IN_TREATMENT || this == WAITING_RESULTS;
    }

    public boolean isCompleted() {
        return this == ADMITTED || this == DISCHARGED ||
               this == LEFT_WITHOUT_TREATMENT || this == TRANSFERRED || this == DECEASED;
    }
}
