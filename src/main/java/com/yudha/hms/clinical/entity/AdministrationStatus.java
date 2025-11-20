package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Medication Administration Status Enum.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum AdministrationStatus {
    PENDING("Pending", "Menunggu"),
    GIVEN("Given", "Diberikan"),
    REFUSED("Refused by Patient", "Ditolak Pasien"),
    HELD("Held", "Ditahan"),
    MISSED("Missed", "Terlewat"),
    DISCONTINUED("Discontinued", "Dihentikan");

    private final String displayName;
    private final String indonesianName;

    AdministrationStatus(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }

    public boolean isCompleted() {
        return this == GIVEN;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean wasNotGiven() {
        return this == REFUSED || this == HELD || this == MISSED;
    }
}
