package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.entity.RadiologyModality;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RadiologyModality entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface RadiologyModalityRepository extends JpaRepository<RadiologyModality, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<RadiologyModality> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by code
     */
    Optional<RadiologyModality> findByCodeAndDeletedAtIsNull(String code);

    /**
     * Find all active modalities (paginated)
     */
    Page<RadiologyModality> findByIsActiveAndDeletedAtIsNull(Boolean isActive, Pageable pageable);

    /**
     * Find all active modalities (list)
     */
    List<RadiologyModality> findByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find modalities that require radiation
     */
    List<RadiologyModality> findByRequiresRadiationTrueAndIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find modalities that don't require radiation
     */
    List<RadiologyModality> findByRequiresRadiationFalseAndIsActiveTrueAndDeletedAtIsNull();

    /**
     * Count active modalities
     */
    long countByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Search modalities by name or code
     */
    @Query("SELECT m FROM RadiologyModality m WHERE (LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.code) LIKE LOWER(CONCAT('%', :search, '%'))) AND m.isActive = true AND m.deletedAt IS NULL")
    Page<RadiologyModality> searchModalities(String search, Pageable pageable);
}
