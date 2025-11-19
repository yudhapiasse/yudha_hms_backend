-- =====================================================
-- V7: Emergency Department (IGD) Registration System
-- =====================================================
-- Created: 2025-01-19
-- Description: Emergency registration with triage, ambulance tracking, and critical patient management
-- Features:
--   - Fast-track registration for unconscious/unknown patients
--   - Triage level assignment (ESI scale)
--   - Police case marking
--   - Ambulance arrival tracking
--   - Auto-conversion to inpatient
--   - Critical patient prioritization

-- =====================================================
-- Emergency Registration Table
-- =====================================================
CREATE TABLE IF NOT EXISTS registration_schema.emergency_registration (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Registration Number
    emergency_number VARCHAR(50) NOT NULL UNIQUE,

    -- Patient Information
    patient_id UUID,
    is_unknown_patient BOOLEAN NOT NULL DEFAULT false,
    unknown_patient_identifier VARCHAR(100), -- "UNKNOWN-20250119-001"
    temporary_name VARCHAR(200), -- "Unknown Male #1", "Korban Kecelakaan #2"
    estimated_age INTEGER,
    estimated_gender VARCHAR(10), -- MALE, FEMALE, UNKNOWN

    -- Registration Details
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    registration_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    arrival_mode VARCHAR(30) NOT NULL, -- WALK_IN, AMBULANCE, POLICE, REFERRAL, TRANSFER
    arrival_time TIMESTAMP,

    -- Ambulance Details (if arrival_mode = AMBULANCE)
    ambulance_type VARCHAR(30), -- GOVERNMENT, PRIVATE, HOSPITAL
    ambulance_number VARCHAR(50),
    ambulance_origin VARCHAR(200), -- Origin hospital/location
    paramedic_name VARCHAR(200),
    paramedic_phone VARCHAR(20),

    -- Chief Complaint & Presenting Problem
    chief_complaint TEXT NOT NULL,
    presenting_problem TEXT,
    symptoms TEXT,
    onset_time TIMESTAMP, -- When symptoms started
    duration_minutes INTEGER, -- Duration of symptoms

    -- Triage Information
    triage_level VARCHAR(20) NOT NULL, -- RED, YELLOW, GREEN, BLACK (ESI-based)
    triage_priority INTEGER NOT NULL DEFAULT 3, -- 1=Highest, 5=Lowest
    triage_time TIMESTAMP,
    triaged_by_id UUID, -- Nurse who performed triage
    triaged_by_name VARCHAR(200),

    -- Initial Vital Signs (from triage)
    initial_blood_pressure_systolic INTEGER,
    initial_blood_pressure_diastolic INTEGER,
    initial_heart_rate INTEGER,
    initial_respiratory_rate INTEGER,
    initial_temperature DECIMAL(4,1),
    initial_oxygen_saturation INTEGER,
    initial_gcs_score INTEGER, -- Glasgow Coma Scale (3-15)
    initial_pain_score INTEGER, -- 0-10

    -- Police Case
    is_police_case BOOLEAN NOT NULL DEFAULT false,
    police_case_type VARCHAR(30), -- ACCIDENT, VIOLENCE, ASSAULT, SUSPICIOUS_DEATH, OTHER
    police_report_number VARCHAR(100),
    police_station VARCHAR(200),
    police_officer_name VARCHAR(200),
    police_officer_contact VARCHAR(20),

    -- Accident/Trauma Details
    is_trauma_case BOOLEAN NOT NULL DEFAULT false,
    trauma_type VARCHAR(50), -- MOTOR_VEHICLE, FALL, BURN, PENETRATING, BLUNT, OTHER
    accident_location TEXT,
    accident_time TIMESTAMP,
    mechanism_of_injury TEXT,

    -- Medical Team
    attending_doctor_id UUID,
    attending_doctor_name VARCHAR(200),
    assigned_nurse_id UUID,
    assigned_nurse_name VARCHAR(200),

    -- Room/Bed Assignment
    er_zone VARCHAR(30), -- RED_ZONE, YELLOW_ZONE, GREEN_ZONE, RESUS_ROOM, ISOLATION
    er_bed_number VARCHAR(20),

    -- Status
    status VARCHAR(30) NOT NULL DEFAULT 'REGISTERED',
    -- REGISTERED, TRIAGED, IN_TREATMENT, WAITING_RESULTS, ADMITTED, DISCHARGED,
    -- LEFT_WITHOUT_TREATMENT, TRANSFERRED, DECEASED

    -- Disposition (Outcome)
    disposition VARCHAR(30), -- ADMITTED_INPATIENT, DISCHARGED_HOME, TRANSFERRED, DECEASED, LEFT_AMA, OBSERVATION
    disposition_time TIMESTAMP,
    disposition_notes TEXT,

    -- Conversion to Inpatient
    converted_to_inpatient BOOLEAN NOT NULL DEFAULT false,
    inpatient_admission_id UUID,
    conversion_time TIMESTAMP,

    -- Timing Metrics
    door_to_triage_minutes INTEGER, -- Time from arrival to triage
    door_to_doctor_minutes INTEGER, -- Time from arrival to doctor assessment
    total_er_time_minutes INTEGER, -- Total time in ER

    -- Priority Flags
    is_critical BOOLEAN NOT NULL DEFAULT false,
    requires_isolation BOOLEAN NOT NULL DEFAULT false,
    isolation_reason VARCHAR(100),
    is_infectious BOOLEAN NOT NULL DEFAULT false,
    infectious_disease VARCHAR(100),

    -- Companion/Guardian Information
    companion_name VARCHAR(200),
    companion_relationship VARCHAR(100),
    companion_phone VARCHAR(20),
    companion_address TEXT,

    -- Referral Information
    referred_from VARCHAR(200),
    referral_doctor VARCHAR(200),
    referral_diagnosis TEXT,
    referral_letter_number VARCHAR(100),

    -- Insurance/Payment
    payment_method VARCHAR(30), -- BPJS, CASH, INSURANCE, COMPANY, FREE
    insurance_name VARCHAR(200),
    insurance_number VARCHAR(100),
    guarantee_letter_number VARCHAR(100),

    -- Notes
    medical_history_summary TEXT,
    current_medications TEXT,
    allergies TEXT,
    special_needs TEXT,
    registration_notes TEXT,
    clinical_notes TEXT,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),

    -- Foreign Keys
    CONSTRAINT fk_emergency_patient FOREIGN KEY (patient_id)
        REFERENCES patient_schema.patient(id) ON DELETE SET NULL,
    CONSTRAINT fk_emergency_inpatient FOREIGN KEY (inpatient_admission_id)
        REFERENCES registration_schema.inpatient_admission(id) ON DELETE SET NULL,

    -- Constraints
    CONSTRAINT chk_emergency_gender CHECK (estimated_gender IN ('MALE', 'FEMALE', 'UNKNOWN')),
    CONSTRAINT chk_emergency_triage CHECK (triage_level IN ('RED', 'YELLOW', 'GREEN', 'BLACK', 'WHITE')),
    CONSTRAINT chk_emergency_priority CHECK (triage_priority BETWEEN 1 AND 5),
    CONSTRAINT chk_emergency_gcs CHECK (initial_gcs_score IS NULL OR (initial_gcs_score BETWEEN 3 AND 15)),
    CONSTRAINT chk_emergency_pain CHECK (initial_pain_score IS NULL OR (initial_pain_score BETWEEN 0 AND 10)),
    CONSTRAINT chk_emergency_spo2 CHECK (initial_oxygen_saturation IS NULL OR (initial_oxygen_saturation BETWEEN 0 AND 100))
);

-- =====================================================
-- Triage Assessment Table (Detailed Triage Record)
-- =====================================================
CREATE TABLE IF NOT EXISTS registration_schema.triage_assessment (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Link to Emergency Registration
    emergency_registration_id UUID NOT NULL,

    -- Triage Metadata
    triage_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    triaged_by_id UUID,
    triaged_by_name VARCHAR(200) NOT NULL,
    triage_method VARCHAR(30) NOT NULL DEFAULT 'ESI', -- ESI, ATS, CTAS, MTS

    -- ESI (Emergency Severity Index) - 1 to 5
    esi_level INTEGER NOT NULL,
    -- Level 1: Resuscitation (immediate life-threatening)
    -- Level 2: Emergent (high risk, confusion, severe pain)
    -- Level 3: Urgent (stable, needs multiple resources)
    -- Level 4: Less Urgent (needs one resource)
    -- Level 5: Non-Urgent (no resources needed)

    -- Vital Signs
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    heart_rate INTEGER,
    respiratory_rate INTEGER,
    temperature DECIMAL(4,1),
    oxygen_saturation INTEGER,
    blood_glucose DECIMAL(5,1),

    -- Neurological Assessment
    gcs_eye_opening INTEGER, -- 1-4
    gcs_verbal_response INTEGER, -- 1-5
    gcs_motor_response INTEGER, -- 1-6
    gcs_total INTEGER, -- 3-15
    pupil_response VARCHAR(30), -- EQUAL_REACTIVE, UNEQUAL, NON_REACTIVE
    consciousness_level VARCHAR(30), -- ALERT, VERBAL, PAIN, UNRESPONSIVE (AVPU)

    -- Pain Assessment
    pain_score INTEGER, -- 0-10
    pain_location VARCHAR(200),
    pain_characteristics VARCHAR(200), -- Sharp, Dull, Burning, etc.
    pain_onset VARCHAR(100),

    -- Respiratory Assessment
    respiratory_distress BOOLEAN DEFAULT false,
    airway_status VARCHAR(30), -- PATENT, COMPROMISED, OBSTRUCTED
    breathing_pattern VARCHAR(50), -- NORMAL, LABORED, SHALLOW, IRREGULAR
    oxygen_therapy BOOLEAN DEFAULT false,
    oxygen_delivery_method VARCHAR(50),
    oxygen_flow_rate DECIMAL(4,1),

    -- Cardiovascular Assessment
    peripheral_pulses VARCHAR(30), -- STRONG, WEAK, ABSENT
    capillary_refill_seconds DECIMAL(3,1),
    skin_color VARCHAR(30), -- NORMAL, PALE, CYANOTIC, FLUSHED
    skin_temperature VARCHAR(30), -- WARM, COOL, COLD, HOT

    -- Chief Complaint & History
    chief_complaint TEXT NOT NULL,
    history_present_illness TEXT,
    symptom_onset TIMESTAMP,
    relevant_medical_history TEXT,
    current_medications TEXT,
    allergies TEXT,

    -- Red Flags / Warning Signs
    has_chest_pain BOOLEAN DEFAULT false,
    has_difficulty_breathing BOOLEAN DEFAULT false,
    has_altered_consciousness BOOLEAN DEFAULT false,
    has_severe_bleeding BOOLEAN DEFAULT false,
    has_severe_pain BOOLEAN DEFAULT false,
    has_seizures BOOLEAN DEFAULT false,
    has_poisoning BOOLEAN DEFAULT false,

    -- Resource Needs Assessment
    expected_resources_count INTEGER DEFAULT 0, -- Number of resources needed (lab, imaging, procedures)
    needs_lab_work BOOLEAN DEFAULT false,
    needs_imaging BOOLEAN DEFAULT false,
    needs_procedure BOOLEAN DEFAULT false,
    needs_specialist BOOLEAN DEFAULT false,

    -- Isolation/Infection Control
    requires_isolation BOOLEAN DEFAULT false,
    isolation_type VARCHAR(50), -- AIRBORNE, DROPLET, CONTACT, PROTECTIVE
    suspected_infection VARCHAR(200),

    -- Triage Decision
    recommended_zone VARCHAR(30), -- RED_ZONE, YELLOW_ZONE, GREEN_ZONE, RESUS_ROOM
    triage_category VARCHAR(30), -- IMMEDIATE, URGENT, NON_URGENT, MINOR
    estimated_wait_time_minutes INTEGER,

    -- Notes
    triage_notes TEXT,
    nursing_interventions TEXT,

    -- Re-triage
    is_retriage BOOLEAN DEFAULT false,
    previous_triage_id UUID,
    retriage_reason TEXT,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,

    -- Foreign Keys
    CONSTRAINT fk_triage_emergency FOREIGN KEY (emergency_registration_id)
        REFERENCES registration_schema.emergency_registration(id) ON DELETE CASCADE,
    CONSTRAINT fk_triage_previous FOREIGN KEY (previous_triage_id)
        REFERENCES registration_schema.triage_assessment(id) ON DELETE SET NULL,

    -- Constraints
    CONSTRAINT chk_triage_esi CHECK (esi_level BETWEEN 1 AND 5),
    CONSTRAINT chk_triage_gcs_eye CHECK (gcs_eye_opening IS NULL OR (gcs_eye_opening BETWEEN 1 AND 4)),
    CONSTRAINT chk_triage_gcs_verbal CHECK (gcs_verbal_response IS NULL OR (gcs_verbal_response BETWEEN 1 AND 5)),
    CONSTRAINT chk_triage_gcs_motor CHECK (gcs_motor_response IS NULL OR (gcs_motor_response BETWEEN 1 AND 6)),
    CONSTRAINT chk_triage_gcs_total CHECK (gcs_total IS NULL OR (gcs_total BETWEEN 3 AND 15)),
    CONSTRAINT chk_triage_pain CHECK (pain_score IS NULL OR (pain_score BETWEEN 0 AND 10)),
    CONSTRAINT chk_triage_spo2 CHECK (oxygen_saturation IS NULL OR (oxygen_saturation BETWEEN 0 AND 100))
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Emergency Registration Indexes
CREATE INDEX idx_emergency_number ON registration_schema.emergency_registration(emergency_number);
CREATE INDEX idx_emergency_patient ON registration_schema.emergency_registration(patient_id);
CREATE INDEX idx_emergency_registration_date ON registration_schema.emergency_registration(registration_date);
CREATE INDEX idx_emergency_status ON registration_schema.emergency_registration(status);
CREATE INDEX idx_emergency_triage_level ON registration_schema.emergency_registration(triage_level);
CREATE INDEX idx_emergency_priority ON registration_schema.emergency_registration(triage_priority, is_critical);
CREATE INDEX idx_emergency_police_case ON registration_schema.emergency_registration(is_police_case) WHERE is_police_case = true;
CREATE INDEX idx_emergency_unknown ON registration_schema.emergency_registration(is_unknown_patient) WHERE is_unknown_patient = true;
CREATE INDEX idx_emergency_inpatient ON registration_schema.emergency_registration(inpatient_admission_id) WHERE converted_to_inpatient = true;
CREATE INDEX idx_emergency_active ON registration_schema.emergency_registration(status) WHERE status IN ('REGISTERED', 'TRIAGED', 'IN_TREATMENT', 'WAITING_RESULTS');

-- Triage Assessment Indexes
CREATE INDEX idx_triage_emergency ON registration_schema.triage_assessment(emergency_registration_id);
CREATE INDEX idx_triage_time ON registration_schema.triage_assessment(triage_time);
CREATE INDEX idx_triage_esi ON registration_schema.triage_assessment(esi_level);
CREATE INDEX idx_triage_retriage ON registration_schema.triage_assessment(is_retriage);

-- =====================================================
-- Auto-update Timestamp Trigger
-- =====================================================
CREATE TRIGGER update_emergency_registration_timestamp
    BEFORE UPDATE ON registration_schema.emergency_registration
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_triage_assessment_timestamp
    BEFORE UPDATE ON registration_schema.triage_assessment
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Comments for Documentation
-- =====================================================
COMMENT ON TABLE registration_schema.emergency_registration IS 'Emergency department patient registrations with triage and ambulance tracking';
COMMENT ON TABLE registration_schema.triage_assessment IS 'Detailed triage assessments using ESI methodology';

COMMENT ON COLUMN registration_schema.emergency_registration.triage_level IS 'RED=Critical, YELLOW=Urgent, GREEN=Non-urgent, BLACK=Deceased, WHITE=Minor';
COMMENT ON COLUMN registration_schema.emergency_registration.triage_priority IS '1=Highest priority (immediate), 5=Lowest priority';
COMMENT ON COLUMN registration_schema.emergency_registration.initial_gcs_score IS 'Glasgow Coma Scale: 15=Fully conscious, 3=Deep coma';
COMMENT ON COLUMN registration_schema.triage_assessment.esi_level IS 'Emergency Severity Index: 1=Resuscitation, 2=Emergent, 3=Urgent, 4=Less Urgent, 5=Non-Urgent';

-- =====================================================
-- Sample Emergency Numbers Sequence (Optional)
-- =====================================================
COMMENT ON COLUMN registration_schema.emergency_registration.emergency_number IS 'Format: ER-YYYYMMDD-NNNN';
