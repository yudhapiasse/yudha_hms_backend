package com.yudha.hms.laboratory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * Service for Barcode Generation.
 *
 * Generates unique barcodes for specimens, orders, and other laboratory items.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BarcodeGenerationService {

    private static final String SPECIMEN_PREFIX = "SP";
    private static final String ORDER_PREFIX = "LO";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final Random random = new Random();

    /**
     * Generate specimen barcode
     * Format: SP + YYYYMMDD + 6-digit random number
     * Example: SP202501210123456
     */
    public String generateSpecimenBarcode() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String randomPart = String.format("%06d", random.nextInt(1000000));
        String barcode = SPECIMEN_PREFIX + datePart + randomPart;

        log.debug("Generated specimen barcode: {}", barcode);
        return barcode;
    }

    /**
     * Generate order barcode
     * Format: LO + YYYYMMDD + 5-digit sequence
     * Example: LO2025012100123
     */
    public String generateOrderBarcode(long sequenceNumber) {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String sequencePart = String.format("%05d", sequenceNumber);
        String barcode = ORDER_PREFIX + datePart + sequencePart;

        log.debug("Generated order barcode: {}", barcode);
        return barcode;
    }

    /**
     * Generate unique identifier barcode
     * Format: UUID-based barcode
     */
    public String generateUniqueBarcode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        log.debug("Generated unique barcode: {}", uuid);
        return uuid;
    }

    /**
     * Generate barcode with custom prefix
     */
    public String generateBarcodeWithPrefix(String prefix, int length) {
        StringBuilder barcode = new StringBuilder(prefix);
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        barcode.append(datePart);

        int remainingLength = length - barcode.length();
        if (remainingLength > 0) {
            int maxRandom = (int) Math.pow(10, remainingLength);
            String randomPart = String.format("%0" + remainingLength + "d", random.nextInt(maxRandom));
            barcode.append(randomPart);
        }

        log.debug("Generated custom barcode: {}", barcode);
        return barcode.toString();
    }

    /**
     * Validate barcode format
     */
    public boolean validateBarcodeFormat(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            return false;
        }

        // Check specimen barcode format
        if (barcode.startsWith(SPECIMEN_PREFIX)) {
            return barcode.length() == 16 && barcode.substring(2, 10).matches("\\d{8}");
        }

        // Check order barcode format
        if (barcode.startsWith(ORDER_PREFIX)) {
            return barcode.length() == 15 && barcode.substring(2, 10).matches("\\d{8}");
        }

        // Check unique identifier format
        return barcode.length() == 12 && barcode.matches("[A-Z0-9]+");
    }

    /**
     * Generate check digit using Luhn algorithm
     */
    public int generateCheckDigit(String barcode) {
        int sum = 0;
        boolean alternate = false;

        for (int i = barcode.length() - 1; i >= 0; i--) {
            char c = barcode.charAt(i);
            if (Character.isDigit(c)) {
                int digit = Character.getNumericValue(c);
                if (alternate) {
                    digit *= 2;
                    if (digit > 9) {
                        digit = (digit % 10) + 1;
                    }
                }
                sum += digit;
                alternate = !alternate;
            }
        }

        return (10 - (sum % 10)) % 10;
    }

    /**
     * Generate barcode with check digit
     */
    public String generateBarcodeWithCheckDigit(String baseBarcode) {
        int checkDigit = generateCheckDigit(baseBarcode);
        return baseBarcode + checkDigit;
    }

    /**
     * Verify barcode with check digit
     */
    public boolean verifyBarcodeCheckDigit(String barcodeWithCheckDigit) {
        if (barcodeWithCheckDigit == null || barcodeWithCheckDigit.length() < 2) {
            return false;
        }

        String baseBarcode = barcodeWithCheckDigit.substring(0, barcodeWithCheckDigit.length() - 1);
        char checkDigitChar = barcodeWithCheckDigit.charAt(barcodeWithCheckDigit.length() - 1);

        if (!Character.isDigit(checkDigitChar)) {
            return false;
        }

        int providedCheckDigit = Character.getNumericValue(checkDigitChar);
        int calculatedCheckDigit = generateCheckDigit(baseBarcode);

        return providedCheckDigit == calculatedCheckDigit;
    }
}
