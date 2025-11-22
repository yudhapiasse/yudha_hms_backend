package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.constant.reporting.AmendmentType;
import com.yudha.hms.radiology.entity.reporting.ReportAmendment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportAmendmentRepository extends JpaRepository<ReportAmendment, UUID> {

    List<ReportAmendment> findByReportId(UUID reportId);

    List<ReportAmendment> findByReportIdOrderByAmendmentNumberAsc(UUID reportId);

    List<ReportAmendment> findByAmendmentType(AmendmentType amendmentType);

    @Query("SELECT r FROM ReportAmendment r WHERE r.reportId = :reportId AND r.amendmentType = :type")
    List<ReportAmendment> findByReportIdAndAmendmentType(
            @Param("reportId") UUID reportId,
            @Param("type") AmendmentType type
    );

    @Query("SELECT r FROM ReportAmendment r WHERE r.amendedBy = :userId AND CAST(r.amendedAt AS date) BETWEEN :startDate AND :endDate")
    List<ReportAmendment> findByAmendedByAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(r) FROM ReportAmendment r WHERE r.reportId = :reportId")
    long countByReportId(@Param("reportId") UUID reportId);
}
