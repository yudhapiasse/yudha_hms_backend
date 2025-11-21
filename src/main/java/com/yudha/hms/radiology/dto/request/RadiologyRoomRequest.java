package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Radiology Room Request DTO.
 *
 * Used for creating and updating radiology rooms and equipment.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyRoomRequest {

    /**
     * Room code (unique identifier)
     */
    @NotBlank(message = "Kode ruangan harus diisi")
    @Size(max = 50, message = "Kode ruangan maksimal 50 karakter")
    private String roomCode;

    /**
     * Room name
     */
    @NotBlank(message = "Nama ruangan harus diisi")
    @Size(max = 200, message = "Nama ruangan maksimal 200 karakter")
    private String roomName;

    /**
     * Room location
     */
    @Size(max = 200, message = "Lokasi ruangan maksimal 200 karakter")
    private String location;

    /**
     * Floor number
     */
    @Size(max = 50, message = "Lantai maksimal 50 karakter")
    private String floor;

    /**
     * Modality ID
     */
    @NotNull(message = "Modalitas harus dipilih")
    private UUID modalityId;

    // ========== Equipment Information ==========

    /**
     * Equipment name
     */
    @Size(max = 200, message = "Nama peralatan maksimal 200 karakter")
    private String equipmentName;

    /**
     * Equipment model
     */
    @Size(max = 200, message = "Model peralatan maksimal 200 karakter")
    private String equipmentModel;

    /**
     * Manufacturer
     */
    @Size(max = 200, message = "Produsen maksimal 200 karakter")
    private String manufacturer;

    /**
     * Installation date
     */
    private LocalDate installationDate;

    // ========== Calibration ==========

    /**
     * Last calibration date
     */
    private LocalDate lastCalibrationDate;

    /**
     * Next calibration date
     */
    private LocalDate nextCalibrationDate;

    // ========== Capacity ==========

    /**
     * Maximum bookings per day
     */
    private Integer maxBookingsPerDay;

    /**
     * Notes
     */
    private String notes;

    /**
     * Operational status
     */
    @Builder.Default
    private Boolean isOperational = true;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean isActive = true;
}
