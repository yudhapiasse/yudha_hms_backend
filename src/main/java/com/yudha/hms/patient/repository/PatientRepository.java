package com.yudha.hms.patient.repository;

import com.yudha.hms.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Patient Repository.
 *
 * Data access layer for Patient entity with custom query methods.
 * Supports dynamic queries via JpaSpecificationExecutor for advanced search.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID>, JpaSpecificationExecutor<Patient> {

    /**
     * Find patient by MRN (Medical Record Number)
     * Excludes soft-deleted patients (via @Where clause in entity)
     *
     * @param mrn medical record number
     * @return optional patient
     */
    Optional<Patient> findByMrn(String mrn);

    /**
     * Find patient by NIK
     * Excludes soft-deleted patients
     *
     * @param nik Indonesian national ID
     * @return optional patient
     */
    Optional<Patient> findByNik(String nik);

    /**
     * Find patient by BPJS number
     * Excludes soft-deleted patients
     *
     * @param bpjsNumber BPJS card number
     * @return optional patient
     */
    Optional<Patient> findByBpjsNumber(String bpjsNumber);

    /**
     * Check if patient exists by MRN
     * Excludes soft-deleted patients
     *
     * @param mrn medical record number
     * @return true if exists
     */
    boolean existsByMrn(String mrn);

    /**
     * Check if patient exists by NIK
     * Excludes soft-deleted patients
     *
     * @param nik Indonesian national ID
     * @return true if exists
     */
    boolean existsByNik(String nik);

    /**
     * Check if patient exists by BPJS number
     * Excludes soft-deleted patients
     *
     * @param bpjsNumber BPJS card number
     * @return true if exists
     */
    boolean existsByBpjsNumber(String bpjsNumber);

    /**
     * Check if NIK exists for different patient (for updates)
     *
     * @param nik NIK to check
     * @param patientId current patient ID to exclude
     * @return true if exists
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Patient p " +
           "WHERE p.nik = :nik AND p.id != :patientId")
    boolean existsByNikAndIdNot(@Param("nik") String nik, @Param("patientId") UUID patientId);

    /**
     * Check if BPJS number exists for different patient (for updates)
     *
     * @param bpjsNumber BPJS number to check
     * @param patientId current patient ID to exclude
     * @return true if exists
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Patient p " +
           "WHERE p.bpjsNumber = :bpjsNumber AND p.id != :patientId")
    boolean existsByBpjsNumberAndIdNot(@Param("bpjsNumber") String bpjsNumber, @Param("patientId") UUID patientId);

    /**
     * Get next MRN sequence value
     * Uses database sequence
     *
     * @return next sequence number
     */
    @Query(value = "SELECT nextval('patient_schema.mrn_sequence')", nativeQuery = true)
    Long getNextMrnSequence();

    /**
     * Find patients by name (case-insensitive, partial match)
     * Uses trigram search for better matching
     *
     * @param name name to search
     * @return list of matching patients
     */
    @Query("SELECT p FROM Patient p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    java.util.List<Patient> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find active patients only
     *
     * @return list of active patients
     */
    java.util.List<Patient> findByIsActiveTrue();

    /**
     * Find VIP patients
     *
     * @return list of VIP patients
     */
    java.util.List<Patient> findByIsVipTrue();
}
