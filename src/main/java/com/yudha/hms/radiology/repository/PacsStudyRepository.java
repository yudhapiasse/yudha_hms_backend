package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.StudyStatus;
import com.yudha.hms.radiology.entity.PacsStudy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PacsStudy entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Repository
public interface PacsStudyRepository extends JpaRepository<PacsStudy, UUID> {

    Optional<PacsStudy> findByStudyInstanceUid(String studyInstanceUid);

    Optional<PacsStudy> findByAccessionNumber(String accessionNumber);

    Optional<PacsStudy> findByOrderIdAndDeletedAtIsNull(UUID orderId);

    List<PacsStudy> findByPatientIdAndDeletedAtIsNull(String patientId);

    Page<PacsStudy> findByPatientIdAndDeletedAtIsNull(String patientId, Pageable pageable);

    List<PacsStudy> findByStudyDateAndDeletedAtIsNull(LocalDate studyDate);

    List<PacsStudy> findByStudyStatusAndDeletedAtIsNull(StudyStatus status);

    @Query("SELECT s FROM PacsStudy s WHERE s.studyDate BETWEEN :startDate AND :endDate AND s.deletedAt IS NULL")
    List<PacsStudy> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM PacsStudy s WHERE s.archived = false AND s.deletedAt IS NULL")
    List<PacsStudy> findUnarchived();

    @Query("SELECT s FROM PacsStudy s WHERE s.acquisitionComplete = false AND s.deletedAt IS NULL")
    List<PacsStudy> findIncompleteStudies();

    @Query("SELECT s FROM PacsStudy s WHERE s.viewableExternally = true AND s.shareExpiresAt > CURRENT_TIMESTAMP AND s.deletedAt IS NULL")
    List<PacsStudy> findActivelySharedStudies();

    long countByStudyStatusAndDeletedAtIsNull(StudyStatus status);
}
