package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.Patient;
import com.yudha.hms.integration.satusehat.entity.SatusehatResourceMapping;
import com.yudha.hms.integration.satusehat.service.PatientResourceService;
import com.yudha.hms.integration.satusehat.service.PatientSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API controller for SATUSEHAT Patient resource operations.
 *
 * Provides endpoints for:
 * - Patient synchronization with SATUSEHAT
 * - Sync status tracking
 * - Failed submission retry
 * - Patient search in SATUSEHAT
 * - FHIR Patient resource operations
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/patient")
@RequiredArgsConstructor
public class SatusehatPatientController {

    private final PatientSyncService patientSyncService;
    private final PatientResourceService patientResourceService;

    /**
     * Sync patient to SATUSEHAT.
     *
     * POST /api/v1/satusehat/patient/sync/{patientId}
     */
    @PostMapping("/sync/{patientId}")
    public ResponseEntity<SyncResponse> syncPatient(
        @PathVariable UUID patientId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Sync patient request for patientId: {}", patientId);

        try {
            // TODO: Fetch HMS patient from repository
            // For now, return a placeholder response
            // SatusehatResourceMapping mapping = patientSyncService.syncPatient(organizationId, hmsPatient, userId);

            return ResponseEntity.ok(SyncResponse.builder()
                .success(true)
                .message("Patient sync initiated successfully")
                .patientId(patientId)
                .build());

        } catch (Exception e) {
            log.error("Failed to sync patient: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(SyncResponse.builder()
                .success(false)
                .message("Patient sync failed: " + e.getMessage())
                .patientId(patientId)
                .build());
        }
    }

    /**
     * Get patient sync status.
     *
     * GET /api/v1/satusehat/patient/sync/{patientId}/status
     */
    @GetMapping("/sync/{patientId}/status")
    public ResponseEntity<SyncStatusResponse> getSyncStatus(@PathVariable UUID patientId) {
        log.info("Get sync status request for patientId: {}", patientId);

        SatusehatResourceMapping mapping = patientSyncService.getSyncStatus(patientId);

        if (mapping == null) {
            return ResponseEntity.ok(SyncStatusResponse.builder()
                .patientId(patientId)
                .status("NOT_SYNCED")
                .message("Patient has not been synced to SATUSEHAT")
                .build());
        }

        return ResponseEntity.ok(SyncStatusResponse.builder()
            .patientId(patientId)
            .status(mapping.getSubmissionStatus().name())
            .ihsNumber(mapping.getSatusehatResourceId())
            .lastSubmitted(mapping.getLastSubmittedAt())
            .retryCount(mapping.getRetryCount())
            .lastError(mapping.getLastError())
            .message("Sync status retrieved successfully")
            .build());
    }

    /**
     * Retry failed submissions.
     *
     * POST /api/v1/satusehat/patient/sync/retry
     */
    @PostMapping("/sync/retry")
    public ResponseEntity<RetryResponse> retryFailedSubmissions(
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Retry failed submissions request for organization: {}", organizationId);

        try {
            int successCount = patientSyncService.retryFailedSubmissions(organizationId, userId);

            return ResponseEntity.ok(RetryResponse.builder()
                .success(true)
                .message(successCount + " patients successfully retried")
                .retriedCount(successCount)
                .build());

        } catch (Exception e) {
            log.error("Failed to retry submissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(RetryResponse.builder()
                .success(false)
                .message("Retry failed: " + e.getMessage())
                .retriedCount(0)
                .build());
        }
    }

    /**
     * Get failed submissions.
     *
     * GET /api/v1/satusehat/patient/sync/failed
     */
    @GetMapping("/sync/failed")
    public ResponseEntity<List<SatusehatResourceMapping>> getFailedSubmissions(
        @RequestParam String organizationId
    ) {
        log.info("Get failed submissions request for organization: {}", organizationId);

        List<SatusehatResourceMapping> failedMappings = patientSyncService.getFailedSubmissions(organizationId);

        return ResponseEntity.ok(failedMappings);
    }

    /**
     * Search patient by NIK.
     *
     * GET /api/v1/satusehat/patient/search/nik/{nik}
     */
    @GetMapping("/search/nik/{nik}")
    public ResponseEntity<PatientResourceService.Bundle> searchPatientByNik(
        @PathVariable String nik,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search patient by NIK request: {}", nik);

        try {
            PatientResourceService.Bundle bundle = patientResourceService.searchPatientByNik(
                organizationId,
                nik,
                userId
            );

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search patient by NIK: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search patient by name.
     *
     * GET /api/v1/satusehat/patient/search/name
     */
    @GetMapping("/search/name")
    public ResponseEntity<PatientResourceService.Bundle> searchPatientByName(
        @RequestParam String name,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search patient by name request: {}", name);

        try {
            PatientResourceService.Bundle bundle = patientResourceService.searchPatientByName(
                organizationId,
                name,
                userId
            );

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search patient by name: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get patient by IHS number.
     *
     * GET /api/v1/satusehat/patient/{ihsNumber}
     */
    @GetMapping("/{ihsNumber}")
    public ResponseEntity<Patient> getPatientByIhsNumber(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get patient request for IHS number: {}", ihsNumber);

        try {
            Patient patient = patientResourceService.getPatientByIhsNumber(
                organizationId,
                ihsNumber,
                userId
            );

            return ResponseEntity.ok(patient);

        } catch (Exception e) {
            log.error("Failed to get patient by IHS number: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Reset failed submission.
     *
     * POST /api/v1/satusehat/patient/sync/{mappingId}/reset
     */
    @PostMapping("/sync/{mappingId}/reset")
    public ResponseEntity<Map<String, String>> resetFailedSubmission(@PathVariable UUID mappingId) {
        log.info("Reset failed submission request for mapping: {}", mappingId);

        try {
            patientSyncService.resetFailedSubmission(mappingId);

            return ResponseEntity.ok(Map.of(
                "success", "true",
                "message", "Failed submission reset successfully"
            ));

        } catch (Exception e) {
            log.error("Failed to reset submission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", "false",
                "message", "Reset failed: " + e.getMessage()
            ));
        }
    }

    // ========================================================================
    // RESPONSE DTOs
    // ========================================================================

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SyncResponse {
        private Boolean success;
        private String message;
        private UUID patientId;
        private String ihsNumber;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SyncStatusResponse {
        private UUID patientId;
        private String status;
        private String ihsNumber;
        private java.time.LocalDateTime lastSubmitted;
        private Integer retryCount;
        private String lastError;
        private String message;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RetryResponse {
        private Boolean success;
        private String message;
        private Integer retriedCount;
    }
}
