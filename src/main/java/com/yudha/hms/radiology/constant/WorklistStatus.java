package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * DICOM Worklist Status Enum.
 *
 * Represents the status of a DICOM modality worklist entry.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum WorklistStatus {

    PENDING("Pending", "Menunggu"),
    SCHEDULED("Scheduled", "Dijadwalkan"),
    SENT_TO_MODALITY("Sent to Modality", "Dikirim ke Modalitas"),
    IN_PROGRESS("In Progress", "Sedang Berlangsung"),
    COMPLETED("Completed", "Selesai"),
    CANCELLED("Cancelled", "Dibatalkan"),
    FAILED("Failed", "Gagal");

    private final String englishName;
    private final String indonesianName;

    WorklistStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
