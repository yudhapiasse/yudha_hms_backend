package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for vital signs.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignsResponse {

    private UUID id;

    private UUID encounterId;

    private UUID patientId;

    // Timing
    private LocalDateTime measurementTime;

    private Shift shift;

    private String shiftDisplay;

    private String measurementType;

    // Basic vital signs
    private Integer systolicBp;

    private Integer diastolicBp;

    private String bloodPressure; // Formatted: "120/80"

    private Integer heartRate;

    private Integer respiratoryRate;

    private BigDecimal temperature;

    private String temperatureRoute;

    private Integer spo2;

    private Boolean oxygenTherapy;

    private BigDecimal oxygenFlowRate;

    private String oxygenDeliveryMethod;

    // Physical measurements
    private BigDecimal weight;

    private BigDecimal height;

    private BigDecimal bmi;

    private BigDecimal headCircumference;

    // Glasgow Coma Scale
    private Integer gcsEye;

    private Integer gcsVerbal;

    private Integer gcsMotor;

    private Integer gcsTotal;

    private String gcsSeverity; // SEVERE, MODERATE, MILD

    // Pain assessment
    private Integer painScore;

    private String painLocation;

    private String painQuality;

    // Fluid balance
    private Integer fluidIntakeMl;

    private Integer fluidOutputMl;

    private Integer fluidBalanceMl;

    private Integer urineOutputMl;

    // Blood glucose
    private BigDecimal bloodGlucose;

    private String bloodGlucoseUnit;

    // Additional parameters
    private Integer meanArterialPressure;

    private String peripheralPulse;

    private BigDecimal capillaryRefillTime;

    private String pupilReaction;

    // Alerts and flags
    private Boolean isAbnormal;

    private String abnormalFlags;

    private Boolean requiresNotification;

    private Boolean notificationSent;

    private UUID notifiedProviderId;

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

    // Audit
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Computed fields
    private Boolean withinNormalLimits;

    private Boolean requiresUrgentNotification;
}
