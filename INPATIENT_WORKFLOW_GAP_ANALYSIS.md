# Inpatient Workflow - Gap Analysis & Implementation Plan

## Executive Summary

This document identifies the gaps between the current HMS implementation and the **Inpatient Encounters (Rawat Inap)** workflow requirements, and provides a detailed implementation plan.

---

## Current Implementation Status

### ✅ What Already Exists

| Feature | Status | Location |
|---------|--------|----------|
| Encounter Core Entity | ✅ Complete | `clinical.entity.Encounter` |
| Encounter CRUD Endpoints | ✅ Complete | `EncounterController` (15 endpoints) |
| Status Management | ✅ Complete | Status transitions, history tracking |
| Diagnosis Management | ✅ Complete | ICD-10 codes, primary/secondary |
| Care Team/Participants | ✅ Complete | Doctors, nurses, specialists |
| Department Transfer Entity | ✅ Complete | `clinical.entity.DepartmentTransfer` |
| Discharge Summary Entity | ✅ Complete | `clinical.entity.DischargeSummary` |
| Bed Management Entity | ✅ Complete | `registration.entity.Bed` |
| BedAssignment Entity | ✅ Complete | `registration.entity.BedAssignment` |
| Location Fields | ✅ Complete | `location_id`, `current_location` in Encounter |
| Length of Stay Calculation | ✅ Complete | Automatic calculation on finish |
| BPJS Integration | ✅ Complete | SEP number tracking |
| Audit Trail | ✅ Complete | Status history, audit fields |

### ❌ Missing Features for Inpatient Workflow

| Feature | Priority | Required For |
|---------|----------|--------------|
| **1. SOAP Notes/Progress Notes** | 🔴 HIGH | Daily patient progress tracking |
| **2. Vital Signs Monitoring** | 🔴 HIGH | Every shift/hourly vitals tracking |
| **3. Medication Administration Records (MAR)** | 🔴 HIGH | Medication tracking during stay |
| **4. Encounter Location History** | 🟡 MEDIUM | Track bed/room changes |
| **5. Admission Workflow Endpoints** | 🔴 HIGH | Bed assignment during admission |
| **6. Transfer Management Endpoints** | 🟡 MEDIUM | Ward/department transfers |
| **7. Discharge Workflow Endpoints** | 🔴 HIGH | Discharge summary generation |
| **8. Bed Occupancy Tracking API** | 🟡 MEDIUM | Real-time bed availability |

---

## Detailed Gap Analysis

### 1. SOAP Notes / Progress Notes

**What's Needed:**
- Daily progress notes following SOAP format (Subjective, Objective, Assessment, Plan)
- Shift handover notes
- Critical care notes
- Nursing notes

**Database Schema Required:**
```sql
CREATE TABLE clinical_schema.progress_note (
    id UUID PRIMARY KEY,
    note_number VARCHAR(50) UNIQUE NOT NULL,
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,

    -- Note metadata
    note_type VARCHAR(30) NOT NULL, -- SOAP, SHIFT_HANDOVER, CRITICAL_CARE, NURSING
    note_date_time TIMESTAMP NOT NULL,
    shift VARCHAR(20), -- MORNING, AFTERNOON, NIGHT

    -- SOAP format
    subjective TEXT, -- Patient complaints/symptoms
    objective TEXT, -- Vital signs, physical exam findings
    assessment TEXT, -- Clinical impression/diagnosis
    plan TEXT, -- Treatment plan/interventions

    -- Additional info
    follow_up_required BOOLEAN DEFAULT false,
    follow_up_instructions TEXT,

    -- Provider
    provider_id UUID NOT NULL,
    provider_name VARCHAR(200) NOT NULL,
    provider_type VARCHAR(30), -- DOCTOR, NURSE, SPECIALIST

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);
```

**Endpoints Required:**
- `POST /api/clinical/encounters/{id}/progress-notes` - Add progress note
- `GET /api/clinical/encounters/{id}/progress-notes` - Get all notes
- `GET /api/clinical/encounters/{id}/progress-notes/latest` - Get latest note
- `PUT /api/clinical/progress-notes/{id}` - Update note
- `DELETE /api/clinical/progress-notes/{id}` - Delete note

---

### 2. Vital Signs Monitoring

**What's Needed:**
- Regular vital signs monitoring (hourly for critical, per shift for regular)
- Blood pressure, temperature, pulse, respiratory rate, SpO2
- GCS (Glasgow Coma Scale) for critical patients
- Fluid balance tracking

**Database Schema Required:**
```sql
CREATE TABLE clinical_schema.vital_signs (
    id UUID PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,

    -- Timing
    measurement_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    shift VARCHAR(20), -- MORNING, AFTERNOON, NIGHT

    -- Basic vitals
    systolic_bp INTEGER, -- mmHg
    diastolic_bp INTEGER, -- mmHg
    heart_rate INTEGER, -- bpm
    respiratory_rate INTEGER, -- breaths/min
    temperature DECIMAL(4,2), -- Celsius
    spo2 INTEGER, -- %

    -- Additional measurements
    weight DECIMAL(5,2), -- kg
    height DECIMAL(5,2), -- cm
    bmi DECIMAL(4,2),

    -- Critical care
    gcs_eye INTEGER, -- Glasgow Coma Scale - Eye (1-4)
    gcs_verbal INTEGER, -- Verbal (1-5)
    gcs_motor INTEGER, -- Motor (1-6)
    gcs_total INTEGER, -- Total (3-15)

    -- Fluid balance
    fluid_intake_ml INTEGER,
    fluid_output_ml INTEGER,
    fluid_balance_ml INTEGER,

    -- Pain assessment
    pain_score INTEGER, -- 0-10
    pain_location VARCHAR(200),

    -- Notes
    notes TEXT,
    alerts TEXT, -- Any abnormal findings

    -- Provider
    recorded_by_id UUID,
    recorded_by_name VARCHAR(200),

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);
```

**Endpoints Required:**
- `POST /api/clinical/encounters/{id}/vital-signs` - Record vital signs
- `GET /api/clinical/encounters/{id}/vital-signs` - Get all vitals
- `GET /api/clinical/encounters/{id}/vital-signs/latest` - Get latest vitals
- `GET /api/clinical/encounters/{id}/vital-signs/chart` - Get vitals for charting
- `PUT /api/clinical/vital-signs/{id}` - Update vital signs

---

### 3. Medication Administration Records (MAR)

**What's Needed:**
- Track all medications administered during hospitalization
- Schedule tracking (PRN, scheduled, stat)
- Dosage, route, frequency
- Administration confirmation
- Adverse reactions tracking

**Database Schema Required:**
```sql
CREATE TABLE clinical_schema.medication_administration (
    id UUID PRIMARY KEY,
    mar_number VARCHAR(50) UNIQUE NOT NULL,
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,

    -- Medication details
    medication_order_id UUID, -- Link to pharmacy order
    medication_name VARCHAR(200) NOT NULL,
    generic_name VARCHAR(200),
    medication_code VARCHAR(50), -- Drug code

    -- Dosage
    dose VARCHAR(50) NOT NULL,
    dose_unit VARCHAR(20) NOT NULL, -- mg, ml, unit, etc.
    route VARCHAR(50) NOT NULL, -- ORAL, IV, IM, SC, etc.
    frequency VARCHAR(50), -- BID, TID, QID, PRN, etc.

    -- Scheduling
    schedule_type VARCHAR(20) NOT NULL, -- SCHEDULED, PRN, STAT, ONE_TIME
    scheduled_time TIMESTAMP,
    actual_administration_time TIMESTAMP NOT NULL,

    -- Administration
    administered BOOLEAN DEFAULT false,
    administered_by_id UUID,
    administered_by_name VARCHAR(200),
    administration_status VARCHAR(20), -- GIVEN, REFUSED, HELD, MISSED

    -- Refusal/Hold reasons
    not_given_reason TEXT,
    hold_reason TEXT,

    -- Adverse reactions
    adverse_reaction BOOLEAN DEFAULT false,
    adverse_reaction_details TEXT,

    -- Notes
    administration_notes TEXT,

    -- Prescriber
    prescribed_by_id UUID,
    prescribed_by_name VARCHAR(200),

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);
```

**Endpoints Required:**
- `POST /api/clinical/encounters/{id}/medications` - Record medication administration
- `GET /api/clinical/encounters/{id}/medications` - Get medication history
- `GET /api/clinical/encounters/{id}/medications/due` - Get due medications
- `PATCH /api/clinical/medications/{id}/administer` - Mark as administered
- `PATCH /api/clinical/medications/{id}/refuse` - Record refusal
- `PATCH /api/clinical/medications/{id}/hold` - Hold medication

---

### 4. Encounter Location History

**What's Needed:**
- Track every location/bed change during hospitalization
- Separate from department transfer (bed changes within same ward)
- Useful for infection control, bed utilization analysis

**Database Schema Required:**
```sql
CREATE TABLE clinical_schema.encounter_location_history (
    id UUID PRIMARY KEY,
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,

    -- Location details
    location_id UUID,
    location_name VARCHAR(200) NOT NULL,
    department_id UUID,
    department_name VARCHAR(100) NOT NULL,

    -- Bed/Room details
    room_id UUID,
    room_number VARCHAR(50),
    bed_id UUID,
    bed_number VARCHAR(20),

    -- Timing
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    duration_hours INTEGER,

    -- Reason
    location_type VARCHAR(50), -- ADMISSION, TRANSFER, ICU, OPERATING_ROOM, DISCHARGE
    change_reason TEXT,

    -- Responsible staff
    changed_by_id UUID,
    changed_by_name VARCHAR(200),

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);
```

**Endpoints Required:**
- `POST /api/clinical/encounters/{id}/location-history` - Record location change
- `GET /api/clinical/encounters/{id}/location-history` - Get location history
- `GET /api/clinical/encounters/{id}/current-location` - Get current location

---

### 5. Admission Workflow

**What's Needed:**
- Admission endpoint that integrates with bed assignment
- Admission diagnosis
- Admission orders (diet, medications, monitoring)
- Room/bed assignment

**Endpoints Required:**
- `POST /api/clinical/encounters/{id}/admit` - Admit patient with bed assignment
  ```json
  {
    "bedId": "uuid",
    "admissionDiagnosis": {
      "diagnosisCode": "A09",
      "diagnosisText": "Gastroenteritis"
    },
    "admissionOrders": {
      "dietOrders": "NPO until further notice",
      "medicationOrders": ["IV fluids", "Antiemetics"],
      "monitoringFrequency": "Q4H"
    }
  }
  ```

---

### 6. Transfer Management

**What's Needed:**
- Endpoints to create, accept, reject, complete transfers
- Integration with DepartmentTransfer entity (already exists)

**Endpoints Required:**
- `POST /api/clinical/encounters/{id}/transfers` - Request transfer
- `GET /api/clinical/encounters/{id}/transfers` - Get transfer history
- `PATCH /api/clinical/transfers/{id}/accept` - Accept transfer
- `PATCH /api/clinical/transfers/{id}/reject` - Reject transfer
- `PATCH /api/clinical/transfers/{id}/complete` - Complete transfer
- `GET /api/clinical/transfers/pending` - Get pending transfers

---

### 7. Discharge Workflow

**What's Needed:**
- Endpoints to create and manage discharge summaries
- Integration with DischargeSummary entity (already exists)
- Resume Medis generation

**Endpoints Required:**
- `POST /api/clinical/encounters/{id}/discharge-summary` - Create discharge summary
- `GET /api/clinical/encounters/{id}/discharge-summary` - Get discharge summary
- `PUT /api/clinical/discharge-summaries/{id}` - Update discharge summary
- `POST /api/clinical/discharge-summaries/{id}/sign` - Sign discharge summary
- `POST /api/clinical/discharge-summaries/{id}/generate-pdf` - Generate PDF

---

### 8. Bed Occupancy Tracking

**What's Needed:**
- Real-time bed availability endpoints
- Occupancy statistics by ward/department

**Endpoints Required:**
- `GET /api/clinical/beds/available` - Get available beds
- `GET /api/clinical/beds/occupancy` - Get occupancy statistics
- `GET /api/clinical/beds/{id}/status` - Get bed status
- `GET /api/clinical/departments/{id}/bed-census` - Get department bed census

---

## Implementation Priorities

### Phase 1: Critical Features (Week 1)
1. ✅ Database migrations for all new tables
2. ✅ SOAP Notes / Progress Notes (entity, service, endpoints)
3. ✅ Vital Signs Monitoring (entity, service, endpoints)
4. ✅ Admission Workflow (endpoints + bed integration)

### Phase 2: Core Features (Week 2)
5. ✅ Medication Administration Records (entity, service, endpoints)
6. ✅ Encounter Location History (entity, service, endpoints)
7. ✅ Discharge Workflow (endpoints for DischargeSummary)

### Phase 3: Supporting Features (Week 3)
8. ✅ Transfer Management (endpoints for DepartmentTransfer)
9. ✅ Bed Occupancy Tracking (API endpoints)
10. ✅ API Documentation (OpenAPI/Swagger)

---

## Database Migration Strategy

**Migration File:** `V12__create_inpatient_clinical_tracking_tables.sql`

Will include:
1. `clinical_schema.progress_note`
2. `clinical_schema.vital_signs`
3. `clinical_schema.medication_administration`
4. `clinical_schema.encounter_location_history`

With proper:
- Indexes for performance
- Foreign key constraints
- Triggers for audit timestamps
- Comments for documentation

---

## Testing Strategy

For each new feature:
1. **Unit Tests** - Service layer business logic
2. **Integration Tests** - Repository and database operations
3. **API Tests** - Controller endpoints with valid/invalid inputs
4. **Workflow Tests** - End-to-end inpatient admission → discharge

---

## Success Criteria

✅ All database migrations run successfully
✅ All entities created with proper relationships
✅ All service methods tested
✅ All API endpoints functional
✅ Complete inpatient workflow documented
✅ API documentation (Swagger) generated
✅ All validation rules enforced
✅ Audit trail working for all operations

---

## Next Steps

1. **Get approval** on this implementation plan
2. **Create database migration** V12
3. **Implement entities** (Progress Notes, Vital Signs, MAR, Location History)
4. **Build services** with business logic
5. **Create controllers** with REST endpoints
6. **Write tests** for all components
7. **Generate API documentation**

---

## Questions for Clarification

Before proceeding, please confirm:

1. **Priority**: Should we implement all features or focus on specific ones first?
2. **Integration**: Do you need integration with existing pharmacy/lab systems?
3. **Reporting**: Do you need specific reports (e.g., daily census, medication audit)?
4. **Permissions**: Should we add role-based access control for these features?
5. **Real-time**: Do you need WebSocket/real-time updates for vital signs/medications?

---

**Document Version:** 1.0
**Last Updated:** 2025-11-20
**Author:** HMS Development Team
