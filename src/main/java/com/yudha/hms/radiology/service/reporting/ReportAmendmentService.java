package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.constant.reporting.AmendmentType;
import com.yudha.hms.radiology.constant.reporting.ReportStatus;
import com.yudha.hms.radiology.entity.reporting.RadiologyReport;
import com.yudha.hms.radiology.entity.reporting.ReportAmendment;
import com.yudha.hms.radiology.repository.reporting.RadiologyReportRepository;
import com.yudha.hms.radiology.repository.reporting.ReportAmendmentRepository;
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
public class ReportAmendmentService {

    private final ReportAmendmentRepository reportAmendmentRepository;
    private final RadiologyReportRepository radiologyReportRepository;

    @Transactional
    public ReportAmendment createAmendment(ReportAmendment amendment) {
        log.info("Creating amendment for report: {}", amendment.getReportId());
        
        RadiologyReport report = radiologyReportRepository.findById(amendment.getReportId())
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + amendment.getReportId()));
        
        if (report.getReportStatus() != ReportStatus.VERIFIED) {
            throw new IllegalStateException("Only verified reports can be amended");
        }
        
        long amendmentCount = reportAmendmentRepository.countByReportId(amendment.getReportId());
        amendment.setAmendmentNumber((int) (amendmentCount + 1));
        amendment.setAmendedAt(LocalDateTime.now());
        
        ReportAmendment savedAmendment = reportAmendmentRepository.save(amendment);
        
        report.setReportStatus(ReportStatus.AMENDED);
        radiologyReportRepository.save(report);
        
        log.info("Amendment created with number: {}", savedAmendment.getAmendmentNumber());
        return savedAmendment;
    }

    @Transactional(readOnly = true)
    public ReportAmendment getAmendmentById(UUID id) {
        return reportAmendmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Amendment not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReportAmendment> getAmendmentsByReport(UUID reportId) {
        return reportAmendmentRepository.findByReportIdOrderByAmendmentNumberAsc(reportId);
    }

    @Transactional(readOnly = true)
    public List<ReportAmendment> getAmendmentsByType(AmendmentType type) {
        return reportAmendmentRepository.findByAmendmentType(type);
    }

    @Transactional(readOnly = true)
    public List<ReportAmendment> getAmendmentsByReportAndType(UUID reportId, AmendmentType type) {
        return reportAmendmentRepository.findByReportIdAndAmendmentType(reportId, type);
    }

    @Transactional(readOnly = true)
    public List<ReportAmendment> getAmendmentsByUserAndDateRange(
            UUID userId, 
            LocalDate startDate, 
            LocalDate endDate) {
        return reportAmendmentRepository.findByAmendedByAndDateRange(userId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public long countAmendmentsByReport(UUID reportId) {
        return reportAmendmentRepository.countByReportId(reportId);
    }

    @Transactional
    public ReportAmendment verifyAmendment(UUID id, UUID verifiedBy) {
        log.info("Verifying amendment: {}", id);

        ReportAmendment amendment = getAmendmentById(id);
        amendment.setVerifiedBy(verifiedBy);
        amendment.setVerifiedAt(LocalDateTime.now());

        return reportAmendmentRepository.save(amendment);
    }

    @Transactional
    public void deleteAmendment(UUID id) {
        log.info("Soft deleting amendment: {}", id);
        ReportAmendment amendment = getAmendmentById(id);
        amendment.setDeletedAt(LocalDateTime.now());
        reportAmendmentRepository.save(amendment);
    }
}
