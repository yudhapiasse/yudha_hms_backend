package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.EncounterClass;
import com.yudha.hms.clinical.entity.Priority;
import com.yudha.hms.clinical.entity.EncounterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * External Patient Encounter Request DTO.
 *
 * Creates an encounter for emergency patients without full registration.
 * Allows for "Unknown Patient" handling with post-encounter data completion.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalPatientEncounterRequest {

    // Minimal patient information (can be updated later)
    private String patientName; // "Unknown Male", "Unknown Female", or actual name
    private String patientGender; // M, F, U (Unknown)
    private Integer estimatedAge; // If actual DOB not known
    private LocalDate estimatedDateOfBirth;
    private String identificationMarks; // Physical description for unknown patients

    // Encounter details
    @NotNull(message = "Encounter type is required")
    private EncounterType encounterType; // Usually EMERGENCY

    @NotNull(message = "Encounter class is required")
    private EncounterClass encounterClass;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    private String arrivalMode; // Ambulance, Walk-in, Police

    @NotNull(message = "Attending physician ID is required")
    private UUID attendingPhysicianId;

    @NotNull(message = "Department ID is required")
    private UUID departmentId;

    private LocalDateTime encounterStart;

    // For linking if patient is identified later
    private UUID linkedPatientId; // Can be set later when patient is identified

    // Police case information
    private Boolean isPoliceCase;
    private String policeCaseNumber;
    private String policeStation;

    // Brought by information
    private String broughtByName;
    private String broughtByRelation;
    private String broughtByContact;

    private String initialAssessment;
    private String notes;

    // Flag for post-encounter completion
    private Boolean requiresDataCompletion;
}
