package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.PrescriptionStatus;
import com.yudha.hms.pharmacy.constant.PrescriptionType;
import com.yudha.hms.pharmacy.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Prescription Repository.
 *
 * Data access layer for Prescription entity with comprehensive query methods.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID>,
        JpaSpecificationExecutor<Prescription> {

    /**
     * Find prescription by prescription number
     */
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);

    /**
     * Find prescriptions by patient
     */
    List<Prescription> findByPatientIdOrderByPrescriptionDateDesc(UUID patientId);

    /**
     * Find prescriptions by patient (pageable)
     */
    Page<Prescription> findByPatientIdOrderByPrescriptionDateDesc(UUID patientId, Pageable pageable);

    /**
     * Find prescriptions by encounter
     */
    List<Prescription> findByEncounterIdOrderByCreatedAtDesc(UUID encounterId);

    /**
     * Find prescriptions by doctor
     */
    List<Prescription> findByDoctorIdOrderByPrescriptionDateDesc(UUID doctorId);

    /**
     * Find prescriptions by doctor (pageable)
     */
    Page<Prescription> findByDoctorIdOrderByPrescriptionDateDesc(UUID doctorId, Pageable pageable);

    /**
     * Find prescriptions by status
     */
    List<Prescription> findByStatusOrderByPrescriptionDateDesc(PrescriptionStatus status);

    /**
     * Find prescriptions pending verification
     */
    @Query("SELECT p FROM Prescription p WHERE p.status = 'PENDING_VERIFICATION' " +
           "AND p.active = true AND p.deletedAt IS NULL ORDER BY p.submittedAt ASC")
    List<Prescription> findPendingVerification();

    /**
     * Find verified prescriptions not yet dispensed
     */
    @Query("SELECT p FROM Prescription p WHERE p.status = 'VERIFIED' " +
           "AND p.active = true AND p.deletedAt IS NULL ORDER BY p.verifiedAt ASC")
    List<Prescription> findVerifiedNotDispensed();

    /**
     * Find prescriptions by date range
     */
    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :startDate AND :endDate " +
           "AND p.active = true AND p.deletedAt IS NULL ORDER BY p.prescriptionDate DESC")
    List<Prescription> findByDateRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    /**
     * Find controlled drug prescriptions
     */
    @Query("SELECT p FROM Prescription p WHERE p.isControlled = true " +
           "AND p.active = true AND p.deletedAt IS NULL ORDER BY p.prescriptionDate DESC")
    List<Prescription> findControlledDrugPrescriptions();

    /**
     * Find controlled drug prescriptions within date range
     */
    @Query("SELECT p FROM Prescription p WHERE p.isControlled = true " +
           "AND p.prescriptionDate BETWEEN :startDate AND :endDate " +
           "AND p.active = true AND p.deletedAt IS NULL ORDER BY p.prescriptionDate DESC")
    List<Prescription> findControlledDrugPrescriptions(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * Find prescriptions with interactions
     */
    @Query("SELECT p FROM Prescription p WHERE p.hasInteractions = true " +
           "AND p.active = true AND p.deletedAt IS NULL ORDER BY p.prescriptionDate DESC")
    List<Prescription> findWithInteractions();

    /**
     * Find expired prescriptions
     */
    @Query("SELECT p FROM Prescription p WHERE p.validUntil < CURRENT_DATE " +
           "AND p.status NOT IN ('DISPENSED', 'CANCELLED', 'EXPIRED') " +
           "AND p.active = true AND p.deletedAt IS NULL")
    List<Prescription> findExpiredPrescriptions();

    /**
     * Find prescriptions by patient and date range
     */
    @Query("SELECT p FROM Prescription p WHERE p.patientId = :patientId " +
           "AND p.prescriptionDate BETWEEN :startDate AND :endDate " +
           "AND p.active = true AND p.deletedAt IS NULL ORDER BY p.prescriptionDate DESC")
    List<Prescription> findByPatientAndDateRange(@Param("patientId") UUID patientId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * Count prescriptions by status
     */
    long countByStatus(PrescriptionStatus status);

    /**
     * Count prescriptions by doctor and status
     */
    long countByDoctorIdAndStatus(UUID doctorId, PrescriptionStatus status);

    /**
     * Check if prescription number exists
     */
    boolean existsByPrescriptionNumber(String prescriptionNumber);
}
