package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.LabReportResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for LabReportResult entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabReportResultRepository extends JpaRepository<LabReportResult, UUID> {

    /**
     * Find by report
     */
    List<LabReportResult> findByReportIdOrderByDisplayOrder(UUID reportId);

    /**
     * Find by result
     */
    List<LabReportResult> findByResultId(UUID resultId);

    /**
     * Count results in report
     */
    long countByReportId(UUID reportId);
}
