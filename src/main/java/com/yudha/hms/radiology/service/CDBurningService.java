package com.yudha.hms.radiology.service;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.radiology.constant.CDRequestStatus;
import com.yudha.hms.radiology.constant.CDRequestType;
import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.radiology.entity.CDBurningRequest;
import com.yudha.hms.radiology.repository.CDBurningRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for CD Burning operations.
 *
 * Handles CD/DVD burning workflow for patient image delivery.
 * Manages request queue, processing, burning, and delivery tracking.
 *
 * Features:
 * - Create CD burning request with auto-numbering
 * - Queue management with priority
 * - Assign requests to operators
 * - Track burning process
 * - Record delivery
 * - Retry failed requests
 * - Query requests by status, patient, priority
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CDBurningService {

    private final CDBurningRequestRepository requestRepository;

    /**
     * Create a CD burning request.
     *
     * @param patient Patient
     * @param studyIds Array of study IDs to burn
     * @param requestType Type of CD request
     * @param requestedBy User ID who requested
     * @param requestedFor Name of person/entity requesting
     * @param requestReason Reason for request
     * @param priority Request priority
     * @param includeViewer Include DICOM viewer
     * @param includeReports Include reports
     * @param anonymize Anonymize images
     * @return Created CD burning request
     */
    public CDBurningRequest createBurningRequest(Patient patient, UUID[] studyIds,
                                                CDRequestType requestType, UUID requestedBy,
                                                String requestedFor, String requestReason,
                                                OrderPriority priority, Boolean includeViewer,
                                                Boolean includeReports, Boolean anonymize) {
        log.info("Creating CD burning request for patient: {}", patient.getId());

        // Generate request number
        String requestNumber = generateRequestNumber();

        // Build patient name
        String patientName = patient.getFullName();

        CDBurningRequest request = CDBurningRequest.builder()
                .requestNumber(requestNumber)
                .requestType(requestType != null ? requestType : CDRequestType.PATIENT_CD)
                .studyIds(studyIds)
                .patient(patient)
                .patientName(patientName)
                .cdLabel(buildCdLabel(patientName, requestType))
                .includeViewer(includeViewer != null ? includeViewer : true)
                .includeReports(includeReports != null ? includeReports : true)
                .outputFormat("DICOM")
                .anonymize(anonymize != null ? anonymize : false)
                .compress(false)
                .requestedBy(requestedBy)
                .requestedFor(requestedFor)
                .requestReason(requestReason)
                .status(CDRequestStatus.PENDING)
                .priority(priority != null ? priority : OrderPriority.ROUTINE)
                .cdCount(1)
                .failed(false)
                .retryCount(0)
                .build();

        CDBurningRequest saved = requestRepository.save(request);
        log.info("CD burning request created successfully: {}", saved.getRequestNumber());
        return saved;
    }

    /**
     * Generate request number with format: CD + YYYYMMDD + 6-digit sequence.
     *
     * @return Generated request number
     */
    private String generateRequestNumber() {
        String today = LocalDate.now().toString().replace("-", "");
        String lastRequestNumber = requestRepository.findTopByOrderByCreatedAtDesc()
                .map(CDBurningRequest::getRequestNumber)
                .orElse("CD000000000000000");

        int sequence = 1;
        if (lastRequestNumber.startsWith("CD" + today)) {
            String lastSeq = lastRequestNumber.substring(10);
            sequence = Integer.parseInt(lastSeq) + 1;
        }

        return String.format("CD%s%06d", today, sequence);
    }

    /**
     * Build CD label.
     *
     * @param patientName Patient name
     * @param requestType Request type
     * @return CD label
     */
    private String buildCdLabel(String patientName, CDRequestType requestType) {
        String date = LocalDate.now().toString();
        return String.format("%s - %s - %s", patientName, requestType, date);
    }

    /**
     * Get CD burning request by ID.
     *
     * @param id Request ID
     * @return CD burning request
     */
    @Transactional(readOnly = true)
    public CDBurningRequest getRequestById(UUID id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CD burning request not found: " + id));
    }

    /**
     * Get CD burning request by request number.
     *
     * @param requestNumber Request number
     * @return CD burning request
     */
    @Transactional(readOnly = true)
    public CDBurningRequest getRequestByNumber(String requestNumber) {
        return requestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new IllegalArgumentException("CD burning request not found: " + requestNumber));
    }

    /**
     * Get CD burning requests by patient ID.
     *
     * @param patientId Patient ID
     * @return List of CD burning requests
     */
    @Transactional(readOnly = true)
    public List<CDBurningRequest> getRequestsByPatient(UUID patientId) {
        return requestRepository.findByPatientIdAndDeletedAtIsNull(patientId);
    }

    /**
     * Get CD burning requests by patient ID with pagination.
     *
     * @param patientId Patient ID
     * @param pageable Pagination
     * @return Page of CD burning requests
     */
    @Transactional(readOnly = true)
    public Page<CDBurningRequest> getRequestsByPatient(UUID patientId, Pageable pageable) {
        return requestRepository.findByPatientIdAndDeletedAtIsNull(patientId, pageable);
    }

    /**
     * Get CD burning requests by status.
     *
     * @param status Request status
     * @return List of CD burning requests
     */
    @Transactional(readOnly = true)
    public List<CDBurningRequest> getRequestsByStatus(CDRequestStatus status) {
        return requestRepository.findByStatusAndDeletedAtIsNull(status);
    }

    /**
     * Get pending CD burning requests (PENDING, QUEUED).
     *
     * @return List of pending requests
     */
    @Transactional(readOnly = true)
    public List<CDBurningRequest> getPendingRequests() {
        return requestRepository.findPendingRequests(
                Arrays.asList(CDRequestStatus.PENDING, CDRequestStatus.QUEUED)
        );
    }

    /**
     * Get active requests for a user.
     *
     * @param userId User ID
     * @return List of active requests
     */
    @Transactional(readOnly = true)
    public List<CDBurningRequest> getActiveRequestsByUser(UUID userId) {
        return requestRepository.findActiveRequestsByUser(userId);
    }

    /**
     * Get requests by requester.
     *
     * @param userId User ID
     * @return List of requests
     */
    @Transactional(readOnly = true)
    public List<CDBurningRequest> getRequestsByRequester(UUID userId) {
        return requestRepository.findByRequestedBy(userId);
    }

    /**
     * Update request status.
     *
     * @param id Request ID
     * @param status New status
     * @return Updated request
     */
    public CDBurningRequest updateRequestStatus(UUID id, CDRequestStatus status) {
        log.info("Updating CD burning request status: {} to {}", id, status);

        CDBurningRequest request = getRequestById(id);
        request.setStatus(status);

        if (status == CDRequestStatus.PROCESSING) {
            request.setProcessingStartedAt(LocalDateTime.now());
        } else if (status == CDRequestStatus.COMPLETED) {
            request.setProcessingCompletedAt(LocalDateTime.now());
        }

        return requestRepository.save(request);
    }

    /**
     * Assign request to operator.
     *
     * @param id Request ID
     * @param operatorId Operator user ID
     * @return Updated request
     */
    public CDBurningRequest assignRequest(UUID id, UUID operatorId) {
        log.info("Assigning CD burning request: {} to operator: {}", id, operatorId);

        CDBurningRequest request = getRequestById(id);
        request.setAssignedTo(operatorId);
        request.setStatus(CDRequestStatus.QUEUED);

        return requestRepository.save(request);
    }

    /**
     * Start burning process.
     *
     * @param id Request ID
     * @return Updated request
     */
    public CDBurningRequest startBurning(UUID id) {
        log.info("Starting CD burning process: {}", id);

        CDBurningRequest request = getRequestById(id);
        request.setStatus(CDRequestStatus.BURNING);
        request.setProcessingStartedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    /**
     * Complete burning process.
     *
     * @param id Request ID
     * @param burnedBy User ID who completed burning
     * @param cdCount Number of CDs burned
     * @return Updated request
     */
    public CDBurningRequest completeBurning(UUID id, UUID burnedBy, Integer cdCount) {
        log.info("Completing CD burning process: {}", id);

        CDBurningRequest request = getRequestById(id);
        request.setStatus(CDRequestStatus.COMPLETED);
        request.setProcessingCompletedAt(LocalDateTime.now());
        request.setBurnedAt(LocalDateTime.now());
        request.setBurnedBy(burnedBy);
        request.setCdCount(cdCount != null ? cdCount : 1);

        return requestRepository.save(request);
    }

    /**
     * Mark request as ready for pickup.
     *
     * @param id Request ID
     * @return Updated request
     */
    public CDBurningRequest markReadyForPickup(UUID id) {
        log.info("Marking CD burning request ready for pickup: {}", id);

        CDBurningRequest request = getRequestById(id);
        request.setStatus(CDRequestStatus.READY_FOR_PICKUP);

        return requestRepository.save(request);
    }

    /**
     * Record delivery of CD.
     *
     * @param id Request ID
     * @param deliveryMethod Delivery method
     * @param deliveredTo Person/entity delivered to
     * @param deliveryNotes Delivery notes
     * @return Updated request
     */
    public CDBurningRequest recordDelivery(UUID id, String deliveryMethod,
                                          String deliveredTo, String deliveryNotes) {
        log.info("Recording CD delivery: {}", id);

        CDBurningRequest request = getRequestById(id);
        request.setStatus(CDRequestStatus.DELIVERED);
        request.setDeliveryMethod(deliveryMethod);
        request.setDeliveredTo(deliveredTo);
        request.setDeliveredAt(LocalDateTime.now());
        request.setDeliveryNotes(deliveryNotes);

        return requestRepository.save(request);
    }

    /**
     * Mark request as failed.
     *
     * @param id Request ID
     * @param failureReason Failure reason
     * @return Updated request
     */
    public CDBurningRequest markAsFailed(UUID id, String failureReason) {
        log.info("Marking CD burning request as failed: {}", id);

        CDBurningRequest request = getRequestById(id);
        request.setFailed(true);
        request.setFailureReason(failureReason);
        request.setStatus(CDRequestStatus.FAILED);
        request.setRetryCount(request.getRetryCount() + 1);

        return requestRepository.save(request);
    }

    /**
     * Retry failed request.
     *
     * @param id Request ID
     * @return Updated request
     */
    public CDBurningRequest retryRequest(UUID id) {
        log.info("Retrying CD burning request: {}", id);

        CDBurningRequest request = getRequestById(id);

        if (!request.getFailed()) {
            throw new IllegalStateException("Request is not failed: " + id);
        }

        request.setFailed(false);
        request.setStatus(CDRequestStatus.PENDING);
        request.setFailureReason(null);

        return requestRepository.save(request);
    }

    /**
     * Cancel request.
     *
     * @param id Request ID
     * @param reason Cancellation reason
     * @return Updated request
     */
    public CDBurningRequest cancelRequest(UUID id, String reason) {
        log.info("Cancelling CD burning request: {} with reason: {}", id, reason);

        CDBurningRequest request = getRequestById(id);
        request.setStatus(CDRequestStatus.CANCELLED);
        request.setNotes(reason);

        return requestRepository.save(request);
    }

    /**
     * Get failed requests eligible for retry.
     *
     * @param maxRetries Maximum retry count
     * @return List of failed requests
     */
    @Transactional(readOnly = true)
    public List<CDBurningRequest> getFailedRequestsForRetry(Integer maxRetries) {
        return requestRepository.findFailedRequestsForRetry(maxRetries != null ? maxRetries : 3);
    }

    /**
     * Get count of requests by status.
     *
     * @param status Request status
     * @return Count of requests
     */
    @Transactional(readOnly = true)
    public long getRequestCountByStatus(CDRequestStatus status) {
        return requestRepository.countByStatusAndDeletedAtIsNull(status);
    }

    /**
     * Soft delete request.
     *
     * @param id Request ID
     */
    public void deleteRequest(UUID id) {
        log.info("Deleting CD burning request: {}", id);

        CDBurningRequest request = getRequestById(id);
        request.setDeletedAt(LocalDateTime.now());
        requestRepository.save(request);
    }
}
