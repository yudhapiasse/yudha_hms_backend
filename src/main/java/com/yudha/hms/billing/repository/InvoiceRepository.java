package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.constant.InvoiceStatus;
import com.yudha.hms.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Invoice Repository.
 *
 * Data access layer for Invoice entity with custom query methods.
 * Supports dynamic queries via JpaSpecificationExecutor for advanced search.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {

    /**
     * Find invoice by invoice number
     *
     * @param invoiceNumber invoice number
     * @return optional invoice
     */
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    /**
     * Find invoices by patient
     *
     * @param patientId patient ID
     * @return list of invoices
     */
    List<Invoice> findByPatientIdOrderByInvoiceDateDesc(UUID patientId);

    /**
     * Find invoices by encounter
     *
     * @param encounterId encounter ID
     * @return list of invoices
     */
    List<Invoice> findByEncounterId(UUID encounterId);

    /**
     * Find invoices by status
     *
     * @param status invoice status
     * @return list of invoices
     */
    List<Invoice> findByStatusOrderByInvoiceDateDesc(InvoiceStatus status);

    /**
     * Find invoices by patient and status
     *
     * @param patientId patient ID
     * @param status invoice status
     * @return list of invoices
     */
    List<Invoice> findByPatientIdAndStatus(UUID patientId, InvoiceStatus status);

    /**
     * Find invoices by date range
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate ORDER BY i.invoiceDate DESC")
    List<Invoice> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find overdue invoices
     *
     * @param currentDate current date
     * @return list of overdue invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentDate AND i.status IN ('ISSUED', 'PARTIALLY_PAID') ORDER BY i.dueDate")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDate currentDate);

    /**
     * Find outstanding invoices (not fully paid)
     *
     * @return list of outstanding invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('ISSUED', 'PARTIALLY_PAID', 'OVERDUE') ORDER BY i.invoiceDate DESC")
    List<Invoice> findOutstandingInvoices();

    /**
     * Calculate total outstanding amount
     *
     * @return total outstanding amount
     */
    @Query("SELECT SUM(i.outstandingBalance) FROM Invoice i WHERE i.status IN ('ISSUED', 'PARTIALLY_PAID', 'OVERDUE')")
    BigDecimal calculateTotalOutstanding();

    /**
     * Check if invoice number exists
     *
     * @param invoiceNumber invoice number
     * @return true if exists
     */
    boolean existsByInvoiceNumber(String invoiceNumber);

    /**
     * Find invoices by patient MRN
     *
     * @param patientMrn patient MRN
     * @return list of invoices
     */
    List<Invoice> findByPatientMrnOrderByInvoiceDateDesc(String patientMrn);
}
