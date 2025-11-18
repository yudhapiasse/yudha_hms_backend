package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.AdmissionType;
import com.yudha.hms.registration.entity.RoomClass;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating a new inpatient admission.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionRequest {

    // Patient information
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // Outpatient registration reference (if applicable)
    private UUID outpatientRegistrationId;

    // Admission details
    @NotNull(message = "Admission type is required")
    private AdmissionType admissionType;

    @Size(max = 30)
    private String admissionSource; // OUTPATIENT, EMERGENCY, REFERRAL, DIRECT

    // Room selection
    @NotNull(message = "Room class is required")
    private RoomClass roomClass;

    private UUID preferredRoomId;
    private UUID preferredBedId;

    // Medical team
    private UUID admittingDoctorId;
    private String admittingDoctorName;

    private UUID attendingDoctorId;
    private String attendingDoctorName;

    // Referring doctor (if transfer or referral)
    private UUID referringDoctorId;
    private String referringDoctorName;

    @Size(max = 200)
    private String referringFacility;

    // Clinical information
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    private String admissionDiagnosis;
    private String secondaryDiagnoses;

    // Diagnoses (ICD-10 codes)
    private List<DiagnosisDto> diagnoses;

    // Estimated stay
    @Min(value = 1, message = "Estimated stay must be at least 1 day")
    private Integer estimatedLengthOfStayDays;

    private LocalDate estimatedDischargeDate;

    // Payment information
    @NotBlank(message = "Payment method is required")
    @Size(max = 20)
    private String paymentMethod; // CASH, BPJS, INSURANCE, COMPANY

    private Boolean isBpjs;

    @Size(max = 50)
    private String bpjsCardNumber;

    @Size(max = 100)
    private String insuranceName;

    @Size(max = 50)
    private String insuranceNumber;

    @DecimalMin(value = "0.0")
    private BigDecimal insuranceCoverageLimit;

    // Deposit
    @DecimalMin(value = "0.0")
    private BigDecimal depositPaid;

    @Size(max = 50)
    private String depositReceiptNumber;

    // Emergency contact
    @Size(max = 200)
    private String emergencyContactName;

    @Size(max = 50)
    private String emergencyContactRelationship;

    @Size(max = 20)
    private String emergencyContactPhone;

    // Patient belongings
    private Boolean belongingsStored;
    private String belongingsList;

    // Special needs
    private Boolean requiresIsolation;

    @Size(max = 50)
    private String isolationType; // AIRBORNE, DROPLET, CONTACT, PROTECTIVE

    private Boolean requiresInterpreter;

    @Size(max = 50)
    private String interpreterLanguage;

    private Boolean hasAllergies;
    private String allergyNotes;

    // Notes
    private String admissionNotes;
    private String specialInstructions;

    // Number of days to calculate deposit (typically 2-3 days)
    @Min(1)
    @Builder.Default
    private Integer depositDays = 3;

    /**
     * Inner DTO for diagnosis information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisDto {
        private UUID icd10Id;

        @NotBlank
        @Size(max = 10)
        private String icd10Code;

        @NotBlank
        private String icd10Description;

        @NotBlank
        @Size(max = 20)
        private String diagnosisType; // PRIMARY, SECONDARY, COMPLICATION, COMORBIDITY

        private Boolean isPrimary;
        private String notes;
    }
}
