package com.yudha.hms.registration.entity;

/**
 * Emergency registration status.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public enum EmergencyStatus {
    REGISTERED("Registered", "Terdaftar"),
    ARRIVED("Arrived", "Tiba"),
    TRIAGED("Triaged", "Sudah Triage"),
    IN_TREATMENT("In Treatment", "Dalam Perawatan"),
    WAITING_RESULTS("Waiting for Results", "Menunggu Hasil"),
    ADMITTED("Admitted to Inpatient", "Dirawat Inap"),
    DISCHARGED("Discharged", "Pulang"),
    LEFT_WITHOUT_TREATMENT("Left Without Treatment", "Pulang Paksa"),
    TRANSFERRED("Transferred", "Dirujuk"),
    DECEASED("Deceased", "Meninggal");

    private final String displayName;
    private final String indonesianName;

    EmergencyStatus(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    public boolean isActive() {
        return this == REGISTERED || this == ARRIVED || this == TRIAGED ||
               this == IN_TREATMENT || this == WAITING_RESULTS;
    }

    public boolean isCompleted() {
        return this == ADMITTED || this == DISCHARGED ||
               this == LEFT_WITHOUT_TREATMENT || this == TRANSFERRED || this == DECEASED;
    }

    /**
     * Check if patient can be triaged from current status.
     */
    public boolean canBeTriage() {
        return this == REGISTERED || this == ARRIVED;
    }

    /**
     * Check if treatment can be started from current status.
     */
    public boolean canStartTreatment() {
        return this == ARRIVED || this == TRIAGED;
    }
}
