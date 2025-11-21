package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.constant.ContrastType;
import com.yudha.hms.radiology.constant.ReactionSeverity;
import com.yudha.hms.radiology.entity.ContrastAdministration;
import com.yudha.hms.radiology.entity.RadiologyOrderItem;
import com.yudha.hms.radiology.repository.ContrastAdministrationRepository;
import com.yudha.hms.radiology.repository.RadiologyOrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Contrast Administration operations.
 *
 * Handles CRUD operations and business logic for contrast media administration.
 *
 * Features:
 * - Record contrast administration
 * - Track contrast batch
 * - Record adverse reactions
 * - Update reaction severity
 * - Get patients with contrast reactions
 * - Get contrast usage statistics
 * - Alert for patients with previous reactions
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContrastAdministrationService {

    private final ContrastAdministrationRepository contrastRepository;
    private final RadiologyOrderItemRepository orderItemRepository;

    /**
     * Record a new contrast administration.
     *
     * @param administration Contrast administration to record
     * @return Created administration record
     * @throws IllegalArgumentException if order item not found
     */
    public ContrastAdministration recordAdministration(ContrastAdministration administration) {
        log.info("Recording contrast administration for order item: {}", administration.getOrderItem().getId());

        // Validate order item exists
        RadiologyOrderItem orderItem = orderItemRepository.findById(administration.getOrderItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + administration.getOrderItem().getId()));

        // Set patient from order
        if (administration.getPatient() == null) {
            administration.setPatient(orderItem.getOrder().getPatient());
        }

        // Set defaults
        if (administration.getReactionObserved() == null) {
            administration.setReactionObserved(false);
        }
        if (administration.getAdministeredAt() == null) {
            administration.setAdministeredAt(LocalDateTime.now());
        }

        ContrastAdministration saved = contrastRepository.save(administration);
        log.info("Contrast administration recorded successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Record a contrast administration with all details.
     *
     * @param orderItemId Order item ID
     * @param contrastName Contrast name
     * @param contrastType Contrast type
     * @param volumeMl Volume in ml
     * @param batchNumber Batch number
     * @param administeredBy User ID who administered
     * @return Created administration record
     */
    public ContrastAdministration recordAdministration(UUID orderItemId, String contrastName,
                                                        ContrastType contrastType, BigDecimal volumeMl,
                                                        String batchNumber, UUID administeredBy) {
        log.info("Recording contrast administration for order item: {}", orderItemId);

        RadiologyOrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        ContrastAdministration administration = ContrastAdministration.builder()
                .orderItem(orderItem)
                .patient(orderItem.getOrder().getPatient())
                .contrastName(contrastName)
                .contrastType(contrastType)
                .volumeMl(volumeMl)
                .batchNumber(batchNumber)
                .administeredBy(administeredBy)
                .administeredAt(LocalDateTime.now())
                .reactionObserved(false)
                .build();

        ContrastAdministration saved = contrastRepository.save(administration);
        log.info("Contrast administration recorded successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Record an adverse reaction to contrast.
     *
     * @param administrationId Administration ID
     * @param reactionSeverity Reaction severity
     * @param reactionDescription Description of reaction
     * @param treatmentGiven Treatment provided
     * @return Updated administration record
     * @throws IllegalArgumentException if administration not found
     */
    public ContrastAdministration recordReaction(UUID administrationId, ReactionSeverity reactionSeverity,
                                                  String reactionDescription, String treatmentGiven) {
        log.info("Recording contrast reaction for administration: {} - Severity: {}", administrationId, reactionSeverity);

        ContrastAdministration administration = getAdministrationById(administrationId);

        administration.setReactionObserved(true);
        administration.setReactionSeverity(reactionSeverity);
        administration.setReactionDescription(reactionDescription);
        administration.setTreatmentGiven(treatmentGiven);

        ContrastAdministration updated = contrastRepository.save(administration);

        // Log warning for severe reactions
        if (reactionSeverity == ReactionSeverity.SEVERE) {
            log.warn("SEVERE contrast reaction recorded for patient: {} - Administration: {}",
                    administration.getPatient().getId(), administrationId);
        }

        log.info("Contrast reaction recorded successfully");
        return updated;
    }

    /**
     * Update reaction severity.
     *
     * @param administrationId Administration ID
     * @param newSeverity New severity level
     * @return Updated administration record
     * @throws IllegalArgumentException if administration not found
     * @throws IllegalStateException if no reaction was observed
     */
    public ContrastAdministration updateReactionSeverity(UUID administrationId, ReactionSeverity newSeverity) {
        log.info("Updating reaction severity for administration: {} to {}", administrationId, newSeverity);

        ContrastAdministration administration = getAdministrationById(administrationId);

        if (!administration.getReactionObserved()) {
            throw new IllegalStateException("Cannot update severity - no reaction was observed: " + administrationId);
        }

        ReactionSeverity previousSeverity = administration.getReactionSeverity();
        administration.setReactionSeverity(newSeverity);

        ContrastAdministration updated = contrastRepository.save(administration);
        log.info("Reaction severity updated: {} → {}", previousSeverity, newSeverity);
        return updated;
    }

    /**
     * Get administration by ID.
     *
     * @param id Administration ID
     * @return Administration record
     * @throws IllegalArgumentException if administration not found
     */
    @Transactional(readOnly = true)
    public ContrastAdministration getAdministrationById(UUID id) {
        return contrastRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrast administration not found with ID: " + id));
    }

    /**
     * Get administration by order item.
     *
     * @param orderItemId Order item ID
     * @return Administration record if exists
     */
    @Transactional(readOnly = true)
    public ContrastAdministration getAdministrationByOrderItem(UUID orderItemId) {
        return contrastRepository.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Contrast administration not found for order item: " + orderItemId));
    }

    /**
     * Get patient's contrast administration history.
     * Useful for checking previous reactions before administering contrast.
     *
     * @param patientId Patient ID
     * @return List of administrations
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getPatientContrastHistory(UUID patientId) {
        return contrastRepository.findPatientContrastHistory(patientId);
    }

    /**
     * Check if patient has previous contrast reactions.
     * Important safety check before administering contrast.
     *
     * @param patientId Patient ID
     * @return True if patient has history of reactions
     */
    @Transactional(readOnly = true)
    public boolean patientHasPreviousReactions(UUID patientId) {
        List<ContrastAdministration> history = getPatientContrastHistory(patientId);
        return history.stream().anyMatch(ContrastAdministration::getReactionObserved);
    }

    /**
     * Get patient's previous reactions.
     *
     * @param patientId Patient ID
     * @return List of administrations with reactions
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getPatientPreviousReactions(UUID patientId) {
        List<ContrastAdministration> history = getPatientContrastHistory(patientId);
        return history.stream()
                .filter(ContrastAdministration::getReactionObserved)
                .toList();
    }

    /**
     * Get administrations by contrast type.
     *
     * @param contrastType Contrast type
     * @return List of administrations
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getAdministrationsByType(ContrastType contrastType) {
        return contrastRepository.findByContrastType(contrastType);
    }

    /**
     * Get administrations by batch number.
     * Useful for tracking adverse events related to specific batches.
     *
     * @param batchNumber Batch number
     * @return List of administrations
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getAdministrationsByBatch(String batchNumber) {
        return contrastRepository.findByBatchNumber(batchNumber);
    }

    /**
     * Get all administrations with reactions.
     *
     * @return List of administrations with reactions
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getAdministrationsWithReactions() {
        return contrastRepository.findAdministrationsWithReactions();
    }

    /**
     * Get administrations by reaction severity.
     *
     * @param reactionSeverity Reaction severity
     * @return List of administrations
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getAdministrationsByReactionSeverity(ReactionSeverity reactionSeverity) {
        return contrastRepository.findByReactionSeverity(reactionSeverity);
    }

    /**
     * Get severe contrast reactions.
     * Important for safety monitoring and reporting.
     *
     * @return List of administrations with severe reactions
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getSevereReactions() {
        return contrastRepository.findSevereReactions();
    }

    /**
     * Get administrations by date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of administrations
     */
    @Transactional(readOnly = true)
    public Page<ContrastAdministration> getAdministrationsByDateRange(LocalDateTime startDate,
                                                                       LocalDateTime endDate, Pageable pageable) {
        return contrastRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Get administrations by user.
     *
     * @param userId User ID
     * @return List of administrations
     */
    @Transactional(readOnly = true)
    public List<ContrastAdministration> getAdministrationsByUser(UUID userId) {
        return contrastRepository.findByAdministeredByOrderByAdministeredAtDesc(userId);
    }

    /**
     * Get contrast usage statistics.
     * Returns count of administrations by contrast type.
     *
     * @param contrastType Contrast type
     * @return Usage count
     */
    @Transactional(readOnly = true)
    public long getContrastUsageCount(ContrastType contrastType) {
        return contrastRepository.countByContrastType(contrastType);
    }

    /**
     * Get total reaction count.
     *
     * @return Count of administrations with reactions
     */
    @Transactional(readOnly = true)
    public long getReactionCount() {
        return contrastRepository.countByReactionObservedTrue();
    }

    /**
     * Calculate reaction rate for a contrast type.
     *
     * @param contrastType Contrast type
     * @return Reaction rate as percentage
     */
    @Transactional(readOnly = true)
    public double getReactionRate(ContrastType contrastType) {
        List<ContrastAdministration> administrations = getAdministrationsByType(contrastType);
        if (administrations.isEmpty()) {
            return 0.0;
        }

        long reactionCount = administrations.stream()
                .filter(ContrastAdministration::getReactionObserved)
                .count();

        return (reactionCount * 100.0) / administrations.size();
    }

    /**
     * Check batch safety.
     * Returns true if batch has high reaction rate (>5%).
     *
     * @param batchNumber Batch number
     * @return True if batch has safety concerns
     */
    @Transactional(readOnly = true)
    public boolean checkBatchSafety(String batchNumber) {
        List<ContrastAdministration> administrations = getAdministrationsByBatch(batchNumber);
        if (administrations.size() < 10) {
            return false;  // Not enough data
        }

        long reactionCount = administrations.stream()
                .filter(ContrastAdministration::getReactionObserved)
                .count();

        double reactionRate = (reactionCount * 100.0) / administrations.size();
        return reactionRate > 5.0;
    }

    /**
     * Alert for patient with previous reactions.
     * Returns warning message if patient has history of reactions.
     *
     * @param patientId Patient ID
     * @return Alert message if applicable, null otherwise
     */
    @Transactional(readOnly = true)
    public String getPatientReactionAlert(UUID patientId) {
        List<ContrastAdministration> reactions = getPatientPreviousReactions(patientId);

        if (reactions.isEmpty()) {
            return null;
        }

        // Check for severe reactions
        long severeCount = reactions.stream()
                .filter(r -> r.getReactionSeverity() == ReactionSeverity.SEVERE)
                .count();

        if (severeCount > 0) {
            return String.format("ALERT: Patient has %d SEVERE contrast reaction(s) in history. " +
                    "Consult with radiologist before administering contrast.", severeCount);
        }

        return String.format("WARNING: Patient has %d previous contrast reaction(s). " +
                "Review history and consider premedication.", reactions.size());
    }
}
