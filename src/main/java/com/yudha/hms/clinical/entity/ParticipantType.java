package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Participant Type Enum.
 * Defines the role of a practitioner in an encounter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum ParticipantType {
    PRIMARY("Primary", "Utama"),
    SECONDARY("Secondary", "Sekunder"),
    CONSULTANT("Consultant", "Konsultan"),
    ANESTHESIOLOGIST("Anesthesiologist", "Anestesiologi"),
    NURSE("Nurse", "Perawat"),
    SPECIALIST("Specialist", "Spesialis");

    private final String displayName;
    private final String indonesianName;

    ParticipantType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}