package com.yudha.hms.laboratory.entity;

import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Test Utilization Entity.
 *
 * Track test utilization for statistics and reporting.
 * Aggregated data by period (daily, weekly, monthly, quarterly, yearly).
 * Includes utilization metrics, quality indicators, and financial data.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "test_utilization", schema = "laboratory_schema",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_test_utilization_period_test",
                        columnNames = {"period_type", "period_start", "period_end", "test_id"})
        },
        indexes = {
                @Index(name = "idx_test_utilization_test", columnList = "test_id"),
                @Index(name = "idx_test_utilization_period", columnList = "period_type, period_start, period_end"),
                @Index(name = "idx_test_utilization_category", columnList = "test_category")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TestUtilization extends BaseEntity {

    // ========== Period ==========

    /**
     * Period type (DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY)
     */
    @Column(name = "period_type", nullable = false, length = 50)
    private String periodType;

    /**
     * Period start date
     */
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    /**
     * Period end date
     */
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    // ========== Test Information ==========

    /**
     * Test ID
     */
    @Column(name = "test_id", nullable = false)
    private UUID testId;

    /**
     * Test name
     */
    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    /**
     * Test category
     */
    @Column(name = "test_category", length = 100)
    private String testCategory;

    // ========== Utilization Metrics ==========

    /**
     * Total orders
     */
    @Column(name = "total_orders", nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;

    /**
     * Completed tests
     */
    @Column(name = "completed_tests", nullable = false)
    @Builder.Default
    private Integer completedTests = 0;

    /**
     * Cancelled tests
     */
    @Column(name = "cancelled_tests", nullable = false)
    @Builder.Default
    private Integer cancelledTests = 0;

    // ========== By Priority ==========

    /**
     * Routine orders
     */
    @Column(name = "routine_orders", nullable = false)
    @Builder.Default
    private Integer routineOrders = 0;

    /**
     * Urgent orders
     */
    @Column(name = "urgent_orders", nullable = false)
    @Builder.Default
    private Integer urgentOrders = 0;

    /**
     * CITO orders
     */
    @Column(name = "cito_orders", nullable = false)
    @Builder.Default
    private Integer citoOrders = 0;

    // ========== By Patient Type ==========

    /**
     * Inpatient orders
     */
    @Column(name = "inpatient_orders", nullable = false)
    @Builder.Default
    private Integer inpatientOrders = 0;

    /**
     * Outpatient orders
     */
    @Column(name = "outpatient_orders", nullable = false)
    @Builder.Default
    private Integer outpatientOrders = 0;

    /**
     * Emergency orders
     */
    @Column(name = "emergency_orders", nullable = false)
    @Builder.Default
    private Integer emergencyOrders = 0;

    // ========== Financial ==========

    /**
     * Total revenue
     */
    @Column(name = "total_revenue", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    // ========== Quality Metrics ==========

    /**
     * Rejected specimens
     */
    @Column(name = "rejected_specimens", nullable = false)
    @Builder.Default
    private Integer rejectedSpecimens = 0;

    /**
     * Repeat tests (due to errors or quality issues)
     */
    @Column(name = "repeat_tests", nullable = false)
    @Builder.Default
    private Integer repeatTests = 0;

    /**
     * Critical values reported
     */
    @Column(name = "critical_values_reported", nullable = false)
    @Builder.Default
    private Integer criticalValuesReported = 0;

    // ========== TAT Metrics ==========

    /**
     * Average TAT in minutes
     */
    @Column(name = "average_tat_minutes", precision = 10, scale = 2)
    private BigDecimal averageTatMinutes;

    /**
     * Median TAT in minutes
     */
    @Column(name = "median_tat_minutes", precision = 10, scale = 2)
    private BigDecimal medianTatMinutes;

    /**
     * TAT compliance percentage (tests meeting TAT target)
     */
    @Column(name = "tat_compliance_percentage", precision = 5, scale = 2)
    private BigDecimal tatCompliancePercentage;

    // ========== Helper Methods ==========

    /**
     * Calculate completion rate
     */
    public BigDecimal getCompletionRate() {
        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completedTests)
                .divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate cancellation rate
     */
    public BigDecimal getCancellationRate() {
        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(cancelledTests)
                .divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate rejection rate
     */
    public BigDecimal getRejectionRate() {
        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(rejectedSpecimens)
                .divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate repeat rate
     */
    public BigDecimal getRepeatRate() {
        if (completedTests == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(repeatTests)
                .divide(BigDecimal.valueOf(completedTests), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate critical value rate
     */
    public BigDecimal getCriticalValueRate() {
        if (completedTests == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(criticalValuesReported)
                .divide(BigDecimal.valueOf(completedTests), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Increment total orders
     */
    public void incrementTotalOrders() {
        this.totalOrders++;
    }

    /**
     * Increment completed tests
     */
    public void incrementCompletedTests() {
        this.completedTests++;
    }

    /**
     * Add revenue
     */
    public void addRevenue(BigDecimal amount) {
        if (amount != null) {
            this.totalRevenue = this.totalRevenue.add(amount);
        }
    }
}
