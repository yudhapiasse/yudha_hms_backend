package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.constant.QAStatus;
import com.yudha.hms.radiology.constant.StudyStatus;
import com.yudha.hms.radiology.entity.DicomWorklist;
import com.yudha.hms.radiology.entity.PacsStudy;
import com.yudha.hms.radiology.entity.RadiologyOrder;
import com.yudha.hms.radiology.repository.PacsStudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for PACS Study operations.
 *
 * Handles PACS study management including creation, status updates,
 * quality assurance, archival, and cloud synchronization.
 *
 * Features:
 * - Create study from worklist entry
 * - Generate Study Instance UID
 * - Update study status workflow
 * - Mark acquisition complete
 * - Quality assurance workflow
 * - Archive studies
 * - Cloud PACS synchronization
 * - External viewing management
 * - Query studies by patient, date, status
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PacsStudyService {

    private final PacsStudyRepository studyRepository;

    /**
     * Create a PACS study from worklist entry.
     *
     * @param worklist DICOM worklist entry
     * @param order Radiology order
     * @return Created PACS study
     */
    public PacsStudy createStudy(DicomWorklist worklist, RadiologyOrder order) {
        log.info("Creating PACS study for accession: {}", worklist.getAccessionNumber());

        // Generate Study Instance UID
        String studyInstanceUid = generateStudyInstanceUid();

        PacsStudy study = PacsStudy.builder()
                .order(order)
                .worklist(worklist)
                .studyInstanceUid(studyInstanceUid)
                .accessionNumber(worklist.getAccessionNumber())
                .studyId(worklist.getRequestedProcedureId())
                .patientId(worklist.getPatientId())
                .patientName(worklist.getPatientName())
                .patientBirthDate(worklist.getPatientBirthDate())
                .patientSex(worklist.getPatientSex())
                .studyDate(LocalDate.now())
                .studyTime(LocalTime.now())
                .studyDescription(worklist.getStudyDescription())
                .modalityCode(worklist.getModalityCode())
                .bodyPartExamined(worklist.getBodyPartExamined())
                .referringPhysicianName(null)
                .studyStatus(StudyStatus.IN_PROGRESS)
                .acquisitionComplete(false)
                .archived(false)
                .cloudPacsSynced(false)
                .viewableExternally(false)
                .numberOfSeries(0)
                .numberOfInstances(0)
                .build();

        PacsStudy saved = studyRepository.save(study);
        log.info("PACS study created successfully: {}", saved.getStudyInstanceUid());
        return saved;
    }

    /**
     * Generate Study Instance UID using OID format.
     * Format: 1.2.840.113619.2.X.Y.Z where X.Y.Z are timestamps
     *
     * @return Generated Study Instance UID
     */
    private String generateStudyInstanceUid() {
        long timestamp = System.currentTimeMillis();
        return String.format("1.2.840.113619.2.%d.%d.%d",
                LocalDate.now().toEpochDay(),
                timestamp / 1000,
                timestamp % 1000);
    }

    /**
     * Get study by ID.
     *
     * @param id Study ID
     * @return PACS study
     */
    @Transactional(readOnly = true)
    public PacsStudy getStudyById(UUID id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + id));
    }

    /**
     * Get study by Study Instance UID.
     *
     * @param studyInstanceUid Study Instance UID
     * @return PACS study
     */
    @Transactional(readOnly = true)
    public PacsStudy getStudyByInstanceUid(String studyInstanceUid) {
        return studyRepository.findByStudyInstanceUid(studyInstanceUid)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + studyInstanceUid));
    }

    /**
     * Get study by accession number.
     *
     * @param accessionNumber Accession number
     * @return PACS study
     */
    @Transactional(readOnly = true)
    public PacsStudy getStudyByAccessionNumber(String accessionNumber) {
        return studyRepository.findByAccessionNumber(accessionNumber)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + accessionNumber));
    }

    /**
     * Get study by order ID.
     *
     * @param orderId Order ID
     * @return PACS study
     */
    @Transactional(readOnly = true)
    public PacsStudy getStudyByOrderId(UUID orderId) {
        return studyRepository.findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found for order: " + orderId));
    }

    /**
     * Get studies by patient ID.
     *
     * @param patientId Patient ID
     * @return List of studies
     */
    @Transactional(readOnly = true)
    public List<PacsStudy> getStudiesByPatientId(String patientId) {
        return studyRepository.findByPatientIdAndDeletedAtIsNull(patientId);
    }

    /**
     * Get studies by patient ID with pagination.
     *
     * @param patientId Patient ID
     * @param pageable Pagination
     * @return Page of studies
     */
    @Transactional(readOnly = true)
    public Page<PacsStudy> getStudiesByPatientId(String patientId, Pageable pageable) {
        return studyRepository.findByPatientIdAndDeletedAtIsNull(patientId, pageable);
    }

    /**
     * Get studies by study date.
     *
     * @param studyDate Study date
     * @return List of studies
     */
    @Transactional(readOnly = true)
    public List<PacsStudy> getStudiesByDate(LocalDate studyDate) {
        return studyRepository.findByStudyDateAndDeletedAtIsNull(studyDate);
    }

    /**
     * Get studies by status.
     *
     * @param status Study status
     * @return List of studies
     */
    @Transactional(readOnly = true)
    public List<PacsStudy> getStudiesByStatus(StudyStatus status) {
        return studyRepository.findByStudyStatusAndDeletedAtIsNull(status);
    }

    /**
     * Get studies in date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of studies
     */
    @Transactional(readOnly = true)
    public List<PacsStudy> getStudiesByDateRange(LocalDate startDate, LocalDate endDate) {
        return studyRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Get unarchived studies.
     *
     * @return List of unarchived studies
     */
    @Transactional(readOnly = true)
    public List<PacsStudy> getUnarchivedStudies() {
        return studyRepository.findUnarchived();
    }

    /**
     * Get incomplete studies.
     *
     * @return List of incomplete studies
     */
    @Transactional(readOnly = true)
    public List<PacsStudy> getIncompleteStudies() {
        return studyRepository.findIncompleteStudies();
    }

    /**
     * Get actively shared studies.
     *
     * @return List of actively shared studies
     */
    @Transactional(readOnly = true)
    public List<PacsStudy> getActivelySharedStudies() {
        return studyRepository.findActivelySharedStudies();
    }

    /**
     * Update study status.
     *
     * @param id Study ID
     * @param status New status
     * @return Updated study
     */
    public PacsStudy updateStudyStatus(UUID id, StudyStatus status) {
        log.info("Updating study status: {} to {}", id, status);

        PacsStudy study = getStudyById(id);
        study.setStudyStatus(status);

        return studyRepository.save(study);
    }

    /**
     * Mark acquisition as complete.
     *
     * @param id Study ID
     * @param numberOfSeries Number of series acquired
     * @param numberOfInstances Number of instances acquired
     * @param studySizeMb Study size in MB
     * @return Updated study
     */
    public PacsStudy markAcquisitionComplete(UUID id, Integer numberOfSeries,
                                            Integer numberOfInstances, BigDecimal studySizeMb) {
        log.info("Marking acquisition complete for study: {}", id);

        PacsStudy study = getStudyById(id);
        study.setAcquisitionComplete(true);
        study.setAcquisitionCompletedAt(LocalDateTime.now());
        study.setNumberOfSeries(numberOfSeries);
        study.setNumberOfInstances(numberOfInstances);
        study.setStudySizeMb(studySizeMb);
        study.setStudyStatus(StudyStatus.ACQUISITION_COMPLETE);

        return studyRepository.save(study);
    }

    /**
     * Perform quality assurance on study.
     *
     * @param id Study ID
     * @param qaStatus QA status
     * @param qaPerformedBy User ID who performed QA
     * @param qaNotes QA notes
     * @return Updated study
     */
    public PacsStudy performQualityAssurance(UUID id, QAStatus qaStatus,
                                            UUID qaPerformedBy, String qaNotes) {
        log.info("Performing QA on study: {} with status: {}", id, qaStatus);

        PacsStudy study = getStudyById(id);
        study.setQaStatus(qaStatus);
        study.setQaPerformedBy(qaPerformedBy);
        study.setQaPerformedAt(LocalDateTime.now());
        study.setQaNotes(qaNotes);

        return studyRepository.save(study);
    }

    /**
     * Archive study.
     *
     * @param id Study ID
     * @param archivalRuleId Archival rule ID
     * @return Updated study
     */
    public PacsStudy archiveStudy(UUID id, UUID archivalRuleId) {
        log.info("Archiving study: {}", id);

        PacsStudy study = getStudyById(id);
        study.setArchived(true);
        study.setArchivedAt(LocalDateTime.now());
        study.setArchivalRuleId(archivalRuleId);
        study.setStudyStatus(StudyStatus.ARCHIVED);

        return studyRepository.save(study);
    }

    /**
     * Mark study as synced to cloud PACS.
     *
     * @param id Study ID
     * @param cloudPacsUrl Cloud PACS URL
     * @return Updated study
     */
    public PacsStudy markCloudPacsSynced(UUID id, String cloudPacsUrl) {
        log.info("Marking study as cloud PACS synced: {}", id);

        PacsStudy study = getStudyById(id);
        study.setCloudPacsSynced(true);
        study.setCloudPacsSyncedAt(LocalDateTime.now());
        study.setCloudPacsUrl(cloudPacsUrl);

        return studyRepository.save(study);
    }

    /**
     * Enable external viewing for study.
     *
     * @param id Study ID
     * @param shareExpiresAt Share expiration time
     * @return Updated study
     */
    public PacsStudy enableExternalViewing(UUID id, LocalDateTime shareExpiresAt) {
        log.info("Enabling external viewing for study: {}", id);

        PacsStudy study = getStudyById(id);
        study.setViewableExternally(true);
        study.setShareExpiresAt(shareExpiresAt);

        return studyRepository.save(study);
    }

    /**
     * Disable external viewing for study.
     *
     * @param id Study ID
     * @return Updated study
     */
    public PacsStudy disableExternalViewing(UUID id) {
        log.info("Disabling external viewing for study: {}", id);

        PacsStudy study = getStudyById(id);
        study.setViewableExternally(false);
        study.setShareExpiresAt(null);

        return studyRepository.save(study);
    }

    /**
     * Update study metadata (series, instances, size).
     *
     * @param id Study ID
     * @param numberOfSeries Number of series
     * @param numberOfInstances Number of instances
     * @param studySizeMb Study size in MB
     * @return Updated study
     */
    public PacsStudy updateStudyMetadata(UUID id, Integer numberOfSeries,
                                        Integer numberOfInstances, BigDecimal studySizeMb) {
        log.info("Updating study metadata: {}", id);

        PacsStudy study = getStudyById(id);
        study.setNumberOfSeries(numberOfSeries);
        study.setNumberOfInstances(numberOfInstances);
        study.setStudySizeMb(studySizeMb);

        return studyRepository.save(study);
    }

    /**
     * Get study count by status.
     *
     * @param status Study status
     * @return Count of studies
     */
    @Transactional(readOnly = true)
    public long getStudyCountByStatus(StudyStatus status) {
        return studyRepository.countByStudyStatusAndDeletedAtIsNull(status);
    }

    /**
     * Soft delete study.
     *
     * @param id Study ID
     */
    public void deleteStudy(UUID id) {
        log.info("Deleting study: {}", id);

        PacsStudy study = getStudyById(id);
        study.setDeletedAt(LocalDateTime.now());
        studyRepository.save(study);
    }
}
