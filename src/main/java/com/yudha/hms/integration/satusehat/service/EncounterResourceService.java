package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Encounter;
import com.yudha.hms.integration.satusehat.exception.SatusehatIntegrationException;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for managing FHIR Encounter resources in SATUSEHAT.
 * 
 * Provides CRUD operations and batch processing for Encounter resources.
 * 
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EncounterResourceService {
    
    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;
    
    /**
     * Create a new encounter in SATUSEHAT.
     */
    public Encounter createEncounter(String organizationId, Encounter encounter, UUID userId) {
        log.info("Creating encounter in SATUSEHAT for organization: {}", organizationId);
        
        // Validate subject (patient reference) is present
        if (encounter.getSubject() == null || encounter.getSubject().getReference() == null) {
            throw new SatusehatValidationException("Subject (Patient reference) is required for encounter creation");
        }
        
        var config = authService.getActiveConfig(organizationId);
        
        Encounter createdEncounter = httpClient.post(
            "/Encounter",
            encounter,
            config,
            Encounter.class,
            userId
        );
        
        log.info("Encounter created successfully with ID: {}", createdEncounter.getId());
        return createdEncounter;
    }
    
    /**
     * Update an existing encounter in SATUSEHAT.
     */
    public Encounter updateEncounter(String organizationId, String encounterId, Encounter encounter, UUID userId) {
        log.info("Updating encounter {} in SATUSEHAT for organization: {}", encounterId, organizationId);
        
        // Ensure encounter ID matches
        if (encounter.getId() == null) {
            encounter.setId(encounterId);
        }
        
        var config = authService.getActiveConfig(organizationId);
        
        Encounter updatedEncounter = httpClient.put(
            "/Encounter/" + encounterId,
            encounter,
            config,
            Encounter.class,
            userId
        );
        
        log.info("Encounter {} updated successfully", encounterId);
        return updatedEncounter;
    }
    
    /**
     * Get encounter by ID.
     */
    public Encounter getEncounterById(String organizationId, String encounterId, UUID userId) {
        log.info("Retrieving encounter {} from SATUSEHAT for organization: {}", encounterId, organizationId);
        
        var config = authService.getActiveConfig(organizationId);
        
        Encounter encounter = httpClient.get(
            "/Encounter/" + encounterId,
            config,
            Encounter.class,
            userId
        );
        
        log.info("Encounter {} retrieved successfully", encounterId);
        return encounter;
    }
    
    /**
     * Search encounters by patient IHS number.
     */
    public Bundle searchEncountersByPatient(String organizationId, String ihsNumber, UUID userId) {
        log.info("Searching encounters for patient {} in SATUSEHAT", ihsNumber);
        
        Map<String, String> params = new HashMap<>();
        params.put("subject", "Patient/" + ihsNumber);
        
        return searchEncounters(organizationId, params, userId);
    }
    
    /**
     * Search encounters by status.
     */
    public Bundle searchEncountersByStatus(String organizationId, String status, UUID userId) {
        log.info("Searching encounters with status {} in SATUSEHAT", status);
        
        Map<String, String> params = new HashMap<>();
        params.put("status", status);
        
        return searchEncounters(organizationId, params, userId);
    }
    
    /**
     * Search encounters by date range.
     */
    public Bundle searchEncountersByDateRange(String organizationId, String startDate, String endDate, UUID userId) {
        log.info("Searching encounters between {} and {} in SATUSEHAT", startDate, endDate);
        
        Map<String, String> params = new HashMap<>();
        params.put("date", "ge" + startDate); // Greater than or equal
        params.put("date", "le" + endDate);   // Less than or equal
        
        return searchEncounters(organizationId, params, userId);
    }
    
    /**
     * Generic search method.
     */
    private Bundle searchEncounters(String organizationId, Map<String, String> searchParams, UUID userId) {
        // Build query string
        StringBuilder queryString = new StringBuilder("/Encounter?");
        searchParams.forEach((key, value) -> {
            if (queryString.length() > 11) {
                queryString.append("&");
            }
            queryString.append(key).append("=").append(value);
        });
        
        log.debug("Searching encounters with query: {}", queryString);
        
        var config = authService.getActiveConfig(organizationId);
        
        Bundle bundle = httpClient.get(
            queryString.toString(),
            config,
            Bundle.class,
            userId
        );
        
        log.info("Encounter search completed, found {} results", bundle.getTotal());
        return bundle;
    }
    
    /**
     * Batch create encounters (up to 100 per batch).
     */
    public BatchResponse batchCreateEncounters(String organizationId, List<Encounter> encounters, UUID userId) {
        log.info("Batch creating {} encounters in SATUSEHAT", encounters.size());
        
        if (encounters.size() > 100) {
            throw new SatusehatValidationException("Batch size cannot exceed 100 encounters");
        }
        
        var config = authService.getActiveConfig(organizationId);
        
        // Build FHIR Bundle for batch operation
        Map<String, Object> batchBundle = new HashMap<>();
        batchBundle.put("resourceType", "Bundle");
        batchBundle.put("type", "batch");
        
        List<Map<String, Object>> entries = new ArrayList<>();
        for (Encounter encounter : encounters) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("resource", encounter);
            
            Map<String, String> request = new HashMap<>();
            request.put("method", "POST");
            request.put("url", "Encounter");
            entry.put("request", request);
            
            entries.add(entry);
        }
        batchBundle.put("entry", entries);
        
        // Submit batch
        @SuppressWarnings("unchecked")
        Map<String, Object> response = httpClient.post(
            "/",
            batchBundle,
            config,
            Map.class,
            userId
        );
        
        // Process response
        BatchResponse batchResponse = processBatchResponse(response);
        
        log.info("Batch create completed: {} success, {} failed",
            batchResponse.getSuccessCount(), batchResponse.getFailureCount());
        
        return batchResponse;
    }
    
    /**
     * Process batch response and extract results.
     */
    @SuppressWarnings("unchecked")
    private BatchResponse processBatchResponse(Map<String, Object> response) {
        BatchResponse batchResponse = new BatchResponse();
        
        List<Map<String, Object>> entries = (List<Map<String, Object>>) response.get("entry");
        if (entries == null) {
            return batchResponse;
        }
        
        for (Map<String, Object> entry : entries) {
            Map<String, Object> responseData = (Map<String, Object>) entry.get("response");
            if (responseData != null) {
                String status = (String) responseData.get("status");
                if (status != null && status.startsWith("20")) {
                    batchResponse.incrementSuccess();
                    if (entry.get("resource") != null) {
                        batchResponse.addSuccessResource(entry.get("resource"));
                    }
                } else {
                    batchResponse.incrementFailure();
                    batchResponse.addFailure(status, (String) responseData.get("outcome"));
                }
            }
        }
        
        return batchResponse;
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
            private Encounter resource;
            
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
    
    /**
     * Batch response DTO.
     */
    @lombok.Data
    public static class BatchResponse {
        private int successCount = 0;
        private int failureCount = 0;
        private List<Object> successResources = new ArrayList<>();
        private List<FailureDetail> failures = new ArrayList<>();
        
        public void incrementSuccess() {
            successCount++;
        }
        
        public void incrementFailure() {
            failureCount++;
        }
        
        public void addSuccessResource(Object resource) {
            successResources.add(resource);
        }
        
        public void addFailure(String status, String outcome) {
            failures.add(new FailureDetail(status, outcome));
        }
        
        @lombok.Data
        @lombok.AllArgsConstructor
        public static class FailureDetail {
            private String status;
            private String outcome;
        }
    }
}
