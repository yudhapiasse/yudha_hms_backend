package com.yudha.hms.billing.controller;

import com.yudha.hms.billing.dto.*;
import com.yudha.hms.billing.service.InvoiceService;
import com.yudha.hms.billing.service.InvoicePdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Invoice Management.
 *
 * Endpoints:
 * - POST /api/v1/billing/invoices - Create invoice
 * - GET /api/v1/billing/invoices/{id} - Get invoice by ID
 * - GET /api/v1/billing/invoices/number/{invoiceNumber} - Get invoice by number
 * - GET /api/v1/billing/invoices/patient/{patientId} - Get patient invoices
 * - PUT /api/v1/billing/invoices/{id} - Update invoice
 * - POST /api/v1/billing/invoices/{id}/issue - Issue invoice
 * - POST /api/v1/billing/invoices/{id}/void - Void invoice
 * - POST /api/v1/billing/invoices/{id}/discount - Apply discount
 * - POST /api/v1/billing/invoices/{id}/print - Mark as printed
 * - GET /api/v1/billing/invoices/{id}/pdf - Download invoice PDF
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/v1/billing/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;

    /**
     * Create a new invoice.
     *
     * @param request create invoice request
     * @return created invoice
     */
    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
        @Valid @RequestBody CreateInvoiceRequest request,
        @RequestParam(required = false, defaultValue = "system") String createdBy
    ) {
        log.info("POST /api/v1/billing/invoices - Creating invoice for patient: {}", request.getPatientId());

        InvoiceResponse response = invoiceService.createInvoice(request, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get invoice by ID.
     *
     * @param id invoice ID
     * @return invoice
     */
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable UUID id) {
        log.info("GET /api/v1/billing/invoices/{} - Getting invoice", id);

        InvoiceResponse response = invoiceService.getInvoiceById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Get invoice by invoice number.
     *
     * @param invoiceNumber invoice number
     * @return invoice
     */
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        log.info("GET /api/v1/billing/invoices/number/{} - Getting invoice by number", invoiceNumber);

        InvoiceResponse response = invoiceService.getInvoiceByNumber(invoiceNumber);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all invoices for a patient.
     *
     * @param patientId patient ID
     * @return list of invoices
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByPatient(@PathVariable UUID patientId) {
        log.info("GET /api/v1/billing/invoices/patient/{} - Getting patient invoices", patientId);

        List<InvoiceResponse> responses = invoiceService.getInvoicesByPatient(patientId);

        return ResponseEntity.ok(responses);
    }

    /**
     * Update an existing invoice.
     *
     * @param id invoice ID
     * @param request update request
     * @return updated invoice
     */
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> updateInvoice(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateInvoiceRequest request,
        @RequestParam(required = false, defaultValue = "system") String updatedBy
    ) {
        log.info("PUT /api/v1/billing/invoices/{} - Updating invoice", id);

        InvoiceResponse response = invoiceService.updateInvoice(id, request, updatedBy);

        return ResponseEntity.ok(response);
    }

    /**
     * Issue an invoice (change status from DRAFT to ISSUED).
     *
     * @param id invoice ID
     * @return issued invoice
     */
    @PostMapping("/{id}/issue")
    public ResponseEntity<InvoiceResponse> issueInvoice(
        @PathVariable UUID id,
        @RequestParam(required = false, defaultValue = "system") String issuedBy
    ) {
        log.info("POST /api/v1/billing/invoices/{}/issue - Issuing invoice", id);

        InvoiceResponse response = invoiceService.issueInvoice(id, issuedBy);

        return ResponseEntity.ok(response);
    }

    /**
     * Void an invoice.
     *
     * @param id invoice ID
     * @param request void request
     * @return voided invoice
     */
    @PostMapping("/{id}/void")
    public ResponseEntity<InvoiceResponse> voidInvoice(
        @PathVariable UUID id,
        @Valid @RequestBody VoidInvoiceRequest request,
        @RequestParam(required = false, defaultValue = "system") String voidedBy
    ) {
        log.info("POST /api/v1/billing/invoices/{}/void - Voiding invoice", id);

        InvoiceResponse response = invoiceService.voidInvoice(id, request, voidedBy);

        return ResponseEntity.ok(response);
    }

    /**
     * Apply discount to invoice.
     *
     * @param id invoice ID
     * @param discountAmount discount amount (optional if using percentage)
     * @param discountPercentage discount percentage (optional if using amount)
     * @param reason discount reason
     * @return updated invoice
     */
    @PostMapping("/{id}/discount")
    public ResponseEntity<InvoiceResponse> applyDiscount(
        @PathVariable UUID id,
        @RequestParam(required = false) BigDecimal discountAmount,
        @RequestParam(required = false) BigDecimal discountPercentage,
        @RequestParam(required = false) String reason,
        @RequestParam(required = false, defaultValue = "system") String appliedBy
    ) {
        log.info("POST /api/v1/billing/invoices/{}/discount - Applying discount", id);

        if (discountAmount == null && discountPercentage == null) {
            return ResponseEntity.badRequest().build();
        }

        InvoiceResponse response = invoiceService.applyDiscount(
            id, discountAmount, discountPercentage, reason, appliedBy
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Mark invoice as printed.
     *
     * @param id invoice ID
     * @return success message
     */
    @PostMapping("/{id}/print")
    public ResponseEntity<String> markAsPrinted(@PathVariable UUID id) {
        log.info("POST /api/v1/billing/invoices/{}/print - Marking invoice as printed", id);

        invoiceService.markAsPrinted(id);

        return ResponseEntity.ok("Invoice marked as printed");
    }

    /**
     * Download invoice as PDF.
     *
     * @param id invoice ID
     * @return PDF file
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable UUID id) {
        log.info("GET /api/v1/billing/invoices/{}/pdf - Downloading invoice PDF", id);

        // Get invoice to retrieve invoice number
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);

        // Generate PDF
        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(id);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
            "attachment",
            invoicePdfService.getInvoicePdfFileName(invoice.getInvoiceNumber())
        );
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
