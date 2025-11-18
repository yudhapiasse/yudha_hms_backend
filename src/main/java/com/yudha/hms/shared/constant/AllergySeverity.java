package com.yudha.hms.shared.constant;

import lombok.Getter;

/**
 * Allergy Severity enumeration.
 *
 * Severity levels for patient allergies.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Getter
public enum AllergySeverity {
    MILD("Ringan", "Mild reaction (rash, itching)"),
    MODERATE("Sedang", "Moderate reaction (swelling, difficulty breathing)"),
    SEVERE("Berat", "Severe reaction (anaphylaxis risk)"),
    LIFE_THREATENING("Mengancam Nyawa", "Life-threatening reaction (anaphylactic shock)");

    private final String displayName;
    private final String description;

    AllergySeverity(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}