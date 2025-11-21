package com.yudha.hms.radiology.constant;

/**
 * Laterality Enumeration.
 *
 * Indicates which side of the body is examined (for applicable examinations).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum Laterality {
    /**
     * Left side
     */
    LEFT("Left", "Kiri", "Left side of the body"),

    /**
     * Right side
     */
    RIGHT("Right", "Kanan", "Right side of the body"),

    /**
     * Both sides
     */
    BILATERAL("Bilateral", "Bilateral", "Both sides of the body"),

    /**
     * Not applicable for this examination
     */
    NOT_APPLICABLE("Not Applicable", "Tidak Berlaku", "Laterality is not applicable for this examination");

    private final String displayName;
    private final String displayNameId;
    private final String description;

    Laterality(String displayName, String displayNameId, String description) {
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
     * Check if laterality specifies a single side
     */
    public boolean isSingleSide() {
        return this == LEFT || this == RIGHT;
    }
}
