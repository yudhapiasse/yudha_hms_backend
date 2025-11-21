package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.constant.ClaimStatus;
import com.yudha.hms.billing.constant.ClaimType;
import com.yudha.hms.billing.entity.InsuranceCompany;
import com.yudha.hms.billing.entity.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Insurance Claim Repository.
 *
 * Data access layer for InsuranceClaim entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, UUID>,
        JpaSpecificationExecutor<InsuranceClaim> {

    /**
     * Find claim by claim number
     *
     * @param claimNumber claim number
     * @return optional claim
     */
    Optional<InsuranceClaim> findByClaimNumber(String claimNumber);

    /**
     * Find claims by patient
     *
     * @param patientId patient ID
     * @return list of claims
     */
    List<InsuranceClaim> findByPatientIdOrderBySubmissionDateDesc(UUID patientId);

    /**
     * Find claims by insurance company
     *
     * @param insuranceCompany insurance company
     * @return list of claims
     */
    List<InsuranceClaim> findByInsuranceCompanyOrderBySubmissionDateDesc(InsuranceCompany insuranceCompany);

    /**
     * Find claims by status
     *
     * @param status claim status
     * @return list of claims
     */
    List<InsuranceClaim> findByStatusOrderBySubmissionDateDesc(ClaimStatus status);

    /**
     * Find claims by claim type
     *
     * @param claimType claim type
     * @return list of claims
     */
    List<InsuranceClaim> findByClaimTypeOrderBySubmissionDateDesc(ClaimType claimType);

    /**
     * Find claims by submission date range
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of claims
     */
    @Query("SELECT c FROM InsuranceClaim c WHERE c.submissionDate BETWEEN :startDate AND :endDate " +
           "AND c.deletedAt IS NULL ORDER BY c.submissionDate DESC")
    List<InsuranceClaim> findBySubmissionDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find claims by service date range
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of claims
     */
    @Query("SELECT c FROM InsuranceClaim c WHERE c.serviceStartDate <= :endDate " +
           "AND c.serviceEndDate >= :startDate " +
           "AND c.deletedAt IS NULL ORDER BY c.serviceStartDate DESC")
    List<InsuranceClaim> findByServiceDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find pending claims (submitted or under review)
     *
     * @return list of claims
     */
    @Query("SELECT c FROM InsuranceClaim c WHERE c.status IN ('SUBMITTED', 'UNDER_REVIEW') " +
           "AND c.deletedAt IS NULL ORDER BY c.submissionDate")
    List<InsuranceClaim> findPendingClaims();

    /**
     * Find overdue claims (submitted beyond review period)
     *
     * @param daysOverdue days overdue
     * @return list of claims
     */
    @Query("SELECT c FROM InsuranceClaim c WHERE c.status IN ('SUBMITTED', 'UNDER_REVIEW') " +
           "AND c.submissionDate < :overdueDate " +
           "AND c.deletedAt IS NULL ORDER BY c.submissionDate")
    List<InsuranceClaim> findOverdueClaims(@Param("overdueDate") LocalDateTime overdueDate);

    /**
     * Calculate total claim amount by insurance company and status
     *
     * @param insuranceCompany insurance company
     * @param status claim status
     * @return total amount
     */
    @Query("SELECT SUM(c.claimAmount) FROM InsuranceClaim c " +
           "WHERE c.insuranceCompany = :insuranceCompany " +
           "AND c.status = :status " +
           "AND c.deletedAt IS NULL")
    BigDecimal calculateTotalByCompanyAndStatus(
            @Param("insuranceCompany") InsuranceCompany insuranceCompany,
            @Param("status") ClaimStatus status
    );

    /**
     * Calculate total approved amount by insurance company
     *
     * @param insuranceCompany insurance company
     * @return total approved amount
     */
    @Query("SELECT SUM(c.approvedAmount) FROM InsuranceClaim c " +
           "WHERE c.insuranceCompany = :insuranceCompany " +
           "AND c.status IN ('APPROVED', 'PARTIALLY_APPROVED', 'PAID') " +
           "AND c.deletedAt IS NULL")
    BigDecimal calculateTotalApprovedByCompany(
            @Param("insuranceCompany") InsuranceCompany insuranceCompany
    );

    /**
     * Calculate outstanding amount (approved but not paid)
     *
     * @param insuranceCompany insurance company
     * @return outstanding amount
     */
    @Query("SELECT SUM(c.approvedAmount) FROM InsuranceClaim c " +
           "WHERE c.insuranceCompany = :insuranceCompany " +
           "AND c.status IN ('APPROVED', 'PARTIALLY_APPROVED') " +
           "AND c.deletedAt IS NULL")
    BigDecimal calculateOutstandingByCompany(
            @Param("insuranceCompany") InsuranceCompany insuranceCompany
    );

    /**
     * Find claims by invoice ID
     *
     * @param invoiceId invoice ID
     * @return list of claims
     */
    @Query("SELECT c FROM InsuranceClaim c WHERE c.invoice.id = :invoiceId " +
           "AND c.deletedAt IS NULL ORDER BY c.submissionDate DESC")
    List<InsuranceClaim> findByInvoiceId(@Param("invoiceId") UUID invoiceId);

    /**
     * Check if claim number exists
     *
     * @param claimNumber claim number
     * @return true if exists
     */
    boolean existsByClaimNumber(String claimNumber);

    /**
     * Find rejected claims that can be appealed
     *
     * @return list of claims
     */
    @Query("SELECT c FROM InsuranceClaim c WHERE c.status IN ('REJECTED', 'PARTIALLY_APPROVED') " +
           "AND c.rejectionDate IS NOT NULL " +
           "AND c.appealDate IS NULL " +
           "AND c.deletedAt IS NULL ORDER BY c.rejectionDate DESC")
    List<InsuranceClaim> findAppealableClaims();

    /**
     * Find claims by policy number
     *
     * @param policyNumber policy number
     * @return list of claims
     */
    List<InsuranceClaim> findByPolicyNumberOrderBySubmissionDateDesc(String policyNumber);
}
