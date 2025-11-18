package com.yudha.hms.shared.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

/**
 * BPJS Number Validator Utility.
 *
 * Validates BPJS Kesehatan card number.
 *
 * BPJS Number Format: XXXXXXXXXXXXX (13 digits)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@UtilityClass
public class BpjsValidator {

    private static final Pattern BPJS_PATTERN = Pattern.compile("^[0-9]{13}$");
    private static final int BPJS_LENGTH = 13;

    /**
     * Validate BPJS number format
     *
     * @param bpjsNumber BPJS number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String bpjsNumber) {
        if (bpjsNumber == null || bpjsNumber.isBlank()) {
            return false;
        }

        // Check length
        if (bpjsNumber.length() != BPJS_LENGTH) {
            return false;
        }

        // Check if all digits
        return BPJS_PATTERN.matcher(bpjsNumber).matches();
    }

    /**
     * Validate BPJS number with detailed error message
     *
     * @param bpjsNumber BPJS number to validate
     * @return error message in Indonesian, or null if valid
     */
    public static String validateWithMessage(String bpjsNumber) {
        if (bpjsNumber == null || bpjsNumber.isBlank()) {
            return "Nomor BPJS tidak boleh kosong";
        }

        if (bpjsNumber.length() != BPJS_LENGTH) {
            return String.format("Nomor BPJS harus 13 digit, bukan %d digit", bpjsNumber.length());
        }

        if (!BPJS_PATTERN.matcher(bpjsNumber).matches()) {
            return "Nomor BPJS harus berupa angka";
        }

        return null; // Valid
    }

    /**
     * Format BPJS number for display (with spaces for readability)
     * Format: XXXX-XXXX-XXXXX
     *
     * @param bpjsNumber BPJS number
     * @return formatted BPJS number
     */
    public static String format(String bpjsNumber) {
        if (!isValid(bpjsNumber)) {
            return bpjsNumber;
        }
        return String.format("%s-%s-%s",
            bpjsNumber.substring(0, 4),
            bpjsNumber.substring(4, 8),
            bpjsNumber.substring(8, 13)
        );
    }

    /**
     * Remove formatting from BPJS number
     *
     * @param formattedBpjsNumber formatted BPJS number (may contain dashes)
     * @return clean BPJS number (digits only)
     */
    public static String clean(String formattedBpjsNumber) {
        if (formattedBpjsNumber == null) {
            return null;
        }
        return formattedBpjsNumber.replaceAll("[^0-9]", "");
    }
}