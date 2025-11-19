package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DoctorSchedule entities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {

    /**
     * Find schedules by doctor.
     */
    List<DoctorSchedule> findByDoctorIdAndIsActiveTrue(UUID doctorId);

    /**
     * Find schedules by polyclinic.
     */
    List<DoctorSchedule> findByPolyclinicIdAndIsActiveTrue(UUID polyclinicId);

    /**
     * Find schedules by doctor and polyclinic.
     */
    List<DoctorSchedule> findByDoctorIdAndPolyclinicIdAndIsActiveTrue(UUID doctorId, UUID polyclinicId);

    /**
     * Find schedule by doctor, polyclinic and day of week.
     */
    Optional<DoctorSchedule> findByDoctorIdAndPolyclinicIdAndDayOfWeekAndIsActiveTrue(
        UUID doctorId,
        UUID polyclinicId,
        String dayOfWeek
    );

    /**
     * Find schedules by day of week.
     */
    List<DoctorSchedule> findByDayOfWeekAndIsActiveTrue(String dayOfWeek);

    /**
     * Find schedules valid for a specific date.
     */
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.isActive = true " +
           "AND ds.dayOfWeek = :dayOfWeek " +
           "AND (ds.effectiveDate IS NULL OR ds.effectiveDate <= :date) " +
           "AND (ds.expiryDate IS NULL OR ds.expiryDate >= :date)")
    List<DoctorSchedule> findValidSchedulesForDate(
        @Param("dayOfWeek") String dayOfWeek,
        @Param("date") LocalDate date
    );

    /**
     * Find doctor schedules valid for a specific date.
     */
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor.id = :doctorId " +
           "AND ds.isActive = true " +
           "AND ds.dayOfWeek = :dayOfWeek " +
           "AND (ds.effectiveDate IS NULL OR ds.effectiveDate <= :date) " +
           "AND (ds.expiryDate IS NULL OR ds.expiryDate >= :date)")
    List<DoctorSchedule> findDoctorSchedulesForDate(
        @Param("doctorId") UUID doctorId,
        @Param("dayOfWeek") String dayOfWeek,
        @Param("date") LocalDate date
    );

    /**
     * Find schedules by doctor and polyclinic for a specific date.
     */
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor.id = :doctorId " +
           "AND ds.polyclinic.id = :polyclinicId " +
           "AND ds.isActive = true " +
           "AND ds.dayOfWeek = :dayOfWeek " +
           "AND (ds.effectiveDate IS NULL OR ds.effectiveDate <= :date) " +
           "AND (ds.expiryDate IS NULL OR ds.expiryDate >= :date)")
    Optional<DoctorSchedule> findScheduleForDate(
        @Param("doctorId") UUID doctorId,
        @Param("polyclinicId") UUID polyclinicId,
        @Param("dayOfWeek") String dayOfWeek,
        @Param("date") LocalDate date
    );

    /**
     * Count schedules by doctor.
     */
    long countByDoctorIdAndIsActiveTrue(UUID doctorId);

    /**
     * Delete schedules by doctor.
     */
    void deleteByDoctorId(UUID doctorId);
}