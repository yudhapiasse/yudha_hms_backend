package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.ReturnReason;
import com.yudha.hms.pharmacy.entity.Dispensing;
import com.yudha.hms.pharmacy.entity.DrugReturn;
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
 * Drug Return Repository.
 *
 * Provides data access for drug return and exchange transactions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface DrugReturnRepository extends JpaRepository<DrugReturn, UUID>,
        JpaSpecificationExecutor<DrugReturn> {

    /**
     * Find return by number
     */
    Optional<DrugReturn> findByReturnNumber(String returnNumber);

    /**
     * Find returns by dispensing
     */
    List<DrugReturn> findByDispensingOrderByReturnDateDesc(Dispensing dispensing);

    /**
     * Find returns by patient
     */
    List<DrugReturn> findByPatientIdOrderByReturnDateDesc(UUID patientId);

    /**
     * Find returns by location
     */
    List<DrugReturn> findByLocationIdOrderByReturnDateDesc(UUID locationId);

    /**
     * Find returns by reason
     */
    List<DrugReturn> findByReasonOrderByReturnDateDesc(ReturnReason reason);

    /**
     * Find pending approval returns
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.isApproved = false " +
           "AND r.status = 'PENDING' " +
           "AND r.active = true " +
           "ORDER BY r.returnDate ASC")
    List<DrugReturn> findPendingApproval();

    /**
     * Find approved returns pending restock
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.isApproved = true " +
           "AND r.canRestock = true " +
           "AND r.restocked = false " +
           "AND r.active = true " +
           "ORDER BY r.approvedAt ASC")
    List<DrugReturn> findPendingRestock();

    /**
     * Find pharmacy error returns - for quality monitoring
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.isPharmacyError = true " +
           "AND r.returnDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.returnDate DESC")
    List<DrugReturn> findPharmacyErrors(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Find returns requiring incident reports
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.incidentReportRequired = true " +
           "AND (r.incidentReportNumber IS NULL OR r.incidentReportNumber = '') " +
           "AND r.active = true " +
           "ORDER BY r.returnDate ASC")
    List<DrugReturn> findRequiringIncidentReport();

    /**
     * Find quality issues pending supplier notification
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.qualityIssue = true " +
           "AND r.supplierNotified = false " +
           "AND r.active = true " +
           "ORDER BY r.returnDate ASC")
    List<DrugReturn> findQualityIssuesPendingNotification();

    /**
     * Find returns in date range
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.returnDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.returnDate DESC")
    List<DrugReturn> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find returns by location and date range - for shift reports
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.locationId = :locationId " +
           "AND r.returnDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.returnDate DESC")
    List<DrugReturn> findByLocationAndDateRange(@Param("locationId") UUID locationId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Find returns pending refund processing
     */
    @Query("SELECT r FROM DrugReturn r WHERE r.isApproved = true " +
           "AND r.refundProcessed = false " +
           "AND r.refundAmount > 0 " +
           "AND r.active = true " +
           "ORDER BY r.approvedAt ASC")
    List<DrugReturn> findPendingRefund();

    /**
     * Count returns by reason in date range - for reporting
     */
    @Query("SELECT r.reason, COUNT(r) FROM DrugReturn r " +
           "WHERE r.returnDate BETWEEN :startDate AND :endDate " +
           "GROUP BY r.reason " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> countByReasonInDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate total return value in date range
     */
    @Query("SELECT SUM(r.totalAmount) FROM DrugReturn r " +
           "WHERE r.isApproved = true " +
           "AND r.returnDate BETWEEN :startDate AND :endDate")
    Double getTotalReturnValue(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    /**
     * Get pharmacy error rate (percentage of returns due to pharmacy errors)
     */
    @Query("SELECT (COUNT(r) * 100.0 / (SELECT COUNT(r2) FROM DrugReturn r2 " +
           "WHERE r2.returnDate BETWEEN :startDate AND :endDate)) " +
           "FROM DrugReturn r WHERE r.isPharmacyError = true " +
           "AND r.returnDate BETWEEN :startDate AND :endDate")
    Double getPharmacyErrorRate(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
}
