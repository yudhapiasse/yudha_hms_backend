package com.yudha.hms.registration.dto;

import com.yudha.hms.registration.entity.AdmissionStatus;
import com.yudha.hms.registration.entity.AdmissionType;
import com.yudha.hms.registration.entity.RoomClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for inpatient admission response.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdmissionResponse {

    private UUID id;
    private String admissionNumber;

    // Patient
    private UUID patientId;
    private String patientName;
    private String patientMrn;

    // Admission details
    private LocalDateTime admissionDate;
    private LocalDateTime admissionTime;
    private AdmissionType admissionType;
    private String admissionSource;

    // Room and bed
    private UUID roomId;
    private String roomNumber;
    private String roomName;
    private RoomClass roomClass;

    private UUID bedId;
    private String bedNumber;
    private String fullBedLocation; // e.g., "Gedung A - Floor 5 - A501 - BED-1"

    // Medical team
    private UUID admittingDoctorId;
    private String admittingDoctorName;
    private UUID attendingDoctorId;
    private String attendingDoctorName;
    private UUID referringDoctorId;
    private String referringDoctorName;
    private String referringFacility;

    // Clinical
    private String chiefComplaint;
    private String admissionDiagnosis;
    private List<DiagnosisInfo> diagnoses;

    // Stay
    private Integer estimatedLengthOfStayDays;
    private LocalDate estimatedDischargeDate;
    private Integer actualLengthOfStayDays;

    // Payment
    private String paymentMethod;
    private Boolean isBpjs;
    private String bpjsCardNumber;
    private String insuranceName;
    private String insuranceNumber;

    // Financial
    private BigDecimal roomRatePerDay;
    private BigDecimal requiredDeposit;
    private BigDecimal depositPaid;
    private BigDecimal depositBalance;
    private LocalDateTime depositPaidDate;
    private String depositReceiptNumber;

    // Status
    private AdmissionStatus status;

    // Discharge
    private LocalDateTime dischargeDate;
    private String dischargeType;
    private String dischargeDisposition;

    // Emergency contact
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;

    // Special needs
    private Boolean requiresIsolation;
    private String isolationType;
    private Boolean requiresInterpreter;
    private String interpreterLanguage;
    private Boolean hasAllergies;
    private String allergyNotes;

    // Notes
    private String admissionNotes;
    private String specialInstructions;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    /**
     * Inner class for diagnosis information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiagnosisInfo {
        private UUID id;
        private String icd10Code;
        private String icd10Description;
        private String diagnosisType;
        private Boolean isPrimary;
        private LocalDateTime diagnosedAt;
        private String notes;
    }
}
