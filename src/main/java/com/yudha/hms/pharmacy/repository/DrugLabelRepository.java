package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.entity.Dispensing;
import com.yudha.hms.pharmacy.entity.DrugLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Drug Label Repository.
 *
 * Provides data access for drug label generation and printing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface DrugLabelRepository extends JpaRepository<DrugLabel, UUID>,
        JpaSpecificationExecutor<DrugLabel> {

    /**
     * Find labels by dispensing
     */
    List<DrugLabel> findByDispensingOrderByCreatedAtAsc(Dispensing dispensing);

    /**
     * Find labels by patient
     */
    List<DrugLabel> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    /**
     * Find recently printed labels
     */
    @Query("SELECT l FROM DrugLabel l WHERE l.printedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY l.printedAt DESC")
    List<DrugLabel> findRecentlyPrinted(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Find labels printed by user
     */
    List<DrugLabel> findByPrintedByIdOrderByPrintedAtDesc(UUID printedById);

    /**
     * Find unprinted labels (generated but not yet printed)
     */
    @Query("SELECT l FROM DrugLabel l WHERE l.printCount = 0 " +
           "ORDER BY l.createdAt ASC")
    List<DrugLabel> findUnprinted();

    /**
     * Find reprinted labels (printed more than once)
     */
    @Query("SELECT l FROM DrugLabel l WHERE l.printCount > 1 " +
           "ORDER BY l.printedAt DESC")
    List<DrugLabel> findReprints();

    /**
     * Count labels printed in date range
     */
    @Query("SELECT COUNT(l) FROM DrugLabel l WHERE l.printedAt BETWEEN :startDate AND :endDate")
    Long countPrintedInDateRange(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
}
