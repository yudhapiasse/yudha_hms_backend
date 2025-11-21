package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.InvoiceStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Invoice Entity for Hospital Billing System.
 *
 * Represents a billing invoice for a patient encounter/visit.
 * Consolidates all charges from various departments into a single bill.
 *
 * Features:
 * - Automatic invoice numbering
 * - Multiple line items from different services
 * - Discount and tax calculation
 * - Deposit deduction
 * - Payment tracking
 * - Multiple payment methods support
 * - PDF generation support
 * - Void and correction handling
 *
 * Indonesian-specific features:
 * - BPJS claim integration
 * - PPh 23 tax for professional services
 * - Insurance company billing
 * - Corporate agreement billing
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "invoice", schema = "billing_schema", indexes = {
    @Index(name = "idx_invoice_number", columnList = "invoice_number", unique = true),
    @Index(name = "idx_invoice_patient", columnList = "patient_id"),
    @Index(name = "idx_invoice_encounter", columnList = "encounter_id"),
    @Index(name = "idx_invoice_status", columnList = "status"),
    @Index(name = "idx_invoice_date", columnList = "invoice_date"),
    @Index(name = "idx_invoice_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends SoftDeletableEntity {

    // ========================================================================
    // BASIC INFORMATION
    // ========================================================================

    /**
     * Invoice number (unique, auto-generated)
     * Format: INV-YYYYMM-XXXXX (e.g., INV-202501-00001)
     */
    @Column(name = "invoice_number", length = 50, nullable = false, unique = true)
    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;

    /**
     * Patient ID (UUID reference)
     */
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private UUID patientId;

    /**
     * Patient MRN (for quick reference)
     */
    @Column(name = "patient_mrn", length = 50)
    private String patientMrn;

    /**
     * Patient name (for quick reference)
     */
    @Column(name = "patient_name", length = 200)
    private String patientName;

    /**
     * Encounter ID (visit/admission reference)
     * NULL for standalone invoices (e.g., pharmacy retail)
     */
    @Column(name = "encounter_id")
    private UUID encounterId;

    /**
     * Encounter type (OUTPATIENT, INPATIENT, EMERGENCY)
     */
    @Column(name = "encounter_type", length = 50)
    private String encounterType;

    // ========================================================================
    // DATES
    // ========================================================================

    /**
     * Invoice date
     */
    @Column(name = "invoice_date", nullable = false)
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    /**
     * Due date for payment
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * Service period start
     */
    @Column(name = "service_period_start")
    private LocalDate servicePeriodStart;

    /**
     * Service period end
     */
    @Column(name = "service_period_end")
    private LocalDate servicePeriodEnd;

    // ========================================================================
    // STATUS
    // ========================================================================

    /**
     * Invoice status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    @NotNull(message = "Status is required")
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    // ========================================================================
    // LINE ITEMS
    // ========================================================================

    /**
     * Invoice items (charges)
     */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    // ========================================================================
    // AMOUNTS
    // ========================================================================

    /**
     * Subtotal (sum of all item totals before discount and tax)
     */
    @Column(name = "subtotal", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", inclusive = true)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Discount amount
     */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * Discount percentage
     */
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    /**
     * Discount reason/notes
     */
    @Column(name = "discount_reason", length = 500)
    private String discountReason;

    /**
     * Tax amount (PPN if applicable)
     */
    @Column(name = "tax_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /**
     * Tax percentage (typically 11% PPN in Indonesia)
     */
    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    /**
     * Total after discount and tax
     */
    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.0", inclusive = true)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Deposit deduction
     */
    @Column(name = "deposit_deduction", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal depositDeduction = BigDecimal.ZERO;

    /**
     * Amount paid
     */
    @Column(name = "paid_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /**
     * Outstanding balance
     */
    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    // ========================================================================
    // PAYMENT INFORMATION
    // ========================================================================

    /**
     * Payment type (CASH, BPJS, INSURANCE, COMPANY)
     */
    @Column(name = "payment_type", length = 50)
    private String paymentType;

    /**
     * Insurance company ID (for insurance payments)
     */
    @Column(name = "insurance_company_id")
    private UUID insuranceCompanyId;

    /**
     * Insurance claim number
     */
    @Column(name = "insurance_claim_number", length = 100)
    private String insuranceClaimNumber;

    /**
     * BPJS SEP number (for BPJS claims)
     */
    @Column(name = "bpjs_sep_number", length = 100)
    private String bpjsSepNumber;

    /**
     * Company ID (for corporate billing)
     */
    @Column(name = "company_id")
    private UUID companyId;

    // ========================================================================
    // VOID/CORRECTION
    // ========================================================================

    /**
     * Voided flag
     */
    @Column(name = "is_voided")
    @Builder.Default
    private Boolean voided = false;

    /**
     * Void reason
     */
    @Column(name = "void_reason", length = 500)
    private String voidReason;

    /**
     * Voided date
     */
    @Column(name = "voided_date")
    private LocalDateTime voidedDate;

    /**
     * Voided by (user)
     */
    @Column(name = "voided_by", length = 100)
    private String voidedBy;

    /**
     * Replacement invoice ID (if this is a corrected invoice)
     */
    @Column(name = "replacement_invoice_id")
    private UUID replacementInvoiceId;

    /**
     * Original invoice ID (if this is a replacement/correction)
     */
    @Column(name = "original_invoice_id")
    private UUID originalInvoiceId;

    // ========================================================================
    // ADDITIONAL INFORMATION
    // ========================================================================

    /**
     * Payment terms
     */
    @Column(name = "payment_terms", length = 500)
    private String paymentTerms;

    /**
     * Notes
     */
    @Column(name = "notes", length = 2000)
    private String notes;

    /**
     * Printed flag
     */
    @Column(name = "is_printed")
    @Builder.Default
    private Boolean printed = false;

    /**
     * Print count
     */
    @Column(name = "print_count")
    @Builder.Default
    private Integer printCount = 0;

    /**
     * Last printed date
     */
    @Column(name = "last_printed_date")
    private LocalDateTime lastPrintedDate;

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Add item to invoice
     *
     * @param item invoice item
     */
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
        recalculateTotals();
    }

    /**
     * Remove item from invoice
     *
     * @param item invoice item
     */
    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
        recalculateTotals();
    }

    /**
     * Recalculate all totals
     */
    public void recalculateTotals() {
        // Calculate subtotal
        subtotal = items.stream()
            .map(InvoiceItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply discount
        BigDecimal afterDiscount = subtotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);

        // Apply tax
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        total = afterDiscount.add(tax);

        // Calculate outstanding
        BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        BigDecimal deposit = depositDeduction != null ? depositDeduction : BigDecimal.ZERO;
        outstandingBalance = total.subtract(paid).subtract(deposit);

        // Update status based on payment
        if (outstandingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            status = InvoiceStatus.PAID;
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            status = InvoiceStatus.PARTIALLY_PAID;
        }
    }

    /**
     * Mark as printed
     */
    public void markAsPrinted() {
        printed = true;
        printCount = (printCount != null ? printCount : 0) + 1;
        lastPrintedDate = LocalDateTime.now();
    }

    /**
     * Void this invoice
     *
     * @param reason void reason
     * @param voidedByUser user who voided
     */
    public void voidInvoice(String reason, String voidedByUser) {
        voided = true;
        voidReason = reason;
        voidedDate = LocalDateTime.now();
        voidedBy = voidedByUser;
        status = InvoiceStatus.VOID;
    }
}
