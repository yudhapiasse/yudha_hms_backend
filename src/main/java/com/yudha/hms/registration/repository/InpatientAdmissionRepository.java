package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.AdmissionStatus;
import com.yudha.hms.registration.entity.InpatientAdmission;
import com.yudha.hms.registration.entity.RoomClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for InpatientAdmission entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface InpatientAdmissionRepository extends JpaRepository<InpatientAdmission, UUID> {

    /**
     * Find admission by admission number.
     *
     * @param admissionNumber admission number
     * @return optional admission
     */
    Optional<InpatientAdmission> findByAdmissionNumber(String admissionNumber);

    /**
     * Find all admissions for a patient.
     *
     * @param patientId patient ID
     * @return list of admissions
     */
    List<InpatientAdmission> findByPatientId(UUID patientId);

    /**
     * Find current (active) admission for a patient.
     *
     * @param patientId patient ID
     * @return optional admission
     */
    @Query("SELECT a FROM InpatientAdmission a WHERE a.patientId = :patientId AND (a.status = 'ADMITTED' OR a.status = 'IN_TREATMENT') ORDER BY a.admissionDate DESC")
    Optional<InpatientAdmission> findCurrentAdmissionByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find admissions by status.
     *
     * @param status admission status
     * @return list of admissions
     */
    List<InpatientAdmission> findByStatus(AdmissionStatus status);

    /**
     * Find active admissions (ADMITTED or IN_TREATMENT).
     *
     * @return list of active admissions
     */
    @Query("SELECT a FROM InpatientAdmission a WHERE a.status IN ('ADMITTED', 'IN_TREATMENT') ORDER BY a.admissionDate DESC")
    List<InpatientAdmission> findAllActive();

    /**
     * Find admissions by room.
     *
     * @param roomId room ID
     * @return list of admissions
     */
    List<InpatientAdmission> findByRoom_Id(UUID roomId);

    /**
     * Find admissions by bed.
     *
     * @param bedId bed ID
     * @return list of admissions
     */
    List<InpatientAdmission> findByBed_Id(UUID bedId);

    /**
     * Find admissions by room class.
     *
     * @param roomClass room class
     * @return list of admissions
     */
    List<InpatientAdmission> findByRoomClass(RoomClass roomClass);

    /**
     * Find admissions by doctor.
     *
     * @param doctorId doctor ID
     * @return list of admissions
     */
    List<InpatientAdmission> findByAdmittingDoctorId(UUID doctorId);

    /**
     * Find admissions by date range.
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of admissions
     */
    @Query("SELECT a FROM InpatientAdmission a WHERE a.admissionDate BETWEEN :startDate AND :endDate ORDER BY a.admissionDate DESC")
    List<InpatientAdmission> findByAdmissionDateBetween(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find admissions scheduled for discharge today.
     *
     * @param today today's date
     * @return list of admissions
     */
    @Query("SELECT a FROM InpatientAdmission a WHERE a.estimatedDischargeDate = :today AND (a.status = 'ADMITTED' OR a.status = 'IN_TREATMENT')")
    List<InpatientAdmission> findScheduledForDischarge(@Param("today") java.time.LocalDate today);

    /**
     * Count active admissions.
     *
     * @return count of active admissions
     */
    @Query("SELECT COUNT(a) FROM InpatientAdmission a WHERE a.status IN ('ADMITTED', 'IN_TREATMENT')")
    long countActive();

    /**
     * Count admissions by room class.
     *
     * @param roomClass room class
     * @return count of admissions
     */
    long countByRoomClass(RoomClass roomClass);

    /**
     * Find admissions with unpaid deposits.
     *
     * @return list of admissions
     */
    @Query("SELECT a FROM InpatientAdmission a WHERE (a.status = 'ADMITTED' OR a.status = 'IN_TREATMENT') AND a.depositPaid < a.requiredDeposit")
    List<InpatientAdmission> findWithUnpaidDeposits();

    /**
     * Check if patient has active admission.
     *
     * @param patientId patient ID
     * @return true if patient has active admission
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM InpatientAdmission a WHERE a.patientId = :patientId AND (a.status = 'ADMITTED' OR a.status = 'IN_TREATMENT')")
    boolean hasActiveAdmission(@Param("patientId") UUID patientId);
}
