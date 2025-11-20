package com.yudha.hms.registration.controller;

import com.yudha.hms.registration.dto.EmergencyInterventionRequest;
import com.yudha.hms.registration.dto.EmergencyInterventionResponse;
import com.yudha.hms.registration.entity.InterventionType;
import com.yudha.hms.registration.service.EmergencyInterventionService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Emergency Intervention management.
 *
 * Endpoints for tracking critical interventions during emergency care including:
 * - Resuscitation events
 * - Airway management
 * - Emergency procedures
 * - Medications and transfusions
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/registration/emergency")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EmergencyInterventionController {

    private final EmergencyInterventionService interventionService;

    /**
     * Record new emergency intervention.
     *
     * POST /api/registration/emergency/{emergencyId}/interventions
     */
    @PostMapping("/{emergencyId}/interventions")
    public ResponseEntity<ApiResponse<EmergencyInterventionResponse>> recordIntervention(
        @PathVariable UUID emergencyId,
        @Valid @RequestBody EmergencyInterventionRequest request
    ) {
        log.info("REST: Recording intervention for emergency: {}", emergencyId);

        EmergencyInterventionResponse response = interventionService.recordIntervention(emergencyId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Intervention recorded successfully",
                response
            ));
    }

    /**
     * Get intervention by ID.
     *
     * GET /api/registration/emergency/interventions/{id}
     */
    @GetMapping("/interventions/{id}")
    public ResponseEntity<ApiResponse<EmergencyInterventionResponse>> getInterventionById(
        @PathVariable UUID id
    ) {
        log.info("REST: Fetching intervention: {}", id);

        EmergencyInterventionResponse response = interventionService.getInterventionById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all interventions for emergency registration.
     *
     * GET /api/registration/emergency/{emergencyId}/interventions
     */
    @GetMapping("/{emergencyId}/interventions")
    public ResponseEntity<ApiResponse<List<EmergencyInterventionResponse>>> getInterventionsByEmergency(
        @PathVariable UUID emergencyId
    ) {
        log.info("REST: Fetching interventions for emergency: {}", emergencyId);

        List<EmergencyInterventionResponse> interventions =
            interventionService.getInterventionsByEmergency(emergencyId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d interventions", interventions.size()),
            interventions
        ));
    }

    /**
     * Get interventions by type.
     *
     * GET /api/registration/emergency/{emergencyId}/interventions/type/{type}
     */
    @GetMapping("/{emergencyId}/interventions/type/{type}")
    public ResponseEntity<ApiResponse<List<EmergencyInterventionResponse>>> getInterventionsByType(
        @PathVariable UUID emergencyId,
        @PathVariable InterventionType type
    ) {
        log.info("REST: Fetching {} interventions for emergency: {}", type, emergencyId);

        List<EmergencyInterventionResponse> interventions =
            interventionService.getInterventionsByType(emergencyId, type);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d %s interventions", interventions.size(), type),
            interventions
        ));
    }

    /**
     * Get resuscitation timeline.
     *
     * GET /api/registration/emergency/{emergencyId}/resuscitation-timeline
     */
    @GetMapping("/{emergencyId}/resuscitation-timeline")
    public ResponseEntity<ApiResponse<List<EmergencyInterventionResponse>>> getResuscitationTimeline(
        @PathVariable UUID emergencyId
    ) {
        log.info("REST: Fetching resuscitation timeline for emergency: {}", emergencyId);

        List<EmergencyInterventionResponse> timeline =
            interventionService.getResuscitationTimeline(emergencyId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Resuscitation timeline: %d events", timeline.size()),
            timeline
        ));
    }

    /**
     * Get critical interventions.
     *
     * GET /api/registration/emergency/{emergencyId}/critical-interventions
     */
    @GetMapping("/{emergencyId}/critical-interventions")
    public ResponseEntity<ApiResponse<List<EmergencyInterventionResponse>>> getCriticalInterventions(
        @PathVariable UUID emergencyId
    ) {
        log.info("REST: Fetching critical interventions for emergency: {}", emergencyId);

        List<EmergencyInterventionResponse> criticalInterventions =
            interventionService.getCriticalInterventions(emergencyId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d critical interventions", criticalInterventions.size()),
            criticalInterventions
        ));
    }

    /**
     * Get interventions with complications.
     *
     * GET /api/registration/emergency/{emergencyId}/interventions-with-complications
     */
    @GetMapping("/{emergencyId}/interventions-with-complications")
    public ResponseEntity<ApiResponse<List<EmergencyInterventionResponse>>> getInterventionsWithComplications(
        @PathVariable UUID emergencyId
    ) {
        log.info("REST: Fetching interventions with complications for emergency: {}", emergencyId);

        List<EmergencyInterventionResponse> interventions =
            interventionService.getInterventionsWithComplications(emergencyId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d interventions with complications", interventions.size()),
            interventions
        ));
    }

    /**
     * Get ongoing resuscitations.
     *
     * GET /api/registration/emergency/{emergencyId}/ongoing-resuscitations
     */
    @GetMapping("/{emergencyId}/ongoing-resuscitations")
    public ResponseEntity<ApiResponse<List<EmergencyInterventionResponse>>> getOngoingResuscitations(
        @PathVariable UUID emergencyId
    ) {
        log.info("REST: Fetching ongoing resuscitations for emergency: {}", emergencyId);

        List<EmergencyInterventionResponse> ongoing =
            interventionService.getOngoingResuscitations(emergencyId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d ongoing resuscitations", ongoing.size()),
            ongoing
        ));
    }

    /**
     * Update intervention.
     *
     * PUT /api/registration/emergency/interventions/{id}
     */
    @PutMapping("/interventions/{id}")
    public ResponseEntity<ApiResponse<EmergencyInterventionResponse>> updateIntervention(
        @PathVariable UUID id,
        @Valid @RequestBody EmergencyInterventionRequest request
    ) {
        log.info("REST: Updating intervention: {}", id);

        EmergencyInterventionResponse response = interventionService.updateIntervention(id, request);

        return ResponseEntity.ok(ApiResponse.success(
            "Intervention updated successfully",
            response
        ));
    }

    /**
     * Complete intervention.
     *
     * POST /api/registration/emergency/interventions/{id}/complete
     */
    @PostMapping("/interventions/{id}/complete")
    public ResponseEntity<ApiResponse<EmergencyInterventionResponse>> completeIntervention(
        @PathVariable UUID id,
        @RequestParam String outcome,
        @RequestParam(required = false) String outcomeNotes
    ) {
        log.info("REST: Completing intervention: {} with outcome: {}", id, outcome);

        EmergencyInterventionResponse response =
            interventionService.completeIntervention(id, outcome, outcomeNotes);

        return ResponseEntity.ok(ApiResponse.success(
            "Intervention marked as completed",
            response
        ));
    }

    /**
     * Record ROSC achievement.
     *
     * POST /api/registration/emergency/interventions/{id}/record-rosc
     */
    @PostMapping("/interventions/{id}/record-rosc")
    public ResponseEntity<ApiResponse<EmergencyInterventionResponse>> recordROSC(
        @PathVariable UUID id
    ) {
        log.info("REST: Recording ROSC for intervention: {}", id);

        EmergencyInterventionResponse response = interventionService.recordROSC(id);

        return ResponseEntity.ok(ApiResponse.success(
            "ROSC recorded successfully",
            response
        ));
    }

    /**
     * Record complication.
     *
     * POST /api/registration/emergency/interventions/{id}/record-complication
     */
    @PostMapping("/interventions/{id}/record-complication")
    public ResponseEntity<ApiResponse<EmergencyInterventionResponse>> recordComplication(
        @PathVariable UUID id,
        @RequestParam String complicationDetails
    ) {
        log.info("REST: Recording complication for intervention: {}", id);

        EmergencyInterventionResponse response =
            interventionService.recordComplication(id, complicationDetails);

        return ResponseEntity.ok(ApiResponse.success(
            "Complication recorded successfully",
            response
        ));
    }

    /**
     * Delete intervention.
     *
     * DELETE /api/registration/emergency/interventions/{id}
     */
    @DeleteMapping("/interventions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIntervention(
        @PathVariable UUID id
    ) {
        log.info("REST: Deleting intervention: {}", id);

        interventionService.deleteIntervention(id);

        return ResponseEntity.ok(ApiResponse.success("Intervention deleted successfully"));
    }

    /**
     * Get intervention statistics for emergency registration.
     *
     * GET /api/registration/emergency/{emergencyId}/intervention-statistics
     */
    @GetMapping("/{emergencyId}/intervention-statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInterventionStatistics(
        @PathVariable UUID emergencyId
    ) {
        log.info("REST: Fetching intervention statistics for emergency: {}", emergencyId);

        List<EmergencyInterventionResponse> allInterventions =
            interventionService.getInterventionsByEmergency(emergencyId);

        boolean hasSuccessfulResuscitation = interventionService.hasSuccessfulResuscitation(emergencyId);
        Integer totalResuscitationDuration = interventionService.getTotalResuscitationDuration(emergencyId);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalInterventions", allInterventions.size());
        statistics.put("resuscitationCount", allInterventions.stream()
            .filter(i -> Boolean.TRUE.equals(i.getIsResuscitation())).count());
        statistics.put("hasSuccessfulResuscitation", hasSuccessfulResuscitation);
        statistics.put("totalResuscitationDurationMinutes", totalResuscitationDuration);
        statistics.put("criticalInterventionsCount", allInterventions.stream()
            .filter(EmergencyInterventionResponse::getIsCritical).count());
        statistics.put("interventionsWithComplications", allInterventions.stream()
            .filter(i -> Boolean.TRUE.equals(i.getComplicationsOccurred())).count());
        statistics.put("completedInterventions", allInterventions.stream()
            .filter(EmergencyInterventionResponse::getIsCompleted).count());
        statistics.put("pendingInterventions", allInterventions.stream()
            .filter(i -> !i.getIsCompleted()).count());

        // Count by intervention type
        Map<InterventionType, Long> byType = new HashMap<>();
        for (InterventionType type : InterventionType.values()) {
            long count = interventionService.countInterventionsByType(emergencyId, type);
            if (count > 0) {
                byType.put(type, count);
            }
        }
        statistics.put("interventionsByType", byType);

        return ResponseEntity.ok(ApiResponse.success(
            "Intervention statistics retrieved successfully",
            statistics
        ));
    }

    /**
     * Get all intervention types.
     *
     * GET /api/registration/emergency/intervention-types
     */
    @GetMapping("/intervention-types")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInterventionTypes() {
        log.info("REST: Fetching all intervention types");

        List<Map<String, Object>> types = java.util.Arrays.stream(InterventionType.values())
            .map(type -> {
                Map<String, Object> typeInfo = new HashMap<>();
                typeInfo.put("value", type.name());
                typeInfo.put("displayName", type.getDisplayName());
                typeInfo.put("indonesianName", type.getIndonesianName());
                typeInfo.put("description", type.getDescription());
                typeInfo.put("requiresSupervision", type.requiresSupervision());
                typeInfo.put("isLifeSaving", type.isLifeSaving());
                typeInfo.put("isInvasiveProcedure", type.isInvasiveProcedure());
                typeInfo.put("isCardiacIntervention", type.isCardiacIntervention());
                return typeInfo;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Intervention types retrieved successfully",
            types
        ));
    }
}
