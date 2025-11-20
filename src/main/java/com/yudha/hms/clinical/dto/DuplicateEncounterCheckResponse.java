package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Duplicate Encounter Check Response DTO.
 *
 * Response for checking potential duplicate encounters for a patient.
 * Flags same-day multiple encounters and duplicate active inpatient encounters.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateEncounterCheckResponse {

    private UUID patientId;
    private Boolean hasPotentialDuplicates;
    private Boolean hasActiveInpatientEncounter;
    private Boolean hasSameDayEncounters;

    @Builder.Default
    private List<PotentialDuplicateEncounter> potentialDuplicates = new ArrayList<>();

    @Builder.Default
    private List<SameDayEncounter> sameDayEncounters = new ArrayList<>();

    private String warning;
    private Boolean canProceed; // Whether new encounter can be created

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PotentialDuplicateEncounter {
        private UUID encounterId;
        private String encounterNumber;
        private String encounterType;
        private String encounterStatus;
        private LocalDateTime encounterStart;
        private String departmentName;
        private String chiefComplaint;
        private String similarityReason; // Why it's flagged as duplicate
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SameDayEncounter {
        private UUID encounterId;
        private String encounterNumber;
        private String encounterType;
        private String departmentName;
        private LocalDateTime encounterStart;
        private String status;
    }
}
