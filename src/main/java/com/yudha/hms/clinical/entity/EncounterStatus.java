package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Encounter Status Enum.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum EncounterStatus {
    REGISTERED("Registered", "Terdaftar", 1),
    IN_PROGRESS("In Progress", "Sedang Berlangsung", 2),
    FINISHED("Finished", "Selesai", 3),
    CANCELLED("Cancelled", "Dibatalkan", 4);

    private final String displayName;
    private final String indonesianName;
    private final int order;

    EncounterStatus(String displayName, String indonesianName, int order) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.order = order;
    }

    public boolean isActive() {
        return this == REGISTERED || this == IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this == FINISHED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }
}
