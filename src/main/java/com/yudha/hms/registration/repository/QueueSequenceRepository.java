package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.QueueSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for QueueSequence entities.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface QueueSequenceRepository extends JpaRepository<QueueSequence, UUID> {

    /**
     * Find queue sequence by polyclinic and date.
     * Uses pessimistic locking to prevent concurrent queue number generation issues.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT qs FROM QueueSequence qs WHERE qs.polyclinic.id = :polyclinicId AND qs.queueDate = :date")
    Optional<QueueSequence> findByPolyclinicIdAndQueueDateWithLock(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date
    );

    /**
     * Find queue sequence by polyclinic and date (without lock).
     */
    Optional<QueueSequence> findByPolyclinicIdAndQueueDate(UUID polyclinicId, LocalDate date);

    /**
     * Find all queue sequences for a specific date.
     */
    List<QueueSequence> findByQueueDate(LocalDate date);

    /**
     * Find all queue sequences for a polyclinic.
     */
    List<QueueSequence> findByPolyclinicIdOrderByQueueDateDesc(UUID polyclinicId);

    /**
     * Find today's queue sequences.
     */
    @Query("SELECT qs FROM QueueSequence qs WHERE qs.queueDate = CURRENT_DATE ORDER BY qs.polyclinic.name ASC")
    List<QueueSequence> findTodaysQueues();

    /**
     * Delete old queue sequences (cleanup).
     */
    @Query("DELETE FROM QueueSequence qs WHERE qs.queueDate < :date")
    void deleteOldQueueSequences(@Param("date") LocalDate date);

    /**
     * Get current queue number for polyclinic today.
     */
    @Query("SELECT qs.lastQueueNumber FROM QueueSequence qs " +
           "WHERE qs.polyclinic.id = :polyclinicId " +
           "AND qs.queueDate = :date")
    Optional<Integer> getCurrentQueueNumber(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date
    );
}