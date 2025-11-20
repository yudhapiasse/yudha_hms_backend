package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.service.CareManagementService;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for SATUSEHAT Care Management Resources.
 *
 * Provides endpoints for:
 * - EpisodeOfCare (Care episodes and chronic disease management)
 * - CarePlan (Comprehensive care planning and coordination)
 * - QuestionnaireResponse (Patient-reported outcomes and assessments)
 * - RelatedPerson (Family members, caregivers, emergency contacts)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/care-management")
@RequiredArgsConstructor
public class SatusehatCareManagementController {

    private final CareManagementService careManagementService;

    // ========================================================================
    // EPISODE OF CARE ENDPOINTS
    // ========================================================================

    /**
     * Create an episode of care.
     *
     * POST /api/v1/satusehat/care-management/episode-of-care
     */
    @PostMapping("/episode-of-care")
    public ResponseEntity<EpisodeOfCare> createEpisodeOfCare(
        @RequestBody EpisodeOfCare episodeOfCare,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create EpisodeOfCare for organization: {}", organizationId);
        EpisodeOfCare created = careManagementService.createEpisodeOfCare(organizationId, episodeOfCare, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an episode of care.
     *
     * PUT /api/v1/satusehat/care-management/episode-of-care/{episodeId}
     */
    @PutMapping("/episode-of-care/{episodeId}")
    public ResponseEntity<EpisodeOfCare> updateEpisodeOfCare(
        @PathVariable String episodeId,
        @RequestBody EpisodeOfCare episodeOfCare,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update EpisodeOfCare: {}", episodeId);
        EpisodeOfCare updated = careManagementService.updateEpisodeOfCare(organizationId, episodeId, episodeOfCare, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get episode of care by ID.
     *
     * GET /api/v1/satusehat/care-management/episode-of-care/{episodeId}
     */
    @GetMapping("/episode-of-care/{episodeId}")
    public ResponseEntity<EpisodeOfCare> getEpisodeOfCareById(
        @PathVariable String episodeId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get EpisodeOfCare: {}", episodeId);
        EpisodeOfCare episodeOfCare = careManagementService.getEpisodeOfCareById(organizationId, episodeId, userId);
        return ResponseEntity.ok(episodeOfCare);
    }

    /**
     * Search episodes of care by patient.
     *
     * GET /api/v1/satusehat/care-management/episode-of-care/patient/{ihsNumber}
     */
    @GetMapping("/episode-of-care/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<EpisodeOfCare>> searchEpisodesOfCareByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search EpisodesOfCare for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<EpisodeOfCare> results =
            careManagementService.searchEpisodesOfCareByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search episodes of care by status.
     *
     * GET /api/v1/satusehat/care-management/episode-of-care/status/{status}
     */
    @GetMapping("/episode-of-care/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<EpisodeOfCare>> searchEpisodesOfCareByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search EpisodesOfCare by status: {}", status);
        ClinicalResourceService.SearchBundle<EpisodeOfCare> results =
            careManagementService.searchEpisodesOfCareByStatus(organizationId, status, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // CARE PLAN ENDPOINTS
    // ========================================================================

    /**
     * Create a care plan.
     *
     * POST /api/v1/satusehat/care-management/care-plan
     */
    @PostMapping("/care-plan")
    public ResponseEntity<CarePlan> createCarePlan(
        @RequestBody CarePlan carePlan,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create CarePlan for organization: {}", organizationId);
        CarePlan created = careManagementService.createCarePlan(organizationId, carePlan, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update a care plan.
     *
     * PUT /api/v1/satusehat/care-management/care-plan/{carePlanId}
     */
    @PutMapping("/care-plan/{carePlanId}")
    public ResponseEntity<CarePlan> updateCarePlan(
        @PathVariable String carePlanId,
        @RequestBody CarePlan carePlan,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update CarePlan: {}", carePlanId);
        CarePlan updated = careManagementService.updateCarePlan(organizationId, carePlanId, carePlan, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get care plan by ID.
     *
     * GET /api/v1/satusehat/care-management/care-plan/{carePlanId}
     */
    @GetMapping("/care-plan/{carePlanId}")
    public ResponseEntity<CarePlan> getCarePlanById(
        @PathVariable String carePlanId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get CarePlan: {}", carePlanId);
        CarePlan carePlan = careManagementService.getCarePlanById(organizationId, carePlanId, userId);
        return ResponseEntity.ok(carePlan);
    }

    /**
     * Search care plans by patient.
     *
     * GET /api/v1/satusehat/care-management/care-plan/patient/{ihsNumber}
     */
    @GetMapping("/care-plan/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<CarePlan>> searchCarePlansByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search CarePlans for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<CarePlan> results =
            careManagementService.searchCarePlansByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search care plans by status.
     *
     * GET /api/v1/satusehat/care-management/care-plan/status/{status}
     */
    @GetMapping("/care-plan/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<CarePlan>> searchCarePlansByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search CarePlans by status: {}", status);
        ClinicalResourceService.SearchBundle<CarePlan> results =
            careManagementService.searchCarePlansByStatus(organizationId, status, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search care plans by category.
     *
     * GET /api/v1/satusehat/care-management/care-plan/category/{categoryCode}
     */
    @GetMapping("/care-plan/category/{categoryCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<CarePlan>> searchCarePlansByCategory(
        @PathVariable String categoryCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search CarePlans by category: {}", categoryCode);
        ClinicalResourceService.SearchBundle<CarePlan> results =
            careManagementService.searchCarePlansByCategory(organizationId, categoryCode, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // QUESTIONNAIRE RESPONSE ENDPOINTS
    // ========================================================================

    /**
     * Create a questionnaire response.
     *
     * POST /api/v1/satusehat/care-management/questionnaire-response
     */
    @PostMapping("/questionnaire-response")
    public ResponseEntity<QuestionnaireResponse> createQuestionnaireResponse(
        @RequestBody QuestionnaireResponse questionnaireResponse,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create QuestionnaireResponse for organization: {}", organizationId);
        QuestionnaireResponse created = careManagementService.createQuestionnaireResponse(organizationId, questionnaireResponse, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update a questionnaire response.
     *
     * PUT /api/v1/satusehat/care-management/questionnaire-response/{responseId}
     */
    @PutMapping("/questionnaire-response/{responseId}")
    public ResponseEntity<QuestionnaireResponse> updateQuestionnaireResponse(
        @PathVariable String responseId,
        @RequestBody QuestionnaireResponse questionnaireResponse,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update QuestionnaireResponse: {}", responseId);
        QuestionnaireResponse updated = careManagementService.updateQuestionnaireResponse(organizationId, responseId, questionnaireResponse, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get questionnaire response by ID.
     *
     * GET /api/v1/satusehat/care-management/questionnaire-response/{responseId}
     */
    @GetMapping("/questionnaire-response/{responseId}")
    public ResponseEntity<QuestionnaireResponse> getQuestionnaireResponseById(
        @PathVariable String responseId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get QuestionnaireResponse: {}", responseId);
        QuestionnaireResponse questionnaireResponse = careManagementService.getQuestionnaireResponseById(organizationId, responseId, userId);
        return ResponseEntity.ok(questionnaireResponse);
    }

    /**
     * Search questionnaire responses by patient.
     *
     * GET /api/v1/satusehat/care-management/questionnaire-response/patient/{ihsNumber}
     */
    @GetMapping("/questionnaire-response/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<QuestionnaireResponse>> searchQuestionnaireResponsesByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search QuestionnaireResponses for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<QuestionnaireResponse> results =
            careManagementService.searchQuestionnaireResponsesByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search questionnaire responses by questionnaire.
     *
     * GET /api/v1/satusehat/care-management/questionnaire-response/questionnaire
     */
    @GetMapping("/questionnaire-response/questionnaire")
    public ResponseEntity<ClinicalResourceService.SearchBundle<QuestionnaireResponse>> searchQuestionnaireResponsesByQuestionnaire(
        @RequestParam String questionnaireUrl,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search QuestionnaireResponses for questionnaire: {}", questionnaireUrl);
        ClinicalResourceService.SearchBundle<QuestionnaireResponse> results =
            careManagementService.searchQuestionnaireResponsesByQuestionnaire(organizationId, questionnaireUrl, userId);
        return ResponseEntity.ok(results);
    }

    // ========================================================================
    // RELATED PERSON ENDPOINTS
    // ========================================================================

    /**
     * Create a related person.
     *
     * POST /api/v1/satusehat/care-management/related-person
     */
    @PostMapping("/related-person")
    public ResponseEntity<RelatedPerson> createRelatedPerson(
        @RequestBody RelatedPerson relatedPerson,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to create RelatedPerson for organization: {}", organizationId);
        RelatedPerson created = careManagementService.createRelatedPerson(organizationId, relatedPerson, userId);
        return ResponseEntity.ok(created);
    }

    /**
     * Update a related person.
     *
     * PUT /api/v1/satusehat/care-management/related-person/{relatedPersonId}
     */
    @PutMapping("/related-person/{relatedPersonId}")
    public ResponseEntity<RelatedPerson> updateRelatedPerson(
        @PathVariable String relatedPersonId,
        @RequestBody RelatedPerson relatedPerson,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to update RelatedPerson: {}", relatedPersonId);
        RelatedPerson updated = careManagementService.updateRelatedPerson(organizationId, relatedPersonId, relatedPerson, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get related person by ID.
     *
     * GET /api/v1/satusehat/care-management/related-person/{relatedPersonId}
     */
    @GetMapping("/related-person/{relatedPersonId}")
    public ResponseEntity<RelatedPerson> getRelatedPersonById(
        @PathVariable String relatedPersonId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to get RelatedPerson: {}", relatedPersonId);
        RelatedPerson relatedPerson = careManagementService.getRelatedPersonById(organizationId, relatedPersonId, userId);
        return ResponseEntity.ok(relatedPerson);
    }

    /**
     * Search related persons by patient.
     *
     * GET /api/v1/satusehat/care-management/related-person/patient/{ihsNumber}
     */
    @GetMapping("/related-person/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<RelatedPerson>> searchRelatedPersonsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search RelatedPersons for patient: {}", ihsNumber);
        ClinicalResourceService.SearchBundle<RelatedPerson> results =
            careManagementService.searchRelatedPersonsByPatient(organizationId, ihsNumber, userId);
        return ResponseEntity.ok(results);
    }

    /**
     * Search related persons by identifier (NIK).
     *
     * GET /api/v1/satusehat/care-management/related-person/identifier/{nik}
     */
    @GetMapping("/related-person/identifier/{nik}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<RelatedPerson>> searchRelatedPersonsByIdentifier(
        @PathVariable String nik,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Request to search RelatedPersons by identifier: {}", nik);
        ClinicalResourceService.SearchBundle<RelatedPerson> results =
            careManagementService.searchRelatedPersonsByIdentifier(organizationId, nik, userId);
        return ResponseEntity.ok(results);
    }
}
