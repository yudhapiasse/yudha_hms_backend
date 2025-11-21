package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Radiology Order Schedule Request DTO.
 *
 * Used for scheduling or rescheduling radiology orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyOrderScheduleRequest {

    /**
     * Scheduled date
     */
    @NotNull(message = "Tanggal jadwal harus diisi")
    private LocalDate scheduledDate;

    /**
     * Scheduled time
     */
    @NotNull(message = "Waktu jadwal harus diisi")
    private LocalTime scheduledTime;

    /**
     * Room ID
     */
    @NotNull(message = "Ruangan harus dipilih")
    private UUID roomId;

    /**
     * Technician ID
     */
    @NotNull(message = "Teknisi harus dipilih")
    private UUID technicianId;

    /**
     * Notes
     */
    private String notes;
}
