package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.radiology.constant.reporting.TranscriptionStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "voice_transcription", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceTranscription extends SoftDeletableEntity {

    @Column(name = "report_id", nullable = false)
    private UUID reportId;

    @Column(name = "audio_file_path", length = 500)
    private String audioFilePath;

    @Column(name = "audio_file_size_bytes")
    private Long audioFileSizeBytes;

    @Column(name = "audio_duration_seconds")
    private Integer audioDurationSeconds;

    @Column(name = "audio_format", length = 20)
    private String audioFormat;

    @Column(name = "transcription_engine", length = 50)
    private String transcriptionEngine;

    @Column(name = "transcription_model", length = 50)
    private String transcriptionModel;

    @Column(name = "transcription_language", length = 10)
    private String transcriptionLanguage = "id-ID";

    @Column(name = "raw_transcription", columnDefinition = "TEXT")
    private String rawTranscription;

    @Column(name = "edited_transcription", columnDefinition = "TEXT")
    private String editedTranscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "transcription_status", length = 30, nullable = false)
    private TranscriptionStatus transcriptionStatus = TranscriptionStatus.PENDING;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "processing_time_seconds")
    private Integer processingTimeSeconds;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "word_error_rate", precision = 5, scale = 2)
    private BigDecimal wordErrorRate;

    @Column(name = "requires_editing")
    private Boolean requiresEditing = false;

    @Column(name = "edited_by")
    private UUID editedBy;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "edit_count")
    private Integer editCount = 0;

    @Column(name = "speaker_id", length = 50)
    private String speakerId;

    @Column(name = "speaker_name", length = 200)
    private String speakerName;

    @Column(name = "multi_speaker")
    private Boolean multiSpeaker = false;

    @Column(name = "failed")
    private Boolean failed = false;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "integration_request_id", length = 100)
    private String integrationRequestId;

    @Column(name = "callback_url", length = 500)
    private String callbackUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
