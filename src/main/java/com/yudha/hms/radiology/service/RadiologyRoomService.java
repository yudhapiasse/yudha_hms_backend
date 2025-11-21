package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.entity.RadiologyModality;
import com.yudha.hms.radiology.entity.RadiologyRoom;
import com.yudha.hms.radiology.repository.RadiologyModalityRepository;
import com.yudha.hms.radiology.repository.RadiologyOrderRepository;
import com.yudha.hms.radiology.repository.RadiologyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Radiology Room operations.
 *
 * Handles CRUD operations and business logic for radiology rooms/equipment.
 *
 * Features:
 * - CRUD operations with validation
 * - Get rooms by modality
 * - Get available rooms for booking
 * - Get operational rooms
 * - Check room availability for date/time
 * - Schedule maintenance
 * - Mark room as operational/non-operational
 * - Get rooms requiring calibration
 * - Validation: unique room code, valid modality
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RadiologyRoomService {

    private final RadiologyRoomRepository roomRepository;
    private final RadiologyModalityRepository modalityRepository;
    private final RadiologyOrderRepository orderRepository;

    /**
     * Create a new radiology room.
     *
     * @param room Room to create
     * @return Created room
     * @throws IllegalArgumentException if room code exists or modality not found
     */
    public RadiologyRoom createRoom(RadiologyRoom room) {
        log.info("Creating new radiology room: {}", room.getRoomName());

        // Validate unique room code
        if (roomRepository.findByRoomCodeAndDeletedAtIsNull(room.getRoomCode()).isPresent()) {
            throw new IllegalArgumentException("Room code already exists: " + room.getRoomCode());
        }

        // Validate modality exists
        if (room.getModality() == null || room.getModality().getId() == null) {
            throw new IllegalArgumentException("Modality is required for room");
        }
        modalityRepository.findByIdAndDeletedAtIsNull(room.getModality().getId())
                .orElseThrow(() -> new IllegalArgumentException("Modality not found: " + room.getModality().getId()));

        // Set defaults
        if (room.getIsOperational() == null) {
            room.setIsOperational(true);
        }
        if (room.getIsAvailable() == null) {
            room.setIsAvailable(true);
        }

        RadiologyRoom saved = roomRepository.save(room);
        log.info("Room created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Update an existing radiology room.
     *
     * @param id ID of room to update
     * @param roomUpdate Updated room data
     * @return Updated room
     * @throws IllegalArgumentException if room not found or code exists
     */
    public RadiologyRoom updateRoom(UUID id, RadiologyRoom roomUpdate) {
        log.info("Updating radiology room: {}", id);

        RadiologyRoom existing = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));

        // Check room code uniqueness if changed
        if (!existing.getRoomCode().equals(roomUpdate.getRoomCode())) {
            if (roomRepository.findByRoomCodeAndDeletedAtIsNull(roomUpdate.getRoomCode()).isPresent()) {
                throw new IllegalArgumentException("Room code already exists: " + roomUpdate.getRoomCode());
            }
        }

        // Update fields
        existing.setRoomCode(roomUpdate.getRoomCode());
        existing.setRoomName(roomUpdate.getRoomName());
        existing.setLocation(roomUpdate.getLocation());
        existing.setFloor(roomUpdate.getFloor());
        existing.setEquipmentName(roomUpdate.getEquipmentName());
        existing.setEquipmentModel(roomUpdate.getEquipmentModel());
        existing.setManufacturer(roomUpdate.getManufacturer());
        existing.setInstallationDate(roomUpdate.getInstallationDate());
        existing.setLastCalibrationDate(roomUpdate.getLastCalibrationDate());
        existing.setNextCalibrationDate(roomUpdate.getNextCalibrationDate());
        existing.setIsOperational(roomUpdate.getIsOperational());
        existing.setIsAvailable(roomUpdate.getIsAvailable());
        existing.setMaxBookingsPerDay(roomUpdate.getMaxBookingsPerDay());
        existing.setNotes(roomUpdate.getNotes());

        // Update modality if changed
        if (roomUpdate.getModality() != null &&
                !existing.getModality().getId().equals(roomUpdate.getModality().getId())) {
            RadiologyModality newModality = modalityRepository.findByIdAndDeletedAtIsNull(roomUpdate.getModality().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Modality not found: " + roomUpdate.getModality().getId()));
            existing.setModality(newModality);
        }

        RadiologyRoom updated = roomRepository.save(existing);
        log.info("Room updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) a radiology room.
     *
     * @param id ID of room to delete
     * @param deletedBy User ID who performed deletion
     * @throws IllegalArgumentException if room not found
     */
    public void deleteRoom(UUID id, String deletedBy) {
        log.info("Deleting radiology room: {}", id);

        RadiologyRoom room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));

        // Soft delete
        room.setDeletedAt(LocalDateTime.now());
        room.setDeletedBy(deletedBy);
        roomRepository.save(room);

        log.info("Room deleted successfully: {}", id);
    }

    /**
     * Get room by ID.
     *
     * @param id Room ID
     * @return Room
     * @throws IllegalArgumentException if room not found
     */
    @Transactional(readOnly = true)
    public RadiologyRoom getRoomById(UUID id) {
        return roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));
    }

    /**
     * Get room by code.
     *
     * @param roomCode Room code
     * @return Room
     * @throws IllegalArgumentException if room not found
     */
    @Transactional(readOnly = true)
    public RadiologyRoom getRoomByCode(String roomCode) {
        return roomRepository.findByRoomCodeAndDeletedAtIsNull(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with code: " + roomCode));
    }

    /**
     * Get all operational rooms.
     *
     * @return List of operational rooms
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getAllOperationalRooms() {
        return roomRepository.findByIsOperationalTrueAndDeletedAtIsNull();
    }

    /**
     * Get all available rooms (operational and available for booking).
     *
     * @return List of available rooms
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getAvailableRooms() {
        return roomRepository.findByIsOperationalTrueAndIsAvailableTrueAndDeletedAtIsNull();
    }

    /**
     * Get rooms by modality.
     *
     * @param modalityId Modality ID
     * @return List of operational rooms for the modality
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getRoomsByModality(UUID modalityId) {
        return roomRepository.findByModalityIdAndIsOperationalTrueAndDeletedAtIsNull(modalityId);
    }

    /**
     * Get available rooms by modality.
     *
     * @param modalityId Modality ID
     * @return List of available rooms for the modality
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getAvailableRoomsByModality(UUID modalityId) {
        return roomRepository.findByModalityIdAndIsOperationalTrueAndIsAvailableTrueAndDeletedAtIsNull(modalityId);
    }

    /**
     * Get rooms by location.
     *
     * @param location Location
     * @return List of operational rooms at the location
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getRoomsByLocation(String location) {
        return roomRepository.findByLocationAndIsOperationalTrueAndDeletedAtIsNull(location);
    }

    /**
     * Get rooms by floor.
     *
     * @param floor Floor
     * @return List of operational rooms on the floor
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getRoomsByFloor(String floor) {
        return roomRepository.findByFloorAndIsOperationalTrueAndDeletedAtIsNull(floor);
    }

    /**
     * Get rooms by manufacturer.
     *
     * @param manufacturer Manufacturer name
     * @return List of operational rooms from the manufacturer
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getRoomsByManufacturer(String manufacturer) {
        return roomRepository.findByManufacturerAndIsOperationalTrueAndDeletedAtIsNull(manufacturer);
    }

    /**
     * Get rooms requiring calibration.
     *
     * @return List of rooms needing calibration
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getRoomsRequiringCalibration() {
        return roomRepository.findRoomsNeedingCalibration(LocalDate.now());
    }

    /**
     * Get rooms requiring calibration by date.
     *
     * @param date Target date
     * @return List of rooms needing calibration by the date
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getRoomsRequiringCalibrationByDate(LocalDate date) {
        return roomRepository.findRoomsNeedingCalibration(date);
    }

    /**
     * Search rooms by name or code.
     *
     * @param search Search term
     * @param pageable Pagination parameters
     * @return Page of matching rooms
     */
    @Transactional(readOnly = true)
    public Page<RadiologyRoom> searchRooms(String search, Pageable pageable) {
        return roomRepository.searchRooms(search, pageable);
    }

    /**
     * Check room availability for a specific date.
     * Checks if room is operational, available, and not fully booked.
     *
     * @param roomId Room ID
     * @param date Target date
     * @return True if room is available
     */
    @Transactional(readOnly = true)
    public boolean checkRoomAvailability(UUID roomId, LocalDate date) {
        RadiologyRoom room = getRoomById(roomId);

        // Check if room is operational and available
        if (!room.getIsOperational() || !room.getIsAvailable()) {
            return false;
        }

        // Check booking capacity if set
        if (room.getMaxBookingsPerDay() != null) {
            long bookingCount = orderRepository.findByScheduledDateAndRoomIdAndDeletedAtIsNull(date, roomId).size();
            if (bookingCount >= room.getMaxBookingsPerDay()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get available rooms for booking on a specific date.
     *
     * @param modalityId Modality ID (optional, can be null)
     * @param date Target date
     * @return List of available rooms
     */
    @Transactional(readOnly = true)
    public List<RadiologyRoom> getAvailableRoomsForDate(UUID modalityId, LocalDate date) {
        List<RadiologyRoom> rooms;
        if (modalityId != null) {
            rooms = getAvailableRoomsByModality(modalityId);
        } else {
            rooms = getAvailableRooms();
        }

        // Filter by availability for the date
        return rooms.stream()
                .filter(room -> checkRoomAvailability(room.getId(), date))
                .toList();
    }

    /**
     * Schedule maintenance for a room.
     * Marks room as unavailable.
     *
     * @param roomId Room ID
     * @param maintenanceDate Maintenance date
     * @return Updated room
     * @throws IllegalArgumentException if room not found
     */
    public RadiologyRoom scheduleMaintenance(UUID roomId, LocalDate maintenanceDate) {
        log.info("Scheduling maintenance for room: {} on {}", roomId, maintenanceDate);

        RadiologyRoom room = getRoomById(roomId);
        room.setIsAvailable(false);
        room.setNotes((room.getNotes() != null ? room.getNotes() + "\n" : "") +
                "Maintenance scheduled for: " + maintenanceDate);

        RadiologyRoom updated = roomRepository.save(room);
        log.info("Maintenance scheduled for room: {}", roomId);
        return updated;
    }

    /**
     * Mark room as operational.
     *
     * @param roomId Room ID
     * @return Updated room
     * @throws IllegalArgumentException if room not found
     */
    public RadiologyRoom markRoomAsOperational(UUID roomId) {
        log.info("Marking room as operational: {}", roomId);

        RadiologyRoom room = getRoomById(roomId);
        room.setIsOperational(true);
        room.setIsAvailable(true);

        RadiologyRoom updated = roomRepository.save(room);
        log.info("Room marked as operational: {}", roomId);
        return updated;
    }

    /**
     * Mark room as non-operational.
     *
     * @param roomId Room ID
     * @param reason Reason for marking non-operational
     * @return Updated room
     * @throws IllegalArgumentException if room not found
     */
    public RadiologyRoom markRoomAsNonOperational(UUID roomId, String reason) {
        log.info("Marking room as non-operational: {} - Reason: {}", roomId, reason);

        RadiologyRoom room = getRoomById(roomId);
        room.setIsOperational(false);
        room.setIsAvailable(false);
        room.setNotes((room.getNotes() != null ? room.getNotes() + "\n" : "") +
                LocalDateTime.now() + " - Non-operational: " + reason);

        RadiologyRoom updated = roomRepository.save(room);
        log.info("Room marked as non-operational: {}", roomId);
        return updated;
    }

    /**
     * Update room calibration date.
     *
     * @param roomId Room ID
     * @param calibrationDate Calibration date
     * @param nextCalibrationDate Next calibration due date
     * @return Updated room
     * @throws IllegalArgumentException if room not found
     */
    public RadiologyRoom updateCalibrationDate(UUID roomId, LocalDate calibrationDate, LocalDate nextCalibrationDate) {
        log.info("Updating calibration date for room: {}", roomId);

        RadiologyRoom room = getRoomById(roomId);
        room.setLastCalibrationDate(calibrationDate);
        room.setNextCalibrationDate(nextCalibrationDate);

        RadiologyRoom updated = roomRepository.save(room);
        log.info("Calibration date updated for room: {}", roomId);
        return updated;
    }

    /**
     * Count operational rooms.
     *
     * @return Count of operational rooms
     */
    @Transactional(readOnly = true)
    public long countOperationalRooms() {
        return roomRepository.countByIsOperationalTrueAndDeletedAtIsNull();
    }

    /**
     * Count available rooms.
     *
     * @return Count of available rooms
     */
    @Transactional(readOnly = true)
    public long countAvailableRooms() {
        return roomRepository.countByIsOperationalTrueAndIsAvailableTrueAndDeletedAtIsNull();
    }

    /**
     * Count rooms by modality.
     *
     * @param modalityId Modality ID
     * @return Count of operational rooms
     */
    @Transactional(readOnly = true)
    public long countRoomsByModality(UUID modalityId) {
        return roomRepository.countByModalityIdAndIsOperationalTrueAndDeletedAtIsNull(modalityId);
    }

    /**
     * Check if a room code exists.
     *
     * @param roomCode Room code
     * @return True if code exists
     */
    @Transactional(readOnly = true)
    public boolean roomCodeExists(String roomCode) {
        return roomRepository.findByRoomCodeAndDeletedAtIsNull(roomCode).isPresent();
    }
}
