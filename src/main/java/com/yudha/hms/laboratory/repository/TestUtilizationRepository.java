package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.TestUtilization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TestUtilization entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface TestUtilizationRepository extends JpaRepository<TestUtilization, UUID> {

    /**
     * Find by test and period
     */
    Optional<TestUtilization> findByTestIdAndPeriodTypeAndPeriodStartAndPeriodEnd(
            UUID testId, String periodType, LocalDate periodStart, LocalDate periodEnd);

    /**
     * Find by test
     */
    List<TestUtilization> findByTestIdOrderByPeriodStartDesc(UUID testId);

    /**
     * Find by period
     */
    List<TestUtilization> findByPeriodTypeAndPeriodStartAndPeriodEndOrderByTotalOrdersDesc(
            String periodType, LocalDate periodStart, LocalDate periodEnd);

    /**
     * Find by category and period
     */
    List<TestUtilization> findByTestCategoryAndPeriodTypeAndPeriodStartAndPeriodEndOrderByTotalOrdersDesc(
            String testCategory, String periodType, LocalDate periodStart, LocalDate periodEnd);

    /**
     * Find top utilized tests in period
     */
    @Query("SELECT t FROM TestUtilization t WHERE t.periodType = :periodType AND t.periodStart = :periodStart AND t.periodEnd = :periodEnd ORDER BY t.totalOrders DESC LIMIT :limit")
    List<TestUtilization> findTopUtilizedTests(
            @Param("periodType") String periodType,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd,
            @Param("limit") int limit);

    /**
     * Calculate total revenue for period
     */
    @Query("SELECT SUM(t.totalRevenue) FROM TestUtilization t WHERE t.periodType = :periodType AND t.periodStart = :periodStart AND t.periodEnd = :periodEnd")
    Double calculateTotalRevenue(
            @Param("periodType") String periodType,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd);

    /**
     * Find tests with high rejection rates
     */
    @Query("SELECT t FROM TestUtilization t WHERE t.periodType = :periodType AND t.periodStart = :periodStart AND t.periodEnd = :periodEnd AND (CAST(t.rejectedSpecimens AS double) / CAST(t.totalOrders AS double)) > :threshold ORDER BY t.rejectedSpecimens DESC")
    List<TestUtilization> findTestsWithHighRejectionRates(
            @Param("periodType") String periodType,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd,
            @Param("threshold") double threshold);

    /**
     * Get utilization summary by category
     */
    @Query("SELECT t.testCategory, SUM(t.totalOrders), SUM(t.completedTests), SUM(t.totalRevenue) FROM TestUtilization t WHERE t.periodType = :periodType AND t.periodStart = :periodStart AND t.periodEnd = :periodEnd GROUP BY t.testCategory")
    List<Object[]> getUtilizationSummaryByCategory(
            @Param("periodType") String periodType,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd);
}
