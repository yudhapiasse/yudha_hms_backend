package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing Clinical Documentation FHIR resources.
 *
 * Handles:
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
@Service
@RequiredArgsConstructor
public class ClinicalDocumentService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ========================================================================
    // COMPOSITION OPERATIONS
    // ========================================================================

    /**
     * Create a composition (clinical document).
     */
    public Composition createComposition(String organizationId, Composition composition, UUID userId) {
        log.info("Creating Composition in SATUSEHAT for organization: {}", organizationId);

        if (composition.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        if (composition.getAuthor() == null || composition.getAuthor().isEmpty()) {
            throw new SatusehatValidationException("At least one author is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Composition created = httpClient.post(
            "/Composition",
            composition,
            config,
            Composition.class,
            userId
        );

        log.info("Composition created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update a composition.
     */
    public Composition updateComposition(String organizationId, String compositionId, Composition composition, UUID userId) {
        log.info("Updating Composition {} in SATUSEHAT", compositionId);

        if (composition.getId() == null) {
            composition.setId(compositionId);
        }

        var config = authService.getActiveConfig(organizationId);

        Composition updated = httpClient.put(
            "/Composition/" + compositionId,
            composition,
            config,
            Composition.class,
            userId
        );

        log.info("Composition {} updated successfully", compositionId);
        return updated;
    }

    /**
     * Get composition by ID.
     */
    public Composition getCompositionById(String organizationId, String compositionId, UUID userId) {
        log.info("Retrieving Composition {} from SATUSEHAT", compositionId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/Composition/" + compositionId,
            config,
            Composition.class,
            userId
        );
    }

    /**
     * Search compositions by patient.
     */
    public ClinicalResourceService.SearchBundle<Composition> searchCompositionsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching Compositions for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchCompositions(organizationId, params, userId);
    }

    /**
     * Search compositions by encounter.
     */
    public ClinicalResourceService.SearchBundle<Composition> searchCompositionsByEncounter(
        String organizationId,
        String encounterId,
        UUID userId
    ) {
        log.info("Searching Compositions for encounter {}", encounterId);

        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);

        return searchCompositions(organizationId, params, userId);
    }

    /**
     * Search compositions by type.
     */
    public ClinicalResourceService.SearchBundle<Composition> searchCompositionsByType(
        String organizationId,
        String typeCode,
        UUID userId
    ) {
        log.info("Searching Compositions by type {}", typeCode);

        Map<String, String> params = new HashMap<>();
        params.put("type", typeCode);

        return searchCompositions(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<Composition> searchCompositions(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Composition?");
        params.forEach((key, value) -> {
            if (queryString.length() > 13) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Composition> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // ALLERGY INTOLERANCE OPERATIONS
    // ========================================================================

    /**
     * Create an allergy intolerance record.
     */
    public AllergyIntolerance createAllergyIntolerance(
        String organizationId,
        AllergyIntolerance allergyIntolerance,
        UUID userId
    ) {
        log.info("Creating AllergyIntolerance in SATUSEHAT for organization: {}", organizationId);

        if (allergyIntolerance.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required");
        }
        if (allergyIntolerance.getCode() == null) {
            throw new SatusehatValidationException("Allergy code is required");
        }

        var config = authService.getActiveConfig(organizationId);

        AllergyIntolerance created = httpClient.post(
            "/AllergyIntolerance",
            allergyIntolerance,
            config,
            AllergyIntolerance.class,
            userId
        );

        log.info("AllergyIntolerance created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update an allergy intolerance record.
     */
    public AllergyIntolerance updateAllergyIntolerance(
        String organizationId,
        String allergyId,
        AllergyIntolerance allergyIntolerance,
        UUID userId
    ) {
        log.info("Updating AllergyIntolerance {} in SATUSEHAT", allergyId);

        if (allergyIntolerance.getId() == null) {
            allergyIntolerance.setId(allergyId);
        }

        var config = authService.getActiveConfig(organizationId);

        AllergyIntolerance updated = httpClient.put(
            "/AllergyIntolerance/" + allergyId,
            allergyIntolerance,
            config,
            AllergyIntolerance.class,
            userId
        );

        log.info("AllergyIntolerance {} updated successfully", allergyId);
        return updated;
    }

    /**
     * Get allergy intolerance by ID.
     */
    public AllergyIntolerance getAllergyIntoleranceById(
        String organizationId,
        String allergyId,
        UUID userId
    ) {
        log.info("Retrieving AllergyIntolerance {} from SATUSEHAT", allergyId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/AllergyIntolerance/" + allergyId,
            config,
            AllergyIntolerance.class,
            userId
        );
    }

    /**
     * Search allergy intolerances by patient.
     */
    public ClinicalResourceService.SearchBundle<AllergyIntolerance> searchAllergyIntolerancesByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching AllergyIntolerances for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("patient", "Patient/" + ihsNumber);

        return searchAllergyIntolerances(organizationId, params, userId);
    }

    /**
     * Search allergy intolerances by category.
     */
    public ClinicalResourceService.SearchBundle<AllergyIntolerance> searchAllergyIntolerancesByCategory(
        String organizationId,
        String category,
        UUID userId
    ) {
        log.info("Searching AllergyIntolerances by category {}", category);

        Map<String, String> params = new HashMap<>();
        params.put("category", category);

        return searchAllergyIntolerances(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<AllergyIntolerance> searchAllergyIntolerances(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/AllergyIntolerance?");
        params.forEach((key, value) -> {
            if (queryString.length() > 21) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<AllergyIntolerance> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // CLINICAL IMPRESSION OPERATIONS
    // ========================================================================

    /**
     * Create a clinical impression.
     */
    public ClinicalImpression createClinicalImpression(
        String organizationId,
        ClinicalImpression clinicalImpression,
        UUID userId
    ) {
        log.info("Creating ClinicalImpression in SATUSEHAT for organization: {}", organizationId);

        if (clinicalImpression.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }

        var config = authService.getActiveConfig(organizationId);

        ClinicalImpression created = httpClient.post(
            "/ClinicalImpression",
            clinicalImpression,
            config,
            ClinicalImpression.class,
            userId
        );

        log.info("ClinicalImpression created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update a clinical impression.
     */
    public ClinicalImpression updateClinicalImpression(
        String organizationId,
        String impressionId,
        ClinicalImpression clinicalImpression,
        UUID userId
    ) {
        log.info("Updating ClinicalImpression {} in SATUSEHAT", impressionId);

        if (clinicalImpression.getId() == null) {
            clinicalImpression.setId(impressionId);
        }

        var config = authService.getActiveConfig(organizationId);

        ClinicalImpression updated = httpClient.put(
            "/ClinicalImpression/" + impressionId,
            clinicalImpression,
            config,
            ClinicalImpression.class,
            userId
        );

        log.info("ClinicalImpression {} updated successfully", impressionId);
        return updated;
    }

    /**
     * Get clinical impression by ID.
     */
    public ClinicalImpression getClinicalImpressionById(
        String organizationId,
        String impressionId,
        UUID userId
    ) {
        log.info("Retrieving ClinicalImpression {} from SATUSEHAT", impressionId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/ClinicalImpression/" + impressionId,
            config,
            ClinicalImpression.class,
            userId
        );
    }

    /**
     * Search clinical impressions by patient.
     */
    public ClinicalResourceService.SearchBundle<ClinicalImpression> searchClinicalImpressionsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching ClinicalImpressions for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);

        return searchClinicalImpressions(organizationId, params, userId);
    }

    /**
     * Search clinical impressions by encounter.
     */
    public ClinicalResourceService.SearchBundle<ClinicalImpression> searchClinicalImpressionsByEncounter(
        String organizationId,
        String encounterId,
        UUID userId
    ) {
        log.info("Searching ClinicalImpressions for encounter {}", encounterId);

        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);

        return searchClinicalImpressions(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<ClinicalImpression> searchClinicalImpressions(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/ClinicalImpression?");
        params.forEach((key, value) -> {
            if (queryString.length() > 21) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<ClinicalImpression> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // IMMUNIZATION OPERATIONS
    // ========================================================================

    /**
     * Create an immunization record.
     */
    public Immunization createImmunization(
        String organizationId,
        Immunization immunization,
        UUID userId
    ) {
        log.info("Creating Immunization in SATUSEHAT for organization: {}", organizationId);

        if (immunization.getPatient() == null) {
            throw new SatusehatValidationException("Patient reference is required");
        }
        if (immunization.getVaccineCode() == null) {
            throw new SatusehatValidationException("Vaccine code is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Immunization created = httpClient.post(
            "/Immunization",
            immunization,
            config,
            Immunization.class,
            userId
        );

        log.info("Immunization created successfully with ID: {}", created.getId());
        return created;
    }

    /**
     * Update an immunization record.
     */
    public Immunization updateImmunization(
        String organizationId,
        String immunizationId,
        Immunization immunization,
        UUID userId
    ) {
        log.info("Updating Immunization {} in SATUSEHAT", immunizationId);

        if (immunization.getId() == null) {
            immunization.setId(immunizationId);
        }

        var config = authService.getActiveConfig(organizationId);

        Immunization updated = httpClient.put(
            "/Immunization/" + immunizationId,
            immunization,
            config,
            Immunization.class,
            userId
        );

        log.info("Immunization {} updated successfully", immunizationId);
        return updated;
    }

    /**
     * Get immunization by ID.
     */
    public Immunization getImmunizationById(
        String organizationId,
        String immunizationId,
        UUID userId
    ) {
        log.info("Retrieving Immunization {} from SATUSEHAT", immunizationId);

        var config = authService.getActiveConfig(organizationId);

        return httpClient.get(
            "/Immunization/" + immunizationId,
            config,
            Immunization.class,
            userId
        );
    }

    /**
     * Search immunizations by patient.
     */
    public ClinicalResourceService.SearchBundle<Immunization> searchImmunizationsByPatient(
        String organizationId,
        String ihsNumber,
        UUID userId
    ) {
        log.info("Searching Immunizations for patient {}", ihsNumber);

        Map<String, String> params = new HashMap<>();
        params.put("patient", "Patient/" + ihsNumber);

        return searchImmunizations(organizationId, params, userId);
    }

    /**
     * Search immunizations by vaccine type.
     */
    public ClinicalResourceService.SearchBundle<Immunization> searchImmunizationsByVaccineType(
        String organizationId,
        String vaccineCode,
        UUID userId
    ) {
        log.info("Searching Immunizations by vaccine type {}", vaccineCode);

        Map<String, String> params = new HashMap<>();
        params.put("vaccine-code", vaccineCode);

        return searchImmunizations(organizationId, params, userId);
    }

    /**
     * Search immunizations by status.
     */
    public ClinicalResourceService.SearchBundle<Immunization> searchImmunizationsByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching Immunizations by status {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchImmunizations(organizationId, params, userId);
    }

    private ClinicalResourceService.SearchBundle<Immunization> searchImmunizations(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Immunization?");
        params.forEach((key, value) -> {
            if (queryString.length() > 15) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Immunization> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
