package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Timeline Response DTO.
 *
 * Complete timeline visualization data for a patient's medical history.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineResponse {

    private UUID patientId;
    private List<TimelineEventResponse> events;

    // Time Range
    private LocalDateTime firstEncounter;
    private LocalDateTime lastEncounter;

    // Statistics
    private Integer totalEncounters;
    private Integer totalReadmissions;
    private Integer readmissionsWithin30Days;
    private Integer totalChronicConditions;

    // Chronic Disease Tracking
    private List<ChronicDiseaseProgressionResponse> chronicDiseaseProgression;

    // Treatment Pattern Analysis
    private Map<String, Integer> departmentVisitCounts;
    private Map<String, Integer> diagnosisCategoryCounts;
    private Map<String, List<LocalDateTime>> recurringDiagnoses;

    // Readmission Analysis
    private List<ReadmissionEventResponse> readmissionEvents;
}