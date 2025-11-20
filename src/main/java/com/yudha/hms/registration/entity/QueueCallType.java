package com.yudha.hms.registration.entity;

import lombok.Getter;

/**
 * Queue Call Type Enum.
 * Indicates the type of queue call made.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum QueueCallType {
    NORMAL("Normal", "Normal", "Regular queue call"),
    RECALL("Recall", "Panggilan Ulang", "Patient called again after no response"),
    URGENT("Urgent", "Urgent", "Urgent call for priority patient");

    private final String displayName;
    private final String indonesianName;
    private final String description;

    QueueCallType(String displayName, String indonesianName, String description) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
    }
}
