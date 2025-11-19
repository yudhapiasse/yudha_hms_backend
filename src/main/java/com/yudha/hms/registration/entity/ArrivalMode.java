package com.yudha.hms.registration.entity;

/**
 * Mode of arrival to emergency department.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public enum ArrivalMode {
    WALK_IN("Walk-in"),
    AMBULANCE("Ambulance"),
    POLICE("Police"),
    REFERRAL("Referral"),
    TRANSFER("Transfer from another facility");

    private final String displayName;

    ArrivalMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
