package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for managing clinical FHIR resources in SATUSEHAT.
 * 
 * Handles:
 * - Condition (Diagnosis)
 * - Observation (Vital Signs, Lab Results)
 * - Transaction Bundle submissions
 * 
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicalResourceService {
    
    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;
    
    // ========================================================================
    // CONDITION (DIAGNOSIS) OPERATIONS
    // ========================================================================
    
    /**
     * Create a condition (diagnosis) in SATUSEHAT.
     */
    public Condition createCondition(String organizationId, Condition condition, UUID userId) {
        log.info("Creating condition in SATUSEHAT for organization: {}", organizationId);
        
        if (condition.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        
        var config = authService.getActiveConfig(organizationId);
        
        Condition createdCondition = httpClient.post(
            "/Condition",
            condition,
            config,
            Condition.class,
            userId
        );
        
        log.info("Condition created successfully with ID: {}", createdCondition.getId());
        return createdCondition;
    }
    
    /**
     * Update a condition in SATUSEHAT.
     */
    public Condition updateCondition(String organizationId, String conditionId, Condition condition, UUID userId) {
        log.info("Updating condition {} in SATUSEHAT", conditionId);
        
        if (condition.getId() == null) {
            condition.setId(conditionId);
        }
        
        var config = authService.getActiveConfig(organizationId);
        
        Condition updatedCondition = httpClient.put(
            "/Condition/" + conditionId,
            condition,
            config,
            Condition.class,
            userId
        );
        
        log.info("Condition {} updated successfully", conditionId);
        return updatedCondition;
    }
    
    /**
     * Get condition by ID.
     */
    public Condition getConditionById(String organizationId, String conditionId, UUID userId) {
        log.info("Retrieving condition {} from SATUSEHAT", conditionId);
        
        var config = authService.getActiveConfig(organizationId);
        
        Condition condition = httpClient.get(
            "/Condition/" + conditionId,
            config,
            Condition.class,
            userId
        );
        
        return condition;
    }
    
    /**
     * Search conditions by patient.
     */
    public SearchBundle<Condition> searchConditionsByPatient(String organizationId, String ihsNumber, UUID userId) {
        log.info("Searching conditions for patient {}", ihsNumber);
        
        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);
        
        return searchConditions(organizationId, params, userId);
    }
    
    /**
     * Search conditions by encounter.
     */
    public SearchBundle<Condition> searchConditionsByEncounter(String organizationId, String encounterId, UUID userId) {
        log.info("Searching conditions for encounter {}", encounterId);
        
        Map<String, String> params = new HashMap<>();
        params.put("encounter", "Encounter/" + encounterId);
        
        return searchConditions(organizationId, params, userId);
    }
    
    private SearchBundle<Condition> searchConditions(String organizationId, Map<String, String> params, UUID userId) {
        StringBuilder queryString = new StringBuilder("/Condition?");
        params.forEach((key, value) -> {
            if (queryString.length() > 12) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });
        
        var config = authService.getActiveConfig(organizationId);
        
        @SuppressWarnings("unchecked")
        SearchBundle<Condition> bundle = httpClient.get(
            queryString.toString(),
            config,
            SearchBundle.class,
            userId
        );
        
        return bundle;
    }
    
    // ========================================================================
    // OBSERVATION OPERATIONS
    // ========================================================================
    
    /**
     * Create an observation in SATUSEHAT.
     */
    public Observation createObservation(String organizationId, Observation observation, UUID userId) {
        log.info("Creating observation in SATUSEHAT for organization: {}", organizationId);
        
        if (observation.getSubject() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required");
        }
        
        var config = authService.getActiveConfig(organizationId);
        
        Observation createdObservation = httpClient.post(
            "/Observation",
            observation,
            config,
            Observation.class,
            userId
        );
        
        log.info("Observation created successfully with ID: {}", createdObservation.getId());
        return createdObservation;
    }
    
    /**
     * Search observations by patient.
     */
    public SearchBundle<Observation> searchObservationsByPatient(String organizationId, String ihsNumber, UUID userId) {
        log.info("Searching observations for patient {}", ihsNumber);
        
        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);
        
        return searchObservations(organizationId, params, userId);
    }
    
    /**
     * Search observations by category.
     */
    public SearchBundle<Observation> searchObservationsByCategory(
        String organizationId, 
        String category, 
        UUID userId
    ) {
        log.info("Searching observations with category {}", category);
        
        Map<String, String> params = new HashMap<>();
        params.put("category", category);
        
        return searchObservations(organizationId, params, userId);
    }
    
    private SearchBundle<Observation> searchObservations(String organizationId, Map<String, String> params, UUID userId) {
        StringBuilder queryString = new StringBuilder("/Observation?");
        params.forEach((key, value) -> {
            if (queryString.length() > 14) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });
        
        var config = authService.getActiveConfig(organizationId);
        
        @SuppressWarnings("unchecked")
        SearchBundle<Observation> bundle = httpClient.get(
            queryString.toString(),
            config,
            SearchBundle.class,
            userId
        );
        
        return bundle;
    }
    
    // ========================================================================
    // TRANSACTION BUNDLE OPERATIONS
    // ========================================================================
    
    /**
     * Submit a transaction bundle to SATUSEHAT.
     * 
     * This allows submitting multiple resources (Encounter, Condition, Observation, etc.)
     * in a single transaction.
     */
    public TransactionBundle submitTransactionBundle(
        String organizationId,
        TransactionBundle bundle,
        UUID userId
    ) {
        log.info("Submitting transaction bundle with {} entries to SATUSEHAT",
            bundle.getEntry() != null ? bundle.getEntry().size() : 0);
        
        if (bundle.getEntry() == null || bundle.getEntry().isEmpty()) {
            throw new SatusehatValidationException("Transaction bundle must contain at least one entry");
        }
        
        var config = authService.getActiveConfig(organizationId);
        
        TransactionBundle responseBundle = httpClient.post(
            "/",
            bundle,
            config,
            TransactionBundle.class,
            userId
        );
        
        // Count successes and failures
        int successCount = 0;
        int failureCount = 0;
        
        if (responseBundle.getEntry() != null) {
            for (TransactionBundle.Entry entry : responseBundle.getEntry()) {
                if (entry.getResponse() != null) {
                    String status = entry.getResponse().getStatus();
                    if (status != null && status.startsWith("20")) {
                        successCount++;
                    } else {
                        failureCount++;
                    }
                }
            }
        }
        
        log.info("Transaction bundle completed: {} success, {} failed", successCount, failureCount);
        
        return responseBundle;
    }
    
    /**
     * Build a transaction bundle for a complete encounter with clinical data.
     */
    public TransactionBundle buildEncounterBundle(
        Encounter encounter,
        List<Condition> conditions,
        List<Observation> observations
    ) {
        List<TransactionBundle.Entry> entries = new ArrayList<>();
        
        // Add encounter
        if (encounter != null) {
            entries.add(TransactionBundle.Entry.builder()
                .fullUrl("urn:uuid:encounter-" + UUID.randomUUID())
                .resource(encounter)
                .request(TransactionBundle.Request.builder()
                    .method("POST")
                    .url("Encounter")
                    .build())
                .build());
        }
        
        // Add conditions
        if (conditions != null) {
            for (int i = 0; i < conditions.size(); i++) {
                entries.add(TransactionBundle.Entry.builder()
                    .fullUrl("urn:uuid:condition-" + i)
                    .resource(conditions.get(i))
                    .request(TransactionBundle.Request.builder()
                        .method("POST")
                        .url("Condition")
                        .build())
                    .build());
            }
        }
        
        // Add observations
        if (observations != null) {
            for (int i = 0; i < observations.size(); i++) {
                entries.add(TransactionBundle.Entry.builder()
                    .fullUrl("urn:uuid:observation-" + i)
                    .resource(observations.get(i))
                    .request(TransactionBundle.Request.builder()
                        .method("POST")
                        .url("Observation")
                        .build())
                    .build());
            }
        }
        
        return TransactionBundle.builder()
            .resourceType("Bundle")
            .type("transaction")
            .entry(entries)
            .build();
    }
    
    /**
     * Generic search bundle for any resource type.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SearchBundle<T> {
        @com.fasterxml.jackson.annotation.JsonProperty("resourceType")
        private String resourceType = "Bundle";
        
        @com.fasterxml.jackson.annotation.JsonProperty("type")
        private String type;
        
        @com.fasterxml.jackson.annotation.JsonProperty("total")
        private Integer total;
        
        @com.fasterxml.jackson.annotation.JsonProperty("entry")
        private List<SearchEntry<T>> entry;
        
        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        public static class SearchEntry<T> {
            @com.fasterxml.jackson.annotation.JsonProperty("fullUrl")
            private String fullUrl;
            
            @com.fasterxml.jackson.annotation.JsonProperty("resource")
            private T resource;
        }
    }
}
