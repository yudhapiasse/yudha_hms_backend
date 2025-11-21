package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.entity.Drug;
import com.yudha.hms.pharmacy.entity.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Stock Batch Repository.
 *
 * Critical repository for FIFO/FEFO inventory management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface StockBatchRepository extends JpaRepository<StockBatch, UUID>,
        JpaSpecificationExecutor<StockBatch> {

    /**
     * Find batch by drug, location, and batch number
     */
    Optional<StockBatch> findByDrugAndLocationIdAndBatchNumber(Drug drug, UUID locationId, String batchNumber);

    /**
     * Find all active batches for a drug at a location
     */
    @Query("SELECT b FROM StockBatch b WHERE b.drug = :drug AND b.locationId = :locationId " +
           "AND b.active = true AND b.quantityOnHand > 0 ORDER BY b.expiryDate ASC, b.receivedDate ASC")
    List<StockBatch> findActiveBatchesByDrugAndLocation(@Param("drug") Drug drug,
                                                         @Param("locationId") UUID locationId);

    /**
     * FEFO: Find batches with earliest expiry dates (First Expiry First Out)
     * Returns batches in FEFO order
     */
    @Query("SELECT b FROM StockBatch b WHERE b.drug = :drug AND b.locationId = :locationId " +
           "AND b.active = true AND b.quantityAvailable > 0 AND b.isQuarantined = false " +
           "AND (b.expiryDate IS NULL OR b.expiryDate > CURRENT_DATE) " +
           "ORDER BY b.expiryDate ASC NULLS LAST, b.receivedDate ASC")
    List<StockBatch> findBatchesForFefo(@Param("drug") Drug drug, @Param("locationId") UUID locationId);

    /**
     * FIFO: Find batches in First In First Out order
     */
    @Query("SELECT b FROM StockBatch b WHERE b.drug = :drug AND b.locationId = :locationId " +
           "AND b.active = true AND b.quantityAvailable > 0 AND b.isQuarantined = false " +
           "ORDER BY b.receivedDate ASC, b.createdAt ASC")
    List<StockBatch> findBatchesForFifo(@Param("drug") Drug drug, @Param("locationId") UUID locationId);

    /**
     * Find batches expiring within days
     */
    @Query("SELECT b FROM StockBatch b WHERE b.locationId = :locationId " +
           "AND b.active = true AND b.quantityOnHand > 0 " +
           "AND b.expiryDate BETWEEN CURRENT_DATE AND :expiryDate " +
           "ORDER BY b.expiryDate ASC")
    List<StockBatch> findBatchesExpiringBefore(@Param("locationId") UUID locationId,
                                                @Param("expiryDate") LocalDate expiryDate);

    /**
     * Find expired batches
     */
    @Query("SELECT b FROM StockBatch b WHERE b.locationId = :locationId " +
           "AND b.active = true AND b.quantityOnHand > 0 " +
           "AND b.expiryDate < CURRENT_DATE ORDER BY b.expiryDate ASC")
    List<StockBatch> findExpiredBatches(@Param("locationId") UUID locationId);

    /**
     * Find quarantined batches
     */
    @Query("SELECT b FROM StockBatch b WHERE b.locationId = :locationId " +
           "AND b.isQuarantined = true ORDER BY b.createdAt DESC")
    List<StockBatch> findQuarantinedBatches(@Param("locationId") UUID locationId);

    /**
     * Calculate total available quantity for drug at location
     */
    @Query("SELECT COALESCE(SUM(b.quantityAvailable), 0) FROM StockBatch b " +
           "WHERE b.drug = :drug AND b.locationId = :locationId " +
           "AND b.active = true AND b.isQuarantined = false")
    BigDecimal getTotalAvailableQuantity(@Param("drug") Drug drug, @Param("locationId") UUID locationId);

    /**
     * Calculate total on-hand quantity for drug at location
     */
    @Query("SELECT COALESCE(SUM(b.quantityOnHand), 0) FROM StockBatch b " +
           "WHERE b.drug = :drug AND b.locationId = :locationId AND b.active = true")
    BigDecimal getTotalOnHandQuantity(@Param("drug") Drug drug, @Param("locationId") UUID locationId);

    /**
     * Find batches below reorder point
     */
    @Query("SELECT b FROM StockBatch b WHERE b.locationId = :locationId " +
           "AND b.active = true AND b.drug.currentStock < b.drug.minimumStockLevel")
    List<StockBatch> findBatchesBelowReorderPoint(@Param("locationId") UUID locationId);

    /**
     * Find all active batches at location
     */
    @Query("SELECT b FROM StockBatch b WHERE b.locationId = :locationId " +
           "AND b.active = true ORDER BY b.drug.genericName, b.expiryDate")
    List<StockBatch> findAllActiveByLocation(@Param("locationId") UUID locationId);

    /**
     * Check if batch exists
     */
    boolean existsByDrugAndLocationIdAndBatchNumber(Drug drug, UUID locationId, String batchNumber);

    /**
     * Count active batches for drug
     */
    long countByDrugAndActiveTrue(Drug drug);

    /**
     * Find batches by supplier
     */
    List<StockBatch> findBySupplierId(UUID supplierId);
}
