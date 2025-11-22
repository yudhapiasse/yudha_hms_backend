package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.constant.reporting.DistributionMethod;
import com.yudha.hms.radiology.constant.reporting.DistributionStatus;
import com.yudha.hms.radiology.entity.reporting.ReportDistribution;
import com.yudha.hms.radiology.repository.reporting.ReportDistributionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportDistributionService {

    private final ReportDistributionRepository reportDistributionRepository;

    @Transactional
    public ReportDistribution createDistribution(ReportDistribution distribution) {
        log.info("Creating distribution for report: {} via {}", 
                distribution.getReportId(), distribution.getDistributionMethod());
        
        distribution.setDistributionStatus(DistributionStatus.PENDING);
        
        return reportDistributionRepository.save(distribution);
    }

    @Transactional(readOnly = true)
    public ReportDistribution getDistributionById(UUID id) {
        return reportDistributionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Distribution not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getDistributionsByReport(UUID reportId) {
        return reportDistributionRepository.findByReportId(reportId);
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getDistributionsByStatus(DistributionStatus status) {
        return reportDistributionRepository.findByDistributionStatus(status);
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getDistributionsByMethod(DistributionMethod method) {
        return reportDistributionRepository.findByDistributionMethod(method);
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getDistributionsByRecipient(UUID recipientId) {
        return reportDistributionRepository.findByRecipientId(recipientId);
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getDistributionsByStatusAndMethod(
            DistributionStatus status, 
            DistributionMethod method) {
        return reportDistributionRepository.findByStatusAndMethod(status, method);
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getFailedWithRetryAvailable() {
        return reportDistributionRepository.findFailedWithRetryAvailable();
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getScheduledDistributions() {
        return reportDistributionRepository.findScheduledDistributions(
                LocalDateTime.now(), 
                DistributionStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<ReportDistribution> getDistributionsByReportAndStatus(UUID reportId, DistributionStatus status) {
        return reportDistributionRepository.findByReportIdAndStatus(reportId, status);
    }

    @Transactional(readOnly = true)
    public long countDistributionsByReport(UUID reportId) {
        return reportDistributionRepository.countByReportId(reportId);
    }

    @Transactional(readOnly = true)
    public long countDistributionsByStatus(DistributionStatus status) {
        return reportDistributionRepository.countByStatus(status);
    }

    @Transactional
    public ReportDistribution markAsSent(UUID id) {
        log.info("Marking distribution as sent: {}", id);
        
        ReportDistribution distribution = getDistributionById(id);
        distribution.setDistributionStatus(DistributionStatus.SENT);
        distribution.setSentAt(LocalDateTime.now());
        
        return reportDistributionRepository.save(distribution);
    }

    @Transactional
    public ReportDistribution markAsDelivered(UUID id) {
        log.info("Marking distribution as delivered: {}", id);
        
        ReportDistribution distribution = getDistributionById(id);
        distribution.setDistributionStatus(DistributionStatus.DELIVERED);
        distribution.setDeliveredAt(LocalDateTime.now());
        distribution.setDeliveryConfirmed(true);
        
        return reportDistributionRepository.save(distribution);
    }

    @Transactional
    public ReportDistribution markAsFailed(UUID id, String failureReason) {
        log.info("Marking distribution as failed: {}", id);
        
        ReportDistribution distribution = getDistributionById(id);
        distribution.setDistributionStatus(DistributionStatus.FAILED);
        distribution.setFailed(true);
        distribution.setFailureReason(failureReason);
        
        return reportDistributionRepository.save(distribution);
    }

    @Transactional
    public ReportDistribution retryDistribution(UUID id) {
        log.info("Retrying distribution: {}", id);
        
        ReportDistribution distribution = getDistributionById(id);
        
        if (distribution.getRetryCount() >= distribution.getMaxRetries()) {
            throw new IllegalStateException("Maximum retry attempts exceeded");
        }
        
        distribution.setRetryCount(distribution.getRetryCount() + 1);
        distribution.setLastRetryAt(LocalDateTime.now());
        distribution.setDistributionStatus(DistributionStatus.PENDING);
        distribution.setFailed(false);
        
        return reportDistributionRepository.save(distribution);
    }

    @Transactional
    public ReportDistribution recordPortalAccess(UUID id) {
        log.info("Recording portal access for distribution: {}", id);
        
        ReportDistribution distribution = getDistributionById(id);
        distribution.setPortalAccessed(true);
        distribution.setPortalAccessedAt(LocalDateTime.now());
        
        return reportDistributionRepository.save(distribution);
    }

    @Transactional
    public ReportDistribution recordReadReceipt(UUID id) {
        log.info("Recording read receipt for distribution: {}", id);
        
        ReportDistribution distribution = getDistributionById(id);
        distribution.setReadReceipt(true);
        distribution.setReadAt(LocalDateTime.now());
        
        return reportDistributionRepository.save(distribution);
    }

    @Transactional
    public void deleteDistribution(UUID id) {
        log.info("Soft deleting distribution: {}", id);
        ReportDistribution distribution = getDistributionById(id);
        distribution.setDeletedAt(LocalDateTime.now());
        reportDistributionRepository.save(distribution);
    }
}
