package com.yudha.hms.integration.eklaim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.eklaim.entity.EklaimAuditLog;
import com.yudha.hms.integration.eklaim.entity.EklaimClaim;
import com.yudha.hms.integration.eklaim.repository.EklaimAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * E-Klaim Audit Logging Service.
 *
 * Provides comprehensive audit trail for all E-Klaim operations:
 * - API calls (request/response logging)
 * - Data modifications (old/new values)
 * - User actions tracking
 * - Error logging
 *
 * Audit logs are critical for:
 * - Regulatory compliance (5-year retention required)
 * - Troubleshooting and debugging
 * - Security monitoring
 * - Performance analysis
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EklaimAuditService {

    private final EklaimAuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log E-Klaim API call (async for performance).
     *
     * @param claim Related claim
     * @param action E-Klaim action/method name
     * @param method HTTP method
     * @param requestData Request body (plain JSON)
     * @param encryptedRequest Encrypted request
     * @param responseData Response body (plain JSON)
     * @param encryptedResponse Encrypted response
     * @param statusCode HTTP status code
     * @param executionTimeMs Execution time in milliseconds
     * @param userId User ID who performed the action
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logApiCall(
        EklaimClaim claim,
        String action,
        String method,
        String requestData,
        String encryptedRequest,
        String responseData,
        String encryptedResponse,
        String statusCode,
        Integer executionTimeMs,
        UUID userId
    ) {
        try {
            EklaimAuditLog auditLog = EklaimAuditLog.builder()
                .claim(claim)
                .action(action)
                .method(method)
                .requestData(requestData)
                .encryptedRequest(encryptedRequest)
                .responseData(responseData)
                .encryptedResponse(encryptedResponse)
                .statusCode(statusCode)
                .executionTimeMs(executionTimeMs)
                .ipAddress(getClientIpAddress())
                .userAgent(getUserAgent())
                .userId(userId)
                .build();

            auditLogRepository.save(auditLog);

            log.debug("Audit log created for action: {} on claim: {}",
                action, claim != null ? claim.getClaimNumber() : "N/A");

        } catch (Exception e) {
            log.error("Failed to create audit log for action: {}", action, e);
        }
    }

    /**
     * Log E-Klaim API error.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(
        EklaimClaim claim,
        String action,
        String method,
        String requestData,
        String errorMessage,
        Integer executionTimeMs,
        UUID userId
    ) {
        try {
            EklaimAuditLog auditLog = EklaimAuditLog.builder()
                .claim(claim)
                .action(action)
                .method(method)
                .requestData(requestData)
                .errorMessage(errorMessage)
                .statusCode("ERROR")
                .executionTimeMs(executionTimeMs)
                .ipAddress(getClientIpAddress())
                .userAgent(getUserAgent())
                .userId(userId)
                .build();

            auditLogRepository.save(auditLog);

            log.debug("Error audit log created for action: {}", action);

        } catch (Exception e) {
            log.error("Failed to create error audit log for action: {}", action, e);
        }
    }

    /**
     * Log data modification (for claim updates).
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logDataModification(
        EklaimClaim claim,
        String action,
        Object oldValues,
        Object newValues,
        UUID userId
    ) {
        try {
            String oldValuesJson = oldValues != null ?
                objectMapper.writeValueAsString(oldValues) : null;
            String newValuesJson = newValues != null ?
                objectMapper.writeValueAsString(newValues) : null;

            EklaimAuditLog auditLog = EklaimAuditLog.builder()
                .claim(claim)
                .action(action)
                .method("UPDATE")
                .oldValues(oldValuesJson)
                .newValues(newValuesJson)
                .statusCode("200")
                .ipAddress(getClientIpAddress())
                .userAgent(getUserAgent())
                .userId(userId)
                .build();

            auditLogRepository.save(auditLog);

            log.debug("Data modification audit log created for claim: {}",
                claim.getClaimNumber());

        } catch (Exception e) {
            log.error("Failed to create data modification audit log", e);
        }
    }

    /**
     * Get audit trail for a specific claim.
     */
    @Transactional(readOnly = true)
    public List<EklaimAuditLog> getClaimAuditTrail(UUID claimId) {
        return auditLogRepository.findByClaimIdOrderByCreatedAtDesc(claimId);
    }

    /**
     * Get audit logs by action type.
     */
    @Transactional(readOnly = true)
    public List<EklaimAuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
    }

    /**
     * Get audit logs for date range.
     */
    @Transactional(readOnly = true)
    public List<EklaimAuditLog> getAuditLogsByDateRange(
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        return auditLogRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Get failed requests for troubleshooting.
     */
    @Transactional(readOnly = true)
    public List<EklaimAuditLog> getFailedRequests() {
        return auditLogRepository.findFailedRequests();
    }

    /**
     * Clean up old audit logs (retention policy: keep for 5 years).
     * Should be scheduled to run periodically.
     */
    @Transactional
    public void cleanupOldLogs() {
        LocalDateTime retentionDate = LocalDateTime.now().minusYears(5);
        auditLogRepository.deleteOlderThan(retentionDate);
        log.info("Cleaned up audit logs older than {}", retentionDate);
    }

    /**
     * Get client IP address from HTTP request.
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // Check for proxy headers
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.debug("Could not get client IP address", e);
        }
        return null;
    }

    /**
     * Get user agent from HTTP request.
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.debug("Could not get user agent", e);
        }
        return null;
    }
}
