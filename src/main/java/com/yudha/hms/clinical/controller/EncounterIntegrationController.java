package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.EncounterIntegrationResponse;
import com.yudha.hms.clinical.dto.ProgressNoteItemResponse;
import com.yudha.hms.clinical.service.EncounterIntegrationService;
import com.yudha.hms.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Encounter Integration Controller.
 *
 * REST API endpoints for encounter integration with other HMS modules.
 * Provides unified view of clinical documentation, orders, billing, pharmacy,
 * BPJS, and SATUSEHAT integrations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/encounter-integration")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EncounterIntegrationController {

    private final EncounterIntegrationService encounterIntegrationService;

    /**
     * Get complete integration data for an encounter.
     *
     * Returns aggregated data from all integrated modules:
     * - Clinical Documentation
     * - Orders and Results
     * - Billing
     * - Pharmacy
     * - BPJS
     * - SATUSEHAT
     *
     * GET /api/clinical/encounter-integration/{encounterId}
     *
     * @param encounterId Encounter ID
     * @return Complete integration response
     */
    @GetMapping("/{encounterId}")
    public ResponseEntity<ApiResponse<EncounterIntegrationResponse>> getEncounterIntegration(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Getting integration data for encounter: {}", encounterId);

        EncounterIntegrationResponse integration = encounterIntegrationService.getEncounterIntegration(encounterId);

        return ResponseEntity.ok(ApiResponse.success(
            "Encounter integration data retrieved",
            integration
        ));
    }

    /**
     * Get clinical documentation for an encounter.
     *
     * Returns all progress notes (SOAP notes, nursing notes, etc.)
     *
     * GET /api/clinical/encounter-integration/{encounterId}/clinical-documentation
     *
     * @param encounterId Encounter ID
     * @return List of progress notes
     */
    @GetMapping("/{encounterId}/clinical-documentation")
    public ResponseEntity<ApiResponse<List<ProgressNoteItemResponse>>> getClinicalDocumentation(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Getting clinical documentation for encounter: {}", encounterId);

        List<ProgressNoteItemResponse> notes = encounterIntegrationService.getClinicalDocumentation(encounterId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d progress notes", notes.size()),
            notes
        ));
    }

    /**
     * Get SOAP notes specifically for an encounter.
     *
     * GET /api/clinical/encounter-integration/{encounterId}/soap-notes
     *
     * @param encounterId Encounter ID
     * @return List of SOAP notes
     */
    @GetMapping("/{encounterId}/soap-notes")
    public ResponseEntity<ApiResponse<List<ProgressNoteItemResponse>>> getSOAPNotes(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Getting SOAP notes for encounter: {}", encounterId);

        List<ProgressNoteItemResponse> notes = encounterIntegrationService.getSOAPNotes(encounterId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d SOAP notes", notes.size()),
            notes
        ));
    }

    /**
     * Check if encounter has complete clinical documentation.
     *
     * GET /api/clinical/encounter-integration/{encounterId}/documentation-complete
     *
     * @param encounterId Encounter ID
     * @return Boolean indicating completeness
     */
    @GetMapping("/{encounterId}/documentation-complete")
    public ResponseEntity<ApiResponse<Boolean>> checkDocumentationComplete(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Checking documentation completeness for encounter: {}", encounterId);

        boolean isComplete = encounterIntegrationService.hasCompleteClinicalDocumentation(encounterId);

        return ResponseEntity.ok(ApiResponse.success(
            isComplete ? "Clinical documentation is complete" : "Clinical documentation is incomplete",
            isComplete
        ));
    }
}
