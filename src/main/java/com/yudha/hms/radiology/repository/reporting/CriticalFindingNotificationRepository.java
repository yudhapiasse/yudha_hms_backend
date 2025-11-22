package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.constant.reporting.FindingSeverity;
import com.yudha.hms.radiology.constant.reporting.NotificationPriority;
import com.yudha.hms.radiology.entity.reporting.CriticalFindingNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CriticalFindingNotificationRepository extends JpaRepository<CriticalFindingNotification, UUID> {

    List<CriticalFindingNotification> findByReportId(UUID reportId);

    List<CriticalFindingNotification> findByAcknowledgedFalse();

    List<CriticalFindingNotification> findByFindingSeverity(FindingSeverity severity);

    List<CriticalFindingNotification> findByPriority(NotificationPriority priority);

    @Query("SELECT c FROM CriticalFindingNotification c WHERE c.acknowledged = false AND c.priority = :priority")
    List<CriticalFindingNotification> findUnacknowledgedByPriority(@Param("priority") NotificationPriority priority);

    @Query("SELECT c FROM CriticalFindingNotification c WHERE c.notifiedAt BETWEEN :startDate AND :endDate")
    List<CriticalFindingNotification> findByNotificationDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT c FROM CriticalFindingNotification c WHERE c.acknowledged = false AND c.requiresImmediateAction = true")
    List<CriticalFindingNotification> findUnacknowledgedRequiringImmediateAction();

    @Query("SELECT COUNT(c) FROM CriticalFindingNotification c WHERE c.reportId = :reportId")
    long countByReportId(@Param("reportId") UUID reportId);

    @Query("SELECT COUNT(c) FROM CriticalFindingNotification c WHERE c.acknowledged = false")
    long countUnacknowledged();
}
