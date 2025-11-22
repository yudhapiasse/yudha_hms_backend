package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.constant.reporting.TranscriptionStatus;
import com.yudha.hms.radiology.entity.reporting.VoiceTranscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoiceTranscriptionRepository extends JpaRepository<VoiceTranscription, UUID> {

    Optional<VoiceTranscription> findByReportId(UUID reportId);

    List<VoiceTranscription> findByTranscriptionStatus(TranscriptionStatus status);

    List<VoiceTranscription> findByRequiresEditingTrue();

    List<VoiceTranscription> findByFailedTrue();

    @Query("SELECT v FROM VoiceTranscription v WHERE v.failed = true AND v.retryCount < 3")
    List<VoiceTranscription> findFailedWithRetryAvailable();

    @Query("SELECT v FROM VoiceTranscription v WHERE v.transcriptionStatus = :status AND v.startedAt BETWEEN :startDate AND :endDate")
    List<VoiceTranscription> findByStatusAndDateRange(
            @Param("status") TranscriptionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT v FROM VoiceTranscription v WHERE v.editedBy = :userId")
    List<VoiceTranscription> findByEditedBy(@Param("userId") UUID userId);

    @Query("SELECT AVG(v.processingTimeSeconds) FROM VoiceTranscription v WHERE v.transcriptionStatus = 'COMPLETED'")
    Double findAverageProcessingTime();

    @Query("SELECT COUNT(v) FROM VoiceTranscription v WHERE v.transcriptionStatus = :status")
    long countByStatus(@Param("status") TranscriptionStatus status);
}
