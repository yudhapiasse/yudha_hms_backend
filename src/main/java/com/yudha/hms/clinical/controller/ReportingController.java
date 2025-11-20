package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.DailyEncounterReportResponse;
import com.yudha.hms.clinical.dto.MonthlyEncounterReportResponse;
import com.yudha.hms.clinical.dto.PerformanceIndicatorsResponse;
import com.yudha.hms.clinical.service.EncounterAnalyticsService;
import com.yudha.hms.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Reporting Controller.
 *
 * Provides analytics and reporting endpoints for encounter management.
 * Includes daily reports, monthly reports, and performance indicators.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ReportingController {

    private final EncounterAnalyticsService analyticsService;

    /**
     * Get daily encounter report.
     *
     * Provides operational metrics for a specific date including:
     * - Total encounters by type (outpatient, inpatient, emergency)
     * - Length of stay metrics (ALOS, median LOS)
     * - Bed utilization (BOR, BTR)
     * - Emergency response times (door-to-doctor, triage)
     * - Status and insurance distribution
     *
     * @param date The report date (defaults to today if not specified)
     * @return Daily encounter report
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyEncounterReportResponse>> getDailyReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        LocalDate reportDate = date != null ? date : LocalDate.now();
        log.info("Fetching daily encounter report for date: {}", reportDate);

        DailyEncounterReportResponse report = analyticsService.getDailyReport(reportDate);

        log.info("Daily report generated successfully for {}: {} total encounters",
                reportDate, report.getTotalEncounters());

        return ResponseEntity.ok(ApiResponse.success(
                "Daily encounter report retrieved successfully",
                report
        ));
    }

    /**
     * Get monthly encounter report.
     *
     * Provides aggregated analytics for a specific month including:
     * - Top diagnoses by encounter type
     * - Readmission rates and analysis
     * - Insurance mix and financial metrics
     * - Doctor productivity statistics
     * - Daily trends throughout the month
     *
     * @param yearMonth The report month in YYYY-MM format (defaults to current month)
     * @return Monthly encounter report
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MonthlyEncounterReportResponse>> getMonthlyReport(
            @RequestParam(required = false)
            String yearMonth
    ) {
        YearMonth reportMonth;
        if (yearMonth != null && !yearMonth.isBlank()) {
            reportMonth = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        } else {
            reportMonth = YearMonth.now();
        }

        log.info("Fetching monthly encounter report for: {}", reportMonth);

        MonthlyEncounterReportResponse report = analyticsService.getMonthlyReport(reportMonth);

        log.info("Monthly report generated successfully for {}: {} total encounters, {} readmissions",
                reportMonth, report.getTotalEncounters(), report.getTotalReadmissions());

        return ResponseEntity.ok(ApiResponse.success(
                "Monthly encounter report retrieved successfully",
                report
        ));
    }

    /**
     * Get performance indicators.
     *
     * Provides key performance indicators (KPIs) for a date range including:
     * - Emergency department metrics (door-to-doctor time, admission rate)
     * - Inpatient metrics (time to admission)
     * - Discharge metrics (before noon rate, processing time)
     * - Queue performance (average waiting time)
     * - Readmission rates
     * - Target achievement indicators
     *
     * @param startDate Start date of the reporting period
     * @param endDate End date of the reporting period (defaults to today)
     * @return Performance indicators report
     */
    @GetMapping("/performance-indicators")
    public ResponseEntity<ApiResponse<PerformanceIndicatorsResponse>> getPerformanceIndicators(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        LocalDate reportEndDate = endDate != null ? endDate : LocalDate.now();

        log.info("Fetching performance indicators from {} to {}", startDate, reportEndDate);

        PerformanceIndicatorsResponse indicators =
                analyticsService.getPerformanceIndicators(startDate, reportEndDate);

        log.info("Performance indicators generated successfully. Targets met - " +
                        "Door-to-Doctor: {}, Discharge before noon: {}, Readmission: {}",
                indicators.getDoorToDoctorTargetMet(),
                indicators.getDischargeBeforeNoonTargetMet(),
                indicators.getReadmissionTargetMet());

        return ResponseEntity.ok(ApiResponse.success(
                "Performance indicators retrieved successfully",
                indicators
        ));
    }

    /**
     * Get weekly performance summary.
     *
     * Convenience endpoint for last 7 days performance indicators.
     *
     * @return Performance indicators for the last week
     */
    @GetMapping("/weekly-summary")
    public ResponseEntity<ApiResponse<PerformanceIndicatorsResponse>> getWeeklySummary() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Last 7 days including today

        log.info("Fetching weekly performance summary (last 7 days)");

        PerformanceIndicatorsResponse indicators =
                analyticsService.getPerformanceIndicators(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(
                "Weekly performance summary retrieved successfully",
                indicators
        ));
    }

    /**
     * Get monthly performance summary.
     *
     * Convenience endpoint for current month performance indicators.
     *
     * @return Performance indicators for the current month
     */
    @GetMapping("/monthly-summary")
    public ResponseEntity<ApiResponse<PerformanceIndicatorsResponse>> getMonthlySummary() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        log.info("Fetching monthly performance summary for current month");

        PerformanceIndicatorsResponse indicators =
                analyticsService.getPerformanceIndicators(startOfMonth, today);

        return ResponseEntity.ok(ApiResponse.success(
                "Monthly performance summary retrieved successfully",
                indicators
        ));
    }

    /**
     * Get yesterday's report.
     *
     * Convenience endpoint for yesterday's daily report.
     *
     * @return Daily report for yesterday
     */
    @GetMapping("/yesterday")
    public ResponseEntity<ApiResponse<DailyEncounterReportResponse>> getYesterdayReport() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Fetching yesterday's report: {}", yesterday);

        DailyEncounterReportResponse report = analyticsService.getDailyReport(yesterday);

        return ResponseEntity.ok(ApiResponse.success(
                "Yesterday's encounter report retrieved successfully",
                report
        ));
    }

    /**
     * Get current month report.
     *
     * Convenience endpoint for current month's report.
     *
     * @return Monthly report for current month
     */
    @GetMapping("/current-month")
    public ResponseEntity<ApiResponse<MonthlyEncounterReportResponse>> getCurrentMonthReport() {
        YearMonth currentMonth = YearMonth.now();
        log.info("Fetching current month report: {}", currentMonth);

        MonthlyEncounterReportResponse report = analyticsService.getMonthlyReport(currentMonth);

        return ResponseEntity.ok(ApiResponse.success(
                "Current month encounter report retrieved successfully",
                report
        ));
    }

    /**
     * Get previous month report.
     *
     * Convenience endpoint for previous month's report.
     *
     * @return Monthly report for previous month
     */
    @GetMapping("/previous-month")
    public ResponseEntity<ApiResponse<MonthlyEncounterReportResponse>> getPreviousMonthReport() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        log.info("Fetching previous month report: {}", previousMonth);

        MonthlyEncounterReportResponse report = analyticsService.getMonthlyReport(previousMonth);

        return ResponseEntity.ok(ApiResponse.success(
                "Previous month encounter report retrieved successfully",
                report
        ));
    }
}
