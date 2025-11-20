package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Daily Encounter Report Response DTO.
 *
 * Provides daily encounter statistics and operational metrics.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyEncounterReportResponse {

    private LocalDate reportDate;

    // Total Encounters by Type
    private Integer totalEncounters;
    private Integer outpatientEncounters;
    private Integer inpatientEncounters;
    private Integer emergencyEncounters;

    // Length of Stay Metrics
    private Double averageLengthOfStayDays; // ALOS
    private Double medianLengthOfStayDays;
    private Integer totalInpatientDays;

    // Bed Metrics (for inpatient)
    private Integer totalBeds; // Total available beds
    private Integer occupiedBeds;
    private Double bedOccupancyRate; // BOR (percentage)
    private Double bedTurnoverRate; // BTR (admissions/beds)

    // Emergency Response Times
    private Double averageDoorToDoctorMinutes; // Emergency triage to doctor seen
    private Double averageTriageTimeMinutes;
    private Double emergencyAdmissionRate; // Percentage admitted

    // Status Distribution
    @Builder.Default
    private Map<String, Integer> encountersByStatus = new HashMap<>();

    // Insurance Distribution
    private Integer bpjsEncounters;
    private Integer privateInsuranceEncounters;
    private Integer selfPayEncounters;
    private Double bpjsPercentage;

    // Completion Metrics
    private Integer completedEncounters;
    private Integer inProgressEncounters;
    private Integer cancelledEncounters;
    private Double completionRate; // Percentage
}
