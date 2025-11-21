package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.SampleType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lab Test Request DTO.
 *
 * Used for creating and updating lab tests in the test catalog.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestRequest {

    /**
     * Test code (internal hospital code)
     */
    @NotBlank(message = "Kode test harus diisi")
    @Size(max = 50, message = "Kode test maksimal 50 karakter")
    private String testCode;

    /**
     * Test name
     */
    @NotBlank(message = "Nama test harus diisi")
    @Size(max = 200, message = "Nama test maksimal 200 karakter")
    private String name;

    /**
     * Short name
     */
    @Size(max = 100, message = "Nama singkat maksimal 100 karakter")
    private String shortName;

    /**
     * Test category ID
     */
    @NotNull(message = "Kategori test harus dipilih")
    private UUID categoryId;

    // ========== LOINC Coding ==========

    /**
     * LOINC code for international standard
     */
    @Size(max = 20, message = "Kode LOINC maksimal 20 karakter")
    private String loincCode;

    /**
     * LOINC display name
     */
    @Size(max = 500, message = "Nama LOINC maksimal 500 karakter")
    private String loincDisplayName;

    // ========== Sample Requirements ==========

    /**
     * Sample type required
     */
    @NotNull(message = "Tipe sampel harus dipilih")
    private SampleType sampleType;

    /**
     * Sample volume in ml
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Volume sampel harus lebih dari 0")
    private BigDecimal sampleVolumeMl;

    /**
     * Sample volume unit
     */
    @Size(max = 20, message = "Unit volume maksimal 20 karakter")
    private String sampleVolumeUnit;

    /**
     * Sample container type
     */
    @Size(max = 100, message = "Tipe kontainer maksimal 100 karakter")
    private String sampleContainer;

    /**
     * Sample preservation requirements
     */
    @Size(max = 200, message = "Persyaratan preservasi maksimal 200 karakter")
    private String samplePreservation;

    /**
     * Fasting required
     */
    @Builder.Default
    private Boolean fastingRequired = false;

    /**
     * Fasting duration in hours
     */
    @Min(value = 0, message = "Durasi puasa tidak boleh negatif")
    @Max(value = 48, message = "Durasi puasa maksimal 48 jam")
    private Integer fastingDurationHours;

    // ========== Processing Information ==========

    /**
     * Processing time in minutes (TAT - Turnaround Time)
     */
    @Min(value = 0, message = "Waktu proses tidak boleh negatif")
    private Integer processingTimeMinutes;

    /**
     * Urgency available (can be marked as urgent/cito)
     */
    @Builder.Default
    private Boolean urgencyAvailable = true;

    /**
     * CITO processing time in minutes
     */
    @Min(value = 0, message = "Waktu proses CITO tidak boleh negatif")
    private Integer citoProcessingTimeMinutes;

    // ========== Cost Information ==========

    /**
     * Base cost
     */
    @NotNull(message = "Biaya dasar harus diisi")
    @DecimalMin(value = "0.0", inclusive = false, message = "Biaya dasar harus lebih dari 0")
    private BigDecimal baseCost;

    /**
     * Urgent cost (additional or total for urgent tests)
     */
    @DecimalMin(value = "0.0", message = "Biaya urgent tidak boleh negatif")
    private BigDecimal urgentCost;

    /**
     * BPJS tariff (for BPJS patients)
     */
    @DecimalMin(value = "0.0", message = "Tarif BPJS tidak boleh negatif")
    private BigDecimal bpjsTariff;

    // ========== Test Configuration ==========

    /**
     * Test method
     */
    @Size(max = 200, message = "Metode test maksimal 200 karakter")
    private String testMethod;

    /**
     * Test methodology (detailed)
     */
    private String testMethodology;

    /**
     * Reference method
     */
    @Size(max = 200, message = "Metode referensi maksimal 200 karakter")
    private String referenceMethod;

    /**
     * Requires approval before processing
     */
    @Builder.Default
    private Boolean requiresApproval = false;

    /**
     * Requires pathologist review
     */
    @Builder.Default
    private Boolean requiresPathologistReview = false;

    // ========== Critical Values ==========

    /**
     * Has critical values defined
     */
    @Builder.Default
    private Boolean hasCriticalValues = false;

    /**
     * Critical low value
     */
    private BigDecimal criticalLowValue;

    /**
     * Critical high value
     */
    private BigDecimal criticalHighValue;

    // ========== Quality Control ==========

    /**
     * QC required
     */
    @Builder.Default
    private Boolean qcRequired = true;

    /**
     * QC frequency in hours
     */
    @Min(value = 0, message = "Frekuensi QC tidak boleh negatif")
    private Integer qcFrequencyHours;

    /**
     * Calibration required
     */
    @Builder.Default
    private Boolean calibrationRequired = false;

    /**
     * Calibration frequency in days
     */
    @Min(value = 0, message = "Frekuensi kalibrasi tidak boleh negatif")
    private Integer calibrationFrequencyDays;

    // ========== Additional Information ==========

    /**
     * Clinical indication
     */
    private String clinicalIndication;

    /**
     * Preparation instructions for patient
     */
    private String preparationInstructions;

    /**
     * Interpretation guide
     */
    private String interpretationGuide;

    /**
     * Notes
     */
    private String notes;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean active = true;
}
