package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for setting claim data (set_claim_data method).
 *
 * Sets comprehensive claim information including:
 * - Patient demographics
 * - Admission details
 * - Discharge information
 * - Billing summary
 *
 * Method: set_claim_data
 * Endpoint: PUT /ws/v1.0/claim/data
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClaimDataRequest extends EklaimBaseRequest {

    @JsonProperty("data")
    private ClaimInfo data;

    @Data
    public static class ClaimInfo {
        @NotBlank(message = "Claim number is required")
        @JsonProperty("claim_number")
        private String claimNumber;

        // Patient Information
        @NotBlank(message = "Patient name is required")
        @JsonProperty("patient_name")
        private String patientName;

        @NotBlank(message = "Gender is required")
        @Pattern(regexp = "^[12]$", message = "Gender must be 1 (male) or 2 (female)")
        @JsonProperty("gender")
        private String gender;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("birth_date")
        private LocalDate birthDate;

        @Min(0)
        @Max(150)
        @JsonProperty("age_years")
        private Integer ageYears;

        @Min(0)
        @Max(11)
        @JsonProperty("age_months")
        private Integer ageMonths = 0;

        @Min(0)
        @Max(30)
        @JsonProperty("age_days")
        private Integer ageDays = 0;

        @JsonProperty("weight")
        @DecimalMin("0.0")
        @DecimalMax("500.0")
        private BigDecimal weight; // In kilograms

        // Admission Information
        @NotNull(message = "Admission date is required")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("admission_date")
        private java.time.LocalDateTime admissionDate;

        @NotBlank(message = "Admission type is required")
        @Pattern(regexp = "^[1-4]$", message = "Admission type: 1=emergency, 2=planned, 3=elective, 4=delivery")
        @JsonProperty("admission_type")
        private String admissionType;

        @NotBlank(message = "Care type is required")
        @Pattern(regexp = "^[1-2]$", message = "Care type: 1=outpatient, 2=inpatient")
        @JsonProperty("care_type")
        private String careType;

        @NotBlank(message = "Care class is required")
        @Pattern(regexp = "^[1-3]$", message = "Care class: 1=first class, 2=second class, 3=third class")
        @JsonProperty("care_class")
        private String careClass;

        // Discharge Information
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("discharge_date")
        private java.time.LocalDateTime dischargeDate;

        @Pattern(regexp = "^[1-8]$", message = "Discharge status: 1=alive improved, 2=alive unimproved, 3=alive with complications, 4=dead <48h, 5=dead ≥48h, 6=transferred, 7=returned home on request, 8=stillbirth")
        @JsonProperty("discharge_status")
        private String dischargeStatus;

        @Min(0)
        @JsonProperty("los_days")
        private Integer losDays; // Length of stay in days

        @Min(0)
        @JsonProperty("los_hours")
        private Integer losHours = 0;

        // Billing Information
        @NotNull(message = "Total billing is required")
        @DecimalMin("0.0")
        @JsonProperty("total_billing")
        private BigDecimal totalBilling;

        @JsonProperty("billing_details")
        private BillingDetails billingDetails;
    }

    @Data
    public static class BillingDetails {
        @DecimalMin("0.0")
        @JsonProperty("accommodation")
        private BigDecimal accommodation = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("intensive_care")
        private BigDecimal intensiveCare = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("professional_services")
        private BigDecimal professionalServices = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("consultation")
        private BigDecimal consultation = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("procedures")
        private BigDecimal procedures = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("radiology")
        private BigDecimal radiology = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("laboratory")
        private BigDecimal laboratory = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("blood_services")
        private BigDecimal bloodServices = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("rehabilitation")
        private BigDecimal rehabilitation = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("medications")
        private BigDecimal medications = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("medical_supplies")
        private BigDecimal medicalSupplies = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("medical_devices")
        private BigDecimal medicalDevices = BigDecimal.ZERO;

        @DecimalMin("0.0")
        @JsonProperty("other_costs")
        private BigDecimal otherCosts = BigDecimal.ZERO;
    }
}
