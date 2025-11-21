package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.constant.AlertSeverity;
import com.yudha.hms.laboratory.constant.AlertType;
import com.yudha.hms.laboratory.entity.CriticalValueAlert;
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
 * Repository for CriticalValueAlert entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface CriticalValueAlertRepository extends JpaRepository<CriticalValueAlert, UUID> {

    /**
     * Find alerts by result
     */
    List<CriticalValueAlert> findByResultIdOrderByCreatedAtDesc(UUID resultId);

    /**
     * Find alerts by patient
     */
    Page<CriticalValueAlert> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable);

    /**
     * Find unacknowledged alerts
     */
    @Query("SELECT a FROM CriticalValueAlert a WHERE a.acknowledged = false ORDER BY a.severity DESC, a.createdAt ASC")
    List<CriticalValueAlert> findUnacknowledgedAlerts();

    /**
     * Find alerts by type
     */
    List<CriticalValueAlert> findByAlertTypeOrderByCreatedAtDesc(AlertType alertType);

    /**
     * Find alerts by severity
     */
    List<CriticalValueAlert> findBySeverityOrderByCreatedAtDesc(AlertSeverity severity);

    /**
     * Find unresolved alerts
     */
    @Query("SELECT a FROM CriticalValueAlert a WHERE a.resolved = false ORDER BY a.createdAt DESC")
    Page<CriticalValueAlert> findUnresolvedAlerts(Pageable pageable);

    /**
     * Find alerts by notified person
     */
    List<CriticalValueAlert> findByNotifiedToOrderByCreatedAtDesc(UUID notifiedTo);

    /**
     * Find alerts in date range
     */
    @Query("SELECT a FROM CriticalValueAlert a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<CriticalValueAlert> findAlertsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count unacknowledged alerts
     */
    long countByAcknowledged(Boolean acknowledged);

    /**
     * Count alerts by type and date range
     */
    @Query("SELECT COUNT(a) FROM CriticalValueAlert a WHERE a.alertType = :alertType AND a.createdAt BETWEEN :startDate AND :endDate")
    long countByTypeAndDateRange(@Param("alertType") AlertType alertType, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
