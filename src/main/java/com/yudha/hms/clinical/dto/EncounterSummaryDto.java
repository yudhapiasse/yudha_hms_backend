package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.EncounterClass;
import com.yudha.hms.clinical.entity.EncounterStatus;
import com.yudha.hms.clinical.entity.EncounterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Summary DTO.
 * Lighter version for list views and search results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterSummaryDto {

    private UUID id;

    private String encounterNumber;

    private UUID patientId;

    private String patientName; // Can be populated from joined query

    private EncounterType encounterType;

    private EncounterClass encounterClass;

    private EncounterStatus status;

    private LocalDateTime encounterStart;

    private LocalDateTime encounterEnd;

    private String currentDepartment;

    private String attendingDoctorName;

    private String primaryDiagnosis; // Primary diagnosis text

    private Boolean isBpjs;

    private LocalDateTime createdAt;
}
