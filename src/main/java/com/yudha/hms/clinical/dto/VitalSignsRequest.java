package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.Shift;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for recording vital signs.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignsRequest {

    private LocalDateTime measurementTime;

    private Shift shift;

    private String measurementType; // ROUTINE, ADMISSION, PRE_OP, POST_OP, STAT

    // Basic vital signs
    @Min(value = 40, message = "Systolic BP must be at least 40 mmHg")
    @Max(value = 300, message = "Systolic BP must not exceed 300 mmHg")
    private Integer systolicBp;

    @Min(value = 20, message = "Diastolic BP must be at least 20 mmHg")
    @Max(value = 200, message = "Diastolic BP must not exceed 200 mmHg")
    private Integer diastolicBp;

    @Min(value = 20, message = "Heart rate must be at least 20 bpm")
    @Max(value = 300, message = "Heart rate must not exceed 300 bpm")
    private Integer heartRate;

    @Min(value = 4, message = "Respiratory rate must be at least 4/min")
    @Max(value = 80, message = "Respiratory rate must not exceed 80/min")
    private Integer respiratoryRate;

    private BigDecimal temperature;

    private String temperatureRoute; // ORAL, AXILLARY, RECTAL, TYMPANIC

    @Min(value = 50, message = "SpO2 must be at least 50%")
    @Max(value = 100, message = "SpO2 must not exceed 100%")
    private Integer spo2;

    private Boolean oxygenTherapy;

    private BigDecimal oxygenFlowRate;

    private String oxygenDeliveryMethod;

    // Physical measurements
    private BigDecimal weight;

    private BigDecimal height;

    private BigDecimal headCircumference;

    // Glasgow Coma Scale
    @Min(value = 1, message = "GCS eye must be between 1-4")
    @Max(value = 4, message = "GCS eye must be between 1-4")
    private Integer gcsEye;

    @Min(value = 1, message = "GCS verbal must be between 1-5")
    @Max(value = 5, message = "GCS verbal must be between 1-5")
    private Integer gcsVerbal;

    @Min(value = 1, message = "GCS motor must be between 1-6")
    @Max(value = 6, message = "GCS motor must be between 1-6")
    private Integer gcsMotor;

    // Pain assessment
    @Min(value = 0, message = "Pain score must be between 0-10")
    @Max(value = 10, message = "Pain score must be between 0-10")
    private Integer painScore;

    private String painLocation;

    private String painQuality;

    // Fluid balance
    private Integer fluidIntakeMl;

    private Integer fluidOutputMl;

    private Integer urineOutputMl;

    // Blood glucose
    private BigDecimal bloodGlucose;

    private String bloodGlucoseUnit;

    // Additional parameters
    private String peripheralPulse;

    private BigDecimal capillaryRefillTime;

    private String pupilReaction;

    // Notes
    private String notes;

    private String alerts;

    // Provider
    private UUID recordedById;

    private String recordedByName;

    private String recordedByRole;

    // Location
    private String locationName;

    private String bedNumber;
}
