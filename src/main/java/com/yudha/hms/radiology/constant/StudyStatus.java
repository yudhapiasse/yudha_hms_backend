package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * PACS Study Status Enum.
 *
 * Represents the status of a radiology study in PACS.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum StudyStatus {

    SCHEDULED("Scheduled", "Dijadwalkan"),
    IN_PROGRESS("In Progress", "Sedang Berlangsung"),
    ACQUISITION_COMPLETE("Acquisition Complete", "Akuisisi Selesai"),
    PRELIMINARY_READ("Preliminary Read", "Pembacaan Awal"),
    FINAL_READ("Final Read", "Pembacaan Akhir"),
    VERIFIED("Verified", "Diverifikasi"),
    ARCHIVED("Archived", "Diarsipkan"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    StudyStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
