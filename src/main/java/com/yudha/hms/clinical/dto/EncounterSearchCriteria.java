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
 * Encounter Search Criteria DTO.
 * Used for filtering encounters in search/list operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterSearchCriteria {

    private UUID patientId;

    private String encounterNumber;

    private EncounterType encounterType;

    private EncounterClass encounterClass;

    private EncounterStatus status;

    private String department;

    private UUID attendingDoctorId;

    private Boolean isBpjs;

    private String sepNumber;

    private LocalDateTime encounterStartFrom;

    private LocalDateTime encounterStartTo;

    private LocalDateTime encounterEndFrom;

    private LocalDateTime encounterEndTo;

    // Pagination
    private Integer page;

    private Integer size;

    // Sorting
    private String sortBy; // encounterStart, encounterNumber, status, etc.

    private String sortDirection; // ASC, DESC
}
