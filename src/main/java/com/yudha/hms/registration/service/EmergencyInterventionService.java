package com.yudha.hms.registration.service;

import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.registration.dto.EmergencyInterventionRequest;
import com.yudha.hms.registration.dto.EmergencyInterventionResponse;
import com.yudha.hms.registration.entity.EmergencyIntervention;
import com.yudha.hms.registration.entity.EmergencyRegistration;
import com.yudha.hms.registration.entity.InterventionType;
import com.yudha.hms.registration.repository.EmergencyInterventionRepository;
import com.yudha.hms.registration.repository.EmergencyRegistrationRepository;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Emergency Intervention management.
 * Handles critical procedure and intervention tracking during emergency care.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmergencyInterventionService {

    private final EmergencyInterventionRepository interventionRepository;
    private final EmergencyRegistrationRepository emergencyRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Record a new emergency intervention.
     */
    @Transactional
    public EmergencyInterventionResponse recordIntervention(
        UUID emergencyRegistrationId,
        EmergencyInterventionRequest request
    ) {
        log.info("Recording intervention for emergency registration: {}", emergencyRegistrationId);

        // Validate emergency registration exists
        EmergencyRegistration emergency = emergencyRepository.findById(emergencyRegistrationId)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Registration", "ID", emergencyRegistrationId));

        // Validate encounter exists
        if (emergency.getEncounterId() == null) {
            throw new BusinessException("Emergency registration does not have associated encounter. Cannot record intervention.");
        }

        Encounter encounter = encounterRepository.findById(emergency.getEncounterId())
            .orElseThrow(() -> new ResourceNotFoundException("Encounter", "ID", emergency.getEncounterId()));

        // Build intervention
        EmergencyIntervention intervention = buildInterventionFromRequest(emergency, encounter, request);

        // Save intervention
        intervention = interventionRepository.save(intervention);

        log.info("Intervention recorded: {} - {}", intervention.getInterventionType(), intervention.getInterventionName());

        return mapToResponse(intervention);
    }

    /**
     * Get intervention by ID.
     */
    public EmergencyInterventionResponse getInterventionById(UUID id) {
        log.info("Retrieving intervention: {}", id);

        EmergencyIntervention intervention = interventionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Intervention", "ID", id));

        return mapToResponse(intervention);
    }

    /**
     * Get all interventions for an emergency registration.
     */
    public List<EmergencyInterventionResponse> getInterventionsByEmergency(UUID emergencyRegistrationId) {
        log.info("Retrieving interventions for emergency registration: {}", emergencyRegistrationId);

        List<EmergencyIntervention> interventions =
            interventionRepository.findByEmergencyRegistrationIdOrderByInterventionTimeDesc(emergencyRegistrationId);

        return interventions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all interventions for an encounter.
     */
    public List<EmergencyInterventionResponse> getInterventionsByEncounter(UUID encounterId) {
        log.info("Retrieving interventions for encounter: {}", encounterId);

        List<EmergencyIntervention> interventions =
            interventionRepository.findByEncounterIdOrderByInterventionTimeDesc(encounterId);

        return interventions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get interventions by type.
     */
    public List<EmergencyInterventionResponse> getInterventionsByType(
        UUID emergencyRegistrationId,
        InterventionType type
    ) {
        log.info("Retrieving {} interventions for emergency: {}", type, emergencyRegistrationId);

        List<EmergencyIntervention> interventions =
            interventionRepository.findByEmergencyRegistrationIdAndInterventionTypeOrderByInterventionTimeDesc(
                emergencyRegistrationId, type
            );

        return interventions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get resuscitation timeline for emergency registration.
     */
    public List<EmergencyInterventionResponse> getResuscitationTimeline(UUID emergencyRegistrationId) {
        log.info("Retrieving resuscitation timeline for emergency: {}", emergencyRegistrationId);

        List<EmergencyIntervention> resuscitations =
            interventionRepository.findByEmergencyRegistrationIdAndIsResuscitationTrueOrderByInterventionTimeDesc(
                emergencyRegistrationId
            );

        return resuscitations.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get critical interventions.
     */
    public List<EmergencyInterventionResponse> getCriticalInterventions(UUID emergencyRegistrationId) {
        log.info("Retrieving critical interventions for emergency: {}", emergencyRegistrationId);

        List<EmergencyIntervention> criticalInterventions =
            interventionRepository.findCriticalInterventions(emergencyRegistrationId);

        return criticalInterventions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get interventions with complications.
     */
    public List<EmergencyInterventionResponse> getInterventionsWithComplications(UUID emergencyRegistrationId) {
        log.info("Retrieving interventions with complications for emergency: {}", emergencyRegistrationId);

        List<EmergencyIntervention> interventions =
            interventionRepository.findInterventionsWithComplications(emergencyRegistrationId);

        return interventions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get ongoing resuscitations.
     */
    public List<EmergencyInterventionResponse> getOngoingResuscitations(UUID emergencyRegistrationId) {
        log.info("Retrieving ongoing resuscitations for emergency: {}", emergencyRegistrationId);

        List<EmergencyIntervention> ongoing =
            interventionRepository.findOngoingResuscitations(emergencyRegistrationId);

        return ongoing.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Update intervention.
     */
    @Transactional
    public EmergencyInterventionResponse updateIntervention(UUID id, EmergencyInterventionRequest request) {
        log.info("Updating intervention: {}", id);

        EmergencyIntervention intervention = interventionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Intervention", "ID", id));

        // Update fields
        updateInterventionFromRequest(intervention, request);

        intervention = interventionRepository.save(intervention);

        log.info("Intervention updated: {}", id);

        return mapToResponse(intervention);
    }

    /**
     * Mark intervention as completed.
     */
    @Transactional
    public EmergencyInterventionResponse completeIntervention(UUID id, String outcome, String outcomeNotes) {
        log.info("Completing intervention: {}", id);

        EmergencyIntervention intervention = interventionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Intervention", "ID", id));

        intervention.complete(outcome, outcomeNotes);
        intervention = interventionRepository.save(intervention);

        log.info("Intervention completed: {} with outcome: {}", id, outcome);

        return mapToResponse(intervention);
    }

    /**
     * Record ROSC achievement.
     */
    @Transactional
    public EmergencyInterventionResponse recordROSC(UUID id) {
        log.info("Recording ROSC for intervention: {}", id);

        EmergencyIntervention intervention = interventionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Intervention", "ID", id));

        intervention.recordROSC();
        intervention = interventionRepository.save(intervention);

        log.info("ROSC recorded for intervention: {}", id);

        return mapToResponse(intervention);
    }

    /**
     * Record complication.
     */
    @Transactional
    public EmergencyInterventionResponse recordComplication(UUID id, String complicationDetails) {
        log.info("Recording complication for intervention: {}", id);

        EmergencyIntervention intervention = interventionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Intervention", "ID", id));

        intervention.recordComplication(complicationDetails);
        intervention = interventionRepository.save(intervention);

        log.info("Complication recorded for intervention: {}", id);

        return mapToResponse(intervention);
    }

    /**
     * Delete intervention.
     */
    @Transactional
    public void deleteIntervention(UUID id) {
        log.info("Deleting intervention: {}", id);

        EmergencyIntervention intervention = interventionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Emergency Intervention", "ID", id));

        interventionRepository.delete(intervention);

        log.info("Intervention deleted: {}", id);
    }

    /**
     * Check if resuscitation was successful.
     */
    public boolean hasSuccessfulResuscitation(UUID emergencyRegistrationId) {
        return interventionRepository.hasSuccessfulResuscitation(emergencyRegistrationId);
    }

    /**
     * Get total resuscitation duration.
     */
    public Integer getTotalResuscitationDuration(UUID emergencyRegistrationId) {
        Integer duration = interventionRepository.getTotalResuscitationDuration(emergencyRegistrationId);
        return duration != null ? duration : 0;
    }

    /**
     * Count interventions by type.
     */
    public long countInterventionsByType(UUID emergencyRegistrationId, InterventionType type) {
        return interventionRepository.countByEmergencyRegistrationIdAndInterventionType(
            emergencyRegistrationId, type
        );
    }

    // ========== Helper Methods ==========

    /**
     * Build intervention from request.
     */
    private EmergencyIntervention buildInterventionFromRequest(
        EmergencyRegistration emergency,
        Encounter encounter,
        EmergencyInterventionRequest request
    ) {
        EmergencyIntervention.EmergencyInterventionBuilder builder = EmergencyIntervention.builder()
            .emergencyRegistration(emergency)
            .encounter(encounter)
            .interventionType(request.getInterventionType())
            .interventionName(request.getInterventionName())
            .interventionTime(request.getInterventionTime() != null ?
                request.getInterventionTime() : LocalDateTime.now())
            .performedById(request.getPerformedById())
            .performedByName(request.getPerformedByName())
            .performedByRole(request.getPerformedByRole())
            .indication(request.getIndication())
            .urgency(request.getUrgency() != null ? request.getUrgency() : "ROUTINE")
            .notes(request.getNotes())
            .location(request.getLocation() != null ? request.getLocation() : emergency.getErZone())
            .bedNumber(request.getBedNumber() != null ? request.getBedNumber() : emergency.getErBedNumber());

        // Resuscitation fields
        if (Boolean.TRUE.equals(request.getIsResuscitation())) {
            builder.isResuscitation(true)
                .resuscitationStartTime(request.getResuscitationStartTime())
                .resuscitationEndTime(request.getResuscitationEndTime())
                .roscAchieved(request.getRoscAchieved())
                .roscTime(request.getRoscTime())
                .cprQualityScore(request.getCprQualityScore())
                .defibrillationAttempts(request.getDefibrillationAttempts())
                .epinephrineDoses(request.getEpinephrineDoses());
        }

        // Airway management fields
        if (request.getInterventionType() == InterventionType.AIRWAY_MANAGEMENT) {
            builder.airwayType(request.getAirwayType())
                .tubeSize(request.getTubeSize())
                .insertionAttempts(request.getInsertionAttempts())
                .airwaySecured(request.getAirwaySecured());
        }

        // Procedure fields
        builder.procedureCode(request.getProcedureCode())
            .procedureSite(request.getProcedureSite())
            .procedureApproach(request.getProcedureApproach())
            .complications(request.getComplications())
            .procedureOutcome(request.getProcedureOutcome());

        // Medication fields
        if (request.getInterventionType() == InterventionType.EMERGENCY_MEDICATION) {
            builder.medicationName(request.getMedicationName())
                .medicationDose(request.getMedicationDose())
                .medicationRoute(request.getMedicationRoute())
                .medicationFrequency(request.getMedicationFrequency());
        }

        // Transfusion fields
        if (request.getInterventionType() == InterventionType.TRANSFUSION) {
            builder.bloodProductType(request.getBloodProductType())
                .unitsTransfused(request.getUnitsTransfused())
                .transfusionReaction(request.getTransfusionReaction())
                .crossMatchRequired(request.getCrossMatchRequired());
        }

        // Common fields
        builder.outcome(request.getOutcome())
            .outcomeNotes(request.getOutcomeNotes())
            .complicationsOccurred(request.getComplicationsOccurred() != null ?
                request.getComplicationsOccurred() : false);

        return builder.build();
    }

    /**
     * Update intervention from request.
     */
    private void updateInterventionFromRequest(
        EmergencyIntervention intervention,
        EmergencyInterventionRequest request
    ) {
        if (request.getInterventionType() != null) {
            intervention.setInterventionType(request.getInterventionType());
        }
        if (request.getInterventionName() != null) {
            intervention.setInterventionName(request.getInterventionName());
        }
        if (request.getInterventionTime() != null) {
            intervention.setInterventionTime(request.getInterventionTime());
        }
        if (request.getPerformedById() != null) {
            intervention.setPerformedById(request.getPerformedById());
        }
        if (request.getPerformedByName() != null) {
            intervention.setPerformedByName(request.getPerformedByName());
        }
        if (request.getPerformedByRole() != null) {
            intervention.setPerformedByRole(request.getPerformedByRole());
        }

        // Resuscitation fields
        if (request.getIsResuscitation() != null) {
            intervention.setIsResuscitation(request.getIsResuscitation());
        }
        if (request.getResuscitationStartTime() != null) {
            intervention.setResuscitationStartTime(request.getResuscitationStartTime());
        }
        if (request.getResuscitationEndTime() != null) {
            intervention.setResuscitationEndTime(request.getResuscitationEndTime());
            intervention.calculateResuscitationDuration();
        }
        if (request.getRoscAchieved() != null) {
            intervention.setRoscAchieved(request.getRoscAchieved());
        }
        if (request.getRoscTime() != null) {
            intervention.setRoscTime(request.getRoscTime());
        }
        if (request.getCprQualityScore() != null) {
            intervention.setCprQualityScore(request.getCprQualityScore());
        }
        if (request.getDefibrillationAttempts() != null) {
            intervention.setDefibrillationAttempts(request.getDefibrillationAttempts());
        }
        if (request.getEpinephrineDoses() != null) {
            intervention.setEpinephrineDoses(request.getEpinephrineDoses());
        }

        // Airway fields
        if (request.getAirwayType() != null) {
            intervention.setAirwayType(request.getAirwayType());
        }
        if (request.getTubeSize() != null) {
            intervention.setTubeSize(request.getTubeSize());
        }
        if (request.getInsertionAttempts() != null) {
            intervention.setInsertionAttempts(request.getInsertionAttempts());
        }
        if (request.getAirwaySecured() != null) {
            intervention.setAirwaySecured(request.getAirwaySecured());
        }

        // Procedure fields
        if (request.getProcedureCode() != null) {
            intervention.setProcedureCode(request.getProcedureCode());
        }
        if (request.getProcedureSite() != null) {
            intervention.setProcedureSite(request.getProcedureSite());
        }
        if (request.getProcedureApproach() != null) {
            intervention.setProcedureApproach(request.getProcedureApproach());
        }
        if (request.getComplications() != null) {
            intervention.setComplications(request.getComplications());
        }
        if (request.getProcedureOutcome() != null) {
            intervention.setProcedureOutcome(request.getProcedureOutcome());
        }

        // Medication fields
        if (request.getMedicationName() != null) {
            intervention.setMedicationName(request.getMedicationName());
        }
        if (request.getMedicationDose() != null) {
            intervention.setMedicationDose(request.getMedicationDose());
        }
        if (request.getMedicationRoute() != null) {
            intervention.setMedicationRoute(request.getMedicationRoute());
        }
        if (request.getMedicationFrequency() != null) {
            intervention.setMedicationFrequency(request.getMedicationFrequency());
        }

        // Transfusion fields
        if (request.getBloodProductType() != null) {
            intervention.setBloodProductType(request.getBloodProductType());
        }
        if (request.getUnitsTransfused() != null) {
            intervention.setUnitsTransfused(request.getUnitsTransfused());
        }
        if (request.getTransfusionReaction() != null) {
            intervention.setTransfusionReaction(request.getTransfusionReaction());
        }
        if (request.getCrossMatchRequired() != null) {
            intervention.setCrossMatchRequired(request.getCrossMatchRequired());
        }

        // Common fields
        if (request.getIndication() != null) {
            intervention.setIndication(request.getIndication());
        }
        if (request.getUrgency() != null) {
            intervention.setUrgency(request.getUrgency());
        }
        if (request.getOutcome() != null) {
            intervention.setOutcome(request.getOutcome());
        }
        if (request.getOutcomeNotes() != null) {
            intervention.setOutcomeNotes(request.getOutcomeNotes());
        }
        if (request.getComplicationsOccurred() != null) {
            intervention.setComplicationsOccurred(request.getComplicationsOccurred());
        }
        if (request.getNotes() != null) {
            intervention.setNotes(request.getNotes());
        }
        if (request.getLocation() != null) {
            intervention.setLocation(request.getLocation());
        }
        if (request.getBedNumber() != null) {
            intervention.setBedNumber(request.getBedNumber());
        }
    }

    /**
     * Map entity to response DTO.
     */
    private EmergencyInterventionResponse mapToResponse(EmergencyIntervention intervention) {
        return EmergencyInterventionResponse.builder()
            .id(intervention.getId())
            .emergencyRegistrationId(intervention.getEmergencyRegistration().getId())
            .encounterId(intervention.getEncounter().getId())
            .interventionType(intervention.getInterventionType())
            .interventionTypeDisplay(intervention.getInterventionType() != null ?
                intervention.getInterventionType().getDisplayName() : null)
            .interventionTypeindonesian(intervention.getInterventionType() != null ?
                intervention.getInterventionType().getIndonesianName() : null)
            .interventionName(intervention.getInterventionName())
            .interventionTime(intervention.getInterventionTime())
            .performedById(intervention.getPerformedById())
            .performedByName(intervention.getPerformedByName())
            .performedByRole(intervention.getPerformedByRole())
            .isResuscitation(intervention.getIsResuscitation())
            .resuscitationStartTime(intervention.getResuscitationStartTime())
            .resuscitationEndTime(intervention.getResuscitationEndTime())
            .resuscitationDurationMinutes(intervention.getResuscitationDurationMinutes())
            .roscAchieved(intervention.getRoscAchieved())
            .roscTime(intervention.getRoscTime())
            .cprQualityScore(intervention.getCprQualityScore())
            .defibrillationAttempts(intervention.getDefibrillationAttempts())
            .epinephrineDoses(intervention.getEpinephrineDoses())
            .airwayType(intervention.getAirwayType())
            .tubeSize(intervention.getTubeSize())
            .insertionAttempts(intervention.getInsertionAttempts())
            .airwaySecured(intervention.getAirwaySecured())
            .procedureCode(intervention.getProcedureCode())
            .procedureSite(intervention.getProcedureSite())
            .procedureApproach(intervention.getProcedureApproach())
            .complications(intervention.getComplications())
            .procedureOutcome(intervention.getProcedureOutcome())
            .medicationName(intervention.getMedicationName())
            .medicationDose(intervention.getMedicationDose())
            .medicationRoute(intervention.getMedicationRoute())
            .medicationFrequency(intervention.getMedicationFrequency())
            .bloodProductType(intervention.getBloodProductType())
            .unitsTransfused(intervention.getUnitsTransfused())
            .transfusionReaction(intervention.getTransfusionReaction())
            .crossMatchRequired(intervention.getCrossMatchRequired())
            .indication(intervention.getIndication())
            .urgency(intervention.getUrgency())
            .outcome(intervention.getOutcome())
            .outcomeNotes(intervention.getOutcomeNotes())
            .complicationsOccurred(intervention.getComplicationsOccurred())
            .notes(intervention.getNotes())
            .location(intervention.getLocation())
            .bedNumber(intervention.getBedNumber())
            .createdAt(intervention.getCreatedAt())
            .updatedAt(intervention.getUpdatedAt())
            .createdBy(intervention.getCreatedBy())
            .updatedBy(intervention.getUpdatedBy())
            .isCritical(intervention.isCriticalIntervention())
            .requiresSupervision(intervention.requiresSupervision())
            .isCompleted(intervention.getOutcome() != null)
            .displayName(intervention.getDisplayName())
            .build();
    }
}
