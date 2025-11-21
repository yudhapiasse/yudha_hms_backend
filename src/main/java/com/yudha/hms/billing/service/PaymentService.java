package com.yudha.hms.billing.service;

import com.yudha.hms.billing.constant.PaymentMethod;
import com.yudha.hms.billing.constant.PaymentStatus;
import com.yudha.hms.billing.dto.*;
import com.yudha.hms.billing.entity.Invoice;
import com.yudha.hms.billing.entity.Payment;
import com.yudha.hms.billing.repository.InvoiceRepository;
import com.yudha.hms.billing.repository.PaymentRepository;
import com.yudha.hms.billing.util.PaymentNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for payment processing operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentNumberGenerator paymentNumberGenerator;

    /**
     * Process a new payment for an invoice.
     *
     * @param request payment request
     * @param createdBy user creating the payment
     * @return payment response
     */
    @Transactional
    public PaymentResponse processPayment(CreatePaymentRequest request, String createdBy) {
        log.info("Processing payment for invoice: {}", request.getInvoiceId());

        // Validate invoice exists and is payable
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + request.getInvoiceId()));

        if (invoice.getVoided()) {
            throw new IllegalStateException("Cannot process payment for voided invoice");
        }

        // Validate payment amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }

        if (request.getAmount().compareTo(invoice.getOutstandingBalance()) > 0) {
            throw new IllegalArgumentException(
                    String.format("Payment amount (%.2f) exceeds outstanding balance (%.2f)",
                            request.getAmount(), invoice.getOutstandingBalance())
            );
        }

        // Generate payment number
        String paymentNumber = paymentNumberGenerator.generatePaymentNumber();

        // Calculate change amount for cash payments
        BigDecimal changeAmount = BigDecimal.ZERO;
        if (request.getPaymentMethod() == PaymentMethod.CASH && request.getCashTendered() != null) {
            changeAmount = request.getCashTendered().subtract(request.getAmount());
            if (changeAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Cash tendered is less than payment amount");
            }
        }

        // Create payment
        Payment payment = Payment.builder()
                .paymentNumber(paymentNumber)
                .invoice(invoice)
                .patientId(invoice.getPatientId())
                .patientMrn(invoice.getPatientMrn())
                .patientName(invoice.getPatientName())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .gatewayTransactionId(request.getGatewayTransactionId())
                .gatewayName(request.getGatewayName())
                .authorizationCode(request.getAuthorizationCode())
                .cardLast4(request.getCardLast4())
                .cardType(request.getCardType())
                .bankName(request.getBankName())
                .accountLast4(request.getAccountLast4())
                .cashTendered(request.getCashTendered())
                .changeAmount(changeAmount)
                .cashierId(request.getCashierId())
                .cashierName(request.getCashierName())
                .cashRegisterId(request.getCashRegisterId())
                .shiftId(request.getShiftId())
                .receiptNumber(paymentNumberGenerator.generateReceiptNumber(paymentNumber))
                .receiptPrinted(false)
                .receiptPrintCount(0)
                .refunded(false)
                .referenceNumber(request.getReferenceNumber())
                .notes(request.getNotes())
                .build();

        // Set audit fields
        payment.setCreatedBy(createdBy);
        payment.setUpdatedBy(createdBy);

        // Auto-confirm cash payments
        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setConfirmedDate(LocalDateTime.now());
            payment.setConfirmedBy(createdBy);
        }

        // Save payment
        payment = paymentRepository.save(payment);

        // Update invoice
        updateInvoiceWithPayment(invoice, payment, createdBy);

        log.info("Payment processed successfully: {}", payment.getPaymentNumber());
        return mapToResponse(payment);
    }

    /**
     * Confirm a pending or processing payment.
     *
     * @param paymentId payment ID
     * @param confirmedBy user confirming the payment
     * @return updated payment response
     */
    @Transactional
    public PaymentResponse confirmPayment(UUID paymentId, String confirmedBy) {
        log.info("Confirming payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Only pending or processing payments can be confirmed");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setConfirmedDate(LocalDateTime.now());
        payment.setConfirmedBy(confirmedBy);
        payment.setUpdatedBy(confirmedBy);

        payment = paymentRepository.save(payment);

        // Update invoice if not already updated
        Invoice invoice = payment.getInvoice();
        if (invoice.getPaidAmount() == null ||
            invoice.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            updateInvoiceWithPayment(invoice, payment, confirmedBy);
        }

        log.info("Payment confirmed: {}", payment.getPaymentNumber());
        return mapToResponse(payment);
    }

    /**
     * Cancel a pending payment.
     *
     * @param paymentId payment ID
     * @param cancelledBy user cancelling the payment
     * @return updated payment response
     */
    @Transactional
    public PaymentResponse cancelPayment(UUID paymentId, String cancelledBy) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (!payment.getStatus().isCancellable()) {
            throw new IllegalStateException("Payment cannot be cancelled in current status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedBy(cancelledBy);

        payment = paymentRepository.save(payment);

        log.info("Payment cancelled: {}", payment.getPaymentNumber());
        return mapToResponse(payment);
    }

    /**
     * Process a refund for a completed payment.
     *
     * @param paymentId payment ID
     * @param request refund request
     * @param processedBy user processing the refund
     * @return refund payment response
     */
    @Transactional
    public PaymentResponse processRefund(UUID paymentId, RefundPaymentRequest request, String processedBy) {
        log.info("Processing refund for payment: {}", paymentId);

        Payment originalPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (!originalPayment.getStatus().isRefundable()) {
            throw new IllegalStateException("Payment is not refundable in current status: " + originalPayment.getStatus());
        }

        // Validate refund amount
        BigDecimal maxRefundable = originalPayment.getAmount();
        if (originalPayment.getRefundAmount() != null) {
            maxRefundable = maxRefundable.subtract(originalPayment.getRefundAmount());
        }

        if (request.getRefundAmount().compareTo(maxRefundable) > 0) {
            throw new IllegalArgumentException(
                    String.format("Refund amount (%.2f) exceeds maximum refundable amount (%.2f)",
                            request.getRefundAmount(), maxRefundable)
            );
        }

        // Update original payment
        originalPayment.processRefund(request.getRefundAmount(), request.getReason(), processedBy);
        originalPayment.setUpdatedBy(processedBy);
        paymentRepository.save(originalPayment);

        // Create refund payment record (negative amount)
        String refundPaymentNumber = paymentNumberGenerator.generatePaymentNumber();
        Payment refundPayment = Payment.builder()
                .paymentNumber(refundPaymentNumber)
                .invoice(originalPayment.getInvoice())
                .patientId(originalPayment.getPatientId())
                .patientMrn(originalPayment.getPatientMrn())
                .patientName(originalPayment.getPatientName())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(originalPayment.getPaymentMethod())
                .amount(request.getRefundAmount().negate()) // Negative amount for refund
                .status(PaymentStatus.COMPLETED)
                .cashierId(originalPayment.getCashierId())
                .cashierName(originalPayment.getCashierName())
                .cashRegisterId(originalPayment.getCashRegisterId())
                .shiftId(originalPayment.getShiftId())
                .receiptNumber(paymentNumberGenerator.generateReceiptNumber(refundPaymentNumber))
                .receiptPrinted(false)
                .receiptPrintCount(0)
                .refunded(false)
                .originalPayment(originalPayment)
                .refundReason(request.getReason())
                .refundProcessedBy(processedBy)
                .notes("Refund for payment: " + originalPayment.getPaymentNumber())
                .build();

        refundPayment.setCreatedBy(processedBy);
        refundPayment.setUpdatedBy(processedBy);

        refundPayment = paymentRepository.save(refundPayment);

        // Update invoice
        Invoice invoice = originalPayment.getInvoice();
        BigDecimal currentPaid = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        invoice.setPaidAmount(currentPaid.subtract(request.getRefundAmount()));
        invoice.setOutstandingBalance(invoice.getTotal()
                .subtract(invoice.getPaidAmount())
                .subtract(invoice.getDepositDeduction() != null ? invoice.getDepositDeduction() : BigDecimal.ZERO));
        invoice.setUpdatedBy(processedBy);
        invoiceRepository.save(invoice);

        log.info("Refund processed: {} for original payment: {}",
                refundPayment.getPaymentNumber(), originalPayment.getPaymentNumber());
        return mapToResponse(refundPayment);
    }

    /**
     * Generate cashier shift report.
     *
     * @param shiftId shift ID
     * @return cashier shift report
     */
    @Transactional(readOnly = true)
    public CashierShiftReportResponse generateCashierShiftReport(UUID shiftId) {
        log.info("Generating cashier shift report for shift: {}", shiftId);

        List<Payment> payments = paymentRepository.findByShiftIdOrderByPaymentDate(shiftId);

        if (payments.isEmpty()) {
            throw new IllegalArgumentException("No payments found for shift: " + shiftId);
        }

        // Get shift information from first payment
        Payment firstPayment = payments.get(0);

        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;
        int totalPaymentCount = 0;

        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                if (payment.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
                    totalAmount = totalAmount.add(payment.getAmount());
                    totalPaymentCount++;
                } else {
                    // Negative amount = refund
                    totalRefunds = totalRefunds.add(payment.getAmount().abs());
                }
            }
        }

        BigDecimal netAmount = totalAmount.subtract(totalRefunds);

        // Calculate payment method breakdown
        Map<PaymentMethod, CashierShiftReportResponse.PaymentMethodSummary> breakdown =
                calculatePaymentMethodBreakdown(payments);

        // Build report
        CashierShiftReportResponse report = CashierShiftReportResponse.builder()
                .shiftId(shiftId)
                .cashierId(firstPayment.getCashierId())
                .cashierName(firstPayment.getCashierName())
                .cashRegisterId(firstPayment.getCashRegisterId())
                .shiftStartTime(payments.stream()
                        .map(Payment::getPaymentDate)
                        .min(LocalDateTime::compareTo)
                        .orElse(null))
                .shiftEndTime(payments.stream()
                        .map(Payment::getPaymentDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(null))
                .totalPayments(totalPaymentCount)
                .totalAmount(totalAmount)
                .totalRefunds(totalRefunds)
                .netAmount(netAmount)
                .paymentMethodBreakdown(breakdown)
                .payments(payments.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .build();

        log.info("Cashier shift report generated for shift: {}", shiftId);
        return report;
    }

    /**
     * Get payment by ID.
     *
     * @param paymentId payment ID
     * @return payment response
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        return mapToResponse(payment);
    }

    /**
     * Get payment by payment number.
     *
     * @param paymentNumber payment number
     * @return payment response
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentNumber));
        return mapToResponse(payment);
    }

    /**
     * Get all payments for an invoice.
     *
     * @param invoiceId invoice ID
     * @return list of payment responses
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByInvoiceId(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        return paymentRepository.findByInvoiceOrderByPaymentDateDesc(invoice).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mark payment receipt as printed.
     *
     * @param paymentId payment ID
     * @return updated payment response
     */
    @Transactional
    public PaymentResponse markReceiptAsPrinted(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.setReceiptPrinted(true);
        payment.setReceiptPrintCount(
                payment.getReceiptPrintCount() != null ? payment.getReceiptPrintCount() + 1 : 1
        );
        payment.setLastReceiptPrintDate(LocalDateTime.now());

        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    /**
     * Update invoice with payment information.
     *
     * @param invoice invoice to update
     * @param payment payment
     * @param updatedBy user updating
     */
    private void updateInvoiceWithPayment(Invoice invoice, Payment payment, String updatedBy) {
        BigDecimal currentPaid = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        invoice.setPaidAmount(currentPaid.add(payment.getAmount()));

        BigDecimal depositDeduction = invoice.getDepositDeduction() != null ?
                invoice.getDepositDeduction() : BigDecimal.ZERO;

        invoice.setOutstandingBalance(
                invoice.getTotal()
                        .subtract(invoice.getPaidAmount())
                        .subtract(depositDeduction)
        );

        // Update invoice status based on outstanding balance
        if (invoice.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(com.yudha.hms.billing.constant.InvoiceStatus.PAID);
        } else if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(com.yudha.hms.billing.constant.InvoiceStatus.PARTIALLY_PAID);
        }

        invoice.setUpdatedBy(updatedBy);
        invoiceRepository.save(invoice);
    }

    /**
     * Calculate payment method breakdown for shift report.
     *
     * @param payments list of payments
     * @return payment method breakdown map
     */
    private Map<PaymentMethod, CashierShiftReportResponse.PaymentMethodSummary> calculatePaymentMethodBreakdown(
            List<Payment> payments) {
        Map<PaymentMethod, CashierShiftReportResponse.PaymentMethodSummary> breakdown = new HashMap<>();

        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.COMPLETED &&
                payment.getAmount().compareTo(BigDecimal.ZERO) >= 0) {

                PaymentMethod method = payment.getPaymentMethod();
                CashierShiftReportResponse.PaymentMethodSummary summary = breakdown.get(method);

                if (summary == null) {
                    summary = CashierShiftReportResponse.PaymentMethodSummary.builder()
                            .method(method)
                            .count(0)
                            .amount(BigDecimal.ZERO)
                            .build();
                    breakdown.put(method, summary);
                }

                summary.setCount(summary.getCount() + 1);
                summary.setAmount(summary.getAmount().add(payment.getAmount()));
            }
        }

        return breakdown;
    }

    /**
     * Map payment entity to response DTO.
     *
     * @param payment payment entity
     * @return payment response
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .invoiceId(payment.getInvoice().getId())
                .invoiceNumber(payment.getInvoice().getInvoiceNumber())
                .patientId(payment.getPatientId())
                .patientMrn(payment.getPatientMrn())
                .patientName(payment.getPatientName())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .gatewayName(payment.getGatewayName())
                .authorizationCode(payment.getAuthorizationCode())
                .cardLast4(payment.getCardLast4())
                .cardType(payment.getCardType())
                .bankName(payment.getBankName())
                .accountLast4(payment.getAccountLast4())
                .cashTendered(payment.getCashTendered())
                .changeAmount(payment.getChangeAmount())
                .cashierId(payment.getCashierId())
                .cashierName(payment.getCashierName())
                .cashRegisterId(payment.getCashRegisterId())
                .shiftId(payment.getShiftId())
                .receiptNumber(payment.getReceiptNumber())
                .receiptPrinted(payment.getReceiptPrinted())
                .receiptPrintCount(payment.getReceiptPrintCount())
                .lastReceiptPrintDate(payment.getLastReceiptPrintDate())
                .refunded(payment.getRefunded())
                .refundAmount(payment.getRefundAmount())
                .refundDate(payment.getRefundDate())
                .refundReason(payment.getRefundReason())
                .refundProcessedBy(payment.getRefundProcessedBy())
                .originalPaymentId(payment.getOriginalPayment() != null ?
                        payment.getOriginalPayment().getId() : null)
                .referenceNumber(payment.getReferenceNumber())
                .notes(payment.getNotes())
                .confirmedDate(payment.getConfirmedDate())
                .confirmedBy(payment.getConfirmedBy())
                .createdAt(payment.getCreatedAt())
                .createdBy(payment.getCreatedBy())
                .updatedAt(payment.getUpdatedAt())
                .updatedBy(payment.getUpdatedBy())
                .build();
    }
}
