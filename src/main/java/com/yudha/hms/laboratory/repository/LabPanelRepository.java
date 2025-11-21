package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.LabPanel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LabPanel entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabPanelRepository extends JpaRepository<LabPanel, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<LabPanel> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by panel code
     */
    Optional<LabPanel> findByPanelCodeAndDeletedAtIsNull(String panelCode);

    /**
     * Find all active panels (paginated)
     */
    Page<LabPanel> findByActiveAndDeletedAtIsNull(Boolean active, Pageable pageable);

    /**
     * Find all active panels (list)
     */
    List<LabPanel> findByActiveTrueAndDeletedAtIsNullOrderByPanelNameAsc();

    /**
     * Find popular panels
     */
    @Query("SELECT p FROM LabPanel p WHERE p.isPopular = true AND p.active = true AND p.deletedAt IS NULL ORDER BY p.displayOrder")
    List<LabPanel> findPopularPanels();

    /**
     * Find by category
     */
    List<LabPanel> findByCategoryIdAndActiveAndDeletedAtIsNullOrderByDisplayOrder(UUID categoryId, Boolean active);

    /**
     * Find by category and active
     */
    List<LabPanel> findByCategoryIdAndActiveTrueAndDeletedAtIsNullOrderByPanelNameAsc(UUID categoryId);

    /**
     * Search panels by name or code
     */
    @Query("SELECT p FROM LabPanel p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.panelCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND p.active = true AND p.deletedAt IS NULL")
    Page<LabPanel> searchPanels(@Param("search") String search, Pageable pageable);

    /**
     * Find all panels (paginated)
     */
    Page<LabPanel> findByDeletedAtIsNull(Pageable pageable);

    /**
     * Count active panels
     */
    long countByActiveTrueAndDeletedAtIsNull();
}
