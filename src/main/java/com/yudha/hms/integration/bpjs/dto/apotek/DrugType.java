package com.yudha.hms.integration.bpjs.dto.apotek;

/**
 * BPJS Drug Type Constants.
 *
 * Types of drugs in BPJS pharmacy system:
 * - Type 1: PRB (Program Rujuk Balik) drugs - chronic disease management
 * - Type 2: Chronic drugs (not yet stable)
 * - Type 3: Chemotherapy drugs
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public enum DrugType {

    /**
     * Type 1: PRB (Program Rujuk Balik) drugs.
     * For chronic disease management (stable chronic conditions).
     */
    PRB(1, "PRB", "Program Rujuk Balik"),

    /**
     * Type 2: Chronic drugs not yet stable.
     * For chronic conditions that are not yet stable/controlled.
     */
    KRONIS_BELUM_STABIL(2, "Kronis Belum Stabil", "Chronic Not Yet Stable"),

    /**
     * Type 3: Chemotherapy drugs.
     * For cancer treatment and chemotherapy.
     */
    KEMOTERAPI(3, "Kemoterapi", "Chemotherapy");

    private final int code;
    private final String name;
    private final String description;

    DrugType(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get DrugType from code.
     *
     * @param code Drug type code
     * @return DrugType enum
     * @throws IllegalArgumentException if invalid code
     */
    public static DrugType fromCode(int code) {
        for (DrugType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid drug type code: " + code);
    }
}
