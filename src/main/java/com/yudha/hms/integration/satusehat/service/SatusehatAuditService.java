package com.yudha.hms.integration.satusehat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.satusehat.entity.SatusehatAuditLog;
import com.yudha.hms.integration.satusehat.entity.SatusehatConfig;
import com.yudha.hms.integration.satusehat.repository.SatusehatAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * SATUSEHAT Audit Service.
 *
 * Handles comprehensive audit logging for all SATUSEHAT API operations.
 * Logs are persisted asynchronously to avoid blocking main operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SatusehatAuditService {

    private final SatusehatAuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log successful OAuth2 authentication.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuthSuccess(SatusehatConfig config,
                                String endpoint,
                                String method,
                                String responseBody,
                                int executionTimeMs,
                                UUID userId) {
        try {
            SatusehatAuditLog log = SatusehatAuditLog.builder()
                .config(config)
                .operationType("AUTH")
                .method(method)
                .endpoint(endpoint)
                .responseBody(maskSensitiveData(responseBody))
                .responseStatus(200)
                .executionTimeMs(executionTimeMs)
                .userId(userId)
                .build();

            auditLogRepository.save(log);
            SatusehatAuditService.log.debug("Logged successful authentication for organization: {}",
                config.getOrganizationId());

        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Log failed OAuth2 authentication.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuthFailure(SatusehatConfig config,
                                String endpoint,
                                String method,
                                String errorMessage,
                                int executionTimeMs,
                                UUID userId) {
        try {
            SatusehatAuditLog log = SatusehatAuditLog.builder()
                .config(config)
                .operationType("AUTH")
                .method(method)
                .endpoint(endpoint)
                .errorMessage(errorMessage)
                .responseStatus(401)
                .executionTimeMs(executionTimeMs)
                .userId(userId)
                .build();

            auditLogRepository.save(log);
            SatusehatAuditService.log.debug("Logged failed authentication for organization: {}",
                config.getOrganizationId());

        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Log FHIR API call.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logApiCall(SatusehatConfig config,
                          String operationType,
                          String resourceType,
                          String resourceId,
                          String method,
                          String endpoint,
                          String requestBody,
                          String responseBody,
                          int responseStatus,
                          int executionTimeMs,
                          UUID userId) {
        try {
            SatusehatAuditLog log = SatusehatAuditLog.builder()
                .config(config)
                .operationType(operationType)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .method(method)
                .endpoint(endpoint)
                .requestBody(maskSensitiveData(requestBody))
                .responseBody(maskSensitiveData(responseBody))
                .responseStatus(responseStatus)
                .executionTimeMs(executionTimeMs)
                .userId(userId)
                .build();

            auditLogRepository.save(log);

        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Log API error.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logApiError(SatusehatConfig config,
                           String operationType,
                           String resourceType,
                           String method,
                           String endpoint,
                           String requestBody,
                           String errorCode,
                           String errorMessage,
                           int responseStatus,
                           int executionTimeMs,
                           int retryCount,
                           UUID userId) {
        try {
            SatusehatAuditLog log = SatusehatAuditLog.builder()
                .config(config)
                .operationType(operationType)
                .resourceType(resourceType)
                .method(method)
                .endpoint(endpoint)
                .requestBody(maskSensitiveData(requestBody))
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .responseStatus(responseStatus)
                .executionTimeMs(executionTimeMs)
                .retryCount(retryCount)
                .userId(userId)
                .build();

            auditLogRepository.save(log);

        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Mask sensitive data in logs (tokens, passwords, etc.).
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.isBlank()) {
            return data;
        }

        try {
            // Mask access tokens
            data = data.replaceAll(
                "(\"access_token\"\\s*:\\s*\")([^\"]+)(\")",
                "$1***MASKED***$3"
            );

            // Mask client secrets
            data = data.replaceAll(
                "(\"client_secret\"\\s*:\\s*\")([^\"]+)(\")",
                "$1***MASKED***$3"
            );

            // Mask authorization headers
            data = data.replaceAll(
                "(Authorization\"\\s*:\\s*\"Bearer\\s+)([^\"]+)(\")",
                "$1***MASKED***$3"
            );

            return data;

        } catch (Exception e) {
            log.warn("Failed to mask sensitive data: {}", e.getMessage());
            return data;
        }
    }
}
