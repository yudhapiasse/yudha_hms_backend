package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.StockMovementType;
import com.yudha.hms.pharmacy.entity.Drug;
import com.yudha.hms.pharmacy.entity.StockBatch;
import com.yudha.hms.pharmacy.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Stock Movement Repository.
 *
 * Tracks all stock movements for audit and reporting.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID>,
        JpaSpecificationExecutor<StockMovement> {

    /**
     * Find movements by drug
     */
    List<StockMovement> findByDrugOrderByMovementDateDesc(Drug drug);

    /**
     * Find movements by batch
     */
    List<StockMovement> findByBatchOrderByMovementDateDesc(StockBatch batch);

    /**
     * Find movements by location
     */
    List<StockMovement> findByLocationIdOrderByMovementDateDesc(UUID locationId);

    /**
     * Find movements by type
     */
    List<StockMovement> findByMovementTypeOrderByMovementDateDesc(StockMovementType movementType);

    /**
     * Find movements by date range
     */
    @Query("SELECT m FROM StockMovement m WHERE m.movementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.movementDate DESC")
    List<StockMovement> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Find movements by drug and location
     */
    @Query("SELECT m FROM StockMovement m WHERE m.drug = :drug AND m.locationId = :locationId " +
           "ORDER BY m.movementDate DESC")
    List<StockMovement> findByDrugAndLocation(@Param("drug") Drug drug, @Param("locationId") UUID locationId);

    /**
     * Find movements by reference
     */
    @Query("SELECT m FROM StockMovement m WHERE m.referenceType = :referenceType " +
           "AND m.referenceId = :referenceId ORDER BY m.movementDate DESC")
    List<StockMovement> findByReference(@Param("referenceType") String referenceType,
                                        @Param("referenceId") UUID referenceId);

    /**
     * Find recent movements (last N days)
     */
    @Query("SELECT m FROM StockMovement m WHERE m.locationId = :locationId " +
           "AND m.movementDate >= :since ORDER BY m.movementDate DESC")
    List<StockMovement> findRecentMovements(@Param("locationId") UUID locationId,
                                            @Param("since") LocalDateTime since);
}
