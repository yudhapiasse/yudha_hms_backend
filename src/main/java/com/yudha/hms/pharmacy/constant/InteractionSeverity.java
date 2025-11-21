package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Interaction Severity enumeration for Drug Interactions.
 *
 * Defines severity levels for drug-drug interactions:
 * - MINOR: Minor interaction, no action needed
 * - MODERATE: Moderate interaction, monitor patient
 * - MAJOR: Major interaction, use alternative or adjust dose
 * - CONTRAINDICATED: Contraindicated, do not use together
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum InteractionSeverity {
    MINOR("Ringan", "MINOR", "Minor interaction, no action needed", 1),
    MODERATE("Sedang", "MODERATE", "Moderate interaction, monitor patient", 2),
    MAJOR("Berat", "MAJOR", "Major interaction, use alternative or adjust dose", 3),
    CONTRAINDICATED("Kontraindikasi", "CONTRAIND", "Contraindicated, do not use together", 4);

    private final String displayName;
    private final String code;
    private final String description;
    private final int severityLevel;

    InteractionSeverity(String displayName, String code, String description, int severityLevel) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.severityLevel = severityLevel;
    }

    public static InteractionSeverity fromCode(String code) {
        for (InteractionSeverity severity : values()) {
            if (severity.code.equalsIgnoreCase(code)) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Unknown interaction severity code: " + code);
    }

    /**
     * Check if requires immediate action
     */
    public boolean requiresImmediateAction() {
        return this == MAJOR || this == CONTRAINDICATED;
    }

    /**
     * Check if can be prescribed together
     */
    public boolean canBePrescribedTogether() {
        return this != CONTRAINDICATED;
    }

    /**
     * Check if requires monitoring
     */
    public boolean requiresMonitoring() {
        return this == MODERATE || this == MAJOR;
    }
}
