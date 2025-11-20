package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Chronic Disease Progression Response DTO.
 *
 * Tracks the progression of a chronic disease over time.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChronicDiseaseProgressionResponse {

    private String diagnosisCode;
    private String diagnosisName;
    private String diseaseCategory;

    // Timeline
    private LocalDateTime firstDiagnosed;
    private LocalDateTime lastEncounter;

    // Visit Statistics
    private Integer totalRelatedVisits;
    private List<LocalDateTime> visitDates;

    // Treatment Pattern
    private List<String> commonTreatments;
    private List<String> associatedDepartments;

    // Progression Indicators
    private String progressionTrend; // STABLE, IMPROVING, DETERIORATING, UNKNOWN
    private String severity; // MILD, MODERATE, SEVERE, CRITICAL
}