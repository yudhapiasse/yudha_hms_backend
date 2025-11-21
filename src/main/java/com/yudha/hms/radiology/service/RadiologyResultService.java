package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.entity.*;
import com.yudha.hms.radiology.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for Radiology Result operations.
 *
 * Handles CRUD operations and business logic for radiology results/reports.
 * Complex service with result management, reporting workflow, and amendment tracking.
 *
 * Features:
 * - Create result with auto-numbering (RR + YYYYMMDD + 6-digit sequence)
 * - Enter findings, impression, recommendations
 * - Attach DICOM study
 * - Add images to result
 * - Radiologist review and finalization
 * - Amend result with reason
 * - Cancel result
 * - Get results by patient
 * - Get results awaiting reporting
 * - Get finalized results
 * - Search results with criteria
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RadiologyResultService {

    private final RadiologyResultRepository resultRepository;
    private final RadiologyOrderItemRepository orderItemRepository;
    private final RadiologyExaminationRepository examinationRepository;
    private final RadiologyImageRepository imageRepository;

    /**
     * Create a new radiology result.
     *
     * @param orderItemId Order item ID
     * @param performedByTechnicianId Technician ID
     * @return Created result
     * @throws IllegalArgumentException if order item not found
     */
    public RadiologyResult createResult(UUID orderItemId, UUID performedByTechnicianId) {
        log.info("Creating new radiology result for order item: {}", orderItemId);

        // Validate order item exists
        RadiologyOrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        // Check if result already exists for this order item
        if (resultRepository.findByOrderItemIdAndDeletedAtIsNull(orderItemId).isPresent()) {
            throw new IllegalStateException("Result already exists for order item: " + orderItemId);
        }

        // Generate result number
        String resultNumber = generateResultNumber();

        // Create result
        RadiologyResult result = RadiologyResult.builder()
                .resultNumber(resultNumber)
                .orderItem(orderItem)
                .examination(orderItem.getExamination())
                .patient(orderItem.getOrder().getPatient())
                .performedDate(LocalDateTime.now())
                .performedByTechnicianId(performedByTechnicianId)
                .isFinalized(false)
                .isAmended(false)
                .imageCount(0)
                .build();

        RadiologyResult saved = resultRepository.save(result);

        // Update order item with result ID
        orderItem.setResultId(saved.getId());
        orderItemRepository.save(orderItem);

        log.info("Result created successfully: {}", saved.getResultNumber());
        return saved;
    }

    /**
     * Enter findings and impression for a result.
     *
     * @param resultId Result ID
     * @param findings Findings text
     * @param impression Impression text
     * @param recommendations Recommendations text
     * @return Updated result
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if result is already finalized
     */
    public RadiologyResult enterFindings(UUID resultId, String findings, String impression, String recommendations) {
        log.info("Entering findings for result: {}", resultId);

        RadiologyResult result = getResultById(resultId);

        // Check if result can be edited
        if (result.getIsFinalized()) {
            throw new IllegalStateException("Cannot edit finalized result: " + resultId);
        }

        result.setFindings(findings);
        result.setImpression(impression);
        result.setRecommendations(recommendations);

        RadiologyResult updated = resultRepository.save(result);
        log.info("Findings entered successfully for result: {}", resultId);
        return updated;
    }

    /**
     * Attach DICOM study to a result.
     *
     * @param resultId Result ID
     * @param dicomStudyId DICOM study ID
     * @return Updated result
     * @throws IllegalArgumentException if result not found
     */
    public RadiologyResult attachDicomStudy(UUID resultId, String dicomStudyId) {
        log.info("Attaching DICOM study {} to result: {}", dicomStudyId, resultId);

        RadiologyResult result = getResultById(resultId);
        result.setDicomStudyId(dicomStudyId);

        RadiologyResult updated = resultRepository.save(result);
        log.info("DICOM study attached successfully");
        return updated;
    }

    /**
     * Add an image to a result.
     * Updates the image count on the result.
     *
     * @param resultId Result ID
     * @return Updated result
     * @throws IllegalArgumentException if result not found
     */
    public RadiologyResult addImageToResult(UUID resultId) {
        log.info("Adding image to result: {}", resultId);

        RadiologyResult result = getResultById(resultId);

        // Count images
        long imageCount = imageRepository.countByResultId(resultId);
        result.setImageCount((int) imageCount);

        RadiologyResult updated = resultRepository.save(result);
        log.info("Image count updated for result: {}", resultId);
        return updated;
    }

    /**
     * Assign radiologist to a result for review.
     *
     * @param resultId Result ID
     * @param radiologistId Radiologist ID
     * @return Updated result
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if result is already finalized
     */
    public RadiologyResult assignRadiologist(UUID resultId, UUID radiologistId) {
        log.info("Assigning radiologist {} to result: {}", radiologistId, resultId);

        RadiologyResult result = getResultById(resultId);

        if (result.getIsFinalized()) {
            throw new IllegalStateException("Cannot assign radiologist to finalized result: " + resultId);
        }

        result.setRadiologistId(radiologistId);

        RadiologyResult updated = resultRepository.save(result);
        log.info("Radiologist assigned successfully");
        return updated;
    }

    /**
     * Radiologist reviews and reports on a result.
     *
     * @param resultId Result ID
     * @param radiologistId Radiologist ID
     * @param findings Updated findings
     * @param impression Updated impression
     * @param recommendations Updated recommendations
     * @return Updated result
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if result is already finalized
     */
    public RadiologyResult radiologistReview(UUID resultId, UUID radiologistId,
                                              String findings, String impression, String recommendations) {
        log.info("Radiologist {} reviewing result: {}", radiologistId, resultId);

        RadiologyResult result = getResultById(resultId);

        if (result.getIsFinalized()) {
            throw new IllegalStateException("Result is already finalized: " + resultId);
        }

        result.setRadiologistId(radiologistId);
        result.setFindings(findings);
        result.setImpression(impression);
        result.setRecommendations(recommendations);
        result.setReportedDate(LocalDateTime.now());

        RadiologyResult updated = resultRepository.save(result);
        log.info("Radiologist review completed for result: {}", resultId);
        return updated;
    }

    /**
     * Finalize a result.
     * Marks the result as final and sets the finalization date.
     *
     * @param resultId Result ID
     * @param finalizedByRadiologistId Radiologist ID who finalized
     * @return Updated result
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if result is already finalized or lacks findings
     */
    public RadiologyResult finalizeResult(UUID resultId, UUID finalizedByRadiologistId) {
        log.info("Finalizing result: {}", resultId);

        RadiologyResult result = getResultById(resultId);

        if (result.getIsFinalized()) {
            throw new IllegalStateException("Result is already finalized: " + resultId);
        }

        // Validate result has findings and impression
        if (result.getFindings() == null || result.getFindings().isEmpty()) {
            throw new IllegalStateException("Cannot finalize result without findings: " + resultId);
        }
        if (result.getImpression() == null || result.getImpression().isEmpty()) {
            throw new IllegalStateException("Cannot finalize result without impression: " + resultId);
        }

        result.setRadiologistId(finalizedByRadiologistId);
        result.setIsFinalized(true);
        result.setFinalizedDate(LocalDateTime.now());

        RadiologyResult updated = resultRepository.save(result);
        log.info("Result finalized successfully: {}", resultId);
        return updated;
    }

    /**
     * Amend a result.
     * Creates a new version with the amendment reason.
     *
     * @param resultId Result ID to amend
     * @param amendmentReason Reason for amendment
     * @param amendedBy User ID who amended
     * @return Updated result
     * @throws IllegalArgumentException if result not found
     * @throws IllegalStateException if result is not finalized
     */
    public RadiologyResult amendResult(UUID resultId, String amendmentReason, UUID amendedBy) {
        log.info("Amending result: {} - Reason: {}", resultId, amendmentReason);

        RadiologyResult result = getResultById(resultId);

        if (!result.getIsFinalized()) {
            throw new IllegalStateException("Only finalized results can be amended: " + resultId);
        }

        // Mark as amended
        result.setIsAmended(true);
        result.setAmendmentReason(amendmentReason);

        RadiologyResult updated = resultRepository.save(result);
        log.info("Result amended successfully: {}", resultId);
        return updated;
    }

    /**
     * Cancel a result.
     *
     * @param resultId Result ID
     * @param cancelReason Cancellation reason
     * @return Updated result (soft deleted)
     * @throws IllegalArgumentException if result not found
     */
    public RadiologyResult cancelResult(UUID resultId, String cancelReason) {
        log.info("Cancelling result: {} - Reason: {}", resultId, cancelReason);

        RadiologyResult result = getResultById(resultId);

        if (result.getIsFinalized()) {
            throw new IllegalStateException("Cannot cancel finalized result. Use amend instead: " + resultId);
        }

        // Soft delete
        result.setDeletedAt(LocalDateTime.now());
        result.setAmendmentReason("CANCELLED: " + cancelReason);

        RadiologyResult updated = resultRepository.save(result);
        log.info("Result cancelled successfully: {}", resultId);
        return updated;
    }

    /**
     * Get result by ID.
     *
     * @param id Result ID
     * @return Result
     * @throws IllegalArgumentException if result not found
     */
    @Transactional(readOnly = true)
    public RadiologyResult getResultById(UUID id) {
        return resultRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + id));
    }

    /**
     * Get result by number.
     *
     * @param resultNumber Result number
     * @return Result
     * @throws IllegalArgumentException if result not found
     */
    @Transactional(readOnly = true)
    public RadiologyResult getResultByNumber(String resultNumber) {
        return resultRepository.findByResultNumberAndDeletedAtIsNull(resultNumber)
                .orElseThrow(() -> new IllegalArgumentException("Result not found: " + resultNumber));
    }

    /**
     * Get result by order item.
     *
     * @param orderItemId Order item ID
     * @return Result if exists
     */
    @Transactional(readOnly = true)
    public RadiologyResult getResultByOrderItem(UUID orderItemId) {
        return resultRepository.findByOrderItemIdAndDeletedAtIsNull(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found for order item: " + orderItemId));
    }

    /**
     * Get results by patient.
     *
     * @param patientId Patient ID
     * @param pageable Pagination parameters
     * @return Page of results
     */
    @Transactional(readOnly = true)
    public Page<RadiologyResult> getResultsByPatient(UUID patientId, Pageable pageable) {
        return resultRepository.findByPatientIdAndDeletedAtIsNull(patientId, pageable);
    }

    /**
     * Get results by examination.
     *
     * @param examinationId Examination ID
     * @return List of results
     */
    @Transactional(readOnly = true)
    public List<RadiologyResult> getResultsByExamination(UUID examinationId) {
        return resultRepository.findByExaminationIdAndDeletedAtIsNull(examinationId);
    }

    /**
     * Get results by technician.
     *
     * @param technicianId Technician ID
     * @return List of results
     */
    @Transactional(readOnly = true)
    public List<RadiologyResult> getResultsByTechnician(UUID technicianId) {
        return resultRepository.findByPerformedByTechnicianIdAndDeletedAtIsNull(technicianId);
    }

    /**
     * Get results by radiologist.
     *
     * @param radiologistId Radiologist ID
     * @return List of results
     */
    @Transactional(readOnly = true)
    public List<RadiologyResult> getResultsByRadiologist(UUID radiologistId) {
        return resultRepository.findByRadiologistIdAndDeletedAtIsNull(radiologistId);
    }

    /**
     * Get pending results (awaiting finalization).
     *
     * @return List of pending results
     */
    @Transactional(readOnly = true)
    public List<RadiologyResult> getPendingResults() {
        return resultRepository.findPendingResults();
    }

    /**
     * Get results awaiting radiologist review.
     *
     * @return List of results awaiting radiologist
     */
    @Transactional(readOnly = true)
    public List<RadiologyResult> getResultsAwaitingRadiologist() {
        return resultRepository.findResultsAwaitingRadiologist();
    }

    /**
     * Get finalized results.
     *
     * @return List of finalized results
     */
    @Transactional(readOnly = true)
    public List<RadiologyResult> getFinalizedResults() {
        return resultRepository.findByIsFinalizedTrueAndDeletedAtIsNull();
    }

    /**
     * Get amended results.
     *
     * @return List of amended results
     */
    @Transactional(readOnly = true)
    public List<RadiologyResult> getAmendedResults() {
        return resultRepository.findByIsAmendedTrueAndDeletedAtIsNull();
    }

    /**
     * Get results by date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of results
     */
    @Transactional(readOnly = true)
    public Page<RadiologyResult> getResultsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return resultRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Search results.
     *
     * @param search Search term
     * @param pageable Pagination parameters
     * @return Page of matching results
     */
    @Transactional(readOnly = true)
    public Page<RadiologyResult> searchResults(String search, Pageable pageable) {
        return resultRepository.searchResults(search, pageable);
    }

    /**
     * Get images for a result.
     *
     * @param resultId Result ID
     * @return List of images
     */
    @Transactional(readOnly = true)
    public List<RadiologyImage> getResultImages(UUID resultId) {
        return imageRepository.findByResultIdOrderByImageNumberAsc(resultId);
    }

    /**
     * Get key images for a result.
     *
     * @param resultId Result ID
     * @return List of key images
     */
    @Transactional(readOnly = true)
    public List<RadiologyImage> getResultKeyImages(UUID resultId) {
        return imageRepository.findKeyImagesByResult(resultId);
    }

    /**
     * Count finalized results.
     *
     * @return Count of finalized results
     */
    @Transactional(readOnly = true)
    public long countFinalizedResults() {
        return resultRepository.countByIsFinalizedTrueAndDeletedAtIsNull();
    }

    /**
     * Count pending results.
     *
     * @return Count of pending results
     */
    @Transactional(readOnly = true)
    public long countPendingResults() {
        return resultRepository.countByIsFinalizedFalseAndDeletedAtIsNull();
    }

    /**
     * Count results by examination.
     *
     * @param examinationId Examination ID
     * @return Count of results
     */
    @Transactional(readOnly = true)
    public long countResultsByExamination(UUID examinationId) {
        return resultRepository.countByExaminationIdAndDeletedAtIsNull(examinationId);
    }

    /**
     * Count results by radiologist.
     *
     * @param radiologistId Radiologist ID
     * @return Count of results
     */
    @Transactional(readOnly = true)
    public long countResultsByRadiologist(UUID radiologistId) {
        return resultRepository.countByRadiologistIdAndDeletedAtIsNull(radiologistId);
    }

    // Private helper methods

    /**
     * Generate unique result number.
     * Format: RR + YYYYMMDD + 6-digit sequence
     */
    private String generateResultNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RR" + datePart;

        // Count results for today (simplified - in production use proper sequence)
        long count = resultRepository.count();
        String sequence = String.format("%06d", (count % 1000000) + 1);

        return prefix + sequence;
    }
}
