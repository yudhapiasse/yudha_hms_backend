package com.yudha.hms.radiology.constant;

/**
 * Maintenance Type Enumeration.
 *
 * Types of equipment maintenance performed in the radiology department.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum MaintenanceType {
    /**
     * Preventive maintenance - scheduled routine maintenance
     */
    PREVENTIVE("Preventive", "Pencegahan", "Scheduled routine maintenance to prevent failures"),

    /**
     * Corrective maintenance - fixing broken equipment
     */
    CORRECTIVE("Corrective", "Perbaikan", "Maintenance to fix broken or malfunctioning equipment"),

    /**
     * Calibration - accuracy verification and adjustment
     */
    CALIBRATION("Calibration", "Kalibrasi", "Verification and adjustment of equipment accuracy");

    private final String displayName;
    private final String displayNameId;
    private final String description;

    MaintenanceType(String displayName, String displayNameId, String description) {
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
     * Check if this is scheduled maintenance
     */
    public boolean isScheduled() {
        return this == PREVENTIVE || this == CALIBRATION;
    }

    /**
     * Check if this is emergency/unplanned maintenance
     */
    public boolean isUnplanned() {
        return this == CORRECTIVE;
    }
}
