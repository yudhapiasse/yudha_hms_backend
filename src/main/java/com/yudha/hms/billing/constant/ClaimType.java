package com.yudha.hms.billing.constant;

import lombok.Getter;

/**
 * Claim Type enumeration for Insurance Claim Management.
 *
 * Types of insurance claims:
 * - OUTPATIENT: Outpatient/ambulatory care claims
 * - INPATIENT: Inpatient/hospitalization claims
 * - EMERGENCY: Emergency department claims
 * - DENTAL: Dental care claims
 * - MATERNITY: Maternity care claims
 * - SURGICAL: Surgical procedure claims
 * - PHARMACY: Pharmacy/medication claims
 * - DIAGNOSTIC: Diagnostic/laboratory claims
 * - THERAPY: Therapy/rehabilitation claims
 * - PREVENTIVE: Preventive care claims
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum ClaimType {
    OUTPATIENT("Rawat Jalan", "OUTPATIENT", "Outpatient care claim"),
    INPATIENT("Rawat Inap", "INPATIENT", "Inpatient care claim"),
    EMERGENCY("Gawat Darurat", "EMERGENCY", "Emergency care claim"),
    DENTAL("Gigi", "DENTAL", "Dental care claim"),
    MATERNITY("Persalinan", "MATERNITY", "Maternity care claim"),
    SURGICAL("Bedah", "SURGICAL", "Surgical procedure claim"),
    PHARMACY("Farmasi", "PHARMACY", "Pharmacy claim"),
    DIAGNOSTIC("Diagnostik", "DIAGNOSTIC", "Diagnostic/lab claim"),
    THERAPY("Terapi", "THERAPY", "Therapy/rehabilitation claim"),
    PREVENTIVE("Preventif", "PREVENTIVE", "Preventive care claim");

    private final String displayName;
    private final String code;
    private final String description;

    ClaimType(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Get ClaimType from code
     *
     * @param code claim type code
     * @return ClaimType enum
     */
    public static ClaimType fromCode(String code) {
        for (ClaimType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown claim type code: " + code);
    }

    /**
     * Check if claim type requires hospitalization documentation
     *
     * @return true if requires hospitalization docs
     */
    public boolean requiresHospitalizationDocs() {
        return this == INPATIENT || this == EMERGENCY;
    }

    /**
     * Check if claim type is for procedures
     *
     * @return true if procedure-based
     */
    public boolean isProcedureBased() {
        return this == SURGICAL || this == DENTAL || this == THERAPY;
    }
}
