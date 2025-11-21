package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.OrderPriority;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Lab Order Request DTO.
 *
 * Used for creating new lab orders from clinical modules.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabOrderRequest {

    // ========== Patient and Encounter Information ==========

    /**
     * Patient ID
     */
    @NotNull(message = "ID pasien harus diisi")
    private UUID patientId;

    /**
     * Encounter ID (visit/admission)
     */
    @NotNull(message = "ID kunjungan harus diisi")
    private UUID encounterId;

    // ========== Ordering Information ==========

    /**
     * Ordering doctor ID
     */
    @NotNull(message = "ID dokter pemeriksa harus diisi")
    private UUID orderingDoctorId;

    /**
     * Ordering department
     */
    @Size(max = 100, message = "Departemen maksimal 100 karakter")
    private String orderingDepartment;

    /**
     * Ordering location
     */
    @Size(max = 100, message = "Lokasi maksimal 100 karakter")
    private String orderingLocation;

    // ========== Priority ==========

    /**
     * Priority level (ROUTINE, URGENT, CITO)
     */
    @NotNull(message = "Prioritas harus dipilih")
    private OrderPriority priority;

    /**
     * Urgency reason (required for URGENT/CITO)
     */
    private String urgencyReason;

    // ========== Clinical Information ==========

    /**
     * Clinical indication for tests
     */
    private String clinicalIndication;

    /**
     * Diagnosis code (ICD-10)
     */
    @Size(max = 20, message = "Kode diagnosis maksimal 20 karakter")
    private String diagnosisCode;

    /**
     * Diagnosis text
     */
    @Size(max = 500, message = "Teks diagnosis maksimal 500 karakter")
    private String diagnosisText;

    // ========== Test Selection ==========

    /**
     * Test IDs to order
     */
    @NotEmpty(message = "Minimal satu test atau panel harus dipilih")
    private List<UUID> testIds;

    /**
     * Panel IDs to order
     */
    private List<UUID> panelIds;

    // ========== Sample Collection ==========

    /**
     * Collection scheduled date and time
     */
    private LocalDateTime collectionScheduledAt;

    /**
     * Collection location
     */
    @Size(max = 200, message = "Lokasi pengambilan maksimal 200 karakter")
    private String collectionLocation;

    // ========== Billing ==========

    /**
     * Payment method (CASH, INSURANCE, BPJS, etc.)
     */
    @Size(max = 50, message = "Metode pembayaran maksimal 50 karakter")
    private String paymentMethod;

    /**
     * Insurance company ID
     */
    private UUID insuranceCompanyId;

    /**
     * Coverage type
     */
    @Size(max = 50, message = "Tipe coverage maksimal 50 karakter")
    private String coverageType;

    // ========== Recurring Support ==========

    /**
     * Is recurring order
     */
    @Builder.Default
    private Boolean isRecurring = false;

    /**
     * Recurrence pattern (DAILY, WEEKLY, etc.)
     */
    @Size(max = 100, message = "Pola rekurensi maksimal 100 karakter")
    private String recurrencePattern;

    /**
     * Recurrence end date
     */
    private LocalDate recurrenceEndDate;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    private String notes;
}
