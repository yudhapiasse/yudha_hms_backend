package com.yudha.hms.registration.service.outpatient;

import com.yudha.hms.registration.entity.Polyclinic;
import com.yudha.hms.registration.entity.QueueSequence;
import com.yudha.hms.registration.repository.PolyclinicRepository;
import com.yudha.hms.registration.repository.QueueSequenceRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Service for queue number generation and management.
 * Handles sequential queue numbering per polyclinic per day.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

    private final QueueSequenceRepository queueSequenceRepository;
    private final PolyclinicRepository polyclinicRepository;

    /**
     * Generate next queue number for a polyclinic on a specific date.
     * Thread-safe implementation using pessimistic locking.
     *
     * @param polyclinicId polyclinic ID
     * @param date queue date
     * @return queue code (e.g., "A001", "UM015")
     */
    @Transactional
    public String generateQueueNumber(UUID polyclinicId, LocalDate date) {
        log.info("Generating queue number for polyclinic {} on {}", polyclinicId, date);

        // Get or create queue sequence with pessimistic lock
        QueueSequence sequence = queueSequenceRepository
            .findByPolyclinicIdAndQueueDateWithLock(polyclinicId, date)
            .orElseGet(() -> createNewQueueSequence(polyclinicId, date));

        // Get next queue number
        String queueCode = sequence.getNextQueueNumber();

        // Save updated sequence
        queueSequenceRepository.save(sequence);

        log.info("Generated queue number: {} (sequence: {})", queueCode, sequence.getLastQueueNumber());

        return queueCode;
    }

    /**
     * Get current queue number for a polyclinic today.
     *
     * @param polyclinicId polyclinic ID
     * @return current queue number or 0 if no queues yet
     */
    public Integer getCurrentQueueNumber(UUID polyclinicId) {
        return getCurrentQueueNumber(polyclinicId, LocalDate.now());
    }

    /**
     * Get current queue number for a polyclinic on a specific date.
     *
     * @param polyclinicId polyclinic ID
     * @param date queue date
     * @return current queue number or 0 if no queues yet
     */
    public Integer getCurrentQueueNumber(UUID polyclinicId, LocalDate date) {
        return queueSequenceRepository
            .getCurrentQueueNumber(polyclinicId, date)
            .orElse(0);
    }

    /**
     * Get current queue code for a polyclinic today.
     *
     * @param polyclinicId polyclinic ID
     * @return current queue code or null if no queues yet
     */
    public String getCurrentQueueCode(UUID polyclinicId) {
        return getCurrentQueueCode(polyclinicId, LocalDate.now());
    }

    /**
     * Get current queue code for a polyclinic on a specific date.
     *
     * @param polyclinicId polyclinic ID
     * @param date queue date
     * @return current queue code or null if no queues yet
     */
    public String getCurrentQueueCode(UUID polyclinicId, LocalDate date) {
        return queueSequenceRepository
            .findByPolyclinicIdAndQueueDate(polyclinicId, date)
            .map(QueueSequence::getCurrentQueueCode)
            .orElse(null);
    }

    /**
     * Reset queue sequence for a polyclinic on a specific date.
     * USE WITH CAUTION - This will reset the queue counter to 0.
     *
     * @param polyclinicId polyclinic ID
     * @param date queue date
     */
    @Transactional
    public void resetQueue(UUID polyclinicId, LocalDate date) {
        log.warn("Resetting queue for polyclinic {} on {}", polyclinicId, date);

        queueSequenceRepository
            .findByPolyclinicIdAndQueueDate(polyclinicId, date)
            .ifPresent(sequence -> {
                sequence.reset();
                queueSequenceRepository.save(sequence);
                log.info("Queue reset successfully for polyclinic {} on {}", polyclinicId, date);
            });
    }

    /**
     * Clean up old queue sequences (older than specified date).
     *
     * @param beforeDate delete sequences before this date
     */
    @Transactional
    public void cleanupOldQueues(LocalDate beforeDate) {
        log.info("Cleaning up queue sequences before {}", beforeDate);
        queueSequenceRepository.deleteOldQueueSequences(beforeDate);
        log.info("Old queue sequences cleaned up successfully");
    }

    /**
     * Get queue prefix for a polyclinic.
     * Maps polyclinic code to queue prefix.
     *
     * @param polyclinicCode polyclinic code
     * @return queue prefix
     */
    private String getQueuePrefix(String polyclinicCode) {
        // Map polyclinic codes to prefixes
        switch (polyclinicCode) {
            case "POLI-UMUM":
                return "UM";
            case "POLI-ANAK":
                return "AN";
            case "POLI-KAND":
                return "KD";
            case "POLI-GIGI":
                return "GG";
            case "POLI-MATA":
                return "MT";
            case "POLI-THT":
                return "TH";
            case "POLI-JANT":
                return "JT";
            case "POLI-PENY":
                return "PD";
            default:
                // Use first letter of code
                return polyclinicCode.substring(polyclinicCode.lastIndexOf("-") + 1, polyclinicCode.lastIndexOf("-") + 2);
        }
    }

    /**
     * Create new queue sequence for a polyclinic on a specific date.
     *
     * @param polyclinicId polyclinic ID
     * @param date queue date
     * @return new queue sequence
     */
    private QueueSequence createNewQueueSequence(UUID polyclinicId, LocalDate date) {
        log.info("Creating new queue sequence for polyclinic {} on {}", polyclinicId, date);

        Polyclinic polyclinic = polyclinicRepository.findById(polyclinicId)
            .orElseThrow(() -> new ResourceNotFoundException("Polyclinic", "ID", polyclinicId));

        String prefix = getQueuePrefix(polyclinic.getCode());

        QueueSequence sequence = QueueSequence.builder()
            .polyclinic(polyclinic)
            .queueDate(date)
            .lastQueueNumber(0)
            .prefix(prefix)
            .build();

        return queueSequenceRepository.save(sequence);
    }
}