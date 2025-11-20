package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.DepartmentTransfer;
import com.yudha.hms.clinical.entity.TransferStatus;
import com.yudha.hms.clinical.entity.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Department Transfer Repository.
 *
 * Provides data access methods for department transfer operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface DepartmentTransferRepository extends JpaRepository<DepartmentTransfer, UUID> {

    // ========== Basic Lookups ==========

    /**
     * Find transfer by transfer number.
     */
    Optional<DepartmentTransfer> findByTransferNumber(String transferNumber);

    /**
     * Find all transfers for an encounter, ordered by request time.
     */
    List<DepartmentTransfer> findByEncounterIdOrderByTransferRequestedAtDesc(UUID encounterId);

    /**
     * Find all transfers for a patient.
     */
    List<DepartmentTransfer> findByPatientIdOrderByTransferRequestedAtDesc(UUID patientId);

    // ========== Status-based Queries ==========

    /**
     * Find transfers by status.
     */
    List<DepartmentTransfer> findByTransferStatusOrderByTransferRequestedAtDesc(TransferStatus status);

    /**
     * Find pending transfer requests (REQUESTED or PENDING_APPROVAL).
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.transferStatus IN ('REQUESTED', 'PENDING_APPROVAL') " +
           "ORDER BY dt.transferRequestedAt DESC")
    List<DepartmentTransfer> findPendingTransfers();

    /**
     * Find active transfers (not completed/cancelled/rejected).
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.transferStatus IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED', 'IN_TRANSIT') " +
           "ORDER BY dt.transferRequestedAt DESC")
    List<DepartmentTransfer> findActiveTransfers();

    /**
     * Find transfers requiring approval.
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.requiresApproval = true " +
           "AND dt.transferStatus = 'PENDING_APPROVAL' " +
           "ORDER BY dt.transferRequestedAt DESC")
    List<DepartmentTransfer> findTransfersPendingApproval();

    // ========== Department-based Queries ==========

    /**
     * Find transfers from a specific department.
     */
    List<DepartmentTransfer> findByFromDepartmentIdOrderByTransferRequestedAtDesc(UUID departmentId);

    /**
     * Find transfers to a specific department.
     */
    List<DepartmentTransfer> findByToDepartmentIdOrderByTransferRequestedAtDesc(UUID departmentId);

    /**
     * Find active transfers for a department (either from or to).
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE (dt.fromDepartmentId = :departmentId OR dt.toDepartmentId = :departmentId) " +
           "AND dt.transferStatus IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED', 'IN_TRANSIT') " +
           "ORDER BY dt.transferRequestedAt DESC")
    List<DepartmentTransfer> findActiveDepartmentTransfers(@Param("departmentId") UUID departmentId);

    // ========== Location-based Queries ==========

    /**
     * Find transfers from a specific location (bed/room).
     */
    List<DepartmentTransfer> findByFromLocationIdOrderByTransferRequestedAtDesc(UUID locationId);

    /**
     * Find transfers to a specific location (bed/room).
     */
    List<DepartmentTransfer> findByToLocationIdOrderByTransferRequestedAtDesc(UUID locationId);

    /**
     * Check if location has pending incoming transfer.
     */
    @Query("SELECT CASE WHEN COUNT(dt) > 0 THEN true ELSE false END " +
           "FROM DepartmentTransfer dt " +
           "WHERE dt.toLocationId = :locationId " +
           "AND dt.transferStatus IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED', 'IN_TRANSIT')")
    boolean hasActivetransferToLocation(@Param("locationId") UUID locationId);

    // ========== Type-based Queries ==========

    /**
     * Find transfers by type.
     */
    List<DepartmentTransfer> findByTransferTypeOrderByTransferRequestedAtDesc(TransferType transferType);

    /**
     * Find ICU-related transfers.
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.transferType IN ('ICU_ADMISSION', 'ICU_DISCHARGE', 'STEP_UP') " +
           "ORDER BY dt.transferRequestedAt DESC")
    List<DepartmentTransfer> findICUTransfers();

    /**
     * Find emergency transfers.
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.transferType = 'EMERGENCY' " +
           "OR dt.urgency = 'EMERGENCY' " +
           "ORDER BY dt.transferRequestedAt DESC")
    List<DepartmentTransfer> findEmergencyTransfers();

    // ========== Practitioner-based Queries ==========

    /**
     * Find transfers for a transferring practitioner.
     */
    List<DepartmentTransfer> findByTransferringPractitionerIdOrderByTransferRequestedAtDesc(UUID practitionerId);

    /**
     * Find transfers for a receiving practitioner.
     */
    List<DepartmentTransfer> findByReceivingPractitionerIdOrderByTransferRequestedAtDesc(UUID practitionerId);

    /**
     * Find transfers approved by specific user.
     */
    List<DepartmentTransfer> findByApprovedByIdOrderByApprovedAtDesc(UUID approvedById);

    // ========== Time-based Queries ==========

    /**
     * Find transfers requested within a date range.
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.transferRequestedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY dt.transferRequestedAt DESC")
    List<DepartmentTransfer> findByRequestDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find completed transfers within a date range.
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.transferStatus = 'COMPLETED' " +
           "AND dt.transferCompletedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY dt.transferCompletedAt DESC")
    List<DepartmentTransfer> findCompletedByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // ========== Specific Business Queries ==========

    /**
     * Find active transfer for encounter (should be max 1).
     */
    @Query("SELECT dt FROM DepartmentTransfer dt " +
           "WHERE dt.encounter.id = :encounterId " +
           "AND dt.transferStatus IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED', 'IN_TRANSIT') " +
           "ORDER BY dt.transferRequestedAt DESC")
    Optional<DepartmentTransfer> findActiveTransferForEncounter(@Param("encounterId") UUID encounterId);

    /**
     * Count pending approvals.
     */
    @Query("SELECT COUNT(dt) FROM DepartmentTransfer dt " +
           "WHERE dt.transferStatus = 'PENDING_APPROVAL'")
    long countPendingApprovals();

    /**
     * Count active transfers for a department.
     */
    @Query("SELECT COUNT(dt) FROM DepartmentTransfer dt " +
           "WHERE (dt.fromDepartmentId = :departmentId OR dt.toDepartmentId = :departmentId) " +
           "AND dt.transferStatus IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED', 'IN_TRANSIT')")
    long countActiveDepartmentTransfers(@Param("departmentId") UUID departmentId);

    /**
     * Check if patient has active transfer.
     */
    @Query("SELECT CASE WHEN COUNT(dt) > 0 THEN true ELSE false END " +
           "FROM DepartmentTransfer dt " +
           "WHERE dt.patientId = :patientId " +
           "AND dt.transferStatus IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED', 'IN_TRANSIT')")
    boolean hasActiveTransfer(@Param("patientId") UUID patientId);
}
