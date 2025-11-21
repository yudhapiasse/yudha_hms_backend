package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.WorklistStatus;
import com.yudha.hms.radiology.entity.DicomWorklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DicomWorklist entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Repository
public interface DicomWorklistRepository extends JpaRepository<DicomWorklist, UUID> {

    Optional<DicomWorklist> findByAccessionNumber(String accessionNumber);

    Optional<DicomWorklist> findByOrderIdAndDeletedAtIsNull(UUID orderId);

    List<DicomWorklist> findByWorklistStatusAndDeletedAtIsNull(WorklistStatus status);

    @Query("SELECT w FROM DicomWorklist w WHERE w.scheduledProcedureStepStartDate = :date AND w.deletedAt IS NULL ORDER BY w.scheduledProcedureStepStartTime ASC")
    List<DicomWorklist> findByScheduledDate(@Param("date") LocalDate date);

    @Query("SELECT w FROM DicomWorklist w WHERE w.modalityCode = :modality AND w.scheduledProcedureStepStartDate = :date AND w.deletedAt IS NULL")
    List<DicomWorklist> findByModalityAndDate(@Param("modality") String modality, @Param("date") LocalDate date);

    @Query("SELECT w FROM DicomWorklist w WHERE w.sentToModality = false AND w.worklistStatus = 'SCHEDULED' AND w.deletedAt IS NULL")
    List<DicomWorklist> findPendingWorklistItems();

    Optional<DicomWorklist> findTopByOrderByCreatedAtDesc();

    long countByWorklistStatusAndDeletedAtIsNull(WorklistStatus status);
}
