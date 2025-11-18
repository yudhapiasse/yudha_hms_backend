package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.BedAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for BedAssignment entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface BedAssignmentRepository extends JpaRepository<BedAssignment, UUID> {

    /**
     * Find all assignments for an admission.
     *
     * @param admissionId admission ID
     * @return list of bed assignments
     */
    List<BedAssignment> findByAdmission_Id(UUID admissionId);

    /**
     * Find current assignment for an admission.
     *
     * @param admissionId admission ID
     * @return optional current assignment
     */
    @Query("SELECT ba FROM BedAssignment ba WHERE ba.admission.id = :admissionId AND ba.isCurrent = true")
    Optional<BedAssignment> findCurrentByAdmissionId(@Param("admissionId") UUID admissionId);

    /**
     * Find all assignments for a patient.
     *
     * @param patientId patient ID
     * @return list of bed assignments
     */
    List<BedAssignment> findByPatientId(UUID patientId);

    /**
     * Find all assignments for a bed.
     *
     * @param bedId bed ID
     * @return list of bed assignments
     */
    List<BedAssignment> findByBed_Id(UUID bedId);

    /**
     * Find current assignments for a bed.
     *
     * @param bedId bed ID
     * @return optional current assignment
     */
    @Query("SELECT ba FROM BedAssignment ba WHERE ba.bed.id = :bedId AND ba.isCurrent = true")
    Optional<BedAssignment> findCurrentByBedId(@Param("bedId") UUID bedId);
}
