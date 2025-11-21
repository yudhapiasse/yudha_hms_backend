package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.constant.PaymentMethod;
import com.yudha.hms.billing.constant.PaymentStatus;
import com.yudha.hms.billing.entity.Invoice;
import com.yudha.hms.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Payment Repository.
 *
 * Data access layer for Payment entity with custom query methods.
 * Supports dynamic queries via JpaSpecificationExecutor for advanced search.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    /**
     * Find payment by payment number
     *
     * @param paymentNumber payment number
     * @return optional payment
     */
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    /**
     * Find payments by invoice
     *
     * @param invoice invoice
     * @return list of payments
     */
    List<Payment> findByInvoiceOrderByPaymentDateDesc(Invoice invoice);

    /**
     * Find payments by patient
     *
     * @param patientId patient ID
     * @return list of payments
     */
    List<Payment> findByPatientIdOrderByPaymentDateDesc(UUID patientId);

    /**
     * Find payments by status
     *
     * @param status payment status
     * @return list of payments
     */
    List<Payment> findByStatusOrderByPaymentDateDesc(PaymentStatus status);

    /**
     * Find payments by payment method
     *
     * @param paymentMethod payment method
     * @return list of payments
     */
    List<Payment> findByPaymentMethodOrderByPaymentDateDesc(PaymentMethod paymentMethod);

    /**
     * Find payments by date range
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of payments
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentDate DESC")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find payments by cashier
     *
     * @param cashierId cashier ID
     * @return list of payments
     */
    List<Payment> findByCashierIdOrderByPaymentDateDesc(UUID cashierId);

    /**
     * Find payments by shift
     *
     * @param shiftId shift ID
     * @return list of payments
     */
    List<Payment> findByShiftIdOrderByPaymentDate(UUID shiftId);

    /**
     * Calculate total payments by shift and method
     *
     * @param shiftId shift ID
     * @param paymentMethod payment method
     * @param status payment status
     * @return total amount
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.shiftId = :shiftId " +
           "AND p.paymentMethod = :paymentMethod AND p.status = :status")
    BigDecimal calculateTotalByShiftAndMethod(
        @Param("shiftId") UUID shiftId,
        @Param("paymentMethod") PaymentMethod paymentMethod,
        @Param("status") PaymentStatus status
    );

    /**
     * Check if payment number exists
     *
     * @param paymentNumber payment number
     * @return true if exists
     */
    boolean existsByPaymentNumber(String paymentNumber);

    /**
     * Find pending/processing payments
     *
     * @return list of pending payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status IN ('PENDING', 'PROCESSING') ORDER BY p.paymentDate DESC")
    List<Payment> findPendingPayments();
}
