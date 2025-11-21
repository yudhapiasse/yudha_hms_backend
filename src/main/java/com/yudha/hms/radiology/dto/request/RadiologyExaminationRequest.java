package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Radiology Examination Request DTO.
 *
 * Used for creating and updating radiology examinations in the catalog.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyExaminationRequest {

    /**
     * Examination code (internal hospital code)
     */
    @NotBlank(message = "Kode pemeriksaan harus diisi")
    @Size(max = 50, message = "Kode pemeriksaan maksimal 50 karakter")
    private String examCode;

    /**
     * Examination name
     */
    @NotBlank(message = "Nama pemeriksaan harus diisi")
    @Size(max = 200, message = "Nama pemeriksaan maksimal 200 karakter")
    private String examName;

    /**
     * Short name
     */
    @Size(max = 100, message = "Nama singkat maksimal 100 karakter")
    private String shortName;

    /**
     * Modality ID
     */
    @NotNull(message = "Modalitas harus dipilih")
    private UUID modalityId;

    // ========== Coding ==========

    /**
     * CPT code for procedure
     */
    @Size(max = 20, message = "Kode CPT maksimal 20 karakter")
    private String cptCode;

    /**
     * ICD procedure code
     */
    @Size(max = 20, message = "Kode prosedur ICD maksimal 20 karakter")
    private String icdProcedureCode;

    // ========== Preparation Requirements ==========

    /**
     * Preparation instructions
     */
    private String preparationInstructions;

    /**
     * Whether fasting is required
     */
    @Builder.Default
    private Boolean fastingRequired = false;

    /**
     * Fasting duration in hours
     */
    @Min(value = 0, message = "Durasi puasa tidak boleh negatif")
    @Max(value = 48, message = "Durasi puasa maksimal 48 jam")
    private Integer fastingDurationHours;

    // ========== Contrast Requirements ==========

    /**
     * Whether contrast is required
     */
    @Builder.Default
    private Boolean requiresContrast = false;

    /**
     * Contrast type
     */
    @Size(max = 50, message = "Tipe kontras maksimal 50 karakter")
    private String contrastType;

    /**
     * Contrast volume in ml
     */
    @DecimalMin(value = "0.0", message = "Volume kontras tidak boleh negatif")
    private BigDecimal contrastVolumeMl;

    // ========== Timing Information ==========

    /**
     * Examination duration in minutes
     */
    @Min(value = 0, message = "Durasi pemeriksaan tidak boleh negatif")
    private Integer examDurationMinutes;

    /**
     * Reporting time in minutes
     */
    @Min(value = 0, message = "Waktu pelaporan tidak boleh negatif")
    private Integer reportingTimeMinutes;

    // ========== Cost Information ==========

    /**
     * Base cost
     */
    @NotNull(message = "Biaya dasar harus diisi")
    @DecimalMin(value = "0.0", inclusive = false, message = "Biaya dasar harus lebih dari 0")
    private BigDecimal baseCost;

    /**
     * Contrast cost
     */
    @DecimalMin(value = "0.0", message = "Biaya kontras tidak boleh negatif")
    @Builder.Default
    private BigDecimal contrastCost = BigDecimal.ZERO;

    /**
     * BPJS tariff (for BPJS patients)
     */
    @DecimalMin(value = "0.0", message = "Tarif BPJS tidak boleh negatif")
    private BigDecimal bpjsTariff;

    // ========== Body Part and Positioning ==========

    /**
     * Body part examined
     */
    @Size(max = 200, message = "Bagian tubuh maksimal 200 karakter")
    private String bodyPart;

    /**
     * Whether laterality is applicable
     */
    @Builder.Default
    private Boolean lateralityApplicable = false;

    /**
     * Positioning notes
     */
    private String positioningNotes;

    // ========== Clinical Information ==========

    /**
     * Clinical indication
     */
    private String clinicalIndication;

    /**
     * Interpretation guide
     */
    private String interpretationGuide;

    // ========== Approval ==========

    /**
     * Whether approval is required
     */
    @Builder.Default
    private Boolean requiresApproval = false;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean isActive = true;
}
