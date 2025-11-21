package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * CD Burning Request Status Enum.
 *
 * Represents the status of a CD/DVD burning request.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum CDRequestStatus {

    PENDING("Pending", "Menunggu"),
    QUEUED("Queued", "Dalam Antrian"),
    PROCESSING("Processing", "Sedang Diproses"),
    BURNING("Burning", "Sedang Dibakar"),
    COMPLETED("Completed", "Selesai"),
    READY_FOR_PICKUP("Ready for Pickup", "Siap Diambil"),
    DELIVERED("Delivered", "Dikirim"),
    FAILED("Failed", "Gagal"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    CDRequestStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
