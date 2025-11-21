package com.yudha.hms.billing.service;

import com.yudha.hms.billing.constant.InvoiceStatus;
import com.yudha.hms.billing.dto.*;
import com.yudha.hms.billing.entity.Invoice;
import com.yudha.hms.billing.entity.InvoiceItem;
import com.yudha.hms.billing.entity.Tariff;
import com.yudha.hms.billing.repository.InvoiceRepository;
import com.yudha.hms.billing.repository.TariffRepository;
import com.yudha.hms.billing.util.InvoiceNumberGenerator;
import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.patient.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Invoice Service for managing billing invoices.
 *
 * Features:
 * - Invoice generation with automatic charge compilation
 * - Discount and tax calculation
 * - Deposit deduction
 * - Invoice void and correction
 * - Payment tracking
 * - Invoice numbering
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TariffRepository tariffRepository;
    private final PatientRepository patientRepository;
    private final InvoiceNumberGenerator invoiceNumberGenerator;

    /**
     * Create a new invoice.
     *
     * @param request create invoice request
     * @param createdBy user creating the invoice
     * @return created invoice response
     */
    public InvoiceResponse createInvoice(CreateInvoiceRequest request, String createdBy) {
        log.info("Creating invoice for patient: {}", request.getPatientId());

        // Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new EntityNotFoundException("Patient not found: " + request.getPatientId()));

        // Generate invoice number
        String invoiceNumber = invoiceNumberGenerator.generateInvoiceNumber();

        // Create invoice entity
        Invoice invoice = Invoice.builder()
            .invoiceNumber(invoiceNumber)
            .patientId(patient.getId())
            .patientMrn(patient.getMrn())
            .patientName(patient.getFullName())
            .encounterId(request.getEncounterId())
            .encounterType(request.getEncounterType())
            .invoiceDate(request.getInvoiceDate())
            .dueDate(request.getDueDate())
            .servicePeriodStart(request.getServicePeriodStart())
            .servicePeriodEnd(request.getServicePeriodEnd())
            .status(InvoiceStatus.DRAFT)
            .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
            .discountPercentage(request.getDiscountPercentage())
            .discountReason(request.getDiscountReason())
            .taxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO)
            .taxPercentage(request.getTaxPercentage())
            .depositDeduction(request.getDepositDeduction() != null ? request.getDepositDeduction() : BigDecimal.ZERO)
            .paymentType(request.getPaymentType())
            .insuranceCompanyId(request.getInsuranceCompanyId())
            .insuranceClaimNumber(request.getInsuranceClaimNumber())
            .bpjsSepNumber(request.getBpjsSepNumber())
            .companyId(request.getCompanyId())
            .paymentTerms(request.getPaymentTerms())
            .notes(request.getNotes())
            .build();

        // Set audit fields
        invoice.setCreatedBy(createdBy);
        invoice.setUpdatedBy(createdBy);

        // Add invoice items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            int lineNumber = 1;
            for (InvoiceItemRequest itemRequest : request.getItems()) {
                InvoiceItem item = createInvoiceItem(invoice, itemRequest, lineNumber++);
                invoice.addItem(item);
            }
        }

        // Calculate totals
        invoice.recalculateTotals();

        // Save invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice created successfully: {}", savedInvoice.getInvoiceNumber());
        return mapToResponse(savedInvoice);
    }

    /**
     * Update an existing invoice.
     *
     * @param invoiceId invoice ID
     * @param request update request
     * @param updatedBy user updating the invoice
     * @return updated invoice response
     */
    public InvoiceResponse updateInvoice(UUID invoiceId, UpdateInvoiceRequest request, String updatedBy) {
        log.info("Updating invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));

        // Check if invoice is editable
        if (!invoice.getStatus().isEditable() && !invoice.getStatus().canAcceptPayment()) {
            throw new IllegalStateException("Invoice cannot be modified in status: " + invoice.getStatus());
        }

        // Update fields
        if (request.getDueDate() != null) {
            invoice.setDueDate(request.getDueDate());
        }

        if (request.getStatus() != null) {
            invoice.setStatus(request.getStatus());
        }

        if (request.getDiscountAmount() != null) {
            invoice.setDiscountAmount(request.getDiscountAmount());
        }

        if (request.getDiscountPercentage() != null) {
            invoice.setDiscountPercentage(request.getDiscountPercentage());
        }

        if (request.getDiscountReason() != null) {
            invoice.setDiscountReason(request.getDiscountReason());
        }

        if (request.getTaxAmount() != null) {
            invoice.setTaxAmount(request.getTaxAmount());
        }

        if (request.getTaxPercentage() != null) {
            invoice.setTaxPercentage(request.getTaxPercentage());
        }

        if (request.getDepositDeduction() != null) {
            invoice.setDepositDeduction(request.getDepositDeduction());
        }

        if (request.getPaymentType() != null) {
            invoice.setPaymentType(request.getPaymentType());
        }

        if (request.getInsuranceCompanyId() != null) {
            invoice.setInsuranceCompanyId(request.getInsuranceCompanyId());
        }

        if (request.getInsuranceClaimNumber() != null) {
            invoice.setInsuranceClaimNumber(request.getInsuranceClaimNumber());
        }

        if (request.getBpjsSepNumber() != null) {
            invoice.setBpjsSepNumber(request.getBpjsSepNumber());
        }

        if (request.getCompanyId() != null) {
            invoice.setCompanyId(request.getCompanyId());
        }

        if (request.getPaymentTerms() != null) {
            invoice.setPaymentTerms(request.getPaymentTerms());
        }

        if (request.getNotes() != null) {
            invoice.setNotes(request.getNotes());
        }

        // Update items if provided
        if (request.getItems() != null) {
            // Clear existing items
            invoice.getItems().clear();

            // Add new items
            int lineNumber = 1;
            for (InvoiceItemRequest itemRequest : request.getItems()) {
                InvoiceItem item = createInvoiceItem(invoice, itemRequest, lineNumber++);
                invoice.addItem(item);
            }
        }

        // Recalculate totals
        invoice.recalculateTotals();

        invoice.setUpdatedBy(updatedBy);

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice updated successfully: {}", updatedInvoice.getInvoiceNumber());
        return mapToResponse(updatedInvoice);
    }

    /**
     * Get invoice by ID.
     *
     * @param invoiceId invoice ID
     * @return invoice response
     */
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(UUID invoiceId) {
        log.debug("Getting invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));

        return mapToResponse(invoice);
    }

    /**
     * Get invoice by invoice number.
     *
     * @param invoiceNumber invoice number
     * @return invoice response
     */
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        log.debug("Getting invoice by number: {}", invoiceNumber);

        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceNumber));

        return mapToResponse(invoice);
    }

    /**
     * Get all invoices for a patient.
     *
     * @param patientId patient ID
     * @return list of invoice responses
     */
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByPatient(UUID patientId) {
        log.debug("Getting invoices for patient: {}", patientId);

        List<Invoice> invoices = invoiceRepository.findByPatientIdOrderByInvoiceDateDesc(patientId);
        return invoices.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Void an invoice.
     *
     * @param invoiceId invoice ID
     * @param request void request
     * @param voidedBy user voiding the invoice
     * @return voided invoice response
     */
    public InvoiceResponse voidInvoice(UUID invoiceId, VoidInvoiceRequest request, String voidedBy) {
        log.info("Voiding invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));

        // Check if invoice can be voided
        if (invoice.getStatus().isFinal()) {
            throw new IllegalStateException("Cannot void invoice in final status: " + invoice.getStatus());
        }

        // Void the invoice
        invoice.voidInvoice(request.getReason(), voidedBy);

        Invoice voidedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice voided successfully: {}", voidedInvoice.getInvoiceNumber());
        return mapToResponse(voidedInvoice);
    }

    /**
     * Issue an invoice (change status from DRAFT to ISSUED).
     *
     * @param invoiceId invoice ID
     * @param issuedBy user issuing the invoice
     * @return issued invoice response
     */
    public InvoiceResponse issueInvoice(UUID invoiceId, String issuedBy) {
        log.info("Issuing invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT invoices can be issued");
        }

        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setUpdatedBy(issuedBy);

        Invoice issuedInvoice = invoiceRepository.save(invoice);

        log.info("Invoice issued successfully: {}", issuedInvoice.getInvoiceNumber());
        return mapToResponse(issuedInvoice);
    }

    /**
     * Mark invoice as printed.
     *
     * @param invoiceId invoice ID
     */
    public void markAsPrinted(UUID invoiceId) {
        log.debug("Marking invoice as printed: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));

        invoice.markAsPrinted();
        invoiceRepository.save(invoice);
    }

    /**
     * Apply discount to invoice.
     *
     * @param invoiceId invoice ID
     * @param discountAmount discount amount (can be null if using percentage)
     * @param discountPercentage discount percentage (can be null if using amount)
     * @param reason discount reason
     * @param appliedBy user applying discount
     * @return updated invoice
     */
    public InvoiceResponse applyDiscount(UUID invoiceId, BigDecimal discountAmount,
                                        BigDecimal discountPercentage, String reason, String appliedBy) {
        log.info("Applying discount to invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));

        if (!invoice.getStatus().isEditable() && !invoice.getStatus().canAcceptPayment()) {
            throw new IllegalStateException("Cannot apply discount to invoice in status: " + invoice.getStatus());
        }

        if (discountAmount != null) {
            invoice.setDiscountAmount(discountAmount);
        } else if (discountPercentage != null) {
            // Calculate discount amount from percentage
            BigDecimal calculatedDiscount = invoice.getSubtotal()
                .multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100));
            invoice.setDiscountAmount(calculatedDiscount);
            invoice.setDiscountPercentage(discountPercentage);
        }

        invoice.setDiscountReason(reason);
        invoice.setUpdatedBy(appliedBy);

        invoice.recalculateTotals();

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return mapToResponse(updatedInvoice);
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Create invoice item from request.
     */
    private InvoiceItem createInvoiceItem(Invoice invoice, InvoiceItemRequest request, int lineNumber) {
        // Fetch tariff if provided
        Tariff tariff = null;
        if (request.getTariffId() != null) {
            tariff = tariffRepository.findById(request.getTariffId())
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found: " + request.getTariffId()));
        }

        InvoiceItem item = InvoiceItem.builder()
            .invoice(invoice)
            .lineNumber(lineNumber)
            .serviceDate(request.getServiceDate())
            .tariff(tariff)
            .itemType(request.getItemType() != null ? request.getItemType() :
                     (tariff != null ? tariff.getTariffType() : null))
            .itemCode(request.getItemCode() != null ? request.getItemCode() :
                     (tariff != null ? tariff.getCode() : null))
            .itemName(request.getItemName())
            .itemDescription(request.getItemDescription())
            .quantity(request.getQuantity())
            .unit(request.getUnit() != null ? request.getUnit() :
                 (tariff != null ? tariff.getUnit() : null))
            .unitPrice(request.getUnitPrice())
            .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
            .discountPercentage(request.getDiscountPercentage())
            .departmentId(request.getDepartmentId())
            .departmentName(request.getDepartmentName())
            .practitionerId(request.getPractitionerId())
            .practitionerName(request.getPractitionerName())
            .sourceReferenceId(request.getSourceReferenceId())
            .sourceReferenceType(request.getSourceReferenceType())
            .covered(request.getCovered() != null ? request.getCovered() : false)
            .coveragePercentage(request.getCoveragePercentage())
            .notes(request.getNotes())
            .build();

        // Set package deal separately if needed (it's a reference, not directly settable in builder)
        if (request.getPackageDealId() != null) {
            // Note: Would need PackageDealRepository to fetch the entity
            // For now, just set the field directly
            // item.setPackageDeal(...);
        }

        return item;
    }

    /**
     * Map Invoice entity to InvoiceResponse DTO.
     */
    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceResponse.InvoiceItemResponse> itemResponses = invoice.getItems().stream()
            .map(this::mapItemToResponse)
            .collect(Collectors.toList());

        return InvoiceResponse.builder()
            .id(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .patientId(invoice.getPatientId())
            .patientMrn(invoice.getPatientMrn())
            .patientName(invoice.getPatientName())
            .encounterId(invoice.getEncounterId())
            .encounterType(invoice.getEncounterType())
            .invoiceDate(invoice.getInvoiceDate())
            .dueDate(invoice.getDueDate())
            .servicePeriodStart(invoice.getServicePeriodStart())
            .servicePeriodEnd(invoice.getServicePeriodEnd())
            .status(invoice.getStatus())
            .subtotal(invoice.getSubtotal())
            .discountAmount(invoice.getDiscountAmount())
            .discountPercentage(invoice.getDiscountPercentage())
            .discountReason(invoice.getDiscountReason())
            .taxAmount(invoice.getTaxAmount())
            .taxPercentage(invoice.getTaxPercentage())
            .total(invoice.getTotal())
            .depositDeduction(invoice.getDepositDeduction())
            .paidAmount(invoice.getPaidAmount())
            .outstandingBalance(invoice.getOutstandingBalance())
            .paymentType(invoice.getPaymentType())
            .insuranceCompanyId(invoice.getInsuranceCompanyId())
            .insuranceClaimNumber(invoice.getInsuranceClaimNumber())
            .bpjsSepNumber(invoice.getBpjsSepNumber())
            .companyId(invoice.getCompanyId())
            .paymentTerms(invoice.getPaymentTerms())
            .notes(invoice.getNotes())
            .printed(invoice.getPrinted())
            .printCount(invoice.getPrintCount())
            .lastPrintedDate(invoice.getLastPrintedDate())
            .items(itemResponses)
            .createdAt(invoice.getCreatedAt())
            .createdBy(invoice.getCreatedBy())
            .updatedAt(invoice.getUpdatedAt())
            .updatedBy(invoice.getUpdatedBy())
            .build();
    }

    /**
     * Map InvoiceItem entity to ItemResponse DTO.
     */
    private InvoiceResponse.InvoiceItemResponse mapItemToResponse(InvoiceItem item) {
        return InvoiceResponse.InvoiceItemResponse.builder()
            .id(item.getId())
            .lineNumber(item.getLineNumber())
            .serviceDate(item.getServiceDate())
            .tariffId(item.getTariff() != null ? item.getTariff().getId() : null)
            .itemType(item.getItemType())
            .itemCode(item.getItemCode())
            .itemName(item.getItemName())
            .itemDescription(item.getItemDescription())
            .quantity(item.getQuantity())
            .unit(item.getUnit())
            .unitPrice(item.getUnitPrice())
            .totalPrice(item.getTotalPrice())
            .discountAmount(item.getDiscountAmount())
            .discountPercentage(item.getDiscountPercentage())
            .netAmount(item.getNetAmount())
            .taxAmount(item.getTaxAmount())
            .departmentId(item.getDepartmentId())
            .departmentName(item.getDepartmentName())
            .practitionerId(item.getPractitionerId())
            .practitionerName(item.getPractitionerName())
            .sourceReferenceId(item.getSourceReferenceId())
            .sourceReferenceType(item.getSourceReferenceType())
            .packageDealId(item.getPackageDeal() != null ? item.getPackageDeal().getId() : null)
            .covered(item.getCovered())
            .coveragePercentage(item.getCoveragePercentage())
            .coveredAmount(item.getCoveredAmount())
            .patientResponsibility(item.getPatientResponsibility())
            .notes(item.getNotes())
            .build();
    }
}
