package com.yudha.hms.radiology.controller;

import com.yudha.hms.radiology.dto.request.RadiologyRoomRequest;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.PageResponse;
import com.yudha.hms.radiology.dto.response.RadiologyRoomResponse;
import com.yudha.hms.radiology.entity.RadiologyModality;
import com.yudha.hms.radiology.entity.RadiologyRoom;
import com.yudha.hms.radiology.service.RadiologyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Radiology Room Controller.
 *
 * REST controller for managing radiology rooms and imaging equipment.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/rooms")
@RequiredArgsConstructor
@Slf4j
public class RadiologyRoomController {

    private final RadiologyRoomService roomService;

    /**
     * Create new radiology room
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RadiologyRoomResponse>> createRoom(
            @Valid @RequestBody RadiologyRoomRequest request) {
        log.info("Creating radiology room: {}", request.getRoomName());

        RadiologyRoom room = convertToEntity(request);
        RadiologyRoom savedRoom = roomService.createRoom(room);
        RadiologyRoomResponse response = toResponse(savedRoom);

        log.info("Radiology room created successfully: {}", savedRoom.getRoomCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", response));
    }

    /**
     * Update existing radiology room
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyRoomResponse>> updateRoom(
            @PathVariable UUID id,
            @Valid @RequestBody RadiologyRoomRequest request) {
        log.info("Updating radiology room ID: {}", id);

        RadiologyRoom roomUpdate = convertToEntity(request);
        RadiologyRoom room = roomService.updateRoom(id, roomUpdate);
        RadiologyRoomResponse response = toResponse(room);

        log.info("Radiology room updated successfully: {}", room.getRoomCode());

        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", response));
    }

    /**
     * Get radiology room by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyRoomResponse>> getRoomById(
            @PathVariable UUID id) {
        log.info("Fetching radiology room ID: {}", id);

        RadiologyRoom room = roomService.getRoomById(id);
        RadiologyRoomResponse response = toResponse(room);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search radiology rooms
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RadiologyRoomResponse>>> searchRooms(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "roomName") Pageable pageable) {
        log.info("Searching radiology rooms - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<RadiologyRoom> rooms = roomService.searchRooms(search, pageable);
        Page<RadiologyRoomResponse> responsePage = rooms.map(this::toResponse);
        PageResponse<RadiologyRoomResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get room by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<RadiologyRoomResponse>> getRoomByCode(
            @PathVariable String code) {
        log.info("Fetching radiology room by code: {}", code);

        RadiologyRoom room = roomService.getRoomByCode(code);
        RadiologyRoomResponse response = toResponse(room);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get rooms by modality
     */
    @GetMapping("/modality/{modalityId}")
    public ResponseEntity<ApiResponse<List<RadiologyRoomResponse>>> getRoomsByModality(
            @PathVariable UUID modalityId) {
        log.info("Fetching rooms for modality ID: {}", modalityId);

        List<RadiologyRoom> rooms = roomService.getRoomsByModality(modalityId);
        List<RadiologyRoomResponse> responses = rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get operational rooms
     */
    @GetMapping("/operational")
    public ResponseEntity<ApiResponse<List<RadiologyRoomResponse>>> getOperationalRooms() {
        log.info("Fetching operational radiology rooms");

        List<RadiologyRoom> rooms = roomService.getAllOperationalRooms();
        List<RadiologyRoomResponse> responses = rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get available rooms
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RadiologyRoomResponse>>> getAvailableRooms() {
        log.info("Fetching available radiology rooms");

        List<RadiologyRoom> rooms = roomService.getAvailableRooms();
        List<RadiologyRoomResponse> responses = rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get rooms requiring calibration
     */
    @GetMapping("/calibration-due")
    public ResponseEntity<ApiResponse<List<RadiologyRoomResponse>>> getRoomsRequiringCalibration() {
        log.info("Fetching rooms requiring calibration");

        List<RadiologyRoom> rooms = roomService.getRoomsRequiringCalibration();
        List<RadiologyRoomResponse> responses = rooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Check room availability for a date
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkRoomAvailability(
            @PathVariable UUID id,
            @RequestParam LocalDate date) {
        log.info("Checking availability for room ID: {} on date: {}", id, date);

        boolean available = roomService.checkRoomAvailability(id, date);

        return ResponseEntity.ok(ApiResponse.success(
                available ? "Room is available" : "Room is not available",
                available));
    }

    /**
     * Mark room as operational
     */
    @PatchMapping("/{id}/operational")
    public ResponseEntity<ApiResponse<RadiologyRoomResponse>> markRoomAsOperational(
            @PathVariable UUID id) {
        log.info("Marking room as operational ID: {}", id);

        RadiologyRoom room = roomService.markRoomAsOperational(id);
        RadiologyRoomResponse response = toResponse(room);

        log.info("Room marked as operational successfully: {}", room.getRoomCode());

        return ResponseEntity.ok(ApiResponse.success("Room marked as operational successfully", response));
    }

    /**
     * Mark room as non-operational
     */
    @PatchMapping("/{id}/non-operational")
    public ResponseEntity<ApiResponse<RadiologyRoomResponse>> markRoomAsNonOperational(
            @PathVariable UUID id,
            @RequestParam String reason) {
        log.info("Marking room as non-operational ID: {}", id);

        RadiologyRoom room = roomService.markRoomAsNonOperational(id, reason);
        RadiologyRoomResponse response = toResponse(room);

        log.info("Room marked as non-operational successfully: {}", room.getRoomCode());

        return ResponseEntity.ok(ApiResponse.success("Room marked as non-operational successfully", response));
    }

    /**
     * Soft delete radiology room
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable UUID id) {
        log.info("Deleting radiology room ID: {}", id);

        roomService.deleteRoom(id, "SYSTEM");
        log.info("Radiology room deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private RadiologyRoomResponse toResponse(RadiologyRoom room) {
        RadiologyRoomResponse response = new RadiologyRoomResponse();
        response.setId(room.getId());
        response.setRoomCode(room.getRoomCode());
        response.setRoomName(room.getRoomName());
        response.setLocation(room.getLocation());
        response.setFloor(room.getFloor());

        // Modality information
        if (room.getModality() != null) {
            response.setModalityId(room.getModality().getId());
            response.setModalityCode(room.getModality().getCode());
            response.setModalityName(room.getModality().getName());
        }

        // Equipment information
        response.setEquipmentName(room.getEquipmentName());
        response.setEquipmentModel(room.getEquipmentModel());
        response.setManufacturer(room.getManufacturer());
        response.setInstallationDate(room.getInstallationDate());

        // Calibration
        response.setLastCalibrationDate(room.getLastCalibrationDate());
        response.setNextCalibrationDate(room.getNextCalibrationDate());

        // Calculate calibration status
        if (room.getNextCalibrationDate() != null) {
            LocalDate today = LocalDate.now();
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, room.getNextCalibrationDate());
            response.setDaysUntilCalibration(daysUntil);
            response.setCalibrationOverdue(daysUntil < 0);
        }

        // Status
        response.setIsOperational(room.getIsOperational());
        response.setIsAvailable(room.getIsAvailable());

        // Capacity
        response.setMaxBookingsPerDay(room.getMaxBookingsPerDay());

        // Notes
        response.setNotes(room.getNotes());

        // Audit fields
        response.setCreatedAt(room.getCreatedAt());
        response.setCreatedBy(room.getCreatedBy());
        response.setUpdatedAt(room.getUpdatedAt());
        response.setUpdatedBy(room.getUpdatedBy());

        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private RadiologyRoom convertToEntity(RadiologyRoomRequest request) {
        RadiologyRoom room = new RadiologyRoom();
        room.setRoomCode(request.getRoomCode());
        room.setRoomName(request.getRoomName());
        room.setLocation(request.getLocation());
        room.setFloor(request.getFloor());
        room.setEquipmentName(request.getEquipmentName());
        room.setEquipmentModel(request.getEquipmentModel());
        room.setManufacturer(request.getManufacturer());
        room.setInstallationDate(request.getInstallationDate());
        room.setLastCalibrationDate(request.getLastCalibrationDate());
        room.setNextCalibrationDate(request.getNextCalibrationDate());
        room.setIsOperational(request.getIsOperational());
        room.setIsAvailable(request.getIsActive());
        room.setMaxBookingsPerDay(request.getMaxBookingsPerDay());
        room.setNotes(request.getNotes());

        // Set modality - need to create a minimal modality object with just the ID
        if (request.getModalityId() != null) {
            RadiologyModality modality = new RadiologyModality();
            modality.setId(request.getModalityId());
            room.setModality(modality);
        }

        return room;
    }
}
