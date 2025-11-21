package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Drug Unit enumeration for Pharmacy Module.
 *
 * Defines unit configurations for drug packaging and dispensing:
 * - TABLET: Tablet form
 * - CAPSULE: Capsule form
 * - AMPULE: Injectable ampule
 * - VIAL: Injectable vial
 * - BOTTLE: Bottle (liquid/syrup)
 * - TUBE: Tube (cream/ointment)
 * - SACHET: Sachet/powder form
 * - SUPPOSITORY: Suppository
 * - PATCH: Transdermal patch
 * - INHALER: Inhaler device
 * - DROPS: Eye/ear drops
 * - SPRAY: Spray form
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum DrugUnit {
    TABLET("Tablet", "TAB", "Tablet form"),
    CAPSULE("Kapsul", "CAP", "Capsule form"),
    AMPULE("Ampul", "AMP", "Injectable ampule"),
    VIAL("Vial", "VIAL", "Injectable vial"),
    BOTTLE("Botol", "BTL", "Bottle (liquid/syrup)"),
    TUBE("Tube", "TUBE", "Tube (cream/ointment)"),
    SACHET("Sachet", "SCT", "Sachet/powder form"),
    SUPPOSITORY("Suppositoria", "SUPP", "Suppository"),
    PATCH("Patch", "PATCH", "Transdermal patch"),
    INHALER("Inhaler", "INH", "Inhaler device"),
    DROPS("Tetes", "DROPS", "Eye/ear drops"),
    SPRAY("Spray", "SPRAY", "Spray form"),
    INJECTION("Injeksi", "INJ", "Injection"),
    CREAM("Krim", "CRM", "Cream"),
    OINTMENT("Salep", "OIN", "Ointment"),
    SOLUTION("Larutan", "SOL", "Solution"),
    SUSPENSION("Suspensi", "SUSP", "Suspension"),
    EMULSION("Emulsi", "EMUL", "Emulsion");

    private final String displayName;
    private final String code;
    private final String description;

    DrugUnit(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static DrugUnit fromCode(String code) {
        for (DrugUnit unit : values()) {
            if (unit.code.equalsIgnoreCase(code)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown drug unit code: " + code);
    }

    /**
     * Check if unit is injectable
     */
    public boolean isInjectable() {
        return this == AMPULE || this == VIAL || this == INJECTION;
    }

    /**
     * Check if unit is oral
     */
    public boolean isOral() {
        return this == TABLET || this == CAPSULE || this == BOTTLE ||
               this == SACHET || this == SOLUTION || this == SUSPENSION;
    }

    /**
     * Check if unit is topical
     */
    public boolean isTopical() {
        return this == TUBE || this == CREAM || this == OINTMENT ||
               this == PATCH || this == SPRAY;
    }
}
