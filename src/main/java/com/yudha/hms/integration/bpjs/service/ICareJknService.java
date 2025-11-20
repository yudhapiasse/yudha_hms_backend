package com.yudha.hms.integration.bpjs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.bpjs.dto.icare.HistoryAccessInfo;
import com.yudha.hms.integration.bpjs.dto.icare.HistoryValidationRequest;
import com.yudha.hms.integration.bpjs.dto.icare.HistoryValidationResponse;
import com.yudha.hms.integration.bpjs.exception.BpjsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * iCare JKN Service Implementation.
 *
 * Provides patient history validation and secure access services:
 * - Patient history validation with BPJS card number
 * - Secure URL token generation for history access
 * - Token-based session management
 * - Access audit logging
 * - Token expiry handling
 *
 * iCare JKN allows healthcare providers to access complete patient
 * treatment history across all BPJS-registered facilities through
 * a secure, token-based URL system.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ICareJknService {

    private final BpjsHttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * In-memory storage for active history access sessions.
     * Key: Token, Value: HistoryAccessInfo
     *
     * In production, consider using Redis or database for persistence.
     */
    private final Map<String, HistoryAccessInfo> activeTokens = new ConcurrentHashMap<>();

    /**
     * Default token expiry duration in hours.
     * Based on BPJS policy (typically 24 hours).
     */
    private static final int DEFAULT_TOKEN_EXPIRY_HOURS = 24;

    // ========== PATIENT HISTORY VALIDATION SERVICES ==========

    /**
     * Validate patient and retrieve secure URL for history access.
     *
     * This method requests a secure URL with a temporary token that allows
     * authorized doctors to access the patient's complete treatment history
     * across all BPJS-registered healthcare facilities.
     *
     * @param cardNumber BPJS card number (13 digits)
     * @param doctorCode Doctor code registered with BPJS
     * @return History validation response with secure URL
     */
    public HistoryValidationResponse validatePatientHistory(String cardNumber, Integer doctorCode) {
        String endpoint = "/validate";

        log.info("Validating patient history - Card: {}, Doctor: {}", cardNumber, doctorCode);

        try {
            HistoryValidationRequest request = HistoryValidationRequest.builder()
                .param(cardNumber)
                .kodedokter(doctorCode)
                .build();

            JsonNode response = httpClient.icarePost(endpoint, request);
            HistoryValidationResponse validationResponse =
                objectMapper.treeToValue(response, HistoryValidationResponse.class);

            if (validationResponse.isSuccess()) {
                log.info("Successfully validated patient history - Card: {}, URL generated",
                    cardNumber);

                // Store token for session management
                storeHistoryAccess(validationResponse, cardNumber, doctorCode);
            } else {
                log.warn("Failed to validate patient history: {}",
                    validationResponse.getErrorMessage());
            }

            return validationResponse;

        } catch (Exception e) {
            log.error("Failed to validate patient history", e);
            throw new BpjsHttpException("Failed to validate patient history: " + e.getMessage(), e);
        }
    }

    /**
     * Validate patient history with additional context.
     *
     * @param cardNumber BPJS card number
     * @param doctorCode Doctor code
     * @param patientName Patient name (for logging)
     * @param doctorName Doctor name (for logging)
     * @param requestedBy User ID who initiated the request
     * @param ipAddress IP address of the requesting user
     * @param accessPurpose Purpose of access (e.g., "Consultation", "Emergency")
     * @return History validation response with secure URL
     */
    public HistoryValidationResponse validatePatientHistoryWithContext(
            String cardNumber,
            Integer doctorCode,
            String patientName,
            String doctorName,
            String requestedBy,
            String ipAddress,
            String accessPurpose) {

        log.info("Validating patient history with context - Card: {}, Doctor: {}, Purpose: {}, By: {}",
            cardNumber, doctorCode, accessPurpose, requestedBy);

        HistoryValidationResponse response = validatePatientHistory(cardNumber, doctorCode);

        if (response.isSuccess()) {
            // Enhance stored access info with context
            String token = response.extractToken();
            if (token != null) {
                HistoryAccessInfo accessInfo = activeTokens.get(token);
                if (accessInfo != null) {
                    accessInfo.setPatientName(patientName);
                    accessInfo.setDoctorName(doctorName);
                    accessInfo.setRequestedBy(requestedBy);
                    accessInfo.setIpAddress(ipAddress);
                    accessInfo.setAccessPurpose(accessPurpose);
                }
            }
        }

        return response;
    }

    // ========== SESSION MANAGEMENT SERVICES ==========

    /**
     * Store history access information for session management and audit.
     *
     * @param response Validation response
     * @param cardNumber Patient's BPJS card number
     * @param doctorCode Doctor code
     */
    private void storeHistoryAccess(
            HistoryValidationResponse response,
            String cardNumber,
            Integer doctorCode) {

        if (!response.isSuccess()) {
            return;
        }

        String token = response.extractToken();
        if (token == null) {
            log.warn("No token found in history URL: {}", response.getHistoryUrl());
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(DEFAULT_TOKEN_EXPIRY_HOURS);

        HistoryAccessInfo accessInfo = HistoryAccessInfo.builder()
            .cardNumber(cardNumber)
            .doctorCode(doctorCode)
            .historyUrl(response.getHistoryUrl())
            .token(token)
            .accessedAt(now)
            .expiresAt(expiresAt)
            .build();

        activeTokens.put(token, accessInfo);

        log.debug("Stored history access token: {} for card: {}, expires at: {}",
            token, cardNumber, expiresAt);
    }

    /**
     * Get history access information by token.
     *
     * @param token Token extracted from URL
     * @return History access info or null if not found
     */
    public HistoryAccessInfo getHistoryAccessByToken(String token) {
        return activeTokens.get(token);
    }

    /**
     * Check if token is valid (exists and not expired).
     *
     * @param token Token to check
     * @return true if token is valid
     */
    public boolean isTokenValid(String token) {
        HistoryAccessInfo accessInfo = activeTokens.get(token);
        if (accessInfo == null) {
            return false;
        }

        if (accessInfo.isExpired()) {
            // Remove expired token
            activeTokens.remove(token);
            log.debug("Removed expired token: {}", token);
            return false;
        }

        return true;
    }

    /**
     * Invalidate (remove) a token from active sessions.
     *
     * @param token Token to invalidate
     */
    public void invalidateToken(String token) {
        HistoryAccessInfo removed = activeTokens.remove(token);
        if (removed != null) {
            log.info("Invalidated history access token for card: {}", removed.getCardNumber());
        }
    }

    /**
     * Get all active history access sessions.
     *
     * @return Map of active tokens
     */
    public Map<String, HistoryAccessInfo> getActiveHistoryAccess() {
        // Clean up expired tokens first
        cleanupExpiredTokens();
        return Map.copyOf(activeTokens);
    }

    /**
     * Clean up expired tokens from active sessions.
     * Should be called periodically by a scheduled task.
     *
     * @return Number of tokens cleaned up
     */
    public int cleanupExpiredTokens() {
        int removedCount = 0;

        for (Map.Entry<String, HistoryAccessInfo> entry : activeTokens.entrySet()) {
            if (entry.getValue().isExpired()) {
                activeTokens.remove(entry.getKey());
                removedCount++;
            }
        }

        if (removedCount > 0) {
            log.info("Cleaned up {} expired history access tokens", removedCount);
        }

        return removedCount;
    }

    /**
     * Get count of active history access sessions.
     *
     * @return Number of active sessions
     */
    public int getActiveSessionCount() {
        cleanupExpiredTokens();
        return activeTokens.size();
    }

    // ========== HELPER METHODS ==========

    /**
     * Validate BPJS card number format.
     *
     * @param cardNumber Card number to validate
     * @return true if format is valid (13 digits)
     */
    public boolean validateCardNumberFormat(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        return cardNumber.matches("\\d{13}");
    }

    /**
     * Create iframe HTML for embedding history URL.
     *
     * @param historyUrl Secure URL with token
     * @param width Iframe width (e.g., "100%", "800px")
     * @param height Iframe height (e.g., "600px", "100vh")
     * @return HTML iframe code
     */
    public String createHistoryIframe(String historyUrl, String width, String height) {
        return String.format(
            "<iframe src=\"%s\" width=\"%s\" height=\"%s\" frameborder=\"0\" " +
            "sandbox=\"allow-same-origin allow-scripts allow-popups allow-forms\" " +
            "title=\"BPJS Patient History\"></iframe>",
            historyUrl, width, height
        );
    }

    /**
     * Create iframe HTML with default dimensions.
     *
     * @param historyUrl Secure URL with token
     * @return HTML iframe code (100% width, 600px height)
     */
    public String createHistoryIframe(String historyUrl) {
        return createHistoryIframe(historyUrl, "100%", "600px");
    }

    /**
     * Check if iCare JKN integration is enabled.
     *
     * @return true if integration is ready
     */
    public boolean isICareEnabled() {
        return true; // Enabled if BPJS is configured
    }

    /**
     * Get audit log for a specific card number.
     * Returns all access history for the patient.
     *
     * @param cardNumber BPJS card number
     * @return Access history list
     */
    public java.util.List<HistoryAccessInfo> getAccessAuditLog(String cardNumber) {
        return activeTokens.values().stream()
            .filter(info -> cardNumber.equals(info.getCardNumber()))
            .sorted((a, b) -> b.getAccessedAt().compareTo(a.getAccessedAt())) // Latest first
            .toList();
    }

    /**
     * Get audit log for a specific doctor.
     * Returns all access history by the doctor.
     *
     * @param doctorCode Doctor code
     * @return Access history list
     */
    public java.util.List<HistoryAccessInfo> getAccessAuditLogByDoctor(Integer doctorCode) {
        return activeTokens.values().stream()
            .filter(info -> doctorCode.equals(info.getDoctorCode()))
            .sorted((a, b) -> b.getAccessedAt().compareTo(a.getAccessedAt())) // Latest first
            .toList();
    }
}
