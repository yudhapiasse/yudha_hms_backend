package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.TokenInfo;
import com.yudha.hms.integration.satusehat.entity.SatusehatConfig;
import com.yudha.hms.integration.satusehat.repository.SatusehatConfigRepository;
import com.yudha.hms.integration.satusehat.service.SatusehatAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * SATUSEHAT REST Controller.
 *
 * Provides API endpoints for SATUSEHAT operations:
 * - OAuth2 authentication testing
 * - Configuration management
 * - Token management
 * - Status checks
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@RestController
@RequestMapping("/api/v1/satusehat")
@RequiredArgsConstructor
public class SatusehatController {

    private final SatusehatAuthService authService;
    private final SatusehatConfigRepository configRepository;

    /**
     * Test OAuth2 authentication.
     *
     * POST /api/v1/satusehat/auth/test
     */
    @PostMapping("/auth/test")
    public ResponseEntity<Map<String, Object>> testAuthentication(
        @RequestParam String organizationId,
        @AuthenticationPrincipal Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());

        // Request access token
        String accessToken = authService.getAccessToken(organizationId, userId);

        // Validate token format
        boolean isValid = authService.isValidTokenFormat(accessToken);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("organizationId", organizationId);
        response.put("tokenValid", isValid);
        response.put("tokenLength", accessToken.length());
        response.put("message", "OAuth2 authentication successful");

        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token.
     *
     * POST /api/v1/satusehat/auth/refresh
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(
        @RequestParam String organizationId,
        @AuthenticationPrincipal Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());

        // Refresh token
        authService.refreshTokenIfNeeded(organizationId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("organizationId", organizationId);
        response.put("message", "Token refresh completed");

        return ResponseEntity.ok(response);
    }

    /**
     * Invalidate cached token (force refresh on next request).
     *
     * DELETE /api/v1/satusehat/auth/token
     */
    @DeleteMapping("/auth/token")
    public ResponseEntity<Map<String, Object>> invalidateToken(
        @RequestParam String organizationId,
        @RequestParam String environment
    ) {
        authService.invalidateToken(organizationId, environment);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("organizationId", organizationId);
        response.put("environment", environment);
        response.put("message", "Token invalidated successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Get configuration by organization ID.
     *
     * GET /api/v1/satusehat/config/{organizationId}
     */
    @GetMapping("/config/{organizationId}")
    public ResponseEntity<SatusehatConfig> getConfig(
        @PathVariable String organizationId
    ) {
        return configRepository.findByOrganizationIdAndIsActiveTrue(organizationId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Health check endpoint.
     *
     * GET /api/v1/satusehat/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "SATUSEHAT Integration");
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }
}
