package com.yudha.hms.clinical.entity;

/**
 * Discharge Disposition Enum.
 *
 * Represents where the patient is going after discharge.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public enum DischargeDisposition {
    HOME("Home", "Pulang", "Discharged to home with or without home health services"),
    HOME_HEALTH_SERVICE("Home Health Service", "Pulang dengan Layanan Kesehatan Rumah",
            "Discharged to home with home health care services"),
    TRANSFER_OTHER_FACILITY("Transfer to Other Facility", "Transfer ke Fasilitas Lain",
            "Transferred to another acute care hospital"),
    TRANSFER_REHABILITATION("Transfer to Rehabilitation", "Transfer ke Rehabilitasi",
            "Transferred to rehabilitation facility"),
    TRANSFER_NURSING_HOME("Transfer to Nursing Home", "Transfer ke Panti Jompo",
            "Transferred to skilled nursing facility or nursing home"),
    AGAINST_MEDICAL_ADVICE("Against Medical Advice", "Pulang Paksa (APS)",
            "Left against medical advice (APS - Atas Permintaan Sendiri)"),
    DECEASED("Deceased", "Meninggal", "Patient died in hospital"),
    HOSPICE("Hospice", "Hospis", "Discharged to hospice care"),
    OTHER("Other", "Lainnya", "Other disposition not listed");

    private final String displayName;
    private final String indonesianName;
    private final String description;

    DischargeDisposition(String displayName, String indonesianName, String description) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHomeDischarge() {
        return this == HOME || this == HOME_HEALTH_SERVICE;
    }

    public boolean isTransfer() {
        return this == TRANSFER_OTHER_FACILITY || this == TRANSFER_REHABILITATION ||
               this == TRANSFER_NURSING_HOME;
    }

    public boolean isAgainstAdvice() {
        return this == AGAINST_MEDICAL_ADVICE;
    }

    public boolean isDeceased() {
        return this == DECEASED;
    }
}
