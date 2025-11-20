package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Doctor Workload Response DTO.
 *
 * Shows doctor's current workload in queue.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorWorkloadResponse {

    private UUID doctorId;
    private String doctorName;
    private String specialty;
    private Integer waitingPatients;
    private Integer servingPatients;
    private Integer completedToday;
    private Double averageServiceTimeMinutes;
    private String status; // AVAILABLE, BUSY, BREAK, OFF_DUTY
}
