package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.VerificationStatus;
import com.yudha.hms.pharmacy.entity.Prescription;
import com.yudha.hms.pharmacy.entity.PrescriptionVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Prescription Verification Repository.
 *
 * Data access layer for PrescriptionVerification entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface PrescriptionVerificationRepository extends JpaRepository<PrescriptionVerification, UUID> {

    /**
     * Find verifications by prescription
     */
    List<PrescriptionVerification> findByPrescriptionOrderByVerifiedAtDesc(Prescription prescription);

    /**
     * Find verifications by prescription ID
     */
    List<PrescriptionVerification> findByPrescription_IdOrderByVerifiedAtDesc(UUID prescriptionId);

    /**
     * Find latest verification for prescription
     */
    Optional<PrescriptionVerification> findFirstByPrescription_IdOrderByVerifiedAtDesc(UUID prescriptionId);

    /**
     * Find verifications by pharmacist
     */
    List<PrescriptionVerification> findByPharmacistIdOrderByVerifiedAtDesc(UUID pharmacistId);

    /**
     * Find verifications by status
     */
    List<PrescriptionVerification> findByStatusOrderByVerifiedAtDesc(VerificationStatus status);

    /**
     * Find verifications with interactions
     */
    @Query("SELECT v FROM PrescriptionVerification v WHERE v.interactionsFound = true " +
           "ORDER BY v.verifiedAt DESC")
    List<PrescriptionVerification> findWithInteractions();

    /**
     * Find verifications with dosage issues
     */
    @Query("SELECT v FROM PrescriptionVerification v WHERE v.dosageIssuesFound = true " +
           "ORDER BY v.verifiedAt DESC")
    List<PrescriptionVerification> findWithDosageIssues();

    /**
     * Find verifications with allergies
     */
    @Query("SELECT v FROM PrescriptionVerification v WHERE v.allergiesFound = true " +
           "ORDER BY v.verifiedAt DESC")
    List<PrescriptionVerification> findWithAllergies();

    /**
     * Find verifications requiring dual verification
     */
    @Query("SELECT v FROM PrescriptionVerification v WHERE v.dualVerificationRequired = true " +
           "AND v.secondPharmacistId IS NULL " +
           "ORDER BY v.verifiedAt ASC")
    List<PrescriptionVerification> findRequiringDualVerification();

    /**
     * Find verifications by date range
     */
    @Query("SELECT v FROM PrescriptionVerification v WHERE v.verifiedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY v.verifiedAt DESC")
    List<PrescriptionVerification> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Count verifications by pharmacist
     */
    long countByPharmacistId(UUID pharmacistId);

    /**
     * Count verifications by status
     */
    long countByStatus(VerificationStatus status);
}
