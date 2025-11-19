# Emergency Department (IGD) Registration System

## Overview
Comprehensive emergency registration system with triage, ambulance tracking, police case handling, and automatic conversion to inpatient admission.

## Features Implemented

### 1. **Triage Level Assignment** ✅
- **ESI-Based Classification** (Emergency Severity Index 1-5)
  - **Level 1**: Resuscitation (Immediate life-threatening)
  - **Level 2**: Emergent (High risk, severe symptoms)
  - **Level 3**: Urgent (Stable, needs multiple resources)
  - **Level 4**: Less Urgent (Needs one resource)
  - **Level 5**: Non-Urgent (No resources needed)

- **Color-Coded Triage Levels**:
  - **RED**: Critical - Immediate attention
  - **YELLOW**: Urgent - Prompt attention
  - **GREEN**: Non-Urgent - Can wait
  - **WHITE**: Minor - Low acuity
  - **BLACK**: Deceased - No signs of life

- **Priority Scoring**: 1 (Highest) to 5 (Lowest)

### 2. **Fast-Track Registration for Unknown/Unconscious Patients** ✅
- `isUnknownPatient` flag for unidentified patients
- `unknownPatientIdentifier`: AUTO-generated (e.g., "UNKNOWN-20250119-001")
- `temporaryName`: User-friendly labels ("Unknown Male #1", "Victim #2")
- `estimatedAge` and `estimatedGender` for demographics
- **Minimal required data**: Chief complaint + Triage level only
- **Later identification**: Can be linked to patient record when identified

### 3. **Police Case Marking** ✅
- `isPoliceCase` boolean flag
- **Police Case Types**:
  - `ACCIDENT`: Traffic/motor vehicle accidents
  - `VIOLENCE`: Assault, battery
  - `SUSPICIOUS_DEATH`: Unexplained deaths
  - `OTHER`: Other police matters
- **Police Documentation**:
  - Police report number
  - Police station name
  - Officer name and contact

### 4. **Ambulance Arrival Tracking** ✅
- **Ambulance Details**:
  - Type: GOVERNMENT, PRIVATE, HOSPITAL
  - Ambulance number/plate
  - Origin hospital/location
  - Paramedic name and phone
- **Arrival Mode tracking**: WALK_IN, AMBULANCE, POLICE, REFERRAL, TRANSFER
- **Arrival time** logging for metrics

### 5. **Trauma/Accident Case Handling** ✅
- `isTraumaCase` flag
- **Trauma Types**: MOTOR_VEHICLE, FALL, BURN, PENETRATING, BLUNT, OTHER
- **Accident Details**:
  - Location
  - Time of accident
  - Mechanism of injury (detailed description)

### 6. **Auto-Conversion to Inpatient** ✅
- `convertToInpatient()` method
- Links to `inpatient_admission` table
- Tracks conversion time
- Auto-updates status to ADMITTED
- Preserves ER data for continuity of care

### 7. **Critical Patient Prioritization** ✅
- `isCritical` flag auto-set for RED/BLACK triage
- **Red Flags Detection**:
  - Chest pain
  - Difficulty breathing
  - Altered consciousness
  - Severe bleeding
  - Seizures
  - Poisoning
- **Automatic Prioritization**:
  - Critical cases flagged in queries
  - Door-to-triage and door-to-doctor time tracking
  - ER zone assignment (RED_ZONE, RESUS_ROOM for critical)

## Database Schema

### Tables Created
1. **`emergency_registration`** (68 columns)
   - Registration details
   - Patient information (known/unknown)
   - Triage data
   - Ambulance details
   - Police case information
   - Trauma/accident details
   - Medical team assignment
   - Status and disposition
   - Timing metrics
   - Payment information

2. **`triage_assessment`** (60 columns)
   - Detailed ESI-based triage
   - Vital signs (BP, HR, RR, Temp, SpO2, Glucose)
   - GCS (Glasgow Coma Scale) assessment
   - Pain assessment
   - Respiratory/cardiovascular evaluation
   - Red flags identification
   - Resource needs prediction
   - Isolation requirements
   - Re-triage support

### Key Indexes
- Emergency number (unique)
- Patient ID
- Status (for active case queries)
- Triage level and priority
- Police cases (filtered index)
- Unknown patients (filtered index)

## Entity Classes

### 1. EmergencyRegistration.java
**Location**: `src/main/java/com/yudha/hms/registration/entity/EmergencyRegistration.java`

**Key Methods**:
```java
// Perform triage
public void performTriage(TriageLevel level, Integer priority, UUID nurseId, String nurseName)

// Convert to inpatient
public void convertToInpatient(UUID admissionId)

// Discharge from ER
public void discharge(String dispositionType, String notes)

// Calculate metrics
public void calculateDoorToTriageTime()
public void calculateTotalErTime()

// Status checks
public boolean isCriticalCase()
public boolean isPatientIdentified()
public String getPatientDisplayName()
```

### 2. TriageAssessment.java
**Location**: `src/main/java/com/yudha/hms/registration/entity/TriageAssessment.java`

**Key Methods**:
```java
// Calculate GCS
public void calculateGcsTotal()

// Risk assessment
public boolean hasCriticalRedFlags()
public boolean hasAbnormalVitals()

// ESI determination
public Integer determineESILevel()
```

### 3. Enums
- **TriageLevel**: RED, YELLOW, GREEN, WHITE, BLACK
- **ArrivalMode**: WALK_IN, AMBULANCE, POLICE, REFERRAL, TRANSFER
- **EmergencyStatus**: REGISTERED, TRIAGED, IN_TREATMENT, WAITING_RESULTS, ADMITTED, DISCHARGED, etc.

## Registration Number Format
**Pattern**: `ER-YYYYMMDD-NNNN`
**Example**: `ER-20250119-0001`

## Unknown Patient Identifier Format
**Pattern**: `UNKNOWN-YYYYMMDD-NNN`
**Example**: `UNKNOWN-20250119-001`

## Workflow Examples

### Standard Emergency Registration
```
1. Patient arrives → Register (arrival mode, chief complaint)
2. Triage nurse assessment → Assign triage level (RED/YELLOW/GREEN)
3. ER zone assignment → RED_ZONE/YELLOW_ZONE/GREEN_ZONE
4. Doctor assessment → IN_TREATMENT status
5. Disposition decision:
   - Admit to inpatient → convertToInpatient()
   - Discharge home → discharge("DISCHARGED_HOME")
   - Transfer → discharge("TRANSFERRED")
```

### Unknown/Unconscious Patient
```
1. Arrive via ambulance/police
2. Fast-track registration:
   - isUnknownPatient = true
   - temporaryName = "Unknown Male #1"
   - estimatedAge = 40
   - estimatedGender = "MALE"
   - chiefComplaint = "Found unconscious on street"
3. Immediate triage → RED level (unconscious)
4. Resuscitation room assignment
5. Later: Link to patient record when identified
```

### Police/Trauma Case
```
1. Accident victim arrives
2. Mark isPoliceCase = true, isTraumaCase = true
3. Record police details (officer, report number)
4. Document trauma (mechanism of injury, accident details)
5. Triage based on injuries
6. Police follow-up tracking
```

## Critical Patient Handling

### Automatic Critical Flagging
- **RED triage level** → isCritical = true
- **GCS < 8** → Level 1 ESI
- **Multiple red flags** → Immediate attention
- **Abnormal vitals** → Level 2 ESI

### Priority Queue
```sql
SELECT * FROM emergency_registration
WHERE status IN ('REGISTERED', 'TRIAGED', 'IN_TREATMENT')
ORDER BY is_critical DESC, triage_priority ASC, registration_time ASC;
```

## Next Steps (To Be Implemented)

### DTOs
- [ ] EmergencyRegistrationRequest
- [ ] EmergencyRegistrationResponse
- [ ] TriageAssessmentRequest
- [ ] AmbulanceArrivalDto

### Repositories
- [ ] EmergencyRegistrationRepository
- [ ] TriageAssessmentRepository

### Services
- [ ] EmergencyRegistrationService
- [ ] TriageService

### Controllers
- [ ] EmergencyRegistrationController
- [ ] TriageController

### Endpoints
```
POST   /api/emergency/register          - Fast-track registration
POST   /api/emergency/{id}/triage       - Perform triage assessment
PUT    /api/emergency/{id}/admit        - Convert to inpatient
PUT    /api/emergency/{id}/discharge    - Discharge from ER
GET    /api/emergency/active            - Get all active ER patients
GET    /api/emergency/critical          - Get critical patients only
GET    /api/emergency/{id}              - Get emergency registration details
POST   /api/emergency/{id}/retriage     - Re-triage patient
```

## Key Performance Metrics

### Tracked Automatically
- **Door-to-Triage Time**: Arrival → Triage completion
- **Door-to-Doctor Time**: Arrival → Doctor assessment
- **Total ER Time**: Arrival → Disposition

### Reporting Capabilities
- Triage level distribution
- Average wait times by triage level
- Critical patient outcomes
- Ambulance arrival statistics
- Police case tracking

## Isolation/Infection Control

### Supported Features
- `requiresIsolation` flag
- **Isolation Types**: AIRBORNE, DROPLET, CONTACT, PROTECTIVE
- Suspected infection documentation
- Automatic zone assignment for infectious cases

## Integration Points

### With Inpatient System
- Auto-conversion creates `inpatient_admission` record
- ER data preserved for medical history
- Seamless transition for admitted patients

### With Patient Registry
- Links to existing patient records
- Supports unknown patient workflows
- Demographics update when identified

## Security & Compliance

### Audit Trail
- Created by / Updated by tracking
- Soft delete support
- Version control for concurrent updates

### Police Case Protection
- Filtered indexes for performance
- Special handling for legal cases
- Chain of custody documentation

---

**Status**: ✅ Database Schema Complete | ✅ Entity Classes Complete | 🔄 Services & Controllers In Progress

**Last Updated**: 2025-01-19
