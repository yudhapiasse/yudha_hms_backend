package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.EncounterAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Encounter Audit Log Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface EncounterAuditLogRepository extends JpaRepository<EncounterAuditLog, UUID> {

    /**
     * Find all audit logs for a specific encounter.
     */
    List<EncounterAuditLog> findByEncounterIdOrderByTimestampDesc(UUID encounterId);

    /**
     * Find audit logs for an encounter with pagination.
     */
    Page<EncounterAuditLog> findByEncounterIdOrderByTimestampDesc(UUID encounterId, Pageable pageable);

    /**
     * Find audit logs by user.
     */
    List<EncounterAuditLog> findByUserIdOrderByTimestampDesc(UUID userId);

    /**
     * Find audit logs by patient.
     */
    List<EncounterAuditLog> findByPatientIdOrderByTimestampDesc(UUID patientId);

    /**
     * Find audit logs by action type.
     */
    List<EncounterAuditLog> findByActionTypeOrderByTimestampDesc(
        EncounterAuditLog.AuditActionType actionType
    );

    /**
     * Find sensitive access logs.
     */
    @Query("SELECT a FROM EncounterAuditLog a WHERE a.isSensitiveAccess = true " +
           "ORDER BY a.timestamp DESC")
    List<EncounterAuditLog> findSensitiveAccessLogs();

    /**
     * Find supervisor override logs.
     */
    @Query("SELECT a FROM EncounterAuditLog a WHERE a.supervisorOverride = true " +
           "ORDER BY a.timestamp DESC")
    List<EncounterAuditLog> findSupervisorOverrideLogs();

    /**
     * Find audit logs within date range.
     */
    @Query("SELECT a FROM EncounterAuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.timestamp DESC")
    List<EncounterAuditLog> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find audit logs by encounter and action type.
     */
    List<EncounterAuditLog> findByEncounterIdAndActionTypeOrderByTimestampDesc(
        UUID encounterId,
        EncounterAuditLog.AuditActionType actionType
    );

    /**
     * Count audit logs for encounter.
     */
    long countByEncounterId(UUID encounterId);

    /**
     * Count sensitive access logs for date range.
     */
    @Query("SELECT COUNT(a) FROM EncounterAuditLog a WHERE a.isSensitiveAccess = true " +
           "AND a.timestamp BETWEEN :startDate AND :endDate")
    long countSensitiveAccessInDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
