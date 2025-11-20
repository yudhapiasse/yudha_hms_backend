package com.yudha.hms.integration.eklaim.repository;

import com.yudha.hms.integration.eklaim.entity.EklaimAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for E-Klaim Audit Logs.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface EklaimAuditLogRepository extends JpaRepository<EklaimAuditLog, UUID> {

    /**
     * Find all audit logs for a specific claim
     */
    @Query("SELECT l FROM EklaimAuditLog l WHERE l.claim.id = :claimId ORDER BY l.createdAt DESC")
    List<EklaimAuditLog> findByClaimIdOrderByCreatedAtDesc(@Param("claimId") UUID claimId);

    /**
     * Find audit logs by action type
     */
    List<EklaimAuditLog> findByActionOrderByCreatedAtDesc(String action);

    /**
     * Find audit logs by date range
     */
    @Query("SELECT l FROM EklaimAuditLog l WHERE l.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY l.createdAt DESC")
    List<EklaimAuditLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find failed requests (with error messages)
     */
    @Query("SELECT l FROM EklaimAuditLog l WHERE l.errorMessage IS NOT NULL " +
           "ORDER BY l.createdAt DESC")
    List<EklaimAuditLog> findFailedRequests();

    /**
     * Delete old audit logs (for retention policy compliance)
     */
    @Query("DELETE FROM EklaimAuditLog l WHERE l.createdAt < :beforeDate")
    void deleteOlderThan(@Param("beforeDate") LocalDateTime beforeDate);
}
