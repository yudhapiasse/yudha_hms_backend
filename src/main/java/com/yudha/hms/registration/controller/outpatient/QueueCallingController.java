package com.yudha.hms.registration.controller.outpatient;

import com.yudha.hms.registration.dto.outpatient.OutpatientRegistrationResponse;
import com.yudha.hms.registration.entity.OutpatientRegistration;
import com.yudha.hms.registration.entity.QueueCallHistory;
import com.yudha.hms.registration.service.outpatient.QueueCallingService;
import com.yudha.hms.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Queue Calling Controller.
 * REST API endpoints for queue calling and management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/registration/queue")
@RequiredArgsConstructor
@Slf4j
public class QueueCallingController {

    private final QueueCallingService queueCallingService;

    /**
     * POST /api/registration/queue/polyclinics/{polyclinicId}/call-next
     * Call next patient from queue.
     */
    @PostMapping("/polyclinics/{polyclinicId}/call-next")
    public ResponseEntity<ApiResponse<OutpatientRegistration>> callNextPatient(
        @PathVariable UUID polyclinicId,
        @RequestParam String calledBy,
        @RequestParam(required = false) String consultationRoom
    ) {
        log.info("POST /api/registration/queue/polyclinics/{}/call-next", polyclinicId);

        OutpatientRegistration registration = queueCallingService.callNextPatient(
            polyclinicId,
            calledBy,
            consultationRoom
        );

        return ResponseEntity.ok(ApiResponse.<OutpatientRegistration>builder()
            .success(true)
            .message("Pasien berhasil dipanggil: " + registration.getQueueCode())
            .data(registration)
            .build());
    }

    /**
     * POST /api/registration/queue/polyclinics/{polyclinicId}/call-specific
     * Call specific patient by queue number.
     */
    @PostMapping("/polyclinics/{polyclinicId}/call-specific")
    public ResponseEntity<ApiResponse<OutpatientRegistration>> callSpecificPatient(
        @PathVariable UUID polyclinicId,
        @RequestParam Integer queueNumber,
        @RequestParam String calledBy,
        @RequestParam(required = false) String consultationRoom
    ) {
        log.info("POST /api/registration/queue/polyclinics/{}/call-specific - Queue: {}",
            polyclinicId, queueNumber);

        OutpatientRegistration registration = queueCallingService.callSpecificPatient(
            polyclinicId,
            queueNumber,
            calledBy,
            consultationRoom
        );

        return ResponseEntity.ok(ApiResponse.<OutpatientRegistration>builder()
            .success(true)
            .message("Pasien berhasil dipanggil: " + registration.getQueueCode())
            .data(registration)
            .build());
    }

    /**
     * POST /api/registration/queue/{registrationId}/recall
     * Recall patient (call again).
     */
    @PostMapping("/{registrationId}/recall")
    public ResponseEntity<ApiResponse<OutpatientRegistration>> recallPatient(
        @PathVariable UUID registrationId,
        @RequestParam String calledBy,
        @RequestParam(required = false) String consultationRoom
    ) {
        log.info("POST /api/registration/queue/{}/recall", registrationId);

        OutpatientRegistration registration = queueCallingService.recallPatient(
            registrationId,
            calledBy,
            consultationRoom
        );

        return ResponseEntity.ok(ApiResponse.<OutpatientRegistration>builder()
            .success(true)
            .message("Pasien berhasil dipanggil ulang: " + registration.getQueueCode())
            .data(registration)
            .build());
    }

    /**
     * POST /api/registration/queue/{registrationId}/start-serving
     * Start serving patient (patient responded to call).
     */
    @PostMapping("/{registrationId}/start-serving")
    public ResponseEntity<ApiResponse<OutpatientRegistration>> startServing(
        @PathVariable UUID registrationId
    ) {
        log.info("POST /api/registration/queue/{}/start-serving", registrationId);

        OutpatientRegistration registration = queueCallingService.startServing(registrationId);

        return ResponseEntity.ok(ApiResponse.<OutpatientRegistration>builder()
            .success(true)
            .message("Pelayanan pasien dimulai: " + registration.getQueueCode())
            .data(registration)
            .build());
    }

    /**
     * POST /api/registration/queue/{registrationId}/complete
     * Complete queue service.
     */
    @PostMapping("/{registrationId}/complete")
    public ResponseEntity<ApiResponse<OutpatientRegistration>> completeQueue(
        @PathVariable UUID registrationId
    ) {
        log.info("POST /api/registration/queue/{}/complete", registrationId);

        OutpatientRegistration registration = queueCallingService.completeQueue(registrationId);

        return ResponseEntity.ok(ApiResponse.<OutpatientRegistration>builder()
            .success(true)
            .message("Pelayanan pasien selesai: " + registration.getQueueCode())
            .data(registration)
            .build());
    }

    /**
     * POST /api/registration/queue/{registrationId}/skip
     * Skip patient (not present when called).
     */
    @PostMapping("/{registrationId}/skip")
    public ResponseEntity<ApiResponse<OutpatientRegistration>> skipPatient(
        @PathVariable UUID registrationId,
        @RequestParam String reason
    ) {
        log.info("POST /api/registration/queue/{}/skip - Reason: {}", registrationId, reason);

        OutpatientRegistration registration = queueCallingService.skipPatient(registrationId, reason);

        return ResponseEntity.ok(ApiResponse.<OutpatientRegistration>builder()
            .success(true)
            .message("Pasien dilewati: " + registration.getQueueCode())
            .data(registration)
            .build());
    }

    /**
     * GET /api/registration/queue/polyclinics/{polyclinicId}/status
     * Get current queue status for a polyclinic.
     */
    @GetMapping("/polyclinics/{polyclinicId}/status")
    public ResponseEntity<ApiResponse<List<OutpatientRegistration>>> getCurrentQueueStatus(
        @PathVariable UUID polyclinicId
    ) {
        log.info("GET /api/registration/queue/polyclinics/{}/status", polyclinicId);

        List<OutpatientRegistration> registrations = queueCallingService.getCurrentQueueStatus(polyclinicId);

        return ResponseEntity.ok(ApiResponse.<List<OutpatientRegistration>>builder()
            .success(true)
            .message("Status antrian berhasil diambil")
            .data(registrations)
            .build());
    }

    /**
     * GET /api/registration/queue/polyclinics/{polyclinicId}/waiting
     * Get waiting patients.
     */
    @GetMapping("/polyclinics/{polyclinicId}/waiting")
    public ResponseEntity<ApiResponse<List<OutpatientRegistration>>> getWaitingPatients(
        @PathVariable UUID polyclinicId
    ) {
        log.info("GET /api/registration/queue/polyclinics/{}/waiting", polyclinicId);

        List<OutpatientRegistration> registrations = queueCallingService.getWaitingPatients(polyclinicId);

        return ResponseEntity.ok(ApiResponse.<List<OutpatientRegistration>>builder()
            .success(true)
            .message("Daftar pasien menunggu berhasil diambil")
            .data(registrations)
            .build());
    }

    /**
     * GET /api/registration/queue/polyclinics/{polyclinicId}/serving
     * Get currently serving patients.
     */
    @GetMapping("/polyclinics/{polyclinicId}/serving")
    public ResponseEntity<ApiResponse<List<OutpatientRegistration>>> getServingPatients(
        @PathVariable UUID polyclinicId
    ) {
        log.info("GET /api/registration/queue/polyclinics/{}/serving", polyclinicId);

        List<OutpatientRegistration> registrations = queueCallingService.getServingPatients(polyclinicId);

        return ResponseEntity.ok(ApiResponse.<List<OutpatientRegistration>>builder()
            .success(true)
            .message("Daftar pasien sedang dilayani berhasil diambil")
            .data(registrations)
            .build());
    }

    /**
     * GET /api/registration/queue/polyclinics/{polyclinicId}/skipped
     * Get skipped patients.
     */
    @GetMapping("/polyclinics/{polyclinicId}/skipped")
    public ResponseEntity<ApiResponse<List<OutpatientRegistration>>> getSkippedPatients(
        @PathVariable UUID polyclinicId
    ) {
        log.info("GET /api/registration/queue/polyclinics/{}/skipped", polyclinicId);

        List<OutpatientRegistration> registrations = queueCallingService.getSkippedPatients(polyclinicId);

        return ResponseEntity.ok(ApiResponse.<List<OutpatientRegistration>>builder()
            .success(true)
            .message("Daftar pasien yang dilewati berhasil diambil")
            .data(registrations)
            .build());
    }

    /**
     * GET /api/registration/queue/{registrationId}/call-history
     * Get call history for a registration.
     */
    @GetMapping("/{registrationId}/call-history")
    public ResponseEntity<ApiResponse<List<QueueCallHistory>>> getCallHistory(
        @PathVariable UUID registrationId
    ) {
        log.info("GET /api/registration/queue/{}/call-history", registrationId);

        List<QueueCallHistory> history = queueCallingService.getCallHistory(registrationId);

        return ResponseEntity.ok(ApiResponse.<List<QueueCallHistory>>builder()
            .success(true)
            .message("Riwayat panggilan berhasil diambil")
            .data(history)
            .build());
    }

    /**
     * GET /api/registration/queue/polyclinics/{polyclinicId}/statistics
     * Get call statistics for a polyclinic today.
     */
    @GetMapping("/polyclinics/{polyclinicId}/statistics")
    public ResponseEntity<ApiResponse<QueueCallingService.QueueCallStatistics>> getCallStatistics(
        @PathVariable UUID polyclinicId
    ) {
        log.info("GET /api/registration/queue/polyclinics/{}/statistics", polyclinicId);

        QueueCallingService.QueueCallStatistics stats = queueCallingService.getCallStatistics(polyclinicId);

        return ResponseEntity.ok(ApiResponse.<QueueCallingService.QueueCallStatistics>builder()
            .success(true)
            .message("Statistik panggilan berhasil diambil")
            .data(stats)
            .build());
    }
}
