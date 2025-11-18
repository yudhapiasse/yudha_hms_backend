package com.yudha.hms.shared.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Medical Record Number (MRN) Generator Utility.
 *
 * Generates MRN with configurable format.
 * Default format: YYYYMM-XXXXX
 * Example: 202501-00001
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@UtilityClass
public class MrnGenerator {

    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_DATE_FORMAT = "yyyyMM";
    private static final String DEFAULT_SEPARATOR = "-";
    private static final int DEFAULT_SEQUENCE_LENGTH = 5;

    /**
     * Generate MRN with default format (YYYYMM-XXXXX)
     *
     * @param sequenceNumber sequence number from database
     * @return generated MRN
     */
    public static String generate(Long sequenceNumber) {
        return generate(sequenceNumber, DEFAULT_PREFIX, DEFAULT_DATE_FORMAT, DEFAULT_SEPARATOR, DEFAULT_SEQUENCE_LENGTH);
    }

    /**
     * Generate MRN with custom prefix
     *
     * @param sequenceNumber sequence number
     * @param prefix custom prefix (e.g., "HMS", "RS")
     * @return generated MRN
     */
    public static String generateWithPrefix(Long sequenceNumber, String prefix) {
        return generate(sequenceNumber, prefix, DEFAULT_DATE_FORMAT, DEFAULT_SEPARATOR, DEFAULT_SEQUENCE_LENGTH);
    }

    /**
     * Generate MRN with full customization
     *
     * @param sequenceNumber sequence number from database
     * @param prefix prefix to add before date (optional, can be null or empty)
     * @param dateFormat date format pattern (e.g., "yyyyMM", "yyyy", "yyMMdd")
     * @param separator separator between components (e.g., "-", "/", "")
     * @param sequenceLength length of sequence number (will be zero-padded)
     * @return generated MRN
     */
    public static String generate(Long sequenceNumber, String prefix, String dateFormat, String separator, int sequenceLength) {
        if (sequenceNumber == null || sequenceNumber < 1) {
            throw new IllegalArgumentException("Sequence number must be positive");
        }

        StringBuilder mrn = new StringBuilder();

        // Add prefix if provided
        if (prefix != null && !prefix.isBlank()) {
            mrn.append(prefix);
            if (!separator.isBlank()) {
                mrn.append(separator);
            }
        }

        // Add date component
        if (dateFormat != null && !dateFormat.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            String datePart = LocalDateTime.now().format(formatter);
            mrn.append(datePart);
            if (!separator.isBlank()) {
                mrn.append(separator);
            }
        }

        // Add sequence number (zero-padded)
        String sequenceFormat = "%0" + sequenceLength + "d";
        String sequencePart = String.format(sequenceFormat, sequenceNumber);
        mrn.append(sequencePart);

        return mrn.toString();
    }

    /**
     * Parse sequence number from MRN
     *
     * @param mrn MRN to parse
     * @return sequence number, or null if cannot parse
     */
    public static Long parseSequenceNumber(String mrn) {
        if (mrn == null || mrn.isBlank()) {
            return null;
        }

        try {
            // Try to extract the last numeric part
            String[] parts = mrn.split("-");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                return Long.parseLong(lastPart);
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validate MRN format
     *
     * @param mrn MRN to validate
     * @return true if format is valid
     */
    public static boolean isValidFormat(String mrn) {
        if (mrn == null || mrn.isBlank()) {
            return false;
        }

        // Basic validation: should contain at least a dash and numbers
        return mrn.matches("^[A-Z0-9]+-[0-9]+$") || mrn.matches("^[0-9]+-[0-9]+$");
    }
}