package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Clinical Status Enum.
 * Defines the clinical status of a diagnosis.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum ClinicalStatus {
    ACTIVE("Active", "Aktif"),
    RESOLVED("Resolved", "Sembuh"),
    RECURRENCE("Recurrence", "Kambuh"),
    REMISSION("Remission", "Remisi"),
    INACTIVE("Inactive", "Tidak Aktif");

    private final String displayName;
    private final String indonesianName;

    ClinicalStatus(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }
}