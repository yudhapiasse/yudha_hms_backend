package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.QueueDisplayResponse;
import com.yudha.hms.clinical.dto.QueueItemResponse;
import com.yudha.hms.clinical.service.QueueIntegrationService;
import com.yudha.hms.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Queue Management Controller.
 *
 * REST API endpoints for queue display boards, kiosk integration,
 * and real-time queue monitoring.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/queue")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class QueueManagementController {

    private final QueueIntegrationService queueIntegrationService;

    /**
     * Get queue display data for a department/polyclinic.
     *
     * For display boards, kiosks, and monitoring dashboards.
     *
     * GET /api/clinical/queue/display/{polyclinicId}
     *
     * @param polyclinicId Department/Polyclinic ID
     * @return Queue display data with statistics
     */
    @GetMapping("/display/{polyclinicId}")
    public ResponseEntity<ApiResponse<QueueDisplayResponse>> getQueueDisplay(
        @PathVariable UUID polyclinicId
    ) {
        log.info("REST: Getting queue display for polyclinic: {}", polyclinicId);

        QueueDisplayResponse display = queueIntegrationService.getQueueDisplay(polyclinicId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Queue display for polyclinic with %d waiting patients", display.getTotalWaiting()),
            display
        ));
    }

    /**
     * Get next patient alert for a specific doctor.
     *
     * Used by doctor dashboards to show next patient to call.
     *
     * GET /api/clinical/queue/next-patient
     *
     * @param polyclinicId Polyclinic ID
     * @param doctorId Doctor ID
     * @return Next patient in queue, or null if none
     */
    @GetMapping("/next-patient")
    public ResponseEntity<ApiResponse<QueueItemResponse>> getNextPatientAlert(
        @RequestParam UUID polyclinicId,
        @RequestParam UUID doctorId
    ) {
        log.info("REST: Getting next patient for doctor: {} in polyclinic: {}", doctorId, polyclinicId);

        QueueItemResponse nextPatient = queueIntegrationService.getNextPatientAlert(polyclinicId, doctorId);

        if (nextPatient == null) {
            return ResponseEntity.ok(ApiResponse.success(
                "No patients waiting",
                null
            ));
        }

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Next patient: %s (Queue: %s)", nextPatient.getPatientName(), nextPatient.getQueueCode()),
            nextPatient
        ));
    }

    /**
     * Get queue statistics summary for a department.
     *
     * Quick stats endpoint for dashboards.
     *
     * GET /api/clinical/queue/stats/{polyclinicId}
     *
     * @param polyclinicId Polyclinic ID
     * @return Queue statistics
     */
    @GetMapping("/stats/{polyclinicId}")
    public ResponseEntity<ApiResponse<QueueStatisticsResponse>> getQueueStatistics(
        @PathVariable UUID polyclinicId
    ) {
        log.info("REST: Getting queue statistics for polyclinic: {}", polyclinicId);

        QueueDisplayResponse display = queueIntegrationService.getQueueDisplay(polyclinicId);

        QueueStatisticsResponse stats = QueueStatisticsResponse.builder()
            .polyclinicId(polyclinicId)
            .totalWaiting(display.getTotalWaiting())
            .totalServing(display.getTotalServing())
            .totalCompleted(display.getTotalCompleted())
            .averageWaitingTimeMinutes(display.getAverageWaitingTimeMinutes())
            .build();

        return ResponseEntity.ok(ApiResponse.success(
            "Queue statistics retrieved",
            stats
        ));
    }

    /**
     * Inner class for queue statistics response.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class QueueStatisticsResponse {
        private UUID polyclinicId;
        private Integer totalWaiting;
        private Integer totalServing;
        private Integer totalCompleted;
        private Double averageWaitingTimeMinutes;
    }
}
