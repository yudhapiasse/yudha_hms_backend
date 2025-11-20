package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * Request DTO for medication administration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationAdministrationRequest {

    private UUID medicationOrderId;

    // Medication identification
    @NotBlank(message = "Medication name is required")
    private String medicationName;

    private String genericName;

    private String brandName;

    private String medicationCode;

    private String medicationClass;

    // Dosage
    @NotBlank(message = "Dose is required")
    private String dose;

    @NotBlank(message = "Dose unit is required")
    private String doseUnit;

    private String strength;

    private String totalDoseDescription;

    // Route and frequency
    @NotBlank(message = "Route is required")
    private String route;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    private Integer frequencyTimesPerDay;

    // Schedule
    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType;

    private LocalDate scheduledDate;

    private LocalTime scheduledTime;

    private LocalDateTime scheduledDateTime;

    // Administration site
    private String administrationSite;

    // Verification
    private Boolean requiresWitness;

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
}
