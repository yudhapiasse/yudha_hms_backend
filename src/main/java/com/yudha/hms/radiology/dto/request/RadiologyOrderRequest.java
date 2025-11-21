package com.yudha.hms.radiology.dto.request;

import com.yudha.hms.radiology.constant.OrderPriority;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Radiology Order Request DTO.
 *
 * Used for creating new radiology orders from clinical modules.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyOrderRequest {

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

    // ========== Priority ==========

    /**
     * Priority level (ROUTINE, URGENT, CITO)
     */
    @NotNull(message = "Prioritas harus dipilih")
    private OrderPriority priority;

    // ========== Clinical Information ==========

    /**
     * Clinical indication for examinations
     */
    private String clinicalIndication;

    /**
     * Diagnosis text
     */
    @Size(max = 500, message = "Teks diagnosis maksimal 500 karakter")
    private String diagnosisText;

    // ========== Scheduling ==========

    /**
     * Scheduled date
     */
    private LocalDate scheduledDate;

    /**
     * Scheduled time
     */
    private LocalTime scheduledTime;

    /**
     * Room ID
     */
    private UUID roomId;

    // ========== Examination Selection ==========

    /**
     * Examination IDs to order
     */
    @NotEmpty(message = "Minimal satu pemeriksaan harus dipilih")
    private List<UUID> examinationIds;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    private String notes;

    /**
     * Special instructions
     */
    private String specialInstructions;
}
