package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Bed entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface BedRepository extends JpaRepository<Bed, UUID> {

    /**
     * Find bed by room and bed number.
     *
     * @param roomId room ID
     * @param bedNumber bed number
     * @return optional bed
     */
    Optional<Bed> findByRoom_IdAndBedNumber(UUID roomId, String bedNumber);

    /**
     * Find all beds in a room.
     *
     * @param roomId room ID
     * @return list of beds
     */
    List<Bed> findByRoom_Id(UUID roomId);

    /**
     * Find available beds in a room.
     *
     * @param roomId room ID
     * @return list of available beds
     */
    @Query("SELECT b FROM Bed b WHERE b.room.id = :roomId AND b.isActive = true AND b.isOccupied = false AND b.isMaintenance = false")
    List<Bed> findAvailableByRoomId(@Param("roomId") UUID roomId);

    /**
     * Find all available beds across all rooms.
     *
     * @return list of available beds
     */
    @Query("SELECT b FROM Bed b WHERE b.isActive = true AND b.isOccupied = false AND b.isMaintenance = false")
    List<Bed> findAllAvailable();

    /**
     * Find occupied beds.
     *
     * @param isOccupied occupation status
     * @return list of beds
     */
    List<Bed> findByIsOccupied(Boolean isOccupied);

    /**
     * Find bed by current patient.
     *
     * @param patientId patient ID
     * @return optional bed
     */
    Optional<Bed> findByCurrentPatientId(UUID patientId);

    /**
     * Find bed by current admission.
     *
     * @param admissionId admission ID
     * @return optional bed
     */
    Optional<Bed> findByCurrentAdmissionId(UUID admissionId);

    /**
     * Find beds under maintenance.
     *
     * @param isMaintenance maintenance status
     * @return list of beds
     */
    List<Bed> findByIsMaintenance(Boolean isMaintenance);

    /**
     * Count available beds in a room.
     *
     * @param roomId room ID
     * @return count of available beds
     */
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.id = :roomId AND b.isActive = true AND b.isOccupied = false AND b.isMaintenance = false")
    long countAvailableByRoomId(@Param("roomId") UUID roomId);

    /**
     * Count occupied beds in a room.
     *
     * @param roomId room ID
     * @return count of occupied beds
     */
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.room.id = :roomId AND b.isOccupied = true")
    long countOccupiedByRoomId(@Param("roomId") UUID roomId);
}
