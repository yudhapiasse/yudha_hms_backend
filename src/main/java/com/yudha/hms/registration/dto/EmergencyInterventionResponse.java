package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.InterventionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for emergency interventions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyInterventionResponse {

    // ========== IDs ==========
    private UUID id;
    private UUID emergencyRegistrationId;
    private UUID encounterId;

    // ========== Basic Information ==========
    private InterventionType interventionType;
    private String interventionTypeDisplay;
    private String interventionTypeindonesian;
    private String interventionName;
    private LocalDateTime interventionTime;

    private UUID performedById;
    private String performedByName;
    private String performedByRole;

    // ========== Resuscitation Fields ==========
    private Boolean isResuscitation;
    private LocalDateTime resuscitationStartTime;
    private LocalDateTime resuscitationEndTime;
    private Integer resuscitationDurationMinutes;
    private Boolean roscAchieved;
    private LocalDateTime roscTime;
    private Integer cprQualityScore;
    private Integer defibrillationAttempts;
    private Integer epinephrineDoses;

    // ========== Airway Management ==========
    private String airwayType;
    private String tubeSize;
    private Integer insertionAttempts;
    private Boolean airwaySecured;

    // ========== Procedure ==========
    private String procedureCode;
    private String procedureSite;
    private String procedureApproach;
    private String complications;
    private String procedureOutcome;

    // ========== Medication ==========
    private String medicationName;
    private String medicationDose;
    private String medicationRoute;
    private String medicationFrequency;

    // ========== Transfusion ==========
    private String bloodProductType;
    private Integer unitsTransfused;
    private Boolean transfusionReaction;
    private Boolean crossMatchRequired;

    // ========== Common ==========
    private String indication;
    private String urgency;
    private String outcome;
    private String outcomeNotes;
    private Boolean complicationsOccurred;
    private String notes;

    // ========== Location ==========
    private String location;
    private String bedNumber;

    // ========== Audit ==========
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // ========== Computed Fields ==========
    private Boolean isCritical;
    private Boolean requiresSupervision;
    private Boolean isCompleted;
    private String displayName;
}
