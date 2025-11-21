package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * CD Burning Request Type Enum.
 *
 * Represents the type of CD/DVD burning request.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum CDRequestType {

    PATIENT_CD("Patient CD", "CD Pasien"),
    REFERRING_PHYSICIAN("Referring Physician CD", "CD Dokter Perujuk"),
    INSURANCE_CD("Insurance CD", "CD Asuransi"),
    LEGAL_CD("Legal CD", "CD Legal"),
    BACKUP_CD("Backup CD", "CD Backup"),
    TEACHING_CD("Teaching CD", "CD Pengajaran");

    private final String englishName;
    private final String indonesianName;

    CDRequestType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
