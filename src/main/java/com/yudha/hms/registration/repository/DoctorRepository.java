package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Doctor entities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    /**
     * Find doctor by STR number.
     */
    Optional<Doctor> findByStrNumber(String strNumber);

    /**
     * Find doctor by employee ID.
     */
    Optional<Doctor> findByEmployeeId(String employeeId);

    /**
     * Find all active doctors.
     */
    List<Doctor> findByIsActiveTrue();

    /**
     * Find active doctors ordered by name.
     */
    List<Doctor> findByIsActiveTrueOrderByFullNameAsc();

    /**
     * Find doctors by specialization.
     */
    List<Doctor> findBySpecializationAndIsActiveTrue(String specialization);

    /**
     * Find doctors available for telemedicine.
     */
    List<Doctor> findByIsActiveTrueAndIsAvailableForTelemedicineTrue();

    /**
     * Search doctors by name.
     */
    @Query("SELECT d FROM Doctor d WHERE d.isActive = true AND LOWER(d.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Doctor> searchByName(@Param("name") String name);

    /**
     * Check if STR number exists.
     */
    boolean existsByStrNumber(String strNumber);

    /**
     * Count active doctors.
     */
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isActive = true")
    long countActiveDoctors();

    /**
     * Find doctors by polyclinic (via schedules).
     */
    @Query("SELECT DISTINCT ds.doctor FROM DoctorSchedule ds WHERE ds.polyclinic.id = :polyclinicId AND ds.isActive = true AND ds.doctor.isActive = true")
    List<Doctor> findByPolyclinicId(@Param("polyclinicId") UUID polyclinicId);
}