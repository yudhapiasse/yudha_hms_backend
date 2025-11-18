package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.Room;
import com.yudha.hms.registration.entity.RoomClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Room entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    /**
     * Find room by room number.
     *
     * @param roomNumber room number
     * @return optional room
     */
    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * Find all rooms by room class.
     *
     * @param roomClass room class
     * @return list of rooms
     */
    List<Room> findByRoomClass(RoomClass roomClass);

    /**
     * Find all available rooms (has available beds and is active).
     *
     * @return list of available rooms
     */
    @Query("SELECT r FROM Room r WHERE r.isAvailable = true AND r.isActive = true AND r.availableBeds > 0")
    List<Room> findAllAvailable();

    /**
     * Find available rooms by room class.
     *
     * @param roomClass room class
     * @return list of available rooms
     */
    @Query("SELECT r FROM Room r WHERE r.roomClass = :roomClass AND r.isAvailable = true AND r.isActive = true AND r.availableBeds > 0")
    List<Room> findAvailableByRoomClass(@Param("roomClass") RoomClass roomClass);

    /**
     * Find rooms by building and floor.
     *
     * @param building building name
     * @param floor floor number
     * @return list of rooms
     */
    List<Room> findByBuildingAndFloor(String building, String floor);

    /**
     * Find active rooms.
     *
     * @param isActive active status
     * @return list of rooms
     */
    List<Room> findByIsActive(Boolean isActive);

    /**
     * Count rooms by room class.
     *
     * @param roomClass room class
     * @return count of rooms
     */
    long countByRoomClass(RoomClass roomClass);

    /**
     * Count available rooms by room class.
     *
     * @param roomClass room class
     * @return count of available rooms
     */
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomClass = :roomClass AND r.isAvailable = true AND r.isActive = true AND r.availableBeds > 0")
    long countAvailableByRoomClass(@Param("roomClass") RoomClass roomClass);

    /**
     * Get total available beds count by room class.
     *
     * @param roomClass room class
     * @return sum of available beds
     */
    @Query("SELECT SUM(r.availableBeds) FROM Room r WHERE r.roomClass = :roomClass AND r.isAvailable = true AND r.isActive = true")
    Integer getTotalAvailableBedsByRoomClass(@Param("roomClass") RoomClass roomClass);
}
