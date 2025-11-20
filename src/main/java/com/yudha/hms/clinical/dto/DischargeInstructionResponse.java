package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Discharge Instruction Response DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DischargeInstructionResponse {

    private UUID id;
    private UUID dischargeSummaryId;

    private String instructionCategory;
    private String instructionTitle;
    private String instructionDetails;
    private String instructionFrequency;
    private String instructionDuration;

    private String doInstructions;
    private String dontInstructions;
    private String whenToCallDoctor;

    private Boolean hasVideoTutorial;
    private String videoUrl;
    private Boolean hasPrintedMaterial;
    private String printedMaterialUrl;
    private String diagramUrl;

    private Boolean patientEducated;
    private Boolean patientDemonstratesUnderstanding;
    private String educationNotes;
    private String educatorName;

    private Boolean isCriticalInstruction;
    private Integer displayOrder;
    private String additionalNotes;

    // Computed flags
    private Boolean isWoundCare;
    private Boolean isDietInstruction;
    private Boolean isActivityInstruction;
    private Boolean isPhysicalTherapy;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
