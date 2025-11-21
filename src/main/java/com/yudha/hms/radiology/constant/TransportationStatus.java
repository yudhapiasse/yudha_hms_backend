package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * Transportation Status Enum.
 *
 * Patient transportation coordination statuses for inpatient radiology orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum TransportationStatus {
    NOT_REQUIRED("Not Required", "Tidak Diperlukan"),
    REQUESTED("Requested", "Diminta"),
    IN_TRANSIT("In Transit to Radiology", "Dalam Perjalanan ke Radiologi"),
    ARRIVED("Arrived at Radiology", "Tiba di Radiologi"),
    EXAM_IN_PROGRESS("Examination in Progress", "Pemeriksaan Berlangsung"),
    RETURNING("Returning to Ward", "Kembali ke Ruangan"),
    RETURNED("Returned to Ward", "Sudah Kembali ke Ruangan");

    private final String englishName;
    private final String indonesianName;

    TransportationStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }

    public String getDisplayName() {
        return englishName;
    }
}
