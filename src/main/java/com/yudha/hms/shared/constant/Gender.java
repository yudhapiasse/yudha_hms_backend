package com.yudha.hms.shared.constant;

import lombok.Getter;

/**
 * Gender enumeration for Indonesian HMS.
 *
 * Uses standard gender values as required by Indonesian regulations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Getter
public enum Gender {
    MALE("Laki-laki", "L"),
    FEMALE("Perempuan", "P");

    private final String displayName;
    private final String code;

    Gender(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    /**
     * Get Gender from code (L or P)
     *
     * @param code gender code
     * @return Gender enum
     */
    public static Gender fromCode(String code) {
        for (Gender gender : values()) {
            if (gender.code.equalsIgnoreCase(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender code: " + code);
    }
}