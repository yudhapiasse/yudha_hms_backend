package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum LicenseType {
    STR("Surat Tanda Registrasi", "Surat Tanda Registrasi"),
    SIP("Surat Izin Praktik", "Surat Izin Praktik"),
    SIPP("Surat Izin Praktik Psikolog", "Surat Izin Praktik Psikolog"),
    SIKP("Surat Izin Kerja Perawat", "Surat Izin Kerja Perawat"),
    SIPA("Surat Izin Praktik Apoteker", "Surat Izin Praktik Apoteker"),
    SIK("Surat Izin Kerja", "Surat Izin Kerja");

    private final String englishName;
    private final String indonesianName;

    LicenseType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
