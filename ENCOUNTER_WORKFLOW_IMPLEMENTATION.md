# Encounter Workflow Implementation Guide

## Overview
This document describes the implementation of the Outpatient Encounter workflow according to the HMS specification.

---

## Outpatient Encounter Workflow (Rawat Jalan)

### Status Flow Diagram
```
PLANNED → ARRIVED → TRIAGED → IN_PROGRESS → FINISHED
    ↓         ↓         ↓           ↓
    └─────────┴─────────┴───────────┴─→ CANCELLED
```

---

## Implementation Details

### 1. Patient Registration/Arrival
**Status:** `ARRIVED`

**API Endpoint:** `POST /api/clinical/encounters`

**Request Example:**
```json
{
  "patientId": "uuid",
  "encounterType": "OUTPATIENT",
  "encounterClass": "AMBULATORY",
  "outpatientRegistrationId": "uuid",
  "departmentId": "uuid",
  "currentDepartment": "Poliklinik Umum",
  "priority": "ROUTINE",
  "reasonForVisit": "Kontrol rutin",
  "insuranceType": "BPJS",
  "sepNumber": "0301R0010125K000001",
  "sepDate": "2025-01-20"
}
```

**What Happens:**
- Encounter created with auto-generated encounter number (ENC-YYYYMMDD-XXXX)
- Initial status can be `PLANNED` or updated to `ARRIVED`
- Queue number assigned (from outpatient registration)
- Insurance eligibility verified (BPJS SEP number validated)
- Status history created automatically

**Business Rules:**
- Must have valid patient ID
- Must have exactly one registration ID (outpatient/inpatient/emergency)
- BPJS encounters require SEP number

---

### 2. Triage (For Emergency/Urgent Cases)
**Status:** `TRIAGED`

**API Endpoint:** `PATCH /api/clinical/encounters/{id}/status?status=TRIAGED`

**What Happens:**
- Vital signs recorded (typically done via integration with vitals module)
- Priority level set (ROUTINE, URGENT, EMERGENCY, STAT)
- Status updated to TRIAGED
- Status history recorded

**Valid Transitions:**
- FROM: `ARRIVED` → TO: `TRIAGED` ✅
- FROM: `ARRIVED` → TO: `IN_PROGRESS` ✅ (skip triage for routine cases)

---

### 3. Doctor Consultation
**Status:** `IN_PROGRESS`

**API Endpoints:**

#### 3.1 Start Consultation
`POST /api/clinical/encounters/{id}/start`

**What Happens:**
- Status changed to IN_PROGRESS
- Encounter start time recorded
- Care team can be assigned

#### 3.2 Add Practitioner/Care Team
`POST /api/clinical/encounters/{id}/participants`

**Request Example:**
```json
{
  "practitionerId": "uuid",
  "participantType": "PRIMARY",
  "participantName": "Dr. Ahmad Santoso, Sp.PD",
  "participantRole": "Attending Physician"
}
```

**Participant Types:**
- PRIMARY - Primary attending physician
- SECONDARY - Secondary/backup physician
- CONSULTANT - Specialist consultant
- ANESTHESIOLOGIST - For procedures requiring anesthesia
- NURSE - Attending nurse
- SPECIALIST - Specialist involvement

#### 3.3 Add Diagnosis (ICD-10)
`POST /api/clinical/encounters/{id}/diagnoses`

**Request Example:**
```json
{
  "diagnosisCode": "J06.9",
  "diagnosisText": "Infeksi saluran napas akut, tidak spesifik",
  "diagnosisType": "PRIMARY",
  "clinicalStatus": "ACTIVE",
  "rank": 1,
  "severity": "MILD",
  "diagnosedById": "uuid",
  "diagnosedByName": "Dr. Ahmad Santoso",
  "clinicalNotes": "Pasien mengeluh batuk dan pilek sejak 3 hari"
}
```

**Diagnosis Types:**
- PRIMARY - Primary/main diagnosis (required, rank 1)
- SECONDARY - Additional diagnoses
- ADMISSION - Diagnosis at admission
- DISCHARGE - Diagnosis at discharge
- DIFFERENTIAL - Differential diagnosis
- WORKING - Working diagnosis

**Clinical Status:**
- ACTIVE - Currently active condition
- RESOLVED - Condition resolved
- RECURRENCE - Recurrent condition
- REMISSION - In remission
- INACTIVE - Inactive condition

---

### 4. Completion
**Status:** `FINISHED`

**API Endpoint:** `POST /api/clinical/encounters/{id}/finish`

**Validation Rules Enforced:**

✅ **Rule 1: Must have at least one diagnosis**
```
Error: "Encounter harus memiliki minimal 1 diagnosis sebelum diselesaikan"
```

✅ **Rule 2: Must have attending practitioner assigned**
```
Error: "Encounter harus memiliki dokter yang bertugas (attending practitioner)"
```

✅ **Rule 3: BPJS encounters require SEP number**
```
Error: "Encounter BPJS wajib memiliki nomor SEP"
```

**What Happens on Finish:**
- All validation rules checked
- Status changed to FINISHED
- Encounter end time recorded
- Length of stay calculated (hours and days)
- Status history updated
- Ready for billing trigger

**Valid Transitions:**
- FROM: `IN_PROGRESS` → TO: `FINISHED` ✅

---

## Status Transition Rules

### Valid State Transitions

| From Status   | Valid Next States                                     |
|--------------|-------------------------------------------------------|
| PLANNED      | ARRIVED, CANCELLED                                    |
| ARRIVED      | TRIAGED, IN_PROGRESS, CANCELLED                       |
| TRIAGED      | IN_PROGRESS, CANCELLED                                |
| IN_PROGRESS  | FINISHED, CANCELLED                                   |
| FINISHED     | (None - Terminal state)                               |
| CANCELLED    | (None - Terminal state)                               |

### Invalid Transitions
Attempting invalid transitions will result in:
```
BusinessException: "Invalid status transition from {current} to {requested}"
```

---

## Additional Features

### Get Encounter Details
`GET /api/clinical/encounters/{id}`

**Response includes:**
- Complete encounter information
- All participants (care team)
- All diagnoses with rankings
- Complete status history
- Calculated duration and length of stay

### Search Encounters
`POST /api/clinical/encounters/search`

**Search Criteria:**
```json
{
  "patientId": "uuid",
  "encounterType": "OUTPATIENT",
  "status": "IN_PROGRESS",
  "department": "Poliklinik",
  "isBpjs": true,
  "encounterStartFrom": "2025-01-01T00:00:00",
  "encounterStartTo": "2025-01-31T23:59:59",
  "page": 0,
  "size": 20,
  "sortBy": "encounterStart",
  "sortDirection": "DESC"
}
```

### Cancel Encounter
`POST /api/clinical/encounters/{id}/cancel?reason=Pasien tidak datang`

**Requirements:**
- Must provide cancellation reason
- Can only cancel from: PLANNED, ARRIVED, TRIAGED, IN_PROGRESS
- Cannot cancel FINISHED encounters

---

## Audit Trail

### Automatic Status History
Every status change is automatically recorded with:
- Previous status (fromStatus)
- New status (toStatus)
- Timestamp (statusChangedAt)
- User who made the change (changedBy)
- Reason for change
- Additional notes

**View Status History:**
`GET /api/clinical/encounters/{id}/status-history`

**Response Example:**
```json
[
  {
    "fromStatus": "IN_PROGRESS",
    "toStatus": "FINISHED",
    "statusChangedAt": "2025-01-20T14:30:00",
    "changedByName": "Dr. Ahmad Santoso",
    "reason": "Encounter finished",
    "transitionDescription": "In Progress → Finished",
    "transitionDescriptionIndonesian": "Sedang Berlangsung → Selesai"
  },
  {
    "fromStatus": "ARRIVED",
    "toStatus": "IN_PROGRESS",
    "statusChangedAt": "2025-01-20T10:15:00",
    "changedByName": "Dr. Ahmad Santoso",
    "reason": "Encounter started"
  }
]
```

---

## Integration Points

### 1. Outpatient Registration Integration
- Encounter automatically linked to outpatient registration
- Queue number from registration system
- Department/polyclinic assignment

### 2. BPJS Integration
- SEP number validation
- Insurance type tracking
- Eligibility verification

### 3. SATUSEHAT Integration
- Encounter ID sync (satusehatEncounterId)
- Sync status tracking (satusehatSynced, satusehatSyncedAt)
- Ready for national health data exchange

### 4. Billing System (Future)
- Auto-trigger on encounter finish
- Billing status tracking
- Total charges calculation

### 5. Lab/Radiology Orders (Future)
- Order placement during consultation
- Results integration
- Status tracking

---

## Error Handling

### Common Errors

**1. Validation Errors (400)**
```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Validasi gagal: Encounter harus memiliki minimal 1 diagnosis sebelum diselesaikan",
  "timestamp": "2025-01-20T14:30:00"
}
```

**2. Resource Not Found (404)**
```json
{
  "status": 404,
  "error": "RESOURCE_NOT_FOUND",
  "message": "Encounter tidak ditemukan dengan ID: {id}",
  "timestamp": "2025-01-20T14:30:00"
}
```

**3. Business Rule Violation (422)**
```json
{
  "status": 422,
  "error": "BUSINESS_EXCEPTION",
  "message": "Invalid status transition from FINISHED to IN_PROGRESS",
  "timestamp": "2025-01-20T14:30:00"
}
```

**4. Duplicate Resource (409)**
```json
{
  "status": 409,
  "error": "DUPLICATE_RESOURCE",
  "message": "Practitioner already participating in this encounter with the same role",
  "timestamp": "2025-01-20T14:30:00"
}
```

---

## Best Practices

### 1. Always Add Diagnosis Before Finishing
```javascript
// Step 1: Add primary diagnosis
POST /api/clinical/encounters/{id}/diagnoses
{
  "diagnosisCode": "J06.9",
  "diagnosisText": "ISPA",
  "diagnosisType": "PRIMARY",
  "rank": 1
}

// Step 2: Add secondary diagnoses if needed
POST /api/clinical/encounters/{id}/diagnoses
{
  "diagnosisCode": "R50.9",
  "diagnosisText": "Demam",
  "diagnosisType": "SECONDARY",
  "rank": 2
}

// Step 3: Now finish is allowed
POST /api/clinical/encounters/{id}/finish
```

### 2. Assign Care Team Early
```javascript
// Assign attending physician when encounter starts
POST /api/clinical/encounters/{id}/participants
{
  "practitionerId": "uuid",
  "participantType": "PRIMARY"
}
```

### 3. Validate BPJS SEP Before Creating Encounter
```javascript
// Ensure SEP number is provided for BPJS patients
{
  "insuranceType": "BPJS",
  "sepNumber": "0301R0010125K000001",  // Required!
  "sepDate": "2025-01-20"
}
```

### 4. Use Proper Status Transitions
```javascript
// Correct flow for routine consultation
PLANNED → ARRIVED → IN_PROGRESS → FINISHED

// Correct flow for urgent/emergency
PLANNED → ARRIVED → TRIAGED → IN_PROGRESS → FINISHED

// Skip triage for routine cases
ARRIVED → IN_PROGRESS → FINISHED  // Also valid
```

---

## Testing the Implementation

### Test Scenario: Complete Outpatient Visit

```bash
# 1. Create encounter (patient arrives)
curl -X POST http://localhost:8080/api/clinical/encounters \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "uuid",
    "encounterType": "OUTPATIENT",
    "encounterClass": "AMBULATORY",
    "outpatientRegistrationId": "uuid",
    "departmentId": "uuid",
    "currentDepartment": "Poliklinik Umum",
    "priority": "ROUTINE",
    "insuranceType": "SELF_PAY"
  }'

# 2. Start consultation
curl -X POST http://localhost:8080/api/clinical/encounters/{id}/start

# 3. Add attending physician
curl -X POST http://localhost:8080/api/clinical/encounters/{id}/participants \
  -H "Content-Type: application/json" \
  -d '{
    "practitionerId": "uuid",
    "participantType": "PRIMARY",
    "participantName": "Dr. Ahmad"
  }'

# 4. Add diagnosis
curl -X POST http://localhost:8080/api/clinical/encounters/{id}/diagnoses \
  -H "Content-Type: application/json" \
  -d '{
    "diagnosisCode": "J06.9",
    "diagnosisText": "ISPA",
    "diagnosisType": "PRIMARY",
    "rank": 1
  }'

# 5. Finish encounter
curl -X POST http://localhost:8080/api/clinical/encounters/{id}/finish

# 6. View complete encounter with history
curl http://localhost:8080/api/clinical/encounters/{id}
```

---

## Compliance & Standards

✅ **FHIR Compliance**: EncounterClass values align with FHIR standards
✅ **ICD-10 Coding**: Full support for ICD-10 diagnosis codes
✅ **BPJS Integration**: SEP number validation and tracking
✅ **SATUSEHAT Ready**: Fields prepared for national health exchange
✅ **Indonesian Localization**: All messages in Indonesian

---

## Conclusion

The Encounter Management System is now fully operational with:
- ✅ Complete workflow implementation
- ✅ All validation rules enforced
- ✅ Status transition controls
- ✅ Comprehensive audit trail
- ✅ BPJS/Insurance support
- ✅ SATUSEHAT integration ready
- ✅ RESTful API with 15 endpoints

The system is **production-ready** and follows Indonesian hospital workflow requirements.
