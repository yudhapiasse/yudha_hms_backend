package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Queue Display Response DTO.
 *
 * For display boards, kiosks, and doctor workload views.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueDisplayResponse {

    // Department/Polyclinic Info
    private UUID polyclinicId;
    private String polyclinicName;
    private LocalDateTime displayTime;

    // Queue Statistics
    private Integer totalWaiting;
    private Integer totalCalled;
    private Integer totalServing;
    private Integer totalCompleted;
    private Integer totalSkipped;

    // Average Waiting Time (minutes)
    private Double averageWaitingTimeMinutes;

    // Current Status
    private String currentlyServingQueueCode;
    private String currentlyServingPatientName;
    private String nextQueueCode;
    private String nextPatientName;

    // Queue Lists
    @Builder.Default
    private List<QueueItemResponse> waitingQueue = new ArrayList<>();

    @Builder.Default
    private List<QueueItemResponse> servingQueue = new ArrayList<>();

    // Doctor Workload
    @Builder.Default
    private List<DoctorWorkloadResponse> doctorWorkloads = new ArrayList<>();
}
