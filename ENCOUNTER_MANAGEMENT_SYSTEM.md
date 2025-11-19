# Encounter/Visit Management System

## Overview

Comprehensive encounter management system that links all registration types (outpatient, inpatient, emergency) to clinical workflows, billing, pharmacy, and other hospital systems.

**Status:** Database schema and entities completed ✅
**Build:** SUCCESS (123 source files compiled) ✅

---

## System Architecture

### **Core Components**

```
┌─────────────────────────────────────────────────────────┐
│              ENCOUNTER MANAGEMENT SYSTEM                 │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────┐  ┌─────────────┐  ┌──────────────┐    │
│  │ Outpatient │  │  Inpatient  │  │  Emergency   │    │
│  │Registration│  │  Admission  │  │ Registration │    │
│  └──────┬─────┘  └──────┬──────┘  └──────┬───────┘    │
│         │               │                 │             │
│         └───────────────┴─────────────────┘             │
│                         ↓                                │
│                  ┌─────────────┐                        │
│                  │  ENCOUNTER  │ ← Central Hub           │
│                  └──────┬──────┘                        │
│                         │                                │
│         ┌───────────────┼───────────────┐               │
│         ↓               ↓               ↓               │
│  ┌────────────┐  ┌───────────┐  ┌────────────┐        │
│  │ Department │  │ Discharge │  │  Referral  │        │
│  │ Transfer   │  │  Summary  │  │   Letter   │        │
│  └────────────┘  └───────────┘  └────────────┘        │
│         │               │               │               │
│         └───────────────┴───────────────┘               │
│                         ↓                                │
│  ┌──────────────────────────────────────────────┐      │
│  │     Integration Points                        │      │
│  ├──────────────────────────────────────────────┤      │
│  │ • Clinical Data (SOAP notes, vitals, dx)     │      │
│  │ • Billing & Invoicing                        │      │
│  │ • Pharmacy (medication orders)               │      │
│  │ │ Laboratory & Radiology orders              │      │
│  │ • BPJS SEP Integration                       │      │
│  │ • SATUSEHAT Submission                       │      │
│  └──────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────┘
```

---

## Database Schema

### Migration: `V8__create_encounter_management_tables.sql`

#### **Tables Created:**

1. **`clinical_schema.encounter`** - Central encounter/visit management
2. **`clinical_schema.department_transfer`** - Inter-department transfers
3. **`clinical_schema.discharge_summary`** - Comprehensive discharge documentation
4. **`clinical_schema.referral_letter`** - Surat Rujukan (referral letters)
5. **`clinical_schema.encounter_status_history`** - Audit trail of status changes

---

## Entities Created

### **1. Encounter Entity**
**Location:** `com.yudha.hms.clinical.entity.Encounter`

**Key Features:**
- Links to outpatient, inpatient, or emergency registrations
- Status lifecycle: REGISTERED → IN_PROGRESS → FINISHED
- Department/location tracking
- Care team assignment (doctor, nurse)
- Length of stay calculation
- BPJS SEP integration
- Billing status tracking
- SATUSEHAT compliance

**Enums:**
- `EncounterType`: OUTPATIENT, INPATIENT, EMERGENCY
- `EncounterStatus`: REGISTERED, IN_PROGRESS, FINISHED, CANCELLED
- `EncounterClass`: AMBULATORY, INPATIENT, EMERGENCY, VIRTUAL

**Business Methods:**
```java
encounter.startEncounter();           // Change status to IN_PROGRESS
encounter.finishEncounter();          // Complete encounter
encounter.cancelEncounter(reason);    // Cancel encounter
encounter.calculateLengthOfStay();    // Calculate duration
encounter.getDurationHours();         // Get current duration
```

### **2. DepartmentTransfer Entity**
**Location:** `com.yudha.hms.clinical.entity.DepartmentTransfer`

**Key Features:**
- Transfer between departments/locations
- Transfer types: INTERNAL, EXTERNAL, ICU, WARD, OPERATING_ROOM
- Status flow: REQUESTED → ACCEPTED → IN_TRANSIT → COMPLETED
- Handover summary and notes
- Transport requirements
- Urgency levels

**Business Methods:**
```java
transfer.accept(userId, userName);    // Accept transfer request
transfer.reject(reason);              // Reject transfer
transfer.startTransfer();             // Begin transfer
transfer.complete();                  // Complete transfer
transfer.cancel(reason);              // Cancel transfer
```

### **3. DischargeSummary Entity**
**Location:** `com.yudha.hms.clinical.entity.DischargeSummary`

**Key Features:**
- Comprehensive discharge documentation
- Hospital course narrative
- Diagnoses (primary + secondary)
- Discharge medications
- Follow-up instructions
- Diet and activity restrictions
- Warning signs
- Digital signature support
- Document generation (PDF)

**Sections:**
- Admission details
- Hospital course
- Procedures performed
- Final condition
- Medications (continued, discontinued, new)
- Follow-up care
- Dietary instructions
- Activity restrictions
- Wound care
- Warning signs

**Business Methods:**
```java
summary.sign(doctorId, doctorName);   // Sign discharge summary
summary.isReadyForDischarge();        // Validate completeness
```

### **4. ReferralLetter Entity (Surat Rujukan)**
**Location:** `com.yudha.hms.clinical.entity.ReferralLetter`

**Key Features:**
- Complete referral letter documentation
- Source and destination facility information
- Clinical summary and diagnoses
- Current treatments and medications
- Investigation results
- Urgency levels
- BPJS referral support
- Digital signature
- PDF document generation
- SATUSEHAT integration

**Referral Types:**
- OUTPATIENT, INPATIENT, EMERGENCY, DIAGNOSTIC

**Referral Reasons:**
- CONSULTATION, TREATMENT, INVESTIGATION, ADMISSION

**Business Methods:**
```java
referral.sign(doctorId, name, sig);           // Sign referral
referral.accept(acceptedBy, appointmentDate); // Accept referral
referral.reject(reason);                      // Reject referral
referral.complete();                          // Mark complete
```

---

## Implementation Roadmap

### **Phase 1: Repositories** (Next Step)

Create JPA repositories for all entities:

```java
// EncounterRepository.java
@Repository
public interface EncounterRepository extends JpaRepository<Encounter, UUID> {
    Optional<Encounter> findByEncounterNumber(String encounterNumber);
    List<Encounter> findByPatientIdOrderByEncounterStartDesc(UUID patientId);
    List<Encounter> findByStatus(EncounterStatus status);
    List<Encounter> findByEncounterType(EncounterType type);

    // Find by registration IDs
    Optional<Encounter> findByOutpatientRegistrationId(UUID registrationId);
    Optional<Encounter> findByInpatientAdmissionId(UUID admissionId);
    Optional<Encounter> findByEmergencyRegistrationId(UUID registrationId);

    // Active encounters
    @Query("SELECT e FROM Encounter e WHERE e.status IN ('REGISTERED', 'IN_PROGRESS')")
    List<Encounter> findAllActive();

    // By department
    List<Encounter> findByCurrentDepartmentAndStatus(String department, EncounterStatus status);

    // Date range
    List<Encounter> findByEncounterStartBetween(LocalDateTime start, LocalDateTime end);
}

// DepartmentTransferRepository.java
@Repository
public interface DepartmentTransferRepository extends JpaRepository<DepartmentTransfer, UUID> {
    List<DepartmentTransfer> findByEncounterId(UUID encounterId);
    List<DepartmentTransfer> findByTransferStatus(String status);
    List<DepartmentTransfer> findByToDepartment(String department);
    List<DepartmentTransfer> findPendingTransfers();
}

// DischargeSummaryRepository.java
@Repository
public interface DischargeSummaryRepository extends JpaRepository<DischargeSummary, UUID> {
    Optional<DischargeSummary> findByEncounterId(UUID encounterId);
    Optional<DischargeSummary> findByDischargeNumber(String dischargeNumber);
    List<DischargeSummary> findByPatientId(UUID patientId);
    List<DischargeSummary> findBySignedFalse(); // Unsigned summaries
}

// ReferralLetterRepository.java
@Repository
public interface ReferralLetterRepository extends JpaRepository<ReferralLetter, UUID> {
    Optional<ReferralLetter> findByReferralNumber(String referralNumber);
    List<ReferralLetter> findByPatientId(UUID patientId);
    List<ReferralLetter> findByReferralStatus(String status);
    List<ReferralLetter> findByIsBpjsReferralTrue();
    List<ReferralLetter> findPendingReferrals();
}
```

### **Phase 2: DTOs**

Create request/response DTOs:

```java
// EncounterRequest.java
@Data
public class EncounterRequest {
    private UUID patientId;
    private EncounterType encounterType;
    private UUID registrationId; // outpatient/inpatient/emergency
    private String reasonForVisit;
    private String chiefComplaint;
    private UUID attendingDoctorId;
    private String currentDepartment;
    private String priority;
}

// EncounterResponse.java
@Data
public class EncounterResponse {
    private UUID id;
    private String encounterNumber;
    private UUID patientId;
    private String patientName;
    private EncounterType encounterType;
    private EncounterStatus status;
    private LocalDateTime encounterStart;
    private LocalDateTime encounterEnd;
    private String currentDepartment;
    private String attendingDoctorName;
    private Integer lengthOfStayDays;
    private String billingStatus;
    private BigDecimal totalCharges;
}

// TransferRequest.java
@Data
public class TransferRequest {
    private UUID encounterId;
    private String fromDepartment;
    private String toDepartment;
    private String toLocation;
    private String transferType;
    private String reasonForTransfer;
    private String urgency;
    private String handoverSummary;
    private Boolean requiresTransport;
}

// DischargeSummaryRequest.java
@Data
public class DischargeSummaryRequest {
    private UUID encounterId;
    private String dischargeType;
    private String dischargeDisposition;
    private String hospitalCourse; // Required
    private String primaryDiagnosisCode;
    private String primaryDiagnosisText;
    private String dischargeMedications; // Required
    private String followUpInstructions; // Required
    private LocalDate followUpAppointmentDate;
    private String dietInstructions;
    private String activityRestrictions;
}

// ReferralLetterRequest.java
@Data
public class ReferralLetterRequest {
    private UUID patientId;
    private UUID encounterId;
    private String referralType;
    private String referralReason;
    private String referredToFacility;
    private String referredToSpecialty;
    private String chiefComplaint;
    private String clinicalSummary;
    private String primaryDiagnosisText;
    private String reasonForReferral;
    private String urgencyLevel;
    private Boolean isBpjsReferral;
}
```

### **Phase 3: Services**

#### **3.1 EncounterService**

```java
@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final OutpatientRegistrationRepository outpatientRepo;
    private final InpatientAdmissionRepository inpatientRepo;
    private final EmergencyRegistrationRepository emergencyRepo;

    /**
     * Create encounter from outpatient registration.
     */
    @Transactional
    public EncounterResponse createFromOutpatientRegistration(UUID registrationId) {
        // Get registration
        // Create encounter
        // Link to outpatient registration
        // Set encounter type and class
        // Generate encounter number
        // Save and return
    }

    /**
     * Create encounter from inpatient admission.
     */
    @Transactional
    public EncounterResponse createFromInpatientAdmission(UUID admissionId) {
        // Similar to outpatient
    }

    /**
     * Create encounter from emergency registration.
     */
    @Transactional
    public EncounterResponse createFromEmergencyRegistration(UUID registrationId) {
        // Similar to outpatient
    }

    /**
     * Start encounter (change status to IN_PROGRESS).
     */
    @Transactional
    public EncounterResponse startEncounter(UUID encounterId) {
        Encounter encounter = getEncounter(encounterId);
        encounter.startEncounter();
        encounterRepository.save(encounter);
        return convertToResponse(encounter);
    }

    /**
     * Finish encounter.
     */
    @Transactional
    public EncounterResponse finishEncounter(UUID encounterId) {
        Encounter encounter = getEncounter(encounterId);
        encounter.finishEncounter();
        encounterRepository.save(encounter);
        return convertToResponse(encounter);
    }

    /**
     * Get patient visit history.
     */
    public List<EncounterResponse> getPatientVisitHistory(UUID patientId) {
        return encounterRepository.findByPatientIdOrderByEncounterStartDesc(patientId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get active encounters by department.
     */
    public List<EncounterResponse> getActiveEncountersByDepartment(String department) {
        return encounterRepository.findByCurrentDepartmentAndStatus(
            department,
            EncounterStatus.IN_PROGRESS
        ).stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
    }

    private String generateEncounterNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "ENC-" + today + "-";
        // Generate sequential number
        return prefix + String.format("%04d", getNextSequence());
    }
}
```

#### **3.2 TransferService**

```java
@Service
@RequiredArgsConstructor
public class TransferService {

    private final DepartmentTransferRepository transferRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Request department transfer.
     */
    @Transactional
    public TransferResponse requestTransfer(TransferRequest request) {
        // Validate encounter exists and is active
        // Create transfer record
        // Generate transfer number
        // Set status to REQUESTED
        // Save and return
    }

    /**
     * Accept transfer request.
     */
    @Transactional
    public TransferResponse acceptTransfer(UUID transferId, UUID acceptedById, String acceptedByName) {
        DepartmentTransfer transfer = getTransfer(transferId);
        transfer.accept(acceptedById, acceptedByName);
        transferRepository.save(transfer);
        return convertToResponse(transfer);
    }

    /**
     * Complete transfer and update encounter location.
     */
    @Transactional
    public TransferResponse completeTransfer(UUID transferId) {
        DepartmentTransfer transfer = getTransfer(transferId);
        transfer.complete();

        // Update encounter current department
        Encounter encounter = encounterRepository.findById(transfer.getEncounter().getId())
            .orElseThrow();
        encounter.setCurrentDepartment(transfer.getToDepartment());
        encounter.setCurrentLocation(transfer.getToLocation());
        encounterRepository.save(encounter);

        transferRepository.save(transfer);
        return convertToResponse(transfer);
    }

    /**
     * Get transfer history for encounter.
     */
    public List<TransferResponse> getEncounterTransferHistory(UUID encounterId) {
        return transferRepository.findByEncounterId(encounterId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
}
```

#### **3.3 DischargeService**

```java
@Service
@RequiredArgsConstructor
public class DischargeService {

    private final DischargeSummaryRepository dischargeSummaryRepository;
    private final EncounterRepository encounterRepository;

    /**
     * Create discharge summary.
     */
    @Transactional
    public DischargeSummaryResponse createDischargeSummary(DischargeSummaryRequest request) {
        // Validate encounter exists
        // Create discharge summary
        // Generate discharge number
        // Populate from request
        // Calculate length of stay
        // Save and return
    }

    /**
     * Sign discharge summary.
     */
    @Transactional
    public DischargeSummaryResponse signDischargeSummary(
        UUID summaryId,
        UUID doctorId,
        String doctorName
    ) {
        DischargeSummary summary = getSummary(summaryId);
        summary.sign(doctorId, doctorName);
        dischargeSummaryRepository.save(summary);
        return convertToResponse(summary);
    }

    /**
     * Generate discharge summary document (PDF).
     */
    public byte[] generateDischargeSummaryPdf(UUID summaryId) {
        DischargeSummary summary = getSummary(summaryId);
        // Generate PDF using iText or similar library
        // Format: Header, patient info, hospital course, medications, etc.
        return generatePdf(summary);
    }

    /**
     * Complete discharge process.
     */
    @Transactional
    public EncounterResponse dischargePatient(UUID encounterId, UUID summaryId) {
        // Finalize discharge summary
        // Update encounter status to FINISHED
        // Set discharge date
        // Link discharge summary to encounter
        Encounter encounter = getEncounter(encounterId);
        encounter.finishEncounter();
        encounter.setDischargeDate(LocalDateTime.now());
        encounter.setDischargeSummaryId(summaryId);
        encounterRepository.save(encounter);
        return convertToResponse(encounter);
    }
}
```

#### **3.4 ReferralService**

```java
@Service
@RequiredArgsConstructor
public class ReferralService {

    private final ReferralLetterRepository referralRepository;

    /**
     * Create referral letter (Surat Rujukan).
     */
    @Transactional
    public ReferralLetterResponse createReferralLetter(ReferralLetterRequest request) {
        // Create referral letter
        // Generate referral number
        // Populate clinical information
        // Set validity period
        // Save and return
    }

    /**
     * Sign referral letter.
     */
    @Transactional
    public ReferralLetterResponse signReferral(
        UUID referralId,
        UUID doctorId,
        String doctorName,
        String signature
    ) {
        ReferralLetter referral = getReferral(referralId);
        referral.sign(doctorId, doctorName, signature);
        referralRepository.save(referral);
        return convertToResponse(referral);
    }

    /**
     * Generate referral letter document (PDF).
     */
    public byte[] generateReferralLetterPdf(UUID referralId) {
        ReferralLetter referral = getReferral(referralId);
        // Generate formatted Surat Rujukan PDF
        // Include: Header, patient info, clinical summary, diagnoses, etc.
        return generatePdf(referral);
    }

    /**
     * Generate plain text referral letter.
     */
    public String generateReferralLetterText(UUID referralId) {
        ReferralLetter referral = getReferral(referralId);

        StringBuilder letter = new StringBuilder();
        letter.append("SURAT RUJUKAN\n");
        letter.append("=".repeat(60)).append("\n\n");
        letter.append("No: ").append(referral.getReferralNumber()).append("\n");
        letter.append("Tanggal: ").append(referral.getReferralDate()).append("\n\n");
        letter.append("Kepada Yth: ").append(referral.getReferredToFacility()).append("\n");
        letter.append("Di Tempat\n\n");
        letter.append("Dengan hormat,\n\n");
        letter.append("Mohon pemeriksaan dan penanganan lebih lanjut untuk pasien:\n\n");
        // ... continue formatting

        return letter.toString();
    }
}
```

### **Phase 4: Controllers**

```java
// EncounterController.java
@RestController
@RequestMapping("/api/encounters")
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterService encounterService;

    @PostMapping("/from-outpatient/{registrationId}")
    public ResponseEntity<ApiResponse<EncounterResponse>> createFromOutpatient(
        @PathVariable UUID registrationId
    ) {
        EncounterResponse response = encounterService
            .createFromOutpatientRegistration(registrationId);
        return ResponseEntity.ok(ApiResponse.success("Encounter created", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EncounterResponse>> getEncounter(
        @PathVariable UUID id
    ) {
        EncounterResponse response = encounterService.getEncounter(id);
        return ResponseEntity.ok(ApiResponse.success("Encounter retrieved", response));
    }

    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<ApiResponse<List<EncounterResponse>>> getPatientHistory(
        @PathVariable UUID patientId
    ) {
        List<EncounterResponse> history = encounterService.getPatientVisitHistory(patientId);
        return ResponseEntity.ok(ApiResponse.success("Visit history retrieved", history));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<EncounterResponse>> startEncounter(
        @PathVariable UUID id
    ) {
        EncounterResponse response = encounterService.startEncounter(id);
        return ResponseEntity.ok(ApiResponse.success("Encounter started", response));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<ApiResponse<EncounterResponse>> finishEncounter(
        @PathVariable UUID id
    ) {
        EncounterResponse response = encounterService.finishEncounter(id);
        return ResponseEntity.ok(ApiResponse.success("Encounter finished", response));
    }
}

// TransferController.java
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> requestTransfer(
        @Valid @RequestBody TransferRequest request
    ) {
        TransferResponse response = transferService.requestTransfer(request);
        return ResponseEntity.ok(ApiResponse.success("Transfer requested", response));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<TransferResponse>> acceptTransfer(
        @PathVariable UUID id,
        @RequestParam UUID acceptedById,
        @RequestParam String acceptedByName
    ) {
        TransferResponse response = transferService.acceptTransfer(id, acceptedById, acceptedByName);
        return ResponseEntity.ok(ApiResponse.success("Transfer accepted", response));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TransferResponse>> completeTransfer(
        @PathVariable UUID id
    ) {
        TransferResponse response = transferService.completeTransfer(id);
        return ResponseEntity.ok(ApiResponse.success("Transfer completed", response));
    }
}

// DischargeController.java
@RestController
@RequestMapping("/api/discharge")
@RequiredArgsConstructor
public class DischargeController {

    private final DischargeService dischargeService;

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<DischargeSummaryResponse>> createSummary(
        @Valid @RequestBody DischargeSummaryRequest request
    ) {
        DischargeSummaryResponse response = dischargeService.createDischargeSummary(request);
        return ResponseEntity.ok(ApiResponse.success("Discharge summary created", response));
    }

    @GetMapping("/summary/{id}/pdf")
    public ResponseEntity<byte[]> downloadSummaryPdf(
        @PathVariable UUID id
    ) {
        byte[] pdf = dischargeService.generateDischargeSummaryPdf(id);
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=discharge-summary.pdf")
            .body(pdf);
    }
}

// ReferralController.java
@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReferralLetterResponse>> createReferral(
        @Valid @RequestBody ReferralLetterRequest request
    ) {
        ReferralLetterResponse response = referralService.createReferralLetter(request);
        return ResponseEntity.ok(ApiResponse.success("Referral letter created", response));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadReferralPdf(
        @PathVariable UUID id
    ) {
        byte[] pdf = referralService.generateReferralLetterPdf(id);
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=surat-rujukan.pdf")
            .body(pdf);
    }
}
```

---

## Integration Points

### **1. Clinical Data Integration**

**Link to SOAP Notes:**
```java
// Clinical notes reference encounter
ClinicalNote note = ClinicalNote.builder()
    .encounterId(encounter.getId())
    .subjective("Patient reports...")
    .objective("BP: 120/80...")
    .assessment("Hypertension, controlled")
    .plan("Continue medications...")
    .build();
```

**Link to Vital Signs:**
```java
VitalSigns vitals = VitalSigns.builder()
    .encounterId(encounter.getId())
    .systolicBp(120)
    .diastolicBp(80)
    .heartRate(72)
    .temperature(36.5)
    .build();
```

### **2. Billing Integration**

```java
// Create billing record from encounter
Billing billing = Billing.builder()
    .encounterId(encounter.getId())
    .patientId(encounter.getPatientId())
    .isBpjs(encounter.getIsBpjs())
    .sepNumber(encounter.getSepNumber())
    .billingItems(calculateCharges(encounter))
    .build();
```

### **3. Pharmacy Integration**

```java
// Medication orders linked to encounter
MedicationOrder order = MedicationOrder.builder()
    .encounterId(encounter.getId())
    .patientId(encounter.getPatientId())
    .medication(medication)
    .prescribingDoctorId(encounter.getAttendingDoctorId())
    .build();
```

### **4. Laboratory & Radiology**

```java
// Lab orders linked to encounter
LabOrder labOrder = LabOrder.builder()
    .encounterId(encounter.getId())
    .patientId(encounter.getPatientId())
    .tests(requestedTests)
    .build();
```

---

## API Endpoints Summary

### **Encounter Management**
```
POST   /api/encounters/from-outpatient/{registrationId}  - Create from outpatient
POST   /api/encounters/from-inpatient/{admissionId}      - Create from inpatient
POST   /api/encounters/from-emergency/{registrationId}   - Create from emergency
GET    /api/encounters/{id}                              - Get encounter details
GET    /api/encounters/patient/{patientId}/history       - Visit history
PUT    /api/encounters/{id}/start                        - Start encounter
PUT    /api/encounters/{id}/finish                       - Finish encounter
PUT    /api/encounters/{id}/cancel                       - Cancel encounter
GET    /api/encounters/active                            - All active encounters
GET    /api/encounters/department/{dept}/active          - Active by department
```

### **Transfer Management**
```
POST   /api/transfers                      - Request transfer
GET    /api/transfers/{id}                 - Get transfer details
GET    /api/transfers/encounter/{id}       - Transfer history
PUT    /api/transfers/{id}/accept          - Accept transfer
PUT    /api/transfers/{id}/reject          - Reject transfer
PUT    /api/transfers/{id}/complete        - Complete transfer
PUT    /api/transfers/{id}/cancel          - Cancel transfer
GET    /api/transfers/pending              - Pending transfers
```

### **Discharge Management**
```
POST   /api/discharge/summary                - Create discharge summary
GET    /api/discharge/summary/{id}           - Get summary details
PUT    /api/discharge/summary/{id}/sign      - Sign summary
GET    /api/discharge/summary/{id}/pdf       - Download PDF
POST   /api/discharge/patient/{encounterId}  - Complete discharge
```

### **Referral Management**
```
POST   /api/referrals                  - Create referral letter
GET    /api/referrals/{id}             - Get referral details
PUT    /api/referrals/{id}/sign        - Sign referral
GET    /api/referrals/{id}/pdf         - Download Surat Rujukan PDF
PUT    /api/referrals/{id}/accept      - Accept referral
PUT    /api/referrals/{id}/reject      - Reject referral
GET    /api/referrals/patient/{id}     - Patient's referrals
GET    /api/referrals/pending          - Pending referrals
```

---

## Indonesian Hospital Compliance

### **BPJS Integration**
- SEP number tracking in encounters
- BPJS-specific referral support
- BPJS referral codes

### **SATUSEHAT Integration**
- Encounter submission tracking
- Service request IDs for referrals
- Submission date tracking

### **Document Formatting**
- Surat Rujukan (Referral Letter) in Indonesian
- Discharge summary in Indonesian (Ringkasan Pulang)
- Professional medical formatting

---

## Files Created

### **Database Migration:**
- `V8__create_encounter_management_tables.sql` ✅

### **Entities:**
- `Encounter.java` ✅
- `EncounterType.java` (enum) ✅
- `EncounterStatus.java` (enum) ✅
- `EncounterClass.java` (enum) ✅
- `DepartmentTransfer.java` ✅
- `DischargeSummary.java` ✅
- `ReferralLetter.java` ✅

### **Build Status:**
- **123 source files compiled** ✅
- **BUILD SUCCESS** ✅

---

## Next Implementation Steps

1. **Create Repositories** (4 files)
   - EncounterRepository
   - DepartmentTransferRepository
   - DischargeSummaryRepository
   - ReferralLetterRepository

2. **Create DTOs** (8 files)
   - Request DTOs (4)
   - Response DTOs (4)

3. **Create Services** (4 files)
   - EncounterService
   - TransferService
   - DischargeService
   - ReferralService

4. **Create Controllers** (4 files)
   - EncounterController
   - TransferController
   - DischargeController
   - ReferralController

5. **Add PDF Generation** (optional)
   - Use iText or Flying Saucer
   - Format discharge summaries
   - Format Surat Rujukan

6. **Testing**
   - Unit tests for services
   - Integration tests for controllers
   - End-to-end workflow tests

---

## System Benefits

✅ **Centralized Visit Management** - Single source of truth for patient encounters
✅ **Complete Audit Trail** - Track all status changes and transfers
✅ **Department Integration** - Seamless transfers between departments
✅ **Comprehensive Documentation** - Discharge summaries and referrals
✅ **Billing Ready** - Direct integration with billing systems
✅ **Clinical Workflow** - Links to SOAP notes, orders, and results
✅ **BPJS Compliant** - Full BPJS SEP and referral support
✅ **SATUSEHAT Ready** - Prepared for national health data integration
✅ **Visit History** - Complete patient encounter history
✅ **Indonesian Standards** - Surat Rujukan and local formatting

---

**Version:** 1.0.0
**Status:** Database and Entities Completed
**Last Updated:** 2025-01-19
**Author:** HMS Development Team
