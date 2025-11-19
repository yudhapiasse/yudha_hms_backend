package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.OutpatientRegistration;
import com.yudha.hms.registration.entity.RegistrationStatus;
import com.yudha.hms.registration.entity.RegistrationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for OutpatientRegistration entities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface OutpatientRegistrationRepository extends JpaRepository<OutpatientRegistration, UUID> {

    /**
     * Find registration by registration number.
     */
    Optional<OutpatientRegistration> findByRegistrationNumber(String registrationNumber);

    /**
     * Find registrations by patient.
     */
    List<OutpatientRegistration> findByPatientIdOrderByRegistrationDateDesc(UUID patientId);

    /**
     * Find registrations by polyclinic and date.
     */
    List<OutpatientRegistration> findByPolyclinicIdAndRegistrationDate(UUID polyclinicId, LocalDate date);

    /**
     * Find registrations by doctor and date.
     */
    List<OutpatientRegistration> findByDoctorIdAndRegistrationDate(UUID doctorId, LocalDate date);

    /**
     * Find registrations by date.
     */
    List<OutpatientRegistration> findByRegistrationDate(LocalDate date);

    /**
     * Find registrations by status.
     */
    List<OutpatientRegistration> findByStatus(RegistrationStatus status);

    /**
     * Find active registrations (REGISTERED, WAITING, IN_CONSULTATION).
     */
    @Query("SELECT r FROM OutpatientRegistration r WHERE r.status IN ('REGISTERED', 'WAITING', 'IN_CONSULTATION') ORDER BY r.queueNumber ASC")
    List<OutpatientRegistration> findAllActive();

    /**
     * Find active registrations by polyclinic.
     */
    @Query("SELECT r FROM OutpatientRegistration r WHERE r.polyclinic.id = :polyclinicId " +
           "AND r.status IN ('REGISTERED', 'WAITING', 'IN_CONSULTATION') " +
           "ORDER BY r.queueNumber ASC")
    List<OutpatientRegistration> findActiveByPolyclinic(@Param("polyclinicId") UUID polyclinicId);

    /**
     * Find today's registrations by polyclinic.
     */
    @Query("SELECT r FROM OutpatientRegistration r WHERE r.polyclinic.id = :polyclinicId " +
           "AND r.registrationDate = :date " +
           "ORDER BY r.queueNumber ASC")
    List<OutpatientRegistration> findTodaysByPolyclinic(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date
    );

    /**
     * Count registrations by polyclinic and date.
     */
    long countByPolyclinicIdAndRegistrationDate(UUID polyclinicId, LocalDate date);

    /**
     * Count registrations by doctor and date.
     */
    long countByDoctorIdAndRegistrationDate(UUID doctorId, LocalDate date);

    /**
     * Find appointments by doctor and date.
     */
    @Query("SELECT r FROM OutpatientRegistration r WHERE r.doctor.id = :doctorId " +
           "AND r.appointmentDate = :date " +
           "AND r.registrationType = 'APPOINTMENT' " +
           "AND r.status != 'CANCELLED' " +
           "ORDER BY r.appointmentTime ASC")
    List<OutpatientRegistration> findAppointmentsByDoctorAndDate(
        @Param("doctorId") UUID doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Check if appointment time slot is available.
     */
    @Query("SELECT COUNT(r) > 0 FROM OutpatientRegistration r WHERE r.doctor.id = :doctorId " +
           "AND r.appointmentDate = :date " +
           "AND r.appointmentTime <= :endTime " +
           "AND r.appointmentEndTime >= :startTime " +
           "AND r.status != 'CANCELLED'")
    boolean isTimeSlotBooked(
        @Param("doctorId") UUID doctorId,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    /**
     * Find latest registration number with prefix.
     */
    @Query("SELECT r.registrationNumber FROM OutpatientRegistration r " +
           "WHERE r.registrationNumber LIKE :prefix% " +
           "ORDER BY r.registrationNumber DESC " +
           "LIMIT 1")
    Optional<String> findLatestRegistrationNumberWithPrefix(@Param("prefix") String prefix);

    /**
     * Find registrations by date range.
     */
    @Query("SELECT r FROM OutpatientRegistration r WHERE r.registrationDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.registrationDate DESC, r.queueNumber ASC")
    List<OutpatientRegistration> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find walk-in registrations for today.
     */
    @Query("SELECT r FROM OutpatientRegistration r WHERE r.registrationDate = :date " +
           "AND r.registrationType = 'WALK_IN' " +
           "ORDER BY r.queueNumber ASC")
    List<OutpatientRegistration> findTodaysWalkIns(@Param("date") LocalDate date);

    /**
     * Find appointments for today.
     */
    @Query("SELECT r FROM OutpatientRegistration r WHERE r.appointmentDate = :date " +
           "AND r.registrationType = 'APPOINTMENT' " +
           "AND r.status != 'CANCELLED' " +
           "ORDER BY r.appointmentTime ASC")
    List<OutpatientRegistration> findTodaysAppointments(@Param("date") LocalDate date);

    /**
     * Find next queue number by polyclinic.
     */
    @Query("SELECT COALESCE(MAX(r.queueNumber), 0) FROM OutpatientRegistration r " +
           "WHERE r.polyclinic.id = :polyclinicId " +
           "AND r.registrationDate = :date")
    Integer findMaxQueueNumber(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date
    );

    /**
     * Check if registration number exists.
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Find registrations by payment method.
     */
    List<OutpatientRegistration> findByIsBpjsTrueAndRegistrationDate(LocalDate date);

    /**
     * Count BPJS patients for date.
     */
    long countByIsBpjsTrueAndRegistrationDate(LocalDate date);

    /**
     * Find registrations by registration type and date.
     */
    List<OutpatientRegistration> findByRegistrationTypeAndRegistrationDate(
        RegistrationType registrationType,
        LocalDate date
    );
}