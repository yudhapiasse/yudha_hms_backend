package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Provider Type Enum for healthcare providers.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum ProviderType {
    DOCTOR("Doctor", "Dokter"),
    NURSE("Nurse", "Perawat"),
    SPECIALIST("Specialist", "Spesialis"),
    RESIDENT("Resident", "Residen"),
    INTERN("Intern", "Dokter Magang"),
    NURSING_ASSISTANT("Nursing Assistant", "Asisten Perawat"),
    PHARMACIST("Pharmacist", "Apoteker"),
    THERAPIST("Therapist", "Terapis");

    private final String displayName;
    private final String indonesianName;

    ProviderType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}
