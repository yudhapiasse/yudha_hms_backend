package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.service.ClinicalDocumentService;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for SATUSEHAT Clinical Documentation Resources.
 *
 * Provides endpoints for managing:
 * - Composition (Clinical documents like discharge summaries)
 * - AllergyIntolerance (Allergy and intolerance records)
 * - ClinicalImpression (Clinical assessments and conclusions)
 * - Immunization (Vaccine administration records)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/clinical-documents")
@RequiredArgsConstructor
public class SatusehatClinicalDocumentController {

    private final ClinicalDocumentService clinicalDocumentService;

    // ========================================================================
    // COMPOSITION ENDPOINTS
    // ========================================================================

    /**
     * Create a new composition (clinical document).
     *
     * POST /api/v1/satusehat/clinical-documents/composition
     */
    @PostMapping("/composition")
    public ResponseEntity<Composition> createComposition(
        @RequestBody Composition composition,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create Composition for organization: {}", organizationId);
        Composition created = clinicalDocumentService.createComposition(organizationId, composition, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing composition.
     *
     * PUT /api/v1/satusehat/clinical-documents/composition/{compositionId}
     */
    @PutMapping("/composition/{compositionId}")
    public ResponseEntity<Composition> updateComposition(
        @PathVariable String compositionId,
        @RequestBody Composition composition,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update Composition: {}", compositionId);
        Composition updated = clinicalDocumentService.updateComposition(organizationId, compositionId, composition, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get composition by ID.
     *
     * GET /api/v1/satusehat/clinical-documents/composition/{compositionId}
     */
    @GetMapping("/composition/{compositionId}")
    public ResponseEntity<Composition> getCompositionById(
        @PathVariable String compositionId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get Composition: {}", compositionId);
        Composition composition = clinicalDocumentService.getCompositionById(organizationId, compositionId, userId);
        return ResponseEntity.ok(composition);
    }

    /**
     * Search compositions by patient.
     *
     * GET /api/v1/satusehat/clinical-documents/composition/patient/{ihsNumber}
     */
    @GetMapping("/composition/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Composition>> searchCompositionsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Compositions for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<Composition> results =
            clinicalDocumentService.searchCompositionsByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search compositions by encounter.
     *
     * GET /api/v1/satusehat/clinical-documents/composition/encounter/{encounterId}
     */
    @GetMapping("/composition/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Composition>> searchCompositionsByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Compositions for encounter: {}", encounterId);
        ClinicalResourceService.SearchBundle<Composition> results =
            clinicalDocumentService.searchCompositionsByEncounter(organizationId, encounterId, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search compositions by type.
     *
     * GET /api/v1/satusehat/clinical-documents/composition/type/{typeCode}
     */
    @GetMapping("/composition/type/{typeCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Composition>> searchCompositionsByType(
        @PathVariable String typeCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Compositions by type: {}", typeCode);
        ClinicalResourceService.SearchBundle<Composition> results =
            clinicalDocumentService.searchCompositionsByType(organizationId, typeCode, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // ALLERGY INTOLERANCE ENDPOINTS
    // ========================================================================

    /**
     * Create a new allergy intolerance record.
     *
     * POST /api/v1/satusehat/clinical-documents/allergy-intolerance
     */
    @PostMapping("/allergy-intolerance")
    public ResponseEntity<AllergyIntolerance> createAllergyIntolerance(
        @RequestBody AllergyIntolerance allergyIntolerance,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create AllergyIntolerance for organization: {}", organizationId);
        AllergyIntolerance created = clinicalDocumentService.createAllergyIntolerance(organizationId, allergyIntolerance, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing allergy intolerance record.
     *
     * PUT /api/v1/satusehat/clinical-documents/allergy-intolerance/{allergyId}
     */
    @PutMapping("/allergy-intolerance/{allergyId}")
    public ResponseEntity<AllergyIntolerance> updateAllergyIntolerance(
        @PathVariable String allergyId,
        @RequestBody AllergyIntolerance allergyIntolerance,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update AllergyIntolerance: {}", allergyId);
        AllergyIntolerance updated = clinicalDocumentService.updateAllergyIntolerance(organizationId, allergyId, allergyIntolerance, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get allergy intolerance by ID.
     *
     * GET /api/v1/satusehat/clinical-documents/allergy-intolerance/{allergyId}
     */
    @GetMapping("/allergy-intolerance/{allergyId}")
    public ResponseEntity<AllergyIntolerance> getAllergyIntoleranceById(
        @PathVariable String allergyId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get AllergyIntolerance: {}", allergyId);
        AllergyIntolerance allergyIntolerance = clinicalDocumentService.getAllergyIntoleranceById(organizationId, allergyId, userId);
        return ResponseEntity.ok(allergyIntolerance);
    }

    /**
     * Search allergy intolerances by patient.
     *
     * GET /api/v1/satusehat/clinical-documents/allergy-intolerance/patient/{ihsNumber}
     */
    @GetMapping("/allergy-intolerance/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<AllergyIntolerance>> searchAllergyIntolerancesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search AllergyIntolerances for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<AllergyIntolerance> results =
            clinicalDocumentService.searchAllergyIntolerancesByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search allergy intolerances by category.
     *
     * GET /api/v1/satusehat/clinical-documents/allergy-intolerance/category/{category}
     */
    @GetMapping("/allergy-intolerance/category/{category}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<AllergyIntolerance>> searchAllergyIntolerancesByCategory(
        @PathVariable String category,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search AllergyIntolerances by category: {}", category);
        ClinicalResourceService.SearchBundle<AllergyIntolerance> results =
            clinicalDocumentService.searchAllergyIntolerancesByCategory(organizationId, category, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // CLINICAL IMPRESSION ENDPOINTS
    // ========================================================================

    /**
     * Create a new clinical impression.
     *
     * POST /api/v1/satusehat/clinical-documents/clinical-impression
     */
    @PostMapping("/clinical-impression")
    public ResponseEntity<ClinicalImpression> createClinicalImpression(
        @RequestBody ClinicalImpression clinicalImpression,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create ClinicalImpression for organization: {}", organizationId);
        ClinicalImpression created = clinicalDocumentService.createClinicalImpression(organizationId, clinicalImpression, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing clinical impression.
     *
     * PUT /api/v1/satusehat/clinical-documents/clinical-impression/{impressionId}
     */
    @PutMapping("/clinical-impression/{impressionId}")
    public ResponseEntity<ClinicalImpression> updateClinicalImpression(
        @PathVariable String impressionId,
        @RequestBody ClinicalImpression clinicalImpression,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update ClinicalImpression: {}", impressionId);
        ClinicalImpression updated = clinicalDocumentService.updateClinicalImpression(organizationId, impressionId, clinicalImpression, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get clinical impression by ID.
     *
     * GET /api/v1/satusehat/clinical-documents/clinical-impression/{impressionId}
     */
    @GetMapping("/clinical-impression/{impressionId}")
    public ResponseEntity<ClinicalImpression> getClinicalImpressionById(
        @PathVariable String impressionId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get ClinicalImpression: {}", impressionId);
        ClinicalImpression clinicalImpression = clinicalDocumentService.getClinicalImpressionById(organizationId, impressionId, userId);
        return ResponseEntity.ok(clinicalImpression);
    }

    /**
     * Search clinical impressions by patient.
     *
     * GET /api/v1/satusehat/clinical-documents/clinical-impression/patient/{ihsNumber}
     */
    @GetMapping("/clinical-impression/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ClinicalImpression>> searchClinicalImpressionsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ClinicalImpressions for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<ClinicalImpression> results =
            clinicalDocumentService.searchClinicalImpressionsByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search clinical impressions by encounter.
     *
     * GET /api/v1/satusehat/clinical-documents/clinical-impression/encounter/{encounterId}
     */
    @GetMapping("/clinical-impression/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<ClinicalImpression>> searchClinicalImpressionsByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search ClinicalImpressions for encounter: {}", encounterId);
        ClinicalResourceService.SearchBundle<ClinicalImpression> results =
            clinicalDocumentService.searchClinicalImpressionsByEncounter(organizationId, encounterId, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // IMMUNIZATION ENDPOINTS
    // ========================================================================

    /**
     * Create a new immunization record.
     *
     * POST /api/v1/satusehat/clinical-documents/immunization
     */
    @PostMapping("/immunization")
    public ResponseEntity<Immunization> createImmunization(
        @RequestBody Immunization immunization,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create Immunization for organization: {}", organizationId);
        Immunization created = clinicalDocumentService.createImmunization(organizationId, immunization, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing immunization record.
     *
     * PUT /api/v1/satusehat/clinical-documents/immunization/{immunizationId}
     */
    @PutMapping("/immunization/{immunizationId}")
    public ResponseEntity<Immunization> updateImmunization(
        @PathVariable String immunizationId,
        @RequestBody Immunization immunization,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update Immunization: {}", immunizationId);
        Immunization updated = clinicalDocumentService.updateImmunization(organizationId, immunizationId, immunization, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get immunization by ID.
     *
     * GET /api/v1/satusehat/clinical-documents/immunization/{immunizationId}
     */
    @GetMapping("/immunization/{immunizationId}")
    public ResponseEntity<Immunization> getImmunizationById(
        @PathVariable String immunizationId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get Immunization: {}", immunizationId);
        Immunization immunization = clinicalDocumentService.getImmunizationById(organizationId, immunizationId, userId);
        return ResponseEntity.ok(immunization);
    }

    /**
     * Search immunizations by patient.
     *
     * GET /api/v1/satusehat/clinical-documents/immunization/patient/{ihsNumber}
     */
    @GetMapping("/immunization/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Immunization>> searchImmunizationsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Immunizations for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<Immunization> results =
            clinicalDocumentService.searchImmunizationsByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search immunizations by vaccine type.
     *
     * GET /api/v1/satusehat/clinical-documents/immunization/vaccine-type/{vaccineCode}
     */
    @GetMapping("/immunization/vaccine-type/{vaccineCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Immunization>> searchImmunizationsByVaccineType(
        @PathVariable String vaccineCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Immunizations by vaccine type: {}", vaccineCode);
        ClinicalResourceService.SearchBundle<Immunization> results =
            clinicalDocumentService.searchImmunizationsByVaccineType(organizationId, vaccineCode, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search immunizations by status.
     *
     * GET /api/v1/satusehat/clinical-documents/immunization/status/{status}
     */
    @GetMapping("/immunization/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Immunization>> searchImmunizationsByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search Immunizations by status: {}", status);
        ClinicalResourceService.SearchBundle<Immunization> results =
            clinicalDocumentService.searchImmunizationsByStatus(organizationId, status, userId);
        return ResponseEntity.ok(results);
    }
}
