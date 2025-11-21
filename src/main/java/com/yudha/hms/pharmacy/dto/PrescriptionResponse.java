package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.PrescriptionStatus;
import com.yudha.hms.pharmacy.constant.PrescriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {
    private UUID id;
    private String prescriptionNumber;
    private UUID patientId;
    private String patientName;
    private UUID encounterId;
    private UUID doctorId;
    private String doctorName;
    private LocalDate prescriptionDate;
    private PrescriptionType prescriptionType;
    private PrescriptionStatus status;
    private LocalDate validUntil;
    private String diagnosis;
    private String icd10Codes;
    private String specialInstructions;
    private String allergies;
    private Boolean hasInteractions;
    private String interactionWarnings;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private UUID verifiedBy;
    private String verifiedByName;
    private LocalDateTime dispensedAt;
    private UUID dispensedBy;
    private String dispensedByName;
    private Boolean isControlled;
    private Boolean requiresAuthorization;
    private String authorizationNumber;
    private String notes;
    private Boolean active;
    @Builder.Default
    private List<PrescriptionItemResponse> items = new ArrayList<>();
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
