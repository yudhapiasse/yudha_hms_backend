package com.yudha.hms.registration.entity;

/**
 * Room class enum for inpatient admissions.
 * Indonesian hospital room classifications from economy to VIP.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public enum RoomClass {
    VIP("VIP Suite", 1500000),
    KELAS_1("Kelas 1", 750000),
    KELAS_2("Kelas 2", 400000),
    KELAS_3("Kelas 3", 200000),
    ICU("Intensive Care Unit", 2000000),
    NICU("Neonatal ICU", 2500000),
    PICU("Pediatric ICU", 2300000);

    private final String displayName;
    private final double baseRate;

    RoomClass(String displayName, double baseRate) {
        this.displayName = displayName;
        this.baseRate = baseRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseRate() {
        return baseRate;
    }
}
