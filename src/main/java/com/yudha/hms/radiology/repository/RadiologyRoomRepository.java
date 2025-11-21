package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.entity.RadiologyRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RadiologyRoom entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface RadiologyRoomRepository extends JpaRepository<RadiologyRoom, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<RadiologyRoom> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by room code
     */
    Optional<RadiologyRoom> findByRoomCodeAndDeletedAtIsNull(String roomCode);

    /**
     * Find all operational rooms
     */
    List<RadiologyRoom> findByIsOperationalTrueAndDeletedAtIsNull();

    /**
     * Find all available rooms
     */
    List<RadiologyRoom> findByIsOperationalTrueAndIsAvailableTrueAndDeletedAtIsNull();

    /**
     * Find by modality (operational rooms)
     */
    List<RadiologyRoom> findByModalityIdAndIsOperationalTrueAndDeletedAtIsNull(UUID modalityId);

    /**
     * Find available rooms by modality
     */
    List<RadiologyRoom> findByModalityIdAndIsOperationalTrueAndIsAvailableTrueAndDeletedAtIsNull(UUID modalityId);

    /**
     * Find by location
     */
    List<RadiologyRoom> findByLocationAndIsOperationalTrueAndDeletedAtIsNull(String location);

    /**
     * Find by floor
     */
    List<RadiologyRoom> findByFloorAndIsOperationalTrueAndDeletedAtIsNull(String floor);

    /**
     * Find rooms needing calibration
     */
    @Query("SELECT r FROM RadiologyRoom r WHERE r.nextCalibrationDate IS NOT NULL AND r.nextCalibrationDate <= :date AND r.isOperational = true AND r.deletedAt IS NULL")
    List<RadiologyRoom> findRoomsNeedingCalibration(@Param("date") LocalDate date);

    /**
     * Find rooms by manufacturer
     */
    List<RadiologyRoom> findByManufacturerAndIsOperationalTrueAndDeletedAtIsNull(String manufacturer);

    /**
     * Search rooms by name or code
     */
    @Query("SELECT r FROM RadiologyRoom r WHERE (LOWER(r.roomName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND r.isOperational = true AND r.deletedAt IS NULL")
    Page<RadiologyRoom> searchRooms(@Param("search") String search, Pageable pageable);

    /**
     * Count operational rooms
     */
    long countByIsOperationalTrueAndDeletedAtIsNull();

    /**
     * Count available rooms
     */
    long countByIsOperationalTrueAndIsAvailableTrueAndDeletedAtIsNull();

    /**
     * Count rooms by modality
     */
    long countByModalityIdAndIsOperationalTrueAndDeletedAtIsNull(UUID modalityId);
}
