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
    PLANNED("Planned", "Direncanakan", 1),
    ARRIVED("Arrived", "Tiba", 2),
    TRIAGED("Triaged", "Sudah Triase", 3),
    IN_PROGRESS("In Progress", "Sedang Berlangsung", 4),
    FINISHED("Finished", "Selesai", 5),
    CANCELLED("Cancelled", "Dibatalkan", 6);

    private final String displayName;
    private final String indonesianName;
    private final int order;

    EncounterStatus(String displayName, String indonesianName, int order) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.order = order;
    }

    public boolean isActive() {
        return this == PLANNED || this == ARRIVED || this == TRIAGED || this == IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this == FINISHED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }
}
