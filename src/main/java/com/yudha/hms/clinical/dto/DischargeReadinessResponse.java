package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Discharge Readiness Response DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DischargeReadinessResponse {

    private UUID id;
    private UUID encounterId;
    private UUID patientId;

    // Readiness Criteria
    private Boolean medicalStabilityMet;
    private String medicalStabilityNotes;
    private LocalDateTime medicalStabilityAssessedAt;
    private String medicalStabilityAssessedBy;

    private Boolean homeCareArranged;
    private String homeCareNotes;
    private String caregiverName;
    private String caregiverContact;

    private Boolean medicationsReconciled;
    private String medicationReconciliationNotes;
    private LocalDateTime medicationReconciledAt;
    private String medicationReconciledBy;

    private Boolean followUpScheduled;
    private LocalDateTime followUpAppointmentDate;
    private String followUpProvider;
    private String followUpDepartment;

    private Boolean patientEducationCompleted;
    private String patientEducationTopics;
    private Boolean patientUnderstandingVerified;

    private Boolean dmeOrdered;
    private String dmeDescription;
    private Boolean medicalSuppliesProvided;
    private String medicalSuppliesList;

    private Boolean hasDischargeBarriers;
    private String dischargeBarriers;
    private Boolean barriersResolved;

    // Overall Assessment
    private Boolean readyForDischarge;
    private LocalDateTime readinessAssessedAt;
    private UUID readinessAssessedById;
    private String readinessAssessedByName;

    private String additionalNotes;

    // Computed
    private Double readinessPercentage;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
