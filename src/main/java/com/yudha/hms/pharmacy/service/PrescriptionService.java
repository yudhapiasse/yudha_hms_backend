package com.yudha.hms.pharmacy.service;

import com.yudha.hms.pharmacy.constant.*;
import com.yudha.hms.pharmacy.dto.*;
import com.yudha.hms.pharmacy.entity.*;
import com.yudha.hms.pharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Prescription Service.
 *
 * Handles prescription management including:
 * - Creating and submitting prescriptions
 * - Drug interaction checking
 * - Dosage validation
 * - Pharmacist verification workflow
 * - Dispensing workflow
 * - Prescription history tracking
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final PrescriptionVerificationRepository verificationRepository;
    private final DrugRepository drugRepository;
    private final DrugInteractionRepository drugInteractionRepository;

    /**
     * Create a new prescription (draft state)
     */
    public PrescriptionResponse createPrescription(CreatePrescriptionRequest request, UUID doctorId, String doctorName) {
        log.info("Creating prescription for patient: {}, doctor: {}", request.getPatientId(), doctorId);

        // Create prescription entity
        Prescription prescription = Prescription.builder()
                .prescriptionNumber(generatePrescriptionNumber())
                .patientId(request.getPatientId())
                .encounterId(request.getEncounterId())
                .doctorId(doctorId)
                .prescriptionDate(request.getPrescriptionDate())
                .prescriptionType(request.getPrescriptionType())
                .status(PrescriptionStatus.DRAFT)
                .diagnosis(request.getDiagnosis())
                .icd10Codes(request.getIcd10Codes())
                .specialInstructions(request.getSpecialInstructions())
                .allergies(request.getAllergies())
                .authorizationNumber(request.getAuthorizationNumber())
                .notes(request.getNotes())
                .active(true)
                .build();

        // Calculate valid until date (30 days for regular, 7 days for controlled)
        int validityDays = request.getPrescriptionType().hasRestrictedValidity() ? 7 : 30;
        prescription.setValidUntil(request.getPrescriptionDate().plusDays(validityDays));

        // Add prescription items
        List<PrescriptionItem> items = new ArrayList<>();
        int lineNumber = 1;

        for (PrescriptionItemRequest itemRequest : request.getItems()) {
            Drug drug = drugRepository.findById(itemRequest.getDrugId())
                    .orElseThrow(() -> new IllegalArgumentException("Drug not found: " + itemRequest.getDrugId()));

            PrescriptionItem item = PrescriptionItem.builder()
                    .prescription(prescription)
                    .lineNumber(lineNumber++)
                    .drug(drug)
                    .drugCode(drug.getDrugCode())
                    .drugName(drug.getGenericName())
                    .strength(drug.getStrength())
                    .dosageForm(drug.getDosageForm())
                    .doseQuantity(itemRequest.getDoseQuantity())
                    .doseUnit(itemRequest.getDoseUnit())
                    .frequency(itemRequest.getFrequency())
                    .customFrequency(itemRequest.getCustomFrequency())
                    .route(itemRequest.getRoute())
                    .durationDays(itemRequest.getDurationDays())
                    .quantityToDispense(itemRequest.getQuantityToDispense())
                    .quantityDispensed(BigDecimal.ZERO)
                    .unitPrice(drug.getUnitPrice())
                    .instructions(itemRequest.getInstructions())
                    .specialInstructions(itemRequest.getSpecialInstructions())
                    .isPrn(itemRequest.getIsPrn())
                    .prnIndication(itemRequest.getPrnIndication())
                    .substitutionAllowed(itemRequest.getSubstitutionAllowed())
                    .isControlled(drug.isControlledSubstance())
                    .isHighAlert(drug.getIsHighAlert())
                    .labelPrinted(false)
                    .build();

            // Calculate total price
            if (item.getUnitPrice() != null && item.getQuantityToDispense() != null) {
                item.setTotalPrice(item.getUnitPrice().multiply(item.getQuantityToDispense()));
            }

            items.add(item);
        }

        prescription.setItems(items);

        // Check for controlled drugs
        boolean hasControlledDrugs = items.stream().anyMatch(PrescriptionItem::getIsControlled);
        prescription.setIsControlled(hasControlledDrugs);

        // Check drug interactions
        checkDrugInteractions(prescription);

        // Check if requires authorization (controlled substances)
        prescription.setRequiresAuthorization(hasControlledDrugs);

        // Save prescription
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription created successfully: {}", savedPrescription.getPrescriptionNumber());

        return mapToResponse(savedPrescription);
    }

    /**
     * Submit prescription for verification
     */
    public PrescriptionResponse submitPrescription(UUID prescriptionId) {
        log.info("Submitting prescription for verification: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));

        // Submit prescription (changes status to PENDING_VERIFICATION)
        prescription.submit();

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription submitted: {}", savedPrescription.getPrescriptionNumber());

        return mapToResponse(savedPrescription);
    }

    /**
     * Verify prescription (pharmacist)
     */
    public PrescriptionResponse verifyPrescription(UUID prescriptionId, VerifyPrescriptionRequest request,
                                                   UUID pharmacistId, String pharmacistName) {
        log.info("Verifying prescription: {}, pharmacist: {}", prescriptionId, pharmacistId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));

        if (!prescription.getStatus().canBeVerified()) {
            throw new IllegalStateException("Prescription cannot be verified in current status: " + prescription.getStatus());
        }

        // Create verification record
        PrescriptionVerification verification = PrescriptionVerification.builder()
                .prescription(prescription)
                .pharmacistId(pharmacistId)
                .pharmacistName(pharmacistName)
                .status(request.getStatus())
                .interactionCheckPerformed(true)
                .interactionsFound(request.getInteractionsFound())
                .interactionDetails(request.getInteractionDetails())
                .dosageValidationPerformed(true)
                .dosageIssuesFound(request.getDosageIssuesFound())
                .dosageIssues(request.getDosageIssues())
                .allergyCheckPerformed(true)
                .allergiesFound(request.getAllergiesFound())
                .allergyDetails(request.getAllergyDetails())
                .changesMade(request.getChangesMade())
                .rejectionReason(request.getRejectionReason())
                .clarificationNeeded(request.getClarificationNeeded())
                .comments(request.getComments())
                .dualVerificationRequired(prescription.getPrescriptionType().requiresDualVerification())
                .build();

        verificationRepository.save(verification);

        // Update prescription status based on verification result
        if (request.getStatus().canProceed()) {
            prescription.verify(pharmacistId, pharmacistName);
        } else if (request.getStatus() == VerificationStatus.REJECTED) {
            prescription.reject(request.getRejectionReason());
        } else if (request.getStatus() == VerificationStatus.REQUIRES_CLARIFICATION) {
            // Keep in PENDING_VERIFICATION status
            prescription.setNotes(
                    (prescription.getNotes() != null ? prescription.getNotes() + "\n" : "") +
                    "CLARIFICATION NEEDED: " + request.getClarificationNeeded()
            );
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription verification completed: {}, status: {}",
                savedPrescription.getPrescriptionNumber(), request.getStatus());

        return mapToResponse(savedPrescription);
    }

    /**
     * Dispense prescription items
     */
    public PrescriptionResponse dispensePrescription(UUID prescriptionId, Map<UUID, BigDecimal> itemQuantities,
                                                     UUID pharmacistId, String pharmacistName) {
        log.info("Dispensing prescription: {}, pharmacist: {}", prescriptionId, pharmacistId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));

        if (!prescription.getStatus().canBeDispensed()) {
            throw new IllegalStateException("Prescription cannot be dispensed in current status: " + prescription.getStatus());
        }

        if (prescription.isExpired()) {
            throw new IllegalStateException("Prescription has expired");
        }

        // Update dispensed quantities
        boolean fullyDispensed = true;
        for (PrescriptionItem item : prescription.getItems()) {
            if (itemQuantities.containsKey(item.getId())) {
                BigDecimal quantityToDispense = itemQuantities.get(item.getId());
                BigDecimal newQuantity = item.getQuantityDispensed().add(quantityToDispense);

                if (newQuantity.compareTo(item.getQuantityToDispense()) > 0) {
                    throw new IllegalArgumentException("Cannot dispense more than prescribed quantity for item: " + item.getDrugName());
                }

                item.setQuantityDispensed(newQuantity);

                if (!item.isFullyDispensed()) {
                    fullyDispensed = false;
                }
            } else if (!item.isFullyDispensed()) {
                fullyDispensed = false;
            }
        }

        // Update prescription status
        if (fullyDispensed) {
            prescription.markDispensed(pharmacistId, pharmacistName);
        } else {
            prescription.setStatus(PrescriptionStatus.PARTIALLY_DISPENSED);
            if (prescription.getDispensedAt() == null) {
                prescription.setDispensedAt(LocalDateTime.now());
                prescription.setDispensedBy(pharmacistId);
            }
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription dispensed: {}, status: {}",
                savedPrescription.getPrescriptionNumber(), savedPrescription.getStatus());

        return mapToResponse(savedPrescription);
    }

    /**
     * Cancel prescription
     */
    public PrescriptionResponse cancelPrescription(UUID prescriptionId, String reason) {
        log.info("Cancelling prescription: {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));

        if (prescription.getStatus().isFinal()) {
            throw new IllegalStateException("Cannot cancel prescription in final status: " + prescription.getStatus());
        }

        prescription.setStatus(PrescriptionStatus.CANCELLED);
        prescription.setNotes(
                (prescription.getNotes() != null ? prescription.getNotes() + "\n" : "") +
                "CANCELLED: " + reason
        );

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        log.info("Prescription cancelled: {}", savedPrescription.getPrescriptionNumber());

        return mapToResponse(savedPrescription);
    }

    /**
     * Get prescription by ID
     */
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionId));

        return mapToResponse(prescription);
    }

    /**
     * Get prescription by prescription number
     */
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionByNumber(String prescriptionNumber) {
        Prescription prescription = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + prescriptionNumber));

        return mapToResponse(prescription);
    }

    /**
     * Get prescriptions for patient
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsForPatient(UUID patientId, Pageable pageable) {
        Page<Prescription> prescriptions = prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(
                patientId, pageable);

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get prescriptions for doctor
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsForDoctor(UUID doctorId, Pageable pageable) {
        Page<Prescription> prescriptions = prescriptionRepository.findByDoctorIdOrderByPrescriptionDateDesc(
                doctorId, pageable);

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get pending verifications
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPendingVerifications() {
        List<Prescription> prescriptions = prescriptionRepository.findPendingVerification();

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get verified prescriptions ready for dispensing
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getVerifiedNotDispensed() {
        List<Prescription> prescriptions = prescriptionRepository.findVerifiedNotDispensed();

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get prescription history for patient within date range
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionHistory(UUID patientId, LocalDate startDate, LocalDate endDate) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientAndDateRange(
                patientId, startDate, endDate);

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get controlled drug prescriptions
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getControlledDrugPrescriptions(LocalDate startDate, LocalDate endDate) {
        List<Prescription> prescriptions = prescriptionRepository.findControlledDrugPrescriptions(
                startDate, endDate);

        return prescriptions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check drug interactions for prescription
     */
    private void checkDrugInteractions(Prescription prescription) {
        List<PrescriptionItem> items = prescription.getItems();

        if (items.size() < 2) {
            prescription.setHasInteractions(false);
            return;
        }

        // Get all drug IDs
        List<UUID> drugIds = items.stream()
                .map(item -> item.getDrug().getId())
                .collect(Collectors.toList());

        // Find interactions
        List<DrugInteraction> interactions = drugInteractionRepository.findInteractionsByDrugList(drugIds);

        if (interactions.isEmpty()) {
            prescription.setHasInteractions(false);
            prescription.setInteractionWarnings(null);
            return;
        }

        // Build interaction warnings
        StringBuilder warnings = new StringBuilder();
        for (DrugInteraction interaction : interactions) {
            // Check if both drugs are in the prescription
            boolean drug1Present = drugIds.contains(interaction.getDrug1().getId());
            boolean drug2Present = drugIds.contains(interaction.getDrug2().getId());

            if (drug1Present && drug2Present) {
                warnings.append(String.format("[%s] %s ↔ %s: %s",
                        interaction.getSeverity().getDisplayName(),
                        interaction.getDrug1().getGenericName(),
                        interaction.getDrug2().getGenericName(),
                        interaction.getDescription()
                ));

                if (interaction.getManagement() != null) {
                    warnings.append(" | Management: ").append(interaction.getManagement());
                }

                warnings.append("\n");

                // Update item-level warnings
                updateItemInteractionWarnings(prescription, interaction);
            }
        }

        prescription.setHasInteractions(true);
        prescription.setInteractionWarnings(warnings.toString());
    }

    /**
     * Update interaction warnings for specific items
     */
    private void updateItemInteractionWarnings(Prescription prescription, DrugInteraction interaction) {
        for (PrescriptionItem item : prescription.getItems()) {
            if (item.getDrug().getId().equals(interaction.getDrug1().getId()) ||
                item.getDrug().getId().equals(interaction.getDrug2().getId())) {

                String warning = String.format("[%s] Interaksi dengan %s",
                        interaction.getSeverity().getDisplayName(),
                        item.getDrug().getId().equals(interaction.getDrug1().getId()) ?
                                interaction.getDrug2().getGenericName() :
                                interaction.getDrug1().getGenericName()
                );

                if (item.getInteractionWarnings() == null) {
                    item.setInteractionWarnings(warning);
                } else {
                    item.setInteractionWarnings(item.getInteractionWarnings() + "\n" + warning);
                }
            }
        }
    }

    /**
     * Generate prescription number
     */
    private String generatePrescriptionNumber() {
        // Format: RX-YYYYMMDD-NNNN
        String dateStr = LocalDate.now().toString().replace("-", "");
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return "RX-" + dateStr + "-" + randomStr;
    }

    /**
     * Map prescription entity to response DTO
     */
    private PrescriptionResponse mapToResponse(Prescription prescription) {
        List<PrescriptionItemResponse> itemResponses = prescription.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .prescriptionNumber(prescription.getPrescriptionNumber())
                .patientId(prescription.getPatientId())
                .encounterId(prescription.getEncounterId())
                .doctorId(prescription.getDoctorId())
                .prescriptionDate(prescription.getPrescriptionDate())
                .prescriptionType(prescription.getPrescriptionType())
                .status(prescription.getStatus())
                .validUntil(prescription.getValidUntil())
                .diagnosis(prescription.getDiagnosis())
                .icd10Codes(prescription.getIcd10Codes())
                .specialInstructions(prescription.getSpecialInstructions())
                .allergies(prescription.getAllergies())
                .hasInteractions(prescription.getHasInteractions())
                .interactionWarnings(prescription.getInteractionWarnings())
                .submittedAt(prescription.getSubmittedAt())
                .verifiedAt(prescription.getVerifiedAt())
                .verifiedBy(prescription.getVerifiedBy())
                .dispensedAt(prescription.getDispensedAt())
                .dispensedBy(prescription.getDispensedBy())
                .isControlled(prescription.getIsControlled())
                .requiresAuthorization(prescription.getRequiresAuthorization())
                .authorizationNumber(prescription.getAuthorizationNumber())
                .notes(prescription.getNotes())
                .active(prescription.getActive())
                .items(itemResponses)
                .createdAt(prescription.getCreatedAt())
                .createdBy(prescription.getCreatedBy())
                .updatedAt(prescription.getUpdatedAt())
                .updatedBy(prescription.getUpdatedBy())
                .build();
    }

    /**
     * Map prescription item entity to response DTO
     */
    private PrescriptionItemResponse mapItemToResponse(PrescriptionItem item) {
        return PrescriptionItemResponse.builder()
                .id(item.getId())
                .lineNumber(item.getLineNumber())
                .drugId(item.getDrug().getId())
                .drugCode(item.getDrugCode())
                .drugName(item.getDrugName())
                .strength(item.getStrength())
                .dosageForm(item.getDosageForm())
                .doseQuantity(item.getDoseQuantity())
                .doseUnit(item.getDoseUnit())
                .frequency(item.getFrequency())
                .customFrequency(item.getCustomFrequency())
                .route(item.getRoute())
                .durationDays(item.getDurationDays())
                .quantityToDispense(item.getQuantityToDispense())
                .quantityDispensed(item.getQuantityDispensed())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .instructions(item.getInstructions())
                .specialInstructions(item.getSpecialInstructions())
                .isPrn(item.getIsPrn())
                .prnIndication(item.getPrnIndication())
                .substitutionAllowed(item.getSubstitutionAllowed())
                .substitutedDrugId(item.getSubstitutedDrugId())
                .substitutedDrugName(item.getSubstitutedDrugName())
                .substitutionReason(item.getSubstitutionReason())
                .isControlled(item.getIsControlled())
                .isHighAlert(item.getIsHighAlert())
                .interactionWarnings(item.getInteractionWarnings())
                .labelPrinted(item.getLabelPrinted())
                .build();
    }
}
