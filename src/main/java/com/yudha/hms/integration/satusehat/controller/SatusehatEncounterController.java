package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.Encounter;
import com.yudha.hms.integration.satusehat.service.EncounterResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API controller for SATUSEHAT Encounter resource operations.
 * 
 * Provides endpoints for:
 * - Encounter creation and updates
 * - Encounter search and retrieval
 * - Batch encounter submission
 * - Status management
 * 
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/encounter")
@RequiredArgsConstructor
public class SatusehatEncounterController {
    
    private final EncounterResourceService encounterResourceService;
    
    /**
     * Create a new encounter in SATUSEHAT.
     * 
     * POST /api/v1/satusehat/encounter
     */
    @PostMapping
    public ResponseEntity<Encounter> createEncounter(
        @RequestBody Encounter encounter,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create encounter request for organization: {}", organizationId);
        
        try {
            Encounter createdEncounter = encounterResourceService.createEncounter(
                organizationId,
                encounter,
                userId
            );
            
            return ResponseEntity.ok(createdEncounter);
            
        } catch (Exception e) {
            log.error("Failed to create encounter: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Update an existing encounter in SATUSEHAT.
     * 
     * PUT /api/v1/satusehat/encounter/{encounterId}
     */
    @PutMapping("/{encounterId}")
    public ResponseEntity<Encounter> updateEncounter(
        @PathVariable String encounterId,
        @RequestBody Encounter encounter,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update encounter request for ID: {}", encounterId);
        
        try {
            Encounter updatedEncounter = encounterResourceService.updateEncounter(
                organizationId,
                encounterId,
                encounter,
                userId
            );
            
            return ResponseEntity.ok(updatedEncounter);
            
        } catch (Exception e) {
            log.error("Failed to update encounter: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get encounter by ID.
     * 
     * GET /api/v1/satusehat/encounter/{encounterId}
     */
    @GetMapping("/{encounterId}")
    public ResponseEntity<Encounter> getEncounter(
        @PathVariable String encounterId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get encounter request for ID: {}", encounterId);
        
        try {
            Encounter encounter = encounterResourceService.getEncounterById(
                organizationId,
                encounterId,
                userId
            );
            
            return ResponseEntity.ok(encounter);
            
        } catch (Exception e) {
            log.error("Failed to get encounter: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search encounters by patient IHS number.
     * 
     * GET /api/v1/satusehat/encounter/patient/{ihsNumber}
     */
    @GetMapping("/patient/{ihsNumber}")
    public ResponseEntity<EncounterResourceService.Bundle> searchByPatient(
        @PathVariable String ihsNumber,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search encounters for patient: {}", ihsNumber);
        
        try {
            EncounterResourceService.Bundle bundle = encounterResourceService.searchEncountersByPatient(
                organizationId,
                ihsNumber,
                userId
            );
            
            return ResponseEntity.ok(bundle);
            
        } catch (Exception e) {
            log.error("Failed to search encounters: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search encounters by status.
     * 
     * GET /api/v1/satusehat/encounter/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<EncounterResourceService.Bundle> searchByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search encounters with status: {}", status);
        
        try {
            EncounterResourceService.Bundle bundle = encounterResourceService.searchEncountersByStatus(
                organizationId,
                status,
                userId
            );
            
            return ResponseEntity.ok(bundle);
            
        } catch (Exception e) {
            log.error("Failed to search encounters: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Batch create encounters (up to 100 per batch).
     * 
     * POST /api/v1/satusehat/encounter/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<EncounterResourceService.BatchResponse> batchCreateEncounters(
        @RequestBody List<Encounter> encounters,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Batch create {} encounters for organization: {}", encounters.size(), organizationId);
        
        try {
            EncounterResourceService.BatchResponse response = encounterResourceService.batchCreateEncounters(
                organizationId,
                encounters,
                userId
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to batch create encounters: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Update encounter status.
     * 
     * PATCH /api/v1/satusehat/encounter/{encounterId}/status
     */
    @PatchMapping("/{encounterId}/status")
    public ResponseEntity<Map<String, String>> updateEncounterStatus(
        @PathVariable String encounterId,
        @RequestParam String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update encounter {} status to: {}", encounterId, status);
        
        try {
            // Get current encounter
            Encounter encounter = encounterResourceService.getEncounterById(
                organizationId,
                encounterId,
                userId
            );
            
            // Update status
            encounter.setStatus(status);
            
            // Submit update
            encounterResourceService.updateEncounter(
                organizationId,
                encounterId,
                encounter,
                userId
            );
            
            return ResponseEntity.ok(Map.of(
                "success", "true",
                "message", "Encounter status updated successfully",
                "encounterId", encounterId,
                "status", status
            ));
            
        } catch (Exception e) {
            log.error("Failed to update encounter status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", "false",
                "message", "Status update failed: " + e.getMessage()
            ));
        }
    }
}
