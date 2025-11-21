package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * Image Share Purpose Enum.
 *
 * Represents the purpose of sharing radiology images externally.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum SharePurpose {

    PATIENT_ACCESS("Patient Access", "Akses Pasien"),
    REFERRING_PHYSICIAN("Referring Physician", "Dokter Perujuk"),
    SECOND_OPINION("Second Opinion", "Pendapat Kedua"),
    INSURANCE_CLAIM("Insurance Claim", "Klaim Asuransi"),
    LEGAL_CASE("Legal Case", "Kasus Hukum"),
    RESEARCH("Research", "Penelitian"),
    TEACHING("Teaching", "Pengajaran"),
    OTHER("Other", "Lainnya");

    private final String englishName;
    private final String indonesianName;

    SharePurpose(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
