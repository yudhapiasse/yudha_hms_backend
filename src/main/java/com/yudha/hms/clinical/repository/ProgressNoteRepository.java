package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.NoteType;
import com.yudha.hms.clinical.entity.ProgressNote;
import com.yudha.hms.clinical.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProgressNote entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface ProgressNoteRepository extends JpaRepository<ProgressNote, UUID> {

    /**
     * Find by note number.
     */
    Optional<ProgressNote> findByNoteNumber(String noteNumber);

    /**
     * Find all notes for an encounter.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.encounter.id = :encounterId ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find all notes for a patient.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.patientId = :patientId ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find latest note for an encounter.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.encounter.id = :encounterId ORDER BY pn.noteDateTime DESC LIMIT 1")
    Optional<ProgressNote> findLatestByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find notes by encounter and type.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.encounter.id = :encounterId AND pn.noteType = :noteType ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findByEncounterIdAndNoteType(@Param("encounterId") UUID encounterId, @Param("noteType") NoteType noteType);

    /**
     * Find notes by encounter and shift.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.encounter.id = :encounterId AND pn.shift = :shift ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findByEncounterIdAndShift(@Param("encounterId") UUID encounterId, @Param("shift") Shift shift);

    /**
     * Find notes by provider.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.providerId = :providerId ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findByProviderId(@Param("providerId") UUID providerId);

    /**
     * Find notes requiring cosign.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.requiresCosign = true AND pn.cosigned = false ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findNotesRequiringCosign();

    /**
     * Find notes with critical findings.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.encounter.id = :encounterId AND pn.criticalFindings IS NOT NULL AND pn.criticalFindings != '' ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findCriticalFindingsByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find notes by date range.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.encounter.id = :encounterId AND pn.noteDateTime BETWEEN :startDate AND :endDate ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findByEncounterIdAndDateRange(
        @Param("encounterId") UUID encounterId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find shift handover notes for a specific date.
     */
    @Query("SELECT pn FROM ProgressNote pn WHERE pn.encounter.id = :encounterId " +
           "AND pn.noteType = 'SHIFT_HANDOVER' " +
           "AND CAST(pn.noteDateTime AS date) = CAST(:date AS date) " +
           "ORDER BY pn.noteDateTime DESC")
    List<ProgressNote> findShiftHandoverNotesByDate(
        @Param("encounterId") UUID encounterId,
        @Param("date") LocalDateTime date
    );

    /**
     * Count notes by encounter.
     */
    @Query("SELECT COUNT(pn) FROM ProgressNote pn WHERE pn.encounter.id = :encounterId")
    long countByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Check if encounter has SOAP note for today.
     */
    @Query("SELECT CASE WHEN COUNT(pn) > 0 THEN true ELSE false END FROM ProgressNote pn " +
           "WHERE pn.encounter.id = :encounterId " +
           "AND pn.noteType = 'SOAP' " +
           "AND CAST(pn.noteDateTime AS date) = CAST(:date AS date)")
    boolean hasSOAPNoteForDate(
        @Param("encounterId") UUID encounterId,
        @Param("date") LocalDateTime date
    );
}
