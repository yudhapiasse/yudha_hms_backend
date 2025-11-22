package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.constant.reporting.FindingSeverity;
import com.yudha.hms.radiology.constant.reporting.NotificationPriority;
import com.yudha.hms.radiology.entity.reporting.CriticalFindingNotification;
import com.yudha.hms.radiology.repository.reporting.CriticalFindingNotificationRepository;
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
public class CriticalFindingNotificationService {

    private final CriticalFindingNotificationRepository criticalFindingNotificationRepository;

    @Transactional
    public CriticalFindingNotification createNotification(CriticalFindingNotification notification) {
        log.info("Creating critical finding notification for report: {}", notification.getReportId());
        
        notification.setNotifiedAt(LocalDateTime.now());
        
        return criticalFindingNotificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public CriticalFindingNotification getNotificationById(UUID id) {
        return criticalFindingNotificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CriticalFindingNotification> getNotificationsByReport(UUID reportId) {
        return criticalFindingNotificationRepository.findByReportId(reportId);
    }

    @Transactional(readOnly = true)
    public List<CriticalFindingNotification> getUnacknowledgedNotifications() {
        return criticalFindingNotificationRepository.findByAcknowledgedFalse();
    }

    @Transactional(readOnly = true)
    public List<CriticalFindingNotification> getNotificationsBySeverity(FindingSeverity severity) {
        return criticalFindingNotificationRepository.findByFindingSeverity(severity);
    }

    @Transactional(readOnly = true)
    public List<CriticalFindingNotification> getNotificationsByPriority(NotificationPriority priority) {
        return criticalFindingNotificationRepository.findByPriority(priority);
    }

    @Transactional(readOnly = true)
    public List<CriticalFindingNotification> getUnacknowledgedByPriority(NotificationPriority priority) {
        return criticalFindingNotificationRepository.findUnacknowledgedByPriority(priority);
    }

    @Transactional(readOnly = true)
    public List<CriticalFindingNotification> getNotificationsByDateRange(
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        return criticalFindingNotificationRepository.findByNotificationDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<CriticalFindingNotification> getUnacknowledgedRequiringImmediateAction() {
        return criticalFindingNotificationRepository.findUnacknowledgedRequiringImmediateAction();
    }

    @Transactional(readOnly = true)
    public long countNotificationsByReport(UUID reportId) {
        return criticalFindingNotificationRepository.countByReportId(reportId);
    }

    @Transactional(readOnly = true)
    public long countUnacknowledgedNotifications() {
        return criticalFindingNotificationRepository.countUnacknowledged();
    }

    @Transactional
    public CriticalFindingNotification acknowledgeNotification(UUID id, String acknowledgedBy, String acknowledgmentMethod) {
        log.info("Acknowledging critical finding notification: {}", id);
        
        CriticalFindingNotification notification = getNotificationById(id);
        
        if (notification.getAcknowledged()) {
            throw new IllegalStateException("Notification already acknowledged");
        }
        
        notification.setAcknowledged(true);
        notification.setAcknowledgedBy(acknowledgedBy);
        notification.setAcknowledgedAt(LocalDateTime.now());
        notification.setAcknowledgmentMethod(acknowledgmentMethod);
        
        return criticalFindingNotificationRepository.save(notification);
    }

    @Transactional
    public CriticalFindingNotification markFollowUpComplete(UUID id) {
        log.info("Marking follow-up complete for notification: {}", id);
        
        CriticalFindingNotification notification = getNotificationById(id);
        notification.setFollowUpCompleted(true);
        notification.setFollowUpCompletedAt(LocalDateTime.now());
        
        return criticalFindingNotificationRepository.save(notification);
    }

    @Transactional
    public CriticalFindingNotification verifyReadBack(UUID id) {
        log.info("Verifying read-back for notification: {}", id);
        
        CriticalFindingNotification notification = getNotificationById(id);
        notification.setReadBackVerified(true);
        
        return criticalFindingNotificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(UUID id) {
        log.info("Soft deleting notification: {}", id);
        CriticalFindingNotification notification = getNotificationById(id);
        notification.setDeletedAt(LocalDateTime.now());
        criticalFindingNotificationRepository.save(notification);
    }
}
