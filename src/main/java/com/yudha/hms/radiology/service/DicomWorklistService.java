package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.constant.WorklistStatus;
import com.yudha.hms.radiology.entity.DicomWorklist;
import com.yudha.hms.radiology.entity.RadiologyOrder;
import com.yudha.hms.radiology.entity.RadiologyOrderItem;
import com.yudha.hms.radiology.repository.DicomWorklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for DICOM Modality Worklist operations.
 *
 * Handles DICOM MWL integration for imaging equipment.
 * Provides worklist entries for modalities to query scheduled procedures.
 *
 * Features:
 * - Create worklist entry from radiology order
 * - Generate accession number (ACC + YYYYMMDD + 6-digit sequence)
 * - Send worklist to modality
 * - Update worklist status workflow
 * - Query worklists by date, modality, status
 * - Mark worklist as acknowledged by modality
 * - Cancel worklist entries
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DicomWorklistService {

    private final DicomWorklistRepository worklistRepository;

    /**
     * Create a DICOM worklist entry from radiology order.
     *
     * @param order Radiology order
     * @param orderItem Order item (examination)
     * @param scheduledDate Scheduled procedure date
     * @param scheduledTime Scheduled procedure time
     * @param modalityAeTitle AE Title of the modality
     * @return Created worklist entry
     */
    public DicomWorklist createWorklistEntry(RadiologyOrder order, RadiologyOrderItem orderItem,
                                             LocalDate scheduledDate, LocalTime scheduledTime,
                                             String modalityAeTitle) {
        log.info("Creating DICOM worklist entry for order: {}", order.getOrderNumber());

        // Generate accession number
        String accessionNumber = generateAccessionNumber();

        DicomWorklist worklist = DicomWorklist.builder()
                .order(order)
                .orderItem(orderItem)
                .patientId(order.getPatient().getId().toString())
                .patientName(order.getPatient().getFullName())
                .patientBirthDate(order.getPatient().getBirthDate())
                .patientSex(order.getPatient().getGender() != null ?
                        order.getPatient().getGender().name().substring(0, 1) : null)
                .accessionNumber(accessionNumber)
                .modalityCode(orderItem.getExamination().getModality().getCode())
                .scheduledStationAeTitle(modalityAeTitle)
                .scheduledProcedureStepStartDate(scheduledDate)
                .scheduledProcedureStepStartTime(scheduledTime)
                .scheduledProcedureStepDescription(orderItem.getExamination().getExamName())
                .requestedProcedureId(orderItem.getId().toString())
                .requestedProcedureDescription(orderItem.getExamination().getExamName())
                .studyDescription(orderItem.getExamination().getExamName())
                .worklistStatus(WorklistStatus.PENDING)
                .sentToModality(false)
                .acknowledgedByModality(false)
                .build();

        DicomWorklist saved = worklistRepository.save(worklist);
        log.info("Worklist entry created successfully: {}", saved.getAccessionNumber());
        return saved;
    }

    /**
     * Generate accession number with format: ACC + YYYYMMDD + 6-digit sequence.
     *
     * @return Generated accession number
     */
    private String generateAccessionNumber() {
        String today = LocalDate.now().toString().replace("-", "");
        String lastAccession = worklistRepository.findTopByOrderByCreatedAtDesc()
                .map(DicomWorklist::getAccessionNumber)
                .orElse("ACC000000000000000");

        int sequence = 1;
        if (lastAccession.startsWith("ACC" + today)) {
            String lastSeq = lastAccession.substring(11);
            sequence = Integer.parseInt(lastSeq) + 1;
        }

        return String.format("ACC%s%06d", today, sequence);
    }

    /**
     * Get worklist entry by ID.
     *
     * @param id Worklist ID
     * @return Worklist entry
     */
    @Transactional(readOnly = true)
    public DicomWorklist getWorklistById(UUID id) {
        return worklistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Worklist not found: " + id));
    }

    /**
     * Get worklist entry by accession number.
     *
     * @param accessionNumber Accession number
     * @return Worklist entry
     */
    @Transactional(readOnly = true)
    public DicomWorklist getWorklistByAccessionNumber(String accessionNumber) {
        return worklistRepository.findByAccessionNumber(accessionNumber)
                .orElseThrow(() -> new IllegalArgumentException("Worklist not found: " + accessionNumber));
    }

    /**
     * Get worklist entries by order ID.
     *
     * @param orderId Order ID
     * @return Worklist entry
     */
    @Transactional(readOnly = true)
    public DicomWorklist getWorklistByOrderId(UUID orderId) {
        return worklistRepository.findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Worklist not found for order: " + orderId));
    }

    /**
     * Get worklist entries by status.
     *
     * @param status Worklist status
     * @return List of worklist entries
     */
    @Transactional(readOnly = true)
    public List<DicomWorklist> getWorklistsByStatus(WorklistStatus status) {
        return worklistRepository.findByWorklistStatusAndDeletedAtIsNull(status);
    }

    /**
     * Get worklist entries scheduled for a specific date.
     *
     * @param date Scheduled date
     * @return List of worklist entries
     */
    @Transactional(readOnly = true)
    public List<DicomWorklist> getWorklistsByScheduledDate(LocalDate date) {
        return worklistRepository.findByScheduledDate(date);
    }

    /**
     * Get worklist entries for a specific modality and date.
     *
     * @param modality Modality code
     * @param date Scheduled date
     * @return List of worklist entries
     */
    @Transactional(readOnly = true)
    public List<DicomWorklist> getWorklistsByModalityAndDate(String modality, LocalDate date) {
        return worklistRepository.findByModalityAndDate(modality, date);
    }

    /**
     * Get pending worklist items that need to be sent to modality.
     *
     * @return List of pending worklist entries
     */
    @Transactional(readOnly = true)
    public List<DicomWorklist> getPendingWorklistItems() {
        return worklistRepository.findPendingWorklistItems();
    }

    /**
     * Update worklist status.
     *
     * @param id Worklist ID
     * @param status New status
     * @return Updated worklist
     */
    public DicomWorklist updateWorklistStatus(UUID id, WorklistStatus status) {
        log.info("Updating worklist status: {} to {}", id, status);

        DicomWorklist worklist = getWorklistById(id);
        worklist.setWorklistStatus(status);

        if (status == WorklistStatus.IN_PROGRESS) {
            worklist.setActualProcedureStepStartDate(LocalDate.now());
            worklist.setActualProcedureStepStartTime(LocalTime.now());
        } else if (status == WorklistStatus.COMPLETED) {
            worklist.setActualProcedureStepEndDate(LocalDate.now());
            worklist.setActualProcedureStepEndTime(LocalTime.now());
        }

        return worklistRepository.save(worklist);
    }

    /**
     * Mark worklist as sent to modality.
     *
     * @param id Worklist ID
     * @return Updated worklist
     */
    public DicomWorklist markAsSentToModality(UUID id) {
        log.info("Marking worklist as sent to modality: {}", id);

        DicomWorklist worklist = getWorklistById(id);
        worklist.setSentToModality(true);
        worklist.setSentToModalityAt(LocalDateTime.now());

        if (worklist.getWorklistStatus() == WorklistStatus.PENDING) {
            worklist.setWorklistStatus(WorklistStatus.SENT_TO_MODALITY);
        }

        return worklistRepository.save(worklist);
    }

    /**
     * Mark worklist as acknowledged by modality.
     *
     * @param accessionNumber Accession number
     * @return Updated worklist
     */
    public DicomWorklist markAsAcknowledgedByModality(String accessionNumber) {
        log.info("Marking worklist as acknowledged by modality: {}", accessionNumber);

        DicomWorklist worklist = getWorklistByAccessionNumber(accessionNumber);
        worklist.setAcknowledgedByModality(true);
        worklist.setAcknowledgedAt(LocalDateTime.now());
        worklist.setWorklistStatus(WorklistStatus.SCHEDULED);

        return worklistRepository.save(worklist);
    }

    /**
     * Schedule worklist entry.
     *
     * @param id Worklist ID
     * @param scheduledDate New scheduled date
     * @param scheduledTime New scheduled time
     * @return Updated worklist
     */
    public DicomWorklist scheduleWorklist(UUID id, LocalDate scheduledDate, LocalTime scheduledTime) {
        log.info("Scheduling worklist: {} for {} at {}", id, scheduledDate, scheduledTime);

        DicomWorklist worklist = getWorklistById(id);
        worklist.setScheduledProcedureStepStartDate(scheduledDate);
        worklist.setScheduledProcedureStepStartTime(scheduledTime);
        worklist.setWorklistStatus(WorklistStatus.SCHEDULED);

        return worklistRepository.save(worklist);
    }

    /**
     * Cancel worklist entry.
     *
     * @param id Worklist ID
     * @param reason Cancellation reason
     * @return Updated worklist
     */
    public DicomWorklist cancelWorklist(UUID id, String reason) {
        log.info("Cancelling worklist: {} with reason: {}", id, reason);

        DicomWorklist worklist = getWorklistById(id);
        worklist.setWorklistStatus(WorklistStatus.CANCELLED);
        worklist.setCancellationReason(reason);
        worklist.setCancelledAt(LocalDateTime.now());

        return worklistRepository.save(worklist);
    }

    /**
     * Get worklist count by status.
     *
     * @param status Worklist status
     * @return Count of worklists
     */
    @Transactional(readOnly = true)
    public long getWorklistCountByStatus(WorklistStatus status) {
        return worklistRepository.countByWorklistStatusAndDeletedAtIsNull(status);
    }

    /**
     * Soft delete worklist entry.
     *
     * @param id Worklist ID
     */
    public void deleteWorklist(UUID id) {
        log.info("Deleting worklist: {}", id);

        DicomWorklist worklist = getWorklistById(id);
        worklist.setDeletedAt(LocalDateTime.now());
        worklistRepository.save(worklist);
    }
}
