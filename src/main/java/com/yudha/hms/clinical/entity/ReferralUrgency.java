package com.yudha.hms.clinical.entity;

/**
 * Referral Urgency Enum.
 *
 * Represents the urgency level of a referral.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public enum ReferralUrgency {
    ROUTINE("Routine", "Rutin", "Non-urgent referral", 7),
    URGENT("Urgent", "Mendesak", "Requires attention within 24-48 hours", 2),
    EMERGENCY("Emergency", "Darurat", "Requires immediate transfer", 0);

    private final String displayName;
    private final String indonesianName;
    private final String description;
    private final int maxDaysUntilTransfer; // 0 = immediate

    ReferralUrgency(String displayName, String indonesianName, String description, int maxDaysUntilTransfer) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
        this.maxDaysUntilTransfer = maxDaysUntilTransfer;
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

    public int getMaxDaysUntilTransfer() {
        return maxDaysUntilTransfer;
    }

    public boolean isEmergency() {
        return this == EMERGENCY;
    }

    public boolean requiresImmediateAction() {
        return this == EMERGENCY || this == URGENT;
    }
}
