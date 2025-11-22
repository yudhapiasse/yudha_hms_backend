package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.constant.reporting.DistributionMethod;
import com.yudha.hms.radiology.constant.reporting.DistributionStatus;
import com.yudha.hms.radiology.entity.reporting.ReportDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportDistributionRepository extends JpaRepository<ReportDistribution, UUID> {

    List<ReportDistribution> findByReportId(UUID reportId);

    List<ReportDistribution> findByDistributionStatus(DistributionStatus status);

    List<ReportDistribution> findByDistributionMethod(DistributionMethod method);

    List<ReportDistribution> findByRecipientId(UUID recipientId);

    @Query("SELECT r FROM ReportDistribution r WHERE r.distributionStatus = :status AND r.distributionMethod = :method")
    List<ReportDistribution> findByStatusAndMethod(
            @Param("status") DistributionStatus status,
            @Param("method") DistributionMethod method
    );

    @Query("SELECT r FROM ReportDistribution r WHERE r.failed = true AND r.retryCount < r.maxRetries")
    List<ReportDistribution> findFailedWithRetryAvailable();

    @Query("SELECT r FROM ReportDistribution r WHERE r.scheduledAt <= :currentTime AND r.distributionStatus = :status")
    List<ReportDistribution> findScheduledDistributions(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("status") DistributionStatus status
    );

    @Query("SELECT r FROM ReportDistribution r WHERE r.reportId = :reportId AND r.distributionStatus = :status")
    List<ReportDistribution> findByReportIdAndStatus(
            @Param("reportId") UUID reportId,
            @Param("status") DistributionStatus status
    );

    @Query("SELECT COUNT(r) FROM ReportDistribution r WHERE r.reportId = :reportId")
    long countByReportId(@Param("reportId") UUID reportId);

    @Query("SELECT COUNT(r) FROM ReportDistribution r WHERE r.distributionStatus = :status")
    long countByStatus(@Param("status") DistributionStatus status);
}
