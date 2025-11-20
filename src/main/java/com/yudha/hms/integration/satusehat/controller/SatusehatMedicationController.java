package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.Medication;
import com.yudha.hms.integration.satusehat.dto.fhir.MedicationDispense;
import com.yudha.hms.integration.satusehat.dto.fhir.MedicationRequest;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import com.yudha.hms.integration.satusehat.service.MedicationResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API controller for SATUSEHAT medication resource operations.
 *
 * Provides endpoints for:
 * - Medication (medication catalog with KFA codes)
 * - MedicationRequest (prescriptions)
 * - MedicationDispense (pharmacy dispensing events)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/medication")
@RequiredArgsConstructor
public class SatusehatMedicationController {

    private final MedicationResourceService medicationResourceService;

    // ========================================================================
    // MEDICATION ENDPOINTS
    // ========================================================================

    /**
     * Create a medication in SATUSEHAT.
     *
     * POST /api/v1/satusehat/medication
     */
    @PostMapping
    public ResponseEntity<Medication> createMedication(
        @RequestBody Medication medication,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create medication request for organization: {}", organizationId);

        try {
            Medication createdMedication = medicationResourceService.createMedication(
                organizationId,
                medication,
                userId
            );

            return ResponseEntity.ok(createdMedication);

        } catch (Exception e) {
            log.error("Failed to create medication: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get medication by ID.
     *
     * GET /api/v1/satusehat/medication/{medicationId}
     */
    @GetMapping("/{medicationId}")
    public ResponseEntity<Medication> getMedication(
        @PathVariable String medicationId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get medication request for ID: {}", medicationId);

        try {
            Medication medication = medicationResourceService.getMedicationById(
                organizationId,
                medicationId,
                userId
            );

            return ResponseEntity.ok(medication);

        } catch (Exception e) {
            log.error("Failed to get medication: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search medications by KFA code.
     *
     * GET /api/v1/satusehat/medication/code/{kfaCode}
     */
    @GetMapping("/code/{kfaCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Medication>> searchMedicationsByCode(
        @PathVariable String kfaCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search medications by KFA code: {}", kfaCode);

        try {
            ClinicalResourceService.SearchBundle<Medication> bundle =
                medicationResourceService.searchMedicationsByCode(organizationId, kfaCode, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search medications: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // MEDICATION REQUEST ENDPOINTS
    // ========================================================================

    /**
     * Create a medication request (prescription) in SATUSEHAT.
     *
     * POST /api/v1/satusehat/medication/request
     */
    @PostMapping("/request")
    public ResponseEntity<MedicationRequest> createMedicationRequest(
        @RequestBody MedicationRequest medicationRequest,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create medication request for organization: {}", organizationId);

        try {
            MedicationRequest createdRequest = medicationResourceService.createMedicationRequest(
                organizationId,
                medicationRequest,
                userId
            );

            return ResponseEntity.ok(createdRequest);

        } catch (Exception e) {
            log.error("Failed to create medication request: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a medication request in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/medication/request/{requestId}
     */
    @PutMapping("/request/{requestId}")
    public ResponseEntity<MedicationRequest> updateMedicationRequest(
        @PathVariable String requestId,
        @RequestBody MedicationRequest medicationRequest,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update medication request for ID: {}", requestId);

        try {
            MedicationRequest updatedRequest = medicationResourceService.updateMedicationRequest(
                organizationId,
                requestId,
                medicationRequest,
                userId
            );

            return ResponseEntity.ok(updatedRequest);

        } catch (Exception e) {
            log.error("Failed to update medication request: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get medication request by ID.
     *
     * GET /api/v1/satusehat/medication/request/{requestId}
     */
    @GetMapping("/request/{requestId}")
    public ResponseEntity<MedicationRequest> getMedicationRequest(
        @PathVariable String requestId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get medication request for ID: {}", requestId);

        try {
            MedicationRequest request = medicationResourceService.getMedicationRequestById(
                organizationId,
                requestId,
                userId
            );

            return ResponseEntity.ok(request);

        } catch (Exception e) {
            log.error("Failed to get medication request: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search medication requests by patient.
     *
     * GET /api/v1/satusehat/medication/request/patient/{ihsNumber}
     */
    @GetMapping("/request/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<MedicationRequest>> searchMedicationRequestsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search medication requests for patient: {}", ihsNumber);

        try {
            ClinicalResourceService.SearchBundle<MedicationRequest> bundle =
                medicationResourceService.searchMedicationRequestsByPatient(organizationId, ihsNumber, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search medication requests: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search medication requests by encounter.
     *
     * GET /api/v1/satusehat/medication/request/encounter/{encounterId}
     */
    @GetMapping("/request/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<MedicationRequest>> searchMedicationRequestsByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search medication requests for encounter: {}", encounterId);

        try {
            ClinicalResourceService.SearchBundle<MedicationRequest> bundle =
                medicationResourceService.searchMedicationRequestsByEncounter(organizationId, encounterId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search medication requests: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search medication requests by status.
     *
     * GET /api/v1/satusehat/medication/request/status/{status}
     */
    @GetMapping("/request/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<MedicationRequest>> searchMedicationRequestsByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search medication requests by status: {}", status);

        try {
            ClinicalResourceService.SearchBundle<MedicationRequest> bundle =
                medicationResourceService.searchMedicationRequestsByStatus(organizationId, status, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search medication requests: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // MEDICATION DISPENSE ENDPOINTS
    // ========================================================================

    /**
     * Create a medication dispense in SATUSEHAT.
     *
     * POST /api/v1/satusehat/medication/dispense
     */
    @PostMapping("/dispense")
    public ResponseEntity<MedicationDispense> createMedicationDispense(
        @RequestBody MedicationDispense medicationDispense,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create medication dispense for organization: {}", organizationId);

        try {
            MedicationDispense createdDispense = medicationResourceService.createMedicationDispense(
                organizationId,
                medicationDispense,
                userId
            );

            return ResponseEntity.ok(createdDispense);

        } catch (Exception e) {
            log.error("Failed to create medication dispense: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a medication dispense in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/medication/dispense/{dispenseId}
     */
    @PutMapping("/dispense/{dispenseId}")
    public ResponseEntity<MedicationDispense> updateMedicationDispense(
        @PathVariable String dispenseId,
        @RequestBody MedicationDispense medicationDispense,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update medication dispense for ID: {}", dispenseId);

        try {
            MedicationDispense updatedDispense = medicationResourceService.updateMedicationDispense(
                organizationId,
                dispenseId,
                medicationDispense,
                userId
            );

            return ResponseEntity.ok(updatedDispense);

        } catch (Exception e) {
            log.error("Failed to update medication dispense: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get medication dispense by ID.
     *
     * GET /api/v1/satusehat/medication/dispense/{dispenseId}
     */
    @GetMapping("/dispense/{dispenseId}")
    public ResponseEntity<MedicationDispense> getMedicationDispense(
        @PathVariable String dispenseId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get medication dispense for ID: {}", dispenseId);

        try {
            MedicationDispense dispense = medicationResourceService.getMedicationDispenseById(
                organizationId,
                dispenseId,
                userId
            );

            return ResponseEntity.ok(dispense);

        } catch (Exception e) {
            log.error("Failed to get medication dispense: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search medication dispenses by patient.
     *
     * GET /api/v1/satusehat/medication/dispense/patient/{ihsNumber}
     */
    @GetMapping("/dispense/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<MedicationDispense>> searchMedicationDispensesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search medication dispenses for patient: {}", ihsNumber);

        try {
            ClinicalResourceService.SearchBundle<MedicationDispense> bundle =
                medicationResourceService.searchMedicationDispensesByPatient(organizationId, ihsNumber, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search medication dispenses: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search medication dispenses by prescription.
     *
     * GET /api/v1/satusehat/medication/dispense/prescription/{requestId}
     */
    @GetMapping("/dispense/prescription/{requestId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<MedicationDispense>> searchMedicationDispensesByPrescription(
        @PathVariable String requestId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search medication dispenses for prescription: {}", requestId);

        try {
            ClinicalResourceService.SearchBundle<MedicationDispense> bundle =
                medicationResourceService.searchMedicationDispensesByPrescription(organizationId, requestId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search medication dispenses: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search medication dispenses by status.
     *
     * GET /api/v1/satusehat/medication/dispense/status/{status}
     */
    @GetMapping("/dispense/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<MedicationDispense>> searchMedicationDispensesByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search medication dispenses by status: {}", status);

        try {
            ClinicalResourceService.SearchBundle<MedicationDispense> bundle =
                medicationResourceService.searchMedicationDispensesByStatus(organizationId, status, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search medication dispenses: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
