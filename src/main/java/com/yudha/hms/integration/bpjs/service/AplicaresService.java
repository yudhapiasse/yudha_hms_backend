package com.yudha.hms.integration.bpjs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.bpjs.config.BpjsConfig;
import com.yudha.hms.integration.bpjs.dto.aplicares.*;
import com.yudha.hms.integration.bpjs.exception.BpjsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Aplicares Service Implementation.
 *
 * Provides comprehensive Aplicares services for BPJS quality monitoring and bed availability:
 * - Room class reference data
 * - Bed availability management
 * - Real-time capacity tracking
 * - Room creation and deletion
 *
 * Aplicares (Aplikasi Pelayanan Kesehatan bagi Rumah Sakit) is BPJS's system
 * for monitoring hospital service quality and bed availability.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AplicaresService {

    private final BpjsHttpClient httpClient;
    private final BpjsConfig bpjsConfig;
    private final ObjectMapper objectMapper;

    // ========== REFERENCE DATA SERVICES ==========

    /**
     * Get room class reference data.
     * Returns all BPJS-defined room classes (VIP, VVIP, Kelas 1-3, ICU, etc.).
     *
     * @return Room class reference list
     */
    public RoomClassResponse getRoomClassReference() {
        String endpoint = "/rest/ref/kelas";

        log.info("Getting room class reference from BPJS Aplicares");

        try {
            JsonNode response = httpClient.aplicaresGet(endpoint);
            return objectMapper.treeToValue(response, RoomClassResponse.class);

        } catch (Exception e) {
            log.error("Failed to get room class reference", e);
            throw new BpjsHttpException("Failed to get room class reference: " + e.getMessage(), e);
        }
    }

    // ========== BED AVAILABILITY SERVICES ==========

    /**
     * Get bed availability for the hospital.
     * Retrieves current bed availability across all rooms.
     *
     * @param start Starting record number (1-based)
     * @param limit Number of records to retrieve
     * @return Bed availability response
     */
    public BedAvailabilityResponse getBedAvailability(int start, int limit) {
        String facilityCode = bpjsConfig.getFacilityCode();
        String endpoint = String.format("/rest/bed/read/%s/%d/%d",
            facilityCode, start, limit);

        log.info("Getting bed availability for facility: {} (start: {}, limit: {})",
            facilityCode, start, limit);

        try {
            JsonNode response = httpClient.aplicaresGet(endpoint);
            return objectMapper.treeToValue(response, BedAvailabilityResponse.class);

        } catch (Exception e) {
            log.error("Failed to get bed availability", e);
            throw new BpjsHttpException("Failed to get bed availability: " + e.getMessage(), e);
        }
    }

    /**
     * Get all bed availability (without pagination).
     * Retrieves all bed availability records.
     *
     * @return Complete bed availability response
     */
    public BedAvailabilityResponse getAllBedAvailability() {
        // Use large limit to get all records
        return getBedAvailability(1, 1000);
    }

    /**
     * Create new room in BPJS Aplicares.
     * Registers a new room with bed availability information.
     *
     * @param request Bed availability request
     * @return Operation response
     */
    public AplicaresOperationResponse createRoom(BedAvailabilityRequest request) {
        String facilityCode = bpjsConfig.getFacilityCode();
        String endpoint = String.format("/rest/bed/create/%s", facilityCode);

        log.info("Creating new room in BPJS Aplicares - Room: {} ({}), Class: {}",
            request.getKoderuang(), request.getNamaruang(), request.getKodekelas());

        try {
            JsonNode response = httpClient.aplicaresPost(endpoint, request);
            AplicaresOperationResponse operationResponse =
                objectMapper.treeToValue(response, AplicaresOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully created room: {} in BPJS Aplicares", request.getKoderuang());
            } else {
                log.warn("Failed to create room: {} - {}",
                    request.getKoderuang(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to create room in BPJS Aplicares", e);
            throw new BpjsHttpException("Failed to create room: " + e.getMessage(), e);
        }
    }

    /**
     * Update bed availability in BPJS Aplicares.
     * Updates existing room's bed availability information.
     *
     * @param request Bed availability request
     * @return Operation response
     */
    public AplicaresOperationResponse updateBedAvailability(BedAvailabilityRequest request) {
        String facilityCode = bpjsConfig.getFacilityCode();
        String endpoint = String.format("/rest/bed/update/%s", facilityCode);

        log.info("Updating bed availability in BPJS Aplicares - Room: {}, Available: {}/{}",
            request.getKoderuang(), request.getTersedia(), request.getKapasitas());

        try {
            JsonNode response = httpClient.aplicaresPost(endpoint, request);
            AplicaresOperationResponse operationResponse =
                objectMapper.treeToValue(response, AplicaresOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully updated bed availability for room: {}", request.getKoderuang());
            } else {
                log.warn("Failed to update bed availability for room: {} - {}",
                    request.getKoderuang(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to update bed availability in BPJS Aplicares", e);
            throw new BpjsHttpException("Failed to update bed availability: " + e.getMessage(), e);
        }
    }

    /**
     * Delete room from BPJS Aplicares.
     * Removes a room from the bed availability system.
     *
     * @param request Delete room request
     * @return Operation response
     */
    public AplicaresOperationResponse deleteRoom(DeleteRoomRequest request) {
        String facilityCode = bpjsConfig.getFacilityCode();
        String endpoint = String.format("/rest/bed/delete/%s", facilityCode);

        log.info("Deleting room from BPJS Aplicares - Room: {}, Class: {}",
            request.getKoderuang(), request.getKodekelas());

        try {
            JsonNode response = httpClient.aplicaresPost(endpoint, request);
            AplicaresOperationResponse operationResponse =
                objectMapper.treeToValue(response, AplicaresOperationResponse.class);

            if (operationResponse.isSuccess()) {
                log.info("Successfully deleted room: {} from BPJS Aplicares", request.getKoderuang());
            } else {
                log.warn("Failed to delete room: {} - {}",
                    request.getKoderuang(), operationResponse.getErrorMessage());
            }

            return operationResponse;

        } catch (Exception e) {
            log.error("Failed to delete room from BPJS Aplicares", e);
            throw new BpjsHttpException("Failed to delete room: " + e.getMessage(), e);
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Update bed availability for a specific room.
     * Convenience method with individual parameters.
     *
     * @param roomCode Hospital room code
     * @param roomClass BPJS room class code
     * @param roomName Room name
     * @param totalCapacity Total bed capacity
     * @param available Total available beds
     * @return Operation response
     */
    public AplicaresOperationResponse updateRoomAvailability(
            String roomCode,
            String roomClass,
            String roomName,
            int totalCapacity,
            int available) {

        BedAvailabilityRequest request = BedAvailabilityRequest.builder()
            .koderuang(roomCode)
            .kodekelas(roomClass)
            .namaruang(roomName)
            .kapasitas(String.valueOf(totalCapacity))
            .tersedia(String.valueOf(available))
            .tersediapria("0")
            .tersediawanita("0")
            .tersediapriawanita("0")
            .build();

        return updateBedAvailability(request);
    }

    /**
     * Update bed availability with gender-specific tracking.
     * Convenience method for hospitals that track male/female bed availability.
     *
     * @param roomCode Hospital room code
     * @param roomClass BPJS room class code
     * @param roomName Room name
     * @param totalCapacity Total bed capacity
     * @param availableMale Available beds for male patients
     * @param availableFemale Available beds for female patients
     * @param availableUnisex Available beds for either gender
     * @return Operation response
     */
    public AplicaresOperationResponse updateRoomAvailabilityByGender(
            String roomCode,
            String roomClass,
            String roomName,
            int totalCapacity,
            int availableMale,
            int availableFemale,
            int availableUnisex) {

        int totalAvailable = availableMale + availableFemale + availableUnisex;

        BedAvailabilityRequest request = BedAvailabilityRequest.builder()
            .koderuang(roomCode)
            .kodekelas(roomClass)
            .namaruang(roomName)
            .kapasitas(String.valueOf(totalCapacity))
            .tersedia(String.valueOf(totalAvailable))
            .tersediapria(String.valueOf(availableMale))
            .tersediawanita(String.valueOf(availableFemale))
            .tersediapriawanita(String.valueOf(availableUnisex))
            .build();

        return updateBedAvailability(request);
    }

    /**
     * Check if BPJS Aplicares integration is enabled and configured.
     *
     * @return true if integration is ready
     */
    public boolean isAplicaresEnabled() {
        return bpjsConfig.isEnabled() &&
               bpjsConfig.getFacilityCode() != null &&
               !bpjsConfig.getFacilityCode().isEmpty();
    }
}
