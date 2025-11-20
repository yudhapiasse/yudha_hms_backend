# Outpatient Workflow - Phase 1 Implementation Complete

**Implementation Date:** 2025-11-20
**Phase:** Phase 1 - Critical Integrations
**Status:** ✅ **COMPLETE**

---

## Executive Summary

Phase 1 of the Outpatient Workflow implementation successfully integrates critical missing components to enable a fully functional outpatient encounter workflow. This phase focuses on three key areas:

1. **Auto-create Encounter from Registration** - Seamless integration between registration and clinical workflow
2. **Queue Calling System** - Complete queue management with status tracking and history
3. **Outpatient SOAP Notes** - Extend clinical documentation for outpatient consultations

**Overall Status:**
- ✅ Database Schema Changes: COMPLETE
- ✅ Entity & Enum Updates: COMPLETE
- ✅ Service Layer Implementation: COMPLETE
- ✅ REST API Endpoints: COMPLETE
- ✅ Integration Points: COMPLETE

---

## Implementation Summary

### Files Created: **7 new files**
### Files Modified: **5 existing files**
### Database Migration: **V13**
### New REST Endpoints: **12 endpoints**
### Lines of Code: **~1,850 lines**

---

## 1. Database Schema Changes (V13)

### Migration File
**`V13__outpatient_workflow_phase1_enhancements.sql`**

### Schema Changes

#### 1.1 Outpatient Registration Enhancements

**Added Columns:**
```sql
-- Encounter Integration
encounter_id UUID REFERENCES clinical_schema.encounter(id)

-- Queue Status Tracking
queue_status VARCHAR(20) DEFAULT 'WAITING'

-- Queue Timestamps
queue_called_at TIMESTAMP
queue_called_by VARCHAR(100)
queue_serving_started_at TIMESTAMP
queue_serving_ended_at TIMESTAMP
queue_skipped_at TIMESTAMP
queue_skip_reason TEXT
```

**Indexes Created:**
- `idx_outpatient_registration_encounter` - Fast encounter lookups
- `idx_outpatient_registration_queue_status` - Queue status filtering
- `idx_outpatient_registration_queue_called_at` - Call time tracking
- `idx_outpatient_registration_queue_serving_started_at` - Service time tracking

#### 1.2 Progress Note Enhancements

**Added Column:**
```sql
is_outpatient BOOLEAN DEFAULT false
```

This flag enables filtering outpatient consultation notes from inpatient notes.

#### 1.3 Queue Call History Table

**New Table:** `registration_schema.queue_call_history`

Tracks all queue calls for audit and analytics:
- When was patient called
- Who called the patient
- Patient response (RESPONDED, NO_RESPONSE, SKIPPED)
- Response time
- Call type (NORMAL, RECALL, URGENT)

**Purpose:**
- Audit trail of all queue calls
- Analytics: response rates, average response times
- Quality monitoring

#### 1.4 Queue Dashboard View

**New View:** `registration_schema.v_queue_dashboard`

Real-time queue monitoring with calculated fields:
- Current wait time
- Service time
- Queue status per patient
- Polyclinic information

---

## 2. Entity & Enum Updates

### 2.1 New Enums

#### QueueStatus.java
```java
WAITING,      // Patient checked in, waiting to be called
CALLED,       // Patient has been called
SERVING,      // Patient is currently being served
COMPLETED,    // Service completed
SKIPPED,      // Patient was skipped (not present)
CANCELLED     // Queue cancelled
```

**Helper Methods:**
- `isActive()` - Check if queue is active
- `canBeCalled()` - Validate state transition
- `canStartServing()` - Validate state transition
- `isFinal()` - Check if queue is in final state

#### QueueCallType.java
```java
NORMAL,   // Regular queue call
RECALL,   // Patient called again after no response
URGENT    // Urgent call for priority patient
```

#### QueueResponseStatus.java
```java
RESPONDED,      // Patient responded to the call
NO_RESPONSE,    // Patient did not respond
SKIPPED         // Patient was skipped
```

### 2.2 New Entities

#### QueueCallHistory.java
Comprehensive call tracking entity with:
- Call timestamps and metadata
- Response tracking
- Business methods: `markAsResponded()`, `markAsNoResponse()`, `markAsSkipped()`
- Response time calculation

### 2.3 Updated Entities

#### OutpatientRegistration.java

**Added Fields:**
```java
private QueueStatus queueStatus;
private LocalDateTime queueCalledAt;
private String queueCalledBy;
private LocalDateTime queueServingStartedAt;
private LocalDateTime queueServingEndedAt;
private LocalDateTime queueSkippedAt;
private String queueSkipReason;
private UUID encounterId;
```

**New Business Methods:**
```java
callQueue(String calledBy)           // Call patient from queue
startServing()                        // Start serving patient
completeQueue()                       // Complete queue service
skipQueue(String reason)              // Skip patient (not present)
cancelQueue(String reason, String by) // Cancel queue
getQueueWaitTimeMinutes()            // Calculate wait time
getQueueServiceTimeMinutes()         // Calculate service time
isQueueActive()                      // Check if queue is active
isQueueCompleted()                   // Check if queue is completed
```

#### NoteType.java (enum)

**Added:**
```java
OUTPATIENT_CONSULTATION("Outpatient Consultation", "Konsultasi Rawat Jalan")
```

---

## 3. Service Layer Implementation

### 3.1 QueueCallingService.java

Complete queue calling workflow management.

**Methods (12 total):**

#### Queue Operations
1. `callNextPatient(polyclinicId, calledBy, consultationRoom)` - Call next waiting patient
2. `callSpecificPatient(polyclinicId, queueNumber, calledBy, consultationRoom)` - Call specific queue number
3. `recallPatient(registrationId, calledBy, consultationRoom)` - Recall patient (call again)
4. `startServing(registrationId)` - Patient responded, start service
5. `completeQueue(registrationId)` - Complete queue service
6. `skipPatient(registrationId, reason)` - Skip patient (not present)

#### Queue Queries
7. `getCurrentQueueStatus(polyclinicId)` - Get all active queues
8. `getWaitingPatients(polyclinicId)` - Get waiting patients
9. `getServingPatients(polyclinicId)` - Get currently serving patients
10. `getSkippedPatients(polyclinicId)` - Get skipped patients

#### History & Analytics
11. `getCallHistory(registrationId)` - Get call history for registration
12. `getCallStatistics(polyclinicId)` - Get analytics (response rate, avg response time, recalls)

**Key Features:**
- Thread-safe queue operations
- Automatic call history recording
- State validation (prevents invalid transitions)
- Comprehensive analytics support

**QueueCallStatistics Inner Class:**
```java
- totalCalls: long
- responded: long
- noResponse: long
- recalls: long
- averageResponseTimeSeconds: double
- getResponseRate(): double
- getNoResponseRate(): double
```

### 3.2 OutpatientRegistrationService.java (Enhanced)

**New Method:**
```java
createEncounterForRegistration(registration, patient, polyclinic, doctor)
```

**Integration Points:**

1. **`registerWalkIn()`** - Auto-creates encounter with status ARRIVED
   ```java
   OutpatientRegistration saved = registrationRepository.save(registration);

   // Auto-create encounter
   EncounterResponse encounter = createEncounterForRegistration(saved, patient, polyclinic, doctor);
   saved.setEncounterId(encounter.getId());
   saved = registrationRepository.save(saved);
   ```

2. **`checkInPatient()`** - Creates encounter for appointments on check-in
   ```java
   if (registration.getEncounterId() == null) {
       EncounterResponse encounter = createEncounterForRegistration(...);
       registration.setEncounterId(encounter.getId());
       registration = registrationRepository.save(registration);
   }
   ```

**Encounter Creation Details:**
- Type: `OUTPATIENT`
- Class: `AMBULATORY`
- Status: `ARRIVED`
- Auto-links to outpatient registration
- Copies chief complaint to encounter
- Sets attending doctor
- Handles BPJS/insurance information

### 3.3 ProgressNoteService.java (Enhanced)

**Updated Method:**
```java
generateNoteNumber(NoteType noteType)
```

**Added Support For:**
```java
case OUTPATIENT_CONSULTATION -> "PN-OUTPT";
```

**Generated Note Number Format:**
- `PN-OUTPT-20251120-0001` - Outpatient consultation note

---

## 4. Repository Layer

### 4.1 QueueCallHistoryRepository.java

**Query Methods (14 total):**

#### Basic Queries
1. `findByOutpatientRegistrationIdOrderByCalledAtDesc(registrationId)`
2. `findFirstByOutpatientRegistrationIdOrderByCalledAtDesc(registrationId)`
3. `findByPolyclinicAndDate(polyclinicId, date)`
4. `findByDoctorAndDate(doctorId, date)`

#### Statistics Queries
5. `countByPolyclinicAndDate(polyclinicId, date)`
6. `countByPolyclinicDateAndResponseStatus(polyclinicId, date, status)`
7. `countByPolyclinicDateAndCallType(polyclinicId, date, callType)`
8. `calculateAverageResponseTimeSeconds(polyclinicId, date)`

#### Operational Queries
9. `findNoResponseCallsToday(polyclinicId)`
10. `findRecentCallsByPolyclinic(polyclinicId)` - Last 10 calls
11. `findByPolyclinicAndTimeRange(polyclinicId, startTime, endTime)`

#### Maintenance
12. `deleteOldCallHistory(beforeDate)` - Cleanup old records

---

## 5. REST API Endpoints

### 5.1 QueueCallingController.java

**Base Path:** `/api/registration/queue`

**Endpoints (12 total):**

#### Queue Control Operations

1. **POST** `/polyclinics/{polyclinicId}/call-next`
   - **Purpose:** Call next patient from queue
   - **Parameters:** `calledBy`, `consultationRoom` (optional)
   - **Returns:** Updated registration with CALLED status

2. **POST** `/polyclinics/{polyclinicId}/call-specific`
   - **Purpose:** Call specific patient by queue number
   - **Parameters:** `queueNumber`, `calledBy`, `consultationRoom` (optional)
   - **Returns:** Updated registration

3. **POST** `/{registrationId}/recall`
   - **Purpose:** Recall patient (call again after no response)
   - **Parameters:** `calledBy`, `consultationRoom` (optional)
   - **Returns:** Updated registration

4. **POST** `/{registrationId}/start-serving`
   - **Purpose:** Start serving patient (patient responded)
   - **Returns:** Registration with SERVING status

5. **POST** `/{registrationId}/complete`
   - **Purpose:** Complete queue service
   - **Returns:** Registration with COMPLETED status

6. **POST** `/{registrationId}/skip`
   - **Purpose:** Skip patient (not present when called)
   - **Parameters:** `reason`
   - **Returns:** Registration with SKIPPED status

#### Queue Status Queries

7. **GET** `/polyclinics/{polyclinicId}/status`
   - **Purpose:** Get all active queues for polyclinic
   - **Returns:** List of active registrations

8. **GET** `/polyclinics/{polyclinicId}/waiting`
   - **Purpose:** Get waiting patients
   - **Returns:** List of registrations in WAITING status

9. **GET** `/polyclinics/{polyclinicId}/serving`
   - **Purpose:** Get currently serving patients
   - **Returns:** List of registrations in SERVING status

10. **GET** `/polyclinics/{polyclinicId}/skipped`
    - **Purpose:** Get skipped patients
    - **Returns:** List of registrations in SKIPPED status

#### History & Analytics

11. **GET** `/{registrationId}/call-history`
    - **Purpose:** Get all call attempts for a registration
    - **Returns:** List of QueueCallHistory

12. **GET** `/polyclinics/{polyclinicId}/statistics`
    - **Purpose:** Get call statistics for today
    - **Returns:** QueueCallStatistics object
      ```json
      {
        "polyclinicId": "uuid",
        "date": "2025-11-20",
        "totalCalls": 45,
        "responded": 42,
        "noResponse": 3,
        "recalls": 5,
        "averageResponseTimeSeconds": 23.5,
        "responseRate": 93.3,
        "noResponseRate": 6.7
      }
      ```

---

## 6. Integration Points

### 6.1 Outpatient Registration → Encounter

**Flow Diagram:**
```
Walk-in Registration
    ↓
Create OutpatientRegistration
    ↓
Auto-create Encounter (ARRIVED status)
    ↓
Link encounter_id to registration
    ↓
Patient ready for clinical workflow
```

**Appointment Flow:**
```
Book Appointment
    ↓
Create OutpatientRegistration
    ↓
Patient arrives & checks in
    ↓
Auto-create Encounter (ARRIVED status)
    ↓
Assign queue number
    ↓
Patient ready for clinical workflow
```

### 6.2 Queue Workflow

**Complete Queue Workflow:**
```
1. Patient checks in
   Status: WAITING, Queue: UM001

2. Receptionist calls patient
   POST /queue/polyclinics/{id}/call-next
   Status: CALLED
   Queue Call History created

3. Patient responds and enters consultation room
   POST /queue/{id}/start-serving
   Status: SERVING
   Call History updated: RESPONDED
   Encounter status: IN_PROGRESS

4. Doctor completes consultation
   POST /queue/{id}/complete
   Status: COMPLETED
   Encounter status: FINISHED
```

**Alternative: Patient Not Present**
```
2. Receptionist calls patient
   Status: CALLED

3. No response after 3 minutes
   POST /queue/{id}/skip
   Status: SKIPPED
   Call History: NO_RESPONSE
   Reason: "Patient tidak hadir saat dipanggil"

4. Patient arrives later
   POST /queue/{id}/recall
   Status: CALLED (again)
   Call Type: RECALL
```

### 6.3 Clinical Documentation

**Outpatient SOAP Note Creation:**
```
POST /api/clinical/encounters/{encounterId}/progress-notes

{
  "noteType": "OUTPATIENT_CONSULTATION",
  "noteDateTime": "2025-11-20T10:30:00",
  "subjective": "Pasien mengeluh demam sejak 3 hari yang lalu",
  "objective": "TD: 120/80, Nadi: 88x/menit, Suhu: 38.5°C",
  "assessment": "Demam tifoid suspek",
  "plan": "1. Lab: Darah lengkap, Widal\n2. Rx: Paracetamol 3x500mg",
  "providerType": "DOCTOR",
  ...
}

Response:
{
  "success": true,
  "message": "Progress note berhasil dibuat: PN-OUTPT-20251120-0001",
  "data": {
    "id": "uuid",
    "noteNumber": "PN-OUTPT-20251120-0001",
    "noteType": "OUTPATIENT_CONSULTATION",
    ...
  }
}
```

---

## 7. Key Business Rules Implemented

### 7.1 Queue State Transitions

**Valid Transitions:**
```
WAITING → CALLED    ✅ (call patient)
WAITING → CANCELLED ✅ (cancel registration)

CALLED → SERVING    ✅ (patient responded)
CALLED → SKIPPED    ✅ (patient not present)
CALLED → WAITING    ❌ (invalid)

SKIPPED → CALLED    ✅ (recall patient)
SKIPPED → CANCELLED ✅ (cancel registration)

SERVING → COMPLETED ✅ (finish consultation)
SERVING → WAITING   ❌ (invalid)

COMPLETED → *       ❌ (final state)
CANCELLED → *       ❌ (final state)
```

**Validation:**
All state transitions are validated in `OutpatientRegistration.callQueue()`, `startServing()`, etc.
Invalid transitions throw `IllegalStateException`.

### 7.2 Encounter Auto-Creation Rules

**When Encounter is Created:**
- ✅ Walk-in registration: Immediately upon registration
- ✅ Appointment: Upon patient check-in
- ✅ Never create duplicate: Check `registration.getEncounterId() == null`

**Encounter Properties:**
- Type: Always `OUTPATIENT`
- Class: Always `AMBULATORY`
- Initial Status: `ARRIVED`
- Linked to registration via `outpatientRegistrationId`

### 7.3 Call History Rules

**Recording Calls:**
- Every `callNextPatient()`, `callSpecificPatient()`, `recallPatient()` creates history entry
- Call type determined automatically:
  - First call: `NORMAL`
  - Second+ call: `RECALL`
  - Manual specific call: `NORMAL` or `RECALL` based on current status

**Response Tracking:**
- `startServing()` marks latest call as `RESPONDED`
- `skipPatient()` marks latest call as `NO_RESPONSE`

---

## 8. Testing Scenarios

### Scenario 1: Walk-in Patient Happy Path
```
1. POST /api/registration/outpatient/walk-in
   → Registration created, Queue: UM001, Encounter created, Status: ARRIVED

2. POST /api/registration/queue/polyclinics/{id}/call-next
   → Patient called, Status: CALLED

3. POST /api/registration/queue/{id}/start-serving
   → Consultation started, Status: SERVING, Encounter: IN_PROGRESS

4. POST /api/clinical/encounters/{id}/progress-notes (type: OUTPATIENT_CONSULTATION)
   → SOAP note created: PN-OUTPT-20251120-0001

5. POST /api/clinical/encounters/{id}/diagnoses
   → Diagnosis added (ICD-10)

6. POST /api/registration/queue/{id}/complete
   → Queue completed, Status: COMPLETED

7. POST /api/clinical/encounters/{id}/finish
   → Encounter finished, validation passed (has diagnosis, has doctor)
```

### Scenario 2: Patient Not Present (Skip & Recall)
```
1. POST /api/registration/queue/polyclinics/{id}/call-next
   → UM001 called, Status: CALLED

2. Wait 3 minutes, no response

3. POST /api/registration/queue/{id}/skip?reason=Tidak hadir saat dipanggil
   → Status: SKIPPED, Call History: NO_RESPONSE

4. Call next patient
   POST /api/registration/queue/polyclinics/{id}/call-next
   → UM002 called

5. UM001 patient arrives
   POST /api/registration/queue/{id}/recall
   → UM001 called again, Call Type: RECALL

6. Continue normal flow...
```

### Scenario 3: Appointment Check-in
```
1. POST /api/registration/outpatient/appointment
   → Appointment created for tomorrow, No encounter yet

2. Next day: Patient arrives
   POST /api/registration/outpatient/{id}/check-in
   → Queue assigned: UM015, Encounter created, Status: ARRIVED

3. Continue as walk-in flow...
```

---

## 9. Performance Considerations

### Database Optimizations
- ✅ Indexes on queue_status, queue_called_at for fast filtering
- ✅ Pessimistic locking on queue number generation (already exists in QueueService)
- ✅ View for queue dashboard (pre-calculated joins)

### Scalability
- Queue call history kept for audit (cleanup strategy: delete records older than 1 year)
- Consider partitioning queue_call_history by date for very high volume clinics

### Caching Opportunities
- Dashboard view results can be cached for 30 seconds (frequent polling)
- Call statistics can be cached for 5 minutes

---

## 10. Future Enhancements (Phase 2-4)

### Phase 2: Clinical Documentation
- ✅ Procedure tracking (ICD-9-CM)
- ✅ Outpatient prescription module
- Lab order management
- Radiology order management

### Phase 3: Completion Features
- Auto-generate encounter summary
- Billing integration (auto-trigger on encounter finish)
- Follow-up appointment scheduling
- Print prescriptions/lab orders

### Phase 4: Advanced Features
- Queue display board (WebSocket real-time updates)
- SMS/WhatsApp queue notifications
- Patient mobile app queue check
- Queue analytics dashboard
- Appointment reminder system

---

## 11. Deployment Checklist

### Pre-Deployment
- [ ] Review database migration V13
- [ ] Backup production database
- [ ] Test migration on staging environment

### Deployment Steps
1. [ ] Stop application
2. [ ] Run database migration: `./mvnw flyway:migrate`
3. [ ] Verify migration: Check new columns and tables exist
4. [ ] Deploy new application version
5. [ ] Start application
6. [ ] Smoke test:
   - [ ] Create walk-in registration → Verify encounter created
   - [ ] Call next patient → Verify status changes
   - [ ] Create outpatient consultation note → Verify note number format

### Post-Deployment
- [ ] Monitor logs for errors
- [ ] Test queue calling workflow end-to-end
- [ ] Verify call history recording
- [ ] Check queue statistics API
- [ ] Monitor database performance (new indexes)

---

## 12. API Documentation Examples

### Call Next Patient

**Request:**
```http
POST /api/registration/queue/polyclinics/550e8400-e29b-41d4-a716-446655440000/call-next?calledBy=Dr.%20Ahmad&consultationRoom=Ruang%201
```

**Response:**
```json
{
  "success": true,
  "message": "Pasien berhasil dipanggil: UM001",
  "data": {
    "id": "650e8400-e29b-41d4-a716-446655440000",
    "registrationNumber": "REG-20251120-0001",
    "queueNumber": 1,
    "queueCode": "UM001",
    "queueStatus": "CALLED",
    "queueCalledAt": "2025-11-20T10:30:00",
    "queueCalledBy": "Dr. Ahmad",
    "patientName": "John Doe",
    "chiefComplaint": "Demam sejak 3 hari",
    ...
  }
}
```

### Get Queue Statistics

**Request:**
```http
GET /api/registration/queue/polyclinics/550e8400-e29b-41d4-a716-446655440000/statistics
```

**Response:**
```json
{
  "success": true,
  "message": "Statistik panggilan berhasil diambil",
  "data": {
    "polyclinicId": "550e8400-e29b-41d4-a716-446655440000",
    "date": "2025-11-20",
    "totalCalls": 45,
    "responded": 42,
    "noResponse": 3,
    "recalls": 5,
    "averageResponseTimeSeconds": 23.5,
    "responseRate": 93.33,
    "noResponseRate": 6.67
  }
}
```

---

## 13. Monitoring & Metrics

### Key Metrics to Monitor

1. **Queue Performance:**
   - Average wait time per polyclinic
   - Average service time per polyclinic
   - Queue response rate (% patients responding on first call)
   - Average number of recalls per day

2. **Integration Health:**
   - % of registrations with encounters created (should be 100%)
   - Encounter creation failures
   - Queue state transition errors

3. **Database Performance:**
   - Queue call history table growth rate
   - View query performance (v_queue_dashboard)
   - Index usage statistics

### Alerts to Configure

- ⚠️ **High no-response rate** (> 10% in 1 hour)
- ⚠️ **Long average wait time** (> 60 minutes)
- ⚠️ **Encounter creation failures** (any occurrence)
- ⚠️ **Queue call history table size** (> 1M records, trigger cleanup)

---

## 14. Summary of Changes

### Database Changes
- 1 migration script (V13)
- 2 new tables (queue_call_history + view)
- 8 new columns in outpatient_registration
- 1 new column in progress_note
- 7 new indexes
- 3 new constraints

### Code Changes

**New Files (7):**
1. `QueueStatus.java` - Enum
2. `QueueCallType.java` - Enum
3. `QueueResponseStatus.java` - Enum
4. `QueueCallHistory.java` - Entity
5. `QueueCallHistoryRepository.java` - Repository
6. `QueueCallingService.java` - Service
7. `QueueCallingController.java` - Controller

**Modified Files (5):**
1. `OutpatientRegistration.java` - Added queue fields & methods
2. `OutpatientRegistrationService.java` - Added encounter auto-creation
3. `NoteType.java` - Added OUTPATIENT_CONSULTATION
4. `ProgressNoteService.java` - Added OUTPATIENT_CONSULTATION handling
5. `V13__outpatient_workflow_phase1_enhancements.sql` - Migration

**Lines of Code:**
- New code: ~1,850 lines
- Modified code: ~150 lines
- Total: ~2,000 lines

### API Changes
- 12 new REST endpoints
- 0 breaking changes to existing endpoints

---

## 15. Conclusion

**Phase 1 Status: ✅ COMPLETE**

All critical integrations for the outpatient workflow have been successfully implemented:

1. ✅ **Seamless Integration**: Outpatient registration now automatically creates encounters, eliminating manual steps
2. ✅ **Complete Queue Management**: Full queue calling system with status tracking, history, and analytics
3. ✅ **Clinical Documentation**: SOAP notes extended to support outpatient consultations

**Next Steps:**
- Deploy Phase 1 to production
- Monitor performance and gather user feedback
- Begin Phase 2 development (Clinical Documentation modules)

**Team Readiness:**
- Frontend team can now implement queue display board
- Clinical staff can use new queue calling endpoints
- Reports team can use queue analytics for dashboards

---

**Implementation By:** HMS Development Team
**Date:** 2025-11-20
**Version:** 1.0.0
