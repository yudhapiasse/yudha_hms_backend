package com.yudha.hms.registration.service;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.patient.repository.PatientRepository;
import com.yudha.hms.registration.dto.AdmissionRequest;
import com.yudha.hms.registration.dto.AdmissionResponse;
import com.yudha.hms.registration.dto.WristbandData;
import com.yudha.hms.registration.entity.*;
import com.yudha.hms.registration.repository.*;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.patient.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for inpatient admission management.
 * Handles admission creation, bed assignment, deposit calculation, and discharge.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InpatientAdmissionService {

    private final InpatientAdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final RoomService roomService;
    private final BedRepository bedRepository;
    private final BedAssignmentRepository bedAssignmentRepository;
    private final AdmissionDiagnosisRepository diagnosisRepository;
    private final BarcodeService barcodeService;

    /**
     * Create a new inpatient admission.
     * Validates patient, checks bed availability, assigns bed, calculates deposit.
     *
     * @param request admission request
     * @return admission response
     */
    @Transactional
    public AdmissionResponse createAdmission(AdmissionRequest request) {
        log.info("Creating inpatient admission for patient: {}", request.getPatientId());

        // 1. Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", request.getPatientId()));

        // 2. Check if patient already has active admission
        if (admissionRepository.hasActiveAdmission(request.getPatientId())) {
            throw new BusinessException("Patient already has an active admission");
        }

        // 3. Find available room and bed
        Room room = findAndAssignRoom(request);
        Bed bed = findAndAssignBed(room, request);

        // 4. Calculate deposit
        BigDecimal requiredDeposit = calculateRequiredDeposit(room, request.getDepositDays());

        // 5. Generate admission number
        String admissionNumber = generateAdmissionNumber();

        // 6. Create admission entity
        InpatientAdmission admission = buildAdmission(request, patient, room, bed, admissionNumber, requiredDeposit);

        // 7. Save admission
        admission = admissionRepository.save(admission);
        log.info("Admission created: {} for patient: {}", admissionNumber, patient.getMrn());

        // 8. Create initial bed assignment
        createBedAssignment(admission, room, bed, "INITIAL", null);

        // 9. Add diagnoses if provided
        if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
            addDiagnoses(admission, request.getDiagnoses());
        }

        // 10. Update bed status
        bed.occupy(patient.getId(), admission.getId());
        bedRepository.save(bed);

        // 11. Update room availability
        roomService.occupyBed(room.getId());

        return convertToResponse(admission, patient);
    }

    /**
     * Get admission by ID.
     *
     * @param admissionId admission ID
     * @return admission response
     */
    @Transactional(readOnly = true)
    public AdmissionResponse getAdmission(UUID admissionId) {
        log.info("Fetching admission: {}", admissionId);
        InpatientAdmission admission = admissionRepository.findById(admissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Admission", "ID", admissionId));

        Patient patient = patientRepository.findById(admission.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", admission.getPatientId()));

        return convertToResponse(admission, patient);
    }

    /**
     * Get admission by admission number.
     *
     * @param admissionNumber admission number
     * @return admission response
     */
    @Transactional(readOnly = true)
    public AdmissionResponse getAdmissionByNumber(String admissionNumber) {
        log.info("Fetching admission by number: {}", admissionNumber);
        InpatientAdmission admission = admissionRepository.findByAdmissionNumber(admissionNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Admission", "admission number", admissionNumber));

        Patient patient = patientRepository.findById(admission.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", admission.getPatientId()));

        return convertToResponse(admission, patient);
    }

    /**
     * Get all active admissions.
     *
     * @return list of active admissions
     */
    @Transactional(readOnly = true)
    public List<AdmissionResponse> getAllActiveAdmissions() {
        log.info("Fetching all active admissions");
        List<InpatientAdmission> admissions = admissionRepository.findAllActive();
        return admissions.stream()
            .map(admission -> {
                Patient patient = patientRepository.findById(admission.getPatientId()).orElse(null);
                return convertToResponse(admission, patient);
            })
            .collect(Collectors.toList());
    }

    /**
     * Get admissions for a patient.
     *
     * @param patientId patient ID
     * @return list of admissions
     */
    @Transactional(readOnly = true)
    public List<AdmissionResponse> getPatientAdmissions(UUID patientId) {
        log.info("Fetching admissions for patient: {}", patientId);
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", patientId));

        List<InpatientAdmission> admissions = admissionRepository.findByPatientId(patientId);
        return admissions.stream()
            .map(admission -> convertToResponse(admission, patient))
            .collect(Collectors.toList());
    }

    /**
     * Discharge a patient.
     *
     * @param admissionId admission ID
     * @param dischargeType discharge type (ROUTINE, AMA, TRANSFER, DECEASED)
     * @param dischargeDisposition patient disposition
     * @param dischargeSummary discharge summary
     * @return updated admission response
     */
    @Transactional
    public AdmissionResponse dischargePatient(UUID admissionId, String dischargeType,
                                               String dischargeDisposition, String dischargeSummary) {
        log.info("Discharging patient from admission: {}", admissionId);

        InpatientAdmission admission = admissionRepository.findById(admissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Admission", "ID", admissionId));

        if (!admission.isActive()) {
            throw new BusinessException("Admission is not active and cannot be discharged");
        }

        UUID patientId = admission.getPatientId();
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", patientId));

        // Discharge the admission
        admission.discharge(dischargeType, dischargeDisposition);
        admission.setDischargeSummary(dischargeSummary);

        // Release current bed assignment
        bedAssignmentRepository.findCurrentByAdmissionId(admissionId)
            .ifPresent(assignment -> assignment.release("SYSTEM"));

        // Save admission
        admission = admissionRepository.save(admission);
        log.info("Patient discharged from admission: {}", admission.getAdmissionNumber());

        return convertToResponse(admission, patient);
    }

    /**
     * Transfer patient to different room/bed.
     *
     * @param admissionId admission ID
     * @param newRoomId new room ID
     * @param newBedId new bed ID (optional, will find available if null)
     * @param transferReason reason for transfer
     * @return updated admission response
     */
    @Transactional
    public AdmissionResponse transferPatient(UUID admissionId, UUID newRoomId, UUID newBedId, String transferReason) {
        log.info("Transferring patient from admission: {} to room: {}", admissionId, newRoomId);

        InpatientAdmission admission = admissionRepository.findById(admissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Admission", "ID", admissionId));

        if (!admission.isActive()) {
            throw new BusinessException("Admission is not active and cannot be transferred");
        }

        UUID patientId = admission.getPatientId();
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", patientId));

        // Get old room and bed
        Room oldRoom = admission.getRoom();
        Bed oldBed = admission.getBed();

        // Get new room
        Room newRoom = roomService.getRoomById(newRoomId);
        if (!newRoom.hasAvailableBeds()) {
            throw new BusinessException("Target room has no available beds");
        }

        // Find or get new bed
        Bed newBed;
        if (newBedId != null) {
            newBed = roomService.getBedById(newBedId);
            if (!newBed.isAvailable()) {
                throw new BusinessException("Target bed is not available");
            }
        } else {
            newBed = roomService.findAvailableBedInRoom(newRoomId);
            if (newBed == null) {
                throw new BusinessException("No available beds in target room");
            }
        }

        // Determine transfer type
        String assignmentType = determineTransferType(oldRoom.getRoomClass(), newRoom.getRoomClass());

        // Release old bed assignment
        bedAssignmentRepository.findCurrentByAdmissionId(admissionId)
            .ifPresent(assignment -> assignment.release("SYSTEM"));

        // Release old bed
        if (oldBed != null) {
            oldBed.release();
            bedRepository.save(oldBed);
            roomService.releaseBed(oldRoom.getId());
        }

        // Occupy new bed
        newBed.occupy(patient.getId(), admission.getId());
        bedRepository.save(newBed);
        roomService.occupyBed(newRoom.getId());

        // Update admission
        admission.setRoom(newRoom);
        admission.setBed(newBed);
        admission.setRoomClass(newRoom.getRoomClass());
        admission.setRoomRatePerDay(newRoom.getBaseRoomRate());

        // Create new bed assignment
        createBedAssignment(admission, newRoom, newBed, assignmentType, transferReason);

        admission = admissionRepository.save(admission);
        log.info("Patient transferred to room: {} bed: {}", newRoom.getRoomNumber(), newBed.getBedNumber());

        return convertToResponse(admission, patient);
    }

    /**
     * Generate wristband data for a patient.
     *
     * @param admissionId admission ID
     * @return wristband data with barcodes
     */
    @Transactional(readOnly = true)
    public WristbandData generateWristbandData(UUID admissionId) {
        log.info("Generating wristband data for admission: {}", admissionId);

        InpatientAdmission admission = admissionRepository.findById(admissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Admission", "ID", admissionId));

        Patient patient = patientRepository.findById(admission.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", admission.getPatientId()));

        // Calculate age
        Integer age = patient.getBirthDate() != null ?
            Period.between(patient.getBirthDate(), LocalDate.now()).getYears() : null;

        // Generate barcodes and QR codes
        String mrnBarcode = barcodeService.generatePatientBarcode(patient.getMrn(), 200, 50);
        String mrnQrCode = barcodeService.generatePatientQRCode(patient.getMrn(), 150, 150);
        String admissionBarcode = barcodeService.generatePatientBarcode(admission.getAdmissionNumber(), 200, 50);
        String admissionQrCode = barcodeService.generatePatientQRCode(admission.getAdmissionNumber(), 150, 150);

        // Build wristband data
        WristbandData wristband = WristbandData.builder()
            .patientName(patient.getFullName())
            .mrn(patient.getMrn())
            .birthDate(patient.getBirthDate())
            .age(age)
            .gender(patient.getGender() != null ? patient.getGender().name() : null)
            .admissionNumber(admission.getAdmissionNumber())
            .admissionDate(admission.getAdmissionDate())
            .roomClass(admission.getRoomClass())
            .roomNumber(admission.getRoom() != null ? admission.getRoom().getRoomNumber() : null)
            .bedNumber(admission.getBed() != null ? admission.getBed().getBedNumber() : null)
            .building(admission.getRoom() != null ? admission.getRoom().getBuilding() : null)
            .floor(admission.getRoom() != null ? admission.getRoom().getFloor() : null)
            .attendingDoctorName(admission.getAttendingDoctorName())
            .hasAllergies(admission.getHasAllergies())
            .allergyAlert(admission.getAllergyNotes())
            .requiresIsolation(admission.getRequiresIsolation())
            .isolationType(admission.getIsolationType())
            .mrnBarcode(mrnBarcode)
            .mrnQrCode(mrnQrCode)
            .admissionBarcode(admissionBarcode)
            .admissionQrCode(admissionQrCode)
            .generatedAt(LocalDateTime.now())
            .build();

        // Determine wristband color
        wristband.setWristbandColor(wristband.determineWristbandColor());

        return wristband;
    }

    // ========== Private Helper Methods ==========

    private Room findAndAssignRoom(AdmissionRequest request) {
        Room room;

        if (request.getPreferredRoomId() != null) {
            room = roomService.getRoomById(request.getPreferredRoomId());
            if (!room.hasAvailableBeds() || room.getRoomClass() != request.getRoomClass()) {
                throw new BusinessException("Preferred room is not available or does not match requested class");
            }
        } else {
            room = roomService.findBestAvailableRoom(request.getRoomClass());
            if (room == null) {
                throw new BusinessException("No available rooms for class: " + request.getRoomClass());
            }
        }

        return room;
    }

    private Bed findAndAssignBed(Room room, AdmissionRequest request) {
        Bed bed;

        if (request.getPreferredBedId() != null) {
            bed = roomService.getBedById(request.getPreferredBedId());
            if (!bed.isAvailable() || !bed.getRoom().getId().equals(room.getId())) {
                throw new BusinessException("Preferred bed is not available or not in selected room");
            }
        } else {
            bed = roomService.findAvailableBedInRoom(room.getId());
            if (bed == null) {
                throw new BusinessException("No available beds in room: " + room.getRoomNumber());
            }
        }

        return bed;
    }

    private BigDecimal calculateRequiredDeposit(Room room, Integer depositDays) {
        int days = depositDays != null ? depositDays : 3; // Default 3 days
        return room.getBaseRoomRate().multiply(BigDecimal.valueOf(days));
    }

    private String generateAdmissionNumber() {
        // Format: ADM-YYYYMMDD-NNNN
        String datepart = LocalDate.now().toString().replace("-", "");
        long sequence = admissionRepository.count() + 1;
        return String.format("ADM-%s-%04d", datepart, sequence);
    }

    private InpatientAdmission buildAdmission(AdmissionRequest request, Patient patient, Room room, Bed bed,
                                               String admissionNumber, BigDecimal requiredDeposit) {
        return InpatientAdmission.builder()
            .admissionNumber(admissionNumber)
            .patientId(patient.getId())
            .outpatientRegistrationId(request.getOutpatientRegistrationId())
            .admissionDate(LocalDateTime.now())
            .admissionTime(LocalDateTime.now())
            .admissionType(request.getAdmissionType())
            .admissionSource(request.getAdmissionSource())
            .room(room)
            .bed(bed)
            .roomClass(request.getRoomClass())
            .admittingDoctorId(request.getAdmittingDoctorId())
            .admittingDoctorName(request.getAdmittingDoctorName())
            .attendingDoctorId(request.getAttendingDoctorId())
            .attendingDoctorName(request.getAttendingDoctorName())
            .referringDoctorId(request.getReferringDoctorId())
            .referringDoctorName(request.getReferringDoctorName())
            .referringFacility(request.getReferringFacility())
            .chiefComplaint(request.getChiefComplaint())
            .admissionDiagnosis(request.getAdmissionDiagnosis())
            .secondaryDiagnoses(request.getSecondaryDiagnoses())
            .estimatedLengthOfStayDays(request.getEstimatedLengthOfStayDays())
            .estimatedDischargeDate(request.getEstimatedDischargeDate())
            .paymentMethod(request.getPaymentMethod())
            .isBpjs(request.getIsBpjs() != null ? request.getIsBpjs() : false)
            .bpjsCardNumber(request.getBpjsCardNumber())
            .insuranceName(request.getInsuranceName())
            .insuranceNumber(request.getInsuranceNumber())
            .insuranceCoverageLimit(request.getInsuranceCoverageLimit())
            .roomRatePerDay(room.getBaseRoomRate())
            .requiredDeposit(requiredDeposit)
            .depositPaid(request.getDepositPaid() != null ? request.getDepositPaid() : BigDecimal.ZERO)
            .depositPaidDate(request.getDepositPaid() != null && request.getDepositPaid().compareTo(BigDecimal.ZERO) > 0 ?
                LocalDateTime.now() : null)
            .depositReceiptNumber(request.getDepositReceiptNumber())
            .status(AdmissionStatus.ADMITTED)
            .emergencyContactName(request.getEmergencyContactName())
            .emergencyContactRelationship(request.getEmergencyContactRelationship())
            .emergencyContactPhone(request.getEmergencyContactPhone())
            .belongingsStored(request.getBelongingsStored())
            .belongingsList(request.getBelongingsList())
            .requiresIsolation(request.getRequiresIsolation() != null ? request.getRequiresIsolation() : false)
            .isolationType(request.getIsolationType())
            .requiresInterpreter(request.getRequiresInterpreter() != null ? request.getRequiresInterpreter() : false)
            .interpreterLanguage(request.getInterpreterLanguage())
            .hasAllergies(request.getHasAllergies() != null ? request.getHasAllergies() : false)
            .allergyNotes(request.getAllergyNotes())
            .admissionNotes(request.getAdmissionNotes())
            .specialInstructions(request.getSpecialInstructions())
            .build();
    }

    private void createBedAssignment(InpatientAdmission admission, Room room, Bed bed,
                                      String assignmentType, String transferReason) {
        BedAssignment assignment = BedAssignment.builder()
            .admission(admission)
            .patientId(admission.getPatientId())
            .room(room)
            .bed(bed)
            .roomNumber(room.getRoomNumber())
            .bedNumber(bed.getBedNumber())
            .roomClass(room.getRoomClass())
            .assignedAt(LocalDateTime.now())
            .assignmentType(assignmentType)
            .transferReason(transferReason)
            .roomRatePerDay(room.getBaseRoomRate())
            .isCurrent(true)
            .build();

        bedAssignmentRepository.save(assignment);
        admission.addBedAssignment(assignment);
    }

    private void addDiagnoses(InpatientAdmission admission, List<AdmissionRequest.DiagnosisDto> diagnosisDtos) {
        for (AdmissionRequest.DiagnosisDto dto : diagnosisDtos) {
            AdmissionDiagnosis diagnosis = AdmissionDiagnosis.builder()
                .admission(admission)
                .patientId(admission.getPatientId())
                .icd10Id(dto.getIcd10Id())
                .icd10Code(dto.getIcd10Code())
                .icd10Description(dto.getIcd10Description())
                .diagnosisType(dto.getDiagnosisType())
                .isPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false)
                .diagnosedAt(LocalDateTime.now())
                .notes(dto.getNotes())
                .build();

            diagnosisRepository.save(diagnosis);
            admission.addDiagnosis(diagnosis);
        }
    }

    private String determineTransferType(RoomClass oldClass, RoomClass newClass) {
        if (oldClass == newClass) {
            return "TRANSFER";
        }

        int oldLevel = getRoomClassLevel(oldClass);
        int newLevel = getRoomClassLevel(newClass);

        return newLevel > oldLevel ? "UPGRADE" : "DOWNGRADE";
    }

    private int getRoomClassLevel(RoomClass roomClass) {
        return switch (roomClass) {
            case KELAS_3 -> 1;
            case KELAS_2 -> 2;
            case KELAS_1 -> 3;
            case VIP -> 4;
            case ICU, NICU, PICU -> 5;
        };
    }

    private AdmissionResponse convertToResponse(InpatientAdmission admission, Patient patient) {
        // Get diagnoses
        List<AdmissionDiagnosis> diagnoses = diagnosisRepository.findByAdmission_Id(admission.getId());
        List<AdmissionResponse.DiagnosisInfo> diagnosisInfoList = diagnoses.stream()
            .map(d -> AdmissionResponse.DiagnosisInfo.builder()
                .id(d.getId())
                .icd10Code(d.getIcd10Code())
                .icd10Description(d.getIcd10Description())
                .diagnosisType(d.getDiagnosisType())
                .isPrimary(d.getIsPrimary())
                .diagnosedAt(d.getDiagnosedAt())
                .notes(d.getNotes())
                .build())
            .collect(Collectors.toList());

        // Calculate deposit balance
        BigDecimal depositBalance = admission.getRequiredDeposit().subtract(admission.getDepositPaid());

        // Build full bed location
        String fullBedLocation = null;
        if (admission.getRoom() != null && admission.getBed() != null) {
            fullBedLocation = admission.getRoom().getFullRoomName() + " - " + admission.getBed().getBedNumber();
        }

        return AdmissionResponse.builder()
            .id(admission.getId())
            .admissionNumber(admission.getAdmissionNumber())
            .patientId(patient.getId())
            .patientName(patient.getFullName())
            .patientMrn(patient.getMrn())
            .admissionDate(admission.getAdmissionDate())
            .admissionTime(admission.getAdmissionTime())
            .admissionType(admission.getAdmissionType())
            .admissionSource(admission.getAdmissionSource())
            .roomId(admission.getRoom() != null ? admission.getRoom().getId() : null)
            .roomNumber(admission.getRoom() != null ? admission.getRoom().getRoomNumber() : null)
            .roomName(admission.getRoom() != null ? admission.getRoom().getRoomName() : null)
            .roomClass(admission.getRoomClass())
            .bedId(admission.getBed() != null ? admission.getBed().getId() : null)
            .bedNumber(admission.getBed() != null ? admission.getBed().getBedNumber() : null)
            .fullBedLocation(fullBedLocation)
            .admittingDoctorId(admission.getAdmittingDoctorId())
            .admittingDoctorName(admission.getAdmittingDoctorName())
            .attendingDoctorId(admission.getAttendingDoctorId())
            .attendingDoctorName(admission.getAttendingDoctorName())
            .referringDoctorId(admission.getReferringDoctorId())
            .referringDoctorName(admission.getReferringDoctorName())
            .referringFacility(admission.getReferringFacility())
            .chiefComplaint(admission.getChiefComplaint())
            .admissionDiagnosis(admission.getAdmissionDiagnosis())
            .diagnoses(diagnosisInfoList)
            .estimatedLengthOfStayDays(admission.getEstimatedLengthOfStayDays())
            .estimatedDischargeDate(admission.getEstimatedDischargeDate())
            .actualLengthOfStayDays(admission.getActualLengthOfStayDays())
            .paymentMethod(admission.getPaymentMethod())
            .isBpjs(admission.getIsBpjs())
            .bpjsCardNumber(admission.getBpjsCardNumber())
            .insuranceName(admission.getInsuranceName())
            .insuranceNumber(admission.getInsuranceNumber())
            .roomRatePerDay(admission.getRoomRatePerDay())
            .requiredDeposit(admission.getRequiredDeposit())
            .depositPaid(admission.getDepositPaid())
            .depositBalance(depositBalance)
            .depositPaidDate(admission.getDepositPaidDate())
            .depositReceiptNumber(admission.getDepositReceiptNumber())
            .status(admission.getStatus())
            .dischargeDate(admission.getDischargeDate())
            .dischargeType(admission.getDischargeType())
            .dischargeDisposition(admission.getDischargeDisposition())
            .emergencyContactName(admission.getEmergencyContactName())
            .emergencyContactRelationship(admission.getEmergencyContactRelationship())
            .emergencyContactPhone(admission.getEmergencyContactPhone())
            .requiresIsolation(admission.getRequiresIsolation())
            .isolationType(admission.getIsolationType())
            .requiresInterpreter(admission.getRequiresInterpreter())
            .interpreterLanguage(admission.getInterpreterLanguage())
            .hasAllergies(admission.getHasAllergies())
            .allergyNotes(admission.getAllergyNotes())
            .admissionNotes(admission.getAdmissionNotes())
            .specialInstructions(admission.getSpecialInstructions())
            .createdAt(admission.getCreatedAt())
            .updatedAt(admission.getUpdatedAt())
            .createdBy(admission.getCreatedBy())
            .build();
    }
}
