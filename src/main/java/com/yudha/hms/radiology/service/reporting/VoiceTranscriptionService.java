package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.constant.reporting.TranscriptionStatus;
import com.yudha.hms.radiology.entity.reporting.VoiceTranscription;
import com.yudha.hms.radiology.repository.reporting.VoiceTranscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceTranscriptionService {

    private final VoiceTranscriptionRepository voiceTranscriptionRepository;

    @Transactional
    public VoiceTranscription createTranscription(VoiceTranscription transcription) {
        log.info("Creating voice transcription for report: {}", transcription.getReportId());
        
        Optional<VoiceTranscription> existing = voiceTranscriptionRepository.findByReportId(transcription.getReportId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Transcription already exists for report: " + transcription.getReportId());
        }
        
        transcription.setTranscriptionStatus(TranscriptionStatus.PENDING);
        
        return voiceTranscriptionRepository.save(transcription);
    }

    @Transactional(readOnly = true)
    public VoiceTranscription getTranscriptionById(UUID id) {
        return voiceTranscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transcription not found: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<VoiceTranscription> getTranscriptionByReport(UUID reportId) {
        return voiceTranscriptionRepository.findByReportId(reportId);
    }

    @Transactional(readOnly = true)
    public List<VoiceTranscription> getTranscriptionsByStatus(TranscriptionStatus status) {
        return voiceTranscriptionRepository.findByTranscriptionStatus(status);
    }

    @Transactional(readOnly = true)
    public List<VoiceTranscription> getTranscriptionsRequiringEditing() {
        return voiceTranscriptionRepository.findByRequiresEditingTrue();
    }

    @Transactional(readOnly = true)
    public List<VoiceTranscription> getFailedTranscriptions() {
        return voiceTranscriptionRepository.findByFailedTrue();
    }

    @Transactional(readOnly = true)
    public List<VoiceTranscription> getFailedWithRetryAvailable() {
        return voiceTranscriptionRepository.findFailedWithRetryAvailable();
    }

    @Transactional(readOnly = true)
    public List<VoiceTranscription> getTranscriptionsByStatusAndDateRange(
            TranscriptionStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return voiceTranscriptionRepository.findByStatusAndDateRange(status, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<VoiceTranscription> getTranscriptionsByEditor(UUID editorId) {
        return voiceTranscriptionRepository.findByEditedBy(editorId);
    }

    @Transactional(readOnly = true)
    public Double getAverageProcessingTime() {
        return voiceTranscriptionRepository.findAverageProcessingTime();
    }

    @Transactional(readOnly = true)
    public long countTranscriptionsByStatus(TranscriptionStatus status) {
        return voiceTranscriptionRepository.countByStatus(status);
    }

    @Transactional
    public VoiceTranscription startTranscription(UUID id) {
        log.info("Starting transcription: {}", id);
        
        VoiceTranscription transcription = getTranscriptionById(id);
        transcription.setTranscriptionStatus(TranscriptionStatus.PROCESSING);
        transcription.setStartedAt(LocalDateTime.now());
        
        return voiceTranscriptionRepository.save(transcription);
    }

    @Transactional
    public VoiceTranscription completeTranscription(UUID id, String rawTranscription) {
        log.info("Completing transcription: {}", id);
        
        VoiceTranscription transcription = getTranscriptionById(id);
        transcription.setTranscriptionStatus(TranscriptionStatus.COMPLETED);
        transcription.setRawTranscription(rawTranscription);
        transcription.setCompletedAt(LocalDateTime.now());
        
        if (transcription.getStartedAt() != null) {
            long processingSeconds = java.time.Duration.between(
                    transcription.getStartedAt(), 
                    transcription.getCompletedAt()
            ).getSeconds();
            transcription.setProcessingTimeSeconds((int) processingSeconds);
        }
        
        return voiceTranscriptionRepository.save(transcription);
    }

    @Transactional
    public VoiceTranscription editTranscription(UUID id, String editedTranscription, UUID editedBy) {
        log.info("Editing transcription: {}", id);
        
        VoiceTranscription transcription = getTranscriptionById(id);
        transcription.setEditedTranscription(editedTranscription);
        transcription.setEditedBy(editedBy);
        transcription.setEditedAt(LocalDateTime.now());
        transcription.setEditCount(transcription.getEditCount() + 1);
        
        return voiceTranscriptionRepository.save(transcription);
    }

    @Transactional
    public VoiceTranscription markAsFailed(UUID id, String errorMessage) {
        log.info("Marking transcription as failed: {}", id);
        
        VoiceTranscription transcription = getTranscriptionById(id);
        transcription.setTranscriptionStatus(TranscriptionStatus.FAILED);
        transcription.setFailed(true);
        transcription.setErrorMessage(errorMessage);
        
        return voiceTranscriptionRepository.save(transcription);
    }

    @Transactional
    public VoiceTranscription retryTranscription(UUID id) {
        log.info("Retrying transcription: {}", id);
        
        VoiceTranscription transcription = getTranscriptionById(id);
        
        if (transcription.getRetryCount() >= 3) {
            throw new IllegalStateException("Maximum retry attempts exceeded");
        }
        
        transcription.setRetryCount(transcription.getRetryCount() + 1);
        transcription.setTranscriptionStatus(TranscriptionStatus.PENDING);
        transcription.setFailed(false);
        transcription.setErrorMessage(null);
        
        return voiceTranscriptionRepository.save(transcription);
    }

    @Transactional
    public void deleteTranscription(UUID id) {
        log.info("Soft deleting transcription: {}", id);
        VoiceTranscription transcription = getTranscriptionById(id);
        transcription.setDeletedAt(LocalDateTime.now());
        voiceTranscriptionRepository.save(transcription);
    }
}
