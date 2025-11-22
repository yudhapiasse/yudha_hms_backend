package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.entity.reporting.ReportStatistics;
import com.yudha.hms.radiology.repository.reporting.ReportStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportStatisticsService {

    private final ReportStatisticsRepository reportStatisticsRepository;

    @Transactional
    public ReportStatistics createOrUpdateStatistics(ReportStatistics statistics) {
        log.info("Creating/updating statistics for period: {} - {}", 
                statistics.getPeriodStartDate(), statistics.getPeriodEndDate());
        
        Optional<ReportStatistics> existing = reportStatisticsRepository
                .findByPeriodTypeAndPeriodStartDateAndPeriodEndDateAndRadiologistId(
                        statistics.getPeriodType(),
                        statistics.getPeriodStartDate(),
                        statistics.getPeriodEndDate(),
                        statistics.getRadiologistId()
                );
        
        if (existing.isPresent()) {
            ReportStatistics existingStats = existing.get();
            updateStatisticsFields(existingStats, statistics);
            return reportStatisticsRepository.save(existingStats);
        }
        
        statistics.setComputedAt(LocalDateTime.now());
        return reportStatisticsRepository.save(statistics);
    }

    @Transactional(readOnly = true)
    public ReportStatistics getStatisticsById(UUID id) {
        return reportStatisticsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Statistics not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReportStatistics> getStatisticsByPeriodType(String periodType) {
        return reportStatisticsRepository.findByPeriodType(periodType);
    }

    @Transactional(readOnly = true)
    public List<ReportStatistics> getStatisticsByRadiologist(UUID radiologistId) {
        return reportStatisticsRepository.findByRadiologistId(radiologistId);
    }

    @Transactional(readOnly = true)
    public List<ReportStatistics> getStatisticsByDepartment(UUID departmentId) {
        return reportStatisticsRepository.findByDepartmentId(departmentId);
    }

    @Transactional(readOnly = true)
    public List<ReportStatistics> getStatisticsByModality(String modalityCode) {
        return reportStatisticsRepository.findByModalityCode(modalityCode);
    }

    @Transactional(readOnly = true)
    public List<ReportStatistics> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reportStatisticsRepository.findByDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<ReportStatistics> getStatisticsByPeriodTypeAndDateRange(
            String periodType,
            LocalDate startDate,
            LocalDate endDate) {
        return reportStatisticsRepository.findByPeriodTypeAndDateRange(periodType, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<ReportStatistics> getStatisticsByRadiologistAndPeriodType(
            UUID radiologistId,
            String periodType,
            LocalDate startDate) {
        return reportStatisticsRepository.findByRadiologistAndPeriodTypeAfterDate(
                radiologistId, 
                periodType, 
                startDate
        );
    }

    @Transactional(readOnly = true)
    public Long getTotalReportsByPeriodTypeAndDateRange(
            String periodType,
            LocalDate startDate,
            LocalDate endDate) {
        return reportStatisticsRepository.sumTotalReportsByPeriodTypeAndDateRange(
                periodType, 
                startDate, 
                endDate
        );
    }

    @Transactional
    public void deleteStatistics(UUID id) {
        log.info("Soft deleting statistics: {}", id);
        ReportStatistics statistics = getStatisticsById(id);
        statistics.setDeletedAt(LocalDateTime.now());
        reportStatisticsRepository.save(statistics);
    }

    private void updateStatisticsFields(ReportStatistics existing, ReportStatistics updated) {
        existing.setTotalReports(updated.getTotalReports());
        existing.setPreliminaryReports(updated.getPreliminaryReports());
        existing.setFinalReports(updated.getFinalReports());
        existing.setAmendedReports(updated.getAmendedReports());
        existing.setAvgReportingTimeMinutes(updated.getAvgReportingTimeMinutes());
        existing.setMedianReportingTimeMinutes(updated.getMedianReportingTimeMinutes());
        existing.setReportsWithin24Hours(updated.getReportsWithin24Hours());
        existing.setReportsOver24Hours(updated.getReportsOver24Hours());
        existing.setCriticalFindingsCount(updated.getCriticalFindingsCount());
        existing.setCriticalFindingsNotified(updated.getCriticalFindingsNotified());
        existing.setAvgNotificationTimeMinutes(updated.getAvgNotificationTimeMinutes());
        existing.setAmendmentRate(updated.getAmendmentRate());
        existing.setAddendumCount(updated.getAddendumCount());
        existing.setCorrectionCount(updated.getCorrectionCount());
        existing.setSimpleReports(updated.getSimpleReports());
        existing.setModerateReports(updated.getModerateReports());
        existing.setComplexReports(updated.getComplexReports());
        existing.setTranscribedReports(updated.getTranscribedReports());
        existing.setTranscriptionSuccessRate(updated.getTranscriptionSuccessRate());
        existing.setReportsDistributed(updated.getReportsDistributed());
        existing.setAvgDistributionTimeMinutes(updated.getAvgDistributionTimeMinutes());
        existing.setFailedDistributions(updated.getFailedDistributions());
        existing.setReportsWithComparison(updated.getReportsWithComparison());
        existing.setTemplateUsage(updated.getTemplateUsage());
        existing.setComputedAt(LocalDateTime.now());
        existing.setComputedBy(updated.getComputedBy());
    }
}
