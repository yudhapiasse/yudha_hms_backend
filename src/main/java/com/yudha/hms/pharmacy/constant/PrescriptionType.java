package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Prescription Type enumeration.
 *
 * Categorizes prescriptions based on their nature and regulatory requirements.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum PrescriptionType {
    REGULAR("Reguler", "REG", "Resep standar", false, false),
    NARCOTIC("Narkotika", "NARC", "Zat narkotika terkontrol", true, true),
    PSYCHOTROPIC("Psikotropika", "PSYCH", "Zat psikotropika", true, true),
    HIGH_ALERT("Obat LASA/High Alert", "HIGH", "Obat high alert/LASA", false, true),
    EMERGENCY("Darurat", "EMER", "Resep darurat", false, false),
    STANDING_ORDER("Standing Order", "STAND", "Standing order/protokol", false, false),
    STAT("STAT", "STAT", "Dosis segera satu kali", false, false),
    DISCHARGE("Pulang", "DISCH", "Resep pulang", false, false);

    private final String displayName;
    private final String code;
    private final String description;
    private final boolean isControlled;
    private final boolean requiresSpecialHandling;

    PrescriptionType(String displayName, String code, String description,
                    boolean isControlled, boolean requiresSpecialHandling) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.isControlled = isControlled;
        this.requiresSpecialHandling = requiresSpecialHandling;
    }

    public static PrescriptionType fromCode(String code) {
        for (PrescriptionType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown prescription type code: " + code);
    }

    /**
     * Check if requires special documentation
     */
    public boolean requiresSpecialDocumentation() {
        return isControlled || this == HIGH_ALERT;
    }

    /**
     * Check if requires dual verification
     */
    public boolean requiresDualVerification() {
        return isControlled || this == HIGH_ALERT;
    }

    /**
     * Check if has restricted validity period
     */
    public boolean hasRestrictedValidity() {
        return isControlled || this == EMERGENCY;
    }
}
