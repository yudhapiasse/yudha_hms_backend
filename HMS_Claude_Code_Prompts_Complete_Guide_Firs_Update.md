# Claude Code Prompts: Building HMS for Indonesia - From Zero to Production

## Overview
This document contains step-by-step prompts to use with Claude Code for building a comprehensive Hospital Management System for Indonesia using Spring Boot, PostgreSQL, and React.

---

## Phase 1: Project Setup & Foundation

### 1.1 Initial Project Creation
```
Create a Spring Boot project for a Hospital Management System for Indonesia using Spring Initializr with:
- Spring Boot 3.4.1
- Java 21 LTS
- Maven
- Dependencies: Spring Web, Spring Data JPA, PostgreSQL, Spring Security, Validation, Lombok, Spring Boot DevTools
- Group ID: com.yudha
- Artifact ID: hms-backend
- Package name: com.yudha.hms
Configure it for IntelliJ IDEA with proper .gitignore file
```

### 1.2 Project Structure Setup
```
Set up a modular monolith structure for the HMS backend with the following modules:
- patient (patient management)
- registration (outpatient/inpatient registration)
- clinical (medical records, diagnoses, procedures)
- billing (invoicing, insurance claims)
- pharmacy (drug management, prescriptions)
- laboratory (lab orders and results)
- radiology (imaging orders and PACS integration)
- integration/bpjs (BPJS Kesehatan integration)
- integration/satusehat (SATUSEHAT platform integration)
- shared (common utilities, constants, DTOs)
Create proper package structure with controller, service, repository, entity, and dto packages for each module
```

### 1.3 Database Configuration
```
Configure PostgreSQL 16 database connection for the HMS with:
- Multiple schemas (master, patient, clinical, billing, integration)
- HikariCP connection pooling optimized for 200 concurrent users
- application.yml with profiles for dev, staging, and production
- Database initialization scripts with Indonesian-specific tables
- Enable virtual threads support (Java 21) for better connection handling
Include proper database versioning using Flyway 10.x migrations
```
Sampai di sini, kita sudah memiliki struktur database yang konsisten dan dapat diakses oleh semua layer aplikasi.

### 1.4 Base Entity Configuration
```
Create base entity classes with:
- BaseEntity with id (UUID), createdAt, updatedAt, createdBy, updatedBy
- Audit configuration using Spring Data JPA auditing
- Soft delete implementation with deletedAt field
- Optimistic locking with @Version
Include proper equals, hashCode, and toString implementations
```

---

## Phase 2: Core Patient Management

### 2.1 Patient Entity with Indonesian Requirements
```
Create a comprehensive Patient entity for Indonesian HMS with:
- NIK (16 digits) with validation
- BPJS number
- Medical record number (auto-generated with configurable format)
- KTP address vs domicile address
- Religion (required for Indonesian context)
- Blood type, marital status
- Phone numbers (multiple)
- Emergency contacts
- Patient photo storage reference
Include proper JPA mappings and Indonesian-specific validations
```

### 2.2 Patient Registration API
```
Implement patient registration REST API with:
- POST /api/patients for new patient registration
- NIK validation (length, checksum if applicable)
- Duplicate checking by NIK and medical record number
- BPJS number validation
- Auto-generation of medical record number with format: YYYYMM-XXXXX
- Integration point preparation for DUKCAPIL NIK verification
- Comprehensive error handling with Indonesian error messages
Include request/response DTOs and proper validation
```

### 2.3 Patient Search and Retrieval
```
Create patient search functionality with:
- Search by NIK, BPJS number, medical record number, name
- Pagination and sorting
- Advanced filters (age range, registration date, address)
- Fast search using PostgreSQL full-text search
- Patient barcode/QR code generation for patient cards
Return results with configurable data depth (basic, detailed, complete)
```

### 2.4 Patient Photo Management
```
Implement patient photo upload and management:
- Photo upload endpoint with file size and type validation
- Storage using local filesystem with configurable path
- Thumbnail generation for quick loading
- Photo retrieval API with caching headers
- Fallback to default avatar if no photo
- GDPR-compliant photo deletion
```
---

## Phase 3: Registration & Encounter Management

### 3.1 Outpatient Registration
```
Create outpatient (Rawat Jalan) registration system with:
- Polyclinic selection (Poli Umum, Poli Anak, Poli Kandungan, etc.)
- Doctor selection with schedule availability checking
- Queue number generation per polyclinic
- Appointment booking with time slots
- Walk-in vs appointment differentiation
- Registration fee calculation
- Print queue ticket functionality
Include validation for doctor schedules and polyclinic operating hours
```

### 3.2 Inpatient Admission
```
Implement inpatient (Rawat Inap) admission with:
- Room class selection (VIP, Kelas 1, Kelas 2, Kelas 3)
- Bed availability checking and assignment
- Admission form with diagnoses
- Referring doctor information
- Estimated length of stay
- Deposit calculation based on room class
- Patient wristband data generation
Include room and bed management entities
```
### 3.3 Emergency Registration
```
Create emergency (IGD) registration with:
- Triage level assignment (Red, Yellow, Green)
- Fast-track registration with minimal data
- Unknown/unconscious patient handling
- Police case marking (kecelakaan, violence cases)
- Ambulance arrival tracking
- Auto-conversion to inpatient when admitted
Include critical patient prioritization
```


### `3.4 Encounter Management`

#### 3.4.1 Encounter Core Structure
```
Implement comprehensive encounter/visit management system:

Database Schema:
- encounters table:
* id (UUID/BIGINT primary key)
* encounter_number (unique, auto-generated: ENC-YYYYMMDD-XXXX)
* patient_id (FK to patients)
* encounter_type (enum: OUTPATIENT, INPATIENT, EMERGENCY)
* encounter_class (ambulatory, inpatient, emergency, home health)
* status (enum: PLANNED, ARRIVED, TRIAGED, IN_PROGRESS, FINISHED, CANCELLED)
* priority (routine, urgent, emergency)
* admission_date_time (timestamp)
* discharge_date_time (timestamp, nullable)
* service_type (FK to service_types: consultation, procedure, surgery, etc.)
* department_id (FK to departments)
* location_id (FK to locations/rooms)
* practitioner_id (FK to practitioners - attending doctor)
* referring_practitioner_id (FK to practitioners, nullable)
* insurance_type (BPJS, Asuransi Swasta, Umum/Self-Pay)
* insurance_number (nullable)
* sep_number (BPJS Surat Eligibilitas Peserta, nullable)
* created_by, updated_by, created_at, updated_at
* satusehat_encounter_id (nullable - for integration)
* satusehat_synced (boolean default false)
* satusehat_synced_at (timestamp nullable)

- encounter_participants table (many-to-many):
* encounter_id, practitioner_id
* participant_type (primary, secondary, consultant, anesthesiologist)
* period_start, period_end

- encounter_diagnoses table:
* encounter_id, diagnosis_id (FK to icd10_codes)
* diagnosis_type (primary, secondary, admission, discharge)
* clinical_status (active, resolved)
* rank (integer for ordering)

- encounter_status_history table:
* encounter_id, status, timestamp, user_id, notes
```

#### 3.4.2 Encounter Types Implementation

**A. Outpatient Encounters (Rawat Jalan)**
```
Workflow:
1. Patient registration/arrival
   - Create encounter with status: ARRIVED
   - Select polyclinic/department
   - Assign queue number
   - Verify insurance eligibility (BPJS/private)

2. Triage (for emergency/urgent cases)
   - Record vital signs
   - Set priority level
   - Status: TRIAGED

3. Doctor consultation
   - Status: IN_PROGRESS
   - Clinical documentation (SOAP)
   - Diagnosis entry (ICD-10)
   - Procedure/action performed (ICD-9-CM)
   - Medication prescription
   - Lab/radiology orders

4. Completion
   - Status: FINISHED
   - Generate summary
   - Print prescriptions/lab orders
   - Schedule follow-up if needed
   - Auto-trigger billing

Validation Rules:
- Must have at least one diagnosis before finish
- Must have attending practitioner assigned
- Insurance encounters require SEP number (BPJS)
```

**B. Inpatient Encounters (Rawat Inap)**
```
Workflow:
1. Admission
   - Create encounter with status: PLANNED or ARRIVED
   - Assign bed/room (update location_id)
   - Admission diagnosis
   - Admission orders (diet, medications, monitoring)

2. Hospitalization
   - Status: IN_PROGRESS
   - Daily progress notes (SOAP)
   - Vital signs monitoring (every shift/hourly for critical)
   - Medication administration records
   - Procedures/interventions
   - Diagnosis updates
   - Lab/imaging results review

3. Transfer between wards/departments
   - Record in encounter_location_history
   - Update current location_id
   - Handover notes

4. Discharge
   - Discharge diagnosis (may differ from admission)
   - Discharge summary generation
   - Discharge medications (Resume Medis)
   - Follow-up instructions
   - Status: FINISHED
   - Discharge_date_time recorded
   - Final billing calculation

Additional Features:
- Bed occupancy tracking
- Length of stay (LOS) calculation
- Critical care vs regular ward differentiation
- Discharge planning workflow
- Against medical advice (AMA) documentation
```

**C. Emergency Encounters (IGD/UGD)**
```
Workflow:
1. Arrival
   - Immediate registration (minimal data)
   - Status: ARRIVED
   - Triage assessment (use Emergency Severity Index)
   - Priority: EMERGENCY/URGENT

2. Emergency treatment
   - Status: IN_PROGRESS
   - Rapid documentation
   - Critical interventions tracking
   - Real-time vital signs
   - Emergency medications/procedures

3. Disposition
   - Discharge (Status: FINISHED)
   - Admit to inpatient (auto-convert encounter or create new)
   - Transfer to other facility (referral)
   - DOA/expired documentation

Special Features:
- Triage color coding (red/yellow/green)
- Time-sensitive tracking (door-to-doctor time)
- Resuscitation documentation
- Quick clinical templates
- Emergency consent documentation
```

#### 3.4.3 Encounter Status State Machine
```java
// Allowed status transitions
PLANNED -> ARRIVED -> TRIAGED -> IN_PROGRESS -> FINISHED
PLANNED -> CANCELLED
ARRIVED -> CANCELLED
TRIAGED -> CANCELLED

// Business rules
- Cannot finish without at least one diagnosis
- Cannot cancel encounter with linked billing transactions
- Status changes logged in encounter_status_history
- Notifications sent on status changes (queue updates, etc.)
```

#### 3.4.4 Department Transfer Management
```
For inpatient encounters:

encounter_location_history table:
- encounter_id
- from_department_id, to_department_id
- from_location_id (bed/room), to_location_id
- transfer_type (routine, emergency, step-down, step-up)
- transfer_reason
- transfer_date_time
- transferring_practitioner_id
- receiving_practitioner_id
- handover_notes (clinical summary, pending tasks, special instructions)
- approved_by (for ICU/special care transfers)

Workflow:
1. Request transfer
   - Check destination bed availability
   - Require clinical summary
   - Supervisor approval (if applicable)

2. Execute transfer
   - Update encounter department_id and location_id
   - Record in history table
   - Release source bed
   - Occupy destination bed
   - Notify receiving department
   - Update care team assignments

3. Billing impact
   - Calculate charges for previous department
   - Start new charge period for new department
```

#### 3.4.5 Discharge Process
```
Discharge Planning Workflow:

1. Discharge readiness assessment
   - Medical stability criteria met
   - Home care arrangements confirmed
   - Medications reconciled
   - Follow-up scheduled

2. Discharge documentation
   - Discharge summary (Resume Medis):
     * Admission date and chief complaint
     * Hospital course summary
     * Procedures performed
     * Admission vs discharge diagnoses
     * Discharge condition
     * Discharge medications with instructions
     * Diet and activity restrictions
     * Follow-up appointments
     * Emergency return criteria

3. Discharge prescriptions
   - Medication list with dosing
   - Duration of treatment
   - Pharmacy instructions

4. Discharge instructions
   - Wound care if applicable
   - Physical therapy exercises
   - Dietary guidelines
   - Activity restrictions
   - Warning signs to watch for

5. Administrative completion
   - Status: FINISHED
   - Generate discharge summary PDF
   - Finalize billing
   - Release bed
   - Schedule follow-up visit
   - Provide patient copy of documents

Document Templates:
- Discharge summary (Resume Medis Rawat Inap)
- Discharge prescription
- Home care instructions
- Sick leave certificate (Surat Keterangan Sakit)
```

#### 3.4.6 Referral Letter Generation
```
Surat Rujukan (Referral Letter) System:

Types:
1. Internal referral (between departments)
2. External referral (to other facilities)
3. BPJS referral (to specialist/higher facility)

Required Information:
- Referring hospital/clinic details
- Destination hospital/specialist
- Patient demographics
- Current diagnoses (ICD-10)
- Clinical summary (anamnesis, physical exam, investigations)
- Treatment already given
- Reason for referral
- Urgency level
- Referring doctor signature

Integration Points:
- BPJS VClaim for authorized referrals
- PCare for primary care referrals
- SATUSEHAT for referral tracking

Generate as PDF with QR code for verification
Store in document_management system
```

#### 3.4.7 Visit History and Timeline
```
Patient Encounter History View:

Display Components:
- Chronological list of all encounters
- Encounter type badges (outpatient/inpatient/emergency)
- Department visited
- Attending doctor
- Primary diagnosis
- Admission and discharge dates
- Length of stay (for inpatient)
- Outcome/status

Filters:
- Date range
- Encounter type
- Department
- Doctor
- Diagnosis category

Timeline Visualization:
- Graphical timeline showing:
  * Multiple admissions over time
  * Readmissions (flag if <30 days)
  * Chronic disease progression
  * Treatment patterns

Export Options:
- Generate comprehensive medical history PDF
- Include all encounter summaries
- Include timeline of diagnoses
```

#### 3.4.8 Integration with Other Modules
```
Link encounters to:

1. Clinical Documentation
   - SOAP notes (medical_records table)
   - Progress notes (for inpatient)
   - Consultation notes
   - Procedure reports

2. Orders and Results
   - Medication orders (prescriptions table)
   - Laboratory orders and results
   - Radiology orders and reports
   - Other service requests

3. Billing
   - Auto-generate billing transaction on encounter creation
   - Link all charges to encounter_id
   - Calculate based on encounter type and insurance
   - INA-CBG grouping for BPJS inpatient

4. Pharmacy
   - Encounter-based prescription validation
   - Inpatient medication administration records
   - Stock allocation per encounter

5. BPJS Integration
   - SEP (Surat Eligibilitas Peserta) validation
   - VClaim submission
   - Prior authorization for specific procedures

6. SATUSEHAT Integration (FHIR R4)
   - Sync completed encounters to SATUSEHAT
   - Map status to FHIR Encounter status codes
   - Include participant practitioners
   - Link diagnoses as Condition resources
   - See Phase 6 for complete FHIR mapping
```

#### 3.4.9 Queue Management Integration
```
Real-time Queue Updates:

When encounter status changes:
- ARRIVED: Add to department queue
- TRIAGED: Update priority in queue
- IN_PROGRESS: Mark as "being served", remove from waiting list
- FINISHED: Remove from queue, update statistics

Queue Display:
- Waiting patients count
- Average waiting time
- Next patient alert
- Doctor workload view

Integration with registration kiosk and display boards
```

#### 3.4.10 Reporting and Analytics
```
Encounter Analytics:

Daily Reports:
- Total encounters by type
- Average length of stay (ALOS)
- Bed occupancy rate (BOR)
- Bed turnover rate (BTR)
- Emergency response times

Monthly Reports:
- Top 10 diagnoses by encounter type
- Readmission rates (<30 days)
- Average cost per encounter
- Insurance mix (BPJS vs private vs self-pay)
- Doctor productivity (encounters per day)

Performance Indicators:
- Door-to-doctor time (emergency)
- Time-to-admission (inpatient)
- Discharge before noon rate
- Average queue waiting time
- Patient satisfaction scores
```

#### 3.4.11 Special Scenarios
```
Handle edge cases:

1. Auto-conversion Emergency to Inpatient:
   - Create new inpatient encounter
   - Link to original emergency encounter
   - Maintain continuity of clinical data
   - Separate billing transactions

2. Same-day multiple encounters:
   - Allow multiple outpatient encounters (different departments)
   - Prevent duplicate active inpatient encounters
   - Flag potential duplicate registrations

3. Encounter cancellation:
   - Require cancellation reason
   - Check for linked data (results, medications)
   - Handle billing reversals
   - Cannot cancel if medications dispensed or procedures done

4. Encounter reopening:
   - For missed documentation
   - Require supervisor approval
   - Log audit trail
   - Time limit (e.g., 24 hours post-discharge)

5. External patient encounters:
   - Emergency patients without full registration
   - Unknown patient handling
   - Post-encounter data completion
```

#### 3.4.12 Security and Audit
```
Access Control:
- Role-based viewing of encounters
- Department-based data filtering
- Doctor can only modify own encounters
- Supervisor override capabilities

Audit Trail:
- Log all status changes with timestamp and user
- Track all modifications to encounter data
- Record access to sensitive encounters (VIP, psychiatric)
- Compliance with Indonesian healthcare privacy regulations

Data Retention:
- Active encounters: immediate access
- Completed encounters: archive after 5 years
- Legal hold capability for medico-legal cases
```

---

## Phase 4: Clinical Module

### 4.1 Medical Record Structure
```
Design electronic medical record system with:
- SOAP format (Subjective, Objective, Assessment, Plan)
- Vital signs recording (blood pressure, temperature, pulse, respiration)
- Chief complaints and history
- Physical examination findings
- Diagnosis with ICD-10 coding
- Procedures with ICD-9-CM coding
- Doctor's notes with digital signature preparation
Include templates for common cases
```

### 4.2 Diagnosis Management
```
Implement diagnosis recording with:
- ICD-10 master data with Indonesian translations
- Primary and secondary diagnoses
- Diagnosis search with autocomplete
- Common diagnoses quick selection per department
- Diagnosis history per patient
- Validation for insurance claims requirements
Include top 10 diagnoses per polyclinic for quick access
```

### 4.3 Medical Procedures
```
Create procedure management with:
- ICD-9-CM procedure codes
- Procedure scheduling
- Pre-procedure checklists
- Consent form tracking
- Operating room scheduling integration
- Post-procedure monitoring
- Procedure cost calculation
Include procedure templates per specialty
```

### 4.4 Clinical Documents
```
Build clinical document management:
- Document templates (Surat Keterangan Sehat, Surat Sakit, etc.)
- Digital signature placeholder
- PDF generation for all documents
- Document versioning and audit trail
- Informed consent forms
- Medical resume (Resume Medis) generation
Include automatic field population from patient data
```

---

## Phase 5: BPJS Trust Mark Integration

### 5.1 BPJS Base Configuration & Authentication
```
Set up BPJS Kesehatan Trust Mark integration with proper endpoints and authentication:

Base URLs for Development:
- VClaim: https://apijkn-dev.bpjs-kesehatan.go.id/vclaim-rest-dev
- Antrean RS: https://apijkn-dev.bpjs-kesehatan.go.id/antreanrs-dev
- Apotek: https://apijkn-dev.bpjs-kesehatan.go.id/apotek-rest-dev
- Aplicares: https://apijkn-dev.bpjs-kesehatan.go.id/aplicaresws-rest-dev
- iCare JKN: https://apijkn-dev.bpjs-kesehatan.go.id/icare-dev
- eRekamMedis: https://apijkn-dev.bpjs-kesehatan.go.id/erekammedis-dev
- PCare: https://apijkn-dev.bpjs-kesehatan.go.id/pcare-rest-dev

Base URLs for Production:
- VClaim: https://apijkn.bpjs-kesehatan.go.id/vclaim-rest
- Antrean RS: https://apijkn.bpjs-kesehatan.go.id/antreanrs
- Apotek: https://apijkn.bpjs-kesehatan.go.id/apotek-rest
- Aplicares: https://new-api.bpjs-kesehatan.go.id/aplicaresws
- iCare JKN: https://apijkn.bpjs-kesehatan.go.id/wsihs/api/rs
- eRekamMedis: https://dvlp.bpjs-kesehatan.go.id/eRekamMedis

Authentication Headers:
- X-cons-id: Consumer ID from BPJS Kesehatan
- X-timestamp: Unix timestamp in seconds (UTC+7 for Indonesia)
- X-signature: HMAC-SHA256 signature with Base64 encoding
- user_key: User key for specific web service access

Signature Generation Implementation:
1. Create string: {cons_id}&{timestamp}
2. Generate HMAC-SHA256 using consumer secret as key
3. Encode result to Base64
4. Add to X-signature header

Response Decryption:
1. Decompress using LZ-String algorithm
2. Decrypt AES-256 using key from signature
3. Parse JSON response
4. Handle metadata and response codes

Error Handling:
- 200: Success
- 201: Data not found
- 400: Bad request (validation error)
- 401: Unauthorized (signature/timestamp invalid)
- 402: Forbidden (consumer not registered)
- 404: Not found (endpoint error)
- 500: Internal server error
```

### 5.2 VClaim Services Integration
```
Implement comprehensive VClaim services for claim management:

1. Participant (Peserta) Services:
   - Check eligibility by BPJS card number
   - Check eligibility by NIK
   - Retrieve participant details with history
   - Validate participant class and status
   - Check COB (Coordination of Benefits) status

2. Referral (Rujukan) Management:
   - Search referral by number
   - Search referral by BPJS card
   - Multi referral support (Rujukan > 1)
   - Referral from FKTP and FKRTL
   - Khusus referral handling
   - Spesialistik and sub-spesialistik referrals

3. SEP (Surat Eligibilitas Peserta) Services:
   - Create SEP for outpatient/inpatient
   - Update SEP data
   - Delete SEP with proper validation
   - Internal SEP (for hospital referrals)
   - SEP bridging for existing visits
   - SEP for KLL (traffic accidents)
   - SEP fingerprint validation

4. Monitoring & Reporting:
   - Klaim tracking by date range
   - Klaim history by participant
   - Data kunjungan monitoring
   - Pelayanan peserta tracking
   - Jasa raharja integration for accidents

Include comprehensive error handling and response decryption
```

### 5.3 Aplicares Integration
```
Implement Aplicares for quality monitoring and bed availability:

1. Bed Management (Ketersediaan Kamar):
   - GET /rest/ref/kelas - Reference room classes
   - POST /rest/bed/update - Update bed availability
   - POST /rest/bed/create - Create new room
   - GET /rest/bed/read - Check room availability by hospital code
   - DELETE /rest/bed/delete - Remove room

2. Room Class Management:
   - Support all BPJS room classes (VIP, VVIP, Kelas 1-3, etc.)
   - Real-time bed availability updates
   - Capacity tracking per room class
   - Integration with admission system

3. Quality Indicators:
   - Clinical pathway compliance
   - Service response time (waktu tanggap)
   - Patient safety indicators
   - Infection control metrics
   - Monthly reporting to BPJS

Implement proper authentication headers and response handling
```

### 5.4 Antrean RS (Queue Management) Integration
```
Build comprehensive queue management system integrated with BPJS:

1. Reference Data Services:
   - GET /ref/poli - Polyclinic references
   - GET /ref/dokter - Doctor references
   - GET /jadwaldokter/kodepoli/{kodepoli}/tanggal/{tanggal} - Doctor schedules
   - POST /updatejadwaldokter - Update doctor schedules
   - GET /ref/poli/fp - Fingerprint polyclinic data
   - GET /ref/pasien/fp/identitas/{identitas}/noidentitas/{noidentitas} - Patient fingerprint

2. Queue Management:
   - POST /antrean/add - Add new queue
   - POST /antrean/farmasi/add - Add pharmacy queue
   - POST /antrean/updatewaktu - Update queue timestamps
   - POST /antrean/batal - Cancel queue
   - GET /antrean/pendaftaran/tanggal/{tanggal} - Get queues by date
   - GET /antrean/pendaftaran/kodebooking/{kodebooking} - Get queue by booking code

3. Task ID Management:
   - Task 1: Patient arrival at hospital
   - Task 2: Registration desk service
   - Task 3: Polyclinic queue
   - Task 4: Doctor consultation
   - Task 5: Pharmacy queue
   - Task 6: Prescription ready
   - Task 7: Billing
   - Task 99: Task not available

4. Dashboard & Monitoring:
   - GET /dashboard/waktutunggu/tanggal/{tanggal}/waktu/{waktu} - Daily dashboard
   - GET /dashboard/waktutunggu/bulan/{bulan}/tahun/{tahun}/waktu/{waktu} - Monthly dashboard
   - Real-time queue monitoring
   - Average waiting time calculation

Implement with proper timestamp handling and booking code generation
```

### 5.5 Apotek (Pharmacy) Integration
```
Implement pharmacy services integration with BPJS:

1. Reference Data:
   - Drug formulary status
   - DPHO (Daftar Plafon Harga Obat) references
   - Generic and branded drug mapping
   - Drug interactions database

2. Prescription Services:
   - Validate prescriptions against formulary
   - Check drug availability
   - Calculate drug costs with BPJS limits
   - Non-formulary drug approval process

3. Dispensing Management:
   - Record drug dispensing
   - Track PRB (Program Rujuk Balik) drugs
   - Chronic disease medication tracking
   - Drug stock integration

4. Claim Integration:
   - Drug claim preparation
   - Formulary compliance reporting
   - Cost control monitoring

Include proper error handling for drug availability and formulary checks
```

### 5.6 iCare JKN Integration
```
Implement iCare JKN for patient history and validation:

1. Patient History Validation:
   - POST /validate - Validate and retrieve patient history
   - Parameters: patient card number, doctor code
   - Returns: Secure URL token for history access
   - Token expiry handling

2. History Data Access:
   - Retrieve complete patient treatment history
   - Access diagnostic history
   - Medication history tracking
   - Referral chain validation

3. Security Implementation:
   - Signature creation for FKTL access
   - Response decryption handling
   - Token-based secure URL access
   - Session management

Include proper authentication and decryption mechanisms
```

### 5.7 eRekam Medis Integration
```
Build electronic medical record integration with BPJS:

1. Medical Record Submission:
   - Patient demographic sync
   - Diagnosis submission (ICD-10)
   - Procedure submission (ICD-9-CM)
   - Medication records
   - Laboratory results
   - Radiology reports

2. Data Standards:
   - FHIR R4 compliance
   - HL7 message formatting
   - SATUSEHAT interoperability
   - Data validation rules

3. Compliance Reporting:
   - Kelengkapan rekam medis
   - Timeliness of documentation
   - Quality indicators
   - Audit trail maintenance

Implement with proper data encryption and audit logging
```

### 5.8 Claim Processing & INA-CBGs
```
Build comprehensive E-Klaim 5.10.x Web Service integration for claim processing:

1. E-Klaim Web Service Setup:
   - Configure web service URL and credentials
   - Implement AES-256-CBC encryption for all API communications
   - Generate and manage encryption keys (16-byte key, 16-byte IV)
   - Implement HMAC-SHA256 signatures for data integrity
   - Handle JSON request/response with base64 encoding

   Key Configuration:
   - ws_server: Web service endpoint URL
   - cons_id: Consumer ID from E-Klaim
   - secret_key: Secret key for encryption
   - user_key: User authentication key (kodeinacbg from aplikasi table)

2. Core API Methods Implementation:

   a. Claim Initialization:
      - new_claim(): Create new claim with patient data
      - set_claim_data(): Update claim details progressively
      - claim_print(): Generate claim printout

   b. Grouper Operations:
      - grouper_idrg(): Process iDRG grouping for diagnoses
        * Input: nomor_sep, diagnosa codes (principal + secondary)
        * Output: iDRG code, description, tariff components
      - grouper_inacbg(): Process INACBG grouping for complete case
        * Input: Full patient data, procedures, diagnoses
        * Output: INACBG code, tariffs, special CMG flags

   c. Claim Finalization:
      - finalisasi_claim(): Submit final claim for processing
      - send_claim_individual(): Send individual claims to BPJS
      - send_claim_batch(): Batch submission for multiple claims

   d. Claim Management:
      - get_claim_data(): Retrieve claim details
      - delete_claim(): Remove draft claims
      - reedit_claim(): Reopen finalized claims for correction
      - claim_jkn(): Process JKN (BPJS) specific claims

3. Data Validation & Error Handling:

   Error Code Categories:
   - 200: Success
   - 201-299: Field-specific validation errors
   - 300-399: Business logic errors
   - 400-499: Authorization/authentication errors
   - 500-599: System/server errors

   Common Validations:
   - Diagnosa code verification (ICD-10)
   - Procedure code validation (ICD-9-CM)
   - SEP number format checking
   - Date range validations
   - Tariff limit checks

4. Special Integration Workflows:

   a. SITB (TB Patient System):
      - sitb_list_task(): Get TB patient task list
      - sitb_apply_task(): Apply TB treatment tasks
      - sitb_update_task(): Update treatment progress
      - sitb_finish_task(): Complete TB treatment cycle
      - sitb_list_rujukan(): Manage TB referrals

   b. Special CMG Processing:
      - Handle COVID-19 claims (special_cmg flags)
      - Process chronic disease top-ups
      - Calculate special procedure additional tariffs
      - Manage upgrade class differentials

5. Encryption/Decryption Implementation:

   Request Encryption:
   ```python
   # AES-256-CBC encryption
   cipher = AES.new(key.encode(), AES.MODE_CBC, iv.encode())
   encrypted = base64.b64encode(cipher.encrypt(pad(json_data)))

   # Generate signature
   signature = hmac.new(secret_key.encode(), encrypted, hashlib.sha256).digest()
   request_data = base64.b64encode(json.dumps({
       "data": encrypted.decode(),
       "signature": base64.b64encode(signature).decode()
   }))
   ```

   Response Decryption:
   ```python
   # Verify signature
   received_signature = base64.b64decode(response["signature"])
   expected_signature = hmac.new(secret_key.encode(), response["data"], hashlib.sha256).digest()

   # Decrypt data
   cipher = AES.new(key.encode(), AES.MODE_CBC, iv.encode())
   decrypted = unpad(cipher.decrypt(base64.b64decode(response["data"])))
   ```

6. Claim Status Tracking:

   Status Codes:
   - 1: Draft (editable)
   - 2: Finalized (locked for submission)
   - 3: Sent to BPJS
   - 4: Verified by BPJS
   - 5: Paid
   - 6: Rejected (requires correction)
   - 7: Pending verification
   - 8: CBG review needed

   Monitoring Methods:
   - get_claim_status(): Check individual claim status
   - monitoring_klaim(): Batch status monitoring
   - get_status_verifikasi(): Detailed verification status

7. Financial Reconciliation:

   Payment Processing:
   - claim_ba(): Process payment batch acknowledgment
   - get_data_base_plafon(): Check remaining budget ceiling
   - get_data_pantauan_jkn(): Monitor JKN claim statistics

   Reporting:
   - Generate claim summary reports
   - Track payment vs submission ratios
   - Monitor rejection rates by type
   - Calculate average processing times

8. Advanced Features:

   a. Pending Claims Management:
      - pending_klaim(): List all pending claims
      - claim_final(): Bulk finalization
      - grouper_stage(): Progressive grouping for complex cases

   b. Re-admission Handling:
      - readmisi_ditolak(): Handle rejected readmissions
      - readmisi_diterima(): Process approved readmissions
      - claim_hemodialisa(): Special HD claim processing

   c. Integration Points:
      - Pull SEP data from BPJS VClaim
      - Push claim results to hospital billing
      - Sync with SIMRS for patient data
      - Export to hospital financial system

9. Audit Trail Requirements:

   Log All Operations:
   - API request/response pairs
   - User actions with timestamps
   - Claim state transitions
   - Error occurrences with context
   - Payment confirmations
   - Data modifications history

   Compliance Tracking:
   - Store encrypted copies of submitted claims
   - Maintain verification documents
   - Track approval chains
   - Document rejection resolutions

10. Performance Optimization:

    - Implement request queuing for batch operations
    - Cache frequently used reference data (ICD codes, tariffs)
    - Use connection pooling for API calls
    - Implement retry logic with exponential backoff
    - Monitor API rate limits (default: 1000 req/hour)
    - Optimize large claim batches (max 100 claims/batch)

Store all E-Klaim transactions with complete audit trail including request/response data, timestamps, and user actions
```

---

## Phase 6: SATUSEHAT Integration

### 6.1 SATUSEHAT OAuth2 Authentication Setup
```
Configure comprehensive SATUSEHAT OAuth2 authentication system:

1. Environment Configuration:
   Development/Sandbox:
   - Auth URL: https://api-satusehat-stg.dto.kemkes.go.id/oauth2/v1
   - FHIR Base URL: https://api-satusehat-stg.dto.kemkes.go.id/fhir-r4/v1
   - Client ID: Obtained from SATUSEHAT registration
   - Client Secret: Encrypted storage required

   Production:
   - Auth URL: https://api-satusehat.kemkes.go.id/oauth2/v1
   - FHIR Base URL: https://api-satusehat.kemkes.go.id/fhir-r4/v1
   - Client ID: Production credentials from Ministry of Health
   - Client Secret: Must be secured with encryption

2. Token Management Implementation:
   a. Access Token Request:
      POST /accesstoken?grant_type=client_credentials
      Headers:
      - Content-Type: application/x-www-form-urlencoded
      Body:
      - client_id={client_id}
      - client_secret={client_secret}

      Response:
      {
        "refresh_token_expires_in": "0",
        "api_product_list": "[api-satusehat-prod]",
        "api_product_list_json": ["api-satusehat-prod"],
        "organization_name": "kemkes",
        "token_type": "Bearer",
        "issued_at": "1234567890000",
        "client_id": "your-client-id",
        "access_token": "eyJ...",
        "application_name": "your-app-name",
        "scope": "",
        "expires_in": "3599",
        "refresh_count": "0",
        "status": "approved"
      }

   b. Token Storage:
      - Store in Redis with TTL
      - Implement token refresh 5 minutes before expiry
      - Cache token per environment
      - Encrypt tokens at rest

   c. Token Usage:
      - Add to all API requests: Authorization: Bearer {access_token}
      - Implement automatic retry with new token on 401

3. Rate Limiting & Throttling:
   - Default: 100 requests per second
   - Burst: 1000 requests per minute
   - Implement request queue with rate limiter
   - Track API usage per endpoint
   - Implement circuit breaker for failures

4. Organization Configuration:
   - Organization ID: Obtained from SATUSEHAT registration
   - Location ID: For each facility/unit
   - Practitioner IDs: For all healthcare providers
   - Store in configuration with proper mapping

5. Error Handling:
   - 401: Token expired - refresh automatically
   - 403: Forbidden - check organization permissions
   - 429: Rate limit exceeded - implement backoff
   - 500-503: Server errors - retry with exponential backoff

Include comprehensive logging for all authentication activities
```

### 6.2 Patient Resource Management
```
Implement complete FHIR R4 Patient resource for SATUSEHAT:

1. Patient Resource Structure:
   {
     "resourceType": "Patient",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Patient"]
     },
     "identifier": [
       {
         "use": "official",
         "system": "https://fhir.kemkes.go.id/id/nik",
         "value": "3174012345678901"  // NIK
       },
       {
         "use": "official",
         "system": "https://fhir.kemkes.go.id/id/ihs-number",
         "value": "P02478375538"  // IHS Number from SATUSEHAT
       },
       {
         "use": "usual",
         "system": "https://fhir.kemkes.go.id/id/medical-record",
         "value": "123456"  // Local MR Number
       }
     ],
     "active": true,
     "name": [
       {
         "use": "official",
         "text": "BUDI SANTOSO",
         "family": "SANTOSO",
         "given": ["BUDI"]
       }
     ],
     "telecom": [
       {
         "system": "phone",
         "value": "081234567890",
         "use": "mobile"
       },
       {
         "system": "email",
         "value": "budi@example.com",
         "use": "home"
       }
     ],
     "gender": "male",
     "birthDate": "1990-01-15",
     "deceasedBoolean": false,
     "address": [
       {
         "use": "home",
         "type": "both",
         "text": "Jl. Merdeka No. 123, RT 001/RW 002",
         "line": ["Jl. Merdeka No. 123"],
         "city": "Jakarta Selatan",
         "district": "Kebayoran Baru",
         "state": "DKI Jakarta",
         "postalCode": "12345",
         "country": "ID",
         "extension": [
           {
             "url": "https://fhir.kemkes.go.id/r4/StructureDefinition/administrativeCode",
             "extension": [
               {"url": "province", "valueCode": "31"},
               {"url": "city", "valueCode": "3174"},
               {"url": "district", "valueCode": "317401"},
               {"url": "village", "valueCode": "31740101"}
             ]
           }
         ]
       }
     ],
     "maritalStatus": {
       "coding": [
         {
           "system": "http://terminology.hl7.org/CodeSystem/v3-MaritalStatus",
           "code": "M",
           "display": "Married"
         }
       ]
     },
     "multipleBirthBoolean": false,
     "contact": [
       {
         "relationship": [
           {
             "coding": [
               {
                 "system": "http://terminology.hl7.org/CodeSystem/v2-0131",
                 "code": "C",
                 "display": "Emergency Contact"
               }
             ]
           }
         ],
         "name": {
           "use": "official",
           "text": "SITI AMINAH"
         },
         "telecom": [
           {
             "system": "phone",
             "value": "081987654321",
             "use": "mobile"
           }
         ]
       }
     ],
     "communication": [
       {
         "language": {
           "coding": [
             {
               "system": "urn:ietf:bcp:47",
               "code": "id-ID",
               "display": "Indonesian"
             }
           ]
         },
         "preferred": true
       }
     ],
     "extension": [
       {
         "url": "https://fhir.kemkes.go.id/r4/StructureDefinition/patient-religion",
         "valueCodeableConcept": {
           "coding": [
             {
               "system": "https://fhir.kemkes.go.id/r4/CodeSystem/patient-religion",
               "code": "ISL",
               "display": "Islam"
             }
           ]
         }
       },
       {
         "url": "https://fhir.kemkes.go.id/r4/StructureDefinition/patient-nationality",
         "valueCode": "WNI"
       }
     ]
   }

2. Patient Operations:
   a. Create Patient:
      POST /Patient
      - Validate NIK format (16 digits)
      - Check for existing patient by NIK
      - Return IHS number on success

   b. Update Patient:
      PUT /Patient/{ihs-number}
      - Update allowed fields only
      - Maintain audit trail

   c. Search Patient:
      GET /Patient?identifier=https://fhir.kemkes.go.id/id/nik|3174012345678901
      GET /Patient?name=BUDI&birthdate=1990-01-15
      GET /Patient?identifier=https://fhir.kemkes.go.id/id/ihs-number|P02478375538

   d. Get Patient by ID:
      GET /Patient/{ihs-number}

3. Data Mapping:
   - NIK → identifier[system=nik]
   - Name → name[use=official]
   - Gender → gender (male/female/other/unknown)
   - Birth Date → birthDate (YYYY-MM-DD)
   - Address → address with administrative codes
   - Phone → telecom[system=phone]
   - Religion → extension[patient-religion]
   - Blood Type → extension[patient-bloodType]

4. Validation Requirements:
   - NIK: Required, 16 digits, valid checksum
   - Name: Required, uppercase
   - Birth Date: Required, format YYYY-MM-DD
   - Gender: Required, valid codes
   - Address: Required with administrative codes

5. Error Handling:
   - 400: Invalid resource structure
   - 404: Patient not found
   - 409: Duplicate patient (same NIK)
   - 422: Business rule violation

Include retry queue for failed patient submissions
```

### 6.3 Encounter Resource Implementation
```
Build comprehensive Encounter management for SATUSEHAT:

1. Encounter Resource Structure:
   {
     "resourceType": "Encounter",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Encounter"]
     },
     "identifier": [
       {
         "system": "http://sys-ids.kemkes.go.id/encounter/{org-id}",
         "value": "ENC-2025-000001"
       }
     ],
     "status": "in-progress",  // planned | arrived | triaged | in-progress | onleave | finished | cancelled
     "statusHistory": [
       {
         "status": "arrived",
         "period": {
           "start": "2025-01-20T08:00:00+07:00",
           "end": "2025-01-20T08:30:00+07:00"
         }
       }
     ],
     "class": {
       "system": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
       "code": "AMB",  // AMB=Ambulatory, IMP=Inpatient, EMER=Emergency
       "display": "ambulatory"
     },
     "type": [
       {
         "coding": [
           {
             "system": "http://snomed.info/sct",
             "code": "185389009",
             "display": "Follow-up visit"
           }
         ]
       }
     ],
     "serviceType": {
       "coding": [
         {
           "system": "http://terminology.hl7.org/CodeSystem/service-type",
           "code": "124",
           "display": "General Practice"
         }
       ]
     },
     "priority": {
       "coding": [
         {
           "system": "http://terminology.hl7.org/CodeSystem/v3-ActPriority",
           "code": "R",  // R=Routine, UR=Urgent, EM=Emergency
           "display": "routine"
         }
       ]
     },
     "subject": {
       "reference": "Patient/100000030009",
       "display": "BUDI SANTOSO"
     },
     "participant": [
       {
         "type": [
           {
             "coding": [
               {
                 "system": "http://terminology.hl7.org/CodeSystem/v3-ParticipationType",
                 "code": "ATND",
                 "display": "attender"
               }
             ]
           }
         ],
         "individual": {
           "reference": "Practitioner/N10000001",
           "display": "Dr. AHMAD WIJAYA"
         }
       }
     ],
     "period": {
       "start": "2025-01-20T08:00:00+07:00",
       "end": "2025-01-20T09:30:00+07:00"
     },
     "reasonCode": [
       {
         "coding": [
           {
             "system": "http://hl7.org/fhir/sid/icd-10",
             "code": "K29.7",
             "display": "Gastritis, unspecified"
           }
         ]
       }
     ],
     "diagnosis": [
       {
         "condition": {
           "reference": "Condition/12345",
           "display": "Gastritis"
         },
         "use": {
           "coding": [
             {
               "system": "http://terminology.hl7.org/CodeSystem/diagnosis-role",
               "code": "DD",  // DD=Discharge, AD=Admission, CC=Chief Complaint
               "display": "Discharge diagnosis"
             }
           ]
         },
         "rank": 1
       }
     ],
     "hospitalization": {
       "admitSource": {
         "coding": [
           {
             "system": "http://terminology.hl7.org/CodeSystem/admit-source",
             "code": "outp",
             "display": "From outpatient department"
           }
         ]
       },
       "dischargeDisposition": {
         "coding": [
           {
             "system": "http://terminology.hl7.org/CodeSystem/discharge-disposition",
             "code": "home",
             "display": "Home"
           }
         ]
       }
     },
     "location": [
       {
         "location": {
           "reference": "Location/xyz123",
           "display": "Poli Umum"
         },
         "status": "completed"
       }
     ],
     "serviceProvider": {
       "reference": "Organization/10000004",
       "display": "RS Harapan Sehat"
     }
   }

2. Encounter Workflows:
   a. Outpatient Encounter:
      - Create on patient registration
      - Update status: arrived → in-progress → finished
      - Link all clinical resources
      - Close on discharge

   b. Inpatient Encounter:
      - Create on admission
      - Track bed transfers
      - Update daily progress
      - Discharge summary required

   c. Emergency Encounter:
      - Immediate creation
      - Triage information
      - Priority handling
      - May convert to inpatient

3. Status Management:
   - planned: Appointment scheduled
   - arrived: Patient checked in
   - triaged: Emergency triage completed
   - in-progress: Consultation ongoing
   - onleave: Temporary leave (inpatient)
   - finished: Encounter completed
   - cancelled: Encounter cancelled

4. Required References:
   - Patient: IHS number required
   - Practitioner: Must be registered in SATUSEHAT
   - Organization: Must match registered org
   - Location: Service location mapping

5. Batch Submission:
   - Queue encounters for batch processing
   - Submit up to 100 per batch
   - Handle partial failures
   - Retry failed submissions

Include comprehensive encounter tracking and reconciliation
```

### 6.4 Clinical Resources Submission
```
Implement complete clinical data submission to SATUSEHAT:

1. Condition (Diagnosis) Resource:
   {
     "resourceType": "Condition",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Condition"]
     },
     "clinicalStatus": {
       "coding": [
         {
           "system": "http://terminology.hl7.org/CodeSystem/condition-clinical",
           "code": "active",  // active | recurrence | relapse | inactive | remission | resolved
           "display": "Active"
         }
       ]
     },
     "verificationStatus": {
       "coding": [
         {
           "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
           "code": "confirmed",  // unconfirmed | provisional | differential | confirmed
           "display": "Confirmed"
         }
       ]
     },
     "category": [
       {
         "coding": [
           {
             "system": "http://terminology.hl7.org/CodeSystem/condition-category",
             "code": "encounter-diagnosis",
             "display": "Encounter Diagnosis"
           }
         ]
       }
     ],
     "severity": {
       "coding": [
         {
           "system": "http://snomed.info/sct",
           "code": "6736007",
           "display": "Moderate"
         }
       ]
     },
     "code": {
       "coding": [
         {
           "system": "http://hl7.org/fhir/sid/icd-10",
           "code": "K29.7",
           "display": "Gastritis, unspecified"
         }
       ]
     },
     "subject": {
       "reference": "Patient/100000030009"
     },
     "encounter": {
       "reference": "Encounter/12345"
     },
     "onsetDateTime": "2025-01-15T00:00:00+07:00",
     "recordedDate": "2025-01-20T09:00:00+07:00",
     "recorder": {
       "reference": "Practitioner/N10000001"
     }
   }

2. Procedure Resource:
   {
     "resourceType": "Procedure",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Procedure"]
     },
     "status": "completed",  // preparation | in-progress | not-done | on-hold | stopped | completed
     "category": {
       "coding": [
         {
           "system": "http://snomed.info/sct",
           "code": "103693007",
           "display": "Diagnostic procedure"
         }
       ]
     },
     "code": {
       "coding": [
         {
           "system": "http://hl7.org/fhir/sid/icd-9-cm",
           "code": "45.13",
           "display": "Esophagogastroduodenoscopy [EGD]"
         }
       ]
     },
     "subject": {
       "reference": "Patient/100000030009"
     },
     "encounter": {
       "reference": "Encounter/12345"
     },
     "performedDateTime": "2025-01-20T10:00:00+07:00",
     "performer": [
       {
         "actor": {
           "reference": "Practitioner/N10000001"
         }
       }
     ],
     "location": {
       "reference": "Location/endoscopy-unit"
     },
     "outcome": {
       "text": "Mild gastritis observed"
     },
     "report": [
       {
         "reference": "DiagnosticReport/67890"
       }
     ]
   }

3. Observation Resources:
   a. Vital Signs:
      {
        "resourceType": "Observation",
        "meta": {
          "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Observation"]
        },
        "status": "final",
        "category": [
          {
            "coding": [
              {
                "system": "http://terminology.hl7.org/CodeSystem/observation-category",
                "code": "vital-signs",
                "display": "Vital Signs"
              }
            ]
          }
        ],
        "code": {
          "coding": [
            {
              "system": "http://loinc.org",
              "code": "85354-9",
              "display": "Blood pressure panel"
            }
          ]
        },
        "subject": {
          "reference": "Patient/100000030009"
        },
        "encounter": {
          "reference": "Encounter/12345"
        },
        "effectiveDateTime": "2025-01-20T08:30:00+07:00",
        "component": [
          {
            "code": {
              "coding": [
                {
                  "system": "http://loinc.org",
                  "code": "8480-6",
                  "display": "Systolic blood pressure"
                }
              ]
            },
            "valueQuantity": {
              "value": 120,
              "unit": "mmHg",
              "system": "http://unitsofmeasure.org",
              "code": "mm[Hg]"
            }
          },
          {
            "code": {
              "coding": [
                {
                  "system": "http://loinc.org",
                  "code": "8462-4",
                  "display": "Diastolic blood pressure"
                }
              ]
            },
            "valueQuantity": {
              "value": 80,
              "unit": "mmHg",
              "system": "http://unitsofmeasure.org",
              "code": "mm[Hg]"
            }
          }
        ]
      }

   b. Laboratory Results:
      {
        "resourceType": "Observation",
        "status": "final",
        "category": [
          {
            "coding": [
              {
                "system": "http://terminology.hl7.org/CodeSystem/observation-category",
                "code": "laboratory",
                "display": "Laboratory"
              }
            ]
          }
        ],
        "code": {
          "coding": [
            {
              "system": "http://loinc.org",
              "code": "2339-0",
              "display": "Glucose [Mass/volume] in Blood"
            }
          ]
        },
        "subject": {
          "reference": "Patient/100000030009"
        },
        "valueQuantity": {
          "value": 95,
          "unit": "mg/dL",
          "system": "http://unitsofmeasure.org",
          "code": "mg/dL"
        },
        "referenceRange": [
          {
            "low": {
              "value": 70,
              "unit": "mg/dL"
            },
            "high": {
              "value": 100,
              "unit": "mg/dL"
            }
          }
        ]
      }

4. MedicationRequest (Prescription):
   {
     "resourceType": "MedicationRequest",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/MedicationRequest"]
     },
     "status": "active",
     "intent": "order",
     "medicationCodeableConcept": {
       "coding": [
         {
           "system": "http://sys-ids.kemkes.go.id/kfa",
           "code": "93001019",
           "display": "Omeprazole 20 mg Kapsul"
         }
       ]
     },
     "subject": {
       "reference": "Patient/100000030009"
     },
     "encounter": {
       "reference": "Encounter/12345"
     },
     "authoredOn": "2025-01-20T09:30:00+07:00",
     "requester": {
       "reference": "Practitioner/N10000001"
     },
     "dosageInstruction": [
       {
         "text": "1 capsule twice daily before meals",
         "timing": {
           "repeat": {
             "frequency": 2,
             "period": 1,
             "periodUnit": "d",
             "when": ["AC"]  // AC=before meals
           }
         },
         "route": {
           "coding": [
             {
               "system": "http://terminology.hl7.org/CodeSystem/v3-RouteOfAdministration",
               "code": "PO",
               "display": "Oral"
             }
           ]
         },
         "doseAndRate": [
           {
             "type": {
               "coding": [
                 {
                   "system": "http://terminology.hl7.org/CodeSystem/dose-rate-type",
                   "code": "ordered",
                   "display": "Ordered"
                 }
               ]
             },
             "doseQuantity": {
               "value": 1,
               "unit": "Capsule",
               "system": "http://unitsofmeasure.org",
               "code": "{Capsule}"
             }
           }
         ]
       }
     ],
     "dispenseRequest": {
       "quantity": {
         "value": 28,
         "unit": "Capsule"
       },
       "expectedSupplyDuration": {
         "value": 14,
         "unit": "days",
         "system": "http://unitsofmeasure.org",
         "code": "d"
       }
     }
   }

5. ServiceRequest (Lab/Radiology Orders):
   {
     "resourceType": "ServiceRequest",
     "status": "active",
     "intent": "order",
     "category": [
       {
         "coding": [
           {
             "system": "http://snomed.info/sct",
             "code": "108252007",
             "display": "Laboratory procedure"
           }
         ]
       }
     ],
     "code": {
       "coding": [
         {
           "system": "http://loinc.org",
           "code": "24323-8",
           "display": "Comprehensive metabolic panel"
         }
       ]
     },
     "subject": {
       "reference": "Patient/100000030009"
     },
     "encounter": {
       "reference": "Encounter/12345"
     },
     "authoredOn": "2025-01-20T09:00:00+07:00",
     "requester": {
       "reference": "Practitioner/N10000001"
     }
   }

6. Bundle Submission:
   {
     "resourceType": "Bundle",
     "type": "transaction",
     "entry": [
       {
         "fullUrl": "urn:uuid:encounter-1",
         "resource": { /* Encounter resource */ },
         "request": {
           "method": "POST",
           "url": "Encounter"
         }
       },
       {
         "fullUrl": "urn:uuid:condition-1",
         "resource": { /* Condition resource */ },
         "request": {
           "method": "POST",
           "url": "Condition"
         }
       },
       {
         "fullUrl": "urn:uuid:observation-1",
         "resource": { /* Observation resource */ },
         "request": {
           "method": "POST",
           "url": "Observation"
         }
       }
     ]
   }

7. Data Submission Strategy:
   - Real-time submission for critical data
   - Batch submission every 15 minutes for routine data
   - Queue management with priority levels
   - Retry failed submissions with exponential backoff
   - Daily reconciliation reports
   - Data completeness monitoring

8. Validation & Compliance:
   - FHIR R4 profile validation
   - Required fields checking
   - Code system validation (ICD-10, ICD-9-CM, LOINC)
   - Reference integrity validation
   - Business rule validation

Include comprehensive error handling and monitoring dashboard
```

### 6.5 Organization and Location Resources
```
Implement Organization and Location management for SATUSEHAT:

1. Organization Resource:
   {
     "resourceType": "Organization",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Organization"]
     },
     "identifier": [
       {
         "use": "official",
         "system": "http://sys-ids.kemkes.go.id/organization",
         "value": "1000001"  // Organization ID from SATUSEHAT
       }
     ],
     "active": true,
     "type": [
       {
         "coding": [
           {
             "system": "http://terminology.hl7.org/CodeSystem/organization-type",
             "code": "prov",
             "display": "Healthcare Provider"
           }
         ]
       }
     ],
     "name": "RS Harapan Sehat",
     "telecom": [
       {
         "system": "phone",
         "value": "021-1234567",
         "use": "work"
       },
       {
         "system": "email",
         "value": "info@rsharapansehat.co.id"
       },
       {
         "system": "url",
         "value": "https://rsharapansehat.co.id"
       }
     ],
     "address": [
       {
         "use": "work",
         "type": "both",
         "line": ["Jl. Kesehatan No. 1"],
         "city": "Jakarta Selatan",
         "state": "DKI Jakarta",
         "postalCode": "12345",
         "country": "ID"
       }
     ],
     "partOf": {
       "reference": "Organization/parent-org-id"  // If part of hospital group
     }
   }

2. Location Resource:
   {
     "resourceType": "Location",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Location"]
     },
     "identifier": [
       {
         "system": "http://sys-ids.kemkes.go.id/location/{org-id}",
         "value": "poli-umum-01"
       }
     ],
     "status": "active",
     "name": "Poli Umum",
     "description": "Poliklinik Umum Lantai 1",
     "mode": "instance",
     "type": [
       {
         "coding": [
           {
             "system": "http://terminology.hl7.org/CodeSystem/v3-RoleCode",
             "code": "OF",
             "display": "Outpatient Facility"
           }
         ]
       }
     ],
     "telecom": [
       {
         "system": "phone",
         "value": "021-1234567 ext 101"
       }
     ],
     "address": {
       "use": "work",
       "line": ["Lantai 1, Gedung A"],
       "city": "Jakarta Selatan"
     },
     "physicalType": {
       "coding": [
         {
           "system": "http://terminology.hl7.org/CodeSystem/location-physical-type",
           "code": "ro",
           "display": "Room"
         }
       ]
     },
     "managingOrganization": {
       "reference": "Organization/1000001"
     }
   }

3. Location Types:
   - Polyclinic locations (Poli Umum, Poli Anak, etc.)
   - Inpatient wards and rooms
   - Operating theaters
   - Emergency department
   - Laboratory
   - Radiology department
   - Pharmacy

4. Hierarchical Structure:
   - Hospital → Building → Floor → Department → Room → Bed
   - Maintain parent-child relationships
   - Track bed availability status

Include location mapping for all service areas
```

### 6.6 Practitioner and PractitionerRole Resources
```
Manage healthcare provider data for SATUSEHAT:

1. Practitioner Resource:
   {
     "resourceType": "Practitioner",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/Practitioner"]
     },
     "identifier": [
       {
         "use": "official",
         "system": "https://fhir.kemkes.go.id/id/nik",
         "value": "3174012345678901"  // NIK
       },
       {
         "use": "official",
         "system": "https://fhir.kemkes.go.id/id/ihs-number",
         "value": "N10000001"  // IHS Number for Practitioner
       },
       {
         "use": "official",
         "system": "https://fhir.kemkes.go.id/id/sip",
         "value": "123.456/SIP/2025"  // SIP Number
       }
     ],
     "active": true,
     "name": [
       {
         "use": "official",
         "text": "Dr. AHMAD WIJAYA, Sp.PD",
         "family": "WIJAYA",
         "given": ["AHMAD"],
         "prefix": ["Dr."],
         "suffix": ["Sp.PD"]
       }
     ],
     "telecom": [
       {
         "system": "phone",
         "value": "081234567890",
         "use": "mobile"
       },
       {
         "system": "email",
         "value": "ahmad.wijaya@rsharapansehat.co.id"
       }
     ],
     "gender": "male",
     "birthDate": "1980-05-15",
     "address": [
       {
         "use": "home",
         "line": ["Jl. Dokter No. 10"],
         "city": "Jakarta",
         "postalCode": "12345"
       }
     ],
     "qualification": [
       {
         "identifier": [
           {
             "system": "https://fhir.kemkes.go.id/id/str",
             "value": "1234567890123456"  // STR Number
           }
         ],
         "code": {
           "coding": [
             {
               "system": "http://terminology.hl7.org/CodeSystem/v2-0360",
               "code": "MD",
               "display": "Doctor of Medicine"
             }
           ]
         },
         "period": {
           "start": "2020-01-01",
           "end": "2025-12-31"
         },
         "issuer": {
           "display": "Konsil Kedokteran Indonesia"
         }
       },
       {
         "code": {
           "coding": [
             {
               "system": "http://snomed.info/sct",
               "code": "394579002",
               "display": "Internal Medicine"
             }
           ]
         }
       }
     ]
   }

2. PractitionerRole Resource:
   {
     "resourceType": "PractitionerRole",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/PractitionerRole"]
     },
     "active": true,
     "practitioner": {
       "reference": "Practitioner/N10000001",
       "display": "Dr. AHMAD WIJAYA"
     },
     "organization": {
       "reference": "Organization/1000001",
       "display": "RS Harapan Sehat"
     },
     "code": [
       {
         "coding": [
           {
             "system": "http://snomed.info/sct",
             "code": "309343006",
             "display": "Physician"
           }
         ]
       }
     ],
     "specialty": [
       {
         "coding": [
           {
             "system": "http://snomed.info/sct",
             "code": "394579002",
             "display": "Internal Medicine"
           }
         ]
       }
     ],
     "location": [
       {
         "reference": "Location/poli-penyakit-dalam",
         "display": "Poli Penyakit Dalam"
       }
     ],
     "availableTime": [
       {
         "daysOfWeek": ["mon", "tue", "wed", "thu", "fri"],
         "availableStartTime": "08:00:00",
         "availableEndTime": "16:00:00"
       }
     ]
   }

3. Practitioner Types:
   - Dokter Umum (General Practitioner)
   - Dokter Spesialis (Specialist)
   - Dokter Gigi (Dentist)
   - Perawat (Nurse)
   - Bidan (Midwife)
   - Apoteker (Pharmacist)
   - Analis Kesehatan (Medical Laboratory Technologist)
   - Radiografer (Radiographer)
   - Fisioterapis (Physiotherapist)
   - Ahli Gizi (Nutritionist)

4. Credential Management:
   - STR (Surat Tanda Registrasi) tracking
   - SIP (Surat Izin Praktik) validation
   - Expiry monitoring and renewal alerts
   - Competency certifications

Include practitioner synchronization with SATUSEHAT registry
```

### 6.7 Diagnostic Report and Document Reference
```
Implement diagnostic reporting and document management:

1. DiagnosticReport Resource:
   {
     "resourceType": "DiagnosticReport",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/DiagnosticReport"]
     },
     "identifier": [
       {
         "system": "http://sys-ids.kemkes.go.id/diagnostic/{org-id}",
         "value": "LAB-2025-000001"
       }
     ],
     "status": "final",
     "category": [
       {
         "coding": [
           {
             "system": "http://terminology.hl7.org/CodeSystem/v2-0074",
             "code": "LAB",
             "display": "Laboratory"
           }
         ]
       }
     ],
     "code": {
       "coding": [
         {
           "system": "http://loinc.org",
           "code": "58410-2",
           "display": "Complete blood count panel"
         }
       ]
     },
     "subject": {
       "reference": "Patient/100000030009"
     },
     "encounter": {
       "reference": "Encounter/12345"
     },
     "effectiveDateTime": "2025-01-20T10:00:00+07:00",
     "issued": "2025-01-20T14:00:00+07:00",
     "performer": [
       {
         "reference": "Organization/lab-department"
       }
     ],
     "resultsInterpreter": [
       {
         "reference": "Practitioner/pathologist-id"
       }
     ],
     "result": [
       {
         "reference": "Observation/hemoglobin-result"
       },
       {
         "reference": "Observation/wbc-result"
       },
       {
         "reference": "Observation/platelet-result"
       }
     ],
     "conclusion": "Normal complete blood count",
     "presentedForm": [
       {
         "contentType": "application/pdf",
         "data": "base64-encoded-pdf-content",
         "title": "CBC Report"
       }
     ]
   }

2. DocumentReference Resource:
   {
     "resourceType": "DocumentReference",
     "meta": {
       "profile": ["https://fhir.kemkes.go.id/r4/StructureDefinition/DocumentReference"]
     },
     "status": "current",
     "type": {
       "coding": [
         {
           "system": "http://loinc.org",
           "code": "34133-9",
           "display": "Summarization of episode note"
         }
       ]
     },
     "category": [
       {
         "coding": [
           {
             "system": "http://loinc.org",
             "code": "11506-3",
             "display": "Progress note"
           }
         ]
       }
     ],
     "subject": {
       "reference": "Patient/100000030009"
     },
     "date": "2025-01-20T15:00:00+07:00",
     "author": [
       {
         "reference": "Practitioner/N10000001"
       }
     ],
     "authenticator": {
       "reference": "Practitioner/N10000001"
     },
     "content": [
       {
         "attachment": {
           "contentType": "application/pdf",
           "data": "base64-encoded-content",
           "title": "Discharge Summary",
           "creation": "2025-01-20T15:00:00+07:00"
         },
         "format": {
           "system": "http://terminology.hl7.org/CodeSystem/v3-HL7DocumentFormatCodes",
           "code": "urn:ihe:iti:xds:2017:mimeTypeSufficient",
           "display": "mimeType Sufficient"
         }
       }
     ],
     "context": {
       "encounter": [
         {
           "reference": "Encounter/12345"
         }
       ],
       "period": {
         "start": "2025-01-20T08:00:00+07:00",
         "end": "2025-01-20T15:00:00+07:00"
       }
     }
   }

3. Document Types:
   - Discharge summaries
   - Progress notes
   - Consultation reports
   - Radiology reports
   - Pathology reports
   - Operative reports
   - Consent forms
   - Referral letters

4. Document Management:
   - Version control for documents
   - Digital signature integration
   - Access control and audit trail
   - Document retention policies

Include document upload and retrieval workflows
```

### 6.8 Monitoring and Reconciliation
```
Build comprehensive SATUSEHAT monitoring system:

1. Submission Tracking:
   - Track all resource submissions
   - Monitor success/failure rates
   - Response time tracking
   - Queue depth monitoring
   - Retry attempt tracking

2. Data Reconciliation:
   a. Daily Reconciliation:
      - Compare local records with SATUSEHAT
      - Identify missing submissions
      - Validate resource references
      - Check data completeness

   b. Weekly Reports:
      - Submission statistics by resource type
      - Error analysis and patterns
      - Performance metrics
      - Compliance percentage

3. Error Management:
   - Error categorization:
     * Network errors (retry)
     * Validation errors (fix and resubmit)
     * Business rule violations (review)
     * Rate limit errors (queue)

   - Error Resolution Workflow:
     * Automatic retry for transient errors
     * Queue for manual review
     * Notification to responsible team
     * Error correction tracking

4. Performance Monitoring:
   - API response times
   - Throughput (resources/minute)
   - Success rate by endpoint
   - Peak load handling
   - Resource utilization

5. Compliance Dashboard:
   - Required fields completion rate
   - Timeliness of submissions
   - Data quality metrics
   - Regulatory compliance status

6. Alerting System:
   - High error rate alerts
   - Authentication failure alerts
   - Queue overflow warnings
   - Performance degradation notices
   - Compliance violation alerts

7. Audit Trail:
   - All API calls logged
   - Request/response pairs stored
   - User actions tracked
   - Data modifications history
   - Access logs maintained

8. Reporting Tools:
   - Executive dashboards
   - Technical metrics
   - Compliance reports
   - Error analysis reports
   - Trend analysis

Include automated reconciliation jobs and manual correction tools
```

### 6.9 Implementation Best Practices
```
SATUSEHAT integration best practices and guidelines:

1. Development Strategy:
   - Start with sandbox environment
   - Test with synthetic data
   - Gradual rollout by department
   - Parallel run with existing systems
   - Phased resource implementation

2. Data Quality Assurance:
   - Validate before submission
   - Implement data cleansing
   - Standardize code mappings
   - Regular data audits
   - Continuous improvement

3. Security Considerations:
   - Secure token storage
   - Encrypted communications
   - Access control implementation
   - Audit logging
   - Regular security reviews

4. Performance Optimization:
   - Implement caching strategies
   - Use batch submissions
   - Queue management
   - Connection pooling
   - Load balancing

5. Error Handling Strategy:
   - Graceful degradation
   - Fallback mechanisms
   - Manual override options
   - Error recovery procedures
   - Business continuity planning

6. Training and Documentation:
   - Staff training programs
   - User manuals
   - API documentation
   - Troubleshooting guides
   - Best practices documentation

7. Change Management:
   - Version control
   - Release management
   - Rollback procedures
   - Change notification
   - Impact assessment

8. Compliance Monitoring:
   - Regular compliance checks
   - Policy updates tracking
   - Regulatory changes monitoring
   - Audit preparation
   - Certification maintenance

Include implementation timeline and milestone tracking
```

---

## Phase 7: Billing Module

### 7.1 Billing Structure
```
Create comprehensive billing system:
- Service tariff master data
- Room charges calculation
- Doctor fees structure
- Procedure costs
- Medicine charges from pharmacy
- Laboratory test fees
- Radiology examination costs
- Package deals configuration
Support multiple payment types (cash, insurance, company)
```

### 7.2 Invoice Generation
```
Implement invoice generation with:
- Automatic charge compilation from all departments
- Discount application (percentage or fixed amount)
- Tax calculation (PPN if applicable)
- Deposit deduction
- Payment terms configuration
- Invoice numbering system
- PDF invoice generation with hospital letterhead
- Invoice void and correction handling
Include detailed breakdown per category
```

### 7.3 Payment Processing
```
Build payment processing system:
- Multiple payment methods (cash, card, transfer, QRIS)
- Partial payment handling
- Payment receipt generation
- Cash register integration
- Daily cashier reports
- Payment cancellation and refund
- Outstanding balance tracking
Include integration points for payment gateways
```

### 7.4 Insurance Claim Management
```
Create insurance claim management for non-BPJS:
- Insurance company master data
- Coverage verification
- Claim form generation
- Supporting document compilation
- Claim status tracking
- Coordination of Benefits (COB)
- Claim rejection handling
Generate reports for insurance companies
```

---

## Phase 8: Pharmacy Module

### 8.1 Drug Master Data
```
Implement drug master data management:
- Drug catalog with generic and brand names
- Drug categories and classifications
- Unit configurations (tablet, bottle, ampule)
- Storage requirements
- Drug interactions database
- Formularium status (BPJS approved drugs)
- Minimum and maximum stock levels
- Supplier information
Include barcode support for drugs
```

### 8.2 Prescription Processing
```
Create prescription (e-Prescribing) system:
- Prescription entry by doctors
- Dosage and frequency validation
- Drug interaction checking
- Generic substitution suggestion
- Prescription verification by pharmacist
- Label printing for dispensing
- Prescription history per patient
- Controlled drug handling (narcotics, psychotropics)
Include MIMS or ISO drug database integration
```

### 8.3 Pharmacy Inventory
```
Build pharmacy inventory management:
- Stock receiving and inspection
- Stock movement tracking
- Expiry date monitoring and alerts
- Batch/lot number tracking
- Stock taking and adjustment
- Automatic reorder point calculation
- Transfer between pharmacy locations
- Stock card generation
Include FIFO/FEFO implementation
```

### 8.4 Dispensing Management
```
Implement drug dispensing system:
- Prescription queue management
- Barcode scanning for verification
- Dispensing confirmation
- Patient counseling documentation
- Return and exchange handling
- Unit dose dispensing support
- OTC drug sales
- Dispensing report per shift
Generate drug labels with instructions
```

---

## Phase 9: Laboratory Module

### 9.1 Laboratory Test Master
```
Create laboratory test catalog:
- Test categories (Hematology, Chemistry, Microbiology, etc.)
- Test parameters and normal ranges
- Sample types required
- Sample volume needed
- Processing time (TAT)
- Test costs
- Panel/package configurations
- Critical value definitions
Include age and gender-specific normal ranges
```

### 9.2 Lab Order Management
```
Implement laboratory order system:
- Electronic lab orders from clinical modules
- Order priority (routine, urgent, cito)
- Sample collection scheduling
- Barcode generation for samples
- Sample tracking workflow
- Order cancellation handling
- Recurring order support
- Order status notifications
Include pre-analytical validations
```

### 9.3 Result Entry and Validation
```
Build result entry system:
- Manual result entry interface
- LIS machine interface preparation
- Result validation by technician
- Pathologist verification for critical results
- Delta check implementation
- Panic value alerts
- Result amendment handling
- Historical result comparison
Include automatic flag for abnormal values
```

### 9.4 Lab Reporting
```
Create laboratory reporting:
- Result PDF generation with letterhead
- Cumulative reports for inpatients
- Graphical trend analysis
- Critical value communication log
- TAT monitoring reports
- Test utilization statistics
- Quality control reports
- Integration with clinical modules
Support multiple report formats
```

---

## Phase 10: Radiology Module

### 10.1 Radiology Examination Master
```
Set up radiology examination catalog:
- Modalities (X-Ray, CT, MRI, USG)
- Examination types per modality
- Preparation instructions
- Contrast media requirements
- Examination costs
- Reporting templates
- Average reporting time
- Room and equipment mapping
Include CPT codes for procedures
```

### 10.2 Radiology Order Workflow
```
Implement radiology ordering system:
- Electronic ordering from clinical modules
- Schedule slot management
- Patient preparation checklist
- Contrast allergy checking
- Pregnancy status verification
- Order prioritization
- Transportation coordination for inpatients
- Order status tracking
Include modality worklist preparation
```

### 10.3 PACS Integration
```
Create PACS/DICOM integration:
- DICOM worklist provider
- Study status updates
- Image viewer integration
- DICOM tag mapping
- Study archival rules
- Image sharing via links
- CD burning preparation
- Cloud PACS preparation
Include basic DICOM viewer embedding
```

### 10.4 Radiology Reporting
```
Build radiology reporting system:
- Structured reporting templates
- Voice-to-text integration preparation
- Report verification workflow
- Critical findings communication
- Report amendments
- Previous study comparison
- Report distribution
- Statistical reports
Generate reports in multiple formats (PDF, DICOM SR)
```

---

## Phase 11: Workforce Management Module

### 11.1 Employee Master Data
```
Create comprehensive employee management system for Indonesian hospital:
- Employee data with NIK, NPWP, BPJS Ketenagakerjaan
- Professional licenses (STR, SIP) with expiry tracking
- Education and training records
- Department and position hierarchy
- Employment status (permanent, contract, outsource)
- Bank account for payroll
- Family data for benefits
- Emergency contacts
- Document storage (KTP, ijazah, certificates)
Include validation for Indonesian professional requirements
```

### 11.2 Attendance and Scheduling
```
Implement workforce scheduling and attendance:
- Shift patterns (pagi, siang, malam, libur)
- Duty roster management per department
- Fingerprint/face recognition integration
- Overtime calculation
- Leave management (cuti tahunan, sakit, melahirkan)
- Public holiday configuration
- On-call scheduling for doctors
- Substitute/replacement management
- Attendance reports and summaries
Include Indonesian labor law compliance (UU Ketenagakerjaan)
```

### 11.3 Payroll Integration
```
Build payroll calculation system:
- Basic salary and allowances
- Shift differentials (tunjangan shift)
- Overtime calculation (1.5x, 2x rates)
- BPJS Kesehatan deduction (4% employee, 1% family)
- BPJS Ketenagakerjaan deduction
- PPh 21 tax calculation
- THR (holiday allowance) calculation
- Performance incentives
- Loan and advance deductions
Generate salary slips and bank transfer files
```

### 11.4 Performance Management
```
Create performance evaluation system:
- KPI configuration per position
- Periodic evaluation (monthly, quarterly, yearly)
- 360-degree feedback
- Competency assessment
- Training needs analysis
- Career development planning
- Credential tracking for healthcare workers
- Incident reporting linked to employee
- Reward and punishment records
Include Indonesian healthcare worker competency standards
```

### 11.5 Medical Staff Credentialing
```
Implement medical staff credentialing and privileging:
- Doctor credentials verification
- Clinical privileges assignment
- Peer review process
- Morbidity/mortality linked to physician
- Continuing medical education (CME) points
- Medical committee workflows
- Renewal reminders for STR/SIP
- Performance dashboards per physician
- Surgery success rates tracking
Support Indonesian Medical Council (KKI) requirements
```

---

## Phase 12: Surgery/Operating Theater Module

### 12.1 Operating Room Master Data
```
Set up operating theater management system:
- OR room configuration and equipment
- Surgery type catalog with ICD-9-CM codes
- Surgeon and anesthesiologist database
- Surgery team role definitions
- Equipment and instrument sets
- Sterile supply tracking
- OR time slots and block scheduling
- Emergency OR allocation
- Surgery fee structure
Include integration with sterilization unit (CSSD)
```

### 12.2 Surgery Scheduling
```
Implement surgery scheduling system:
- Elective surgery booking
- Pre-operative assessment checklist
- Surgery waiting list management
- OR utilization optimization
- Emergency surgery insertion
- Surgery duration estimation
- Team member scheduling
- Equipment availability checking
- Patient preparation timeline
- NPO (fasting) time calculation
Generate surgery schedule boards and notifications
```

### 12.3 Pre-Operative Management
```
Create pre-operative workflow:
- Pre-anesthesia evaluation
- Informed consent management
- Pre-operative lab/radiology orders
- Risk assessment scoring (ASA classification)
- Surgical site marking
- Pre-operative medication orders
- Blood product reservation
- Allergy and medical history review
- Surgery safety checklist (WHO standard)
- Family waiting area management
Include Indonesian surgical consent forms
```

### 12.4 Intra-Operative Documentation
```
Build intra-operative recording system:
- Surgery start/end time tracking
- Anesthesia record (type, drugs, vitals)
- Surgery procedure documentation
- Implant/prosthesis tracking with serial numbers
- Blood loss estimation
- Specimen collection and labeling
- Intra-operative complications
- Surgical count verification
- Team member time tracking
- Equipment usage logging
Generate operation reports and integrate with billing
```

### 12.5 Post-Operative Management
```
Implement post-operative care system:
- Recovery room (RR/PACU) monitoring
- Post-op vital signs tracking
- Pain assessment and management
- Post-op orders and medication
- Surgical site infection surveillance
- Drain output monitoring
- Mobility assessment
- Discharge criteria checklist
- Post-op follow-up scheduling
- Complication tracking and reporting
Link to quality indicators and surgical audits
```

### 12.6 Surgery Analytics
```
Create surgery analytics and reporting:
- OR utilization rates
- On-time start performance
- Surgery cancellation analysis
- Surgeon performance metrics
- Average surgery duration by type
- Surgical site infection rates
- Re-operation rates
- Mortality and morbidity statistics
- Cost analysis per surgery
- Equipment utilization reports
Generate dashboards for OR management
```

---

## Phase 13: Newborn/Perinatology Module

### 13.1 Maternal Registration
```
Create maternal and delivery management:
- Gravida/Para/Abortus tracking (GPA)
- Expected delivery date (EDD) calculation
- Prenatal visit history
- High-risk pregnancy markers
- Previous delivery history
- Blood type and Rh factor
- Maternal medical conditions
- Delivery planning (normal, SC, VBAC)
- Birth partner information
- BPJS maternity coverage verification
Include integration with antenatal care (ANC) records
```

### 13.2 Labor and Delivery
```
Implement labor and delivery documentation:
- Admission for delivery workflow
- Labor progress monitoring (partograph)
- Contraction and fetal heart rate tracking
- Cervical dilation recording
- Membrane rupture time
- Labor stages documentation
- Delivery type and method
- APGAR scoring (1 and 5 minutes)
- Birth weight, length, head circumference
- Delivery complications
Generate birth certificates and reports
```

### 13.3 Newborn Registration
```
Build newborn registration system:
- Automatic registration from delivery
- Temporary ID before NIK/birth certificate
- Link to mother's medical record
- Multiple birth handling (twins, triplets)
- Birth defect screening
- Newborn screening tests
- Immunization schedule initialization
- Feeding type (ASI exclusive, formula)
- Newborn identification (bracelet, footprint)
- Birth notification to civil registry
Support Indonesian birth registration requirements
```

### 13.4 NICU Management
```
Create NICU/PICU management system:
- NICU bed and incubator tracking
- Ventilator management
- Weight and growth charts
- Feeding schedule and volume
- Medication dosing by weight
- Phototherapy management
- Central line tracking
- Parent visitation logging
- Kangaroo mother care documentation
- Transport incubator management
Include neonatal scoring systems (SNAPPE, CRIB)
```

### 13.5 Newborn Clinical Care
```
Implement newborn clinical documentation:
- Daily progress notes
- Vital signs with age-appropriate ranges
- Fluid balance monitoring
- Bilirubin tracking and graphs
- Blood gas results
- Nutrition calculation (TPN, enteral)
- Developmental assessment
- Hearing and vision screening
- Congenital disease screening
- Vaccination administration
Generate growth charts and developmental reports
```

### 13.6 Breastfeeding Support
```
Build breastfeeding management system:
- Breastfeeding initiation tracking (IMD)
- Lactation consultation records
- Breast milk pumping logs
- Donor milk management
- Formula supplementation tracking
- Weight gain monitoring
- Feeding problems documentation
- Rooming-in compliance
- Exclusive breastfeeding rates
- Mother's milk storage for NICU
Support Indonesian exclusive breastfeeding programs
```

### 13.7 Discharge and Follow-up
```
Create newborn discharge workflow:
- Discharge criteria checklist
- Parent education documentation
- Car seat verification
- Home care instructions
- Follow-up appointment scheduling
- Newborn metabolic screening results
- Immunization record card
- Danger signs education
- Birth certificate process status
- Integration with Posyandu schedule
Include KMS (Kartu Menuju Sehat) initialization
```

---

## Phase 14: Integration Hub Module

### 14.1 Medical Device Integration
```
Create medical device integration framework:
- Vital signs monitors (GE, Philips, Mindray)
- Ventilators with parameter capture
- Infusion pumps with rate/volume tracking
- ECG machines with interpretation
- Glucometers with results transfer
- Blood gas analyzers
- Dialysis machines
- Anesthesia machines
- Fetal monitors
- POCT devices
Use HL7, ASTM, or proprietary protocols as needed
```

### 14.2 WhatsApp Business Integration
```
Implement WhatsApp notifications for Indonesia:
- Appointment reminders
- Queue status updates
- Lab result ready notifications
- Medication reminders
- Registration confirmations
- Bill payment reminders
- Health tips broadcasting
- Emergency broadcasts
- Two-way chat for inquiries
- Document sharing (results, receipts)
Use WhatsApp Business API with proper templates
```

### 14.3 Payment Gateway Integration
```
Build payment gateway integrations for Indonesia:
- Bank transfer (Virtual Account)
- QRIS payment standard
- E-wallet integration (GoPay, OVO, Dana, ShopeePay)
- Credit/debit card processing
- Installment plans for expensive treatments
- Corporate billing accounts
- Insurance guarantee letters
- Mixed payment methods
- Payment reconciliation
- Refund processing
Include payment proof upload and verification
```

### 14.4 Third-Party Lab Integration
```
Create external laboratory integration:
- Prodia/Cito/Pramita lab connections
- Order routing to external labs
- Result retrieval and mapping
- Barcode synchronization
- Cost and margin management
- TAT monitoring
- Quality control data exchange
- Reference range mapping
- Critical value notifications
- Billing integration
Support multiple lab vendors simultaneously
```

---

## Phase 15: Security & Authentication

### 11.1 JWT Authentication
```
Implement JWT-based authentication:
- User login with username/password
- JWT token generation with claims
- Token refresh mechanism
- Token blacklist for logout
- Password complexity requirements
- Password reset via email/SMS
- Account lockout after failed attempts
- Remember me functionality
Include role and permission claims in JWT
```

### 11.2 Role-Based Access Control
```
Create comprehensive RBAC system:
- Roles: Admin, Doctor, Nurse, Pharmacist, Cashier, Lab Tech, Radiographer
- Permission matrix per module
- Dynamic menu based on roles
- Department-based access control
- Shift-based access restrictions
- Feature flag implementation
- Delegation mechanism
- Emergency override (break-glass)
Store audit log for all access
```

### 11.3 API Security
```
Implement API security measures:
- Rate limiting per endpoint
- CORS configuration for React frontend
- CSRF protection
- XSS prevention
- SQL injection prevention
- Request validation and sanitization
- API versioning strategy
- Webhook security for integrations
Include API key management for external systems
```

### 11.4 Audit Trail
```
Build comprehensive audit system:
- User action logging
- Data change tracking (before/after)
- Login/logout tracking
- Failed authentication attempts
- Sensitive data access logging
- Report generation with filters
- Log retention policies
- Tamper-proof log storage
Include integration with external SIEM if needed
```

---

## Phase 16: Reporting & Analytics

### 12.1 Operational Reports
```
Create operational reporting system:
- Daily census reports
- Bed occupancy rates (BOR)
- Average length of stay (ALOS)
- Turn over interval (TOI)
- Outpatient visit statistics
- Emergency department metrics
- Surgery utilization reports
- Department performance dashboards
Export to Excel and PDF formats
```

### 12.2 Financial Reports
```
Implement financial reporting:
- Daily revenue reports
- Outstanding receivables
- Insurance claim summaries
- Cash flow reports
- Department-wise income
- Doctor revenue sharing reports
- Discount analysis
- Payment method distribution
Include graphical representations
```

### 12.3 Regulatory Reports
```
Build regulatory reporting for Indonesia:
- RL1-RL5 reports for Ministry of Health
- BPJS monthly reports
- SATUSEHAT compliance reports
- Disease surveillance reports
- Mortality and morbidity statistics
- Hospital indicators (MIRM)
- Quality indicators
- Patient safety reports
Automate submission where possible
```

### 12.4 Business Intelligence
```
Create BI dashboard system:
- Real-time KPI monitoring
- Predictive analytics for bed demand
- Patient flow visualization
- Revenue trend analysis
- Clinical quality metrics
- Patient satisfaction tracking
- Comparative analysis
- Custom dashboard builder
Use PostgreSQL materialized views for performance
```

---

## Phase 17: React Frontend Foundation

### 13.1 React Project Setup
```
Create a React frontend for HMS using Vite 5.x with:
- React 18.3.x with TypeScript 5.x
- Tailwind CSS 4.x with custom medical theme
- React Router v6 with protected routes
- Redux Toolkit 2.x for state management
- TanStack Query (React Query) 5.x for server state
- Axios with interceptors
- React Hook Form 7.x with Zod 3.x validation
- Environment configuration for API URLs
Set up proper folder structure: features, components, hooks, utils, services
```

### 13.2 Authentication Flow UI
```
Implement authentication UI with:
- Login page with hospital branding
- JWT token storage and management
- Auto-refresh token mechanism
- Role-based route protection
- Session timeout warning
- Logout functionality
- Password reset flow
- Remember device option
Include loading states and error handling
```

### 13.3 Dashboard Layout
```
Create main dashboard layout with:
- Responsive sidebar navigation
- Role-based menu items
- User profile dropdown
- Notification bell with real-time updates
- Quick search functionality
- Breadcrumb navigation
- Theme switcher (light/dark)
- Language switcher (Indonesian/English)
Include keyboard shortcuts for common actions
```

### 13.4 Patient Management UI
```
Build patient management interface with:
- Patient registration form with NIK validation
- Patient search with filters
- Patient list with pagination
- Patient detail view with tabs
- Medical history timeline
- Document upload interface
- Patient card/barcode printing
- Quick actions menu
Include form validation and error messages in Indonesian
```

---

## Phase 18: React Advanced Features

### 18.1 Clinical Modules UI
```
Create clinical interfaces with:
- SOAP note entry form
- Diagnosis search with ICD-10 autocomplete
- Prescription writing interface
- Lab result viewer with graphs
- Radiology image viewer
- Vital signs charts
- Medical record templates
- Digital signature placeholder
Include keyboard navigation for faster data entry
```

### 18.2 Queue Management Display
```
Implement queue management system:
- TV display for waiting rooms
- Queue number display per polyclinic
- Audio announcement integration
- Doctor room assignments
- Estimated waiting time
- Mobile queue status for patients
- Queue calling interface for staff
- Real-time updates using WebSocket
Support multiple display configurations
```

### 18.3 PWA Implementation
```
Convert React app to PWA with:
- Service worker for offline capability
- Cache strategies for static assets
- Offline data queue for sync
- Install prompt for mobile/desktop
- Push notifications setup
- Background sync for pending transactions
- App icon and splash screens
- Offline fallback pages
Test on various devices and networks
```

### 18.4 Real-time Features
```
Implement real-time updates using WebSocket/SSE:
- Emergency alerts
- New lab results notifications
- Bed availability updates
- Queue status changes
- Chat between departments
- System announcements
- Critical value alerts
- Live dashboard metrics
Include reconnection logic and error handling
```

---

## Phase 19: Testing Strategy

### 19.1 Backend Unit Tests
```
Write comprehensive unit tests for HMS backend:
- Service layer tests with mocked repositories
- Validation tests for Indonesian requirements (NIK, BPJS)
- Business logic tests for billing calculations
- Integration tests for BPJS/SATUSEHAT services
- Repository tests with @DataJpaTest
- Controller tests with MockMvc
- Test coverage minimum 80%
Include test data factories for Indonesian data
```

### 19.2 API Integration Tests
```
Create API integration test suite:
- Patient registration flow
- BPJS eligibility and SEP creation
- Complete outpatient visit flow
- Billing and payment process
- Laboratory order to result flow
- Prescription to dispensing flow
- User authentication and authorization
- Rate limiting and security tests
Use Testcontainers for database
```

### 19.3 Frontend Testing
```
Implement React testing strategy:
- Component unit tests with React Testing Library
- Hook tests for custom hooks
- Form validation tests
- API mock tests with MSW
- Redux state management tests
- Route protection tests
- Accessibility tests (WCAG compliance)
- Visual regression tests
Include Indonesian locale testing
```

### 19.4 End-to-End Tests
```
Build E2E test suite with Playwright:
- Complete patient registration with NIK
- Outpatient visit from registration to billing
- BPJS claim submission flow
- Emergency admission process
- Prescription and dispensing cycle
- Lab order and result entry
- Multi-role workflows
- Performance testing under load
Run tests on multiple browsers
```

---

## Phase 20: Deployment Preparation

### 20.1 Docker Configuration
```
Create Docker configuration for HMS:
- Multi-stage Dockerfile for Spring Boot
- Optimized Dockerfile for React with Nginx
- docker-compose.yml for full stack
- PostgreSQL with volume persistence
- Redis for caching
- RabbitMQ for messaging
- MinIO for file storage
- Environment-specific configurations
Include health checks and restart policies
```

### 20.2 CI/CD Pipeline
```
Set up CI/CD pipeline using GitHub Actions/GitLab CI:
- Automated testing on push
- Code quality checks with SonarQube
- Security scanning for dependencies
- Build Docker images
- Push to registry (Docker Hub/GitLab Registry)
- Deploy to staging environment
- Automated database migrations
- Rollback procedures
Include notifications for build status
```

### 20.3 Database Migration
```
Prepare production database migration:
- Flyway migration scripts
- Master data seeding (ICD-10, drugs)
- Initial user accounts
- RBAC permissions setup
- Backup procedures
- Migration rollback plans
- Data validation scripts
- Performance indexes
Test migrations on staging environment
```

### 20.4 Monitoring Setup
```
Implement monitoring and logging:
- Application metrics with Micrometer
- Prometheus for metrics collection
- Grafana dashboards for visualization
- ELK stack for centralized logging
- Alert rules for critical issues
- Uptime monitoring
- Database performance monitoring
- API response time tracking
Create runbooks for common issues
```

---

## Phase 21: Production Deployment

### 21.1 Server Preparation
```
Prepare production servers for HMS:
- Ubuntu 24.04 LTS setup (or 22.04 LTS acceptable)
- Docker 27.x and Docker Compose 2.x installation
- Nginx reverse proxy configuration
- SSL certificate setup (Let's Encrypt)
- Firewall rules configuration
- Backup storage mounting
- Log rotation setup
- System monitoring agents
Document server specifications and network topology
```

### 21.2 Production Configuration
```
Configure production environment:
- Production database with replication
- Redis cluster for high availability
- RabbitMQ clustering
- Load balancer configuration
- Session management across instances
- File storage configuration
- Email server settings
- SMS gateway configuration
Create production configuration checklist
```

### 21.3 Security Hardening
```
Implement security hardening for production:
- Remove default accounts
- Configure fail2ban
- Enable audit logging
- Set up WAF rules
- Configure DDoS protection
- Implement backup encryption
- Set up VPN for admin access
- Configure intrusion detection
- Regular security updates schedule
Document security procedures
```

### 21.4 Go-Live Preparation
```
Prepare for HMS go-live:
- User training materials in Indonesian
- Data migration from legacy system
- User acceptance testing (UAT)
- Performance testing with expected load
- Disaster recovery testing
- Rollback plan documentation
- Go-live checklist
- Support team preparation
Create go-live runbook with timelines
```

---

## Phase 22: Post-Production

### 22.1 Performance Optimization
```
Optimize HMS performance in production:
- Query optimization based on slow query logs
- Database index tuning
- API response caching strategy
- Frontend bundle optimization
- Image optimization and CDN
- Database connection pool tuning
- Memory usage optimization
- Background job optimization
Monitor and document improvements
```

### 22.2 Backup and Recovery
```
Implement comprehensive backup strategy:
- Automated daily database backups
- Point-in-time recovery setup
- File storage backup
- Configuration backup
- Backup testing procedures
- Off-site backup storage
- Recovery time objective (RTO) documentation
- Recovery point objective (RPO) compliance
Create and test disaster recovery procedures
```

### 22.3 Maintenance Procedures
```
Establish maintenance procedures:
- Zero-downtime deployment strategy
- Database maintenance windows
- Certificate renewal automation
- Log cleanup procedures
- Archive old data strategy
- Performance baseline updates
- Security patch management
- Vendor library updates
Document standard operating procedures (SOP)
```

### 22.4 Support and Documentation
```
Create comprehensive documentation:
- System architecture documentation
- API documentation with Swagger/OpenAPI
- User manuals in Indonesian
- Administrator guide
- Troubleshooting guide
- FAQ documentation
- Video tutorials for common tasks
- Knowledge base setup
Establish support ticket system
```

---

## Phase 23: Advanced Features

### 23.1 Mobile Application
```
Develop mobile app for HMS:
- React Native setup with existing React components
- Patient app for queue status and results
- Doctor app for rounds and emergency calls
- Push notifications for critical alerts
- Offline capability with sync
- Biometric authentication
- Camera integration for document capture
- Native performance optimization
Deploy to Google Play Store
```

### 23.2 Telemedicine Integration
```
Add telemedicine capabilities:
- Video consultation scheduling
- WebRTC video calling implementation
- Screen sharing for report viewing
- Chat functionality
- Prescription during teleconsultation
- Payment integration for telemedicine
- Recording with consent
- Integration with existing encounters
Include bandwidth optimization
```

### 23.3 AI/ML Features
```
Implement AI-powered features:
- Diagnosis suggestion based on symptoms
- Drug interaction prediction
- Readmission risk scoring
- Appointment no-show prediction
- Revenue cycle optimization
- Clinical decision support
- Automated coding assistance
- Anomaly detection in billing
Start with simple models and iterate
```

### 23.4 Analytics and BI
```
Enhance analytics capabilities:
- Real-time analytics dashboard
- Predictive analytics for resource planning
- Clinical pathway analysis
- Patient flow optimization
- Cost analysis per procedure
- Physician performance metrics
- Quality measure tracking
- Custom report builder for management
Use Apache Superset or similar for visualization
```

---

## Phase 24: Compliance and Quality

### 24.1 KARS Accreditation
```
Prepare HMS for KARS accreditation requirements:
- Patient safety indicators tracking
- Clinical pathway compliance
- Infection control monitoring
- Medication error reporting
- Patient satisfaction surveys
- Clinical audit tools
- Incident reporting system
- Quality improvement tracking
Generate required KARS reports automatically
```

### 24.2 ISO Certification
```
Implement ISO requirements:
- Document control system
- Change management procedures
- Risk assessment matrices
- Business continuity planning
- Corrective and preventive actions (CAPA)
- Internal audit scheduling
- Management review preparation
- Continual improvement tracking
Maintain ISO compliance documentation
```

### 24.3 Data Privacy Compliance
```
Ensure data privacy compliance:
- Personal data inventory
- Consent management system
- Data retention policies
- Right to erasure implementation
- Data portability features
- Privacy impact assessments
- Breach notification procedures
- Privacy policy management
Include compliance with Indonesian regulations
```

### 24.4 Clinical Governance
```
Implement clinical governance features:
- Clinical guidelines integration
- Protocol compliance monitoring
- Mortality and morbidity reviews
- Clinical audit cycles
- Peer review systems
- Continuing medical education tracking
- Credentialing management
- Clinical risk management
Support evidence-based medicine practices
```

---

## Phase 25: Critical Care & Support Services

### 25.1 ICU/Critical Care Module
```
Create comprehensive ICU management system:
- ICU bed assignment and tracking
- Ventilator management (mode, settings, weaning protocols)
- Hemodynamic monitoring (arterial lines, Swan-Ganz catheter)
- Sedation assessment (RASS, Ramsay scale)
- APACHE II/SOFA/SAPS scoring
- Hourly fluid balance chart
- Central line tracking with CLABSI prevention
- Daily goals worksheet
- Multidisciplinary rounds documentation
- ICU-specific medication (inotropes, sedatives, paralytics)
- Daily ICU checklist (ventilator bundle, DVT prophylaxis)
- Prone positioning protocol
- Family visitation logging
- Bed-side procedure tracking
- Critical care transfer criteria
- ICU quality indicators
Include integration with ventilator devices via HL7
```

### 25.2 Hemodialysis Unit Management
```
Implement comprehensive hemodialysis module:
- Patient dialysis schedule (2x/3x weekly patterns)
- Machine assignment and tracking
- Dialyzer reuse tracking (if applicable)
- Pre-dialysis assessment (BP, weight, vascular access check)
- Dialysis prescription (time, flow rate, dialysate, heparin)
- Intra-dialysis monitoring (15-minute vital signs)
- Post-dialysis assessment
- Dry weight management and adjustment
- Ultrafiltration goal calculation
- Vascular access documentation (AV fistula, graft, catheter)
- Access complications tracking
- Dialysis adequacy calculation (Kt/V, URR)
- Monthly lab monitoring (anemia, mineral bone disease)
- BPJS chronic disease program (Prolanis) integration
- Water treatment system monitoring
- Machine maintenance scheduling
- Hepatitis B/C surveillance
- Erythropoietin and iron management
Include alert system for intradialytic hypotension and other complications
```

### 25.3 Blood Bank & Transfusion Service
```
Build comprehensive blood bank management:
- Blood inventory by type and component (WB, PRC, FFP, TC, Cryo)
- Donor registration and screening
- Donation session management
- Blood collection documentation
- Blood typing (ABO, Rh) and antibody screening
- Cross-matching procedures
- Blood component preparation
- Blood request from clinical units
- Compatibility testing workflow
- Blood issue and dispensing
- Transfusion reaction monitoring (acute, delayed)
- Temperature monitoring and cold chain
- Expiry date tracking and FEFO
- Emergency blood release protocol (O-negative, uncrossmatched)
- Massive transfusion protocol
- Autologous donation program
- Directed donation management
- Blood wastage tracking and analysis
- Transfusion consent form management
- Infectious disease testing (HIV, HBV, HCV, Syphilis)
- Quality control for reagents and equipment
- Integration with laboratory for testing
Generate daily blood inventory reports and usage statistics
```

### 25.4 CSSD (Central Sterile Supply Department)
```
Create detailed CSSD management system:
- Instrument receiving from OR and departments
- Decontamination process tracking
- Instrument cleaning verification
- Instrument set assembly
- Packaging with sterilization indicators
- Sterilization method selection (steam, ETO, hydrogen peroxide)
- Autoclave cycle recording (temperature, pressure, time)
- Biological indicator testing and results
- Sterilization load documentation
- Sterile storage management
- Instrument expiry date tracking
- Instrument set dispensing to OR
- Instrument tracking (from OR to CSSD and back)
- Equipment maintenance (autoclave, washer-disinfector)
- Validation and calibration scheduling
- Implant tracking with lot numbers
- Flash sterilization documentation
- CSSD quality indicators
- Staff competency tracking
- Consumable inventory (indicator tape, pouches)
Include integration with OR scheduling for instrument planning
```

---

## Phase 26: Medical Records & Quality Management

### 26.1 Medical Records Department (MRMK)
```
Implement Medical Records Department system:
- Medical record numbering system (family folder vs unit)
- Record filing and retrieval workflow
- Record request management
- Record tracking (who has the record)
- Incomplete record tracking
- Deficiency analysis (missing signatures, incomplete forms)
- Physician notification for incomplete charts
- Record completion deadline monitoring
- Document scanning and indexing
- Digital document management
- Medical record retention policy enforcement
- Record destruction scheduling and documentation
- Release of information (ROI) process
- Patient authorization for record release
- Legal copy generation with certification
- Subpoena compliance
- Statistics generation for RL reports
- Coding quality assurance review
- Concurrent vs retrospective review
- Medical record committee reporting
- Record completeness audits
- Storage optimization (active vs inactive records)
Include barcode/RFID tracking for physical records
```

### 26.2 Infection Prevention & Control (PPIRS)
```
Create comprehensive infection control system:
- Healthcare-associated infection (HAI) surveillance
- Device-associated infection tracking:
  - Central line-associated BSI (CLABSI)
  - Catheter-associated UTI (CAUTI)
  - Ventilator-associated pneumonia (VAP)
  - Surgical site infection (SSI)
- Hand hygiene compliance monitoring
- Direct observation forms
- Alcohol-based hand rub consumption tracking
- Isolation precautions management:
  - Standard precautions
  - Contact precautions (MRSA, VRE, C.diff)
  - Droplet precautions (influenza, COVID-19)
  - Airborne precautions (TB, measles)
- Isolation room assignment
- PPE usage tracking
- Outbreak investigation and management
- Line listing for outbreak cases
- Antibiotic stewardship program:
  - Antibiotic consumption monitoring
  - Restricted antibiotic approval workflow
  - Culture and sensitivity tracking
  - Antibiotic appropriateness review
- Environmental surveillance (air, water, surface)
- Disinfection and sterilization monitoring
- Employee health tracking (vaccinations, exposures)
- Needle stick injury reporting
- Post-exposure prophylaxis management
- Infection control education tracking
- KARS infection prevention indicators
- Bundle compliance (VAP bundle, CLABSI bundle, SSI bundle)
- Multidrug-resistant organism (MDRO) surveillance
Generate monthly infection control reports and dashboards
```

### 26.3 Enhanced Incident Reporting System
```
Build detailed patient safety incident reporting:
- Incident type classification:
  - Medication errors
  - Falls
  - Pressure injuries
  - Wrong patient/site/procedure
  - Healthcare-associated infections
  - Equipment failures
  - Communication failures
- Severity rating (no harm, minor, moderate, severe, death)
- Near-miss reporting
- Anonymous reporting option
- Online incident reporting form
- Photo upload for incident documentation
- Witness statements
- Immediate action taken documentation
- Incident investigation workflow
- Root cause analysis (RCA) module:
  - Fishbone diagram creation
  - 5 Whys analysis
  - Timeline reconstruction
  - Contributing factors identification
- Failure mode and effect analysis (FMEA)
- Corrective and preventive action (CAPA) tracking
- Action plan assignment and monitoring
- Deadline tracking and notifications
- Verification of effectiveness
- Incident trending and analysis
- Pareto charts for incident types
- Run charts for monitoring over time
- Benchmark comparison
- Safety culture survey integration
- Patient safety committee reporting
- Regulatory reporting (to Ministry of Health if required)
- Learning from incidents (safety alerts)
- Integration with KARS patient safety standards
Include dashboard for hospital leadership with key safety metrics
```

### 26.4 Clinical Pathway Management
```
Implement clinical pathway system:
- Clinical pathway configuration tool
- Condition/procedure-based pathway selection
- Time-based pathway activities
- Variance tracking and documentation
- Variance types (patient, clinician, system)
- Compliance monitoring per pathway
- Outcome measurement
- Length of stay comparison (actual vs expected)
- Cost analysis per pathway
- Pathway effectiveness analysis
- Multidisciplinary team documentation
- Daily pathway progression checklist
- Critical pathway deviations alerts
- Patient education materials per pathway
- Discharge planning integration
- Clinical pathway library management
- Evidence-based guideline linking
- Pathway revision and version control
- Approval workflow for new pathways
- Clinical pathway compliance reports
Generate pathway performance dashboards for quality improvement
```

### 26.5 Medication Reconciliation
```
Create comprehensive medication reconciliation module:
- Admission medication history (BPMH - Best Possible Medication History)
- Home medication list capture
- OTC and herbal supplement documentation
- Medication allergy verification
- Admission orders comparison
- Discrepancy identification (intentional vs unintentional)
- Prescriber notification of discrepancies
- Discrepancy resolution documentation
- Transfer medication reconciliation (unit to unit, facility to facility)
- Discharge medication reconciliation
- Changes to home medications documentation
- Patient counseling on medication changes
- Discharge prescription generation
- Follow-up medication plan
- High-risk medication flagging (anticoagulants, insulin, opioids)
- Medication reconciliation at all transition points
- Pharmacist review integration
- Medication reconciliation compliance tracking
- Incomplete reconciliation alerts
- Integration with e-prescribing system
- Medication list printing for patient
Include patient portal access to current medication list
```

---

## Phase 27: Indonesian Regulatory Compliance

### 27.1 Digital Signature Integration
```
Implement comprehensive digital signature solution for Indonesia:
- Integration with certified providers (Privy, Digisign, VIDA, or PSrE)
- Certificate authority (CA) integration
- User registration and certificate enrollment
- Digital certificate management
- Certificate renewal notifications
- Certificate revocation handling
- Document signing workflow:
  - Medical records signing by doctors
  - Laboratory results signing by pathologists
  - Radiology reports signing by radiologists
  - Prescription signing
  - Consent form signing
  - Death certificate signing
  - Hospital document signing (referral letters, medical summaries)
- Signature verification process
- Timestamping for legal validity
- Signature audit trail
- Batch signing capability
- Mobile signing support
- Biometric authentication option
- Signature appearance customization
- PDF signing with visible signature
- Multi-party signing workflow (countersignature)
- Signing ceremony logging
- Legal compliance with UU ITE No. 11/2008 and updates
- Regulatory compliance with Permenkes regarding electronic signatures
- Integration with E-Rekam Medis requirements
- Archive signed documents with long-term validation
Generate reports on signing activity and certificate status
```

### 27.2 E-Rekam Medis (Electronic Medical Records) Compliance
```
Ensure full compliance with Permenkes No. 24/2022 on E-RME:
- Authentication and authorization matrix
- Multi-factor authentication for sensitive access
- Biometric authentication option
- Session timeout configuration
- Role-based access control with audit
- Data encryption at rest (AES-256)
- Data encryption in transit (TLS 1.3)
- Database field-level encryption for sensitive data
- Secure key management
- Data masking for unauthorized users
- Audit trail requirements:
  - User access logging
  - Data view/read logging
  - Data modification tracking (before/after values)
  - Delete operation logging
  - Export/print logging
  - Unsuccessful access attempts
  - Tamper-proof audit logs
- Retention policy implementation (minimum 5 years after last visit)
- Archival process for old records
- Legal hold capability
- Data backup requirements (daily, weekly, monthly)
- Backup verification procedures
- Disaster recovery plan
- Recovery time objective (RTO) definition
- Recovery point objective (RPO) compliance
- Business continuity procedures
- Patient consent management
- Consent for treatment
- Consent for data sharing
- Consent withdrawal process
- Privacy policy enforcement
- Data breach notification procedures
- Incident response plan
- Compliance reporting to regulatory bodies
Include documentation of security controls for accreditation
```

### 27.3 SIRS (Sistem Informasi Rumah Sakit) Integration
```
Implement Ministry of Health SIRS integration:
- Hospital profile data submission:
  - Basic hospital information
  - Ownership and type
  - Accreditation status
  - License information
- Facility data reporting:
  - Bed capacity by class
  - Bed availability real-time
  - Specialized units (ICU, NICU, hemodialysis)
  - Operating rooms
  - Emergency capacity
- Service capability reporting:
  - Available services and specialties
  - 24/7 services
  - Referral capability
  - Teaching hospital status
- Human resource data:
  - Medical staff by specialty
  - Nursing staff by education level
  - Allied health professionals
  - Administrative staff
  - Licensure and certification tracking
- Equipment inventory submission:
  - Medical equipment by category
  - Imaging equipment (CT, MRI, X-ray)
  - Laboratory equipment
  - Maintenance status
- Utilization data reporting:
  - Bed occupancy rate (BOR)
  - Average length of stay (ALOS)
  - Turn over interval (TOI)
  - Bed turn over rate (BTO)
  - Outpatient visits
  - Emergency visits
  - Surgical procedures count
- Financial summary reporting:
  - Revenue by source
  - BPJS vs non-BPJS ratio
  - Operating costs
- Quality indicator submission:
  - Patient safety indicators
  - Clinical quality measures
  - Infection rates
- Automated data extraction from HMS
- Scheduled submission (monthly, quarterly, annually)
- Manual override and correction capability
- Submission confirmation tracking
- Error handling and resubmission
Generate SIRS compliance reports for hospital management
```

### 27.4 DUKCAPIL NIK Verification Integration
```
Implement real-time NIK verification with DUKCAPIL:
- API integration with Direktorat Jenderal Kependudukan dan Pencatatan Sipil
- Real-time NIK validation during patient registration
- NIK format validation (16 digits, checksum)
- NIK data retrieval:
  - Full name (as per KTP)
  - Date of birth
  - Gender
  - Address
  - Family card number (KK)
- Photo verification (if available via API)
- Duplicate NIK checking across system
- Manual verification override (for offline scenarios)
- Verification status tracking
- Failed verification logging
- Queue system for batch verification
- Periodic re-verification of existing records
- Data synchronization with local database
- Privacy compliance for retrieved data
- Caching strategy for offline capability
- Error handling for API timeouts
- Fallback procedures when service unavailable
- Integration with patient registration workflow
- Notification to staff for verification failures
- Reporting on verification success rates
Include training materials for staff on handling verification issues
```

### 27.5 SISRUTE (Sistem Rujukan Terintegrasi) Implementation
```
Build comprehensive referral system integration:
- Referral letter creation module:
  - Patient demographics
  - Diagnosis (ICD-10)
  - Procedures performed
  - Current condition
  - Reason for referral
  - Recommended facility
  - Urgency level
- SISRUTE API integration for:
  - Facility search (by location, specialty, capability)
  - Bed availability checking at target facility
  - Referral submission
  - Referral acceptance/rejection workflow
  - Referral tracking number
- Accepting referral workflow:
  - Inbound referral notification
  - Referral review by admitting doctor
  - Acceptance or rejection with reason
  - Bed assignment for accepted referral
  - Patient arrival notification
- Back-referral management:
  - Discharge summary for referring facility
  - Follow-up recommendations
  - Back-referral letter generation
  - Continuity of care documentation
- Emergency referral fast-track
- Ambulance coordination
- Patient transfer checklist
- Inter-facility communication log
- Referral outcome tracking
- Referral pattern analysis
- Integration with BPJS for rujukan verification
- Compliance with Permenkes on referral system
- Mobile notification for urgent referrals
Generate referral statistics and network analysis reports
```

### 27.6 Aplicares Integration & Reporting
```
Implement BPJS Aplicares quality monitoring system:
- Clinical indicator data collection:
  - Waktu tanggap pelayanan dokter di IGD
  - Pemberian antibiotik profilaksis
  - Penggunaan formularium nasional
  - Kejadian infeksi pasca operasi
  - Kejadian infeksi nosokomial
  - Tidak adanya kejadian pasien jatuh
  - Kematian pasien > 48 jam
  - Kepuasan pasien
  - Kecepatan waktu respon komplain
  - Tidak adanya kejadian pulang paksa
- Patient safety indicator reporting:
  - Medication errors
  - Hospital-acquired infections
  - Surgical complications
  - Patient falls
  - Pressure ulcers
- Medication safety reporting:
  - Antibiotic stewardship metrics
  - Formulary compliance
  - High-alert medication safety
- Surgical checklist compliance:
  - WHO surgical safety checklist completion
  - Timeout verification
  - Counts verification
- Data validation before submission
- Automated data aggregation from HMS
- Manual data entry for non-automated indicators
- Submission scheduling (monthly)
- Benchmark comparison with national data
- Trend analysis and dashboards
- Quality improvement action plans based on indicators
- Integration with hospital quality committee
- Regulatory compliance tracking
Generate Aplicares performance reports for management review
```

### 27.7 P-Care Integration (Primary Care BPJS)
```
Implement P-Care integration for primary care referrals:
- P-Care API authentication
- Patient eligibility checking via P-Care
- Referral data retrieval from Puskesmas/Klinik
- Inbound referral information:
  - Primary care facility details
  - Referring physician
  - Diagnosis from primary care
  - Previous treatments
  - Referral validity period
- Diagnosis and treatment documentation
- Referral response submission to P-Care:
  - Acceptance confirmation
  - Specialist consultation results
  - Treatment provided
  - Recommendations
- Back-referral to primary care
- Follow-up care coordination
- Chronic disease management (Prolanis) integration:
  - Diabetes management
  - Hypertension management
  - Program enrollment
  - Regular monitoring data
- Preventive care tracking
- Laboratory result sharing with primary care
- Medication continuity from primary to secondary care
- Integration with BPJS claim system
- P-Care transaction logging
Generate P-Care utilization and outcome reports
```

---

## Phase 28: Financial & Procurement System

### 28.1 General Ledger & Chart of Accounts
```
Implement comprehensive financial accounting system:
- Chart of accounts configuration (Indonesian standard)
- Account hierarchy (Assets, Liabilities, Equity, Revenue, Expenses)
- Multi-level account structure
- Cost center configuration (departments, units)
- Journal entry module:
  - Manual journal entries
  - Automated entries from modules (billing, payroll, procurement)
  - Recurring journal entries
  - Adjusting entries
  - Closing entries
- Posting and unposting functionality
- Period closing process (month-end, year-end)
- General ledger inquiry and reporting
- Trial balance generation
- Account reconciliation
- Bank reconciliation module
- Cash book management
- Petty cash management
- Inter-departmental transfers
- Budget vs actual comparison
- Variance analysis
- Financial dimension tracking (project, grant, department)
- Multi-currency support (if needed)
- Fiscal year configuration
- Audit trail for all transactions
- Approval workflow for journal entries
- Integration with all revenue and expense modules
Include compliance with Indonesian accounting standards (PSAK)
```

### 28.2 Accounts Receivable Management
```
Create comprehensive AR system:
- Customer master data (patients, insurance companies, corporations)
- Invoice generation from billing module
- Payment application:
  - Full payment
  - Partial payment
  - Payment allocation to multiple invoices
  - Prepayment and deposit handling
- Payment methods (cash, card, transfer, check)
- Credit memo and debit memo
- Write-off management for bad debts
- Aging analysis (30, 60, 90, 120+ days)
- Collection management:
  - Collection workflow by aging bucket
  - Collection call logging
  - Payment promise tracking
  - Collection agency assignment
- Insurance claim AR:
  - Claim submission tracking
  - Claim status monitoring
  - Claim payment application
  - Claim rejection and appeal
  - Underpayment/overpayment handling
- Corporate billing for MCU and employees
- Installment plan management
- Guarantee letter tracking (for insurance)
- Revenue recognition
- AR reporting (aging, collection efficiency, DSO)
- Automated reminders and statements
- Integration with general ledger
Generate comprehensive receivables dashboards and KPIs
```

### 28.3 Accounts Payable Management
```
Build comprehensive AP system:
- Vendor master data management
- Vendor classification (medical supplier, pharmaceutical, utilities, services)
- Tax information (NPWP, PKP status)
- Purchase order integration
- Goods receipt integration
- Three-way matching (PO - GR - Invoice)
- Invoice entry and processing
- Invoice approval workflow
- Payment scheduling
- Payment batch processing
- Payment method selection (transfer, check, giro)
- Payment authorization
- Vendor payment via bank integration
- Prepayment to vendors
- Vendor deposit management
- Debit note and credit note
- Vendor reconciliation
- Aging analysis for payables
- Cash requirement forecasting
- Payment terms tracking (net 30, net 60, etc.)
- Early payment discount management
- Withholding tax calculation (PPh 23, PPh 4(2))
- Tax reporting integration
- Vendor performance tracking
- Contract management with vendors
- Integration with general ledger
Generate AP reports (aging, cash flow forecast, vendor analysis)
```

### 28.4 Fixed Asset Management
```
Implement fixed asset accounting system:
- Asset master data:
  - Asset identification (tag number, serial number)
  - Asset category (medical equipment, building, vehicle, IT)
  - Asset description and specifications
  - Location tracking
  - Department assignment
  - Custodian assignment
- Asset acquisition:
  - Purchase
  - Donation
  - Self-constructed
- Asset capitalization
- Asset valuation
- Depreciation calculation:
  - Straight-line method
  - Declining balance method
  - Units of production method
  - Indonesian tax depreciation
- Depreciation posting to GL
- Asset transfer between departments/locations
- Asset disposal:
  - Sale
  - Scrap
  - Donation
  - Trade-in
- Disposal gain/loss calculation
- Asset revaluation
- Impairment testing
- Asset maintenance history linking
- Insurance tracking for assets
- Warranty tracking
- Asset barcode/RFID tagging
- Physical asset verification (stock opname)
- Reconciliation with physical count
- Asset utilization tracking
- Asset register reporting
- Depreciation schedule
- Integration with procurement
- Integration with general ledger
Generate fixed asset reports for tax and financial reporting
```

### 28.5 Procurement & Purchase Order Module
```
Create comprehensive procurement system:
- Purchase requisition (PR) workflow:
  - Department requests
  - Item/service specification
  - Quantity and urgency
  - Budget checking
  - Approval workflow (multi-level)
- Vendor selection process
- Request for quotation (RFQ)
- Quotation comparison
- Purchase order (PO) creation:
  - PO numbering
  - Item details
  - Pricing and terms
  - Delivery schedule
  - Payment terms
- PO approval workflow
- PO transmission to vendor (email, portal)
- PO amendment and cancellation
- Goods receipt process:
  - Delivery note recording
  - Quality inspection
  - Quantity verification
  - Acceptance or rejection
- Return to vendor management
- Service receipt for non-goods
- Three-way matching with invoice
- Blanket purchase orders
- Contract purchase orders
- Consignment inventory
- Budget control and commitment
- Spend analysis by category, vendor, department
- Procurement cycle time tracking
- Vendor performance evaluation
- Integration with inventory management
- Integration with accounts payable
- E-Katalog integration for government hospitals
- E-Purchasing/LKPP integration
Include procurement analytics and vendor scorecards
```

### 28.6 Indonesian Tax System Integration
```
Implement comprehensive tax compliance system:
- e-Faktur integration for PPN (VAT):
  - e-Faktur desktop application integration
  - Tax invoice numbering from DJP
  - Prepopulated tax invoice from billing
  - Upload to e-Faktur system
  - Reporting SPT Masa PPN
- e-Billing pajak integration:
  - Tax payment code (kode billing) generation
  - Payment via bank/online
  - Payment confirmation
  - NTPN recording
- Withholding tax management (PPh):
  - PPh 21 (employee income tax):
    - Gross-up calculation
    - Progressive tax rates
    - PTKP configuration
    - Monthly tax calculation
    - Annual reconciliation
    - 1721-A1 form generation
  - PPh 23 (services and rental):
    - 2% for services
    - 15% for royalty, interest
    - Exemption certificate verification
    - Bukti potong generation
  - PPh 4(2) final tax:
    - Rental of land/building
    - Construction services
  - PPh 22 (import and certain goods)
- Tax reporting:
  - SPT Masa PPh 21
  - SPT Masa PPh 23
  - SPT Masa PPh 4(2)
  - SPT Masa PPN
  - SPT Tahunan PPh Badan
- NPWP validation
- Bukti potong generation and distribution
- DJP online integration for e-Filing
- Tax payment tracking and aging
- Tax compliance dashboard
- Penalty calculation for late payment/reporting
- Integration with payroll system
- Integration with accounts payable
- Integration with general ledger
Generate tax reports and compliance checklists
```

---

## Phase 29: Patient Engagement & Digital Access

### 29.1 Patient Portal Development
```
Build comprehensive patient-facing portal:
- Patient registration and login:
  - NIK-based registration
  - Email/phone verification
  - Password creation and reset
  - Multi-factor authentication option
  - Biometric login (fingerprint, face ID) for mobile
- Personal health record access:
  - Demographics view and update
  - Medical history view
  - Current medications list
  - Allergy information
  - Immunization records
  - Problem list
- Appointment management:
  - Search doctors by specialty
  - View doctor schedules
  - Book appointments
  - Reschedule appointments
  - Cancel appointments
  - Appointment reminders
  - Check-in for appointments
- Lab and radiology results:
  - View test results
  - Download result PDFs
  - Historical result comparison
  - Graphical trends for serial tests
- Medical documents:
  - View and download medical records (with restrictions)
  - Discharge summaries
  - Prescriptions
  - Referral letters
  - Medical certificates
- Billing and payments:
  - View current and historical bills
  - View detailed bill breakdown
  - Make online payments
  - Download receipts
  - View insurance claims status
- Prescription refills:
  - Request refill for chronic medications
  - Track refill status
  - Pickup or delivery option
- Telemedicine integration:
  - Schedule teleconsultation
  - Join video consultation
  - Chat with doctor
- Health education:
  - Disease-specific education materials
  - Medication information
  - Wellness tips
  - Video library
- Communication:
  - Secure messaging with care team
  - Non-urgent questions
  - Feedback and complaints
  - Survey participation
- Family/proxy access:
  - Parent access to child records
  - Caregiver access to elderly patient
  - Consent-based access delegation
- Notifications:
  - Appointment reminders
  - Lab result availability
  - Bill due reminders
  - Health screening reminders
- Mobile app version (iOS and Android)
- Privacy and security:
  - Consent management
  - Access logs visible to patient
  - Data download (GDPR-like)
- Integration with WhatsApp for notifications
- Multi-language support (Indonesian and English)
Include patient portal analytics for hospital to monitor adoption
```

### 29.2 Advanced Appointment System
```
Enhance appointment system with comprehensive features:
- Multi-channel booking:
  - Web portal (patient portal)
  - Mobile app
  - Phone call (call center)
  - WhatsApp Business integration
  - Walk-in registration at hospital
  - Kiosk check-in machines
- Doctor schedule management:
  - Regular schedule configuration
  - Special clinic schedules
  - Schedule exceptions (leave, CME, emergency)
  - Slot duration configuration
  - Slot types (new patient, follow-up, urgent)
  - Block scheduling for procedures
  - Overbooking rules
- Appointment scheduling features:
  - Search by doctor name
  - Search by specialty
  - Search by available date/time
  - Preferred language of doctor
  - Gender preference
  - Provider bio and credentials
  - Patient reviews and ratings (optional)
- Appointment types:
  - Outpatient consultation
  - Follow-up visit
  - Pre-operative assessment
  - Post-operative follow-up
  - Diagnostic procedures
  - Vaccination
  - Medical check-up (MCU)
- Waitlist management:
  - Join waitlist for fully booked slots
  - Automatic notification when slot available
  - Waitlist priority rules
- Appointment reminders:
  - SMS reminder (1 day before)
  - WhatsApp reminder
  - Email reminder
  - Push notification (mobile app)
  - Confirmation request
- Pre-appointment preparation:
  - Fasting instructions
  - Medication instructions
  - Documents to bring
  - Insurance verification reminder
- Check-in process:
  - Online check-in
  - Kiosk check-in
  - QR code for Mobile JKN
  - Front desk check-in
  - Late arrival handling
- No-show tracking:
  - No-show recording
  - No-show penalty rules
  - Multiple no-show restrictions
- Appointment cancellation:
  - Patient self-cancellation (within time limit)
  - Cancellation by hospital
  - Cancellation reason tracking
  - Cancellation fee rules (if applicable)
- Rescheduling:
  - Patient self-reschedule
  - Staff-assisted reschedule
  - Reschedule history
- Queue management integration:
  - Real-time queue status
  - Estimated wait time
  - Queue number display
- Resource booking:
  - Examination room assignment
  - Equipment booking (for procedures)
  - Interpreter booking
  - Wheelchair assistance
- Appointment analytics:
  - Utilization rate per doctor
  - No-show rate analysis
  - Average wait time
  - Lead time for appointments
  - Overbooking effectiveness
  - Channel-wise booking trends
- Integration with billing for registration fees
- Integration with BPJS for eligibility
- Integration with patient portal
- Integration with Mobile JKN
Include comprehensive reporting for capacity planning
```

### 29.3 Mobile JKN Integration
```
Integrate with BPJS Mobile JKN patient application:
- QR code generation for patient check-in:
  - Generate unique QR code per visit
  - Encode patient identifier and visit information
  - Display QR code in HMS for scanning by Mobile JKN
- Mobile JKN appointment integration:
  - Receive appointment requests from Mobile JKN
  - Sync appointments to HMS
  - Confirm or reschedule via Mobile JKN
- Patient check-in via Mobile JKN:
  - Scan QR code from HMS
  - Automatic check-in in HMS
  - Queue number assignment
  - Notification to clinical staff
- Queue status synchronization:
  - Push current queue status to Mobile JKN
  - Update patient's position in queue
  - Estimated wait time
- Notification to Mobile JKN:
  - Appointment confirmation
  - Appointment reminder
  - Lab result ready notification
  - Prescription ready for pickup
  - Bill payment reminder
- Health information sharing:
  - Medication list sync
  - Lab result summary (with consent)
  - Vaccination records
  - Next appointment information
- Feedback integration:
  - Receive patient feedback from Mobile JKN
  - Respond to feedback
  - Integrate with hospital complaint system
- API integration for bidirectional communication
- Error handling and logging
- Privacy compliance (only share with consent)
- Testing in sandbox and production environments
Generate Mobile JKN integration reports and metrics
```

### 29.4 Patient Feedback & Satisfaction System
```
Implement comprehensive feedback collection system:
- Survey design and configuration:
  - Custom question builder
  - Multiple question types (Likert scale, multiple choice, open-ended)
  - Department-specific surveys
  - Service-specific surveys
  - Survey versioning
- Survey distribution channels:
  - SMS with survey link
  - Email with survey link
  - WhatsApp with survey
  - QR code at hospital (scan to fill)
  - Tablet kiosks at exit points
  - Patient portal
  - Mobile app
- Survey timing:
  - Post-discharge surveys
  - Post-outpatient visit
  - Post-emergency visit
  - Post-procedure surveys
  - In-stay surveys for inpatients
- Feedback categories:
  - Registration process
  - Doctor consultation quality
  - Nursing care
  - Cleanliness
  - Food quality (for inpatients)
  - Waiting time
  - Billing process
  - Overall satisfaction
  - Likelihood to recommend (NPS)
- Complaint management:
  - Complaint submission (online and offline)
  - Complaint categorization
  - Severity rating
  - Assignment to responsible department
  - Investigation workflow
  - Response to patient
  - Resolution tracking
  - Escalation rules
  - Root cause analysis for serious complaints
- Real-time feedback monitoring:
  - Dashboard for hospital management
  - Department-wise scores
  - Doctor-wise feedback
  - Alert for negative feedback
- Analytics and reporting:
  - Net Promoter Score (NPS)
  - Patient satisfaction score
  - Trending over time
  - Benchmark with national standards
  - Correlation with other metrics
- Action planning:
  - Quality improvement initiatives based on feedback
  - Service recovery for dissatisfied patients
  - Recognition for highly-rated staff
- Patient testimonials:
  - Collect positive testimonials
  - Publish with consent (marketing)
- Integration with KARS accreditation requirements
- Integration with Aplicares for BPJS reporting
Generate monthly patient satisfaction reports for leadership
```

### 29.5 Health Education & Engagement Platform
```
Create patient education and engagement system:
- Content management system:
  - Disease-specific education materials
  - Medication information
  - Pre-procedure and post-procedure instructions
  - Wellness and prevention topics
  - Chronic disease management guides
  - Nutrition and diet information
  - Exercise and rehabilitation guides
- Content formats:
  - Written articles
  - Infographics
  - Videos
  - Interactive modules
  - Downloadable PDFs
  - Audio content
- Content organization:
  - By medical specialty
  - By disease/condition
  - By topic (nutrition, exercise, mental health)
  - By target audience (children, adults, elderly)
- Personalized content delivery:
  - Content recommendations based on patient's conditions
  - Post-discharge instructions specific to patient
  - Medication-specific education
  - Condition-specific monitoring tips
- Content distribution:
  - Patient portal
  - Mobile app
  - Email newsletters
  - SMS tips
  - WhatsApp broadcasts
  - In-hospital TV displays
  - Waiting room displays
- Interactive tools:
  - BMI calculator
  - Calorie calculator
  - Medication interaction checker
  - Symptom checker (with disclaimer)
  - Health risk assessments
- Community features:
  - Support groups (diabetes, cancer, etc.)
  - Moderated forums
  - Patient stories
  - Q&A with healthcare professionals
- Health campaigns:
  - Seasonal campaigns (flu vaccination, dengue prevention)
  - World health days
  - Screening campaigns
  - Registration and participation tracking
- Wellness programs:
  - Weight management program
  - Diabetes management program
  - Hypertension control program
  - Smoking cessation program
  - Program enrollment and tracking
- Reminders and notifications:
  - Medication reminders
  - Health screening reminders
  - Vaccination schedule reminders
  - Exercise reminders
  - Hydration reminders
- Gamification (optional):
  - Health challenges
  - Points for healthy behaviors
  - Badges and achievements
  - Leaderboards
- Analytics:
  - Content engagement metrics
  - Most viewed topics
  - User journey analysis
  - Program completion rates
- Multi-language support (Indonesian and English)
- Accessibility compliance (for vision/hearing impaired)
Include content approval workflow and version control
```

---

## Phase 30: Specialized Clinical Services

### 30.1 Medical Check-Up (MCU) Package Management
```
Build comprehensive MCU package system:
- MCU package configuration:
  - Standard packages (Basic, Silver, Gold, Platinum)
  - Age-specific packages (pediatric, adult, elderly)
  - Gender-specific packages (men's health, women's health)
  - Occupation-specific packages (pilot, driver, office worker)
  - Disease-specific screening (diabetes, heart, cancer)
  - Custom package builder
- Package components:
  - Laboratory tests included
  - Radiology examinations
  - Specialist consultations
  - Procedures (ECG, spirometry, audiometry)
  - Package pricing with discount
- Corporate MCU management:
  - Corporate client registration
  - Contract management with companies
  - Employee list upload (bulk registration)
  - Group scheduling and coordination
  - Dedicated MCU days for companies
  - Department-wise reports for companies
- MCU appointment scheduling:
  - Online booking for walk-in customers
  - Bulk appointment for corporates
  - Time slot management
  - Preparation instructions
  - Pre-MCU questionnaire
- MCU workflow management:
  - Registration and package selection
  - Station routing (lab, radiology, consultation)
  - Progress tracking per participant
  - Result compilation from all stations
- Result compilation and reporting:
  - Automatic aggregation of all test results
  - Doctor's review and interpretation
  - Executive summary generation
  - Health risk assessment
  - Recommendations and follow-up
  - Result booklet printing
  - Digital delivery via email/portal
- Corporate invoicing:
  - Group invoice generation
  - Attendance-based billing
  - Package vs actual reconciliation
  - Payment terms for corporates
- MCU analytics:
  - Package popularity analysis
  - Corporate client retention
  - Revenue per package
  - Common findings statistics
  - Referral to hospital services
Generate MCU business performance dashboards
```

### 30.2 Dental Clinic Module
```
Implement comprehensive dental management system:
- Dental charting (odontogram):
  - Tooth numbering system (FDI, Universal, Palmer)
  - Visual tooth diagram
  - Tooth status marking (caries, filling, missing, crown)
  - Surface-specific charting
  - Periodontal charting
  - Mobile tooth marking
  - Version control for chart updates
- Dental examination:
  - Chief complaint documentation
  - Oral hygiene assessment
  - Gingival and periodontal status
  - TMJ examination
  - Occlusion assessment
  - Soft tissue examination
- Dental diagnosis:
  - Tooth-specific diagnoses
  - Periodontal disease staging
  - Orthodontic classification
  - Oral pathology
  - ICD-10 coding for dental conditions
- Dental procedure management:
  - Treatment planning
  - Procedure codes (CDT or local codes)
  - Multi-appointment treatment sequencing
  - Procedure documentation
  - Materials used tracking
  - Chair time tracking
- Dental treatment categories:
  - Preventive (cleaning, fluoride, sealants)
  - Restorative (fillings, inlays, onlays)
  - Endodontic (root canal)
  - Periodontic (scaling, curettage, surgery)
  - Prosthodontic (crown, bridge, denture)
  - Orthodontic (braces, retainers)
  - Oral surgery (extraction, implant)
- Dental imaging integration:
  - Intraoral X-ray
  - Panoramic X-ray
  - Cephalometric X-ray
  - CBCT
  - Intraoral photos
  - Image annotation
- Orthodontic treatment tracking:
  - Treatment plan and timeline
  - Bracket placement diagram
  - Wire progression
  - Appliance tracking
  - Monthly adjustment records
  - Progress photos
- Dental materials inventory:
  - Restorative materials stock
  - Impression materials
  - Orthodontic supplies
  - Prosthetic lab materials
  - Batch and expiry tracking
- Dental lab integration:
  - Lab work orders (crown, bridge, denture)
  - Shade selection
  - Lab case tracking
  - Try-in and delivery scheduling
  - External lab management
- Dental billing:
  - Treatment cost estimation
  - Insurance coverage verification
  - Treatment plan approval
  - Procedure-based billing
  - Installment plans for orthodontic/implant
Generate dental practice analytics and treatment statistics
```

### 30.3 Home Care Service Module
```
Create comprehensive home care management:
- Patient enrollment for home care:
  - Eligibility assessment
  - Physician referral for home care
  - Service need assessment
  - Care plan development
  - Caregiver assignment
  - Schedule configuration
- Home care services:
  - Home nursing care
  - Wound care and dressing
  - Medication administration
  - Vital signs monitoring
  - Sample collection for lab
  - Physical therapy at home
  - Occupational therapy
  - IV therapy
  - Palliative care
  - Post-surgical care
- Visit scheduling and routing:
  - Staff assignment per geographic area
  - Daily route optimization
  - Visit time slot management
  - Emergency visit insertion
  - Travel time calculation
  - Visit confirmation with patient/family
- Home visit documentation:
  - Visit start/end time (GPS stamping)
  - Patient assessment
  - Services provided
  - Vital signs recording
  - Medication administration record
  - Care plan updates
  - Photo documentation (wounds, etc.)
  - Patient/family education
  - Next visit planning
- Supply management for home care:
  - Medical supplies tracking
  - Equipment loan tracking (oxygen, suction, beds)
  - Consumable replenishment
  - Equipment maintenance scheduling
  - Return and sanitization
- Medication management:
  - Medication delivery service
  - Medication reconciliation at home
  - Administration assistance
  - Compliance monitoring
  - Prescription refill coordination
- Family caregiver support:
  - Caregiver training documentation
  - Respite care scheduling
  - Caregiver communication log
  - Educational material provision
  - Caregiver support group
- Transportation coordination:
  - Staff vehicle assignment
  - Mileage tracking
  - Transportation reimbursement
  - Patient transport (to/from hospital)
  - Ambulance coordination if needed
- Safety and risk management:
  - Home safety assessment
  - Fall risk evaluation
  - Infection control in home setting
  - Emergency protocol for home care staff
  - Incident reporting for home visits
- Home care billing:
  - Service-based pricing
  - Visit-based or time-based billing
  - Supply and equipment rental charges
  - Insurance claims for home care
  - Package deals for chronic patients
- Integration with hospital systems:
  - Direct admission from home care
  - Lab results from home collection
  - Telemedicine consultation for home patients
  - Medication delivery from pharmacy
  - Electronic care plan sharing
Generate home care utilization and outcome reports
```

### 30.4 Oncology Management Module
```
Build comprehensive oncology care system:
- Cancer patient registration:
  - Cancer diagnosis documentation
  - Tumor site and histology
  - TNM staging
  - Cancer registry enrollment
  - Genetic testing results
  - Biomarker testing
- Multidisciplinary tumor board:
  - Case discussion scheduling
  - Participant roster (surgeons, oncologists, radiologists, pathologists)
  - Case presentation template
  - Discussion notes and decisions
  - Treatment plan consensus
  - Follow-up recommendations
- Chemotherapy protocol management:
  - Protocol library (by cancer type)
  - Dose calculation by BSA/weight
  - Cycle scheduling
  - Premedication orders
  - Hydration requirements
  - Growth factor support
  - Dose adjustment rules
- Chemotherapy administration:
  - Pre-chemo assessment (CBC, organ function)
  - Toxicity assessment (CTCAE grading)
  - Vital signs monitoring during infusion
  - Adverse reaction documentation
  - Post-chemo monitoring
  - Patient education on side effects
  - Emergency protocols for reactions
- Chemotherapy safety:
  - Double-check verification
  - BSA calculation verification
  - Dose limit alerts
  - Chemotherapy extravasation protocol
  - Spill management
  - PPE requirements
  - Waste disposal tracking
- Radiation therapy scheduling:
  - Simulation appointment
  - Treatment planning integration
  - Daily fraction scheduling
  - Machine assignment
  - Position and field verification
  - Dose tracking
  - Skin reaction assessment
- Immunotherapy management:
  - Immune checkpoint inhibitor protocols
  - Immune-related adverse events (irAE) monitoring
  - Corticosteroid management for irAE
  - Patient education on immunotherapy
- Supportive care management:
  - Pain assessment and management
  - Nausea/vomiting management
  - Nutrition support
  - Psychosocial support
  - Palliative care integration
  - Hospice referral when appropriate
- Cancer surveillance and follow-up:
  - Surveillance imaging schedule
  - Tumor marker tracking
  - Response assessment (RECIST criteria)
  - Disease progression documentation
  - Survivorship care plans
  - Late effects monitoring
- Clinical trial integration:
  - Trial enrollment tracking
  - Protocol compliance monitoring
  - Adverse event reporting
  - Data collection for trials
  - Consent management
- Cancer registry reporting:
  - Mandatory reporting to cancer registry
  - Data completeness checking
  - Survival analysis
  - Treatment outcome tracking
Generate oncology quality metrics and outcome reports
```

### 30.5 Cardiology/Cardiac Cath Lab Module
```
Implement cardiology and cath lab management:
- Cardiac diagnostic testing:
  - ECG interpretation and reporting
  - Stress test (treadmill, pharmacologic)
  - Echocardiography reporting
  - Holter monitor setup and analysis
  - Event monitor management
  - Cardiac CT/MRI coordination
- Cath lab procedure scheduling:
  - Elective catheterization booking
  - Emergency PCI slot management
  - Pre-procedure checklist
  - NPO verification
  - Consent documentation
  - Anticoagulation review
  - Allergy check (contrast, medications)
- Cath lab procedure documentation:
  - Indication for procedure
  - Access site (femoral, radial)
  - Hemodynamic measurements
  - Coronary anatomy findings
  - Lesion characteristics
  - Intervention details (balloon, stent type, size)
  - Fluoroscopy time and contrast volume
  - Complications during procedure
  - Final result (TIMI flow, residual stenosis)
- Hemodynamic monitoring:
  - Pressure measurements (aortic, LV, PA, wedge)
  - Cardiac output/index calculation
  - Valve area calculation
  - Shunt calculation
  - Resistance calculations
- Interventional cardiology:
  - PCI (angioplasty and stenting)
  - Device implantation tracking:
    - Stent type, brand, size
    - Lot number tracking
    - Number of devices used
  - Balloon valvuloplasty
  - Structural heart interventions (TAVR, MitraClip)
  - PFO/ASD closure
  - Left atrial appendage closure
- Post-procedure management:
  - Access site monitoring
  - Hemostasis documentation
  - Bed rest requirements
  - Antiplatelet therapy confirmation
  - Discharge criteria
  - Follow-up scheduling
- Cardiac device registry:
  - Pacemaker implantation
  - ICD implantation
  - CRT devices
  - Device parameters
  - Lead information
  - Battery status monitoring
  - Device interrogation schedule
  - Generator replacement planning
- Electrophysiology procedures:
  - EP study documentation
  - Ablation procedure details
  - Success criteria documentation
  - Recurrence tracking
- Cardiology follow-up clinic:
  - Post-PCI follow-up protocol
  - Device clinic for pacemaker/ICD
  - Heart failure clinic
  - Anticoagulation management
  - Cardiac rehabilitation referral
- Quality metrics:
  - Door-to-balloon time for STEM I
  - Radial access rate
  - Complication rates
  - Device success rate
  - 30-day outcomes
  - Readmission rates
Generate cardiology and cath lab performance reports
```

### 30.6 Rehabilitation/Physiotherapy Module
```
Create comprehensive rehabilitation management:
- Rehabilitation assessment:
  - Functional status assessment
  - Range of motion measurement
  - Muscle strength grading
  - Pain assessment scales
  - Mobility assessment
  - ADL (Activities of Daily Living) assessment
  - Fall risk assessment
  - Gait analysis
- Rehabilitation diagnosis:
  - Primary condition (stroke, fracture, surgery, etc.)
  - Functional limitations
  - Rehabilitation goals (short-term, long-term)
  - Prognosis for recovery
- Therapy prescription:
  - Physician referral processing
  - Therapy type selection (PT, OT, speech)
  - Frequency and duration
  - Precautions and contraindications
  - Special instructions
- Physical therapy management:
  - Treatment plan development
  - Exercise prescription
  - Manual therapy techniques
  - Modality application (heat, cold, TENS, ultrasound)
  - Gait training
  - Balance training
  - Strengthening exercises
  - Functional training
- Occupational therapy:
  - ADL training
  - Fine motor skill exercises
  - Cognitive retraining
  - Work hardening
  - Adaptive equipment training
  - Home modification recommendations
- Speech therapy:
  - Swallowing assessment and therapy
  - Language therapy (aphasia)
  - Articulation therapy
  - Voice therapy
  - Cognitive-communication therapy
- Therapy session documentation:
  - Attendance tracking
  - Session notes
  - Interventions performed
  - Patient response to treatment
  - Progress toward goals
  - Functional outcomes measurement
  - Home exercise program
- Equipment and modality tracking:
  - Therapy equipment usage
  - Modality machine scheduling
  - Equipment maintenance
  - Orthotic and prosthetic fitting
  - Assistive device prescription
- Group therapy sessions:
  - Group class scheduling
  - Participant registration
  - Class attendance
  - Group exercise protocols
- Rehabilitation progress tracking:
  - Periodic reassessment
  - Functional outcome measures
  - Goal achievement tracking
  - Discharge planning
  - Home exercise compliance
- Cardiac rehabilitation (if applicable):
  - Phase I (inpatient) - early mobilization
  - Phase II (outpatient) - supervised exercise
  - Phase III (maintenance) - long-term program
  - Risk stratification
  - Exercise prescription based on stress test
  - Lifestyle modification counseling
- Pulmonary rehabilitation:
  - Breathing exercises
  - Airway clearance techniques
  - Oxygen titration during exercise
  - Smoking cessation support
- Billing integration:
  - Session-based billing
  - Evaluation and re-evaluation charges
  - Modality charges
  - Time-based billing
  - Insurance authorization tracking
Generate rehabilitation outcome and productivity reports
```

---

## Phase 31: Hospital Operations Management

### 31.1 Asset Management System
```
Implement comprehensive hospital asset management:
- Medical equipment inventory:
  - Equipment master data (all medical devices)
  - Equipment category (diagnostic, therapeutic, life support, monitoring)
  - Manufacturer and model information
  - Serial number and asset tag
  - Purchase information (date, cost, vendor)
  - Warranty information
  - Location tracking (department, room)
  - Status (in use, under repair, decommissioned)
- Equipment lifecycle management:
  - Acquisition and receiving
  - Installation and commissioning
  - Utilization tracking
  - Preventive maintenance scheduling
  - Corrective maintenance (breakdown repair)
  - Calibration requirements and scheduling
  - Performance testing
  - Decommissioning and disposal
- Preventive maintenance (PM) program:
  - PM schedule configuration (daily, weekly, monthly, quarterly, annual)
  - PM task checklist per equipment type
  - PM work order generation
  - Technician assignment
  - PM completion documentation
  - Parts used tracking
  - Next PM date calculation
  - Overdue PM alerts
- Corrective maintenance:
  - Equipment breakdown reporting
  - Work order creation
  - Priority assignment (emergency, urgent, routine)
  - Technician dispatch
  - Troubleshooting and repair documentation
  - Parts requisition
  - Downtime tracking
  - Cost tracking (labor, parts)
  - Equipment availability after repair
- Calibration management:
  - Calibration schedule per equipment
  - Internal vs external calibration
  - Calibration procedure documentation
  - Calibration certificate management
  - Out-of-tolerance handling
  - Recalibration requirements
  - Regulatory compliance tracking
- Equipment utilization tracking:
  - Usage hours/cycles recording
  - Department-wise utilization
  - Idle equipment identification
  - Utilization reports for planning
  - Cost per use calculation
- Mobile equipment tracking:
  - Equipment check-out/check-in
  - Location transfer between departments
  - Equipment reservation system
  - Availability status
  - Equipment search and locator
- Safety and risk management:
  - Equipment recall tracking
  - Safety alert management
  - Adverse event reporting related to equipment
  - Risk assessment for equipment
  - User training requirements
  - Competency tracking
- Vendor and service contract management:
  - Vendor contact information
  - Service contract details
  - Contract renewal tracking
  - SLA monitoring
  - Vendor performance evaluation
  - Service call logging
- Spare parts inventory:
  - Critical spare parts stock
  - Parts requisition for maintenance
  - Parts usage tracking
  - Reorder level management
  - Parts cost tracking
- Equipment replacement planning:
  - Equipment age analysis
  - Replacement priority scoring
  - Budget planning for replacement
  - Technology assessment
  - ROI analysis
- Regulatory compliance:
  - Equipment licensure tracking
  - Inspection by regulatory bodies
  - Compliance documentation
  - Non-conformance management
- Reports and analytics:
  - Equipment downtime analysis
  - PM compliance rate
  - Maintenance cost per equipment
  - Mean time between failures (MTBF)
  - Mean time to repair (MTTR)
  - Equipment reliability metrics
Generate asset management dashboards for hospital leadership
```

### 31.2 Laundry & Linen Management
```
Build laundry service management system:
- Linen inventory management:
  - Linen catalog (bed sheets, blankets, patient gowns, scrubs, towels)
  - Size and type configuration
  - Par level by department
  - Clean linen stock
  - Soiled linen tracking
  - Linen in circulation
  - Linen lifecycle tracking (new, good, worn, discard)
- Soiled linen collection:
  - Collection schedule per department/ward
  - Isolation linen handling
  - Infectious linen segregation
  - Weight measurement per department
  - Collection documentation
  - Bag tracking with barcode/RFID
- Laundry processing:
  - Batch tracking
  - Washing machine assignment
  - Washing cycle documentation (temperature, detergent, duration)
  - Drying and pressing
  - Quality check after washing
  - Rewash management for stained items
- Clean linen distribution:
  - Department requisition
  - Par level replenishment
  - Distribution schedule
  - Clean linen cart management
  - Department acknowledgment
  - Emergency linen request
- Linen loss tracking:
  - Discarded/damaged linen recording
  - Missing linen investigation
  - Department-wise loss analysis
  - Replacement planning
- External laundry management (if outsourced):
  - Vendor contract management
  - Weight-based billing
  - Pickup and delivery schedule
  - Quality monitoring
  - Complaint management
- Linen tagging and tracking:
  - RFID tag assignment
  - Scan at each touchpoint (collection, wash, distribution)
  - Real-time location tracking
  - Automated inventory counting
  - Loss prevention
- Patient-owned item management:
  - Personal clothing labeling
  - Separate washing
  - Return to patient on discharge
- Staff uniform management:
  - Uniform issuance to staff
  - Exchange program for soiled scrubs
  - Size and stock management
- Infection control compliance:
  - Color-coded bags for infection control
  - Temperature monitoring in washing
  - Sanitization verification
  - Blood-borne pathogen protocol
  - Staff PPE requirements
- Laundry equipment maintenance:
  - Washing machine and dryer maintenance
  - Breakdown reporting
  - Maintenance scheduling
  - Performance monitoring
- Cost management:
  - Water and energy consumption tracking
  - Detergent and chemical usage
  - Labor cost allocation
  - Cost per kg washed
  - Budget vs actual analysis
Generate laundry utilization and cost efficiency reports
```

### 31.3 Housekeeping Management
```
Implement housekeeping and environmental services:
- Room status management:
  - Real-time room status (occupied, dirty, clean, ready)
  - Status update by housekeeping staff
  - Integration with patient admission/discharge
  - Status board for nursing units
- Cleaning task scheduling:
  - Daily cleaning schedule per area
  - Deep cleaning schedule (weekly, monthly)
  - Terminal cleaning after discharge
  - Isolation room cleaning protocol
  - Operating room turnover cleaning
  - Emergency cleaning requests
- Task assignment:
  - Housekeeper assignment by zone
  - Task distribution
  - Mobile app for task notification
  - Task acknowledgment and completion
- Cleaning checklist:
  - Area-specific cleaning checklist
  - High-touch surface cleaning
  - Floor cleaning methods
  - Bathroom sanitization
  - Window and glass cleaning
  - Waste disposal
  - Quality check after cleaning
- Supplies and chemical management:
  - Cleaning supplies inventory
  - Chemical usage tracking
  - Material safety data sheets (MSDS)
  - Dilution ratio monitoring
  - Supply replenishment
  - Cart stocking
- Equipment management:
  - Cleaning equipment inventory
  - Equipment assignment to staff
  - Maintenance of housekeeping equipment
  - Replacement planning
- Environmental rounds:
  - Inspection schedule
  - Cleanliness scoring
  - Deficiency reporting
  - Follow-up on corrections
  - Infection control compliance
- Waste management:
  - General waste collection
  - Medical waste segregation
  - Sharps container management
  - Hazardous waste handling
  - Cytotoxic waste procedures
  - Waste weighing and documentation
  - Third-party waste disposal coordination
- Linen and trash chute management:
  - Chute usage monitoring
  - Cleaning and sanitization
  - Malfunction reporting
- Special event cleaning:
  - Conference room setup
  - Event cleanup
  - VIP room special cleaning
- Pest control:
  - Pest control schedule
  - Vendor coordination
  - Inspection documentation
  - Incident reporting (if pest found)
- Housekeeping staff management:
  - Shift scheduling
  - Attendance tracking
  - Training and competency
  - PPE compliance
  - Performance evaluation
- Quality and infection control:
  - ATP bioluminescence testing
  - Environmental culture results
  - Hand hygiene compliance of housekeeping staff
  - Spill management protocol
  - Isolation cleaning verification
- Work order system:
  - Maintenance requests from housekeeping
  - Follow-up on repairs affecting cleanliness
  - Coordination with engineering
- Patient satisfaction:
  - Cleanliness feedback from patients
  - Complaint resolution
  - Service recovery
Generate housekeeping productivity and quality reports
```

### 31.4 Food Service/Dietary Management
```
Create hospital food service management system:
- Menu planning:
  - Regular menu (breakfast, lunch, dinner, snacks)
  - Therapeutic diet menus (low sodium, diabetic, renal, cardiac)
  - Cultural/religious menu options (halal, vegetarian)
  - Texture-modified diets (pureed, minced, soft)
  - Allergen-free options
  - Cycle menu management
  - Nutritional analysis per menu item
- Diet order management:
  - Physician diet order from EMR
  - Diet code configuration
  - Consistency and allergy checking
  - NPO (nothing by mouth) management
  - Clear liquid to regular diet progression
  - Tube feeding orders
  - Special requests
- Patient meal ordering:
  - Bedside meal ordering (via tablet/phone)
  - Menu selection within diet restrictions
  - Meal preference documentation
  - Order cutoff time management
  - Late tray requests
- Meal production planning:
  - Production forecast by diet type
  - Ingredient requirement calculation
  - Kitchen work order generation
  - Batch cooking management
  - Food safety protocols (HACCP)
  - Temperature monitoring (cooking, holding)
- Tray assembly and delivery:
  - Patient census integration
  - Tray card printing
  - Tray line management
  - Meal verification against diet order
  - Delivery schedule by floor/unit
  - Delivery cart management
  - Meal temperature check at delivery
- Meal service documentation:
  - Meal delivery time
  - Meal consumption percentage
  - Patient refusal documentation
  - Special feeding assistance
  - Between-meal snacks
- Therapeutic diet monitoring:
  - Calorie count for nutritional assessment
  - Protein intake monitoring
  - Fluid restriction tracking
  - Weight-based feeding (tube feeding ml/kg)
- Food allergy management:
  - Allergy alerts on tray cards
  - Allergen-free meal verification
  - Cross-contamination prevention
  - Allergy incident reporting
- Quality control:
  - Food temperature logs
  - Food samples retention
  - Taste testing
  - Portion size consistency
  - Plate waste monitoring
  - Patient feedback on meals
- Kitchen inventory management:
  - Ingredient stock levels
  - FIFO/FEFO for food items
  - Expiry date monitoring
  - Purchasing and receiving
  - Vendor management
  - Cost control
- Staff cafeteria management:
  - Employee meal service
  - Cafeteria menu
  - Point of sale system
  - Employee meal subsidies
  - Cash and card payment
- Special diet counseling:
  - Dietitian meal rounding
  - Patient education on therapeutic diets
  - Discharge diet instructions
  - Recipe sharing
- Food safety and sanitation:
  - Kitchen sanitation schedule
  - Equipment cleaning logs
  - Staff health monitoring
  - Food safety training
  - Health department inspections
  - Pest control
- Meal cost management:
  - Cost per meal calculation
  - Budget vs actual food cost
  - Waste reduction initiatives
  - Nutritional value per dollar
Generate food service quality and cost reports
```

### 31.5 Mortuary/Jenazah Management
```
Implement mortuary and deceased patient management:
- Death notification workflow:
  - Death pronouncement documentation
  - Time of death recording
  - Attending physician documentation
  - Cause of death (preliminary)
  - Family notification log
  - Required autopsy determination
- Deceased patient identification:
  - Patient identification verification
  - Body tagging (ID tag on toe/wrist)
  - Religious/cultural considerations
  - Personal effects inventory
  - Valuables security
- Body transfer to mortuary:
  - Transfer documentation
  - Chain of custody
  - Body bag usage
  - Transfer cart/stretcher
  - Infection control precautions
  - Mortuary admission log
- Cold storage assignment:
  - Refrigeration unit assignment
  - Body placement documentation
  - Temperature monitoring
  - Storage duration tracking
  - Extended storage management
- Autopsy management:
  - Autopsy consent (family/legal)
  - Forensic case handling
  - Pathologist scheduling
  - Autopsy procedure documentation
  - Specimen collection for histopathology
  - Autopsy report
  - Organ/tissue donation coordination
- Death certificate generation:
  - Death certificate form
  - Cause of death documentation (ICD-10)
  - Contributing factors
  - Manner of death
  - Physician signature (digital signature)
  - Certificate issuance to family
  - Registration with civil registry
- Religious and cultural practices:
  - Islamic burial preparation (Indonesian Muslim majority)
  - Body washing facilities (if provided)
  - Kafan (shroud) provision
  - Prayer room access
  - Viewing room for family
  - Cultural sensitivity training for staff
- Body release workflow:
  - Family identification and authorization
  - Release paperwork
  - Funeral home coordination
  - Body handover documentation
  - Personal effects return
  - Receipt from funeral home
  - Outstanding bill settlement
- Forensic and medico-legal cases:
  - Police notification for suspicious deaths
  - Evidence preservation
  - Chain of custody documentation
  - Police clearance for release
  - Court order compliance
  - Forensic autopsy coordination
- Organ and tissue donation:
  - Donor identification
  - Family consent
  - Tissue bank coordination
  - Organ procurement documentation
  - Donation registry reporting
- Infection control for infectious deaths:
  - Special handling for TB, COVID, hepatitis deaths
  - Body bag requirements
  - PPE for staff
  - Disinfection procedures
  - Communication to funeral home about infectious risk
- Unclaimed body management:
  - Holding period tracking
  - Notification to authorities
  - Social services coordination
  - Disposition according to local laws
- Mortuary operations:
  - Refrigeration unit maintenance
  - Cleaning and disinfection schedule
  - Supply management (body bags, tags, forms)
  - Staff schedule (24/7 coverage if needed)
- Reporting and statistics:
  - Mortality statistics
  - Cause of death analysis
  - Autopsy rate
  - Average time from death to release
  - Storage utilization
Generate mortality reports for hospital administration and government
```

### 31.6 Visitor Management System
```
Build comprehensive visitor access control:
- Visitor registration:
  - Visitor check-in at entrance
  - ID capture (NIK, passport, driver's license)
  - Patient name and room number
  - Relationship to patient
  - Photo capture
  - Health screening questionnaire (symptoms, exposure)
  - Temperature checking
  - Vaccination status (COVID-19 or other requirements)
- Badge printing:
  - Temporary visitor badge with photo
  - Visit date and time
  - Patient room authorization
  - Badge return on exit
- Visiting hours management:
  - Department-specific visiting hours
  - Special unit restrictions (ICU, NICU)
  - Flexible hours for special circumstances
  - Visiting hours exception approval
- Access control:
  - Authorized areas for visitors
  - Restricted area enforcement
  - Integration with door access system
  - Security alert for unauthorized access
- Visitor limits:
  - Maximum visitors per patient
  - Age restrictions (pediatric units)
  - ICU visitor restrictions
  - Isolation room visitor protocols
- VIP and special visits:
  - VIP visitor handling
  - Government official visits
  - Celebrity/high-profile patient visitors
  - Media access control
- Contractor and vendor access:
  - Contractor registration
  - Work permit verification
  - Safety orientation completion
  - Escorted vs unescorted access
  - Equipment/tool inventory
- COVID-19 and infectious disease protocols:
  - Health declaration
  - Symptom screening
  - Contact tracing data collection
  - Visitor quarantine status
  - PPE provision for visitors
  - Visitor flow management for social distancing
- Emergency lockdown:
  - Lockdown initiation
  - Visitor evacuation or shelter-in-place
  - Visitor accounting during emergency
  - All-clear and normal operations resumption
- Parking management integration:
  - Visitor parking validation
  - Parking fee waiver for extended visits
  - Parking space availability
- Visiting log and reporting:
  - Visit duration tracking
  - Frequent visitor identification
  - Security incident involving visitors
  - Visitor satisfaction
  - Compliance with visiting policies
- Self-service kiosk:
  - Automated check-in
  - Touchscreen interface
  - ID scanning
  - Badge printing
  - Directions to patient room
Generate visitor analytics and security reports
```

---

## Phase 32: Support Services & Infrastructure

### 32.1 Nutrition/Dietetics Service Module
```
Implement comprehensive clinical nutrition service:
- Nutritional screening:
  - Screening tool (e.g., NRS-2002, MUST)
  - Automatic screening trigger on admission
  - Screening score calculation
  - Risk level categorization (low, medium, high)
  - Referral to dietitian based on score
- Nutritional assessment:
  - Anthropometric measurements (height, weight, BMI, body composition)
  - Biochemical data (albumin, pre-albumin, electrolytes)
  - Clinical data (diagnosis, symptoms affecting intake)
  - Dietary history and intake pattern
  - Functional status
  - Psychosocial factors
- Nutrition diagnosis:
  - PES format (Problem, Etiology, Signs/Symptoms)
  - Malnutrition diagnosis (undernutrition, overnutrition)
  - Nutrient deficiency identification
  - Feeding difficulties
- Nutrition care plan:
  - Calorie and protein goals
  - Micronutrient requirements
  - Route of nutrition (oral, enteral, parenteral)
  - Diet prescription
  - Nutritional supplements
  - Feeding schedule
  - Monitoring parameters
- Medical nutrition therapy (MNT):
  - Disease-specific MNT (diabetes, renal, cardiac, GI disorders)
  - Therapeutic diet implementation
  - Nutrition intervention strategies
  - Patient and family education
  - Behavioral modification
- Enteral nutrition management:
  - Enteral formula selection
  - Feeding route (NG, NJ, gastrostomy, jejunostomy)
  - Feeding regimen (bolus, continuous, cyclic)
  - Feeding rate progression
  - Tolerance monitoring (residuals, diarrhea, vomiting)
  - Complication management
- Parenteral nutrition (PN) management:
  - PN indication and contraindication review
  - PN formulation (macronutrients, micronutrients)
  - Central vs peripheral PN
  - PN order generation
  - Metabolic monitoring (glucose, electrolytes, liver function)
  - PN-related complication tracking
  - Transition from PN to enteral/oral
- Nutrition reassessment:
  - Periodic reassessment schedule
  - Anthropometric trend analysis
  - Intake vs goal comparison
  - Biochemical markers review
  - Care plan adjustment
- Discharge nutrition planning:
  - Discharge diet prescription
  - Home nutrition support arrangements
  - Patient/family education materials
  - Follow-up nutrition clinic appointment
  - Community resource referral
- Outpatient nutrition counseling:
  - Appointment scheduling
  - Initial consultation
  - Follow-up visits
  - Weight management programs
  - Diabetes self-management education
  - Food allergies and intolerances counseling
- Nutrition support team (NST):
  - Multidisciplinary team (dietitian, physician, pharmacist, nurse)
  - Weekly rounds
  - Complex case discussion
  - Protocol development
  - Quality improvement projects
- Nutrition documentation:
  - SOAP or ADIME format
  - Integration with EMR
  - Nutrition progress notes
  - Intervention outcomes
- Quality metrics:
  - Screening completion rate
  - Malnutrition prevalence
  - Time from referral to assessment
  - Enteral feeding complications
  - PN-associated infections
Generate nutrition service utilization and outcome reports
```

### 32.2 Document Management System (DMS)
```
Build comprehensive document control system:
- Document repository:
  - Centralized document storage
  - Category structure (policies, procedures, forms, guidelines)
  - Department-wise organization
  - Document metadata (title, type, department, version)
  - Full-text search capability
  - Advanced filtering
- Document creation and authoring:
  - Document templates
  - Online editing
  - Collaborative authoring
  - Version drafting
  - Comment and review features
- Document approval workflow:
  - Multi-level approval routing
  - Approval matrix by document type
  - Electronic approval/rejection
  - Approval comment tracking
  - Notification to approvers
  - Approval deadline monitoring
- Version control:
  - Version numbering (major, minor)
  - Version comparison
  - Revision history
  - Rollback to previous version
  - Obsolete version archival
- Document distribution:
  - Controlled distribution list
  - Access permission by role/department
  - Document notification upon release
  - Read acknowledgment tracking
  - Print control (watermark for controlled copy)
- Expiry and review management:
  - Document validity period
  - Review due date calculation
  - Review reminders
  - Periodic review workflow
  - Extension requests
- SOP and policy management:
  - Standard operating procedure library
  - Policy repository
  - Clinical guidelines
  - Protocol database
  - Integration with quality management
- Accreditation document preparation:
  - Mapping to KARS/ISO standards
  - Document completeness checking
  - Evidence collection for accreditation
  - Document package generation
- Forms management:
  - Form catalog (patient forms, administrative forms)
  - Electronic fillable forms
  - Form version control
  - Usage tracking
- Training material management:
  - Training document repository
  - E-learning content
  - Competency assessment forms
  - Training record linkage
- External document management:
  - Regulatory documents (Permenkes, guidelines)
  - Vendor manuals and SDS
  - Contract and agreement storage
  - Certificate management
- Audit trail:
  - Document access logging
  - Download and print tracking
  - Modification history
  - User activity report
- Records retention:
  - Retention schedule by document type
  - Archival workflow
  - Secure deletion after retention period
  - Legal hold management
- Integration with hospital systems:
  - EMR integration for clinical protocols
  - HRIS integration for employee documents
  - Quality system integration
  - Compliance reporting
Generate document metrics (total documents, pending approvals, overdue reviews)
```

### 32.3 Internal Communication Platform
```
Create hospital-wide communication system:
- Instant messaging:
  - User-to-user messaging
  - Group chats by department
  - Read receipts
  - File sharing
  - Message search
  - Message history
- Department-to-department communication:
  - Nursing to pharmacy communication
  - Clinical to laboratory queries
  - Coordination between services
  - Handoff communication
- Announcement system:
  - Hospital-wide announcements
  - Department-specific announcements
  - Urgent alerts vs informational
  - Announcement scheduling
  - Expiry date for announcements
  - Read confirmation tracking
- Shift handoff communication:
  - Nursing shift handoff notes
  - Physician sign-out
  - Department handover
  - Critical patient information
  - Pending tasks and follow-ups
- Emergency notification system:
  - Code blue/red/yellow alerts
  - Mass casualty incident notification
  - Evacuation announcement
  - All-clear signals
  - Role-based alert routing
- Doctor paging system:
  - On-call physician directory
  - Paging by specialty
  - Urgent vs routine page
  - Page acknowledgment
  - Call-back number
- Clinical consultation requests:
  - Specialty consultation requests
  - Urgency level
  - Reason for consultation
  - Consultation response
  - Time tracking
- Task assignment and follow-up:
  - Task creation and assignment
  - Due date and priority
  - Task progress updates
  - Task completion confirmation
  - Overdue task alerts
- Hospital directory:
  - Staff contact information
  - Department phone extensions
  - On-call schedules
  - Escalation lists
- Incident notification:
  - Incident reporting
  - Incident response team notification
  - Incident updates
  - All-clear confirmation
- Mobile app for staff:
  - iOS and Android apps
  - Push notifications
  - Offline message queue
  - Location services (find nearest colleague)
  - Voice and video calling
- Integration with existing systems:
  - EMR integration for patient-related communication
  - Nurse call system integration
  - Overhead paging system
  - Email gateway
- Compliance and security:
  - HIPAA/privacy compliant messaging
  - End-to-end encryption
  - Message retention policy
  - Audit logs
  - Prohibition of PHI via non-secure channels
Generate communication analytics (response times, message volume)
```

### 32.4 Multi-Facility Management
```
Implement enterprise features for hospital groups:
- Facility master data:
  - Hospital network configuration
  - Facility profiles (type, size, specialties)
  - Geographic locations
  - Facility hierarchy
- Centralized patient lookup:
  - Cross-facility patient search
  - Unified medical record number
  - Patient location finder
  - Visit history across facilities
  - Shared allergies and medications list
- Inter-facility patient transfer:
  - Transfer request workflow
  - Bed availability checking at receiving facility
  - Transfer acceptance/rejection
  - Transfer documentation
  - Ambulance coordination
  - Medical records transfer
  - Billing and insurance handoff
- Centralized master data:
  - Unified ICD-10, ICD-9-CM codes
  - Drug formulary across facilities
  - Lab test catalog harmonization
  - Radiology procedure catalog
  - Standard charge master
- Shared resource booking:
  - Specialist consultation booking across facilities
  - Equipment sharing (mobile MRI, cath lab)
  - Operating room sharing
  - Telemedicine consultation between facilities
- Centralized procurement:
  - Group purchasing organization (GPO)
  - Bulk purchasing for network
  - Vendor contract negotiation
  - Inventory optimization across facilities
- Corporate-level analytics:
  - Consolidated financial reporting
  - Network-wide KPI dashboards
  - Benchmark between facilities
  - Patient flow analysis
  - Revenue cycle management at corporate level
- Centralized billing and collections:
  - Corporate billing office
  - Accounts receivable aging across facilities
  - Collection agency coordination
  - Payment posting for multiple locations
- Quality and safety reporting:
  - Aggregate adverse events
  - Network-wide infection rates
  - Sentinel event investigation
  - Best practice sharing
  - Quality improvement initiatives
- Unified patient portal:
  - Single login for all facilities
  - Access to records from any facility in network
  - Appointment booking at preferred location
  - Billing access across facilities
- Staff mobility:
  - Staff credentialing valid across network
  - Cross-facility privilege assignment
  - Floating staff management
  - Locum tenens coordination
- Data sharing and interoperability:
  - HL7 interfaces between facilities
  - FHIR API for data exchange
  - Centralized data warehouse
  - Enterprise master patient index (EMPI)
  - Consent management for data sharing
- Corporate administration:
  - Centralized user management
  - Role-based access across facilities
  - Audit and compliance oversight
  - Policy and procedure standardization
Generate enterprise reports and executive dashboards
```

---

## Appendix A: Critical Indonesian Requirements Checklist

### Must-Have Integrations
```
□ BPJS VClaim Services
□ SATUSEHAT OAuth2 and FHIR
□ INA-CBGs for claim grouping
□ DUKCAPIL for NIK verification (when available)
□ P-Care for primary care facilities
□ Aplicares for quality monitoring
□ SISRUTE for referral system
```

### Regulatory Reports
```
□ RL1 - Patient Data
□ RL2 - Disease and Death Data  
□ RL3 - Healthcare Service Data
□ RL4 - Visit and Disease Details
□ RL5 - Quarterly Disease List
□ Monthly BPJS reports
□ SATUSEHAT compliance reports
```

### Indonesian-Specific Features
```
□ NIK validation (16 digits)
□ BPJS card integration
□ Religion data (required)
□ Rujukan Berjenjang support
□ Formularium Nacional compliance
□ Indonesian language support
□ Local payment methods (QRIS, OVO, GoPay)
```

---

## Appendix B: Technology Stack Quick Reference (Updated 2025)

### Backend
- **Framework**: Spring Boot 3.4.1
- **Language**: Java 21 LTS
- **Database**: PostgreSQL 16.6
- **Cache**: Redis 7.4.x
- **Message Queue**: RabbitMQ 3.13.x
- **File Storage**: MinIO (latest stable)
- **Migration**: Flyway 10.x
- **API Docs**: SpringDoc OpenAPI 2.x

### Frontend
- **Framework**: React 18.3.x (stay on 18.x, NOT 19)
- **Language**: TypeScript 5.7.x
- **Build Tool**: Vite 5.4.x (NOT 6.x yet)
- **UI**: Tailwind CSS 4.x
- **State Management**: Redux Toolkit 2.x
- **Server State**: TanStack Query 5.x (formerly React Query)
- **Forms**: React Hook Form 7.x
- **Validation**: Zod 3.x
- **HTTP Client**: Axios latest
- **Testing**: Vitest + React Testing Library
- **E2E**: Playwright latest

### DevOps
- **OS**: Ubuntu 24.04 LTS (or 22.04 LTS acceptable)
- **Container**: Docker 27.x
- **Orchestration**: Docker Compose 2.x (Kubernetes ready)
- **CI/CD**: GitHub Actions / GitLab CI
- **Monitoring**: Prometheus + Grafana 11.x
- **Logging**: ELK Stack 8.x
- **APM**: Micrometer + Prometheus

### Integration Standards
- **Healthcare**: FHIR R4 (HAPI FHIR 7.x), HL7 v2.5 (HAPI HL7v2)
- **Imaging**: DICOM (DCM4CHE 5.x)
- **Documents**: PDF generation (Apache PDFBox or iText)
- **APIs**: REST with OpenAPI 3.0

---

## Final Notes

This guide represents a **COMPLETE** journey from zero to a production-ready, enterprise-grade HMS for Indonesian hospitals. This is now the **MOST COMPREHENSIVE** HMS development guide with extensive coverage of all hospital operations.

### Complete Statistics:

- **32 Phases** of development (expanded from 24)
- **150+ detailed prompts** covering all hospital operations
- **100% coverage** of clinical, administrative, financial, regulatory, and support functions
- **Complete Indonesian regulatory compliance** requirements
- **Production-ready** with all critical systems

### Complete Module Coverage:

**✅ Core Clinical Modules (Phases 1-10):**
- Patient Management & Registration
- Clinical Documentation (SOAP, ICD-10, ICD-9-CM)
- BPJS & SATUSEHAT Integration
- Billing & Claims Management
- Pharmacy (e-Prescribing, Inventory, Dispensing)
- Laboratory (Orders, Results, LIS Integration)
- Radiology (PACS, DICOM Integration)

**✅ Advanced Clinical Services (Phases 11-14):**
- Workforce Management (STR/SIP, Payroll, Credentialing)
- Surgery/Operating Theater (Pre/Intra/Post-operative)
- Newborn/Perinatology (Maternal Care, NICU, Breastfeeding)
- Integration Hub (Medical Devices, WhatsApp, Payment Gateways)

**✅ Security & Infrastructure (Phases 15-22):**
- Authentication & Authorization (JWT, RBAC)
- Reporting & Analytics (BI Dashboards)
- React Frontend (PWA, Real-time Features)
- Testing Strategy (Unit, Integration, E2E)
- Docker Deployment & CI/CD
- Production Deployment & Monitoring
- Post-Production Optimization

**✅ Advanced Features & Compliance (Phases 23-24):**
- Mobile App Development
- Telemedicine Integration
- AI/ML Features
- KARS Accreditation & ISO Certification

**✅ Critical Care & Support Services (Phase 25):** ⭐ NEW
- ICU/Critical Care Management
- Hemodialysis Unit
- Blood Bank & Transfusion Service
- CSSD (Central Sterile Supply Department)

**✅ Medical Records & Quality Management (Phase 26):** ⭐ NEW
- Medical Records Department (MRMK)
- Infection Prevention & Control (PPIRS)
- Enhanced Incident Reporting System
- Clinical Pathway Management
- Medication Reconciliation

**✅ Indonesian Regulatory Compliance (Phase 27):** ⭐ NEW
- Digital Signature Integration (Privy, Digisign, VIDA)
- E-Rekam Medis Compliance (Permenkes No. 24/2022)
- SIRS Integration (Ministry of Health)
- DUKCAPIL NIK Verification
- SISRUTE Referral System
- Aplicares Quality Monitoring
- P-Care Primary Care Integration

**✅ Financial & Procurement System (Phase 28):** ⭐ NEW
- General Ledger & Chart of Accounts
- Accounts Receivable Management
- Accounts Payable Management
- Fixed Asset Management
- Procurement & Purchase Orders
- Indonesian Tax System (e-Faktur, e-Billing, PPh)

**✅ Patient Engagement & Digital Access (Phase 29):** ⭐ NEW
- Comprehensive Patient Portal
- Advanced Appointment System (Multi-channel)
- Mobile JKN Integration
- Patient Feedback & Satisfaction System
- Health Education & Engagement Platform

**✅ Specialized Clinical Services (Phase 30):** ⭐ NEW
- Medical Check-Up (MCU) Packages
- Dental Clinic Management
- Home Care Services
- Oncology Management
- Cardiology/Cardiac Cath Lab
- Rehabilitation/Physiotherapy

**✅ Hospital Operations Management (Phase 31):** ⭐ NEW
- Asset Management System
- Laundry & Linen Management
- Housekeeping Management
- Food Service/Dietary Management
- Mortuary/Jenazah Management
- Visitor Management System

**✅ Support Services & Infrastructure (Phase 32):** ⭐ NEW
- Nutrition/Dietetics Service
- Document Management System
- Internal Communication Platform
- Multi-Facility Management (for hospital groups)

---

### What Makes This Guide Complete:

**1. Clinical Completeness:**
- All major clinical specialties covered
- All support services included
- All ancillary services implemented
- All critical care units addressed

**2. Regulatory Completeness:**
- Full BPJS integration (VClaim, SEP, Claims)
- Complete SATUSEHAT implementation (FHIR R4)
- All Indonesian government systems (SIRS, SISRUTE, Aplicares, P-Care)
- Digital signature compliance (UU ITE)
- E-Rekam Medis full compliance (Permenkes 24/2022)

**3. Financial Completeness:**
- Full accounting system (GL, AP, AR)
- Complete procurement system
- Indonesian tax compliance (e-Faktur, e-Billing, all PPh types)
- Fixed asset management
- Revenue cycle management

**4. Operational Completeness:**
- All hospital support services
- All operational workflows
- All quality and safety systems
- All administrative functions

**5. Technology Completeness:**
- Modern tech stack (Spring Boot 3.4, Java 21, React 18, PostgreSQL 16)
- All integrations (DICOM, HL7, FHIR, payment gateways)
- Complete security implementation
- Production-ready deployment

---

### Implementation Approach:

**Phase Selection Based on Hospital Type:**

**Small Clinic/Primary Care:**
- Implement Phases 1-10, 15-17, 27 (Core + Security + Basic Compliance)
- **Timeline:** 6-8 months
- **Capacity:** 20-50 patients/day

**Medium Hospital (Type C/D):**
- Add Phases 11-14, 25-26, 28-29 (Workforce + Advanced Clinical + Quality + Finance + Patient Engagement)
- **Timeline:** 10-14 months
- **Capacity:** 100-200 patients/day

**Large Hospital (Type B/A):**
- All Phases 1-32 (Complete System)
- **Timeline:** 16-20 months
- **Capacity:** 200-500+ patients/day

**Hospital Group/Network:**
- All Phases + Multi-facility emphasis (Phase 32.4)
- **Timeline:** 20-24 months
- **Capacity:** Multiple facilities

---

### Development Best Practices:

**For Each Phase:**
1. ✅ Read the prompt carefully
2. ✅ Implement all listed features
3. ✅ Write comprehensive tests (80%+ coverage)
4. ✅ Document all APIs and workflows
5. ✅ Perform security review
6. ✅ Test with realistic Indonesian data
7. ✅ Get stakeholder approval before proceeding

**Testing Requirements:**
- Unit tests for all business logic
- Integration tests for all external systems
- E2E tests for critical workflows
- Performance tests for 200+ concurrent users
- Security penetration testing
- BPJS/SATUSEHAT sandbox testing

**Deployment Checklist:**
- ✅ All KARS accreditation requirements met
- ✅ All Indonesian regulatory requirements compliant
- ✅ All data encrypted (at rest and in transit)
- ✅ Comprehensive backup and disaster recovery
- ✅ Complete user training materials (Indonesian language)
- ✅ 24/7 support team prepared
- ✅ SLA agreements defined

---

### Success Criteria:

Your HMS implementation is **COMPLETE** when:

1. ✅ All 32 phases implemented
2. ✅ All Indonesian regulatory integrations working
3. ✅ KARS accreditation requirements met
4. ✅ 200+ concurrent users supported
5. ✅ < 2 second page load times
6. ✅ 99.9% uptime achieved
7. ✅ All RL reports auto-generated
8. ✅ BPJS claims processed successfully
9. ✅ Digital signatures legally valid
10. ✅ User satisfaction > 85%

---

### Ongoing Maintenance:

**Monthly:**
- Security updates for all dependencies
- Performance optimization review
- User feedback analysis
- BPJS/SATUSEHAT integration health check

**Quarterly:**
- Feature enhancements based on user needs
- Regulatory compliance review
- Disaster recovery testing
- Staff training refresher

**Annually:**
- Technology stack upgrade review
- KARS/ISO recertification preparation
- Strategic planning for new features
- Major version release planning

---

**Remember:**
- This guide provides prompts, not finished code - you still need to implement carefully
- Test extensively with real Indonesian healthcare data
- Involve actual healthcare professionals in UAT
- Always comply with Indonesian regulations (Permenkes, UU ITE, etc.)
- Security and patient safety are paramount
- Keep the system updated and maintained

**Final Advice:**
This is a **massive project**. Don't try to build everything at once. Start with Phase 1-10 for MVP, then incrementally add features based on your hospital's priority. Use agile methodology, deliver in sprints, and get frequent feedback from actual users.

Good luck with your HMS development! 🏥

---

---

## Appendix C: BPJS Trust Mark Integration Technical Details

### C.1 BPJS Authentication Implementation
```
Implement BPJS Trust Mark authentication system:

1. Signature Generation Class:
   - Create BpjsSignatureGenerator with HMAC-SHA256 implementation
   - Use javax.crypto.Mac and javax.crypto.spec.SecretKeySpec
   - Generate timestamp: System.currentTimeMillis() / 1000 (UTC seconds)
   - Create signature data: consumerID + "&" + timestamp
   - Sign with consumer secret using HMAC-SHA256
   - Encode result with Base64
   - URL encode if needed for specific endpoints

2. HTTP Headers Configuration:
   - X-cons-id: Store in application properties (encrypted)
   - X-timestamp: Generate fresh for each request
   - X-signature: Generate using signature class
   - user_key: Store in application properties (encrypted)
   - Content-Type: application/json for all requests

3. Response Decryption:
   - Some BPJS responses are encrypted with AES
   - Decrypt using consumer secret as key
   - Parse decrypted JSON response
   - Handle both encrypted and plain responses

4. Environment Configuration:
   Development:
   - VClaim: https://apijkn.bpjs-kesehatan.go.id/vclaim-rest-dev/
   - Antrean: https://apijkn.bpjs-kesehatan.go.id/antreanrs-dev/

   Production:
   - VClaim: https://apijkn.bpjs-kesehatan.go.id/vclaim-rest/
   - Antrean: https://apijkn.bpjs-kesehatan.go.id/antreanrs/

Include comprehensive error handling for authentication failures
```

### C.2 VClaim Complete Service Implementation
```
Build comprehensive VClaim service layer:

1. Peserta (Participant) Services:
   GET /Peserta/nokartu/{noKartu}/tglSEP/{tglSEP}
   - Validate participant by card number
   - Check eligibility on specific date
   - Return: participant details, class, status, COB info

   GET /Peserta/nik/{nik}/tglSEP/{tglSEP}
   - Search participant by NIK
   - Validate eligibility
   - Return: same as card number search

2. Rujukan (Referral) Services:
   GET /Rujukan/{noRujukan}
   - Get referral details by number
   - Validate referral validity period
   - Check if already used

   GET /Rujukan/List/Peserta/{noKartu}
   - Get all active referrals for participant
   - Support multi rujukan

   GET /Rujukan/RS/{noRujukan}
   - Get hospital referral (rujukan RS)
   - For referral between hospitals

3. SEP (Surat Eligibilitas Peserta) Services:
   POST /SEP/2.0/insert
   Request body structure:
   {
     "request": {
       "t_sep": {
         "noKartu": "0001234567890",
         "tglSep": "2025-01-20",
         "ppkPelayanan": "0301R001",
         "jnsPelayanan": "2", // 1=Rawat Inap, 2=Rawat Jalan
         "klsRawat": {
           "klsRawatHak": "3",
           "klsRawatNaik": "",
           "pembiayaan": "",
           "penanggungJawab": ""
         },
         "noMR": "123456",
         "rujukan": {
           "asalRujukan": "1", // 1=FKTP, 2=RS
           "tglRujukan": "2025-01-15",
           "noRujukan": "030101012501150001",
           "ppkRujukan": "03010101"
         },
         "catatan": "",
         "diagAwal": "A00.1",
         "poli": {
           "tujuan": "INT",
           "eksekutif": "0"
         },
         "cob": {
           "cob": "0"
         },
         "katarak": {
           "katarak": "0"
         },
         "jaminan": {
           "lakaLantas": "0",
           "noLP": "",
           "penjamin": {
             "tglKejadian": "",
             "keterangan": "",
             "suplesi": {
               "suplesi": "0",
               "noSepSuplesi": "",
               "lokasiLaka": {
                 "kdPropinsi": "",
                 "kdKabupaten": "",
                 "kdKecamatan": ""
               }
             }
           }
         },
         "tujuanKunj": "0",
         "flagProcedure": "",
         "kdPenunjang": "",
         "assesmentPel": "",
         "skdp": {
           "noSurat": "",
           "kodeDPJP": ""
         },
         "dpjpLayan": "",
         "noTelp": "081234567890",
         "user": "SIMRS"
       }
     }
   }

   PUT /SEP/2.0/update
   - Update existing SEP data
   - Limited fields can be updated

   DELETE /SEP/2.0/delete
   - Cancel/delete SEP
   - Requires user and reason

4. Monitoring Services:
   GET /Monitoring/Klaim/Tanggal/{tglPulang}/JnsPelayanan/{jnsPelayanan}/Status/{status}
   - Monitor claims by date and status
   - Status: 1=Proses, 2=Disetujui, 3=Ditolak

   GET /Monitoring/HistoriPelayanan/NoKartu/{noKartu}/TglMulai/{tglMulai}/TglAkhir/{tglAkhir}
   - Patient service history
   - Track all visits and claims

5. Referensi (Reference) Services:
   GET /referensi/diagnosa/{keyword}
   - Search ICD-10 diagnoses
   - Keyword-based search

   GET /referensi/poli/{poli}
   - Get polyclinic list
   - Hospital-specific poly codes

   GET /referensi/faskes/{keyword}/{jnsFaskes}
   - Search healthcare facilities
   - Type: 1=Puskesmas, 2=Rumah Sakit

Include retry logic and comprehensive error handling
```

### C.3 Antrean RS (Queue) Complete Implementation
```
Implement complete queue management with BPJS:

1. Queue Addition Service:
   POST /antrean/add
   Request Structure:
   {
     "kodebooking": "16032021A001",
     "jenispasien": "JKN", // JKN or NON-JKN
     "nomorkartu": "0001234567890",
     "nik": "3212345678900001",
     "nohp": "081234567890",
     "kodepoli": "001",
     "namapoli": "Poli Umum",
     "pasienbaru": 0, // 0=old, 1=new
     "norm": "123456",
     "tanggalperiksa": "2025-01-20",
     "kodedokter": 12345,
     "namadokter": "Dr. Budi",
     "jampraktek": "08:00-12:00",
     "jeniskunjungan": 1, // 1=Rujukan FKTP, 2=Rujukan Internal, 3=Kontrol, 4=Rujukan Antar RS
     "nomorreferensi": "030101012501150001",
     "nomorantrean": "A-001",
     "angkaantrean": 1,
     "estimasidilayani": 1642737600000, // Unix timestamp in milliseconds
     "sisakuotajkn": 5,
     "kuotajkn": 30,
     "sisakuotanonjkn": 5,
     "kuotanonjkn": 30,
     "keterangan": "Peserta hadir 30 menit sebelum pelayanan"
   }

2. Task ID Update Service:
   POST /antrean/updatewaktu
   Request for each task:
   {
     "kodebooking": "16032021A001",
     "taskid": 1, // 1-7, 99
     "waktu": 1642737600000 // timestamp when task completed
   }

   Task ID Reference:
   - 1: Patient arrival/check-in at hospital
   - 2: Start registration counter service
   - 3: End registration, start polyclinic queue
   - 4: Doctor consultation started
   - 5: Doctor consultation ended, pharmacy queue (if needed)
   - 6: Prescription ready at pharmacy
   - 7: Billing completed
   - 99: Task doesn't run (patient no-show, cancelled)

3. Queue Cancellation:
   POST /antrean/batal
   {
     "kodebooking": "16032021A001",
     "keterangan": "Pasien berhalangan hadir"
   }

4. Dashboard Monitoring:
   GET /dashboard/waktutunggu/tanggal/{tanggal}/waktu/{waktu}
   - Daily dashboard data
   - Parameter waktu: "rs" or "server"

   Response includes:
   - Total queue count
   - Served patients
   - Waiting patients
   - Average waiting time per task
   - Service performance metrics

5. Reference Services:
   GET /ref/poli
   - Get list of polyclinics registered in BPJS

   GET /ref/dokter
   - Get list of doctors registered in BPJS

   GET /jadwaldokter/kodepoli/{kodepoli}/tanggal/{tanggal}
   - Get doctor schedule for specific poly and date

Implement with proper queue status synchronization
```

### C.4 Aplicares Bed Management Implementation
```
Build real-time bed availability system for BPJS:

1. Room Class Reference:
   GET /rest/ref/kelas
   Response structure:
   {
     "metadata": {
       "code": 1,
       "message": "OK",
       "totalitems": 16
     },
     "response": {
       "list": [
         {"kodekelas": "VVP", "namakelas": "VVIP"},
         {"kodekelas": "VIP", "namakelas": "VIP"},
         {"kodekelas": "1", "namakelas": "Kelas 1"},
         {"kodekelas": "2", "namakelas": "Kelas 2"},
         {"kodekelas": "3", "namakelas": "Kelas 3"}
       ]
     }
   }

2. Update Bed Availability:
   POST /rest/bed/update/{kodeppk}
   Request body:
   {
     "kodekelas": "3",
     "koderuang": "RU001",
     "namaruang": "Melati",
     "kapasitas": 20,
     "tersedia": 5,
     "tersediapria": 2,
     "tersediawanita": 3,
     "tersediapriawanita": 0
   }

   Update frequency: Real-time on admission/discharge

3. Create New Room:
   POST /rest/bed/create/{kodeppk}
   - Same structure as update
   - Use when adding new rooms/wards

4. Read Hospital Bed Status:
   GET /rest/bed/read/{kodeppk}/1/100
   - Get all room availability
   - Parameters: hospital code, page, limit
   - Returns current bed status for all rooms

5. Delete Room:
   DELETE /rest/bed/delete/{kodeppk}
   Request body:
   {
     "kodekelas": "3",
     "koderuang": "RU001"
   }

Implement automatic sync with admission/discharge system
```

### C.5 Apotek (Pharmacy) BPJS Integration
```
Implement BPJS pharmacy services:

1. Drug Reference Services:
   GET /referensi/dpho/list
   - Get DPHO (Daftar Plafon Harga Obat) list
   - Contains maximum prices for BPJS drugs

   GET /referensi/obat/{keyword}
   - Search drugs in formulary
   - Check if covered by BPJS

2. PRB (Program Rujuk Balik) Management:
   POST /PRB/insert
   - Create PRB for chronic disease patients
   - Allows 3-month drug supply

   Request includes:
   - SEP number
   - Diagnosis (must be PRB-eligible)
   - Drug list with quantities
   - Doctor DPJP code

3. Drug Claim Validation:
   POST /klaim/obat/validasi
   - Validate drug claims against formulary
   - Check dosage limits
   - Verify prescription appropriateness

4. Non-Formulary Drug Approval:
   POST /approval/obat/nonformularium
   - Request approval for non-formulary drugs
   - Include clinical justification
   - Track approval status

Ensure formulary compliance checking in prescribing module
```

### C.6 iCare JKN History Integration
```
Implement patient history validation with iCare:

1. History Validation Endpoint:
   POST /wsihs/api/rs/validate
   Request:
   {
     "param": "0001234567890", // BPJS card number
     "kodedokter": 12345        // Doctor code
   }

   Response:
   {
     "response": {
       "url": "https://dvlp.bpjs-kesehatan.go.id/ihs/history?token=e6b610b4-2960-46a3-8420-de879756dce3"
     },
     "metaData": {
       "code": 200,
       "message": "Sukses"
     }
   }

2. Token-Based Access:
   - URL contains temporary token
   - Token expires after certain time
   - Display in iframe or new window
   - Shows complete patient history across all facilities

3. Implementation Flow:
   - Call validation API during patient registration
   - Store token temporarily
   - Provide access to authorized doctors
   - Log access for audit purposes

Include proper error handling for expired tokens
```

### C.7 Error Handling & Common Issues
```
Handle common BPJS integration errors:

1. Authentication Errors:
   Code 401: Invalid signature
   - Check timestamp is in UTC
   - Verify consumer secret is correct
   - Ensure signature generation matches BPJS spec

   Code 402: Invalid consumer ID
   - Verify consumer ID in configuration
   - Check if service is registered

2. Business Logic Errors:
   Code 201: SEP already exists
   - Check for duplicate submission
   - Verify if patient already has active SEP

   Code 202: Referral expired
   - Check referral validity period (90 days)
   - Advise patient to get new referral

   Code 203: Participant not active
   - Check participant status
   - Verify payment status with BPJS

3. Data Validation Errors:
   Code 301: Invalid diagnosis code
   - Validate against ICD-10 master
   - Use exact code, not partial

   Code 302: Invalid procedure code
   - Validate against ICD-9-CM master
   - Check if procedure matches diagnosis

4. Retry Strategy:
   - Implement exponential backoff
   - Max 3 retries for timeout errors
   - Log all attempts for debugging
   - Queue failed requests for manual review

5. Monitoring & Alerting:
   - Track API response times
   - Monitor error rates by endpoint
   - Alert on authentication failures
   - Daily reconciliation with BPJS

Include comprehensive logging for all BPJS transactions
```

### C.8 Testing Strategy for BPJS Integration
```
Comprehensive testing approach for BPJS services:

1. Development Environment Testing:
   - Use BPJS development endpoints
   - Test with provided test data
   - Verify all response scenarios

2. Test Scenarios:
   Participant Verification:
   - Valid active participant
   - Inactive participant
   - Non-existent participant
   - COB (other insurance) cases

   SEP Creation:
   - Outpatient SEP
   - Inpatient SEP
   - Emergency SEP
   - SEP with control letter (SKDP)

   Referral Validation:
   - Valid referral
   - Expired referral
   - Used referral
   - Multiple referrals

3. Integration Testing:
   - End-to-end patient flow
   - Queue to billing process
   - Claim submission workflow
   - Error recovery scenarios

4. Performance Testing:
   - Load test with expected daily volume
   - Measure API response times
   - Test timeout handling
   - Verify retry mechanisms

5. UAT with BPJS:
   - Schedule UAT session with BPJS team
   - Test all integration points
   - Get certification for production
   - Document any deviations

Include test data management and rollback procedures
```

### C.9 Production Deployment Checklist
```
BPJS Trust Mark production readiness:

1. Credentials Management:
   □ Production consumer ID obtained
   □ Production consumer secret secured
   □ User key activated
   □ Credentials encrypted in config

2. Endpoint Configuration:
   □ All production URLs configured
   □ Timeout settings optimized
   □ Retry policies configured
   □ Circuit breaker implemented

3. Monitoring Setup:
   □ API monitoring dashboard
   □ Error rate alerts
   □ Response time tracking
   □ Daily transaction reports

4. Compliance Requirements:
   □ Aplicares bed updates automated
   □ Antrean task IDs implemented
   □ Queue synchronization working
   □ All reference data synced

5. Business Process Validation:
   □ Registration flow tested
   □ SEP creation validated
   □ Queue management verified
   □ Claim submission working

6. Support Readiness:
   □ Staff trained on BPJS integration
   □ Troubleshooting guide prepared
   □ BPJS contact list available
   □ Escalation process defined

7. Data Security:
   □ All data encrypted in transit
   □ Sensitive data masked in logs
   □ Audit trail implemented
   □ Access control configured

8. Backup Procedures:
   □ Manual fallback process defined
   □ Offline capability for critical functions
   □ Data recovery procedures
   □ Business continuity plan

Complete all items before go-live
```

---

## Appendix D: Quick Reference - BPJS API Endpoints

### VClaim Services
```
# Peserta (Participant)
GET /Peserta/nokartu/{noKartu}/tglSEP/{tglSEP}
GET /Peserta/nik/{nik}/tglSEP/{tglSEP}

# Rujukan (Referral)
GET /Rujukan/{noRujukan}
GET /Rujukan/List/Peserta/{noKartu}
GET /Rujukan/RS/{noRujukan}

# SEP
POST /SEP/2.0/insert
PUT /SEP/2.0/update
DELETE /SEP/2.0/delete

# Monitoring
GET /Monitoring/Klaim/Tanggal/{tglPulang}/JnsPelayanan/{jnsPelayanan}/Status/{status}
GET /Monitoring/HistoriPelayanan/NoKartu/{noKartu}/TglMulai/{tglMulai}/TglAkhir/{tglAkhir}

# Rencana Kontrol
POST /RencanaKontrol/insert
POST /RencanaKontrol/update
DELETE /RencanaKontrol/delete

# PRB (Program Rujuk Balik)
POST /PRB/insert
PUT /PRB/update
DELETE /PRB/delete
```

### Antrean RS Services
```
# Reference
GET /ref/poli
GET /ref/dokter
GET /jadwaldokter/kodepoli/{kodepoli}/tanggal/{tanggal}

# Queue Management
POST /antrean/add
POST /antrean/updatewaktu
POST /antrean/batal

# Monitoring
GET /antrean/pendaftaran/tanggal/{tanggal}
GET /antrean/pendaftaran/kodebooking/{kodebooking}
GET /dashboard/waktutunggu/tanggal/{tanggal}/waktu/{waktu}
```

### Aplicares Services
```
# Bed Management
GET /rest/ref/kelas
POST /rest/bed/update/{kodeppk}
POST /rest/bed/create/{kodeppk}
GET /rest/bed/read/{kodeppk}/{start}/{limit}
DELETE /rest/bed/delete/{kodeppk}
```

### iCare JKN
```
# History Validation
POST /wsihs/api/rs/validate
```

---

*Last Updated: January 2025 (Version 2.0 - With Complete BPJS Trust Mark Integration)*
*Created for: Yudha HMS Project*
*Target: Indonesian Healthcare Facilities*
*Deployment: On-Premise / Private Cloud*
*BPJS Trust Mark Compliant*
