-- ============================================================================
-- Flyway Migration V20: Create Phase 4.1 Medical Record Structure Tables
-- Description: Physical Examination, Procedures (ICD-9-CM), Clinical Templates,
--              and Digital Signature support for SOAP notes
-- Author: HMS Development Team
-- Date: 2025-11-20
-- ============================================================================

-- ============================================================================
-- PHYSICAL EXAMINATION TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.physical_examination (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,
    examination_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- General Appearance
    general_appearance TEXT,
    conscious_level VARCHAR(50),
    nutritional_status VARCHAR(50),
    hydration_status VARCHAR(50),
    distress_level VARCHAR(50),

    -- Head, Eyes, Ears, Nose, Throat (HEENT)
    head TEXT,
    eyes TEXT,
    ears TEXT,
    nose TEXT,
    throat TEXT,
    neck TEXT,

    -- Cardiovascular System
    cardiovascular TEXT,
    heart_sounds VARCHAR(200),
    peripheral_pulses VARCHAR(200),
    edema TEXT,

    -- Respiratory System
    respiratory TEXT,
    chest_inspection TEXT,
    chest_auscultation TEXT,
    chest_percussion TEXT,

    -- Gastrointestinal/Abdomen
    abdomen TEXT,
    abdomen_inspection TEXT,
    abdomen_auscultation TEXT,
    abdomen_palpation TEXT,
    abdomen_percussion TEXT,

    -- Neurological System
    neurological TEXT,
    mental_status TEXT,
    cranial_nerves TEXT,
    motor_function TEXT,
    sensory_function TEXT,
    reflexes TEXT,
    gait TEXT,

    -- Musculoskeletal System
    musculoskeletal TEXT,
    spine TEXT,
    joints TEXT,
    extremities TEXT,

    -- Skin and Integumentary
    skin TEXT,
    mucous_membranes TEXT,

    -- Additional Systems
    genitourinary TEXT,
    lymphatic TEXT,
    breasts TEXT,

    -- Overall Assessment
    overall_impression TEXT,
    abnormal_findings TEXT,
    clinical_significance TEXT,

    -- Examiner Information
    examiner_id UUID NOT NULL,
    examiner_name VARCHAR(200) NOT NULL,
    examiner_specialty VARCHAR(100),

    -- Digital Signature
    is_signed BOOLEAN DEFAULT FALSE,
    signed_at TIMESTAMP,
    digital_signature TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- ============================================================================
-- ENCOUNTER PROCEDURES TABLE (ICD-9-CM Coding)
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.encounter_procedures (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    procedure_number VARCHAR(50) NOT NULL UNIQUE,

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,

    -- Procedure Coding (ICD-9-CM)
    procedure_code VARCHAR(10) NOT NULL,
    procedure_description TEXT NOT NULL,
    procedure_name VARCHAR(300) NOT NULL,

    -- Classification
    procedure_type VARCHAR(50) NOT NULL,
    procedure_category VARCHAR(50),
    body_site VARCHAR(200),
    laterality VARCHAR(20),

    -- Timing
    procedure_date TIMESTAMP NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_minutes INTEGER,

    -- Status and Outcome
    procedure_status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
    outcome VARCHAR(30),
    outcome_notes TEXT,

    -- Providers
    primary_provider_id UUID NOT NULL,
    primary_provider_name VARCHAR(200) NOT NULL,
    assisting_providers TEXT,
    anesthesiologist_id UUID,
    anesthesiologist_name VARCHAR(200),

    -- Clinical Details
    indication TEXT,
    technique TEXT,
    findings TEXT,
    specimens_collected TEXT,
    complications TEXT,
    blood_loss_ml INTEGER,

    -- Anesthesia
    anesthesia_type VARCHAR(50),
    anesthesia_notes TEXT,

    -- Location
    location_name VARCHAR(200),
    room_number VARCHAR(50),

    -- Consent and Documentation
    consent_obtained BOOLEAN DEFAULT FALSE,
    consent_form_id VARCHAR(100),
    consent_date TIMESTAMP,
    pre_procedure_checklist_completed BOOLEAN DEFAULT FALSE,
    post_procedure_checklist_completed BOOLEAN DEFAULT FALSE,

    -- Post-Procedure Care
    post_procedure_instructions TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_date TIMESTAMP,
    recovery_notes TEXT,

    -- Billing and Coding
    billable BOOLEAN DEFAULT TRUE,
    charge_amount DECIMAL(15,2),
    modifier_codes VARCHAR(100),
    billed BOOLEAN DEFAULT FALSE,
    billed_at TIMESTAMP,

    -- Report and Documentation
    procedure_report TEXT,
    report_dictated BOOLEAN DEFAULT FALSE,
    report_signed BOOLEAN DEFAULT FALSE,
    signed_at TIMESTAMP,
    digital_signature TEXT,

    -- Images and Attachments
    images_captured BOOLEAN DEFAULT FALSE,
    image_ids TEXT,
    video_recorded BOOLEAN DEFAULT FALSE,
    video_ids TEXT,

    -- Quality and Safety
    timeout_performed BOOLEAN DEFAULT FALSE,
    site_marking_verified BOOLEAN DEFAULT FALSE,
    equipment_used TEXT,
    implants_used TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- ============================================================================
-- CLINICAL NOTE TEMPLATES TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.clinical_note_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_code VARCHAR(50) NOT NULL UNIQUE,
    template_name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Classification
    template_type VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    specialty VARCHAR(100),
    diagnosis_codes TEXT,
    procedure_codes TEXT,

    -- SOAP Template Content
    subjective_template TEXT,
    objective_template TEXT,
    assessment_template TEXT,
    plan_template TEXT,

    -- Physical Exam Template
    physical_exam_template TEXT,

    -- Procedure Template
    procedure_template TEXT,
    indication_template TEXT,
    technique_template TEXT,
    findings_template TEXT,

    -- Custom Fields
    custom_fields TEXT,
    required_fields TEXT,
    field_validations TEXT,

    -- Common Clinical Scenarios
    common_medications TEXT,
    common_orders TEXT,
    common_diagnoses TEXT,
    clinical_guidelines TEXT,
    warning_signs TEXT,

    -- Instructions and Help
    usage_instructions TEXT,
    examples TEXT,
    tips TEXT,

    -- Version Control
    template_version INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    supersedes_template_id UUID,

    -- Usage Statistics
    usage_count BIGINT DEFAULT 0,
    last_used_at TIMESTAMP,

    -- Access Control
    is_public BOOLEAN DEFAULT TRUE,
    created_by_id UUID,
    created_by_name VARCHAR(200),
    department_id UUID,
    facility_id UUID,

    -- Approval/Review
    requires_approval BOOLEAN DEFAULT FALSE,
    approved BOOLEAN DEFAULT FALSE,
    approved_by_id UUID,
    approved_by_name VARCHAR(200),
    approved_at TIMESTAMP,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- ============================================================================
-- UPDATE PROGRESS_NOTE TABLE (Add Digital Signature Fields)
-- ============================================================================
ALTER TABLE clinical_schema.progress_note
ADD COLUMN IF NOT EXISTS is_signed BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS signed_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS digital_signature TEXT,
ADD COLUMN IF NOT EXISTS signature_method VARCHAR(50),
ADD COLUMN IF NOT EXISTS signature_ip_address VARCHAR(50),
ADD COLUMN IF NOT EXISTS signature_device VARCHAR(200),
ADD COLUMN IF NOT EXISTS signature_verification_code VARCHAR(100),
ADD COLUMN IF NOT EXISTS template_id UUID,
ADD COLUMN IF NOT EXISTS template_code VARCHAR(50);

-- ============================================================================
-- INDEXES
-- ============================================================================

-- Physical Examination Indexes
CREATE INDEX IF NOT EXISTS idx_physical_exam_encounter ON clinical_schema.physical_examination(encounter_id);
CREATE INDEX IF NOT EXISTS idx_physical_exam_patient ON clinical_schema.physical_examination(patient_id);
CREATE INDEX IF NOT EXISTS idx_physical_exam_date ON clinical_schema.physical_examination(examination_date);

-- Encounter Procedures Indexes
CREATE INDEX IF NOT EXISTS idx_procedure_encounter ON clinical_schema.encounter_procedures(encounter_id);
CREATE INDEX IF NOT EXISTS idx_procedure_patient ON clinical_schema.encounter_procedures(patient_id);
CREATE INDEX IF NOT EXISTS idx_procedure_code ON clinical_schema.encounter_procedures(procedure_code);
CREATE INDEX IF NOT EXISTS idx_procedure_date ON clinical_schema.encounter_procedures(procedure_date);
CREATE INDEX IF NOT EXISTS idx_procedure_provider ON clinical_schema.encounter_procedures(primary_provider_id);
CREATE INDEX IF NOT EXISTS idx_procedure_status ON clinical_schema.encounter_procedures(procedure_status);

-- Clinical Note Templates Indexes
CREATE INDEX IF NOT EXISTS idx_template_code ON clinical_schema.clinical_note_templates(template_code);
CREATE INDEX IF NOT EXISTS idx_template_specialty ON clinical_schema.clinical_note_templates(specialty);
CREATE INDEX IF NOT EXISTS idx_template_category ON clinical_schema.clinical_note_templates(category);
CREATE INDEX IF NOT EXISTS idx_template_active ON clinical_schema.clinical_note_templates(is_active);

-- Progress Note Digital Signature Indexes
CREATE INDEX IF NOT EXISTS idx_progress_note_signed ON clinical_schema.progress_note(is_signed);
CREATE INDEX IF NOT EXISTS idx_progress_note_template ON clinical_schema.progress_note(template_id);

-- ============================================================================
-- COMMENTS
-- ============================================================================
COMMENT ON TABLE clinical_schema.physical_examination IS 'Physical examination findings and documentation';
COMMENT ON TABLE clinical_schema.encounter_procedures IS 'Procedures performed during encounters with ICD-9-CM coding';
COMMENT ON TABLE clinical_schema.clinical_note_templates IS 'Predefined templates for clinical documentation';

COMMENT ON COLUMN clinical_schema.encounter_procedures.procedure_code IS 'ICD-9-CM procedure code';
COMMENT ON COLUMN clinical_schema.physical_examination.digital_signature IS 'Base64 encoded signature or signature ID';
COMMENT ON COLUMN clinical_schema.encounter_procedures.digital_signature IS 'Base64 encoded signature or signature ID';
COMMENT ON COLUMN clinical_schema.progress_note.digital_signature IS 'Base64 encoded signature or signature ID';
