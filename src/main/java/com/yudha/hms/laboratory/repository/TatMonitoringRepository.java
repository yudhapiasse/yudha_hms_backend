package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.TatMonitoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TatMonitoring entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface TatMonitoringRepository extends JpaRepository<TatMonitoring, UUID> {

    /**
     * Find by order
     */
    Optional<TatMonitoring> findByOrderId(UUID orderId);

    /**
     * Find by test
     */
    List<TatMonitoring> findByTestIdOrderByOrderPlacedAtDesc(UUID testId);

    /**
     * Find delayed TATs
     */
    @Query("SELECT t FROM TatMonitoring t WHERE t.delayed = true ORDER BY t.orderPlacedAt DESC")
    Page<TatMonitoring> findDelayedTats(Pageable pageable);

    /**
     * Find TATs not met by priority
     */
    @Query("SELECT t FROM TatMonitoring t WHERE t.tatMet = false AND t.priority = :priority ORDER BY t.orderPlacedAt DESC")
    List<TatMonitoring> findTatsNotMetByPriority(@Param("priority") String priority);

    /**
     * Find TATs by delay category
     */
    List<TatMonitoring> findByDelayCategoryOrderByOrderPlacedAtDesc(String delayCategory);

    /**
     * Calculate average TAT for test in date range
     */
    @Query("SELECT AVG(t.totalTat) FROM TatMonitoring t WHERE t.testId = :testId AND t.orderPlacedAt BETWEEN :startDate AND :endDate")
    Double calculateAverageTatForTest(@Param("testId") UUID testId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate TAT compliance rate
     */
    @Query("SELECT CAST(SUM(CASE WHEN t.tatMet = true THEN 1 ELSE 0 END) AS double) / CAST(COUNT(t) AS double) * 100 FROM TatMonitoring t WHERE t.orderPlacedAt BETWEEN :startDate AND :endDate")
    Double calculateTatComplianceRate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find TATs in date range
     */
    @Query("SELECT t FROM TatMonitoring t WHERE t.orderPlacedAt BETWEEN :startDate AND :endDate ORDER BY t.orderPlacedAt DESC")
    Page<TatMonitoring> findTatsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Count delayed by category in date range
     */
    @Query("SELECT t.delayCategory, COUNT(t) FROM TatMonitoring t WHERE t.delayed = true AND t.orderPlacedAt BETWEEN :startDate AND :endDate GROUP BY t.delayCategory")
    List<Object[]> countDelaysByCategory(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
