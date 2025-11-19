package com.yudha.hms.registration.service;

import com.yudha.hms.registration.dto.RoomAvailabilityDto;
import com.yudha.hms.registration.entity.Bed;
import com.yudha.hms.registration.entity.Room;
import com.yudha.hms.registration.entity.RoomClass;
import com.yudha.hms.registration.repository.BedRepository;
import com.yudha.hms.registration.repository.RoomRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for room and bed management.
 * Handles room availability checking, bed assignment, and room queries.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    /**
     * Get all available rooms with bed information.
     *
     * @return list of available rooms
     */
    public List<RoomAvailabilityDto> getAllAvailableRooms() {
        log.info("Fetching all available rooms");
        List<Room> rooms = roomRepository.findAllAvailable();
        return rooms.stream()
            .map(this::convertToAvailabilityDto)
            .collect(Collectors.toList());
    }

    /**
     * Get available rooms by room class.
     *
     * @param roomClass room class
     * @return list of available rooms
     */
    public List<RoomAvailabilityDto> getAvailableRoomsByClass(RoomClass roomClass) {
        log.info("Fetching available rooms for class: {}", roomClass);
        List<Room> rooms = roomRepository.findAvailableByRoomClass(roomClass);
        return rooms.stream()
            .map(this::convertToAvailabilityDto)
            .collect(Collectors.toList());
    }

    /**
     * Get room by ID with availability information.
     *
     * @param roomId room ID
     * @return room availability DTO
     */
    public RoomAvailabilityDto getRoomAvailability(UUID roomId) {
        log.info("Fetching room availability for room ID: {}", roomId);
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "ID", roomId));
        return convertToAvailabilityDto(room);
    }

    /**
     * Get room by room number with availability information.
     *
     * @param roomNumber room number
     * @return room availability DTO
     */
    public RoomAvailabilityDto getRoomAvailabilityByNumber(String roomNumber) {
        log.info("Fetching room availability for room number: {}", roomNumber);
        Room room = roomRepository.findByRoomNumber(roomNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "room number", roomNumber));
        return convertToAvailabilityDto(room);
    }

    /**
     * Check if a specific room has available beds.
     *
     * @param roomId room ID
     * @return true if room has available beds
     */
    public boolean hasAvailableBeds(UUID roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "ID", roomId));
        return room.hasAvailableBeds();
    }

    /**
     * Get count of available beds by room class.
     *
     * @param roomClass room class
     * @return total available beds
     */
    public Integer getAvailableBedsByRoomClass(RoomClass roomClass) {
        log.info("Counting available beds for room class: {}", roomClass);
        Integer count = roomRepository.getTotalAvailableBedsByRoomClass(roomClass);
        return count != null ? count : 0;
    }

    /**
     * Find best available room for a patient.
     * Selects room with lowest occupancy to distribute patients evenly.
     *
     * @param roomClass desired room class
     * @return room with available bed, or null if none available
     */
    public Room findBestAvailableRoom(RoomClass roomClass) {
        log.info("Finding best available room for class: {}", roomClass);
        List<Room> availableRooms = roomRepository.findAvailableByRoomClass(roomClass);

        if (availableRooms.isEmpty()) {
            log.warn("No available rooms found for class: {}", roomClass);
            return null;
        }

        // Sort by available beds (descending) to prefer rooms with more availability
        return availableRooms.stream()
            .sorted((r1, r2) -> r2.getAvailableBeds().compareTo(r1.getAvailableBeds()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Find an available bed in a specific room.
     *
     * @param roomId room ID
     * @return available bed, or null if none available
     */
    public Bed findAvailableBedInRoom(UUID roomId) {
        log.info("Finding available bed in room: {}", roomId);
        List<Bed> availableBeds = bedRepository.findAvailableByRoomId(roomId);

        if (availableBeds.isEmpty()) {
            log.warn("No available beds in room: {}", roomId);
            return null;
        }

        // Prefer window beds first, then other positions
        return availableBeds.stream()
            .sorted((b1, b2) -> {
                if ("WINDOW".equals(b1.getBedPosition())) return -1;
                if ("WINDOW".equals(b2.getBedPosition())) return 1;
                return 0;
            })
            .findFirst()
            .orElse(null);
    }

    /**
     * Get all rooms by class (including occupied).
     *
     * @param roomClass room class
     * @return list of rooms
     */
    public List<Room> getRoomsByClass(RoomClass roomClass) {
        log.info("Fetching all rooms for class: {}", roomClass);
        return roomRepository.findByRoomClass(roomClass);
    }

    /**
     * Get room entity by ID.
     *
     * @param roomId room ID
     * @return room entity
     */
    public Room getRoomById(UUID roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room", "ID", roomId));
    }

    /**
     * Get bed entity by ID.
     *
     * @param bedId bed ID
     * @return bed entity
     */
    public Bed getBedById(UUID bedId) {
        return bedRepository.findById(bedId)
            .orElseThrow(() -> new ResourceNotFoundException("Bed", "ID", bedId));
    }

    /**
     * Update room when bed is occupied.
     *
     * @param roomId room ID
     */
    @Transactional
    public void occupyBed(UUID roomId) {
        Room room = getRoomById(roomId);
        room.occupyBed();
        roomRepository.save(room);
        log.info("Bed occupied in room: {}. Available beds: {}", room.getRoomNumber(), room.getAvailableBeds());
    }

    /**
     * Update room when bed is released.
     *
     * @param roomId room ID
     */
    @Transactional
    public void releaseBed(UUID roomId) {
        Room room = getRoomById(roomId);
        room.releaseBed();
        roomRepository.save(room);
        log.info("Bed released in room: {}. Available beds: {}", room.getRoomNumber(), room.getAvailableBeds());
    }

    /**
     * Get room occupancy statistics.
     *
     * @return occupancy statistics map
     */
    public RoomOccupancyStats getOccupancyStatistics() {
        log.info("Calculating room occupancy statistics");

        List<Room> allRooms = roomRepository.findByIsActive(true);
        int totalRooms = allRooms.size();
        int totalBeds = allRooms.stream().mapToInt(Room::getTotalBeds).sum();
        int occupiedBeds = totalBeds - allRooms.stream().mapToInt(Room::getAvailableBeds).sum();
        int availableBeds = totalBeds - occupiedBeds;

        double occupancyRate = totalBeds > 0 ? (occupiedBeds * 100.0 / totalBeds) : 0;

        return RoomOccupancyStats.builder()
            .totalRooms(totalRooms)
            .totalBeds(totalBeds)
            .occupiedBeds(occupiedBeds)
            .availableBeds(availableBeds)
            .occupancyRate(occupancyRate)
            .build();
    }

    /**
     * Convert Room entity to RoomAvailabilityDto.
     *
     * @param room room entity
     * @return room availability DTO
     */
    private RoomAvailabilityDto convertToAvailabilityDto(Room room) {
        // Get available beds in this room
        List<Bed> availableBeds = bedRepository.findAvailableByRoomId(room.getId());

        List<RoomAvailabilityDto.BedInfo> bedInfoList = availableBeds.stream()
            .map(bed -> RoomAvailabilityDto.BedInfo.builder()
                .bedId(bed.getId())
                .bedNumber(bed.getBedNumber())
                .bedType(bed.getBedType())
                .bedPosition(bed.getBedPosition())
                .hasMonitor(bed.getHasMonitor())
                .hasVentilator(bed.getHasVentilator())
                .hasOxygen(bed.getHasOxygen())
                .isAvailable(bed.isAvailable())
                .build())
            .collect(Collectors.toList());

        // Calculate estimated deposit (3 days default)
        BigDecimal estimatedDeposit = room.getBaseRoomRate().multiply(BigDecimal.valueOf(3));

        return RoomAvailabilityDto.builder()
            .roomId(room.getId())
            .roomNumber(room.getRoomNumber())
            .roomName(room.getRoomName())
            .roomClass(room.getRoomClass())
            .roomType(room.getRoomType())
            .building(room.getBuilding())
            .floor(room.getFloor())
            .wing(room.getWing())
            .totalBeds(room.getTotalBeds())
            .availableBeds(room.getAvailableBeds())
            .occupiedBeds(room.getTotalBeds() - room.getAvailableBeds())
            .baseRoomRate(room.getBaseRoomRate())
            .hasAc(room.getHasAc())
            .hasTv(room.getHasTv())
            .hasBathroom(room.getHasBathroom())
            .hasWifi(room.getHasWifi())
            .hasRefrigerator(room.getHasRefrigerator())
            .hasSofaBed(room.getHasSofaBed())
            .isAvailable(room.getIsAvailable())
            .isActive(room.getIsActive())
            .availableBedsList(bedInfoList)
            .estimatedDeposit(estimatedDeposit)
            .build();
    }

    /**
     * Inner class for room occupancy statistics.
     */
    @lombok.Data
    @lombok.Builder
    public static class RoomOccupancyStats {
        private Integer totalRooms;
        private Integer totalBeds;
        private Integer occupiedBeds;
        private Integer availableBeds;
        private Double occupancyRate; // Percentage
    }
}
