package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Performance Indicators Response DTO.
 *
 * Key performance indicators for encounter management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceIndicatorsResponse {

    private LocalDate startDate;
    private LocalDate endDate;

    // Emergency Department Metrics
    private Double averageDoorToDoctorTimeMinutes; // Target: <30 min
    private Double medianDoorToDoctorTimeMinutes;
    private Integer emergencyEncountersTotal;
    private Double emergencyAdmissionRate; // Percentage

    // Inpatient Metrics
    private Double averageTimeToAdmissionHours; // From ED to admission
    private Double medianTimeToAdmissionHours;
    private Integer totalAdmissions;

    // Discharge Metrics
    private Integer totalDischarges;
    private Integer dischargesBeforeNoon;
    private Double dischargeBeforeNoonRate; // Percentage - Target: >50%
    private Double averageDischargeProcessingTimeHours;

    // Queue Performance
    private Double averageQueueWaitingTimeMinutes;
    private Double medianQueueWaitingTimeMinutes;
    private Integer totalPatientsQueued;

    // Patient Satisfaction (placeholder - requires survey integration)
    private Double patientSatisfactionScore; // Scale 1-5
    private Integer surveysCompleted;

    // Length of Stay
    private Double averageLengthOfStayDays;
    private Double medianLengthOfStayDays;

    // Bed Utilization
    private Double averageBedOccupancyRate; // Percentage
    private Double bedTurnoverRate;

    // Operational Efficiency
    private Double averageEncounterDurationHours;
    private Integer encountersCompleted;
    private Integer encountersCancelled;
    private Double cancellationRate; // Percentage

    // Readmissions
    private Integer readmissionsWithin30Days;
    private Double readmissionRate; // Percentage - Target: <15%

    // Targets Met (Boolean indicators)
    private Boolean doorToDoctorTargetMet; // <30 minutes
    private Boolean dischargeBeforeNoonTargetMet; // >50%
    private Boolean readmissionTargetMet; // <15%
    private Boolean bedOccupancyTargetMet; // 75-85%
}
