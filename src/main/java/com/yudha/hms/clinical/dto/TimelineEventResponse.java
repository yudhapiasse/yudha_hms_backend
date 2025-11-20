package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Timeline Event Response DTO.
 *
 * Represents a single event in the patient's medical timeline.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEventResponse {

    private UUID encounterId;
    private String encounterNumber;

    // Event Type
    private String eventType; // ADMISSION, DISCHARGE, DIAGNOSIS, TRANSFER, PROCEDURE
    private String encounterType; // OUTPATIENT, INPATIENT, EMERGENCY

    // Timing
    private LocalDateTime eventDate;
    private LocalDateTime admissionDate;
    private LocalDateTime dischargeDate;
    private Integer lengthOfStayDays;

    // Location
    private String department;
    private String location;

    // Care Provider
    private String attendingDoctor;

    // Clinical Summary
    private String primaryDiagnosis;
    private List<String> diagnoses;

    // Status Flags
    private Boolean isReadmission;
    private Integer daysSincePreviousDischarge;
    private Boolean isChronicDisease;
    private String chronicDiseaseType;

    // Visual Properties (for frontend)
    private String color; // Color code for timeline visualization
    private String icon; // Icon identifier for event type
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
}