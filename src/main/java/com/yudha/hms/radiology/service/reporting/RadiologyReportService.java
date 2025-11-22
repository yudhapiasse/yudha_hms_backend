package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.constant.reporting.ReportStatus;
import com.yudha.hms.radiology.entity.reporting.RadiologyReport;
import com.yudha.hms.radiology.repository.reporting.RadiologyReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RadiologyReportService {

    private final RadiologyReportRepository radiologyReportRepository;

    @Transactional
    public RadiologyReport createReport(RadiologyReport report) {
        log.info("Creating radiology report for study: {}", report.getStudyId());
        
        if (radiologyReportRepository.existsByReportNumber(report.getReportNumber())) {
            throw new IllegalArgumentException("Report number already exists: " + report.getReportNumber());
        }
        
        report.setReportStatus(ReportStatus.DRAFT);
        report.setReportedAt(LocalDateTime.now());
        
        return radiologyReportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public RadiologyReport getReportById(UUID id) {
        return radiologyReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + id));
    }

    @Transactional(readOnly = true)
    public RadiologyReport getReportByNumber(String reportNumber) {
        return radiologyReportRepository.findByReportNumber(reportNumber)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportNumber));
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getReportsByStudy(UUID studyId) {
        return radiologyReportRepository.findByStudyId(studyId);
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getReportsByPatient(UUID patientId) {
        return radiologyReportRepository.findByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getReportsByStatus(ReportStatus status) {
        return radiologyReportRepository.findByReportStatus(status);
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getReportsByRadiologistAndStatus(UUID reportedBy, ReportStatus status) {
        return radiologyReportRepository.findByReportedByAndReportStatus(reportedBy, status);
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getCriticalFindingReports() {
        return radiologyReportRepository.findByHasCriticalFindingsTrue();
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getReportsByStatusAndDateRange(
            ReportStatus status, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        return radiologyReportRepository.findByStatusAndDateRange(status, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<RadiologyReport> getReportsByRadiologistAndDateRange(
            UUID reportedBy,
            LocalDate startDate,
            LocalDate endDate) {
        return radiologyReportRepository.findByRadiologistAndDateRange(reportedBy, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public long countReportsByStatus(ReportStatus status) {
        return radiologyReportRepository.countByStatus(status);
    }

    @Transactional
    public RadiologyReport updateReport(UUID id, RadiologyReport updatedReport) {
        log.info("Updating radiology report: {}", id);
        
        RadiologyReport existingReport = getReportById(id);
        
        if (existingReport.getReportStatus() == ReportStatus.VERIFIED) {
            throw new IllegalStateException("Cannot update verified report. Create an amendment instead.");
        }
        
        existingReport.setFindings(updatedReport.getFindings());
        existingReport.setFindingsText(updatedReport.getFindingsText());
        existingReport.setImpression(updatedReport.getImpression());
        existingReport.setRecommendations(updatedReport.getRecommendations());
        existingReport.setTechnique(updatedReport.getTechnique());
        existingReport.setReportComplexity(updatedReport.getReportComplexity());
        existingReport.setHasCriticalFindings(updatedReport.getHasCriticalFindings());
        
        return radiologyReportRepository.save(existingReport);
    }

    @Transactional
    public RadiologyReport markAsPreliminary(UUID id) {
        log.info("Marking report as preliminary: {}", id);

        RadiologyReport report = getReportById(id);
        report.setReportStatus(ReportStatus.PRELIMINARY);
        report.setReportedAt(LocalDateTime.now());

        return radiologyReportRepository.save(report);
    }

    @Transactional
    public RadiologyReport verifyReport(UUID id, UUID verifyingRadiologistId) {
        log.info("Verifying report: {}", id);
        
        RadiologyReport report = getReportById(id);
        
        if (report.getReportStatus() != ReportStatus.PRELIMINARY && 
            report.getReportStatus() != ReportStatus.DRAFT) {
            throw new IllegalStateException("Only preliminary or draft reports can be verified");
        }
        
        report.setReportStatus(ReportStatus.VERIFIED);
        report.setVerifiedBy(verifyingRadiologistId);
        report.setVerifiedAt(LocalDateTime.now());
        
        return radiologyReportRepository.save(report);
    }

    @Transactional
    public RadiologyReport cancelReport(UUID id, String cancellationReason) {
        log.info("Cancelling report: {}", id);

        RadiologyReport report = getReportById(id);
        report.setReportStatus(ReportStatus.CANCELLED);
        report.setCancelled(true);
        report.setCancellationReason(cancellationReason);
        report.setCancelledAt(LocalDateTime.now());

        return radiologyReportRepository.save(report);
    }

    @Transactional
    public void deleteReport(UUID id) {
        log.info("Soft deleting report: {}", id);
        RadiologyReport report = getReportById(id);
        report.setDeletedAt(LocalDateTime.now());
        radiologyReportRepository.save(report);
    }
}
