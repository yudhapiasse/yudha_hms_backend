package com.yudha.hms.billing.controller;

import com.yudha.hms.billing.dto.*;
import com.yudha.hms.billing.service.PaymentReceiptService;
import com.yudha.hms.billing.service.PaymentService;
import com.yudha.hms.shared.dto.ErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for payment operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/v1/billing/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentReceiptService paymentReceiptService;

    /**
     * Create a new payment.
     *
     * @param request payment request
     * @param authentication current user authentication
     * @return created payment response
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication) {
        log.info("Creating payment for invoice: {}", request.getInvoiceId());

        String username = authentication != null ? authentication.getName() : "system";
        PaymentResponse response = paymentService.processPayment(request, username);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get payment by ID.
     *
     * @param id payment ID
     * @return payment response
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        log.info("Getting payment by ID: {}", id);

        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by payment number.
     *
     * @param paymentNumber payment number
     * @return payment response
     */
    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<PaymentResponse> getPaymentByNumber(@PathVariable String paymentNumber) {
        log.info("Getting payment by number: {}", paymentNumber);

        PaymentResponse response = paymentService.getPaymentByNumber(paymentNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments for an invoice.
     *
     * @param invoiceId invoice ID
     * @return list of payment responses
     */
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByInvoice(@PathVariable UUID invoiceId) {
        log.info("Getting payments for invoice: {}", invoiceId);

        List<PaymentResponse> responses = paymentService.getPaymentsByInvoiceId(invoiceId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Confirm a pending or processing payment.
     *
     * @param id payment ID
     * @param authentication current user authentication
     * @return updated payment response
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @PathVariable UUID id,
            Authentication authentication) {
        log.info("Confirming payment: {}", id);

        String username = authentication != null ? authentication.getName() : "system";
        PaymentResponse response = paymentService.confirmPayment(id, username);

        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a pending payment.
     *
     * @param id payment ID
     * @param authentication current user authentication
     * @return updated payment response
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @PathVariable UUID id,
            Authentication authentication) {
        log.info("Cancelling payment: {}", id);

        String username = authentication != null ? authentication.getName() : "system";
        PaymentResponse response = paymentService.cancelPayment(id, username);

        return ResponseEntity.ok(response);
    }

    /**
     * Process a refund for a completed payment.
     *
     * @param id payment ID
     * @param request refund request
     * @param authentication current user authentication
     * @return refund payment response
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> processRefund(
            @PathVariable UUID id,
            @Valid @RequestBody RefundPaymentRequest request,
            Authentication authentication) {
        log.info("Processing refund for payment: {}", id);

        String username = authentication != null ? authentication.getName() : "system";

        // Set processedBy if not provided
        if (request.getProcessedBy() == null) {
            request.setProcessedBy(username);
        }

        PaymentResponse response = paymentService.processRefund(id, request, username);

        return ResponseEntity.ok(response);
    }

    /**
     * Generate cashier shift report.
     *
     * @param shiftId shift ID
     * @return cashier shift report
     */
    @GetMapping("/shift/{shiftId}/report")
    public ResponseEntity<CashierShiftReportResponse> getCashierShiftReport(@PathVariable UUID shiftId) {
        log.info("Generating cashier shift report for shift: {}", shiftId);

        CashierShiftReportResponse response = paymentService.generateCashierShiftReport(shiftId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark payment receipt as printed.
     *
     * @param id payment ID
     * @return updated payment response
     */
    @PostMapping("/{id}/print-receipt")
    public ResponseEntity<PaymentResponse> markReceiptAsPrinted(@PathVariable UUID id) {
        log.info("Marking receipt as printed for payment: {}", id);

        PaymentResponse response = paymentService.markReceiptAsPrinted(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Download payment receipt PDF.
     *
     * @param id payment ID
     * @return receipt PDF bytes
     */
    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable UUID id) {
        log.info("Downloading receipt for payment: {}", id);

        PaymentResponse payment = paymentService.getPaymentById(id);
        byte[] receiptBytes = paymentReceiptService.generateReceiptPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                paymentReceiptService.getReceiptFileName(payment.getReceiptNumber()));

        return new ResponseEntity<>(receiptBytes, headers, HttpStatus.OK);
    }

    /**
     * Exception handler for illegal argument exceptions.
     *
     * @param e exception
     * @return error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Validation error: {}", e.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception handler for illegal state exceptions.
     *
     * @param e exception
     * @return error response
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.warn("Invalid operation: {}", e.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Exception handler for generic exceptions.
     *
     * @param e exception
     * @return error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error processing payment request", e);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred while processing your request")
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
