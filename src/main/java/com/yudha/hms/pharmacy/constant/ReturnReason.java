package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Drug Return Reason Enum.
 *
 * Represents reasons for returning dispensed medications.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum ReturnReason {
    WRONG_DRUG("Obat Salah", "WRONG_DRUG", "Obat yang diberikan salah", true, true),
    WRONG_DOSAGE("Dosis Salah", "WRONG_DOSE", "Dosis yang diberikan salah", true, true),
    WRONG_QUANTITY("Jumlah Salah", "WRONG_QTY", "Jumlah yang diberikan salah", true, true),
    EXPIRED("Kadaluarsa", "EXPIRED", "Obat sudah kadaluarsa", true, false),
    DAMAGED("Rusak", "DAMAGED", "Obat rusak atau cacat", true, false),
    PATIENT_REFUSED("Pasien Menolak", "REFUSED", "Pasien menolak menerima obat", false, true),
    DOCTOR_CANCELLED("Dibatalkan Dokter", "DOC_CANCEL", "Resep dibatalkan oleh dokter", false, true),
    DUPLICATE("Duplikat", "DUPLICATE", "Dispensing duplikat", true, true),
    ADVERSE_REACTION("Reaksi Obat", "ADR", "Pasien mengalami reaksi obat yang merugikan", false, false),
    PATIENT_DISCHARGED("Pasien Pulang", "DISCHARGED", "Pasien pulang sebelum obat habis", false, true),
    THERAPEUTIC_CHANGE("Perubahan Terapi", "THERAP_CHG", "Dokter mengubah terapi", false, true),
    PATIENT_DECEASED("Pasien Meninggal", "DECEASED", "Pasien meninggal dunia", false, false),
    OTHER("Lainnya", "OTHER", "Alasan lainnya", false, true);

    private final String displayName;
    private final String code;
    private final String description;
    private final boolean isPharmacyError;
    private final boolean canRestock;

    ReturnReason(String displayName, String code, String description,
                 boolean isPharmacyError, boolean canRestock) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.isPharmacyError = isPharmacyError;
        this.canRestock = canRestock;
    }

    /**
     * Check if this requires investigation
     */
    public boolean requiresInvestigation() {
        return isPharmacyError || this == ADVERSE_REACTION;
    }

    /**
     * Check if this requires incident report
     */
    public boolean requiresIncidentReport() {
        return isPharmacyError && (this == WRONG_DRUG || this == WRONG_DOSAGE);
    }

    /**
     * Check if stock can be returned to inventory
     */
    public boolean allowsRestock() {
        return canRestock && this != EXPIRED && this != DAMAGED;
    }

    /**
     * Check if this indicates quality issue
     */
    public boolean isQualityIssue() {
        return this == EXPIRED || this == DAMAGED;
    }

    /**
     * Check if refund is typically required
     */
    public boolean requiresRefund() {
        return isPharmacyError || this == ADVERSE_REACTION;
    }
}
