package com.yudha.hms.registration.controller;

import com.yudha.hms.registration.dto.RoomAvailabilityDto;
import com.yudha.hms.registration.entity.RoomClass;
import com.yudha.hms.registration.service.RoomService;
import com.yudha.hms.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for room and bed availability management.
 *
 * Endpoints:
 * - GET /api/rooms/available - Get all available rooms
 * - GET /api/rooms/available/{roomClass} - Get available rooms by class
 * - GET /api/rooms/{id}/availability - Get specific room availability
 * - GET /api/rooms/number/{roomNumber}/availability - Get room by number
 * - GET /api/rooms/occupancy - Get occupancy statistics
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    /**
     * Get all available rooms with bed information.
     *
     * GET /api/rooms/available
     *
     * @return list of available rooms
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RoomAvailabilityDto>>> getAllAvailableRooms() {
        log.info("GET /api/rooms/available - Fetching all available rooms");

        List<RoomAvailabilityDto> rooms = roomService.getAllAvailableRooms();

        return ResponseEntity.ok(
            ApiResponse.success("Available rooms retrieved successfully", rooms)
        );
    }

    /**
     * Get available rooms by room class.
     *
     * GET /api/rooms/available/{roomClass}
     *
     * @param roomClass room class (VIP, KELAS_1, KELAS_2, KELAS_3, ICU, NICU, PICU)
     * @return list of available rooms
     */
    @GetMapping("/available/{roomClass}")
    public ResponseEntity<ApiResponse<List<RoomAvailabilityDto>>> getAvailableRoomsByClass(
            @PathVariable RoomClass roomClass) {
        log.info("GET /api/rooms/available/{} - Fetching available rooms", roomClass);

        List<RoomAvailabilityDto> rooms = roomService.getAvailableRoomsByClass(roomClass);

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Available %s rooms retrieved successfully", roomClass.getDisplayName()),
                rooms
            )
        );
    }

    /**
     * Get room availability by ID.
     *
     * GET /api/rooms/{id}/availability
     *
     * @param id room ID
     * @return room availability information
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<RoomAvailabilityDto>> getRoomAvailability(@PathVariable UUID id) {
        log.info("GET /api/rooms/{}/availability - Fetching room availability", id);

        RoomAvailabilityDto room = roomService.getRoomAvailability(id);

        return ResponseEntity.ok(
            ApiResponse.success("Room availability retrieved successfully", room)
        );
    }

    /**
     * Get room availability by room number.
     *
     * GET /api/rooms/number/{roomNumber}/availability
     *
     * @param roomNumber room number
     * @return room availability information
     */
    @GetMapping("/number/{roomNumber}/availability")
    public ResponseEntity<ApiResponse<RoomAvailabilityDto>> getRoomAvailabilityByNumber(
            @PathVariable String roomNumber) {
        log.info("GET /api/rooms/number/{}/availability - Fetching room availability", roomNumber);

        RoomAvailabilityDto room = roomService.getRoomAvailabilityByNumber(roomNumber);

        return ResponseEntity.ok(
            ApiResponse.success("Room availability retrieved successfully", room)
        );
    }

    /**
     * Get room occupancy statistics.
     *
     * GET /api/rooms/occupancy
     *
     * @return occupancy statistics
     */
    @GetMapping("/occupancy")
    public ResponseEntity<ApiResponse<RoomService.RoomOccupancyStats>> getOccupancyStatistics() {
        log.info("GET /api/rooms/occupancy - Fetching occupancy statistics");

        RoomService.RoomOccupancyStats stats = roomService.getOccupancyStatistics();

        return ResponseEntity.ok(
            ApiResponse.success("Occupancy statistics retrieved successfully", stats)
        );
    }

    /**
     * Get count of available beds by room class.
     *
     * GET /api/rooms/available-beds/{roomClass}
     *
     * @param roomClass room class
     * @return available bed count
     */
    @GetMapping("/available-beds/{roomClass}")
    public ResponseEntity<ApiResponse<Integer>> getAvailableBedsByRoomClass(
            @PathVariable RoomClass roomClass) {
        log.info("GET /api/rooms/available-beds/{} - Fetching available bed count", roomClass);

        Integer count = roomService.getAvailableBedsByRoomClass(roomClass);

        return ResponseEntity.ok(
            ApiResponse.success(
                String.format("Available beds for %s: %d", roomClass.getDisplayName(), count),
                count
            )
        );
    }
}
