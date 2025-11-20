package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Patient;
import com.yudha.hms.integration.satusehat.exception.SatusehatIntegrationException;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing FHIR Patient resources in SATUSEHAT.
 *
 * Provides CRUD operations for Patient resources:
 * - Create patient with NIK validation
 * - Update patient by IHS number
 * - Search patient by identifier, name, birthdate
 * - Get patient by IHS number
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientResourceService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    /**
     * Create a new patient in SATUSEHAT.
     *
     * @param organizationId Organization identifier
     * @param patient FHIR Patient resource
     * @param userId User performing the operation
     * @return Created patient with IHS number assigned
     * @throws SatusehatValidationException if NIK is invalid or patient data is incomplete
     * @throws SatusehatIntegrationException if creation fails
     */
    public Patient createPatient(String organizationId, Patient patient, UUID userId) {
        log.info("Creating patient in SATUSEHAT for organization: {}", organizationId);

        // Validate NIK presence
        String nik = patient.getNik();
        if (nik == null || nik.isEmpty()) {
            throw new SatusehatValidationException("NIK is required for patient creation");
        }

        // Get config
        var config = authService.getActiveConfig(organizationId);

        // POST /Patient
        Patient createdPatient = httpClient.post(
            "/Patient",
            patient,
            config,
            Patient.class,
            userId
        );

        log.info("Patient created successfully with IHS number: {}", createdPatient.getId());
        return createdPatient;
    }

    /**
     * Update an existing patient in SATUSEHAT.
     *
     * @param organizationId Organization identifier
     * @param ihsNumber IHS number (SATUSEHAT patient ID)
     * @param patient Updated FHIR Patient resource
     * @param userId User performing the operation
     * @return Updated patient
     * @throws SatusehatIntegrationException if update fails
     */
    public Patient updatePatient(String organizationId, String ihsNumber, Patient patient, UUID userId) {
        log.info("Updating patient {} in SATUSEHAT for organization: {}", ihsNumber, organizationId);

        // Ensure patient ID matches
        if (patient.getId() == null) {
            patient.setId(ihsNumber);
        }

        // Get config
        var config = authService.getActiveConfig(organizationId);

        // PUT /Patient/{ihs-number}
        Patient updatedPatient = httpClient.put(
            "/Patient/" + ihsNumber,
            patient,
            config,
            Patient.class,
            userId
        );

        log.info("Patient {} updated successfully", ihsNumber);
        return updatedPatient;
    }

    /**
     * Get patient by IHS number.
     *
     * @param organizationId Organization identifier
     * @param ihsNumber IHS number (SATUSEHAT patient ID)
     * @param userId User performing the operation
     * @return Patient resource
     * @throws SatusehatIntegrationException if patient not found or retrieval fails
     */
    public Patient getPatientByIhsNumber(String organizationId, String ihsNumber, UUID userId) {
        log.info("Retrieving patient {} from SATUSEHAT for organization: {}", ihsNumber, organizationId);

        // Get config
        var config = authService.getActiveConfig(organizationId);

        // GET /Patient/{ihs-number}
        Patient patient = httpClient.get(
            "/Patient/" + ihsNumber,
            config,
            Patient.class,
            userId
        );

        log.info("Patient {} retrieved successfully", ihsNumber);
        return patient;
    }

    /**
     * Search patient by NIK.
     *
     * @param organizationId Organization identifier
     * @param nik NIK (National Identity Number)
     * @param userId User performing the operation
     * @return Search result bundle
     * @throws SatusehatIntegrationException if search fails
     */
    public Bundle searchPatientByNik(String organizationId, String nik, UUID userId) {
        log.info("Searching patient by NIK in SATUSEHAT for organization: {}", organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("identifier", "https://fhir.kemkes.go.id/id/nik|" + nik);

        return searchPatient(organizationId, params, userId);
    }

    /**
     * Search patient by name.
     *
     * @param organizationId Organization identifier
     * @param name Patient name
     * @param userId User performing the operation
     * @return Search result bundle
     * @throws SatusehatIntegrationException if search fails
     */
    public Bundle searchPatientByName(String organizationId, String name, UUID userId) {
        log.info("Searching patient by name in SATUSEHAT for organization: {}", organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return searchPatient(organizationId, params, userId);
    }

    /**
     * Search patient by birthdate.
     *
     * @param organizationId Organization identifier
     * @param birthdate Birth date in YYYY-MM-DD format
     * @param userId User performing the operation
     * @return Search result bundle
     * @throws SatusehatIntegrationException if search fails
     */
    public Bundle searchPatientByBirthdate(String organizationId, String birthdate, UUID userId) {
        log.info("Searching patient by birthdate in SATUSEHAT for organization: {}", organizationId);

        Map<String, String> params = new HashMap<>();
        params.put("birthdate", birthdate);

        return searchPatient(organizationId, params, userId);
    }

    /**
     * Search patient by multiple criteria.
     *
     * @param organizationId Organization identifier
     * @param name Patient name (optional)
     * @param birthdate Birth date in YYYY-MM-DD format (optional)
     * @param gender Gender (male|female|other|unknown) (optional)
     * @param userId User performing the operation
     * @return Search result bundle
     * @throws SatusehatIntegrationException if search fails
     */
    public Bundle searchPatient(String organizationId, String name, String birthdate, String gender, UUID userId) {
        log.info("Searching patient with multiple criteria in SATUSEHAT for organization: {}", organizationId);

        Map<String, String> params = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            params.put("name", name);
        }
        if (birthdate != null && !birthdate.isEmpty()) {
            params.put("birthdate", birthdate);
        }
        if (gender != null && !gender.isEmpty()) {
            params.put("gender", gender);
        }

        return searchPatient(organizationId, params, userId);
    }

    /**
     * Generic search method.
     *
     * @param organizationId Organization identifier
     * @param searchParams Search parameters
     * @param userId User performing the operation
     * @return Search result bundle
     * @throws SatusehatIntegrationException if search fails
     */
    private Bundle searchPatient(String organizationId, Map<String, String> searchParams, UUID userId) {
        // Build query string
        StringBuilder queryString = new StringBuilder("/Patient?");
        searchParams.forEach((key, value) -> {
            if (queryString.length() > 9) {
                queryString.append("&");
            }
            queryString.append(key).append("=").append(value);
        });

        log.debug("Searching patient with query: {}", queryString);

        // Get config
        var config = authService.getActiveConfig(organizationId);

        // GET /Patient?{params}
        Bundle bundle = httpClient.get(
            queryString.toString(),
            config,
            Bundle.class,
            userId
        );

        log.info("Patient search completed, found {} results", bundle.getTotal());
        return bundle;
    }

    /**
     * FHIR Bundle for search results.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    public static class Bundle {
        @com.fasterxml.jackson.annotation.JsonProperty("resourceType")
        private String resourceType = "Bundle";

        @com.fasterxml.jackson.annotation.JsonProperty("type")
        private String type;

        @com.fasterxml.jackson.annotation.JsonProperty("total")
        private Integer total;

        @com.fasterxml.jackson.annotation.JsonProperty("link")
        private java.util.List<Link> link;

        @com.fasterxml.jackson.annotation.JsonProperty("entry")
        private java.util.List<Entry> entry;

        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
        public static class Link {
            @com.fasterxml.jackson.annotation.JsonProperty("relation")
            private String relation;

            @com.fasterxml.jackson.annotation.JsonProperty("url")
            private String url;
        }

        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
        public static class Entry {
            @com.fasterxml.jackson.annotation.JsonProperty("fullUrl")
            private String fullUrl;

            @com.fasterxml.jackson.annotation.JsonProperty("resource")
            private Patient resource;

            @com.fasterxml.jackson.annotation.JsonProperty("search")
            private Search search;

            @lombok.Data
            @lombok.Builder
            @lombok.NoArgsConstructor
            @lombok.AllArgsConstructor
            @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
            public static class Search {
                @com.fasterxml.jackson.annotation.JsonProperty("mode")
                private String mode;

                @com.fasterxml.jackson.annotation.JsonProperty("score")
                private Double score;
            }
        }
    }
}
