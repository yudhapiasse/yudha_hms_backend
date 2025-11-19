package com.yudha.hms.registration.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for triage assessment request.
 * Based on Emergency Severity Index (ESI) methodology.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageAssessmentRequest {

    // ========== Triage Metadata ==========
    @NotNull(message = "Emergency registration ID is required")
    private UUID emergencyRegistrationId;

    @NotBlank(message = "Triaged by name is required")
    private String triagedByName;

    private UUID triagedById;

    @Builder.Default
    private String triageMethod = "ESI"; // ESI, ATS, CTAS, MTS

    // ========== ESI Level ==========
    @NotNull(message = "ESI level is required")
    @Min(1)
    @Max(5)
    private Integer esiLevel;

    // ========== Vital Signs ==========
    @Min(0)
    @Max(300)
    private Integer bloodPressureSystolic;

    @Min(0)
    @Max(200)
    private Integer bloodPressureDiastolic;

    @Min(0)
    @Max(300)
    private Integer heartRate;

    @Min(0)
    @Max(100)
    private Integer respiratoryRate;

    @DecimalMin("30.0")
    @DecimalMax("45.0")
    private BigDecimal temperature;

    @Min(0)
    @Max(100)
    private Integer oxygenSaturation;

    @DecimalMin("0.0")
    @DecimalMax("999.9")
    private BigDecimal bloodGlucose;

    // ========== Neurological Assessment (GCS) ==========
    @Min(1)
    @Max(4)
    private Integer gcsEyeOpening;

    @Min(1)
    @Max(5)
    private Integer gcsVerbalResponse;

    @Min(1)
    @Max(6)
    private Integer gcsMotorResponse;

    private String pupilResponse; // EQUAL_REACTIVE, UNEQUAL, NON_REACTIVE
    private String consciousnessLevel; // ALERT, VERBAL, PAIN, UNRESPONSIVE

    // ========== Pain Assessment ==========
    @Min(0)
    @Max(10)
    private Integer painScore;

    private String painLocation;
    private String painCharacteristics;
    private String painOnset;

    // ========== Respiratory Assessment ==========
    @Builder.Default
    private Boolean respiratoryDistress = false;

    private String airwayStatus; // PATENT, COMPROMISED, OBSTRUCTED
    private String breathingPattern; // NORMAL, LABORED, SHALLOW, IRREGULAR

    @Builder.Default
    private Boolean oxygenTherapy = false;

    private String oxygenDeliveryMethod; // NASAL_CANNULA, MASK, NON_REBREATHER
    private BigDecimal oxygenFlowRate;

    // ========== Cardiovascular Assessment ==========
    private String peripheralPulses; // STRONG, WEAK, ABSENT
    private BigDecimal capillaryRefillSeconds;
    private String skinColor; // NORMAL, PALE, CYANOTIC, FLUSHED
    private String skinTemperature; // WARM, COOL, COLD, HOT

    // ========== Chief Complaint & History ==========
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    private String historyPresentIllness;
    private LocalDateTime symptomOnset;
    private String relevantMedicalHistory;
    private String currentMedications;
    private String allergies;

    // ========== Red Flags ==========
    @Builder.Default
    private Boolean hasChestPain = false;

    @Builder.Default
    private Boolean hasDifficultyBreathing = false;

    @Builder.Default
    private Boolean hasAlteredConsciousness = false;

    @Builder.Default
    private Boolean hasSevereBleeding = false;

    @Builder.Default
    private Boolean hasSeverePain = false;

    @Builder.Default
    private Boolean hasSeizures = false;

    @Builder.Default
    private Boolean hasPoisoning = false;

    // ========== Resource Needs Assessment ==========
    @Builder.Default
    private Integer expectedResourcesCount = 0;

    @Builder.Default
    private Boolean needsLabWork = false;

    @Builder.Default
    private Boolean needsImaging = false;

    @Builder.Default
    private Boolean needsProcedure = false;

    @Builder.Default
    private Boolean needsSpecialist = false;

    // ========== Isolation ==========
    @Builder.Default
    private Boolean requiresIsolation = false;

    private String isolationType; // AIRBORNE, DROPLET, CONTACT, PROTECTIVE
    private String suspectedInfection;

    // ========== Triage Decision ==========
    private String recommendedZone; // RED_ZONE, YELLOW_ZONE, GREEN_ZONE, RESUS_ROOM
    private String triageCategory; // IMMEDIATE, URGENT, NON_URGENT, MINOR
    private Integer estimatedWaitTimeMinutes;

    // ========== Notes ==========
    private String triageNotes;
    private String nursingInterventions;

    // ========== Re-triage ==========
    @Builder.Default
    private Boolean isRetriage = false;

    private UUID previousTriageId;
    private String retriageReason;
}
