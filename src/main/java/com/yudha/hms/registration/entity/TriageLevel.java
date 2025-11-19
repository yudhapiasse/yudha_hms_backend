package com.yudha.hms.registration.entity;

/**
 * Triage Level Classification.
 * Based on Emergency Severity Index (ESI) methodology.
 *
 * Color-coded priority system used in emergency departments:
 * - RED: Critical/Immediate - Life-threatening conditions
 * - YELLOW: Urgent - High risk conditions
 * - GREEN: Non-urgent - Stable conditions
 * - WHITE: Minor - Very low acuity
 * - BLACK: Deceased - No signs of life
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public enum TriageLevel {
    RED("Critical", "Immediate life-threatening", 1),
    YELLOW("Urgent", "High risk, needs prompt attention", 2),
    GREEN("Non-Urgent", "Stable, can wait", 3),
    WHITE("Minor", "Minor complaints", 4),
    BLACK("Deceased", "No signs of life", 5);

    private final String displayName;
    private final String description;
    private final int priority;

    TriageLevel(String displayName, String description, int priority) {
        this.displayName = displayName;
        this.description = description;
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCritical() {
        return this == RED || this == BLACK;
    }

    public boolean requiresImmediateAttention() {
        return this == RED;
    }
}
