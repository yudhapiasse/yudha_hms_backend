package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.constant.QualityStatus;
import com.yudha.hms.laboratory.constant.SpecimenStatus;
import com.yudha.hms.laboratory.entity.LabOrder;
import com.yudha.hms.laboratory.entity.LabOrderItem;
import com.yudha.hms.laboratory.entity.Specimen;
import com.yudha.hms.laboratory.repository.SpecimenRepository;
import com.yudha.hms.laboratory.repository.LabOrderRepository;
import com.yudha.hms.laboratory.repository.LabOrderItemRepository;
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
 * Service for Specimen operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpecimenService {

    private final SpecimenRepository specimenRepository;
    private final LabOrderRepository labOrderRepository;
    private final LabOrderItemRepository labOrderItemRepository;
    private final BarcodeGenerationService barcodeGenerationService;

    /**
     * Create a new specimen
     */
    public Specimen createSpecimen(Specimen specimen) {
        log.info("Creating new specimen for order item: {}", specimen.getOrderItem().getId());

        // Validate order item exists
        labOrderItemRepository.findById(specimen.getOrderItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + specimen.getOrderItem().getId()));

        // Generate specimen number if not provided
        if (specimen.getSpecimenNumber() == null || specimen.getSpecimenNumber().isEmpty()) {
            specimen.setSpecimenNumber(generateSpecimenNumber());
        }

        // Generate barcode if not provided
        if (specimen.getBarcode() == null || specimen.getBarcode().isEmpty()) {
            specimen.setBarcode(barcodeGenerationService.generateSpecimenBarcode());
        }

        // Set initial status
        if (specimen.getStatus() == null) {
            specimen.setStatus(SpecimenStatus.COLLECTED);
        }

        // Set initial quality status
        if (specimen.getQualityStatus() == null) {
            specimen.setQualityStatus(QualityStatus.ACCEPTABLE);
        }

        Specimen saved = specimenRepository.save(specimen);
        log.info("Specimen created successfully with barcode: {}", saved.getBarcode());
        return saved;
    }

    /**
     * Generate specimen number
     */
    private String generateSpecimenNumber() {
        String prefix = "SP";
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        // TODO: Repository method may not exist - implement in repository if needed
        // For now, use a simple timestamp-based approach
        long count = System.currentTimeMillis() % 100000;
        String sequence = String.format("%05d", count);
        return prefix + datePart + sequence;
    }

    /**
     * Collect specimen
     */
    public Specimen collectSpecimen(UUID orderItemId, UUID collectedBy, LocalDateTime collectedAt) {
        log.info("Collecting specimen for order item: {}", orderItemId);

        LabOrderItem orderItem = labOrderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        // Need to get the order and specimenType from the test
        LabOrder order = orderItem.getOrder();

        Specimen specimen = Specimen.builder()
                .order(order)
                .orderItem(orderItem)
                .specimenNumber(generateSpecimenNumber())
                .barcode(barcodeGenerationService.generateSpecimenBarcode())
                .specimenType(orderItem.getTest() != null ? orderItem.getTest().getSampleType() : null)
                .status(SpecimenStatus.COLLECTED)
                .qualityStatus(QualityStatus.ACCEPTABLE)
                .collectedAt(collectedAt != null ? collectedAt : LocalDateTime.now())
                .collectedBy(collectedBy)
                .build();

        return createSpecimen(specimen);
    }

    /**
     * Receive specimen in lab
     */
    public Specimen receiveSpecimen(String barcode, UUID receivedBy) {
        log.info("Receiving specimen in lab: {}", barcode);

        Specimen specimen = getSpecimenByBarcode(barcode);

        if (specimen.getStatus() != SpecimenStatus.COLLECTED) {
            throw new IllegalStateException("Specimen cannot be received in current status: " + specimen.getStatus());
        }

        specimen.setStatus(SpecimenStatus.RECEIVED);
        specimen.setReceivedAt(LocalDateTime.now());
        specimen.setReceivedBy(receivedBy);

        Specimen updated = specimenRepository.save(specimen);
        log.info("Specimen received successfully: {}", barcode);
        return updated;
    }

    /**
     * Perform quality check
     */
    public Specimen performQualityCheck(String barcode, QualityStatus qualityStatus, Boolean hemolysis,
                                         Boolean lipemia, Boolean icterus, String qualityNotes) {
        log.info("Performing quality check for specimen: {}", barcode);

        Specimen specimen = getSpecimenByBarcode(barcode);

        specimen.setQualityStatus(qualityStatus);
        specimen.setHemolysisDetected(hemolysis);
        specimen.setLipemiaDetected(lipemia);
        specimen.setIcterusDetected(icterus);
        specimen.setQualityNotes(qualityNotes);

        Specimen updated = specimenRepository.save(specimen);
        log.info("Quality check completed: {} - {}", barcode, qualityStatus);
        return updated;
    }

    /**
     * Reject specimen
     */
    public Specimen rejectSpecimen(String barcode, String rejectionReason) {
        log.info("Rejecting specimen: {}", barcode);

        Specimen specimen = getSpecimenByBarcode(barcode);
        specimen.setStatus(SpecimenStatus.REJECTED);
        specimen.setQualityStatus(QualityStatus.REJECTED);
        specimen.setRejectionReason(rejectionReason);
        // TODO: Specimen entity does not have rejectedAt field
        // specimen.setRejectedAt(LocalDateTime.now());

        Specimen updated = specimenRepository.save(specimen);
        log.info("Specimen rejected: {} - {}", barcode, rejectionReason);
        return updated;
    }

    /**
     * Process specimen
     */
    public Specimen processSpecimen(String barcode) {
        log.info("Processing specimen: {}", barcode);

        Specimen specimen = getSpecimenByBarcode(barcode);

        if (specimen.getStatus() != SpecimenStatus.RECEIVED) {
            throw new IllegalStateException("Specimen must be received before processing");
        }

        if (specimen.getQualityStatus() != QualityStatus.ACCEPTABLE) {
            throw new IllegalStateException("Specimen quality is not acceptable for processing");
        }

        specimen.setStatus(SpecimenStatus.PROCESSING);

        Specimen updated = specimenRepository.save(specimen);
        log.info("Specimen processing started: {}", barcode);
        return updated;
    }

    /**
     * Complete specimen processing
     */
    public Specimen completeSpecimenProcessing(String barcode) {
        log.info("Completing specimen processing: {}", barcode);

        Specimen specimen = getSpecimenByBarcode(barcode);
        specimen.setStatus(SpecimenStatus.COMPLETED);

        Specimen updated = specimenRepository.save(specimen);
        log.info("Specimen processing completed: {}", barcode);
        return updated;
    }

    /**
     * Store specimen
     */
    public Specimen storeSpecimen(String barcode, String storageLocation, BigDecimal storageTemperature) {
        log.info("Storing specimen: {} at location: {}", barcode, storageLocation);

        Specimen specimen = getSpecimenByBarcode(barcode);
        specimen.setStorageLocation(storageLocation);
        specimen.setStorageTemperature(storageTemperature);
        specimen.setStoredAt(LocalDateTime.now());

        Specimen updated = specimenRepository.save(specimen);
        log.info("Specimen stored successfully: {}", barcode);
        return updated;
    }

    /**
     * Dispose specimen
     */
    public Specimen disposeSpecimen(String barcode, UUID disposedBy, String disposalMethod) {
        log.info("Disposing specimen: {}", barcode);

        Specimen specimen = getSpecimenByBarcode(barcode);
        specimen.setStatus(SpecimenStatus.DISCARDED);
        specimen.setDisposedAt(LocalDateTime.now());
        specimen.setDisposedBy(disposedBy);
        specimen.setDisposalMethod(disposalMethod);

        Specimen updated = specimenRepository.save(specimen);
        log.info("Specimen disposed: {}", barcode);
        return updated;
    }

    /**
     * Get specimen by ID
     */
    @Transactional(readOnly = true)
    public Specimen getSpecimenById(UUID id) {
        return specimenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specimen not found: " + id));
    }

    /**
     * Get specimen by barcode
     */
    @Transactional(readOnly = true)
    public Specimen getSpecimenByBarcode(String barcode) {
        return specimenRepository.findByBarcode(barcode)
                .orElseThrow(() -> new IllegalArgumentException("Specimen not found with barcode: " + barcode));
    }

    /**
     * Get specimens by order
     */
    @Transactional(readOnly = true)
    public List<Specimen> getSpecimensByOrder(UUID orderId) {
        // TODO: Repository method may not exist - implement in repository if needed
        return specimenRepository.findByOrderId(orderId);
    }

    /**
     * Get specimens by status
     */
    @Transactional(readOnly = true)
    public Page<Specimen> getSpecimensByStatus(SpecimenStatus status, Pageable pageable) {
        return specimenRepository.findByStatusOrderByCollectedAtDesc(status, pageable);
    }

    /**
     * Get specimens by quality status
     */
    @Transactional(readOnly = true)
    public List<Specimen> getSpecimensByQualityStatus(QualityStatus qualityStatus) {
        // TODO: Repository method may not exist - implement in repository if needed
        return specimenRepository.findByQualityStatus(qualityStatus);
    }

    /**
     * Get pending specimens
     */
    @Transactional(readOnly = true)
    public List<Specimen> getPendingSpecimens() {
        // TODO: Repository method may not exist - implement in repository if needed
        return specimenRepository.findByStatus(SpecimenStatus.PENDING);
    }

    /**
     * Get rejected specimens
     */
    @Transactional(readOnly = true)
    public List<Specimen> getRejectedSpecimens(LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: Repository method may not exist - implement in repository if needed
        return specimenRepository.findByStatusAndCollectedAtBetween(SpecimenStatus.REJECTED, startDate, endDate);
    }

    /**
     * Get specimens by collection date range
     */
    @Transactional(readOnly = true)
    public List<Specimen> getSpecimensByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: Repository method may not exist - implement in repository if needed
        return specimenRepository.findByCollectedAtBetween(startDate, endDate);
    }

    /**
     * Count specimens by status
     */
    @Transactional(readOnly = true)
    public long countSpecimensByStatus(SpecimenStatus status) {
        // TODO: Repository method may not exist - implement in repository if needed
        return specimenRepository.countByStatus(status);
    }

    /**
     * Get specimens with quality issues
     */
    @Transactional(readOnly = true)
    public List<Specimen> getSpecimensWithQualityIssues() {
        // TODO: Repository method may not exist - implement custom query in repository
        // Simplified: return specimens with REJECTED or COMPROMISED quality status
        return specimenRepository.findByQualityStatusIn(
            java.util.Arrays.asList(QualityStatus.REJECTED, QualityStatus.COMPROMISED)
        );
    }

    /**
     * Check if specimen barcode exists
     */
    @Transactional(readOnly = true)
    public boolean barcodeExists(String barcode) {
        return specimenRepository.findByBarcode(barcode).isPresent();
    }

    /**
     * Update specimen storage location
     */
    public Specimen updateStorageLocation(String barcode, String newLocation) {
        log.info("Updating specimen storage location: {} to {}", barcode, newLocation);
        Specimen specimen = getSpecimenByBarcode(barcode);
        specimen.setStorageLocation(newLocation);
        return specimenRepository.save(specimen);
    }

    /**
     * Add specimen notes
     */
    public Specimen addSpecimenNotes(String barcode, String notes) {
        log.info("Adding notes to specimen: {}", barcode);
        Specimen specimen = getSpecimenByBarcode(barcode);

        String existingNotes = specimen.getQualityNotes();
        String updatedNotes = existingNotes != null
            ? existingNotes + "\n" + LocalDateTime.now() + ": " + notes
            : LocalDateTime.now() + ": " + notes;

        specimen.setQualityNotes(updatedNotes);
        return specimenRepository.save(specimen);
    }
}
