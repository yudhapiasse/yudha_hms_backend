package com.yudha.hms.registration.service;

import com.yudha.hms.registration.dto.TriageAssessmentRequest;
import com.yudha.hms.registration.entity.EmergencyRegistration;
import com.yudha.hms.registration.entity.TriageAssessment;
import com.yudha.hms.registration.entity.TriageLevel;
import com.yudha.hms.registration.repository.EmergencyRegistrationRepository;
import com.yudha.hms.registration.repository.TriageAssessmentRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Triage Assessment with ESI-based priority calculation.
 *
 * Implements Emergency Severity Index (ESI) algorithm:
 * - Level 1: Requires immediate life-saving intervention
 * - Level 2: High risk, confused/lethargic, severe pain/distress
 * - Level 3: Stable, needs multiple resources (2+)
 * - Level 4: Needs one resource
 * - Level 5: No resources needed
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TriageService {

    private final TriageAssessmentRepository triageRepository;
    private final EmergencyRegistrationRepository emergencyRepository;

    /**
     * Perform triage assessment for emergency patient.
     *
     * @param request triage assessment request
     * @return created triage assessment
     */
    @Transactional
    public TriageAssessment performTriage(TriageAssessmentRequest request) {
        log.info("Performing triage for emergency registration: {}", request.getEmergencyRegistrationId());

        // Get emergency registration
        EmergencyRegistration emergency = emergencyRepository
            .findById(request.getEmergencyRegistrationId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Emergency Registration",
                "ID",
                request.getEmergencyRegistrationId()
            ));

        // Build triage assessment
        TriageAssessment assessment = buildTriageAssessment(request, emergency);

        // Calculate GCS total
        assessment.calculateGcsTotal();

        // Auto-determine ESI level if needed
        if (assessment.getEsiLevel() == null || shouldRecalculateESI(assessment)) {
            Integer calculatedESI = assessment.determineESILevel();
            assessment.setEsiLevel(calculatedESI);
            log.info("Auto-calculated ESI level: {}", calculatedESI);
        }

        // Determine triage level from ESI
        TriageLevel triageLevel = mapESIToTriageLevel(assessment.getEsiLevel());
        Integer priority = assessment.getEsiLevel(); // ESI maps directly to priority

        // Determine recommended zone
        if (assessment.getRecommendedZone() == null) {
            assessment.setRecommendedZone(determineErZone(triageLevel, assessment));
        }

        // Save assessment
        TriageAssessment saved = triageRepository.save(assessment);

        // Update emergency registration with triage info
        emergency.performTriage(triageLevel, priority, request.getTriagedById(), request.getTriagedByName());
        emergency.setErZone(assessment.getRecommendedZone());

        // Copy vital signs to emergency registration
        copyVitalSignsToEmergency(assessment, emergency);

        // Set critical flags
        if (assessment.hasCriticalRedFlags() || triageLevel.isCritical()) {
            emergency.setIsCritical(true);
        }

        if (Boolean.TRUE.equals(assessment.getRequiresIsolation())) {
            emergency.setRequiresIsolation(true);
            emergency.setIsolationReason(assessment.getSuspectedInfection());
        }

        emergencyRepository.save(emergency);

        // Calculate door-to-triage time
        emergency.calculateDoorToTriageTime();

        log.info("Triage completed: ESI Level {}, Triage Level {}, Zone {}",
            assessment.getEsiLevel(), triageLevel, assessment.getRecommendedZone());

        return saved;
    }

    /**
     * Perform re-triage for deteriorating or improving patient.
     *
     * @param emergencyId emergency registration ID
     * @param request triage assessment request
     * @param retriageReason reason for re-triage
     * @return new triage assessment
     */
    @Transactional
    public TriageAssessment performRetriage(UUID emergencyId, TriageAssessmentRequest request, String retriageReason) {
        log.info("Performing re-triage for emergency: {}", emergencyId);

        // Get previous triage
        TriageAssessment previousTriage = triageRepository
            .findLatestByEmergencyRegistrationId(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Previous Triage",
                "Emergency ID",
                emergencyId
            ));

        // Set re-triage flags
        request.setIsRetriage(true);
        request.setPreviousTriageId(previousTriage.getId());
        request.setRetriageReason(retriageReason);

        return performTriage(request);
    }

    /**
     * Get all triage assessments for an emergency registration.
     *
     * @param emergencyId emergency registration ID
     * @return list of triage assessments
     */
    public List<TriageAssessment> getTriageHistory(UUID emergencyId) {
        return triageRepository.findByEmergencyRegistrationId(emergencyId);
    }

    /**
     * Get latest triage assessment.
     *
     * @param emergencyId emergency registration ID
     * @return latest triage assessment
     */
    public TriageAssessment getLatestTriage(UUID emergencyId) {
        return triageRepository.findLatestByEmergencyRegistrationId(emergencyId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Triage Assessment",
                "Emergency ID",
                emergencyId
            ));
    }

    // ========== Private Helper Methods ==========

    /**
     * Build triage assessment from request.
     */
    private TriageAssessment buildTriageAssessment(TriageAssessmentRequest request, EmergencyRegistration emergency) {
        return TriageAssessment.builder()
            .emergencyRegistration(emergency)
            .triageTime(LocalDateTime.now())
            .triagedById(request.getTriagedById())
            .triagedByName(request.getTriagedByName())
            .triageMethod(request.getTriageMethod())
            .esiLevel(request.getEsiLevel())
            // Vital signs
            .bloodPressureSystolic(request.getBloodPressureSystolic())
            .bloodPressureDiastolic(request.getBloodPressureDiastolic())
            .heartRate(request.getHeartRate())
            .respiratoryRate(request.getRespiratoryRate())
            .temperature(request.getTemperature())
            .oxygenSaturation(request.getOxygenSaturation())
            .bloodGlucose(request.getBloodGlucose())
            // GCS
            .gcsEyeOpening(request.getGcsEyeOpening())
            .gcsVerbalResponse(request.getGcsVerbalResponse())
            .gcsMotorResponse(request.getGcsMotorResponse())
            .pupilResponse(request.getPupilResponse())
            .consciousnessLevel(request.getConsciousnessLevel())
            // Pain
            .painScore(request.getPainScore())
            .painLocation(request.getPainLocation())
            .painCharacteristics(request.getPainCharacteristics())
            .painOnset(request.getPainOnset())
            // Respiratory
            .respiratoryDistress(request.getRespiratoryDistress())
            .airwayStatus(request.getAirwayStatus())
            .breathingPattern(request.getBreathingPattern())
            .oxygenTherapy(request.getOxygenTherapy())
            .oxygenDeliveryMethod(request.getOxygenDeliveryMethod())
            .oxygenFlowRate(request.getOxygenFlowRate())
            // Cardiovascular
            .peripheralPulses(request.getPeripheralPulses())
            .capillaryRefillSeconds(request.getCapillaryRefillSeconds())
            .skinColor(request.getSkinColor())
            .skinTemperature(request.getSkinTemperature())
            // History
            .chiefComplaint(request.getChiefComplaint())
            .historyPresentIllness(request.getHistoryPresentIllness())
            .symptomOnset(request.getSymptomOnset())
            .relevantMedicalHistory(request.getRelevantMedicalHistory())
            .currentMedications(request.getCurrentMedications())
            .allergies(request.getAllergies())
            // Red flags
            .hasChestPain(request.getHasChestPain())
            .hasDifficultyBreathing(request.getHasDifficultyBreathing())
            .hasAlteredConsciousness(request.getHasAlteredConsciousness())
            .hasSevereBleeding(request.getHasSevereBleeding())
            .hasSeverePain(request.getHasSeverePain())
            .hasSeizures(request.getHasSeizures())
            .hasPoisoning(request.getHasPoisoning())
            // Resources
            .expectedResourcesCount(request.getExpectedResourcesCount())
            .needsLabWork(request.getNeedsLabWork())
            .needsImaging(request.getNeedsImaging())
            .needsProcedure(request.getNeedsProcedure())
            .needsSpecialist(request.getNeedsSpecialist())
            // Isolation
            .requiresIsolation(request.getRequiresIsolation())
            .isolationType(request.getIsolationType())
            .suspectedInfection(request.getSuspectedInfection())
            // Decision
            .recommendedZone(request.getRecommendedZone())
            .triageCategory(request.getTriageCategory())
            .estimatedWaitTimeMinutes(request.getEstimatedWaitTimeMinutes())
            // Notes
            .triageNotes(request.getTriageNotes())
            .nursingInterventions(request.getNursingInterventions())
            // Re-triage
            .isRetriage(request.getIsRetriage())
            .previousTriageId(request.getPreviousTriageId())
            .retriageReason(request.getRetriageReason())
            .build();
    }

    /**
     * Check if ESI should be recalculated.
     */
    private boolean shouldRecalculateESI(TriageAssessment assessment) {
        // Recalculate if red flags or abnormal vitals detected
        return assessment.hasCriticalRedFlags() || assessment.hasAbnormalVitals();
    }

    /**
     * Map ESI level to Triage Level color code.
     */
    private TriageLevel mapESIToTriageLevel(Integer esiLevel) {
        return switch (esiLevel) {
            case 1 -> TriageLevel.RED;    // Resuscitation
            case 2 -> TriageLevel.YELLOW; // Emergent
            case 3 -> TriageLevel.GREEN;  // Urgent
            case 4 -> TriageLevel.WHITE;  // Less Urgent
            case 5 -> TriageLevel.WHITE;  // Non-Urgent
            default -> TriageLevel.GREEN;
        };
    }

    /**
     * Determine ER zone based on triage level and assessment.
     */
    private String determineErZone(TriageLevel level, TriageAssessment assessment) {
        // Resuscitation cases
        if (level == TriageLevel.RED || (assessment.getGcsTotal() != null && assessment.getGcsTotal() < 8)) {
            return "RESUS_ROOM";
        }

        // Isolation needed
        if (Boolean.TRUE.equals(assessment.getRequiresIsolation())) {
            return "ISOLATION";
        }

        // By triage level
        return switch (level) {
            case RED -> "RED_ZONE";
            case YELLOW -> "YELLOW_ZONE";
            case GREEN -> "GREEN_ZONE";
            case WHITE -> "GREEN_ZONE";
            case BLACK -> "MORGUE";
        };
    }

    /**
     * Copy vital signs from assessment to emergency registration.
     */
    private void copyVitalSignsToEmergency(TriageAssessment assessment, EmergencyRegistration emergency) {
        emergency.setInitialBloodPressureSystolic(assessment.getBloodPressureSystolic());
        emergency.setInitialBloodPressureDiastolic(assessment.getBloodPressureDiastolic());
        emergency.setInitialHeartRate(assessment.getHeartRate());
        emergency.setInitialRespiratoryRate(assessment.getRespiratoryRate());
        emergency.setInitialTemperature(assessment.getTemperature());
        emergency.setInitialOxygenSaturation(assessment.getOxygenSaturation());
        emergency.setInitialGcsScore(assessment.getGcsTotal());
        emergency.setInitialPainScore(assessment.getPainScore());
    }
}
