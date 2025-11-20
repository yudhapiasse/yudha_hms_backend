package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.QueueCallHistory;
import com.yudha.hms.registration.entity.QueueCallType;
import com.yudha.hms.registration.entity.QueueResponseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for QueueCallHistory entity.
 * Handles queue call history data access.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface QueueCallHistoryRepository extends JpaRepository<QueueCallHistory, UUID> {

    /**
     * Find all call history for an outpatient registration.
     */
    List<QueueCallHistory> findByOutpatientRegistrationIdOrderByCalledAtDesc(UUID outpatientRegistrationId);

    /**
     * Find latest call for an outpatient registration.
     */
    Optional<QueueCallHistory> findFirstByOutpatientRegistrationIdOrderByCalledAtDesc(UUID outpatientRegistrationId);

    /**
     * Find all calls for a polyclinic on a specific date.
     */
    @Query("SELECT qch FROM QueueCallHistory qch " +
           "WHERE qch.polyclinicId = :polyclinicId " +
           "AND CAST(qch.calledAt AS LocalDate) = :date " +
           "ORDER BY qch.calledAt DESC")
    List<QueueCallHistory> findByPolyclinicAndDate(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date
    );

    /**
     * Find all calls for a doctor on a specific date.
     */
    @Query("SELECT qch FROM QueueCallHistory qch " +
           "WHERE qch.doctorId = :doctorId " +
           "AND CAST(qch.calledAt AS LocalDate) = :date " +
           "ORDER BY qch.calledAt DESC")
    List<QueueCallHistory> findByDoctorAndDate(
        @Param("doctorId") UUID doctorId,
        @Param("date") LocalDate date
    );

    /**
     * Count calls for a polyclinic on a specific date.
     */
    @Query("SELECT COUNT(qch) FROM QueueCallHistory qch " +
           "WHERE qch.polyclinicId = :polyclinicId " +
           "AND CAST(qch.calledAt AS LocalDate) = :date")
    long countByPolyclinicAndDate(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date
    );

    /**
     * Count calls by response status for a polyclinic on a specific date.
     */
    @Query("SELECT COUNT(qch) FROM QueueCallHistory qch " +
           "WHERE qch.polyclinicId = :polyclinicId " +
           "AND CAST(qch.calledAt AS LocalDate) = :date " +
           "AND qch.responseStatus = :responseStatus")
    long countByPolyclinicDateAndResponseStatus(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date,
        @Param("responseStatus") QueueResponseStatus responseStatus
    );

    /**
     * Find calls with no response for a polyclinic today.
     */
    @Query("SELECT qch FROM QueueCallHistory qch " +
           "WHERE qch.polyclinicId = :polyclinicId " +
           "AND CAST(qch.calledAt AS LocalDate) = CURRENT_DATE " +
           "AND qch.responseStatus = 'NO_RESPONSE' " +
           "ORDER BY qch.calledAt DESC")
    List<QueueCallHistory> findNoResponseCallsToday(@Param("polyclinicId") UUID polyclinicId);

    /**
     * Find recent calls (last 10) for a polyclinic.
     */
    @Query("SELECT qch FROM QueueCallHistory qch " +
           "WHERE qch.polyclinicId = :polyclinicId " +
           "ORDER BY qch.calledAt DESC " +
           "LIMIT 10")
    List<QueueCallHistory> findRecentCallsByPolyclinic(@Param("polyclinicId") UUID polyclinicId);

    /**
     * Calculate average response time for a polyclinic on a date.
     * Returns average in seconds.
     */
    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (responded_at - called_at))) " +
           "FROM registration_schema.queue_call_history " +
           "WHERE polyclinic_id = :polyclinicId " +
           "AND DATE(called_at) = :date " +
           "AND responded_at IS NOT NULL",
           nativeQuery = true)
    Double calculateAverageResponseTimeSeconds(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date
    );

    /**
     * Count recalls (call type = RECALL) for a polyclinic on a date.
     */
    @Query("SELECT COUNT(qch) FROM QueueCallHistory qch " +
           "WHERE qch.polyclinicId = :polyclinicId " +
           "AND CAST(qch.calledAt AS LocalDate) = :date " +
           "AND qch.callType = :callType")
    long countByPolyclinicDateAndCallType(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("date") LocalDate date,
        @Param("callType") QueueCallType callType
    );

    /**
     * Find calls within time range.
     */
    @Query("SELECT qch FROM QueueCallHistory qch " +
           "WHERE qch.polyclinicId = :polyclinicId " +
           "AND qch.calledAt BETWEEN :startTime AND :endTime " +
           "ORDER BY qch.calledAt DESC")
    List<QueueCallHistory> findByPolyclinicAndTimeRange(
        @Param("polyclinicId") UUID polyclinicId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * Delete old call history before a certain date.
     */
    @Modifying
    @Query("DELETE FROM QueueCallHistory qch WHERE CAST(qch.calledAt AS LocalDate) < :beforeDate")
    void deleteOldCallHistory(@Param("beforeDate") LocalDate beforeDate);
}
