-- ============================================================================
-- Flyway Migration V8: Create Encounter/Visit Management Tables
-- Description: Comprehensive encounter management linking registrations to clinical workflows
-- Author: HMS Development Team
-- Date: 2025-01-19
-- ============================================================================

-- ============================================================================
-- ENCOUNTER TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.encounter (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    encounter_number VARCHAR(50) NOT NULL UNIQUE,

    -- Patient reference
    patient_id UUID NOT NULL,

    -- Encounter type and source
    encounter_type VARCHAR(20) NOT NULL, -- OUTPATIENT, INPATIENT, EMERGENCY
    encounter_class VARCHAR(20) NOT NULL, -- AMBULATORY, INPATIENT, EMERGENCY, VIRTUAL

    -- Registration references (one will be populated based on type)
    outpatient_registration_id UUID REFERENCES registration_schema.outpatient_registration(id),
    inpatient_admission_id UUID REFERENCES registration_schema.inpatient_admission(id),
    emergency_registration_id UUID REFERENCES registration_schema.emergency_registration(id),

    -- Encounter timing
    encounter_start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    encounter_end TIMESTAMP,

    -- Status lifecycle
    status VARCHAR(30) NOT NULL DEFAULT 'REGISTERED',
    -- REGISTERED -> IN_PROGRESS -> FINISHED
    -- REGISTERED -> CANCELLED

    -- Department/Location
    current_department VARCHAR(100),
    current_location VARCHAR(200),
    admitting_department VARCHAR(100),

    -- Care team
    attending_doctor_id UUID,
    attending_doctor_name VARCHAR(200),
    primary_nurse_id UUID,
    primary_nurse_name VARCHAR(200),

    -- Priority
    priority VARCHAR(20), -- ROUTINE, URGENT, EMERGENCY, STAT

    -- Service type
    service_type VARCHAR(50), -- GENERAL_MEDICINE, SURGERY, PEDIATRICS, etc.

    -- Reason for visit
    reason_for_visit TEXT,
    chief_complaint TEXT,

    -- Discharge information
    discharge_disposition VARCHAR(50), -- HOME, TRANSFER, ADMITTED, AMA (Against Medical Advice)
    discharge_date TIMESTAMP,
    discharge_summary_id UUID,

    -- Referral information
    referred_from VARCHAR(200),
    referred_to VARCHAR(200),
    referral_id UUID,

    -- Length of stay (calculated)
    length_of_stay_hours INTEGER,
    length_of_stay_days INTEGER,

    -- BPJS/Insurance
    is_bpjs BOOLEAN DEFAULT false,
    sep_number VARCHAR(50),
    sep_date DATE,
    insurance_provider VARCHAR(200),
    insurance_number VARCHAR(100),

    -- SATUSEHAT integration
    satusehat_encounter_id VARCHAR(100),
    satusehat_submitted BOOLEAN DEFAULT false,
    satusehat_submission_date TIMESTAMP,

    -- Billing status
    billing_status VARCHAR(20), -- PENDING, BILLED, PAID, CANCELLED
    total_charges DECIMAL(15,2) DEFAULT 0,

    -- Notes
    encounter_notes TEXT,

    -- Cancellation
    cancelled_at TIMESTAMP,
    cancelled_by VARCHAR(100),
    cancellation_reason TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_one_registration_type CHECK (
        (outpatient_registration_id IS NOT NULL AND inpatient_admission_id IS NULL AND emergency_registration_id IS NULL) OR
        (outpatient_registration_id IS NULL AND inpatient_admission_id IS NOT NULL AND emergency_registration_id IS NULL) OR
        (outpatient_registration_id IS NULL AND inpatient_admission_id IS NULL AND emergency_registration_id IS NOT NULL)
    )
);

CREATE INDEX idx_encounter_number ON clinical_schema.encounter(encounter_number);
CREATE INDEX idx_encounter_patient ON clinical_schema.encounter(patient_id);
CREATE INDEX idx_encounter_type ON clinical_schema.encounter(encounter_type);
CREATE INDEX idx_encounter_status ON clinical_schema.encounter(status);
CREATE INDEX idx_encounter_start ON clinical_schema.encounter(encounter_start);
CREATE INDEX idx_encounter_outpatient ON clinical_schema.encounter(outpatient_registration_id);
CREATE INDEX idx_encounter_inpatient ON clinical_schema.encounter(inpatient_admission_id);
CREATE INDEX idx_encounter_emergency ON clinical_schema.encounter(emergency_registration_id);
CREATE INDEX idx_encounter_sep ON clinical_schema.encounter(sep_number);
CREATE INDEX idx_encounter_billing ON clinical_schema.encounter(billing_status);

COMMENT ON TABLE clinical_schema.encounter IS 'Central encounter/visit management linking all registration types to clinical workflows';
COMMENT ON COLUMN clinical_schema.encounter.encounter_class IS 'FHIR-compliant encounter class';
COMMENT ON COLUMN clinical_schema.encounter.status IS 'REGISTERED, IN_PROGRESS, FINISHED, CANCELLED';

-- ============================================================================
-- DEPARTMENT TRANSFER TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.department_transfer (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transfer_number VARCHAR(50) NOT NULL UNIQUE,

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL,

    -- Transfer details
    from_department VARCHAR(100) NOT NULL,
    from_location VARCHAR(200),
    to_department VARCHAR(100) NOT NULL,
    to_location VARCHAR(200),

    -- Transfer type
    transfer_type VARCHAR(30) NOT NULL, -- INTERNAL, EXTERNAL, ICU, WARD, OPERATING_ROOM

    -- Timing
    transfer_requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transfer_accepted_at TIMESTAMP,
    transfer_completed_at TIMESTAMP,

    -- Status
    transfer_status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    -- REQUESTED -> ACCEPTED -> IN_TRANSIT -> COMPLETED
    -- REQUESTED -> REJECTED -> CANCELLED

    -- Request details
    requested_by_id UUID,
    requested_by_name VARCHAR(200),
    reason_for_transfer TEXT NOT NULL,
    urgency VARCHAR(20), -- ROUTINE, URGENT, EMERGENCY

    -- Acceptance/Rejection
    accepted_by_id UUID,
    accepted_by_name VARCHAR(200),
    rejection_reason TEXT,

    -- Receiving team
    receiving_doctor_id UUID,
    receiving_doctor_name VARCHAR(200),
    receiving_nurse_id UUID,
    receiving_nurse_name VARCHAR(200),

    -- Handover notes
    handover_summary TEXT,
    current_condition TEXT,
    active_medications TEXT,
    special_instructions TEXT,

    -- Equipment/Transport
    requires_transport BOOLEAN DEFAULT false,
    requires_equipment TEXT,
    mode_of_transport VARCHAR(50), -- WHEELCHAIR, STRETCHER, BED, AMBULANCE

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

CREATE INDEX idx_transfer_number ON clinical_schema.department_transfer(transfer_number);
CREATE INDEX idx_transfer_encounter ON clinical_schema.department_transfer(encounter_id);
CREATE INDEX idx_transfer_patient ON clinical_schema.department_transfer(patient_id);
CREATE INDEX idx_transfer_status ON clinical_schema.department_transfer(transfer_status);
CREATE INDEX idx_transfer_from ON clinical_schema.department_transfer(from_department);
CREATE INDEX idx_transfer_to ON clinical_schema.department_transfer(to_department);

COMMENT ON TABLE clinical_schema.department_transfer IS 'Department and location transfers within and between facilities';

-- ============================================================================
-- DISCHARGE SUMMARY TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.discharge_summary (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    discharge_number VARCHAR(50) NOT NULL UNIQUE,

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,

    -- Discharge details
    discharge_date TIMESTAMP NOT NULL,
    discharge_time TIME,
    discharge_type VARCHAR(30) NOT NULL, -- ROUTINE, AGAINST_MEDICAL_ADVICE, TRANSFER, DECEASED

    -- Discharge disposition
    discharge_disposition VARCHAR(50) NOT NULL,
    -- HOME, HOME_WITH_SERVICES, TRANSFER_TO_HOSPITAL, TRANSFER_TO_SNF,
    -- HOSPICE, DECEASED, LEFT_AGAINST_ADVICE

    -- Clinical summary
    admission_date TIMESTAMP,
    length_of_stay_days INTEGER,

    -- Summary sections
    reason_for_admission TEXT,
    hospital_course TEXT NOT NULL, -- Course of treatment during stay
    procedures_performed TEXT,

    -- Diagnoses
    primary_diagnosis_code VARCHAR(10),
    primary_diagnosis_text TEXT,
    secondary_diagnoses TEXT, -- JSON array or comma-separated

    -- Final condition
    condition_at_discharge VARCHAR(50), -- IMPROVED, STABLE, DETERIORATED, DECEASED
    vital_signs_at_discharge TEXT,

    -- Medications
    discharge_medications TEXT NOT NULL, -- List of medications to continue
    medications_discontinued TEXT,
    new_medications TEXT,

    -- Follow-up care
    follow_up_instructions TEXT NOT NULL,
    follow_up_appointment_date DATE,
    follow_up_doctor VARCHAR(200),
    follow_up_department VARCHAR(100),

    -- Dietary and activity restrictions
    diet_instructions TEXT,
    activity_restrictions TEXT,
    wound_care_instructions TEXT,

    -- Warning signs
    warning_signs TEXT, -- When to seek immediate medical attention
    emergency_contact TEXT,

    -- Referrals
    referral_to VARCHAR(200),
    referral_reason TEXT,

    -- Provider information
    discharge_doctor_id UUID,
    discharge_doctor_name VARCHAR(200),
    attending_doctor_name VARCHAR(200),

    -- Digital signature
    signed BOOLEAN DEFAULT false,
    signed_at TIMESTAMP,
    signed_by_id UUID,
    signed_by_name VARCHAR(200),

    -- SATUSEHAT submission
    satusehat_submitted BOOLEAN DEFAULT false,
    satusehat_submission_date TIMESTAMP,

    -- Document generation
    document_generated BOOLEAN DEFAULT false,
    document_url VARCHAR(500),
    document_generated_at TIMESTAMP,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_discharge_number ON clinical_schema.discharge_summary(discharge_number);
CREATE INDEX idx_discharge_encounter ON clinical_schema.discharge_summary(encounter_id);
CREATE INDEX idx_discharge_patient ON clinical_schema.discharge_summary(patient_id);
CREATE INDEX idx_discharge_date ON clinical_schema.discharge_summary(discharge_date);
CREATE INDEX idx_discharge_type ON clinical_schema.discharge_summary(discharge_type);
CREATE INDEX idx_discharge_signed ON clinical_schema.discharge_summary(signed);

COMMENT ON TABLE clinical_schema.discharge_summary IS 'Comprehensive discharge summaries for encounters';
COMMENT ON COLUMN clinical_schema.discharge_summary.hospital_course IS 'Detailed narrative of treatment during hospitalization';

-- ============================================================================
-- REFERRAL LETTER TABLE (Surat Rujukan)
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.referral_letter (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    referral_number VARCHAR(50) NOT NULL UNIQUE,

    -- References
    encounter_id UUID REFERENCES clinical_schema.encounter(id),
    patient_id UUID NOT NULL,

    -- Referral type
    referral_type VARCHAR(30) NOT NULL, -- OUTPATIENT, INPATIENT, EMERGENCY, DIAGNOSTIC
    referral_reason VARCHAR(50) NOT NULL, -- CONSULTATION, TREATMENT, INVESTIGATION, ADMISSION

    -- Source (referring) information
    referring_facility VARCHAR(200) NOT NULL,
    referring_department VARCHAR(100),
    referring_doctor_id UUID,
    referring_doctor_name VARCHAR(200) NOT NULL,
    referring_doctor_phone VARCHAR(20),

    -- Destination (referred to) information
    referred_to_facility VARCHAR(200) NOT NULL,
    referred_to_department VARCHAR(100),
    referred_to_doctor VARCHAR(200),
    referred_to_specialty VARCHAR(100),

    -- Referral date
    referral_date DATE NOT NULL DEFAULT CURRENT_DATE,
    referral_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_until DATE,

    -- Clinical information
    chief_complaint TEXT NOT NULL,
    clinical_summary TEXT NOT NULL,
    relevant_history TEXT,

    -- Diagnoses
    primary_diagnosis_code VARCHAR(10),
    primary_diagnosis_text TEXT NOT NULL,
    secondary_diagnoses TEXT,

    -- Current treatment
    current_medications TEXT,
    treatments_given TEXT,

    -- Investigation results
    lab_results_summary TEXT,
    imaging_results_summary TEXT,
    other_investigations TEXT,

    -- Vital signs at referral
    vital_signs TEXT,

    -- Reason for referral
    reason_for_referral TEXT NOT NULL,
    urgency_level VARCHAR(20), -- ROUTINE, URGENT, EMERGENCY

    -- Required services
    services_requested TEXT, -- What services/expertise needed
    appointment_requested BOOLEAN DEFAULT false,
    admission_requested BOOLEAN DEFAULT false,

    -- Transport
    transport_required BOOLEAN DEFAULT false,
    transport_mode VARCHAR(50), -- AMBULANCE, PRIVATE, PUBLIC
    patient_condition_for_transport VARCHAR(50), -- STABLE, REQUIRES_MONITORING, CRITICAL

    -- Reply/Response
    referral_accepted BOOLEAN,
    acceptance_date TIMESTAMP,
    accepted_by VARCHAR(200),
    appointment_date TIMESTAMP,
    rejection_reason TEXT,

    -- BPJS specific
    is_bpjs_referral BOOLEAN DEFAULT false,
    bpjs_sep_number VARCHAR(50),
    bpjs_referral_code VARCHAR(50),

    -- Digital signature
    signed BOOLEAN DEFAULT false,
    signed_at TIMESTAMP,
    signed_by_id UUID,
    signed_by_name VARCHAR(200),
    digital_signature TEXT,

    -- Document generation
    document_generated BOOLEAN DEFAULT false,
    document_url VARCHAR(500),
    document_pdf_base64 TEXT,
    document_generated_at TIMESTAMP,

    -- SATUSEHAT submission
    satusehat_submitted BOOLEAN DEFAULT false,
    satusehat_submission_date TIMESTAMP,
    satusehat_service_request_id VARCHAR(100),

    -- Status
    referral_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- PENDING -> ACCEPTED -> COMPLETED
    -- PENDING -> REJECTED -> CANCELLED

    -- Notes
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_referral_number ON clinical_schema.referral_letter(referral_number);
CREATE INDEX idx_referral_encounter ON clinical_schema.referral_letter(encounter_id);
CREATE INDEX idx_referral_patient ON clinical_schema.referral_letter(patient_id);
CREATE INDEX idx_referral_date ON clinical_schema.referral_letter(referral_date);
CREATE INDEX idx_referral_status ON clinical_schema.referral_letter(referral_status);
CREATE INDEX idx_referral_facility_to ON clinical_schema.referral_letter(referred_to_facility);
CREATE INDEX idx_referral_bpjs ON clinical_schema.referral_letter(is_bpjs_referral);

COMMENT ON TABLE clinical_schema.referral_letter IS 'Surat Rujukan - Referral letters to other facilities/specialists';
COMMENT ON COLUMN clinical_schema.referral_letter.referral_reason IS 'Why the patient is being referred';

-- ============================================================================
-- ENCOUNTER STATUS HISTORY TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.encounter_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,

    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,

    status_changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by_id UUID,
    changed_by_name VARCHAR(200),

    reason TEXT,
    notes TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_status_history_encounter ON clinical_schema.encounter_status_history(encounter_id);
CREATE INDEX idx_status_history_date ON clinical_schema.encounter_status_history(status_changed_at);

COMMENT ON TABLE clinical_schema.encounter_status_history IS 'Audit trail of encounter status changes';

-- ============================================================================
-- TRIGGERS
-- ============================================================================

CREATE TRIGGER update_encounter_timestamp
    BEFORE UPDATE ON clinical_schema.encounter
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_transfer_timestamp
    BEFORE UPDATE ON clinical_schema.department_transfer
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_discharge_timestamp
    BEFORE UPDATE ON clinical_schema.discharge_summary
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_referral_timestamp
    BEFORE UPDATE ON clinical_schema.referral_letter
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================================
-- SUMMARY
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V8 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Encounter Management Tables Created:';
    RAISE NOTICE '  - encounter (central visit management)';
    RAISE NOTICE '  - department_transfer (inter-department transfers)';
    RAISE NOTICE '  - discharge_summary (comprehensive discharge documentation)';
    RAISE NOTICE '  - referral_letter (Surat Rujukan)';
    RAISE NOTICE '  - encounter_status_history (audit trail)';
    RAISE NOTICE '';
    RAISE NOTICE 'Key Features:';
    RAISE NOTICE '  - Links to outpatient, inpatient, emergency registrations';
    RAISE NOTICE '  - Status lifecycle tracking';
    RAISE NOTICE '  - Department transfer management';
    RAISE NOTICE '  - Discharge process with summary';
    RAISE NOTICE '  - Referral letter generation (Surat Rujukan)';
    RAISE NOTICE '  - BPJS SEP integration';
    RAISE NOTICE '  - SATUSEHAT compliance';
    RAISE NOTICE '  - Digital signature support';
    RAISE NOTICE '  - Billing integration ready';
    RAISE NOTICE '============================================';
END $$;