package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Dispensing Type Enum.
 *
 * Represents the type of dispensing transaction.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum DispensingType {
    PRESCRIPTION("Resep Dokter", "RX", "Dispensing berdasarkan resep dokter", true),
    OTC("Obat Bebas", "OTC", "Penjualan obat bebas tanpa resep", false),
    UNIT_DOSE("Unit Dose", "UD", "Unit dose untuk pasien rawat inap", true),
    EMERGENCY("Emergency", "EMRG", "Dispensing darurat", true),
    OUTPATIENT("Pasien Rawat Jalan", "OP", "Dispensing untuk pasien rawat jalan", true),
    INPATIENT("Pasien Rawat Inap", "IP", "Dispensing untuk pasien rawat inap", true),
    NARCOTIC("Narkotika", "NARC", "Dispensing obat narkotika", true),
    PSYCHOTROPIC("Psikotropika", "PSYCH", "Dispensing obat psikotropika", true);

    private final String displayName;
    private final String code;
    private final String description;
    private final boolean requiresPrescription;

    DispensingType(String displayName, String code, String description, boolean requiresPrescription) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.requiresPrescription = requiresPrescription;
    }

    /**
     * Check if counseling is required
     */
    public boolean requiresCounseling() {
        return this == PRESCRIPTION || this == NARCOTIC || this == PSYCHOTROPIC;
    }

    /**
     * Check if special documentation is required
     */
    public boolean requiresSpecialDocumentation() {
        return this == NARCOTIC || this == PSYCHOTROPIC;
    }

    /**
     * Check if pharmacist verification is mandatory
     */
    public boolean requiresPharmacistVerification() {
        return this == NARCOTIC || this == PSYCHOTROPIC || this == PRESCRIPTION;
    }

    /**
     * Check if this is for inpatient
     */
    public boolean isInpatient() {
        return this == INPATIENT || this == UNIT_DOSE;
    }

    /**
     * Check if this is for outpatient
     */
    public boolean isOutpatient() {
        return this == OUTPATIENT || this == OTC;
    }
}
