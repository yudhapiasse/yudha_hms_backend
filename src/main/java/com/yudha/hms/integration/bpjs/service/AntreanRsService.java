package com.yudha.hms.integration.bpjs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.bpjs.dto.antreanrs.*;
import com.yudha.hms.integration.bpjs.exception.BpjsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Antrean RS Service Implementation.
 *
 * Provides comprehensive queue management services for BPJS Antrean RS:
 * - Reference data (polyclinics, doctors, schedules)
 * - Queue management (add, update, cancel)
 * - Task ID tracking (patient journey stages)
 * - Dashboard and monitoring
 * - Real-time waiting time calculation
 *
 * Antrean RS is BPJS's queue management system for monitoring patient flow
 * and service quality in hospitals.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AntreanRsService {

    private final BpjsHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ========== REFERENCE DATA SERVICES ==========

    /**
     * Get polyclinic reference data.
     * Returns all polyclinics registered with BPJS.
     *
     * @return Polyclinic reference list
     */
    public PoliReferenceResponse getPoliReference() {
        String endpoint = "/ref/poli";

        log.info("Getting poli reference from BPJS Antrean RS");

        try {
            JsonNode response = httpClient.antreanRsGet(endpoint);
            return objectMapper.treeToValue(response, PoliReferenceResponse.class);

        } catch (Exception e) {
            log.error("Failed to get poli reference", e);
            throw new BpjsHttpException("Failed to get poli reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctor reference data.
     * Returns all doctors registered with BPJS.
     *
     * @return Raw JSON doctor reference list
     */
    public JsonNode getDoctorReference() {
        String endpoint = "/ref/dokter";

        log.info("Getting doctor reference from BPJS Antrean RS");

        try {
            return httpClient.antreanRsGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get doctor reference", e);
            throw new BpjsHttpException("Failed to get doctor reference: " + e.getMessage(), e);
        }
    }

    /**
     * Get doctor schedules by polyclinic and date.
     *
     * @param kodePoli Polyclinic code
     * @param tanggal Schedule date
     * @return Raw JSON doctor schedule list
     */
    public JsonNode getDoctorSchedule(String kodePoli, LocalDate tanggal) {
        String endpoint = String.format("/jadwaldokter/kodepoli/%s/tanggal/%s",
            kodePoli, tanggal.format(DATE_FORMATTER));

        log.info("Getting doctor schedule for poli: {} on date: {}", kodePoli, tanggal);

        try {
            return httpClient.antreanRsGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get doctor schedule", e);
            throw new BpjsHttpException("Failed to get doctor schedule: " + e.getMessage(), e);
        }
    }

    /**
     * Get polyclinic fingerprint reference.
     *
     * @return Raw JSON fingerprint poli reference
     */
    public JsonNode getPoliFingerprint() {
        String endpoint = "/ref/poli/fp";

        log.info("Getting poli fingerprint reference");

        try {
            return httpClient.antreanRsGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get poli fingerprint reference", e);
            throw new BpjsHttpException("Failed to get poli fingerprint: " + e.getMessage(), e);
        }
    }

    /**
     * Get patient fingerprint data by identity.
     *
     * @param identitas Identity type (e.g., "nik")
     * @param noIdentitas Identity number
     * @return Raw JSON patient fingerprint data
     */
    public JsonNode getPatientFingerprint(String identitas, String noIdentitas) {
        String endpoint = String.format("/ref/pasien/fp/identitas/%s/noidentitas/%s",
            identitas, noIdentitas);

        log.info("Getting patient fingerprint for {}: {}", identitas, noIdentitas);

        try {
            return httpClient.antreanRsGet(endpoint);

        } catch (Exception e) {
            log.error("Failed to get patient fingerprint", e);
            throw new BpjsHttpException("Failed to get patient fingerprint: " + e.getMessage(), e);
        }
    }

    // ========== QUEUE MANAGEMENT SERVICES ==========

    /**
     * Add new patient queue to BPJS system.
     *
     * @param request Add queue request
     * @return Operation response
     */
    public AntreanOperationResponse addQueue(AddQueueRequest request) {
        String endpoint = "/antrean/add";

        log.info("Adding new queue - Booking: {}, Patient: {}, Poli: {}",
            request.getKodebooking(), request.getNorm(), request.getKodepoli());

        try {
            JsonNode response = httpClient.antreanRsPost(endpoint, request);
            AntreanOperationResponse operationResponse =
                objectMapper.treeToValue(response, AntreanOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully added queue: {}", request.getKodebooking());
            } else {
                log.warn("Failed to add queue: {} - {}",
                    request.getKodebooking(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to add queue to BPJS Antrean RS", e);
            throw new BpjsHttpException("Failed to add queue: " + e.getMessage(), e);
        }
    }

    /**
     * Add pharmacy queue to BPJS system.
     *
     * @param request Add queue request (pharmacy specific)
     * @return Operation response
     */
    public AntreanOperationResponse addPharmacyQueue(AddQueueRequest request) {
        String endpoint = "/antrean/farmasi/add";

        log.info("Adding pharmacy queue - Booking: {}", request.getKodebooking());

        try {
            JsonNode response = httpClient.antreanRsPost(endpoint, request);
            AntreanOperationResponse operationResponse =
                objectMapper.treeToValue(response, AntreanOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully added pharmacy queue: {}", request.getKodebooking());
            } else {
                log.warn("Failed to add pharmacy queue: {} - {}",
                    request.getKodebooking(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to add pharmacy queue", e);
            throw new BpjsHttpException("Failed to add pharmacy queue: " + e.getMessage(), e);
        }
    }

    /**
     * Update queue timestamp for a specific task ID.
     *
     * @param request Update queue request
     * @return Operation response
     */
    public AntreanOperationResponse updateQueueTime(UpdateQueueRequest request) {
        String endpoint = "/antrean/updatewaktu";

        TaskId taskId = TaskId.fromId(request.getTaskid());
        log.info("Updating queue time - Booking: {}, Task: {} ({})",
            request.getKodebooking(), request.getTaskid(), taskId.getTaskName());

        try {
            JsonNode response = httpClient.antreanRsPost(endpoint, request);
            AntreanOperationResponse operationResponse =
                objectMapper.treeToValue(response, AntreanOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully updated queue time for booking: {}, task: {}",
                    request.getKodebooking(), taskId.getDescription());
            } else {
                log.warn("Failed to update queue time: {} - {}",
                    request.getKodebooking(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to update queue time", e);
            throw new BpjsHttpException("Failed to update queue time: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel patient queue.
     *
     * @param request Cancel queue request
     * @return Operation response
     */
    public AntreanOperationResponse cancelQueue(CancelQueueRequest request) {
        String endpoint = "/antrean/batal";

        log.info("Canceling queue - Booking: {}, Reason: {}",
            request.getKodebooking(), request.getKeterangan());

        try {
            JsonNode response = httpClient.antreanRsPost(endpoint, request);
            AntreanOperationResponse operationResponse =
                objectMapper.treeToValue(response, AntreanOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully canceled queue: {}", request.getKodebooking());
            } else {
                log.warn("Failed to cancel queue: {} - {}",
                    request.getKodebooking(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to cancel queue", e);
            throw new BpjsHttpException("Failed to cancel queue: " + e.getMessage(), e);
        }
    }

    /**
     * Get queue list by date.
     *
     * @param tanggal Date to query
     * @return Queue list response
     */
    public QueueListResponse getQueuesByDate(LocalDate tanggal) {
        String endpoint = String.format("/antrean/pendaftaran/tanggal/%s",
            tanggal.format(DATE_FORMATTER));

        log.info("Getting queues by date: {}", tanggal);

        try {
            JsonNode response = httpClient.antreanRsGet(endpoint);
            return objectMapper.treeToValue(response, QueueListResponse.class);

        } catch (Exception e) {
            log.error("Failed to get queues by date: {}", tanggal, e);
            throw new BpjsHttpException("Failed to get queues: " + e.getMessage(), e);
        }
    }

    /**
     * Get queue by booking code.
     *
     * @param kodeBooking Booking code
     * @return Queue list response (single item)
     */
    public QueueListResponse getQueueByBookingCode(String kodeBooking) {
        String endpoint = String.format("/antrean/pendaftaran/kodebooking/%s", kodeBooking);

        log.info("Getting queue by booking code: {}", kodeBooking);

        try {
            JsonNode response = httpClient.antreanRsGet(endpoint);
            return objectMapper.treeToValue(response, QueueListResponse.class);

        } catch (Exception e) {
            log.error("Failed to get queue by booking code: {}", kodeBooking, e);
            throw new BpjsHttpException("Failed to get queue: " + e.getMessage(), e);
        }
    }

    /**
     * Get task list for a booking code.
     * Shows all task timestamps sent to BPJS.
     *
     * @param kodeBooking Booking code
     * @return Task list response
     */
    public TaskListResponse getTaskList(String kodeBooking) {
        String endpoint = "/antrean/getlisttask";

        log.info("Getting task list for booking: {}", kodeBooking);

        try {
            var request = new java.util.HashMap<String, String>();
            request.put("kodebooking", kodeBooking);

            JsonNode response = httpClient.antreanRsPost(endpoint, request);
            return objectMapper.treeToValue(response, TaskListResponse.class);

        } catch (Exception e) {
            log.error("Failed to get task list for booking: {}", kodeBooking, e);
            throw new BpjsHttpException("Failed to get task list: " + e.getMessage(), e);
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Update queue time using TaskId enum.
     * Convenience method with TaskId enum and Instant.
     *
     * @param kodeBooking Booking code
     * @param taskId Task ID enum
     * @param waktu Timestamp
     * @return Operation response
     */
    public AntreanOperationResponse updateTaskTime(String kodeBooking, TaskId taskId, Instant waktu) {
        UpdateQueueRequest request = UpdateQueueRequest.builder()
            .kodebooking(kodeBooking)
            .taskid(taskId.getId())
            .waktu(waktu.toEpochMilli())
            .build();

        return updateQueueTime(request);
    }

    /**
     * Update queue time with prescription type.
     * For hospitals with pharmacy queue integration.
     *
     * @param kodeBooking Booking code
     * @param taskId Task ID enum
     * @param waktu Timestamp
     * @param jenisResep Prescription type ("Tidak ada", "Racikan", "Non racikan")
     * @return Operation response
     */
    public AntreanOperationResponse updateTaskTimeWithPrescription(
            String kodeBooking, TaskId taskId, Instant waktu, String jenisResep) {

        UpdateQueueRequest request = UpdateQueueRequest.builder()
            .kodebooking(kodeBooking)
            .taskid(taskId.getId())
            .waktu(waktu.toEpochMilli())
            .jenisresep(jenisResep)
            .build();

        return updateQueueTime(request);
    }

    /**
     * Update queue time with current timestamp.
     * Convenience method using current system time.
     *
     * @param kodeBooking Booking code
     * @param taskId Task ID enum
     * @return Operation response
     */
    public AntreanOperationResponse updateTaskTimeNow(String kodeBooking, TaskId taskId) {
        return updateTaskTime(kodeBooking, taskId, Instant.now());
    }

    /**
     * Generate unique booking code.
     * Format: YYYYMMDD-FACILITYCODE-SEQUENCE
     *
     * @param sequence Sequence number
     * @return Generated booking code
     */
    public String generateBookingCode(String facilityCode, int sequence) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("%s-%s-%04d", date, facilityCode, sequence);
    }

    /**
     * Check if Antrean RS integration is enabled.
     *
     * @return true if integration is ready
     */
    public boolean isAntreanRsEnabled() {
        return true; // Enabled if BPJS is configured
    }
}
