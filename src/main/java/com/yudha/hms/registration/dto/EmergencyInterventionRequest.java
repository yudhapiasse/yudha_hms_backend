package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.InterventionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for creating/updating emergency interventions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyInterventionRequest {

    // ========== Basic Information ==========
    @NotNull(message = "Intervention type is required")
    private InterventionType interventionType;

    @NotBlank(message = "Intervention name is required")
    @Size(max = 200, message = "Intervention name must not exceed 200 characters")
    private String interventionName;

    private LocalDateTime interventionTime;

    private UUID performedById;

    @NotBlank(message = "Performer name is required")
    @Size(max = 200, message = "Performer name must not exceed 200 characters")
    private String performedByName;

    @Size(max = 50, message = "Performer role must not exceed 50 characters")
    private String performedByRole; // DOCTOR, NURSE, PARAMEDIC

    // ========== Resuscitation Fields ==========
    private Boolean isResuscitation;

    private LocalDateTime resuscitationStartTime;

    private LocalDateTime resuscitationEndTime;

    private Boolean roscAchieved;

    private LocalDateTime roscTime;

    @Min(value = 0, message = "CPR quality score must be between 0 and 100")
    @Max(value = 100, message = "CPR quality score must be between 0 and 100")
    private Integer cprQualityScore;

    @Min(value = 0, message = "Defibrillation attempts cannot be negative")
    private Integer defibrillationAttempts;

    @Min(value = 0, message = "Epinephrine doses cannot be negative")
    private Integer epinephrineDoses;

    // ========== Airway Management Fields ==========
    @Size(max = 50, message = "Airway type must not exceed 50 characters")
    private String airwayType; // INTUBATION, TRACHEOSTOMY, etc.

    @Size(max = 20, message = "Tube size must not exceed 20 characters")
    private String tubeSize;

    @Min(value = 0, message = "Insertion attempts cannot be negative")
    private Integer insertionAttempts;

    private Boolean airwaySecured;

    // ========== Procedure Fields ==========
    @Size(max = 50, message = "Procedure code must not exceed 50 characters")
    private String procedureCode;

    @Size(max = 100, message = "Procedure site must not exceed 100 characters")
    private String procedureSite;

    @Size(max = 50, message = "Procedure approach must not exceed 50 characters")
    private String procedureApproach;

    private String complications;

    @Size(max = 50, message = "Procedure outcome must not exceed 50 characters")
    private String procedureOutcome; // SUCCESS, FAILED, PARTIAL

    // ========== Medication Fields ==========
    @Size(max = 200, message = "Medication name must not exceed 200 characters")
    private String medicationName;

    @Size(max = 100, message = "Medication dose must not exceed 100 characters")
    private String medicationDose;

    @Size(max = 50, message = "Medication route must not exceed 50 characters")
    private String medicationRoute;

    @Size(max = 100, message = "Medication frequency must not exceed 100 characters")
    private String medicationFrequency;

    // ========== Transfusion Fields ==========
    @Size(max = 50, message = "Blood product type must not exceed 50 characters")
    private String bloodProductType; // PRBC, FFP, PLATELETS

    @Min(value = 0, message = "Units transfused cannot be negative")
    private Integer unitsTransfused;

    private Boolean transfusionReaction;

    private Boolean crossMatchRequired;

    // ========== Common Fields ==========
    @NotBlank(message = "Indication is required")
    private String indication;

    @Size(max = 20, message = "Urgency must not exceed 20 characters")
    private String urgency; // EMERGENCY, URGENT, ROUTINE

    @Size(max = 50, message = "Outcome must not exceed 50 characters")
    private String outcome;

    private String outcomeNotes;

    private Boolean complicationsOccurred;

    private String notes;

    // ========== Location ==========
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location; // RED_ZONE, RESUS_ROOM, etc.

    @Size(max = 20, message = "Bed number must not exceed 20 characters")
    private String bedNumber;
}
