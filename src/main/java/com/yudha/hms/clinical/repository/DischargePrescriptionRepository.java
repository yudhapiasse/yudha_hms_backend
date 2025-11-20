package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.DischargePrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Discharge Prescription Repository.
 *
 * Data access layer for discharge prescription operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface DischargePrescriptionRepository extends JpaRepository<DischargePrescription, UUID> {

    @Query("SELECT dp FROM DischargePrescription dp WHERE dp.dischargeSummary.id = :summaryId ORDER BY dp.displayOrder, dp.medicationName")
    List<DischargePrescription> findByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT dp FROM DischargePrescription dp WHERE dp.dischargeSummary.id = :summaryId AND dp.prescriptionStatus = 'ACTIVE' ORDER BY dp.displayOrder")
    List<DischargePrescription> findActiveByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT dp FROM DischargePrescription dp WHERE dp.dischargeSummary.id = :summaryId AND dp.isNewMedication = true")
    List<DischargePrescription> findNewMedicationsByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT dp FROM DischargePrescription dp WHERE dp.dischargeSummary.id = :summaryId AND dp.isChangedMedication = true")
    List<DischargePrescription> findChangedMedicationsByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT dp FROM DischargePrescription dp WHERE dp.medicationName LIKE %:medicationName%")
    List<DischargePrescription> findByMedicationNameContaining(@Param("medicationName") String medicationName);

    @Query("SELECT dp FROM DischargePrescription dp WHERE dp.prescriberId = :prescriberId ORDER BY dp.createdAt DESC")
    List<DischargePrescription> findByPrescriberId(@Param("prescriberId") UUID prescriberId);

    @Query("SELECT COUNT(dp) FROM DischargePrescription dp WHERE dp.dischargeSummary.id = :summaryId")
    long countByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT COUNT(dp) FROM DischargePrescription dp WHERE dp.dischargeSummary.id = :summaryId AND dp.isNewMedication = true")
    long countNewMedicationsByDischargeSummaryId(@Param("summaryId") UUID summaryId);
}
