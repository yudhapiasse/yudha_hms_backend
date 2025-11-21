package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Dosage Frequency enumeration.
 *
 * Standard dosing frequencies for medication administration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum DosageFrequency {
    // Common frequencies
    ONCE_DAILY("Sekali Sehari", "OD", "Sekali per hari", 1, 24),
    TWICE_DAILY("Dua Kali Sehari", "BD", "Dua kali per hari", 2, 12),
    THREE_TIMES_DAILY("Tiga Kali Sehari", "TDS", "Tiga kali per hari", 3, 8),
    FOUR_TIMES_DAILY("Empat Kali Sehari", "QDS", "Empat kali per hari", 4, 6),

    // Every X hours
    EVERY_4_HOURS("Setiap 4 Jam", "Q4H", "Setiap 4 jam", 6, 4),
    EVERY_6_HOURS("Setiap 6 Jam", "Q6H", "Setiap 6 jam", 4, 6),
    EVERY_8_HOURS("Setiap 8 Jam", "Q8H", "Setiap 8 jam", 3, 8),
    EVERY_12_HOURS("Setiap 12 Jam", "Q12H", "Setiap 12 jam", 2, 12),

    // Special timings
    MORNING("Pagi", "MANE", "Di pagi hari", 1, 24),
    NIGHT("Malam", "NOCTE", "Malam hari/sebelum tidur", 1, 24),
    BEFORE_MEALS("Sebelum Makan", "AC", "Sebelum makan", 3, 8),
    AFTER_MEALS("Sesudah Makan", "PC", "Sesudah makan", 3, 8),
    WITH_MEALS("Bersama Makan", "OM", "Bersama makan", 3, 8),

    // As needed
    AS_NEEDED("Bila Perlu", "PRN", "Bila diperlukan", 0, 0),
    STAT("STAT", "STAT", "Segera, satu kali saja", 1, 0),

    // Weekly
    ONCE_WEEKLY("Sekali Seminggu", "OW", "Sekali per minggu", 1, 168),
    TWICE_WEEKLY("Dua Kali Seminggu", "BW", "Dua kali per minggu", 2, 84),

    // Other
    ALTERNATE_DAYS("Selang Sehari", "ALT", "Setiap dua hari sekali", 1, 48),
    CUSTOM("Khusus", "CUSTOM", "Frekuensi khusus", 0, 0);

    private final String displayName;
    private final String code;
    private final String description;
    private final int timesPerDay;
    private final int intervalHours;

    DosageFrequency(String displayName, String code, String description, int timesPerDay, int intervalHours) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
        this.timesPerDay = timesPerDay;
        this.intervalHours = intervalHours;
    }

    public static DosageFrequency fromCode(String code) {
        for (DosageFrequency frequency : values()) {
            if (frequency.code.equalsIgnoreCase(code)) {
                return frequency;
            }
        }
        throw new IllegalArgumentException("Unknown dosage frequency code: " + code);
    }

    /**
     * Check if frequency is as-needed
     */
    public boolean isAsNeeded() {
        return this == AS_NEEDED || this == STAT;
    }

    /**
     * Check if frequency requires specific timing
     */
    public boolean requiresSpecificTiming() {
        return this == BEFORE_MEALS || this == AFTER_MEALS || this == WITH_MEALS ||
               this == MORNING || this == NIGHT;
    }

    /**
     * Calculate daily dose multiplier
     */
    public int getDailyDoseMultiplier() {
        return timesPerDay;
    }
}
