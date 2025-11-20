package com.yudha.hms.clinical.entity;

/**
 * Referral Type Enum.
 *
 * Represents the type of referral (internal, external, BPJS).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public enum ReferralType {
    INTERNAL("Internal Referral", "Rujukan Internal",
             "Referral between departments within the same hospital"),

    EXTERNAL("External Referral", "Rujukan Eksternal",
             "Referral to another healthcare facility"),

    BPJS_SPECIALIST("BPJS Specialist Referral", "Rujukan Spesialis BPJS",
                    "BPJS referral to specialist at higher facility"),

    BPJS_ADVANCED("BPJS Advanced Referral", "Rujukan Lanjutan BPJS",
                  "BPJS referral for advanced care (ICU, specialist procedures)"),

    EMERGENCY("Emergency Referral", "Rujukan Darurat",
              "Emergency referral requiring immediate transfer"),

    BACK_REFERRAL("Back Referral", "Rujukan Balik",
                  "Referral back to referring facility after treatment");

    private final String displayName;
    private final String indonesianName;
    private final String description;

    ReferralType(String displayName, String indonesianName, String description) {
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

    public boolean isInternal() {
        return this == INTERNAL;
    }

    public boolean isExternal() {
        return this == EXTERNAL || this == BPJS_SPECIALIST || this == BPJS_ADVANCED ||
               this == EMERGENCY || this == BACK_REFERRAL;
    }

    public boolean isBpjs() {
        return this == BPJS_SPECIALIST || this == BPJS_ADVANCED;
    }

    public boolean isEmergency() {
        return this == EMERGENCY;
    }

    public boolean requiresVClaimIntegration() {
        return isBpjs();
    }
}
