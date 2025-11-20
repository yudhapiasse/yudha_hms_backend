package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Location Event Type Enum.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum LocationEventType {
    ADMISSION("Admission", "Masuk Rawat Inap"),
    TRANSFER("Transfer", "Pindah Ruangan"),
    BED_CHANGE("Bed Change", "Ganti Tempat Tidur"),
    ICU_ADMISSION("ICU Admission", "Masuk ICU"),
    ICU_DISCHARGE("ICU Discharge", "Keluar ICU"),
    OR_TRANSFER("OR Transfer", "Pindah ke Ruang Operasi"),
    RECOVERY_TRANSFER("Recovery Transfer", "Pindah ke Recovery"),
    DISCHARGE("Discharge", "Pulang");

    private final String displayName;
    private final String indonesianName;

    LocationEventType(String displayName, String indonesianName) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
    }

    /**
     * Check if this is a critical care event.
     */
    public boolean isCriticalCare() {
        return this == ICU_ADMISSION || this == ICU_DISCHARGE;
    }

    /**
     * Check if this is a transfer event.
     */
    public boolean isTransferEvent() {
        return this == TRANSFER || this == BED_CHANGE ||
               this == OR_TRANSFER || this == RECOVERY_TRANSFER;
    }
}
