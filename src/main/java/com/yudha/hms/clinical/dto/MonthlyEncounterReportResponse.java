package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Monthly Encounter Report Response DTO.
 *
 * Provides monthly aggregated analytics and trends.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyEncounterReportResponse {

    private YearMonth reportMonth;

    // Total Encounters
    private Integer totalEncounters;
    private Double averageEncountersPerDay;

    // Top Diagnoses
    @Builder.Default
    private List<DiagnosisStatistic> topDiagnoses = new ArrayList<>();

    @Builder.Default
    private List<DiagnosisStatistic> topOutpatientDiagnoses = new ArrayList<>();

    @Builder.Default
    private List<DiagnosisStatistic> topInpatientDiagnoses = new ArrayList<>();

    @Builder.Default
    private List<DiagnosisStatistic> topEmergencyDiagnoses = new ArrayList<>();

    // Readmission Analysis
    private Integer totalReadmissions;
    private Integer readmissionsWithin30Days;
    private Double readmissionRate; // Percentage

    // Cost Analysis (placeholder for billing integration)
    private Double averageCostPerEncounter;
    private Double totalRevenue;

    // Insurance Mix
    private Integer bpjsEncounters;
    private Integer privateInsuranceEncounters;
    private Integer selfPayEncounters;
    private Double bpjsPercentage;
    private Double privateInsurancePercentage;
    private Double selfPayPercentage;

    // Doctor Productivity
    @Builder.Default
    private List<DoctorProductivityStatistic> doctorProductivity = new ArrayList<>();

    // Encounter Type Distribution
    @Builder.Default
    private Map<String, Integer> encountersByType = new HashMap<>();

    // Monthly Trends
    @Builder.Default
    private List<DailyTrendData> dailyTrends = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisStatistic {
        private String diagnosisCode;
        private String diagnosisText;
        private Integer count;
        private Double percentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorProductivityStatistic {
        private String doctorId;
        private String doctorName;
        private String specialty;
        private Integer totalEncounters;
        private Double averageEncountersPerDay;
        private Double averageServiceTimeMinutes;
        private Integer outpatientCount;
        private Integer inpatientCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyTrendData {
        private Integer dayOfMonth;
        private Integer encounterCount;
        private Integer outpatientCount;
        private Integer inpatientCount;
        private Integer emergencyCount;
    }
}
