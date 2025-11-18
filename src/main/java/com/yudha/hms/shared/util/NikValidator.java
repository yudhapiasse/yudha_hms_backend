package com.yudha.hms.shared.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * NIK (Nomor Induk Kependudukan) Validator Utility.
 *
 * Validates Indonesian National ID Number (NIK) according to regulations.
 *
 * NIK Format: PPKKSSDDMMYYXXXX (16 digits)
 * - PP: Province code (2 digits)
 * - KK: City/Regency code (2 digits)
 * - SS: District code (2 digits)
 * - DDMMYY: Birth date (6 digits, DD+40 for females)
 * - XXXX: Sequential number (4 digits)
 *
 * Reference: Peraturan Presiden No. 96 Tahun 2018
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@UtilityClass
public class NikValidator {

    private static final Pattern NIK_PATTERN = Pattern.compile("^[0-9]{16}$");
    private static final int NIK_LENGTH = 16;

    /**
     * Validate NIK format and structure
     *
     * @param nik NIK to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String nik) {
        if (nik == null || nik.isBlank()) {
            return false;
        }

        // Check length
        if (nik.length() != NIK_LENGTH) {
            return false;
        }

        // Check if all digits
        if (!NIK_PATTERN.matcher(nik).matches()) {
            return false;
        }

        // Validate birth date component
        try {
            extractBirthDate(nik);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate NIK with detailed error message
     *
     * @param nik NIK to validate
     * @return error message in Indonesian, or null if valid
     */
    public static String validateWithMessage(String nik) {
        if (nik == null || nik.isBlank()) {
            return "NIK tidak boleh kosong";
        }

        if (nik.length() != NIK_LENGTH) {
            return String.format("NIK harus 16 digit, bukan %d digit", nik.length());
        }

        if (!NIK_PATTERN.matcher(nik).matches()) {
            return "NIK harus berupa angka";
        }

        try {
            LocalDate birthDate = extractBirthDate(nik);
            if (birthDate.isAfter(LocalDate.now())) {
                return "Tanggal lahir di NIK tidak valid (tanggal masa depan)";
            }
            return null; // Valid
        } catch (Exception e) {
            return "Format tanggal lahir di NIK tidak valid";
        }
    }

    /**
     * Extract province code from NIK
     *
     * @param nik NIK
     * @return province code (2 digits)
     */
    public static String extractProvinceCode(String nik) {
        if (!isValid(nik)) {
            throw new IllegalArgumentException("NIK tidak valid");
        }
        return nik.substring(0, 2);
    }

    /**
     * Extract city/regency code from NIK
     *
     * @param nik NIK
     * @return city code (4 digits including province)
     */
    public static String extractCityCode(String nik) {
        if (!isValid(nik)) {
            throw new IllegalArgumentException("NIK tidak valid");
        }
        return nik.substring(0, 4);
    }

    /**
     * Extract district code from NIK
     *
     * @param nik NIK
     * @return district code (6 digits including province and city)
     */
    public static String extractDistrictCode(String nik) {
        if (!isValid(nik)) {
            throw new IllegalArgumentException("NIK tidak valid");
        }
        return nik.substring(0, 6);
    }

    /**
     * Extract birth date from NIK
     *
     * For females, the date component has 40 added to the day.
     * E.g., birth date 15/05/1990 becomes:
     * - Male: 150590
     * - Female: 550590 (15+40=55)
     *
     * @param nik NIK
     * @return birth date
     */
    public static LocalDate extractBirthDate(String nik) {
        if (nik == null || nik.length() < 12) {
            throw new IllegalArgumentException("NIK tidak valid");
        }

        String ddmmyy = nik.substring(6, 12);
        int day = Integer.parseInt(ddmmyy.substring(0, 2));
        int month = Integer.parseInt(ddmmyy.substring(2, 4));
        int year = Integer.parseInt(ddmmyy.substring(4, 6));

        // Adjust day for females (day > 40 means female)
        if (day > 40) {
            day = day - 40;
        }

        // Determine century (assume 1900s for years > current year's last 2 digits, 2000s otherwise)
        int currentYearLastTwo = LocalDate.now().getYear() % 100;
        if (year > currentYearLastTwo) {
            year += 1900;
        } else {
            year += 2000;
        }

        return LocalDate.of(year, month, day);
    }

    /**
     * Extract gender from NIK
     *
     * Males have normal day (01-31)
     * Females have day+40 (41-71)
     *
     * @param nik NIK
     * @return "L" for male, "P" for female
     */
    public static String extractGender(String nik) {
        if (nik == null || nik.length() < 8) {
            throw new IllegalArgumentException("NIK tidak valid");
        }

        int day = Integer.parseInt(nik.substring(6, 8));
        return day > 40 ? "P" : "L"; // P = Perempuan, L = Laki-laki
    }

    /**
     * Calculate age from NIK
     *
     * @param nik NIK
     * @return age in years
     */
    public static int calculateAge(String nik) {
        LocalDate birthDate = extractBirthDate(nik);
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Format NIK for display (with spaces for readability)
     * Format: PP KK SS DDMMYY XXXX
     *
     * @param nik NIK
     * @return formatted NIK
     */
    public static String format(String nik) {
        if (!isValid(nik)) {
            return nik;
        }
        return String.format("%s %s %s %s %s",
            nik.substring(0, 2),   // Province
            nik.substring(2, 4),   // City
            nik.substring(4, 6),   // District
            nik.substring(6, 12),  // Birth date
            nik.substring(12, 16)  // Sequential
        );
    }
}