package com.yudha.hms.radiology.constant;

/**
 * Reaction Severity Enumeration.
 *
 * Severity levels for contrast media reactions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ReactionSeverity {
    /**
     * No reaction observed
     */
    NONE("None", "Tidak Ada", "No adverse reaction observed"),

    /**
     * Mild reaction - limited symptoms
     */
    MILD("Mild", "Ringan", "Limited symptoms, self-limiting, no treatment required"),

    /**
     * Moderate reaction - requires medical intervention
     */
    MODERATE("Moderate", "Sedang", "More pronounced symptoms, requires medical intervention"),

    /**
     * Severe reaction - life-threatening
     */
    SEVERE("Severe", "Berat", "Life-threatening reaction, requires immediate emergency treatment");

    private final String displayName;
    private final String displayNameId;
    private final String description;

    ReactionSeverity(String displayName, String displayNameId, String description) {
        this.displayName = displayName;
        this.displayNameId = displayNameId;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameId() {
        return displayNameId;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if this severity requires emergency intervention
     */
    public boolean requiresEmergencyIntervention() {
        return this == SEVERE;
    }

    /**
     * Check if this severity requires medical attention
     */
    public boolean requiresMedicalAttention() {
        return this == MODERATE || this == SEVERE;
    }
}
