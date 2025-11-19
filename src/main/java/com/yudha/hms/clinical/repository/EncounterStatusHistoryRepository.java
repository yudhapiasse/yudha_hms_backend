package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.EncounterStatus;
import com.yudha.hms.clinical.entity.EncounterStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Encounter Status History Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface EncounterStatusHistoryRepository extends JpaRepository<EncounterStatusHistory, UUID> {

    /**
     * Find all status history for an encounter.
     */
    List<EncounterStatusHistory> findByEncounterIdOrderByStatusChangedAtDesc(UUID encounterId);

    /**
     * Find status changes to a specific status.
     */
    List<EncounterStatusHistory> findByToStatusOrderByStatusChangedAtDesc(EncounterStatus toStatus);

    /**
     * Find status changes by user.
     */
    List<EncounterStatusHistory> findByChangedByIdOrderByStatusChangedAtDesc(UUID changedById);

    /**
     * Find status changes within a date range.
     */
    List<EncounterStatusHistory> findByStatusChangedAtBetweenOrderByStatusChangedAtDesc(
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    /**
     * Count status changes for an encounter.
     */
    long countByEncounterId(UUID encounterId);
}
