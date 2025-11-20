-- ============================================================================
-- Flyway Migration V23: Create Procedure Management Tables
-- Description: Phase 4.3 Procedure Management - ICD-9-CM codes, templates,
--              scheduling, checklists, consent forms, and monitoring
-- Author: HMS Development Team
-- Date: 2025-01-20
-- ============================================================================

-- Create ICD-9-CM procedure codes master data table
CREATE TABLE IF NOT EXISTS clinical_schema.icd9cm_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- ICD-9-CM Code Structure
    code VARCHAR(10) NOT NULL UNIQUE,
    description_en TEXT NOT NULL,
    description_id TEXT NOT NULL,
    short_description VARCHAR(255),

    -- Classification
    category_code VARCHAR(10),
    category_name_en VARCHAR(500),
    category_name_id VARCHAR(500),
    procedure_type VARCHAR(50),
    specialty VARCHAR(100),

    -- Clinical Details
    requires_anesthesia BOOLEAN DEFAULT FALSE,
    anesthesia_type_recommended VARCHAR(50),
    avg_duration_minutes INTEGER,
    complexity_level VARCHAR(20),
    body_system VARCHAR(100),

    -- Usage Tracking
    usage_count BIGINT NOT NULL DEFAULT 0,
    is_common BOOLEAN NOT NULL DEFAULT FALSE,

    -- Billing and Coding
    is_billable BOOLEAN NOT NULL DEFAULT TRUE,
    base_cost DECIMAL(15,2),
    requires_pre_authorization BOOLEAN DEFAULT FALSE,
    insurance_coverage_notes TEXT,

    -- Consent and Documentation
    requires_informed_consent BOOLEAN DEFAULT TRUE,
    consent_form_template TEXT,
    requires_pre_procedure_checklist BOOLEAN DEFAULT FALSE,
    checklist_template_id UUID,

    -- Operating Room Requirements
    requires_operating_room BOOLEAN DEFAULT FALSE,
    room_setup_time_minutes INTEGER,
    required_equipment TEXT,
    required_staff_roles TEXT,

    -- Status
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    effective_date DATE,
    deprecated_date DATE,
    replaced_by_code VARCHAR(10),

    -- Search and Display
    search_terms TEXT,

    -- Notes
    clinical_notes TEXT,
    coding_notes TEXT,
    safety_notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_icd9cm_code ON clinical_schema.icd9cm_codes(code);
CREATE INDEX idx_icd9cm_category ON clinical_schema.icd9cm_codes(category_code);
CREATE INDEX idx_icd9cm_active ON clinical_schema.icd9cm_codes(is_active);
CREATE INDEX idx_icd9cm_search ON clinical_schema.icd9cm_codes(code, description_en, description_id);

COMMENT ON TABLE clinical_schema.icd9cm_codes IS 'ICD-9-CM procedure codes master data';

-- Create procedure templates table
CREATE TABLE IF NOT EXISTS clinical_schema.procedure_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Template Identification
    template_name VARCHAR(300) NOT NULL,
    template_code VARCHAR(50) UNIQUE,
    specialty VARCHAR(100) NOT NULL,
    department_code VARCHAR(20),

    -- ICD-9-CM Code Reference
    icd9cm_code_id UUID REFERENCES clinical_schema.icd9cm_codes(id),
    procedure_code VARCHAR(10),

    -- Procedure Definition
    procedure_name VARCHAR(300) NOT NULL,
    procedure_description TEXT,
    procedure_type VARCHAR(50),
    procedure_category VARCHAR(50),

    -- Template Content
    indication_template TEXT,
    technique_template TEXT,
    findings_template TEXT,
    complications_list TEXT,

    -- Clinical Parameters
    typical_duration_minutes INTEGER,
    anesthesia_type VARCHAR(50),
    body_site VARCHAR(200),
    typical_blood_loss_ml INTEGER,

    -- Requirements
    requires_operating_room BOOLEAN DEFAULT FALSE,
    requires_informed_consent BOOLEAN DEFAULT TRUE,
    requires_pre_procedure_checklist BOOLEAN DEFAULT FALSE,
    checklist_template_id UUID,

    -- Staff and Equipment
    required_staff_roles TEXT,
    required_equipment TEXT,
    required_supplies TEXT,
    required_instruments TEXT,

    -- Post-Procedure Care
    post_procedure_instructions TEXT,
    recovery_monitoring TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_days_after INTEGER,
    discharge_criteria TEXT,

    -- Billing
    estimated_cost DECIMAL(15,2),
    billing_notes TEXT,

    -- Documentation Templates
    consent_form_template TEXT,
    operative_report_template TEXT,
    discharge_summary_template TEXT,

    -- Quality and Safety
    timeout_required BOOLEAN DEFAULT FALSE,
    site_marking_required BOOLEAN DEFAULT FALSE,
    safety_checklist TEXT,
    contraindications TEXT,
    precautions TEXT,

    -- Usage and Status
    usage_count BIGINT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    is_hospital_standard BOOLEAN DEFAULT FALSE,

    -- Metadata
    clinical_guidelines_reference TEXT,
    evidence_level VARCHAR(20),
    last_reviewed_date DATE,
    notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_procedure_template_specialty ON clinical_schema.procedure_templates(specialty);
CREATE INDEX idx_procedure_template_icd9 ON clinical_schema.procedure_templates(icd9cm_code_id);
CREATE INDEX idx_procedure_template_active ON clinical_schema.procedure_templates(is_active);
CREATE INDEX idx_procedure_template_name ON clinical_schema.procedure_templates(template_name);

COMMENT ON TABLE clinical_schema.procedure_templates IS 'Procedure templates per specialty';

-- Create procedure schedules table
CREATE TABLE IF NOT EXISTS clinical_schema.procedure_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- References
    encounter_procedure_id UUID REFERENCES clinical_schema.encounter_procedures(id),
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200) NOT NULL,
    encounter_id UUID,

    -- Procedure Details
    procedure_code VARCHAR(10),
    procedure_name VARCHAR(300) NOT NULL,
    procedure_type VARCHAR(50),
    specialty VARCHAR(100),

    -- Scheduling
    scheduled_date DATE NOT NULL,
    scheduled_start_time TIMESTAMP NOT NULL,
    scheduled_end_time TIMESTAMP,
    estimated_duration_minutes INTEGER,
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,

    -- Operating Room
    operating_room_id UUID,
    operating_room_name VARCHAR(100),
    room_type VARCHAR(50),
    room_reserved BOOLEAN DEFAULT FALSE,
    setup_time_minutes INTEGER,
    cleanup_time_minutes INTEGER,

    -- Staff Scheduling
    primary_surgeon_id UUID NOT NULL,
    primary_surgeon_name VARCHAR(200) NOT NULL,
    assisting_surgeons TEXT,
    anesthesiologist_id UUID,
    anesthesiologist_name VARCHAR(200),
    nursing_staff TEXT,
    scrub_nurse_id UUID,
    circulating_nurse_id UUID,

    -- Equipment and Resources
    required_equipment TEXT,
    equipment_reserved BOOLEAN DEFAULT FALSE,
    special_equipment_notes TEXT,
    required_blood_units INTEGER,
    blood_reserved BOOLEAN DEFAULT FALSE,

    -- Status
    schedule_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'ROUTINE',
    is_emergency BOOLEAN DEFAULT FALSE,

    -- Pre-Procedure Preparation
    pre_op_assessment_completed BOOLEAN DEFAULT FALSE,
    pre_op_clearance_obtained BOOLEAN DEFAULT FALSE,
    consent_obtained BOOLEAN DEFAULT FALSE,
    consent_form_id UUID,
    npo_status VARCHAR(50),
    npo_since TIMESTAMP,

    -- Notifications
    patient_notified BOOLEAN DEFAULT FALSE,
    patient_notification_date TIMESTAMP,
    surgeon_notified BOOLEAN DEFAULT FALSE,
    anesthesia_notified BOOLEAN DEFAULT FALSE,

    -- Cancellation
    cancelled_at TIMESTAMP,
    cancelled_by_id UUID,
    cancelled_by_name VARCHAR(200),
    cancellation_reason TEXT,

    -- Rescheduling
    rescheduled_from_id UUID,
    reschedule_reason TEXT,
    reschedule_count INTEGER DEFAULT 0,

    -- Notes
    special_instructions TEXT,
    notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_proc_schedule_procedure ON clinical_schema.procedure_schedules(encounter_procedure_id);
CREATE INDEX idx_proc_schedule_date ON clinical_schema.procedure_schedules(scheduled_date, scheduled_start_time);
CREATE INDEX idx_proc_schedule_room ON clinical_schema.procedure_schedules(operating_room_id);
CREATE INDEX idx_proc_schedule_surgeon ON clinical_schema.procedure_schedules(primary_surgeon_id);
CREATE INDEX idx_proc_schedule_status ON clinical_schema.procedure_schedules(schedule_status);
CREATE INDEX idx_proc_schedule_patient ON clinical_schema.procedure_schedules(patient_id);

COMMENT ON TABLE clinical_schema.procedure_schedules IS 'Procedure scheduling with operating room integration';

-- Create pre-procedure checklists table
CREATE TABLE IF NOT EXISTS clinical_schema.pre_procedure_checklists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    encounter_procedure_id UUID REFERENCES clinical_schema.encounter_procedures(id),
    procedure_schedule_id UUID REFERENCES clinical_schema.procedure_schedules(id),
    patient_id UUID NOT NULL,

    completion_status VARCHAR(20) DEFAULT 'INCOMPLETE',
    completed_at TIMESTAMP,
    completed_by_id UUID,
    completed_by_name VARCHAR(200),
    verified_by_id UUID,
    verified_by_name VARCHAR(200),
    notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_pre_checklist_procedure ON clinical_schema.pre_procedure_checklists(encounter_procedure_id);
CREATE INDEX idx_pre_checklist_schedule ON clinical_schema.pre_procedure_checklists(procedure_schedule_id);
CREATE INDEX idx_pre_checklist_status ON clinical_schema.pre_procedure_checklists(completion_status);

COMMENT ON TABLE clinical_schema.pre_procedure_checklists IS 'Pre-procedure safety checklists';

-- Create checklist items table
CREATE TABLE IF NOT EXISTS clinical_schema.checklist_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    checklist_id UUID NOT NULL REFERENCES clinical_schema.pre_procedure_checklists(id) ON DELETE CASCADE,
    item_order INTEGER,
    item_text TEXT NOT NULL,
    category VARCHAR(50),
    is_checked BOOLEAN DEFAULT FALSE,
    is_required BOOLEAN DEFAULT TRUE,
    checked_at TIMESTAMP,
    checked_by_id UUID,
    checked_by_name VARCHAR(200),
    response_value VARCHAR(500),
    notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_checklist_item_checklist ON clinical_schema.checklist_items(checklist_id);
CREATE INDEX idx_checklist_item_order ON clinical_schema.checklist_items(checklist_id, item_order);

COMMENT ON TABLE clinical_schema.checklist_items IS 'Individual checklist items';

-- Create consent forms table
CREATE TABLE IF NOT EXISTS clinical_schema.consent_forms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    consent_number VARCHAR(50) UNIQUE,
    encounter_procedure_id UUID REFERENCES clinical_schema.encounter_procedures(id),
    patient_id UUID NOT NULL,
    patient_name VARCHAR(200) NOT NULL,
    procedure_name VARCHAR(300) NOT NULL,
    procedure_description TEXT,

    -- Risks and Benefits
    risks_explained TEXT,
    benefits_explained TEXT,
    alternatives_explained TEXT,

    -- Consent Details
    consent_given BOOLEAN DEFAULT FALSE,
    consent_date TIMESTAMP,
    consent_status VARCHAR(20) DEFAULT 'PENDING',

    -- Patient Understanding
    patient_understands BOOLEAN DEFAULT FALSE,
    questions_answered BOOLEAN DEFAULT FALSE,
    interpreter_used BOOLEAN DEFAULT FALSE,
    interpreter_name VARCHAR(200),

    -- Signatures
    patient_signature TEXT,
    patient_signed_at TIMESTAMP,
    guardian_name VARCHAR(200),
    guardian_relationship VARCHAR(100),
    guardian_signature TEXT,
    guardian_signed_at TIMESTAMP,
    witness_name VARCHAR(200),
    witness_signature TEXT,
    witness_signed_at TIMESTAMP,
    physician_id UUID NOT NULL,
    physician_name VARCHAR(200) NOT NULL,
    physician_signature TEXT,
    physician_signed_at TIMESTAMP,

    -- Document
    form_template_id UUID,
    form_content TEXT,
    document_id UUID,
    notes TEXT,

    -- Withdrawal
    withdrawn_at TIMESTAMP,
    withdrawal_reason TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_consent_procedure ON clinical_schema.consent_forms(encounter_procedure_id);
CREATE INDEX idx_consent_patient ON clinical_schema.consent_forms(patient_id);
CREATE INDEX idx_consent_status ON clinical_schema.consent_forms(consent_status);
CREATE INDEX idx_consent_date ON clinical_schema.consent_forms(consent_date);

COMMENT ON TABLE clinical_schema.consent_forms IS 'Informed consent forms for procedures';

-- Create procedure monitoring table
CREATE TABLE IF NOT EXISTS clinical_schema.procedure_monitoring (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    encounter_procedure_id UUID NOT NULL REFERENCES clinical_schema.encounter_procedures(id),
    patient_id UUID NOT NULL,
    monitoring_time TIMESTAMP NOT NULL,
    monitoring_interval VARCHAR(20),
    minutes_post_procedure INTEGER,

    -- Vital Signs
    systolic_bp INTEGER,
    diastolic_bp INTEGER,
    heart_rate INTEGER,
    respiratory_rate INTEGER,
    temperature DOUBLE PRECISION,
    oxygen_saturation INTEGER,
    pain_score INTEGER,

    -- Consciousness and Neurological
    consciousness_level VARCHAR(50),
    glasgow_coma_score INTEGER,
    pupils_equal BOOLEAN DEFAULT TRUE,
    pupils_reactive BOOLEAN DEFAULT TRUE,

    -- Respiratory
    airway_patent BOOLEAN DEFAULT TRUE,
    breathing_adequacy VARCHAR(50),
    oxygen_support VARCHAR(50),
    oxygen_flow_rate INTEGER,

    -- Circulation
    peripheral_pulses VARCHAR(100),
    capillary_refill VARCHAR(50),
    skin_color VARCHAR(50),
    skin_temperature VARCHAR(50),

    -- Wound/Surgical Site
    dressing_intact BOOLEAN DEFAULT TRUE,
    bleeding_status VARCHAR(50),
    drainage_amount VARCHAR(50),
    drainage_type VARCHAR(50),
    swelling VARCHAR(50),
    signs_of_infection BOOLEAN DEFAULT FALSE,

    -- Gastrointestinal
    nausea_present BOOLEAN DEFAULT FALSE,
    vomiting_present BOOLEAN DEFAULT FALSE,
    bowel_sounds VARCHAR(50),
    npo_status VARCHAR(50),

    -- Genitourinary
    urine_output_ml INTEGER,
    voided_spontaneously BOOLEAN DEFAULT FALSE,
    catheter_in_place BOOLEAN DEFAULT FALSE,

    -- Mobility
    mobility_status VARCHAR(50),
    movement_of_extremities VARCHAR(100),

    -- Medications
    pain_medication_given BOOLEAN DEFAULT FALSE,
    antiemetic_given BOOLEAN DEFAULT FALSE,
    medications_administered TEXT,

    -- Overall Assessment
    recovery_status VARCHAR(50) DEFAULT 'RECOVERING',
    complications_noted BOOLEAN DEFAULT FALSE,
    complications_description TEXT,
    interventions_required TEXT,

    -- Discharge Readiness
    ready_for_discharge BOOLEAN DEFAULT FALSE,
    discharge_criteria_met BOOLEAN DEFAULT FALSE,

    -- Staff
    monitored_by_id UUID,
    monitored_by_name VARCHAR(200),
    notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_proc_monitor_procedure ON clinical_schema.procedure_monitoring(encounter_procedure_id);
CREATE INDEX idx_proc_monitor_patient ON clinical_schema.procedure_monitoring(patient_id);
CREATE INDEX idx_proc_monitor_time ON clinical_schema.procedure_monitoring(monitoring_time);
CREATE INDEX idx_proc_monitor_interval ON clinical_schema.procedure_monitoring(monitoring_interval);

COMMENT ON TABLE clinical_schema.procedure_monitoring IS 'Post-procedure patient monitoring and recovery tracking';

-- ============================================================================
-- Insert Sample ICD-9-CM Procedure Codes (Common Indonesian Procedures)
-- ============================================================================

INSERT INTO clinical_schema.icd9cm_codes (code, description_en, description_id, category_code, category_name_en, category_name_id, procedure_type, specialty, requires_anesthesia, anesthesia_type_recommended, avg_duration_minutes, complexity_level, body_system, is_billable, is_active) VALUES
-- Minor Procedures
('86.04', 'Incision with drainage of skin and subcutaneous tissue', 'Insisi dengan drainase kulit dan jaringan subkutan', '86', 'Operations on the integumentary system', 'Operasi sistem integumen', 'SURGICAL', 'GENERAL_SURGERY', TRUE, 'LOCAL', 15, 'LOW', 'INTEGUMENTARY', TRUE, TRUE),
('86.22', 'Excisional debridement of wound, infection, or burn', 'Debridemen eksisi luka, infeksi, atau luka bakar', '86', 'Operations on the integumentary system', 'Operasi sistem integumen', 'SURGICAL', 'GENERAL_SURGERY', TRUE, 'LOCAL', 30, 'LOW', 'INTEGUMENTARY', TRUE, TRUE),
('86.3', 'Other local excision or destruction of lesion or tissue of skin', 'Eksisi lokal atau destruksi lesi kulit lainnya', '86', 'Operations on the integumentary system', 'Operasi sistem integumen', 'SURGICAL', 'DERMATOLOGY', TRUE, 'LOCAL', 20, 'LOW', 'INTEGUMENTARY', TRUE, TRUE),

-- Obstetric Procedures
('73.59', 'Other manually assisted delivery', 'Persalinan dengan bantuan manual lainnya', '73-74', 'Obstetrical procedures', 'Prosedur obstetrik', 'SURGICAL', 'OBSTETRICS', FALSE, 'NONE', 30, 'MODERATE', 'REPRODUCTIVE', TRUE, TRUE),
('74.1', 'Low cervical cesarean section', 'Sectio caesarea serviks bawah', '73-74', 'Obstetrical procedures', 'Prosedur obstetrik', 'SURGICAL', 'OBSTETRICS', TRUE, 'SPINAL', 60, 'MODERATE', 'REPRODUCTIVE', TRUE, TRUE),

-- Orthopedic
('79.05', 'Closed reduction of fracture without internal fixation, femur', 'Reduksi tertutup fraktur femur tanpa fiksasi internal', '79', 'Operations on the musculoskeletal system', 'Operasi sistem muskuloskeletal', 'SURGICAL', 'ORTHOPEDICS', TRUE, 'GENERAL', 45, 'MODERATE', 'MUSCULOSKELETAL', TRUE, TRUE),
('81.52', 'Total hip replacement', 'Penggantian total panggul', '81', 'Operations on the musculoskeletal system', 'Operasi sistem muskuloskeletal', 'SURGICAL', 'ORTHOPEDICS', TRUE, 'GENERAL', 180, 'HIGH', 'MUSCULOSKELETAL', TRUE, TRUE),

-- General Surgery
('47.09', 'Other appendectomy', 'Apendektomi lainnya', '47', 'Operations on the digestive system', 'Operasi sistem pencernaan', 'SURGICAL', 'GENERAL_SURGERY', TRUE, 'GENERAL', 60, 'MODERATE', 'DIGESTIVE', TRUE, TRUE),
('51.23', 'Laparoscopic cholecystectomy', 'Kolesistektomi laparoskopi', '51', 'Operations on the digestive system', 'Operasi sistem pencernaan', 'SURGICAL', 'GENERAL_SURGERY', TRUE, 'GENERAL', 90, 'MODERATE', 'DIGESTIVE', TRUE, TRUE),

-- Dental
('23.19', 'Other surgical extraction of tooth', 'Ekstraksi gigi bedah lainnya', '23', 'Operations on the teeth, gums, and alveoli', 'Operasi gigi, gusi, dan alveoli', 'SURGICAL', 'DENTISTRY', TRUE, 'LOCAL', 30, 'LOW', 'ORAL', TRUE, TRUE),

-- Ophthalmology
('13.41', 'Phacoemulsification and aspiration of cataract', 'Fakoemulsifikasi dan aspirasi katarak', '13', 'Operations on the lens', 'Operasi lensa', 'SURGICAL', 'OPHTHALMOLOGY', TRUE, 'LOCAL', 45, 'MODERATE', 'SENSORY', TRUE, TRUE);

-- Mark common procedures
UPDATE clinical_schema.icd9cm_codes SET is_common = TRUE
WHERE code IN ('86.04', '86.22', '73.59', '74.1', '47.09', '23.19');

-- ============================================================================
-- End of Migration V23
-- ============================================================================
