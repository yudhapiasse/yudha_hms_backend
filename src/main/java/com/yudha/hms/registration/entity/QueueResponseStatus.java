package com.yudha.hms.registration.entity;

import lombok.Getter;

/**
 * Queue Response Status Enum.
 * Tracks patient response to queue call.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum QueueResponseStatus {
    RESPONDED("Responded", "Hadir", "Patient responded to the call"),
    NO_RESPONSE("No Response", "Tidak Hadir", "Patient did not respond to the call"),
    SKIPPED("Skipped", "Dilewati", "Patient was skipped after no response");

    private final String displayName;
    private final String indonesianName;
    private final String description;

    QueueResponseStatus(String displayName, String indonesianName, String description) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
    }
}
