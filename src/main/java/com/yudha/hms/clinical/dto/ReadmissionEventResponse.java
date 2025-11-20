package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Readmission Event Response DTO.
 *
 * Represents a readmission event with context from previous admission.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadmissionEventResponse {

    // Current Admission
    private UUID currentEncounterId;
    private String currentEncounterNumber;
    private LocalDateTime currentAdmissionDate;
    private String currentPrimaryDiagnosis;

    // Previous Admission
    private UUID previousEncounterId;
    private String previousEncounterNumber;
    private LocalDateTime previousDischargeDate;
    private String previousPrimaryDiagnosis;

    // Readmission Analysis
    private Integer daysBetween;
    private Boolean isUnplannedReadmission;
    private Boolean isSameDiagnosisCategory;
    private String readmissionReason;

    // Risk Indicators
    private Boolean isHighRisk; // < 7 days
    private Boolean isMediumRisk; // 7-30 days
    private String riskLevel; // HIGH, MEDIUM, LOW
}