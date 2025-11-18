-- ============================================================================
-- Flyway Migration V4: Create Registration and Clinical Module Tables (Basic)
-- Description: Creates initial tables for registration and clinical workflows
-- Author: HMS Development Team
-- Date: 2025-01-18
-- ============================================================================

-- ============================================================================
-- SECTION 1: Registration Module Tables
-- ============================================================================

-- Patient Registration (Outpatient and Inpatient)
CREATE TABLE IF NOT EXISTS registration_schema.registration (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Patient reference
    patient_id UUID NOT NULL REFERENCES patient_schema.patient(id),

    -- Registration details
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    registration_type VARCHAR(20) NOT NULL, -- OUTPATIENT, INPATIENT, EMERGENCY

    -- Polyclinic/Department
    polyclinic_id UUID REFERENCES master_schema.polyclinic(id),
    department_id UUID REFERENCES master_schema.department(id),

    -- Doctor assignment
    doctor_id UUID, -- Will reference staff table in future migrations
    doctor_name VARCHAR(200), -- Temporary until staff module

    -- BPJS Information
    is_bpjs BOOLEAN DEFAULT false,
    sep_number VARCHAR(50), -- Surat Eligibilitas Peserta number
    sep_date TIMESTAMP,
    rujukan_number VARCHAR(50), -- Referral number
    rujukan_date DATE,
    rujukan_provider VARCHAR(200),

    -- Payer information
    payer_type VARCHAR(20) NOT NULL, -- BPJS, CASH, INSURANCE, COMPANY
    insurance_name VARCHAR(100),
    insurance_number VARCHAR(50),

    -- Visit status
    status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED', -- REGISTERED, IN_PROGRESS, COMPLETED, CANCELLED

    -- Appointment reference (if from appointment)
    appointment_id UUID,

    -- Queue information
    queue_number VARCHAR(20),
    queue_time TIMESTAMP,

    -- Check-in/Check-out
    checkin_time TIMESTAMP,
    checkout_time TIMESTAMP,

    -- Notes
    chief_complaint TEXT, -- Keluhan utama
    notes TEXT,

    -- Cancellation
    cancelled_at TIMESTAMP,
    cancelled_by VARCHAR(100),
    cancellation_reason TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_registration_patient ON registration_schema.registration(patient_id);
CREATE INDEX idx_registration_number ON registration_schema.registration(registration_number);
CREATE INDEX idx_registration_date ON registration_schema.registration(registration_date);
CREATE INDEX idx_registration_type ON registration_schema.registration(registration_type);
CREATE INDEX idx_registration_status ON registration_schema.registration(status);
CREATE INDEX idx_registration_sep ON registration_schema.registration(sep_number);
CREATE INDEX idx_registration_poly ON registration_schema.registration(polyclinic_id);

COMMENT ON TABLE registration_schema.registration IS 'Patient registration records (outpatient, inpatient, emergency)';
COMMENT ON COLUMN registration_schema.registration.sep_number IS 'SEP number from BPJS VClaim';

-- ============================================================================
-- SECTION 2: Clinical Module Tables
-- ============================================================================

-- Clinical Notes (SOAP format)
CREATE TABLE IF NOT EXISTS clinical_schema.clinical_note (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Registration/Visit reference
    registration_id UUID NOT NULL REFERENCES registration_schema.registration(id),
    patient_id UUID NOT NULL REFERENCES patient_schema.patient(id),

    -- SOAP Components
    subjective TEXT, -- S: Keluhan pasien, anamnesis
    objective TEXT, -- O: Pemeriksaan fisik, vital signs
    assessment TEXT, -- A: Diagnosis, assessment
    plan TEXT, -- P: Rencana terapi, tindakan

    -- Clinical information
    note_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note_type VARCHAR(50), -- INITIAL, PROGRESS, DISCHARGE, etc.

    -- Provider
    doctor_id UUID, -- Will reference staff table
    doctor_name VARCHAR(200),

    -- Digital signature
    signed BOOLEAN DEFAULT false,
    signed_at TIMESTAMP,
    signature_data TEXT, -- Digital signature hash/token

    -- SATUSEHAT submission
    satusehat_submitted BOOLEAN DEFAULT false,
    satusehat_submission_date TIMESTAMP,
    satusehat_encounter_id VARCHAR(100),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_clinical_note_registration ON clinical_schema.clinical_note(registration_id);
CREATE INDEX idx_clinical_note_patient ON clinical_schema.clinical_note(patient_id);
CREATE INDEX idx_clinical_note_date ON clinical_schema.clinical_note(note_date);
CREATE INDEX idx_clinical_note_type ON clinical_schema.clinical_note(note_type);
CREATE INDEX idx_clinical_note_signed ON clinical_schema.clinical_note(signed);

COMMENT ON TABLE clinical_schema.clinical_note IS 'Clinical documentation in SOAP format';

-- Diagnoses (ICD-10)
CREATE TABLE IF NOT EXISTS clinical_schema.diagnosis (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- References
    clinical_note_id UUID REFERENCES clinical_schema.clinical_note(id),
    registration_id UUID NOT NULL REFERENCES registration_schema.registration(id),
    patient_id UUID NOT NULL REFERENCES patient_schema.patient(id),

    -- ICD-10 Code
    icd10_id UUID REFERENCES master_schema.icd10(id),
    icd10_code VARCHAR(10) NOT NULL,
    icd10_description TEXT NOT NULL,

    -- Diagnosis type
    diagnosis_type VARCHAR(20) NOT NULL, -- PRIMARY, SECONDARY, DIFFERENTIAL
    is_primary BOOLEAN DEFAULT false,

    -- Clinical details
    diagnosis_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,

    -- Provider
    doctor_id UUID,
    doctor_name VARCHAR(200),

    -- SATUSEHAT submission
    satusehat_submitted BOOLEAN DEFAULT false,
    satusehat_submission_date TIMESTAMP,
    satusehat_condition_id VARCHAR(100),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_diagnosis_clinical_note ON clinical_schema.diagnosis(clinical_note_id);
CREATE INDEX idx_diagnosis_registration ON clinical_schema.diagnosis(registration_id);
CREATE INDEX idx_diagnosis_patient ON clinical_schema.diagnosis(patient_id);
CREATE INDEX idx_diagnosis_icd10 ON clinical_schema.diagnosis(icd10_id);
CREATE INDEX idx_diagnosis_type ON clinical_schema.diagnosis(diagnosis_type);
CREATE INDEX idx_diagnosis_primary ON clinical_schema.diagnosis(is_primary);
CREATE INDEX idx_diagnosis_date ON clinical_schema.diagnosis(diagnosis_date);

COMMENT ON TABLE clinical_schema.diagnosis IS 'Patient diagnoses using ICD-10 codes';

-- Vital Signs
CREATE TABLE IF NOT EXISTS clinical_schema.vital_signs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- References
    clinical_note_id UUID REFERENCES clinical_schema.clinical_note(id),
    registration_id UUID NOT NULL REFERENCES registration_schema.registration(id),
    patient_id UUID NOT NULL REFERENCES patient_schema.patient(id),

    -- Measurement time
    measurement_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Vital signs measurements
    systolic_bp INTEGER, -- mmHg
    diastolic_bp INTEGER, -- mmHg
    heart_rate INTEGER, -- bpm
    respiratory_rate INTEGER, -- breaths/min
    temperature DECIMAL(4,1), -- Celsius
    oxygen_saturation DECIMAL(5,2), -- SpO2 percentage
    weight DECIMAL(6,2), -- kg
    height DECIMAL(5,2), -- cm
    bmi DECIMAL(5,2), -- Calculated

    -- Pain scale
    pain_scale INTEGER CHECK (pain_scale BETWEEN 0 AND 10),

    -- Glasgow Coma Scale (GCS)
    gcs_eye INTEGER CHECK (gcs_eye BETWEEN 1 AND 4),
    gcs_verbal INTEGER CHECK (gcs_verbal BETWEEN 1 AND 5),
    gcs_motor INTEGER CHECK (gcs_motor BETWEEN 1 AND 6),
    gcs_total INTEGER CHECK (gcs_total BETWEEN 3 AND 15),

    -- Measured by
    measured_by VARCHAR(200),
    measured_by_id UUID,

    -- Notes
    notes TEXT,

    -- SATUSEHAT submission
    satusehat_submitted BOOLEAN DEFAULT false,
    satusehat_submission_date TIMESTAMP,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_vital_signs_clinical_note ON clinical_schema.vital_signs(clinical_note_id);
CREATE INDEX idx_vital_signs_registration ON clinical_schema.vital_signs(registration_id);
CREATE INDEX idx_vital_signs_patient ON clinical_schema.vital_signs(patient_id);
CREATE INDEX idx_vital_signs_time ON clinical_schema.vital_signs(measurement_time);

COMMENT ON TABLE clinical_schema.vital_signs IS 'Patient vital signs measurements';

-- ============================================================================
-- SECTION 3: Registration Number Sequence
-- ============================================================================

CREATE SEQUENCE IF NOT EXISTS registration_schema.registration_number_sequence START WITH 1;

COMMENT ON SEQUENCE registration_schema.registration_number_sequence IS 'Sequence for generating registration numbers';

-- ============================================================================
-- SECTION 4: Add Triggers for updated_at
-- ============================================================================

CREATE TRIGGER update_registration_timestamp
    BEFORE UPDATE ON registration_schema.registration
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_clinical_note_timestamp
    BEFORE UPDATE ON clinical_schema.clinical_note
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_diagnosis_timestamp
    BEFORE UPDATE ON clinical_schema.diagnosis
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_vital_signs_timestamp
    BEFORE UPDATE ON clinical_schema.vital_signs
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================================
-- Summary
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V4 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Registration module tables created:';
    RAISE NOTICE '  - registration (outpatient/inpatient/emergency)';
    RAISE NOTICE '  - registration_number_sequence';
    RAISE NOTICE '';
    RAISE NOTICE 'Clinical module tables created:';
    RAISE NOTICE '  - clinical_note (SOAP format)';
    RAISE NOTICE '  - diagnosis (ICD-10 codes)';
    RAISE NOTICE '  - vital_signs (BP, HR, RR, Temp, SpO2, etc.)';
    RAISE NOTICE '';
    RAISE NOTICE 'Indonesian-specific features:';
    RAISE NOTICE '  - SEP number integration';
    RAISE NOTICE '  - BPJS payer type';
    RAISE NOTICE '  - Rujukan (referral) tracking';
    RAISE NOTICE '  - SATUSEHAT submission tracking';
    RAISE NOTICE '  - Digital signature support';
    RAISE NOTICE '============================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Next steps:';
    RAISE NOTICE '  - Additional migrations for other modules';
    RAISE NOTICE '  - Billing, Pharmacy, Laboratory, Radiology tables';
    RAISE NOTICE '  - Integration tables (BPJS, SATUSEHAT)';
    RAISE NOTICE '============================================';
END $$;