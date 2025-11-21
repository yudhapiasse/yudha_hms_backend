package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.PrescriptionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePrescriptionRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    private UUID encounterId;

    @NotNull(message = "Prescription type is required")
    private PrescriptionType prescriptionType;

    @NotNull(message = "Prescription date is required")
    private LocalDate prescriptionDate;

    @Size(max = 500)
    private String diagnosis;

    @Size(max = 500)
    private String icd10Codes;

    private String specialInstructions;

    private String allergies;

    @NotNull(message = "At least one prescription item is required")
    @Size(min = 1, message = "At least one item is required")
    @Valid
    @Builder.Default
    private List<PrescriptionItemRequest> items = new ArrayList<>();

    private String authorizationNumber;

    private String notes;
}
