package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.clinical.repository.ReferralLetterRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Referral Service.
 *
 * Handles complete referral letter workflow including creation, signature,
 * sending, acceptance/rejection, document generation, and integration
 * with BPJS VClaim, PCare, and SATUSEHAT systems.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReferralService {

    private final ReferralLetterRepository referralLetterRepository;
    private final EncounterRepository encounterRepository;

    // ========== Referral Letter CRUD Operations ==========

    public ReferralLetterResponse createReferralLetter(ReferralLetterRequest request) {
        log.info("Creating referral letter for patient: {}", request.getPatientId());

        // Validate encounter if provided
        if (request.getEncounterId() != null) {
            Encounter encounter = encounterRepository.findById(request.getEncounterId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Encounter tidak ditemukan dengan ID: " + request.getEncounterId()
                ));

            // Check if referral already exists for this encounter
            if (referralLetterRepository.existsByEncounterId(request.getEncounterId())) {
                throw new ValidationException(
                    "Referral letter sudah ada untuk encounter ini"
                );
            }
        }

        // Parse enums from strings
        ReferralType referralType = ReferralType.valueOf(request.getReferralType());
        ReferralUrgency urgency = ReferralUrgency.valueOf(request.getUrgency());

        // Set valid until date based on urgency if not provided
        LocalDate validUntil = request.getValidUntil();
        if (validUntil == null) {
            int daysValid = urgency.getMaxDaysUntilTransfer();
            if (daysValid > 0) {
                validUntil = LocalDate.now().plusDays(daysValid);
            }
        }

        // Create referral letter
        ReferralLetter referral = ReferralLetter.builder()
            .referralNumber(generateReferralNumber())
            .encounterId(request.getEncounterId())
            .patientId(request.getPatientId())
            .referralType(referralType)
            .referralReason(request.getReferralReason())
            .urgency(urgency)
            .referringFacility(request.getReferringFacility())
            .referringDepartment(request.getReferringDepartment())
            .referringDoctorId(request.getReferringDoctorId())
            .referringDoctorName(request.getReferringDoctorName())
            .referringDoctorPhone(request.getReferringDoctorPhone())
            .referredToFacility(request.getReferredToFacility())
            .referredToDepartment(request.getReferredToDepartment())
            .referredToDoctor(request.getReferredToDoctor())
            .referredToSpecialty(request.getReferredToSpecialty())
            .referralDate(request.getReferralDate() != null ? request.getReferralDate() : LocalDate.now())
            .referralCreatedAt(LocalDateTime.now())
            .validUntil(validUntil)
            .chiefComplaint(request.getChiefComplaint())
            .anamnesis(request.getAnamnesis())
            .physicalExamination(request.getPhysicalExamination())
            .clinicalSummary(request.getClinicalSummary())
            .relevantHistory(request.getRelevantHistory())
            .primaryDiagnosisCode(request.getPrimaryDiagnosisCode())
            .primaryDiagnosisText(request.getPrimaryDiagnosisText())
            .secondaryDiagnoses(request.getSecondaryDiagnoses())
            .currentMedications(request.getCurrentMedications())
            .treatmentsGiven(request.getTreatmentsGiven())
            .labResultsSummary(request.getLabResultsSummary())
            .imagingResultsSummary(request.getImagingResultsSummary())
            .otherInvestigations(request.getOtherInvestigations())
            .vitalSigns(request.getVitalSigns())
            .reasonForReferral(request.getReasonForReferral())
            .servicesRequested(request.getServicesRequested())
            .appointmentRequested(request.getAppointmentRequested() != null ? request.getAppointmentRequested() : false)
            .admissionRequested(request.getAdmissionRequested() != null ? request.getAdmissionRequested() : false)
            .transportRequired(request.getTransportRequired() != null ? request.getTransportRequired() : false)
            .transportMode(request.getTransportMode())
            .patientConditionForTransport(request.getPatientConditionForTransport())
            .isBpjsReferral(request.getIsBpjsReferral() != null ? request.getIsBpjsReferral() : false)
            .bpjsSepNumber(request.getBpjsSepNumber())
            .bpjsReferralCode(request.getBpjsReferralCode())
            .referralStatus(ReferralStatus.DRAFT)
            .signed(false)
            .documentGenerated(false)
            .bpjsVclaimSubmitted(false)
            .pcareSubmitted(false)
            .satusehatSubmitted(false)
            .notes(request.getNotes())
            .build();

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter created: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse getReferralLetterById(UUID id) {
        ReferralLetter referral = findReferralLetterById(id);
        return mapToResponse(referral);
    }

    public ReferralLetterResponse getReferralLetterByNumber(String referralNumber) {
        ReferralLetter referral = referralLetterRepository.findByReferralNumber(referralNumber)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Referral letter tidak ditemukan dengan nomor: " + referralNumber
            ));
        return mapToResponse(referral);
    }

    public ReferralLetterResponse getReferralLetterByEncounterId(UUID encounterId) {
        ReferralLetter referral = referralLetterRepository.findByEncounterId(encounterId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Referral letter tidak ditemukan untuk encounter: " + encounterId
            ));
        return mapToResponse(referral);
    }

    public List<ReferralLetterResponse> getReferralLettersByPatientId(UUID patientId) {
        return referralLetterRepository.findByPatientIdOrderByReferralCreatedAtDesc(patientId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public ReferralLetterResponse updateReferralLetter(UUID id, ReferralLetterRequest request) {
        log.info("Updating referral letter: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Only allow updates if in DRAFT status
        if (referral.getReferralStatus() != ReferralStatus.DRAFT) {
            throw new ValidationException(
                "Hanya referral dengan status DRAFT yang dapat diubah. Status saat ini: " +
                referral.getReferralStatus().getDisplayName()
            );
        }

        // Update fields
        referral.setReferralType(ReferralType.valueOf(request.getReferralType()));
        referral.setReferralReason(request.getReferralReason());
        referral.setUrgency(ReferralUrgency.valueOf(request.getUrgency()));
        referral.setReferredToFacility(request.getReferredToFacility());
        referral.setReferredToDepartment(request.getReferredToDepartment());
        referral.setReferredToDoctor(request.getReferredToDoctor());
        referral.setReferredToSpecialty(request.getReferredToSpecialty());
        referral.setValidUntil(request.getValidUntil());
        referral.setChiefComplaint(request.getChiefComplaint());
        referral.setAnamnesis(request.getAnamnesis());
        referral.setPhysicalExamination(request.getPhysicalExamination());
        referral.setClinicalSummary(request.getClinicalSummary());
        referral.setRelevantHistory(request.getRelevantHistory());
        referral.setPrimaryDiagnosisCode(request.getPrimaryDiagnosisCode());
        referral.setPrimaryDiagnosisText(request.getPrimaryDiagnosisText());
        referral.setSecondaryDiagnoses(request.getSecondaryDiagnoses());
        referral.setCurrentMedications(request.getCurrentMedications());
        referral.setTreatmentsGiven(request.getTreatmentsGiven());
        referral.setLabResultsSummary(request.getLabResultsSummary());
        referral.setImagingResultsSummary(request.getImagingResultsSummary());
        referral.setOtherInvestigations(request.getOtherInvestigations());
        referral.setVitalSigns(request.getVitalSigns());
        referral.setReasonForReferral(request.getReasonForReferral());
        referral.setServicesRequested(request.getServicesRequested());
        referral.setAppointmentRequested(request.getAppointmentRequested());
        referral.setAdmissionRequested(request.getAdmissionRequested());
        referral.setTransportRequired(request.getTransportRequired());
        referral.setTransportMode(request.getTransportMode());
        referral.setPatientConditionForTransport(request.getPatientConditionForTransport());
        referral.setNotes(request.getNotes());

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter updated: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    // ========== Workflow Operations ==========

    public ReferralLetterResponse signReferralLetter(UUID id, SignReferralRequest request) {
        log.info("Signing referral letter: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate status
        if (referral.getReferralStatus() != ReferralStatus.DRAFT &&
            referral.getReferralStatus() != ReferralStatus.PENDING_SIGNATURE) {
            throw new ValidationException(
                "Hanya referral dengan status DRAFT atau PENDING_SIGNATURE yang dapat ditandatangani. " +
                "Status saat ini: " + referral.getReferralStatus().getDisplayName()
            );
        }

        // Validate that all required fields are filled
        validateReferralCompleteness(referral);

        // Sign the referral
        referral.sign(request.getDoctorId(), request.getDoctorName(), request.getDigitalSignature());
        referral.setReferralStatus(ReferralStatus.SIGNED);

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter signed: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse sendReferralLetter(UUID id) {
        log.info("Sending referral letter: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Send will validate that it's signed
        referral.send();

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter sent: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse acceptReferralLetter(UUID id, AcceptReferralRequest request) {
        log.info("Accepting referral letter: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate status
        if (referral.getReferralStatus() != ReferralStatus.SENT) {
            throw new ValidationException(
                "Hanya referral dengan status SENT yang dapat diterima. " +
                "Status saat ini: " + referral.getReferralStatus().getDisplayName()
            );
        }

        // Accept the referral
        referral.accept(request.getAcceptedBy(), request.getAppointmentDate());

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter accepted: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse rejectReferralLetter(UUID id, RejectReferralRequest request) {
        log.info("Rejecting referral letter: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate status
        if (referral.getReferralStatus() != ReferralStatus.SENT) {
            throw new ValidationException(
                "Hanya referral dengan status SENT yang dapat ditolak. " +
                "Status saat ini: " + referral.getReferralStatus().getDisplayName()
            );
        }

        // Reject the referral
        referral.reject(request.getRejectionReason());

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter rejected: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse markPatientTransferred(UUID id) {
        log.info("Marking patient as transferred for referral: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate status
        if (referral.getReferralStatus() != ReferralStatus.ACCEPTED) {
            throw new ValidationException(
                "Hanya referral dengan status ACCEPTED yang dapat ditandai sebagai transferred. " +
                "Status saat ini: " + referral.getReferralStatus().getDisplayName()
            );
        }

        referral.markPatientTransferred();

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter marked as patient transferred: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse completeReferral(UUID id) {
        log.info("Completing referral: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate status
        if (referral.getReferralStatus() != ReferralStatus.PATIENT_TRANSFERRED) {
            throw new ValidationException(
                "Hanya referral dengan status PATIENT_TRANSFERRED yang dapat diselesaikan. " +
                "Status saat ini: " + referral.getReferralStatus().getDisplayName()
            );
        }

        referral.complete();

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter completed: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse cancelReferral(UUID id) {
        log.info("Cancelling referral: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Cancel will validate if it can be cancelled
        referral.cancel();

        referral = referralLetterRepository.save(referral);
        log.info("Referral letter cancelled: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    // ========== Document Generation ==========

    public ReferralLetterResponse generateDocument(UUID id) {
        log.info("Generating document for referral: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate that referral is signed
        if (!Boolean.TRUE.equals(referral.getSigned())) {
            throw new ValidationException(
                "Referral harus ditandatangani sebelum dokumen dapat dihasilkan"
            );
        }

        // TODO: Implement actual PDF generation with QR code
        // For now, create placeholder data
        String documentUrl = "/documents/referrals/" + referral.getReferralNumber() + ".pdf";
        String qrCode = "QR-" + referral.getReferralNumber();
        String qrCodeUrl = "/qrcodes/referrals/" + referral.getReferralNumber() + ".png";

        referral.generateDocument(documentUrl, qrCode, qrCodeUrl);

        referral = referralLetterRepository.save(referral);
        log.info("Document generated for referral: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    // ========== Integration Operations ==========

    public ReferralLetterResponse submitToVClaim(UUID id, VClaimSubmissionRequest request) {
        log.info("Submitting referral to BPJS VClaim: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate that it's a BPJS referral
        if (!Boolean.TRUE.equals(referral.getIsBpjsReferral())) {
            throw new ValidationException(
                "Hanya referral BPJS yang dapat disubmit ke VClaim"
            );
        }

        // Validate that referral is signed
        if (!Boolean.TRUE.equals(referral.getSigned())) {
            throw new ValidationException(
                "Referral harus ditandatangani sebelum dapat disubmit ke VClaim"
            );
        }

        // Check if already submitted
        if (Boolean.TRUE.equals(referral.getBpjsVclaimSubmitted()) &&
            !Boolean.TRUE.equals(request.getForceResubmit())) {
            throw new ValidationException(
                "Referral sudah pernah disubmit ke VClaim. Gunakan forceResubmit untuk submit ulang."
            );
        }

        // TODO: Implement actual VClaim API integration
        // For now, just mark as submitted

        referral.submitToVClaim(request.getReferenceNumber(), request.getResponse());

        referral = referralLetterRepository.save(referral);
        log.info("Referral submitted to VClaim: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse submitToPCare(UUID id, PCareSubmissionRequest request) {
        log.info("Submitting referral to PCare: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate that referral is signed
        if (!Boolean.TRUE.equals(referral.getSigned())) {
            throw new ValidationException(
                "Referral harus ditandatangani sebelum dapat disubmit ke PCare"
            );
        }

        // Check if already submitted
        if (Boolean.TRUE.equals(referral.getPcareSubmitted()) &&
            !Boolean.TRUE.equals(request.getForceResubmit())) {
            throw new ValidationException(
                "Referral sudah pernah disubmit ke PCare. Gunakan forceResubmit untuk submit ulang."
            );
        }

        // TODO: Implement actual PCare API integration
        // For now, just mark as submitted

        referral.submitToPCare(request.getReferenceNumber());

        referral = referralLetterRepository.save(referral);
        log.info("Referral submitted to PCare: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    public ReferralLetterResponse submitToSatusehat(UUID id, String referenceId) {
        log.info("Submitting referral to SATUSEHAT: {}", id);

        ReferralLetter referral = findReferralLetterById(id);

        // Validate that referral is signed
        if (!Boolean.TRUE.equals(referral.getSigned())) {
            throw new ValidationException(
                "Referral harus ditandatangani sebelum dapat disubmit ke SATUSEHAT"
            );
        }

        // TODO: Implement actual SATUSEHAT API integration
        // For now, just mark as submitted

        referral.submitToSatusehat(referenceId);

        referral = referralLetterRepository.save(referral);
        log.info("Referral submitted to SATUSEHAT: {}", referral.getReferralNumber());

        return mapToResponse(referral);
    }

    // ========== Query Operations ==========

    public List<ReferralLetterResponse> getReferralsByStatus(ReferralStatus status) {
        return referralLetterRepository.findByReferralStatusOrderByReferralCreatedAtDesc(status)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ReferralLetterResponse> getReferralsByType(ReferralType type) {
        return referralLetterRepository.findByReferralTypeOrderByReferralCreatedAtDesc(type)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ReferralLetterResponse> getReferralsByUrgency(ReferralUrgency urgency) {
        return referralLetterRepository.findByUrgencyOrderByReferralCreatedAtDesc(urgency)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ReferralLetterResponse> getPendingVClaimSubmissions() {
        return referralLetterRepository.findPendingVClaimSubmissions()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ReferralLetterResponse> getPendingPCareSubmissions() {
        return referralLetterRepository.findPendingPCareSubmissions()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ReferralLetterResponse> getExpiredReferrals() {
        return referralLetterRepository.findExpiredReferrals(LocalDate.now())
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<ReferralLetterResponse> getUrgentAndEmergencyReferrals() {
        return referralLetterRepository.findUrgentAndEmergencyReferrals()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ========== Helper Methods ==========

    private ReferralLetter findReferralLetterById(UUID id) {
        return referralLetterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Referral letter tidak ditemukan dengan ID: " + id
            ));
    }

    private String generateReferralNumber() {
        // Format: REF-YYYYMMDD-NNNN
        String dateStr = LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        long count = referralLetterRepository.count() + 1;
        return String.format("REF-%s-%04d", dateStr, count);
    }

    private void validateReferralCompleteness(ReferralLetter referral) {
        List<String> errors = new java.util.ArrayList<>();

        if (referral.getChiefComplaint() == null || referral.getChiefComplaint().isBlank()) {
            errors.add("Chief complaint is required");
        }
        if (referral.getAnamnesis() == null || referral.getAnamnesis().isBlank()) {
            errors.add("Anamnesis is required");
        }
        if (referral.getPhysicalExamination() == null || referral.getPhysicalExamination().isBlank()) {
            errors.add("Physical examination is required");
        }
        if (referral.getClinicalSummary() == null || referral.getClinicalSummary().isBlank()) {
            errors.add("Clinical summary is required");
        }
        if (referral.getPrimaryDiagnosisText() == null || referral.getPrimaryDiagnosisText().isBlank()) {
            errors.add("Primary diagnosis is required");
        }
        if (referral.getReasonForReferral() == null || referral.getReasonForReferral().isBlank()) {
            errors.add("Reason for referral is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(
                "Referral tidak lengkap: " + String.join(", ", errors)
            );
        }
    }

    // ========== Mapping Methods ==========

    private ReferralLetterResponse mapToResponse(ReferralLetter referral) {
        return ReferralLetterResponse.builder()
            .id(referral.getId())
            .referralNumber(referral.getReferralNumber())
            .encounterId(referral.getEncounterId())
            .patientId(referral.getPatientId())
            .referralType(referral.getReferralType().name())
            .referralTypeDisplay(referral.getReferralType().getDisplayName())
            .referralStatus(referral.getReferralStatus().name())
            .referralStatusDisplay(referral.getReferralStatus().getDisplayName())
            .referralReason(referral.getReferralReason())
            .urgency(referral.getUrgency().name())
            .urgencyDisplay(referral.getUrgency().getDisplayName())
            .referringFacility(referral.getReferringFacility())
            .referringDepartment(referral.getReferringDepartment())
            .referringDoctorId(referral.getReferringDoctorId())
            .referringDoctorName(referral.getReferringDoctorName())
            .referringDoctorPhone(referral.getReferringDoctorPhone())
            .referredToFacility(referral.getReferredToFacility())
            .referredToDepartment(referral.getReferredToDepartment())
            .referredToDoctor(referral.getReferredToDoctor())
            .referredToSpecialty(referral.getReferredToSpecialty())
            .referralDate(referral.getReferralDate())
            .referralCreatedAt(referral.getReferralCreatedAt())
            .validUntil(referral.getValidUntil())
            .isExpired(referral.isExpired())
            .chiefComplaint(referral.getChiefComplaint())
            .anamnesis(referral.getAnamnesis())
            .physicalExamination(referral.getPhysicalExamination())
            .clinicalSummary(referral.getClinicalSummary())
            .relevantHistory(referral.getRelevantHistory())
            .primaryDiagnosisCode(referral.getPrimaryDiagnosisCode())
            .primaryDiagnosisText(referral.getPrimaryDiagnosisText())
            .secondaryDiagnoses(referral.getSecondaryDiagnoses())
            .currentMedications(referral.getCurrentMedications())
            .treatmentsGiven(referral.getTreatmentsGiven())
            .labResultsSummary(referral.getLabResultsSummary())
            .imagingResultsSummary(referral.getImagingResultsSummary())
            .otherInvestigations(referral.getOtherInvestigations())
            .vitalSigns(referral.getVitalSigns())
            .reasonForReferral(referral.getReasonForReferral())
            .servicesRequested(referral.getServicesRequested())
            .appointmentRequested(referral.getAppointmentRequested())
            .admissionRequested(referral.getAdmissionRequested())
            .transportRequired(referral.getTransportRequired())
            .transportMode(referral.getTransportMode())
            .patientConditionForTransport(referral.getPatientConditionForTransport())
            .referralAccepted(referral.getReferralAccepted())
            .acceptanceDate(referral.getAcceptanceDate())
            .acceptedBy(referral.getAcceptedBy())
            .appointmentDate(referral.getAppointmentDate())
            .rejectionReason(referral.getRejectionReason())
            .isBpjsReferral(referral.getIsBpjsReferral())
            .bpjsSepNumber(referral.getBpjsSepNumber())
            .bpjsReferralCode(referral.getBpjsReferralCode())
            .signed(referral.getSigned())
            .signedAt(referral.getSignedAt())
            .signedById(referral.getSignedById())
            .signedByName(referral.getSignedByName())
            .documentGenerated(referral.getDocumentGenerated())
            .documentUrl(referral.getDocumentUrl())
            .documentGeneratedAt(referral.getDocumentGeneratedAt())
            .qrCode(referral.getQrCode())
            .qrCodeUrl(referral.getQrCodeUrl())
            .bpjsVclaimSubmitted(referral.getBpjsVclaimSubmitted())
            .bpjsVclaimSubmissionDate(referral.getBpjsVclaimSubmissionDate())
            .bpjsVclaimReferenceNumber(referral.getBpjsVclaimReferenceNumber())
            .pcareSubmitted(referral.getPcareSubmitted())
            .pcareSubmissionDate(referral.getPcareSubmissionDate())
            .pcareReferenceNumber(referral.getPcareReferenceNumber())
            .satusehatSubmitted(referral.getSatusehatSubmitted())
            .satusehatSubmissionDate(referral.getSatusehatSubmissionDate())
            .satusehatServiceRequestId(referral.getSatusehatServiceRequestId())
            .notes(referral.getNotes())
            .isPending(referral.isPending())
            .requiresVClaimIntegration(referral.requiresVClaimIntegration())
            .canBeCancelled(referral.getReferralStatus().canBeCancelled())
            .createdAt(referral.getCreatedAt())
            .updatedAt(referral.getUpdatedAt())
            .createdBy(referral.getCreatedBy())
            .updatedBy(referral.getUpdatedBy())
            .build();
    }
}
