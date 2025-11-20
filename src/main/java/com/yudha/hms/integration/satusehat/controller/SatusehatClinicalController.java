package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API controller for SATUSEHAT clinical resource operations.
 * 
 * Provides endpoints for:
 * - Condition (Diagnosis) management
 * - Observation (Vital Signs, Lab Results) management
 * - Transaction bundle submissions
 * 
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/clinical")
@RequiredArgsConstructor
public class SatusehatClinicalController {
    
    private final ClinicalResourceService clinicalResourceService;
    
    // ========================================================================
    // CONDITION (DIAGNOSIS) ENDPOINTS
    // ========================================================================
    
    /**
     * Create a condition (diagnosis) in SATUSEHAT.
     * 
     * POST /api/v1/satusehat/clinical/condition
     */
    @PostMapping("/condition")
    public ResponseEntity<Condition> createCondition(
        @RequestBody Condition condition,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create condition request for organization: {}", organizationId);
        
        try {
            Condition createdCondition = clinicalResourceService.createCondition(
                organizationId,
                condition,
                userId
            );
            
            return ResponseEntity.ok(createdCondition);
            
        } catch (Exception e) {
            log.error("Failed to create condition: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Update a condition in SATUSEHAT.
     * 
     * PUT /api/v1/satusehat/clinical/condition/{conditionId}
     */
    @PutMapping("/condition/{conditionId}")
    public ResponseEntity<Condition> updateCondition(
        @PathVariable String conditionId,
        @RequestBody Condition condition,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update condition request for ID: {}", conditionId);
        
        try {
            Condition updatedCondition = clinicalResourceService.updateCondition(
                organizationId,
                conditionId,
                condition,
                userId
            );
            
            return ResponseEntity.ok(updatedCondition);
            
        } catch (Exception e) {
            log.error("Failed to update condition: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get condition by ID.
     * 
     * GET /api/v1/satusehat/clinical/condition/{conditionId}
     */
    @GetMapping("/condition/{conditionId}")
    public ResponseEntity<Condition> getCondition(
        @PathVariable String conditionId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get condition request for ID: {}", conditionId);
        
        try {
            Condition condition = clinicalResourceService.getConditionById(
                organizationId,
                conditionId,
                userId
            );
            
            return ResponseEntity.ok(condition);
            
        } catch (Exception e) {
            log.error("Failed to get condition: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search conditions by patient.
     * 
     * GET /api/v1/satusehat/clinical/condition/patient/{ihsNumber}
     */
    @GetMapping("/condition/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Condition>> searchConditionsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search conditions for patient: {}", ihsNumber);
        
        try {
            ClinicalResourceService.SearchBundle<Condition> bundle = 
                clinicalResourceService.searchConditionsByPatient(organizationId, ihsNumber, userId);
            
            return ResponseEntity.ok(bundle);
            
        } catch (Exception e) {
            log.error("Failed to search conditions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search conditions by encounter.
     * 
     * GET /api/v1/satusehat/clinical/condition/encounter/{encounterId}
     */
    @GetMapping("/condition/encounter/{encounterId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Condition>> searchConditionsByEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search conditions for encounter: {}", encounterId);
        
        try {
            ClinicalResourceService.SearchBundle<Condition> bundle = 
                clinicalResourceService.searchConditionsByEncounter(organizationId, encounterId, userId);
            
            return ResponseEntity.ok(bundle);
            
        } catch (Exception e) {
            log.error("Failed to search conditions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ========================================================================
    // OBSERVATION ENDPOINTS
    // ========================================================================
    
    /**
     * Create an observation (vital signs, lab result) in SATUSEHAT.
     * 
     * POST /api/v1/satusehat/clinical/observation
     */
    @PostMapping("/observation")
    public ResponseEntity<Observation> createObservation(
        @RequestBody Observation observation,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create observation request for organization: {}", organizationId);
        
        try {
            Observation createdObservation = clinicalResourceService.createObservation(
                organizationId,
                observation,
                userId
            );
            
            return ResponseEntity.ok(createdObservation);
            
        } catch (Exception e) {
            log.error("Failed to create observation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search observations by patient.
     * 
     * GET /api/v1/satusehat/clinical/observation/patient/{ihsNumber}
     */
    @GetMapping("/observation/patient/{ihsNumber}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Observation>> searchObservationsByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search observations for patient: {}", ihsNumber);
        
        try {
            ClinicalResourceService.SearchBundle<Observation> bundle = 
                clinicalResourceService.searchObservationsByPatient(organizationId, ihsNumber, userId);
            
            return ResponseEntity.ok(bundle);
            
        } catch (Exception e) {
            log.error("Failed to search observations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search observations by category (vital-signs, laboratory, etc.).
     * 
     * GET /api/v1/satusehat/clinical/observation/category/{category}
     */
    @GetMapping("/observation/category/{category}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Observation>> searchObservationsByCategory(
        @PathVariable String category,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search observations with category: {}", category);
        
        try {
            ClinicalResourceService.SearchBundle<Observation> bundle = 
                clinicalResourceService.searchObservationsByCategory(organizationId, category, userId);
            
            return ResponseEntity.ok(bundle);
            
        } catch (Exception e) {
            log.error("Failed to search observations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ========================================================================
    // TRANSACTION BUNDLE ENDPOINT
    // ========================================================================
    
    /**
     * Submit a transaction bundle to SATUSEHAT.
     * 
     * This allows submitting multiple resources (Encounter, Condition, Observation, etc.)
     * in a single atomic transaction.
     * 
     * POST /api/v1/satusehat/clinical/bundle
     */
    @PostMapping("/bundle")
    public ResponseEntity<TransactionBundle> submitTransactionBundle(
        @RequestBody TransactionBundle bundle,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Submit transaction bundle request with {} entries",
            bundle.getEntry() != null ? bundle.getEntry().size() : 0);
        
        try {
            TransactionBundle responseBundle = clinicalResourceService.submitTransactionBundle(
                organizationId,
                bundle,
                userId
            );
            
            return ResponseEntity.ok(responseBundle);
            
        } catch (Exception e) {
            log.error("Failed to submit transaction bundle: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
