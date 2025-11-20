package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.service.SatusehatHttpClient;
import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing Care Coordination & Management FHIR resources.
 *
 * Handles:
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
@Service
@RequiredArgsConstructor
public class CareManagementService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ========================================================================
    // EPISODE OF CARE OPERATIONS
    // ========================================================================

    /**
     * Create an episode of care.
     */
    public EpisodeOfCare createEpisodeOfCare(
        String organizationId,
        EpisodeOfCare episodeOfCare,
        UUID userId
    ) {
        log.info("Creating EpisodeOfCare in SATUSEHAT for organization: {}", organizationId);

        if (episodeOfCare.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required");
        }
        if (episodeOfCare.getStatus() == null) {
            throw new SatusehatValidationException("Status is required");
        }

        var config = authService.getActiveConfig(organizationId);

        EpisodeOfCare created = httpClient.post(
            "/EpisodeOfCare",
            episodeOfCare,
            config,
            EpisodeOfCare.class,
            userId
        );

        log.info("EpisodeOfCare created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update an episode of care.
     */
    public EpisodeOfCare updateEpisodeOfCare(
        String organizationId,
        String episodeId,
        EpisodeOfCare episodeOfCare,
        UUID userId
    ) {
        log.info("Updating EpisodeOfCare {} in SATUSEHAT", episodeId);

        if (episodeOfCare.getId() == null) {
            episodeOfCare.setId(episodeId);
        }

        var config = authService.getActiveConfig(organizationId);

        EpisodeOfCare updated = httpClient.put(
            "/EpisodeOfCare/" + episodeId,
            episodeOfCare,
            config,
            EpisodeOfCare.class,
            userId
        );

        log.info("EpisodeOfCare {} updated successfully", episodeId);
        return updated;
    }

    /**
     * Get episode of care by ID.
     */
    public EpisodeOfCare getEpisodeOfCareById(
        String organizationId,
        String episodeId,
        UUID userId
    ) {
        log.info("Retrieving EpisodeOfCare {} from SATUSEHAT", episodeId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/EpisodeOfCare/" + episodeId,
            config,
            EpisodeOfCare.class,
            userId
        );
    }

    /**
     * Search episodes of care by patient.
     */
    public ClinicalResourceService.SearchBundle<EpisodeOfCare> searchEpisodesOfCareByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching EpisodesOfCare for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("patient", "Patient/" + ihsNumber);

        return searchEpisodesOfCare(organizationId, params, userId);
    }

    /**
     * Search episodes of care by status.
     */
    public ClinicalResourceService.SearchBundle<EpisodeOfCare> searchEpisodesOfCareByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching EpisodesOfCare by status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchEpisodesOfCare(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<EpisodeOfCare> searchEpisodesOfCare(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/EpisodeOfCare?");
        params.forEach((key, value) -> {
            if (queryString.length() > 16) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<EpisodeOfCare> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // CARE PLAN OPERATIONS
    // ========================================================================

    /**
     * Create a care plan.
     */
    public CarePlan createCarePlan(
        String organizationId,
        CarePlan carePlan,
        UUID userId
    ) {
        log.info("Creating CarePlan in SATUSEHAT for organization: {}", organizationId);

        if (carePlan.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        if (carePlan.getStatus() == null) {
            throw new SatusehatValidationException("Status is required");
        }
        if (carePlan.getIntent() == null) {
            throw new SatusehatValidationException("Intent is required");
        }

        var config = authService.getActiveConfig(organizationId);

        CarePlan created = httpClient.post(
            "/CarePlan",
            carePlan,
            config,
            CarePlan.class,
            userId
        );

        log.info("CarePlan created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update a care plan.
     */
    public CarePlan updateCarePlan(
        String organizationId,
        String carePlanId,
        CarePlan carePlan,
        UUID userId
    ) {
        log.info("Updating CarePlan {} in SATUSEHAT", carePlanId);

        if (carePlan.getId() == null) {
            carePlan.setId(carePlanId);
        }

        var config = authService.getActiveConfig(organizationId);

        CarePlan updated = httpClient.put(
            "/CarePlan/" + carePlanId,
            carePlan,
            config,
            CarePlan.class,
            userId
        );

        log.info("CarePlan {} updated successfully", carePlanId);
        return updated;
    }

    /**
     * Get care plan by ID.
     */
    public CarePlan getCarePlanById(
        String organizationId,
        String carePlanId,
        UUID userId
    ) {
        log.info("Retrieving CarePlan {} from SATUSEHAT", carePlanId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/CarePlan/" + carePlanId,
            config,
            CarePlan.class,
            userId
        );
    }

    /**
     * Search care plans by patient.
     */
    public ClinicalResourceService.SearchBundle<CarePlan> searchCarePlansByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching CarePlans for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchCarePlans(organizationId, params, userId);
    }

    /**
     * Search care plans by status.
     */
    public ClinicalResourceService.SearchBundle<CarePlan> searchCarePlansByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching CarePlans by status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchCarePlans(organizationId, params, userId);
    }

    /**
     * Search care plans by category.
     */
    public ClinicalResourceService.SearchBundle<CarePlan> searchCarePlansByCategory(
        String organizationId,
        String categoryCode,
        UUID userId
    ) {
        log.info("Searching CarePlans by category {}", categoryCode);

        Map<String, String> params = new HashMap<>();
        params.put("category", categoryCode);

        return searchCarePlans(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<CarePlan> searchCarePlans(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/CarePlan?");
        params.forEach((key, value) -> {
            if (queryString.length() > 10) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<CarePlan> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // QUESTIONNAIRE RESPONSE OPERATIONS
    // ========================================================================

    /**
     * Create a questionnaire response.
     */
    public QuestionnaireResponse createQuestionnaireResponse(
        String organizationId,
        QuestionnaireResponse questionnaireResponse,
        UUID userId
    ) {
        log.info("Creating QuestionnaireResponse in SATUSEHAT for organization: {}", organizationId);

        if (questionnaireResponse.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        if (questionnaireResponse.getStatus() == null) {
            throw new SatusehatValidationException("Status is required");
        }

        var config = authService.getActiveConfig(organizationId);

        QuestionnaireResponse created = httpClient.post(
            "/QuestionnaireResponse",
            questionnaireResponse,
            config,
            QuestionnaireResponse.class,
            userId
        );

        log.info("QuestionnaireResponse created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update a questionnaire response.
     */
    public QuestionnaireResponse updateQuestionnaireResponse(
        String organizationId,
        String responseId,
        QuestionnaireResponse questionnaireResponse,
        UUID userId
    ) {
        log.info("Updating QuestionnaireResponse {} in SATUSEHAT", responseId);

        if (questionnaireResponse.getId() == null) {
            questionnaireResponse.setId(responseId);
        }

        var config = authService.getActiveConfig(organizationId);

        QuestionnaireResponse updated = httpClient.put(
            "/QuestionnaireResponse/" + responseId,
            questionnaireResponse,
            config,
            QuestionnaireResponse.class,
            userId
        );

        log.info("QuestionnaireResponse {} updated successfully", responseId);
        return updated;
    }

    /**
     * Get questionnaire response by ID.
     */
    public QuestionnaireResponse getQuestionnaireResponseById(
        String organizationId,
        String responseId,
        UUID userId
    ) {
        log.info("Retrieving QuestionnaireResponse {} from SATUSEHAT", responseId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/QuestionnaireResponse/" + responseId,
            config,
            QuestionnaireResponse.class,
            userId
        );
    }

    /**
     * Search questionnaire responses by patient.
     */
    public ClinicalResourceService.SearchBundle<QuestionnaireResponse> searchQuestionnaireResponsesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching QuestionnaireResponses for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchQuestionnaireResponses(organizationId, params, userId);
    }

    /**
     * Search questionnaire responses by questionnaire.
     */
    public ClinicalResourceService.SearchBundle<QuestionnaireResponse> searchQuestionnaireResponsesByQuestionnaire(
        String organizationId,
        String questionnaireUrl,
        UUID userId
    ) {
        log.info("Searching QuestionnaireResponses for questionnaire {}", questionnaireUrl);

        Map<String, String> params = new HashMap<>();
        params.put("questionnaire", questionnaireUrl);

        return searchQuestionnaireResponses(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<QuestionnaireResponse> searchQuestionnaireResponses(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/QuestionnaireResponse?");
        params.forEach((key, value) -> {
            if (queryString.length() > 24) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<QuestionnaireResponse> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // RELATED PERSON OPERATIONS
    // ========================================================================

    /**
     * Create a related person.
     */
    public RelatedPerson createRelatedPerson(
        String organizationId,
        RelatedPerson relatedPerson,
        UUID userId
    ) {
        log.info("Creating RelatedPerson in SATUSEHAT for organization: {}", organizationId);

        if (relatedPerson.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required");
        }

        var config = authService.getActiveConfig(organizationId);

        RelatedPerson created = httpClient.post(
            "/RelatedPerson",
            relatedPerson,
            config,
            RelatedPerson.class,
            userId
        );

        log.info("RelatedPerson created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update a related person.
     */
    public RelatedPerson updateRelatedPerson(
        String organizationId,
        String relatedPersonId,
        RelatedPerson relatedPerson,
        UUID userId
    ) {
        log.info("Updating RelatedPerson {} in SATUSEHAT", relatedPersonId);

        if (relatedPerson.getId() == null) {
            relatedPerson.setId(relatedPersonId);
        }

        var config = authService.getActiveConfig(organizationId);

        RelatedPerson updated = httpClient.put(
            "/RelatedPerson/" + relatedPersonId,
            relatedPerson,
            config,
            RelatedPerson.class,
            userId
        );

        log.info("RelatedPerson {} updated successfully", relatedPersonId);
        return updated;
    }

    /**
     * Get related person by ID.
     */
    public RelatedPerson getRelatedPersonById(
        String organizationId,
        String relatedPersonId,
        UUID userId
    ) {
        log.info("Retrieving RelatedPerson {} from SATUSEHAT", relatedPersonId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/RelatedPerson/" + relatedPersonId,
            config,
            RelatedPerson.class,
            userId
        );
    }

    /**
     * Search related persons by patient.
     */
    public ClinicalResourceService.SearchBundle<RelatedPerson> searchRelatedPersonsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching RelatedPersons for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("patient", "Patient/" + ihsNumber);

        return searchRelatedPersons(organizationId, params, userId);
    }

    /**
     * Search related persons by identifier (NIK).
     */
    public ClinicalResourceService.SearchBundle<RelatedPerson> searchRelatedPersonsByIdentifier(
        String organizationId,
        String nik,
        UUID userId
    ) {
        log.info("Searching RelatedPersons by identifier {}", nik);

        Map<String, String> params = new HashMap<>();
        params.put("identifier", nik);

        return searchRelatedPersons(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<RelatedPerson> searchRelatedPersons(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/RelatedPerson?");
        params.forEach((key, value) -> {
            if (queryString.length() > 15) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<RelatedPerson> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
