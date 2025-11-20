package com.yudha.hms.integration.eklaim.repository;

import com.yudha.hms.integration.eklaim.entity.EklaimClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for E-Klaim Claims.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface EklaimClaimRepository extends JpaRepository<EklaimClaim, UUID> {

    /**
     * Find claim by claim number
     */
    Optional<EklaimClaim> findByClaimNumber(String claimNumber);

    /**
     * Find claim by SEP number
     */
    Optional<EklaimClaim> findByNomorSep(String nomorSep);

    /**
     * Find all claims by status
     */
    List<EklaimClaim> findByStatus(Integer status);

    /**
     * Find claims by status and submission date range
     */
    @Query("SELECT c FROM EklaimClaim c WHERE c.status = :status " +
           "AND c.submissionDate BETWEEN :startDate AND :endDate")
    List<EklaimClaim> findByStatusAndSubmissionDateBetween(
        @Param("status") Integer status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find claims by patient ID
     */
    @Query("SELECT c FROM EklaimClaim c WHERE c.patientId = :patientId")
    List<EklaimClaim> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find claims by encounter ID
     */
    @Query("SELECT c FROM EklaimClaim c WHERE c.encounterId = :encounterId")
    Optional<EklaimClaim> findByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find draft claims (status = 1) older than specified date
     */
    @Query("SELECT c FROM EklaimClaim c WHERE c.status = 1 AND c.createdAt < :beforeDate")
    List<EklaimClaim> findDraftClaimsOlderThan(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Find claims pending verification (status = 6)
     */
    @Query("SELECT c FROM EklaimClaim c WHERE c.status = 6")
    List<EklaimClaim> findPendingVerification();

    /**
     * Find claims by BA number (payment batch)
     */
    List<EklaimClaim> findByBaNumber(String baNumber);

    /**
     * Check if SEP is already used
     */
    boolean existsByNomorSep(String nomorSep);

    /**
     * Count claims by status
     */
    long countByStatus(Integer status);

    /**
     * Find TB cases (SITB integration)
     */
    @Query("SELECT c FROM EklaimClaim c WHERE c.isTbCase = true AND c.sitbCompleted = false")
    List<EklaimClaim> findPendingSitbCases();
}
