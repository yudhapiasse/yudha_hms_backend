package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.DispensingStatus;
import com.yudha.hms.pharmacy.constant.DispensingType;
import com.yudha.hms.pharmacy.entity.Dispensing;
import com.yudha.hms.pharmacy.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Dispensing Repository.
 *
 * Provides data access for dispensing operations with queue management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface DispensingRepository extends JpaRepository<Dispensing, UUID>,
        JpaSpecificationExecutor<Dispensing> {

    /**
     * Find dispensing by number
     */
    Optional<Dispensing> findByDispensingNumber(String dispensingNumber);

    /**
     * Find by prescription
     */
    Optional<Dispensing> findByPrescription(Prescription prescription);

    /**
     * Check if prescription already has dispensing
     */
    boolean existsByPrescription(Prescription prescription);

    /**
     * Find by patient
     */
    List<Dispensing> findByPatientIdOrderByDispensingDateDesc(UUID patientId);

    /**
     * Find by location and status
     */
    List<Dispensing> findByLocationIdAndStatusOrderByQueuePositionAsc(UUID locationId, DispensingStatus status);

    /**
     * Find queue at location - all active statuses ordered by priority and position
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status IN ('QUEUE', 'PREPARING', 'VERIFICATION', 'READY') " +
           "AND d.active = true " +
           "ORDER BY d.isUrgent DESC, d.priority DESC, d.queuePosition ASC, d.createdAt ASC")
    List<Dispensing> findQueueByLocation(@Param("locationId") UUID locationId);

    /**
     * Find queue items waiting to be prepared
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status = 'QUEUE' AND d.active = true " +
           "ORDER BY d.isUrgent DESC, d.priority DESC, d.queuePosition ASC, d.createdAt ASC")
    List<Dispensing> findPendingQueue(@Param("locationId") UUID locationId);

    /**
     * Find items currently being prepared
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status = 'PREPARING' AND d.active = true " +
           "ORDER BY d.preparedAt ASC")
    List<Dispensing> findCurrentlyPreparing(@Param("locationId") UUID locationId);

    /**
     * Find items ready for pickup
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status = 'READY' AND d.active = true " +
           "ORDER BY d.actualReadyTime ASC")
    List<Dispensing> findReadyForPickup(@Param("locationId") UUID locationId);

    /**
     * Find items requiring verification
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status = 'VERIFICATION' AND d.active = true " +
           "ORDER BY d.preparedAt ASC")
    List<Dispensing> findPendingVerification(@Param("locationId") UUID locationId);

    /**
     * Find urgent items in queue
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.isUrgent = true " +
           "AND d.status IN ('QUEUE', 'PREPARING', 'VERIFICATION') " +
           "AND d.active = true " +
           "ORDER BY d.priority DESC, d.createdAt ASC")
    List<Dispensing> findUrgentItems(@Param("locationId") UUID locationId);

    /**
     * Find items requiring counseling
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.counselingRequired = true " +
           "AND d.counselingCompleted = false " +
           "AND d.status IN ('READY', 'VERIFICATION') " +
           "AND d.active = true " +
           "ORDER BY d.actualReadyTime ASC")
    List<Dispensing> findRequiringCounseling(@Param("locationId") UUID locationId);

    /**
     * Find by status and date range
     */
    @Query("SELECT d FROM Dispensing d WHERE d.status = :status " +
           "AND d.dispensingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY d.dispensingDate DESC")
    List<Dispensing> findByStatusAndDateRange(@Param("status") DispensingStatus status,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Find by dispensing type
     */
    List<Dispensing> findByTypeOrderByDispensingDateDesc(DispensingType type);

    /**
     * Find by type and location and date range - for shift reports
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.type = :type " +
           "AND d.dispensingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY d.dispensingDate DESC")
    List<Dispensing> findShiftDispensing(@Param("locationId") UUID locationId,
                                          @Param("type") DispensingType type,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Find all dispensing in date range for shift report
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.dispensingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY d.dispensingDate DESC")
    List<Dispensing> findByLocationAndDateRange(@Param("locationId") UUID locationId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Count items in queue by location
     */
    @Query("SELECT COUNT(d) FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status IN ('QUEUE', 'PREPARING', 'VERIFICATION', 'READY') " +
           "AND d.active = true")
    Long countQueueItems(@Param("locationId") UUID locationId);

    /**
     * Get next queue position for location
     */
    @Query("SELECT COALESCE(MAX(d.queuePosition), 0) + 1 FROM Dispensing d " +
           "WHERE d.locationId = :locationId " +
           "AND d.status IN ('QUEUE', 'PREPARING', 'VERIFICATION', 'READY') " +
           "AND d.active = true")
    Integer getNextQueuePosition(@Param("locationId") UUID locationId);

    /**
     * Find items on hold
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status = 'ON_HOLD' AND d.active = true " +
           "ORDER BY d.updatedAt DESC")
    List<Dispensing> findOnHoldItems(@Param("locationId") UUID locationId);

    /**
     * Find overdue items (estimated ready time passed but not yet ready)
     */
    @Query("SELECT d FROM Dispensing d WHERE d.locationId = :locationId " +
           "AND d.status IN ('PREPARING', 'VERIFICATION') " +
           "AND d.estimatedReadyTime < :currentTime " +
           "AND d.active = true " +
           "ORDER BY d.estimatedReadyTime ASC")
    List<Dispensing> findOverdueItems(@Param("locationId") UUID locationId,
                                       @Param("currentTime") LocalDateTime currentTime);
}
