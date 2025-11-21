package com.yudha.hms.billing.util;

import com.yudha.hms.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility for generating invoice numbers.
 *
 * Format: INV-YYYYMM-XXXXX
 * Example: INV-202501-00001
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceNumberGenerator {

    private final InvoiceRepository invoiceRepository;

    private static final String PREFIX = "INV";
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * Generate next invoice number.
     *
     * @return generated invoice number
     */
    public synchronized String generateInvoiceNumber() {
        LocalDate now = LocalDate.now();
        String monthPrefix = now.format(MONTH_FORMATTER);
        String basePrefix = PREFIX + "-" + monthPrefix + "-";

        // Find the last invoice number for this month
        String lastNumber = findLastInvoiceNumber(basePrefix);

        int nextSequence = 1;
        if (lastNumber != null) {
            // Extract sequence number from last invoice
            String[] parts = lastNumber.split("-");
            if (parts.length == 3) {
                try {
                    int lastSequence = Integer.parseInt(parts[2]);
                    nextSequence = lastSequence + 1;
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse sequence from invoice number: {}", lastNumber, e);
                }
            }
        }

        // Format with leading zeros (5 digits)
        String sequenceStr = String.format("%05d", nextSequence);
        String invoiceNumber = basePrefix + sequenceStr;

        log.debug("Generated invoice number: {}", invoiceNumber);
        return invoiceNumber;
    }

    /**
     * Find the last invoice number with given prefix.
     *
     * @param prefix invoice number prefix
     * @return last invoice number or null
     */
    private String findLastInvoiceNumber(String prefix) {
        // Query database for the last invoice number with this prefix
        // This is a simplified version - in production, you'd want a more efficient query
        return invoiceRepository.findAll().stream()
            .map(invoice -> invoice.getInvoiceNumber())
            .filter(number -> number != null && number.startsWith(prefix))
            .max(String::compareTo)
            .orElse(null);
    }

    /**
     * Validate invoice number format.
     *
     * @param invoiceNumber invoice number to validate
     * @return true if valid
     */
    public boolean isValidInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            return false;
        }

        String[] parts = invoiceNumber.split("-");
        if (parts.length != 3) {
            return false;
        }

        // Check prefix
        if (!PREFIX.equals(parts[0])) {
            return false;
        }

        // Check month format (YYYYMM)
        if (parts[1].length() != 6) {
            return false;
        }

        try {
            Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        // Check sequence (5 digits)
        if (parts[2].length() != 5) {
            return false;
        }

        try {
            Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
