package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.LabPanelItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LabPanelItem entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabPanelItemRepository extends JpaRepository<LabPanelItem, UUID> {

    /**
     * Find items by panel (no ordering)
     */
    List<LabPanelItem> findByPanelId(UUID panelId);

    /**
     * Find items by panel ordered by display order
     */
    List<LabPanelItem> findByPanelIdOrderByDisplayOrder(UUID panelId);

    /**
     * Find mandatory items by panel
     */
    List<LabPanelItem> findByPanelIdAndIsMandatoryTrueOrderByDisplayOrder(UUID panelId);

    /**
     * Find by panel and test
     */
    @Query("SELECT pi FROM LabPanelItem pi WHERE pi.panel.id = :panelId AND pi.test.id = :testId")
    Optional<LabPanelItem> findByPanelIdAndTestId(@Param("panelId") UUID panelId, @Param("testId") UUID testId);

    /**
     * Count tests in panel
     */
    long countByPanelId(UUID panelId);
}
