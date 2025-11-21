package com.yudha.hms.billing.service;

import com.yudha.hms.billing.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service for payment receipt generation.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentReceiptService {

    private final PaymentService paymentService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Generate payment receipt PDF.
     *
     * @param paymentId payment ID
     * @return PDF bytes
     */
    @Transactional
    public byte[] generateReceiptPdf(UUID paymentId) {
        log.info("Generating receipt PDF for payment: {}", paymentId);

        PaymentResponse payment = paymentService.getPaymentById(paymentId);

        // Mark as printed
        paymentService.markReceiptAsPrinted(paymentId);

        // TODO: Implement PDF generation using iText or PDFBox
        // For now, return a placeholder text representation
        return generatePlaceholderReceipt(payment);
    }

    /**
     * Generate receipt text content.
     *
     * @param paymentId payment ID
     * @return receipt text
     */
    @Transactional(readOnly = true)
    public String generateReceiptText(UUID paymentId) {
        log.info("Generating receipt text for payment: {}", paymentId);

        PaymentResponse payment = paymentService.getPaymentById(paymentId);
        return buildReceiptText(payment);
    }

    /**
     * Get receipt filename for download.
     *
     * @param receiptNumber receipt number
     * @return filename
     */
    public String getReceiptFileName(String receiptNumber) {
        return "receipt_" + receiptNumber.replace("-", "_") + ".pdf";
    }

    /**
     * Generate placeholder receipt (text representation).
     * TODO: Replace with actual PDF generation using iText/PDFBox/Flying Saucer
     *
     * @param payment payment details
     * @return receipt bytes
     */
    private byte[] generatePlaceholderReceipt(PaymentResponse payment) {
        String receiptText = buildReceiptText(payment);
        return receiptText.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Build receipt text content.
     *
     * @param payment payment details
     * @return formatted receipt text
     */
    private String buildReceiptText(PaymentResponse payment) {
        StringBuilder receipt = new StringBuilder();

        // Header
        receipt.append("================================================================\n");
        receipt.append("                    PAYMENT RECEIPT                              \n");
        receipt.append("                   Hospital Management System                    \n");
        receipt.append("================================================================\n\n");

        // Receipt information
        receipt.append("Receipt Number    : ").append(payment.getReceiptNumber()).append("\n");
        receipt.append("Payment Number    : ").append(payment.getPaymentNumber()).append("\n");
        receipt.append("Invoice Number    : ").append(payment.getInvoiceNumber()).append("\n");
        receipt.append("Payment Date      : ").append(payment.getPaymentDate().format(DATE_FORMATTER)).append("\n\n");

        // Patient information
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("PATIENT INFORMATION\n");
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("MRN               : ").append(payment.getPatientMrn()).append("\n");
        receipt.append("Name              : ").append(payment.getPatientName()).append("\n\n");

        // Payment details
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("PAYMENT DETAILS\n");
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("Payment Method    : ").append(payment.getPaymentMethod().getDisplayName()).append("\n");
        receipt.append("Amount            : Rp ").append(formatCurrency(payment.getAmount())).append("\n");

        // Payment method specific details
        if (payment.getCashTendered() != null) {
            receipt.append("Cash Tendered     : Rp ").append(formatCurrency(payment.getCashTendered())).append("\n");
            receipt.append("Change            : Rp ").append(formatCurrency(payment.getChangeAmount())).append("\n");
        }

        if (payment.getCardLast4() != null) {
            receipt.append("Card Number       : **** **** **** ").append(payment.getCardLast4()).append("\n");
            if (payment.getCardType() != null) {
                receipt.append("Card Type         : ").append(payment.getCardType()).append("\n");
            }
        }

        if (payment.getAccountLast4() != null) {
            receipt.append("Account Number    : **** **** **** ").append(payment.getAccountLast4()).append("\n");
            if (payment.getBankName() != null) {
                receipt.append("Bank Name         : ").append(payment.getBankName()).append("\n");
            }
        }

        if (payment.getGatewayTransactionId() != null) {
            receipt.append("Transaction ID    : ").append(payment.getGatewayTransactionId()).append("\n");
            if (payment.getGatewayName() != null) {
                receipt.append("Gateway           : ").append(payment.getGatewayName()).append("\n");
            }
        }

        if (payment.getAuthorizationCode() != null) {
            receipt.append("Authorization Code: ").append(payment.getAuthorizationCode()).append("\n");
        }

        if (payment.getReferenceNumber() != null) {
            receipt.append("Reference Number  : ").append(payment.getReferenceNumber()).append("\n");
        }

        receipt.append("\n");

        // Payment status
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("STATUS\n");
        receipt.append("----------------------------------------------------------------\n");
        receipt.append("Payment Status    : ").append(payment.getStatus().getDisplayName()).append("\n");

        if (payment.getConfirmedDate() != null) {
            receipt.append("Confirmed Date    : ").append(payment.getConfirmedDate().format(DATE_FORMATTER)).append("\n");
            receipt.append("Confirmed By      : ").append(payment.getConfirmedBy()).append("\n");
        }

        receipt.append("\n");

        // Refund information (if applicable)
        if (payment.getRefunded() != null && payment.getRefunded()) {
            receipt.append("----------------------------------------------------------------\n");
            receipt.append("REFUND INFORMATION\n");
            receipt.append("----------------------------------------------------------------\n");
            receipt.append("Refund Amount     : Rp ").append(formatCurrency(payment.getRefundAmount())).append("\n");
            receipt.append("Refund Date       : ").append(payment.getRefundDate().format(DATE_FORMATTER)).append("\n");
            receipt.append("Refund Reason     : ").append(payment.getRefundReason()).append("\n");
            receipt.append("Processed By      : ").append(payment.getRefundProcessedBy()).append("\n\n");
        }

        // Cashier information
        if (payment.getCashierName() != null) {
            receipt.append("----------------------------------------------------------------\n");
            receipt.append("CASHIER INFORMATION\n");
            receipt.append("----------------------------------------------------------------\n");
            receipt.append("Cashier           : ").append(payment.getCashierName()).append("\n");
            if (payment.getShiftId() != null) {
                receipt.append("Shift ID          : ").append(payment.getShiftId()).append("\n");
            }
            receipt.append("\n");
        }

        // Notes
        if (payment.getNotes() != null && !payment.getNotes().isEmpty()) {
            receipt.append("----------------------------------------------------------------\n");
            receipt.append("NOTES\n");
            receipt.append("----------------------------------------------------------------\n");
            receipt.append(payment.getNotes()).append("\n\n");
        }

        // Footer
        receipt.append("================================================================\n");
        receipt.append("              Thank you for your payment                         \n");
        receipt.append("            Please keep this receipt for your records           \n");
        receipt.append("================================================================\n");
        receipt.append("\n");
        receipt.append("Printed: ").append(java.time.LocalDateTime.now().format(DATE_FORMATTER)).append("\n");

        if (payment.getReceiptPrintCount() != null && payment.getReceiptPrintCount() > 0) {
            receipt.append("Print Count: ").append(payment.getReceiptPrintCount() + 1).append("\n");
        }

        return receipt.toString();
    }

    /**
     * Format currency value for display.
     *
     * @param amount amount to format
     * @return formatted currency string
     */
    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return String.format("%,.2f", amount);
    }
}
