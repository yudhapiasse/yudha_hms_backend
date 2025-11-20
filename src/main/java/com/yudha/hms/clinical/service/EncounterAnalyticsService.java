package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.*;
import com.yudha.hms.registration.entity.OutpatientRegistration;
import com.yudha.hms.registration.entity.QueueStatus;
import com.yudha.hms.registration.repository.OutpatientRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Encounter Analytics Service.
 *
 * Provides comprehensive reporting and analytics for encounter management:
 * - Daily operational reports
 * - Monthly trend analysis
 * - Performance indicators and KPIs
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EncounterAnalyticsService {

    private final EncounterRepository encounterRepository;
    private final EncounterDiagnosisRepository diagnosisRepository;
    private final OutpatientRegistrationRepository outpatientRegistrationRepository;

    /**
     * Generate daily encounter report.
     *
     * @param date Report date
     * @return Daily report with operational metrics
     */
    public DailyEncounterReportResponse getDailyReport(LocalDate date) {
        log.info("Generating daily encounter report for: {}", date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Fetch all encounters for the day
        List<Encounter> encounters = encounterRepository.findByEncounterStartBetween(startOfDay, endOfDay);

        // Calculate metrics
        int total = encounters.size();
        long outpatient = encounters.stream().filter(e -> e.getEncounterType() == EncounterType.OUTPATIENT).count();
        long inpatient = encounters.stream().filter(e -> e.getEncounterType() == EncounterType.INPATIENT).count();
        long emergency = encounters.stream().filter(e -> e.getEncounterType() == EncounterType.EMERGENCY).count();

        // Length of stay for inpatient
        Double avgLOS = calculateAverageLengthOfStay(encounters);
        Double medianLOS = calculateMedianLengthOfStay(encounters);

        // Bed metrics (placeholder - requires bed management integration)
        Integer totalBeds = 100; // TODO: Fetch from bed management
        Integer occupiedBeds = (int) inpatient;
        Double bedOccupancy = totalBeds > 0 ? (occupiedBeds * 100.0 / totalBeds) : 0.0;
        Double bedTurnover = totalBeds > 0 ? (inpatient * 1.0 / totalBeds) : 0.0;

        // Emergency response times
        Double avgDoorToDoctor = calculateAverageDoorToDoctorTime(encounters);
        Double emergencyAdmissionRate = emergency > 0 ?
            (encounters.stream().filter(e -> e.getEncounterType() == EncounterType.EMERGENCY &&
                                             e.getStatus() == EncounterStatus.FINISHED).count() * 100.0 / emergency) : 0.0;

        // Status distribution
        Map<String, Integer> byStatus = encounters.stream()
            .collect(Collectors.groupingBy(e -> e.getStatus().name(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        // Insurance distribution
        long bpjs = encounters.stream().filter(e -> e.getInsuranceType() == InsuranceType.BPJS).count();
        long selfPay = encounters.stream().filter(e -> e.getInsuranceType() == InsuranceType.SELF_PAY).count();
        long insurance = total - bpjs - selfPay;
        Double bpjsPercent = total > 0 ? (bpjs * 100.0 / total) : 0.0;

        // Completion metrics
        long completed = encounters.stream().filter(e -> e.getStatus() == EncounterStatus.FINISHED).count();
        long inProgress = encounters.stream().filter(e -> e.getStatus() == EncounterStatus.IN_PROGRESS).count();
        long cancelled = encounters.stream().filter(e -> e.getStatus() == EncounterStatus.CANCELLED).count();
        Double completionRate = total > 0 ? (completed * 100.0 / total) : 0.0;

        return DailyEncounterReportResponse.builder()
            .reportDate(date)
            .totalEncounters(total)
            .outpatientEncounters((int) outpatient)
            .inpatientEncounters((int) inpatient)
            .emergencyEncounters((int) emergency)
            .averageLengthOfStayDays(avgLOS)
            .medianLengthOfStayDays(medianLOS)
            .totalInpatientDays(calculateTotalInpatientDays(encounters))
            .totalBeds(totalBeds)
            .occupiedBeds(occupiedBeds)
            .bedOccupancyRate(Math.round(bedOccupancy * 10.0) / 10.0)
            .bedTurnoverRate(Math.round(bedTurnover * 100.0) / 100.0)
            .averageDoorToDoctorMinutes(avgDoorToDoctor)
            .averageTriageTimeMinutes(calculateAverageTriageTime(encounters))
            .emergencyAdmissionRate(Math.round(emergencyAdmissionRate * 10.0) / 10.0)
            .encountersByStatus(byStatus)
            .bpjsEncounters((int) bpjs)
            .privateInsuranceEncounters((int) insurance)
            .selfPayEncounters((int) selfPay)
            .bpjsPercentage(Math.round(bpjsPercent * 10.0) / 10.0)
            .completedEncounters((int) completed)
            .inProgressEncounters((int) inProgress)
            .cancelledEncounters((int) cancelled)
            .completionRate(Math.round(completionRate * 10.0) / 10.0)
            .build();
    }

    /**
     * Generate monthly encounter report.
     *
     * @param yearMonth Report month
     * @return Monthly report with trends and analysis
     */
    public MonthlyEncounterReportResponse getMonthlyReport(YearMonth yearMonth) {
        log.info("Generating monthly encounter report for: {}", yearMonth);

        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<Encounter> encounters = encounterRepository.findByEncounterStartBetween(startOfMonth, endOfMonth);

        int total = encounters.size();
        int daysInMonth = yearMonth.lengthOfMonth();
        Double avgPerDay = total * 1.0 / daysInMonth;

        // Top diagnoses
        List<MonthlyEncounterReportResponse.DiagnosisStatistic> topDiagnoses = getTopDiagnoses(encounters, 10);
        List<MonthlyEncounterReportResponse.DiagnosisStatistic> topOutpatient =
            getTopDiagnosesByType(encounters, EncounterType.OUTPATIENT, 10);
        List<MonthlyEncounterReportResponse.DiagnosisStatistic> topInpatient =
            getTopDiagnosesByType(encounters, EncounterType.INPATIENT, 10);
        List<MonthlyEncounterReportResponse.DiagnosisStatistic> topEmergency =
            getTopDiagnosesByType(encounters, EncounterType.EMERGENCY, 10);

        // Readmission analysis
        Map<String, Object> readmissionStats = calculateReadmissions(encounters);

        // Insurance mix
        long bpjs = encounters.stream().filter(e -> e.getInsuranceType() == InsuranceType.BPJS).count();
        long selfPay = encounters.stream().filter(e -> e.getInsuranceType() == InsuranceType.SELF_PAY).count();
        long insurance = total - bpjs - selfPay;

        // Doctor productivity
        List<MonthlyEncounterReportResponse.DoctorProductivityStatistic> doctorProductivity =
            calculateDoctorProductivity(encounters, daysInMonth);

        // Encounter type distribution
        Map<String, Integer> byType = encounters.stream()
            .collect(Collectors.groupingBy(e -> e.getEncounterType().name(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        // Daily trends
        List<MonthlyEncounterReportResponse.DailyTrendData> dailyTrends = calculateDailyTrends(encounters, yearMonth);

        return MonthlyEncounterReportResponse.builder()
            .reportMonth(yearMonth)
            .totalEncounters(total)
            .averageEncountersPerDay(Math.round(avgPerDay * 10.0) / 10.0)
            .topDiagnoses(topDiagnoses)
            .topOutpatientDiagnoses(topOutpatient)
            .topInpatientDiagnoses(topInpatient)
            .topEmergencyDiagnoses(topEmergency)
            .totalReadmissions((Integer) readmissionStats.get("total"))
            .readmissionsWithin30Days((Integer) readmissionStats.get("within30"))
            .readmissionRate((Double) readmissionStats.get("rate"))
            .averageCostPerEncounter(0.0) // TODO: Integrate with billing
            .totalRevenue(0.0) // TODO: Integrate with billing
            .bpjsEncounters((int) bpjs)
            .privateInsuranceEncounters((int) insurance)
            .selfPayEncounters((int) selfPay)
            .bpjsPercentage(total > 0 ? Math.round(bpjs * 1000.0 / total) / 10.0 : 0.0)
            .privateInsurancePercentage(total > 0 ? Math.round(insurance * 1000.0 / total) / 10.0 : 0.0)
            .selfPayPercentage(total > 0 ? Math.round(selfPay * 1000.0 / total) / 10.0 : 0.0)
            .doctorProductivity(doctorProductivity)
            .encountersByType(byType)
            .dailyTrends(dailyTrends)
            .build();
    }

    /**
     * Get performance indicators for a date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Performance indicators and KPIs
     */
    public PerformanceIndicatorsResponse getPerformanceIndicators(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating performance indicators from {} to {}", startDate, endDate);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Encounter> encounters = encounterRepository.findByEncounterStartBetween(start, end);
        List<OutpatientRegistration> registrations = outpatientRegistrationRepository
            .findByDateRange(startDate, endDate);

        // Emergency metrics
        List<Encounter> emergencyEncounters = encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.EMERGENCY)
            .collect(Collectors.toList());
        Double avgDoorToDoctor = calculateAverageDoorToDoctorTime(emergencyEncounters);
        Double medianDoorToDoctor = calculateMedianDoorToDoctorTime(emergencyEncounters);
        long emergencyAdmitted = emergencyEncounters.stream()
            .filter(e -> e.getStatus() == EncounterStatus.FINISHED).count();
        Double emergencyAdmissionRate = emergencyEncounters.size() > 0 ?
            (emergencyAdmitted * 100.0 / emergencyEncounters.size()) : 0.0;

        // Inpatient metrics
        List<Encounter> inpatientEncounters = encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.INPATIENT)
            .collect(Collectors.toList());
        Double avgTimeToAdmission = calculateAverageTimeToAdmission(inpatientEncounters);

        // Discharge metrics
        long totalDischarges = encounters.stream()
            .filter(e -> e.getDischargeDate() != null).count();
        long dischargesBeforeNoon = encounters.stream()
            .filter(e -> e.getDischargeDate() != null && e.getDischargeDate().getHour() < 12).count();
        Double dischargeBeforeNoonRate = totalDischarges > 0 ? (dischargesBeforeNoon * 100.0 / totalDischarges) : 0.0;

        // Queue performance
        Double avgQueueWait = calculateAverageQueueWaitTime(registrations);

        // Length of stay
        Double avgLOS = calculateAverageLengthOfStay(encounters);
        Double medianLOS = calculateMedianLengthOfStay(encounters);

        // Readmissions
        Map<String, Object> readmissionStats = calculateReadmissions(encounters);

        // Operational efficiency
        long completed = encounters.stream().filter(e -> e.getStatus() == EncounterStatus.FINISHED).count();
        long cancelled = encounters.stream().filter(e -> e.getStatus() == EncounterStatus.CANCELLED).count();
        Double cancellationRate = encounters.size() > 0 ? (cancelled * 100.0 / encounters.size()) : 0.0;

        // Target achievement
        boolean doorToDoctorTarget = avgDoorToDoctor != null && avgDoorToDoctor < 30.0;
        boolean dischargeTarget = dischargeBeforeNoonRate >= 50.0;
        boolean readmissionTarget = (Double) readmissionStats.get("rate") < 15.0;

        return PerformanceIndicatorsResponse.builder()
            .startDate(startDate)
            .endDate(endDate)
            .averageDoorToDoctorTimeMinutes(avgDoorToDoctor)
            .medianDoorToDoctorTimeMinutes(medianDoorToDoctor)
            .emergencyEncountersTotal(emergencyEncounters.size())
            .emergencyAdmissionRate(Math.round(emergencyAdmissionRate * 10.0) / 10.0)
            .averageTimeToAdmissionHours(avgTimeToAdmission)
            .medianTimeToAdmissionHours(null) // TODO: Implement
            .totalAdmissions(inpatientEncounters.size())
            .totalDischarges((int) totalDischarges)
            .dischargesBeforeNoon((int) dischargesBeforeNoon)
            .dischargeBeforeNoonRate(Math.round(dischargeBeforeNoonRate * 10.0) / 10.0)
            .averageDischargeProcessingTimeHours(null) // TODO: Implement
            .averageQueueWaitingTimeMinutes(avgQueueWait)
            .medianQueueWaitingTimeMinutes(null) // TODO: Implement
            .totalPatientsQueued(registrations.size())
            .patientSatisfactionScore(null) // TODO: Integrate with survey system
            .surveysCompleted(0)
            .averageLengthOfStayDays(avgLOS)
            .medianLengthOfStayDays(medianLOS)
            .averageBedOccupancyRate(null) // TODO: Integrate with bed management
            .bedTurnoverRate(null) // TODO: Integrate with bed management
            .averageEncounterDurationHours(calculateAverageEncounterDuration(encounters))
            .encountersCompleted((int) completed)
            .encountersCancelled((int) cancelled)
            .cancellationRate(Math.round(cancellationRate * 10.0) / 10.0)
            .readmissionsWithin30Days((Integer) readmissionStats.get("within30"))
            .readmissionRate((Double) readmissionStats.get("rate"))
            .doorToDoctorTargetMet(doorToDoctorTarget)
            .dischargeBeforeNoonTargetMet(dischargeTarget)
            .readmissionTargetMet(readmissionTarget)
            .bedOccupancyTargetMet(null) // TODO: Integrate with bed management
            .build();
    }

    // ========== Private Helper Methods ==========

    private Double calculateAverageLengthOfStay(List<Encounter> encounters) {
        return encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.INPATIENT)
            .filter(e -> e.getEncounterEnd() != null)
            .mapToDouble(e -> ChronoUnit.DAYS.between(e.getEncounterStart(), e.getEncounterEnd()))
            .average()
            .orElse(0.0);
    }

    private Double calculateMedianLengthOfStay(List<Encounter> encounters) {
        List<Long> los = encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.INPATIENT)
            .filter(e -> e.getEncounterEnd() != null)
            .map(e -> ChronoUnit.DAYS.between(e.getEncounterStart(), e.getEncounterEnd()))
            .sorted()
            .collect(Collectors.toList());

        if (los.isEmpty()) return 0.0;
        int middle = los.size() / 2;
        return los.size() % 2 == 0 ? (los.get(middle - 1) + los.get(middle)) / 2.0 : los.get(middle) * 1.0;
    }

    private Integer calculateTotalInpatientDays(List<Encounter> encounters) {
        return (int) encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.INPATIENT)
            .filter(e -> e.getEncounterEnd() != null)
            .mapToLong(e -> ChronoUnit.DAYS.between(e.getEncounterStart(), e.getEncounterEnd()))
            .sum();
    }

    private Double calculateAverageDoorToDoctorTime(List<Encounter> encounters) {
        return encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.EMERGENCY)
            .filter(e -> e.getEncounterStart() != null && e.getEncounterEnd() != null)
            .mapToLong(e -> Duration.between(e.getEncounterStart(),
                e.getEncounterStart().plusMinutes(30)).toMinutes()) // Simplified
            .average()
            .orElse(0.0);
    }

    private Double calculateMedianDoorToDoctorTime(List<Encounter> encounters) {
        List<Long> times = encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.EMERGENCY)
            .map(e -> 30L) // Simplified
            .sorted()
            .collect(Collectors.toList());

        if (times.isEmpty()) return null;
        int middle = times.size() / 2;
        return times.size() % 2 == 0 ? (times.get(middle - 1) + times.get(middle)) / 2.0 : times.get(middle) * 1.0;
    }

    private Double calculateAverageTriageTime(List<Encounter> encounters) {
        return 15.0; // Placeholder
    }

    private List<MonthlyEncounterReportResponse.DiagnosisStatistic> getTopDiagnoses(List<Encounter> encounters, int limit) {
        Map<String, Long> diagnosisCounts = new HashMap<>();

        for (Encounter encounter : encounters) {
            List<EncounterDiagnosis> diagnoses = diagnosisRepository.findByEncounter(encounter);
            for (EncounterDiagnosis diagnosis : diagnoses) {
                diagnosisCounts.merge(diagnosis.getDiagnosisCode() + "|" + diagnosis.getDiagnosisText(), 1L, Long::sum);
            }
        }

        return diagnosisCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                String[] parts = entry.getKey().split("\\|");
                double percentage = encounters.size() > 0 ? (entry.getValue() * 100.0 / encounters.size()) : 0.0;
                return MonthlyEncounterReportResponse.DiagnosisStatistic.builder()
                    .diagnosisCode(parts[0])
                    .diagnosisText(parts.length > 1 ? parts[1] : "")
                    .count(entry.getValue().intValue())
                    .percentage(Math.round(percentage * 10.0) / 10.0)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private List<MonthlyEncounterReportResponse.DiagnosisStatistic> getTopDiagnosesByType(
            List<Encounter> encounters, EncounterType type, int limit) {
        List<Encounter> filtered = encounters.stream()
            .filter(e -> e.getEncounterType() == type)
            .collect(Collectors.toList());
        return getTopDiagnoses(filtered, limit);
    }

    private Map<String, Object> calculateReadmissions(List<Encounter> encounters) {
        // Simplified readmission calculation
        long totalReadmissions = 0;
        long within30Days = 0;

        // TODO: Implement actual readmission detection logic from VisitHistoryService

        double rate = encounters.size() > 0 ? (within30Days * 100.0 / encounters.size()) : 0.0;

        Map<String, Object> result = new HashMap<>();
        result.put("total", (int) totalReadmissions);
        result.put("within30", (int) within30Days);
        result.put("rate", Math.round(rate * 10.0) / 10.0);
        return result;
    }

    private List<MonthlyEncounterReportResponse.DoctorProductivityStatistic> calculateDoctorProductivity(
            List<Encounter> encounters, int daysInMonth) {
        // Placeholder - requires participant data
        return new ArrayList<>();
    }

    private List<MonthlyEncounterReportResponse.DailyTrendData> calculateDailyTrends(
            List<Encounter> encounters, YearMonth yearMonth) {
        Map<Integer, List<Encounter>> byDay = encounters.stream()
            .collect(Collectors.groupingBy(e -> e.getEncounterStart().getDayOfMonth()));

        List<MonthlyEncounterReportResponse.DailyTrendData> trends = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            List<Encounter> dayEncounters = byDay.getOrDefault(day, Collections.emptyList());
            long outpatient = dayEncounters.stream().filter(e -> e.getEncounterType() == EncounterType.OUTPATIENT).count();
            long inpatient = dayEncounters.stream().filter(e -> e.getEncounterType() == EncounterType.INPATIENT).count();
            long emergency = dayEncounters.stream().filter(e -> e.getEncounterType() == EncounterType.EMERGENCY).count();

            trends.add(MonthlyEncounterReportResponse.DailyTrendData.builder()
                .dayOfMonth(day)
                .encounterCount(dayEncounters.size())
                .outpatientCount((int) outpatient)
                .inpatientCount((int) inpatient)
                .emergencyCount((int) emergency)
                .build());
        }
        return trends;
    }

    private Double calculateAverageTimeToAdmission(List<Encounter> encounters) {
        return null; // TODO: Calculate from ED arrival to admission
    }

    private Double calculateAverageQueueWaitTime(List<OutpatientRegistration> registrations) {
        return registrations.stream()
            .filter(r -> r.getQueueStatus() == QueueStatus.COMPLETED)
            .filter(r -> r.getQueueServingStartedAt() != null)
            .mapToDouble(r -> Duration.between(r.getRegistrationTime(), r.getQueueServingStartedAt()).toMinutes())
            .average()
            .orElse(0.0);
    }

    private Double calculateAverageEncounterDuration(List<Encounter> encounters) {
        return encounters.stream()
            .filter(e -> e.getEncounterEnd() != null)
            .mapToDouble(e -> Duration.between(e.getEncounterStart(), e.getEncounterEnd()).toHours())
            .average()
            .orElse(0.0);
    }
}
