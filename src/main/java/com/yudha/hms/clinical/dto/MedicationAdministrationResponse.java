package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.AdministrationStatus;
import com.yudha.hms.clinical.entity.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for medication administration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationAdministrationResponse {

    private UUID id;

    private String marNumber;

    private UUID encounterId;

    private UUID patientId;

    private UUID medicationOrderId;

    // Medication identification
    private String medicationName;

    private String genericName;

    private String brandName;

    private String medicationCode;

    private String medicationClass;

    // Dosage
    private String dose;

    private String doseUnit;

    private String strength;

    private String totalDoseDescription;

    // Route and frequency
    private String route;

    private String frequency;

    private Integer frequencyTimesPerDay;

    // Schedule
    private ScheduleType scheduleType;

    private String scheduleTypeDisplay;

    private LocalDate scheduledDate;

    private LocalTime scheduledTime;

    private LocalDateTime scheduledDateTime;

    // Administration
    private LocalDateTime actualAdministrationDateTime;

    private Boolean administered;

    private AdministrationStatus administrationStatus;

    private String administrationStatusDisplay;

    private String administrationSite;

    // Provider
    private UUID administeredById;

    private String administeredByName;

    private String administeredByRole;

    // Witness
    private Boolean requiresWitness;

    private UUID witnessedById;

    private String witnessedByName;

    private String witnessSignature;

    // Reasons
    private String notGivenReason;

    private String holdReason;

    private String discontinueReason;

    // Patient response
    private String patientResponse;

    private Boolean adverseReaction;

    private String adverseReactionType;

    private String adverseReactionDetails;

    private String adverseReactionSeverity;

    private Boolean adverseReactionReported;

    // PRN
    private String prnReason;

    private String prnEffectiveness;

    // Prescriber
    private UUID prescribedById;

    private String prescribedByName;

    private LocalDateTime prescriptionDate;

    // IV specific
    private String ivSolution;

    private Integer ivVolumeMl;

    private BigDecimal ivRateMlPerHour;

    private Integer ivDurationMinutes;

    private String ivSiteLocation;

    // Notes
    private String administrationNotes;

    private String specialInstructions;

    // Alerts
    private Boolean isHighAlertMedication;

    private String highAlertType;

    // Audit
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    // Computed fields
    private Boolean isDue;

    private Boolean isOverdue;

    private Boolean needsWitnessVerification;
}
