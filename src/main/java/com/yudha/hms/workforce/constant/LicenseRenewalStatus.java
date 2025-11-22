package com.yudha.hms.workforce.constant;

import lombok.Getter;

@Getter
public enum LicenseRenewalStatus {
    VALID("Valid", "Berlaku"),
    EXPIRING_SOON("Expiring Soon", "Akan Berakhir"),
    EXPIRED("Expired", "Kadaluarsa"),
    RENEWAL_IN_PROGRESS("Renewal In Progress", "Proses Perpanjangan"),
    SUSPENDED("Suspended", "Ditangguhkan");

    private final String englishName;
    private final String indonesianName;

    LicenseRenewalStatus(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
