package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.Polyclinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Polyclinic entities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface PolyclinicRepository extends JpaRepository<Polyclinic, UUID> {

    /**
     * Find polyclinic by code.
     */
    Optional<Polyclinic> findByCode(String code);

    /**
     * Find all active polyclinics.
     */
    List<Polyclinic> findByIsActiveTrue();

    /**
     * Find all polyclinics ordered by name.
     */
    List<Polyclinic> findAllByOrderByNameAsc();

    /**
     * Find active polyclinics ordered by name.
     */
    List<Polyclinic> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find polyclinics that allow walk-in.
     */
    List<Polyclinic> findByIsActiveTrueAndAllowWalkInTrue();

    /**
     * Find polyclinics that allow appointments.
     */
    List<Polyclinic> findByIsActiveTrueAndAllowAppointmentsTrue();

    /**
     * Check if polyclinic code exists.
     */
    boolean existsByCode(String code);

    /**
     * Find polyclinics by building.
     */
    List<Polyclinic> findByBuildingAndIsActiveTrue(String building);

    /**
     * Count active polyclinics.
     */
    @Query("SELECT COUNT(p) FROM Polyclinic p WHERE p.isActive = true")
    long countActivePolyclinics();
}