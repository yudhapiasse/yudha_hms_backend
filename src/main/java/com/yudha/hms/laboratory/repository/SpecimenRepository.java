package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.constant.QualityStatus;
import com.yudha.hms.laboratory.constant.SpecimenStatus;
import com.yudha.hms.laboratory.entity.Specimen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Specimen entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface SpecimenRepository extends JpaRepository<Specimen, UUID> {

    /**
     * Find by specimen number
     */
    Optional<Specimen> findBySpecimenNumber(String specimenNumber);

    /**
     * Find by barcode
     */
    Optional<Specimen> findByBarcode(String barcode);

    /**
     * Find by order
     */
    List<Specimen> findByOrderId(UUID orderId);

    /**
     * Find by order item
     */
    Optional<Specimen> findByOrderItemId(UUID orderItemId);

    /**
     * Find by status (paginated)
     */
    Page<Specimen> findByStatusOrderByCollectedAtDesc(SpecimenStatus status, Pageable pageable);

    /**
     * Find by status (list)
     */
    List<Specimen> findByStatus(SpecimenStatus status);

    /**
     * Find by status and collection date range
     */
    List<Specimen> findByStatusAndCollectedAtBetween(SpecimenStatus status, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find by collection date range
     */
    List<Specimen> findByCollectedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find by quality status (single)
     */
    List<Specimen> findByQualityStatus(QualityStatus qualityStatus);

    /**
     * Find by quality status (multiple)
     */
    List<Specimen> findByQualityStatusIn(List<QualityStatus> qualityStatuses);

    /**
     * Find specimens awaiting collection
     */
    @Query("SELECT s FROM Specimen s WHERE s.status = 'PENDING' AND s.order.collectionScheduledAt IS NOT NULL AND s.order.collectionScheduledAt <= :now")
    List<Specimen> findSpecimensAwaitingCollection(@Param("now") LocalDateTime now);

    /**
     * Find rejected specimens
     */
    @Query("SELECT s FROM Specimen s WHERE s.qualityStatus = 'REJECTED' AND s.collectedAt BETWEEN :startDate AND :endDate")
    List<Specimen> findRejectedSpecimens(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find specimens with pre-analytical issues
     */
    @Query("SELECT s FROM Specimen s WHERE (s.volumeAdequate = false OR s.containerAppropriate = false OR s.labelingCorrect = false OR s.temperatureAppropriate = false OR s.hemolysisDetected = true OR s.lipemiaDetected = true OR s.icterusDetected = true) AND s.status NOT IN ('REJECTED', 'DISPOSED')")
    List<Specimen> findSpecimensWithPreAnalyticalIssues();

    /**
     * Find specimens with quality issues (alternative method)
     */
    @Query("SELECT s FROM Specimen s WHERE s.qualityStatus IN :statuses ORDER BY s.collectedAt DESC")
    List<Specimen> findSpecimensWithQualityIssues(@Param("statuses") List<QualityStatus> statuses);

    /**
     * Count specimens by status
     */
    long countByStatus(SpecimenStatus status);
}
