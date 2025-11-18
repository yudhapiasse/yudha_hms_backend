package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.AdmissionDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for AdmissionDiagnosis entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface AdmissionDiagnosisRepository extends JpaRepository<AdmissionDiagnosis, UUID> {

    /**
     * Find all diagnoses for an admission.
     *
     * @param admissionId admission ID
     * @return list of diagnoses
     */
    List<AdmissionDiagnosis> findByAdmission_Id(UUID admissionId);

    /**
     * Find primary diagnosis for an admission.
     *
     * @param admissionId admission ID
     * @return optional primary diagnosis
     */
    @Query("SELECT ad FROM AdmissionDiagnosis ad WHERE ad.admission.id = :admissionId AND ad.isPrimary = true")
    Optional<AdmissionDiagnosis> findPrimaryByAdmissionId(@Param("admissionId") UUID admissionId);

    /**
     * Find all diagnoses for a patient.
     *
     * @param patientId patient ID
     * @return list of diagnoses
     */
    List<AdmissionDiagnosis> findByPatientId(UUID patientId);
}
