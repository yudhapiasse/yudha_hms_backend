package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.constant.reporting.ComparisonChange;
import com.yudha.hms.radiology.entity.reporting.ReportComparison;
import com.yudha.hms.radiology.repository.reporting.ReportComparisonRepository;
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
public class ReportComparisonService {

    private final ReportComparisonRepository reportComparisonRepository;

    @Transactional
    public ReportComparison createComparison(ReportComparison comparison) {
        log.info("Creating comparison between reports: {} and {}", 
                comparison.getCurrentReportId(), comparison.getPriorReportId());
        
        Optional<ReportComparison> existing = reportComparisonRepository
                .findByCurrentReportIdAndPriorReportId(
                        comparison.getCurrentReportId(), 
                        comparison.getPriorReportId());
        
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Comparison already exists between these reports");
        }
        
        comparison.setComparisonDate(LocalDate.now());
        comparison.setComparedAt(LocalDateTime.now());
        
        return reportComparisonRepository.save(comparison);
    }

    @Transactional(readOnly = true)
    public ReportComparison getComparisonById(UUID id) {
        return reportComparisonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comparison not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReportComparison> getComparisonsByCurrentReport(UUID currentReportId) {
        return reportComparisonRepository.findByCurrentReportId(currentReportId);
    }

    @Transactional(readOnly = true)
    public List<ReportComparison> getComparisonsByPriorReport(UUID priorReportId) {
        return reportComparisonRepository.findByPriorReportId(priorReportId);
    }

    @Transactional(readOnly = true)
    public Optional<ReportComparison> getComparisonByReports(UUID currentReportId, UUID priorReportId) {
        return reportComparisonRepository.findByCurrentReportIdAndPriorReportId(currentReportId, priorReportId);
    }

    @Transactional(readOnly = true)
    public List<ReportComparison> getComparisonsByChange(ComparisonChange change) {
        return reportComparisonRepository.findByOverallChange(change);
    }

    @Transactional(readOnly = true)
    public List<ReportComparison> getComparisonsByUserAndDateRange(
            UUID userId, 
            LocalDate startDate, 
            LocalDate endDate) {
        return reportComparisonRepository.findByComparedByAndDateRange(userId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<ReportComparison> getAllComparisonsForReport(UUID reportId) {
        return reportComparisonRepository.findAllComparisonsForReport(reportId);
    }

    @Transactional(readOnly = true)
    public long countComparisonsByCurrentReport(UUID reportId) {
        return reportComparisonRepository.countByCurrentReportId(reportId);
    }

    @Transactional
    public ReportComparison updateComparison(UUID id, ReportComparison updatedComparison) {
        log.info("Updating comparison: {}", id);
        
        ReportComparison existingComparison = getComparisonById(id);
        
        existingComparison.setNewFindings(updatedComparison.getNewFindings());
        existingComparison.setResolvedFindings(updatedComparison.getResolvedFindings());
        existingComparison.setStableFindings(updatedComparison.getStableFindings());
        existingComparison.setProgressedFindings(updatedComparison.getProgressedFindings());
        existingComparison.setOverallChange(updatedComparison.getOverallChange());
        existingComparison.setChangeSummary(updatedComparison.getChangeSummary());
        existingComparison.setClinicalSignificance(updatedComparison.getClinicalSignificance());
        existingComparison.setSignificantChanges(updatedComparison.getSignificantChanges());
        existingComparison.setMeasurementsComparison(updatedComparison.getMeasurementsComparison());
        existingComparison.setFollowUpRecommendations(updatedComparison.getFollowUpRecommendations());
        existingComparison.setRecommendedIntervalDays(updatedComparison.getRecommendedIntervalDays());
        
        return reportComparisonRepository.save(existingComparison);
    }

    @Transactional
    public void deleteComparison(UUID id) {
        log.info("Soft deleting comparison: {}", id);
        ReportComparison comparison = getComparisonById(id);
        comparison.setDeletedAt(LocalDateTime.now());
        reportComparisonRepository.save(comparison);
    }
}
