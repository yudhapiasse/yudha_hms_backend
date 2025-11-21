package com.yudha.hms.billing.util;

import com.yudha.hms.billing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility for generating payment numbers.
 *
 * Format: PAY-YYYYMM-XXXXX
 * Example: PAY-202501-00001
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentNumberGenerator {

    private final PaymentRepository paymentRepository;

    private static final String PREFIX = "PAY";
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * Generate next payment number.
     *
     * @return generated payment number
     */
    public synchronized String generatePaymentNumber() {
        LocalDate now = LocalDate.now();
        String monthPrefix = now.format(MONTH_FORMATTER);
        String basePrefix = PREFIX + "-" + monthPrefix + "-";

        // Find the last payment number for this month
        String lastNumber = findLastPaymentNumber(basePrefix);

        int nextSequence = 1;
        if (lastNumber != null) {
            // Extract sequence number from last payment
            String[] parts = lastNumber.split("-");
            if (parts.length == 3) {
                try {
                    int lastSequence = Integer.parseInt(parts[2]);
                    nextSequence = lastSequence + 1;
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse sequence from payment number: {}", lastNumber, e);
                }
            }
        }

        // Format with leading zeros (5 digits)
        String sequenceStr = String.format("%05d", nextSequence);
        String paymentNumber = basePrefix + sequenceStr;

        log.debug("Generated payment number: {}", paymentNumber);
        return paymentNumber;
    }

    /**
     * Find the last payment number with given prefix.
     *
     * @param prefix payment number prefix
     * @return last payment number or null
     */
    private String findLastPaymentNumber(String prefix) {
        return paymentRepository.findAll().stream()
            .map(payment -> payment.getPaymentNumber())
            .filter(number -> number != null && number.startsWith(prefix))
            .max(String::compareTo)
            .orElse(null);
    }

    /**
     * Generate receipt number.
     * Format: RCP-YYYYMM-XXXXX
     *
     * @param paymentNumber payment number
     * @return receipt number
     */
    public String generateReceiptNumber(String paymentNumber) {
        if (paymentNumber != null && paymentNumber.startsWith("PAY-")) {
            return "RCP-" + paymentNumber.substring(4);
        }
        return "RCP-" + LocalDate.now().format(MONTH_FORMATTER) + "-00000";
    }
}
