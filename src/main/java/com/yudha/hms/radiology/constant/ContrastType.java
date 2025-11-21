package com.yudha.hms.radiology.constant;

/**
 * Contrast Type Enumeration.
 *
 * Types of contrast media used in radiology examinations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ContrastType {
    /**
     * No contrast media
     */
    NONE("None", "Tidak Ada", "No contrast media required"),

    /**
     * Iodine-based contrast (for CT, angiography)
     */
    IODINE_BASED("Iodine-Based", "Berbasis Iodin", "Iodine-based contrast for CT and angiography"),

    /**
     * Gadolinium-based contrast (for MRI)
     */
    GADOLINIUM_BASED("Gadolinium-Based", "Berbasis Gadolinium", "Gadolinium-based contrast for MRI"),

    /**
     * Barium-based contrast (for GI studies)
     */
    BARIUM_BASED("Barium-Based", "Berbasis Barium", "Barium-based contrast for gastrointestinal studies"),

    /**
     * Microbubble contrast (for ultrasound)
     */
    MICROBUBBLE("Microbubble", "Gelembung Mikro", "Microbubble contrast for ultrasound");

    private final String displayName;
    private final String displayNameId;
    private final String description;

    ContrastType(String displayName, String displayNameId, String description) {
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
     * Check if contrast is required
     */
    public boolean isContrastRequired() {
        return this != NONE;
    }

    /**
     * Check if renal function test is required before administration
     */
    public boolean requiresRenalFunctionCheck() {
        return this == IODINE_BASED || this == GADOLINIUM_BASED;
    }
}
