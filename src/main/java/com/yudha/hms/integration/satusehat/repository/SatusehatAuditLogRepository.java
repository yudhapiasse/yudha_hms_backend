package com.yudha.hms.integration.satusehat.repository;

import com.yudha.hms.integration.satusehat.entity.SatusehatAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for SATUSEHAT Audit Logs.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface SatusehatAuditLogRepository extends JpaRepository<SatusehatAuditLog, UUID> {

    /**
     * Find logs by configuration
     */
    @Query("SELECT l FROM SatusehatAuditLog l WHERE l.config.id = :configId ORDER BY l.createdAt DESC")
    List<SatusehatAuditLog> findByConfigId(@Param("configId") UUID configId);

    /**
     * Find logs by operation type
     */
    List<SatusehatAuditLog> findByOperationType(String operationType);

    /**
     * Find logs by resource type and ID
     */
    List<SatusehatAuditLog> findByResourceTypeAndResourceId(String resourceType, String resourceId);

    /**
     * Find logs by user
     */
    List<SatusehatAuditLog> findByUserId(UUID userId);

    /**
     * Find logs with errors
     */
    @Query("SELECT l FROM SatusehatAuditLog l WHERE l.errorMessage IS NOT NULL ORDER BY l.createdAt DESC")
    List<SatusehatAuditLog> findLogsWithErrors();

    /**
     * Find logs by date range
     */
    @Query("SELECT l FROM SatusehatAuditLog l WHERE l.createdAt BETWEEN :startDate AND :endDate ORDER BY l.createdAt DESC")
    List<SatusehatAuditLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find recent logs (last N records)
     */
    @Query("SELECT l FROM SatusehatAuditLog l ORDER BY l.createdAt DESC LIMIT :limit")
    List<SatusehatAuditLog> findRecentLogs(@Param("limit") int limit);

    /**
     * Count logs by operation type
     */
    long countByOperationType(String operationType);

    /**
     * Count failed requests (with errors)
     */
    @Query("SELECT COUNT(l) FROM SatusehatAuditLog l WHERE l.errorMessage IS NOT NULL")
    long countFailedRequests();
}
