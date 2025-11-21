package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Storage Condition enumeration for Drug Management.
 *
 * Defines storage requirements for drugs:
 * - ROOM_TEMPERATURE: Store at room temperature (15-25°C)
 * - REFRIGERATED: Store in refrigerator (2-8°C)
 * - FROZEN: Store in freezer (-20°C or below)
 * - CONTROLLED_ROOM_TEMP: Store at controlled room temp (20-25°C)
 * - COOL_PLACE: Store in cool place (8-15°C)
 * - PROTECT_FROM_LIGHT: Protect from light exposure
 * - PROTECT_FROM_MOISTURE: Keep in dry place
 * - NARCOTICS_SAFE: Store in locked narcotics safe
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum StorageCondition {
    ROOM_TEMPERATURE("Suhu Ruangan", "ROOM_TEMP", "Store at room temperature (15-25°C)"),
    REFRIGERATED("Lemari Es", "REFRIG", "Store in refrigerator (2-8°C)"),
    FROZEN("Beku", "FROZEN", "Store in freezer (-20°C or below)"),
    CONTROLLED_ROOM_TEMP("Suhu Terkontrol", "CTRL_TEMP", "Store at controlled room temp (20-25°C)"),
    COOL_PLACE("Tempat Sejuk", "COOL", "Store in cool place (8-15°C)"),
    PROTECT_FROM_LIGHT("Lindungi dari Cahaya", "NO_LIGHT", "Protect from light exposure"),
    PROTECT_FROM_MOISTURE("Lindungi dari Kelembaban", "DRY", "Keep in dry place"),
    NARCOTICS_SAFE("Lemari Narkotika", "NARC_SAFE", "Store in locked narcotics safe"),
    PSYCHOTROPIC_SAFE("Lemari Psikotropika", "PSYC_SAFE", "Store in locked psychotropic safe");

    private final String displayName;
    private final String code;
    private final String description;

    StorageCondition(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static StorageCondition fromCode(String code) {
        for (StorageCondition condition : values()) {
            if (condition.code.equalsIgnoreCase(code)) {
                return condition;
            }
        }
        throw new IllegalArgumentException("Unknown storage condition code: " + code);
    }

    /**
     * Check if requires temperature control
     */
    public boolean requiresTemperatureControl() {
        return this == REFRIGERATED || this == FROZEN ||
               this == CONTROLLED_ROOM_TEMP || this == COOL_PLACE;
    }

    /**
     * Check if requires special security
     */
    public boolean requiresSpecialSecurity() {
        return this == NARCOTICS_SAFE || this == PSYCHOTROPIC_SAFE;
    }

    /**
     * Check if requires environmental protection
     */
    public boolean requiresEnvironmentalProtection() {
        return this == PROTECT_FROM_LIGHT || this == PROTECT_FROM_MOISTURE;
    }
}
