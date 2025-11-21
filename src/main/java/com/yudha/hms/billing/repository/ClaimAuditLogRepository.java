package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.constant.ClaimStatus;
import com.yudha.hms.billing.entity.ClaimAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Claim Audit Log Repository.
 *
 * Data access layer for ClaimAuditLog entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface ClaimAuditLogRepository extends JpaRepository<ClaimAuditLog, UUID>,
        JpaSpecificationExecutor<ClaimAuditLog> {

    /**
     * Find audit logs by claim ID
     *
     * @param claimId claim ID
     * @return list of audit logs
     */
    List<ClaimAuditLog> findByClaimIdOrderByTimestampDesc(UUID claimId);

    /**
     * Find audit logs by claim number
     *
     * @param claimNumber claim number
     * @return list of audit logs
     */
    List<ClaimAuditLog> findByClaimNumberOrderByTimestampDesc(String claimNumber);

    /**
     * Find audit logs by action
     *
     * @param action action performed
     * @return list of audit logs
     */
    List<ClaimAuditLog> findByActionOrderByTimestampDesc(String action);

    /**
     * Find audit logs by user
     *
     * @param performedBy user
     * @return list of audit logs
     */
    List<ClaimAuditLog> findByPerformedByOrderByTimestampDesc(String performedBy);

    /**
     * Find audit logs by timestamp range
     *
     * @param startDate start timestamp
     * @param endDate end timestamp
     * @return list of audit logs
     */
    @Query("SELECT a FROM ClaimAuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.timestamp DESC")
    List<ClaimAuditLog> findByTimestampRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find audit logs by status transition
     *
     * @param previousStatus previous status
     * @param newStatus new status
     * @return list of audit logs
     */
    List<ClaimAuditLog> findByPreviousStatusAndNewStatusOrderByTimestampDesc(
            ClaimStatus previousStatus,
            ClaimStatus newStatus
    );

    /**
     * Find recent audit logs
     *
     * @param limit maximum number of logs
     * @return list of audit logs
     */
    @Query(value = "SELECT a FROM ClaimAuditLog a ORDER BY a.timestamp DESC")
    List<ClaimAuditLog> findRecentLogs(@Param("limit") int limit);

    /**
     * Count audit logs by claim ID
     *
     * @param claimId claim ID
     * @return count
     */
    long countByClaimId(UUID claimId);

    /**
     * Count audit logs by action
     *
     * @param action action
     * @return count
     */
    long countByAction(String action);
}
