-- ============================================================================
-- Flyway Migration V12: Create Inpatient Clinical Tracking Tables
-- Description: SOAP notes, vital signs, medication administration, location history
-- Author: HMS Development Team
-- Date: 2025-11-20
-- ============================================================================

-- ============================================================================
-- DROP CONFLICTING TABLES FROM EARLIER MIGRATIONS
-- ============================================================================
-- Drop the old vital_signs table from V4 (registration-based) to replace it
-- with the new encounter-based structure for inpatient tracking
DROP TABLE IF EXISTS clinical_schema.vital_signs CASCADE;

-- ============================================================================
-- PROGRESS NOTES / SOAP NOTES TABLE
-- ============================================================================
CREATE TABLE clinical_schema.progress_note (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    note_number VARCHAR(50) NOT NULL UNIQUE,

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL,

    -- Note metadata
    note_type VARCHAR(30) NOT NULL, -- SOAP, SHIFT_HANDOVER, CRITICAL_CARE, NURSING, PROCEDURE
    note_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    shift VARCHAR(20), -- MORNING, AFTERNOON, NIGHT

    -- SOAP format
    subjective TEXT, -- Patient complaints, symptoms (S)
    objective TEXT, -- Vital signs, physical exam findings (O)
    assessment TEXT, -- Clinical impression, diagnosis (A)
    plan TEXT, -- Treatment plan, interventions (P)

    -- Additional clinical information
    additional_notes TEXT,
    follow_up_required BOOLEAN DEFAULT false,
    follow_up_instructions TEXT,
    critical_findings TEXT,

    -- Provider information
    provider_id UUID NOT NULL,
    provider_name VARCHAR(200) NOT NULL,
    provider_type VARCHAR(30), -- DOCTOR, NURSE, SPECIALIST, RESIDENT
    provider_specialty VARCHAR(100),

    -- Cosign/Supervision (for residents)
    requires_cosign BOOLEAN DEFAULT false,
    cosigned BOOLEAN DEFAULT false,
    cosigned_by_id UUID,
    cosigned_by_name VARCHAR(200),
    cosigned_at TIMESTAMP,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_progress_note_number ON clinical_schema.progress_note(note_number);
CREATE INDEX idx_progress_note_encounter ON clinical_schema.progress_note(encounter_id);
CREATE INDEX idx_progress_note_patient ON clinical_schema.progress_note(patient_id);
CREATE INDEX idx_progress_note_date ON clinical_schema.progress_note(note_date_time);
CREATE INDEX idx_progress_note_type ON clinical_schema.progress_note(note_type);
CREATE INDEX idx_progress_note_provider ON clinical_schema.progress_note(provider_id);

COMMENT ON TABLE clinical_schema.progress_note IS 'SOAP notes and progress documentation for inpatient care';
COMMENT ON COLUMN clinical_schema.progress_note.subjective IS 'S - Patient complaints, symptoms, history';
COMMENT ON COLUMN clinical_schema.progress_note.objective IS 'O - Physical exam, vital signs, lab results';
COMMENT ON COLUMN clinical_schema.progress_note.assessment IS 'A - Clinical impression, diagnosis';
COMMENT ON COLUMN clinical_schema.progress_note.plan IS 'P - Treatment plan, orders, interventions';

-- ============================================================================
-- VITAL SIGNS TABLE
-- ============================================================================
CREATE TABLE clinical_schema.vital_signs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL,

    -- Timing
    measurement_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    shift VARCHAR(20), -- MORNING, AFTERNOON, NIGHT
    measurement_type VARCHAR(30) DEFAULT 'ROUTINE', -- ROUTINE, ADMISSION, PRE_OP, POST_OP, STAT

    -- Basic vital signs
    systolic_bp INTEGER, -- mmHg (normal: 90-120)
    diastolic_bp INTEGER, -- mmHg (normal: 60-80)
    heart_rate INTEGER, -- bpm (normal: 60-100)
    respiratory_rate INTEGER, -- breaths/min (normal: 12-20)
    temperature DECIMAL(4,2), -- Celsius (normal: 36.5-37.5)
    temperature_route VARCHAR(20), -- ORAL, AXILLARY, RECTAL, TYMPANIC
    spo2 INTEGER, -- % (normal: 95-100)
    oxygen_therapy BOOLEAN DEFAULT false,
    oxygen_flow_rate DECIMAL(4,2), -- L/min
    oxygen_delivery_method VARCHAR(50), -- NASAL_CANNULA, FACE_MASK, VENTILATOR

    -- Physical measurements
    weight DECIMAL(6,2), -- kg
    height DECIMAL(5,2), -- cm
    bmi DECIMAL(4,2), -- Calculated
    head_circumference DECIMAL(4,2), -- cm (for pediatrics)

    -- Glasgow Coma Scale (for critical care)
    gcs_eye INTEGER, -- 1-4 (Eye opening response)
    gcs_verbal INTEGER, -- 1-5 (Verbal response)
    gcs_motor INTEGER, -- 1-6 (Motor response)
    gcs_total INTEGER, -- 3-15 (Total score)

    -- Pain assessment
    pain_score INTEGER, -- 0-10 (Pain scale)
    pain_location VARCHAR(200),
    pain_quality VARCHAR(200), -- SHARP, DULL, BURNING, CRAMPING

    -- Fluid balance (for ICU/critical care)
    fluid_intake_ml INTEGER, -- Total intake
    fluid_output_ml INTEGER, -- Total output
    fluid_balance_ml INTEGER, -- Net balance (intake - output)
    urine_output_ml INTEGER, -- Specific output tracking

    -- Blood glucose (for diabetic patients)
    blood_glucose DECIMAL(5,2), -- mg/dL or mmol/L
    blood_glucose_unit VARCHAR(10), -- MG_DL, MMOL_L

    -- Additional parameters
    mean_arterial_pressure INTEGER, -- MAP (calculated or measured)
    peripheral_pulse VARCHAR(50), -- STRONG, WEAK, ABSENT
    capillary_refill_time DECIMAL(3,1), -- seconds
    pupil_reaction VARCHAR(50), -- NORMAL, SLUGGISH, NON_REACTIVE

    -- Alerts and flags
    is_abnormal BOOLEAN DEFAULT false,
    abnormal_flags TEXT, -- Comma-separated list of abnormal parameters
    requires_notification BOOLEAN DEFAULT false,
    notification_sent BOOLEAN DEFAULT false,
    notified_provider_id UUID,

    -- Clinical notes
    notes TEXT,
    alerts TEXT, -- Critical findings requiring immediate attention

    -- Provider information
    recorded_by_id UUID,
    recorded_by_name VARCHAR(200),
    recorded_by_role VARCHAR(50), -- NURSE, DOCTOR, NURSING_ASSISTANT

    -- Location
    location_name VARCHAR(200),
    bed_number VARCHAR(50),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_vital_signs_encounter ON clinical_schema.vital_signs(encounter_id);
CREATE INDEX idx_vital_signs_patient ON clinical_schema.vital_signs(patient_id);
CREATE INDEX idx_vital_signs_time ON clinical_schema.vital_signs(measurement_time);
CREATE INDEX idx_vital_signs_abnormal ON clinical_schema.vital_signs(is_abnormal);
CREATE INDEX idx_vital_signs_notification ON clinical_schema.vital_signs(requires_notification);

COMMENT ON TABLE clinical_schema.vital_signs IS 'Vital signs monitoring for inpatient care';
COMMENT ON COLUMN clinical_schema.vital_signs.gcs_total IS 'Glasgow Coma Scale: 3-8 severe, 9-12 moderate, 13-15 mild';
COMMENT ON COLUMN clinical_schema.vital_signs.mean_arterial_pressure IS 'MAP = DBP + (SBP-DBP)/3, normal: 70-100';

-- ============================================================================
-- MEDICATION ADMINISTRATION RECORD (MAR) TABLE
-- ============================================================================
CREATE TABLE clinical_schema.medication_administration (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    mar_number VARCHAR(50) NOT NULL UNIQUE,

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL,
    medication_order_id UUID, -- Link to pharmacy order (future integration)

    -- Medication identification
    medication_name VARCHAR(200) NOT NULL,
    generic_name VARCHAR(200),
    brand_name VARCHAR(200),
    medication_code VARCHAR(50), -- Formulary code or NDC
    medication_class VARCHAR(100), -- ANTIBIOTIC, ANALGESIC, etc.

    -- Dosage information
    dose VARCHAR(50) NOT NULL, -- e.g., "500", "2"
    dose_unit VARCHAR(20) NOT NULL, -- mg, ml, unit, tablet, capsule
    strength VARCHAR(50), -- e.g., "500mg/tablet"
    total_dose_description VARCHAR(100), -- e.g., "500mg (1 tablet)"

    -- Route and frequency
    route VARCHAR(50) NOT NULL, -- ORAL, IV, IM, SC, TOPICAL, RECTAL, SUBLINGUAL
    frequency VARCHAR(50) NOT NULL, -- BID, TID, QID, Q4H, Q6H, PRN, STAT, ONCE
    frequency_times_per_day INTEGER, -- Numeric representation

    -- Schedule information
    schedule_type VARCHAR(20) NOT NULL, -- SCHEDULED, PRN, STAT, ONE_TIME
    scheduled_date DATE,
    scheduled_time TIME,
    scheduled_date_time TIMESTAMP, -- Combined for easier querying

    -- Administration details
    actual_administration_date_time TIMESTAMP,
    administered BOOLEAN DEFAULT false,
    administration_status VARCHAR(20) DEFAULT 'PENDING',
    -- PENDING, GIVEN, REFUSED, HELD, MISSED, DISCONTINUED

    -- Administration site (for injections)
    administration_site VARCHAR(100), -- LEFT_ARM, RIGHT_ARM, ABDOMEN, etc.

    -- Provider information
    administered_by_id UUID,
    administered_by_name VARCHAR(200),
    administered_by_role VARCHAR(50), -- NURSE, DOCTOR

    -- Verification/Witness (for high-risk medications)
    requires_witness BOOLEAN DEFAULT false,
    witnessed_by_id UUID,
    witnessed_by_name VARCHAR(200),
    witness_signature VARCHAR(200),

    -- Reasons for not giving
    not_given_reason TEXT,
    hold_reason TEXT,
    discontinue_reason TEXT,

    -- Patient response
    patient_response TEXT,
    adverse_reaction BOOLEAN DEFAULT false,
    adverse_reaction_type VARCHAR(50), -- ALLERGIC, SIDE_EFFECT, OTHER
    adverse_reaction_details TEXT,
    adverse_reaction_severity VARCHAR(20), -- MILD, MODERATE, SEVERE
    adverse_reaction_reported BOOLEAN DEFAULT false,

    -- PRN specific information
    prn_reason TEXT, -- Why PRN medication was given
    prn_effectiveness TEXT, -- Did it help?

    -- Prescriber information
    prescribed_by_id UUID,
    prescribed_by_name VARCHAR(200),
    prescription_date TIMESTAMP,

    -- IV specific information (for IV medications)
    iv_solution VARCHAR(200),
    iv_volume_ml INTEGER,
    iv_rate_ml_per_hour DECIMAL(6,2),
    iv_duration_minutes INTEGER,
    iv_site_location VARCHAR(100),

    -- Notes
    administration_notes TEXT,
    special_instructions TEXT,

    -- Alerts
    is_high_alert_medication BOOLEAN DEFAULT false,
    high_alert_type VARCHAR(50), -- NARCOTIC, INSULIN, ANTICOAGULANT, etc.

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_mar_number ON clinical_schema.medication_administration(mar_number);
CREATE INDEX idx_mar_encounter ON clinical_schema.medication_administration(encounter_id);
CREATE INDEX idx_mar_patient ON clinical_schema.medication_administration(patient_id);
CREATE INDEX idx_mar_scheduled_time ON clinical_schema.medication_administration(scheduled_date_time);
CREATE INDEX idx_mar_admin_time ON clinical_schema.medication_administration(actual_administration_date_time);
CREATE INDEX idx_mar_status ON clinical_schema.medication_administration(administration_status);
CREATE INDEX idx_mar_adverse ON clinical_schema.medication_administration(adverse_reaction);
CREATE INDEX idx_mar_high_alert ON clinical_schema.medication_administration(is_high_alert_medication);

COMMENT ON TABLE clinical_schema.medication_administration IS 'Medication Administration Record (MAR) for inpatient care';
COMMENT ON COLUMN clinical_schema.medication_administration.schedule_type IS 'SCHEDULED for regular meds, PRN for as-needed, STAT for immediate';
COMMENT ON COLUMN clinical_schema.medication_administration.administration_status IS 'PENDING, GIVEN, REFUSED, HELD, MISSED, DISCONTINUED';

-- ============================================================================
-- ENCOUNTER LOCATION HISTORY TABLE
-- ============================================================================
CREATE TABLE clinical_schema.encounter_location_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- References
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL,

    -- Location details
    location_id UUID,
    location_name VARCHAR(200) NOT NULL,
    location_type VARCHAR(50), -- WARD, ICU, CCU, EMERGENCY, OPERATING_ROOM, RECOVERY

    -- Department
    department_id UUID,
    department_name VARCHAR(100) NOT NULL,

    -- Bed/Room details
    room_id UUID,
    room_number VARCHAR(50),
    room_type VARCHAR(50), -- PRIVATE, SEMI_PRIVATE, WARD, ICU_ROOM
    bed_id UUID,
    bed_number VARCHAR(20),

    -- Timing
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    duration_hours INTEGER,
    duration_days INTEGER,

    -- Event type
    location_event_type VARCHAR(50) NOT NULL,
    -- ADMISSION, TRANSFER, BED_CHANGE, ICU_ADMISSION, ICU_DISCHARGE,
    -- OR_TRANSFER, RECOVERY_TRANSFER, DISCHARGE

    -- Transfer/Change reason
    change_reason TEXT,
    change_notes TEXT,

    -- Responsible staff
    changed_by_id UUID,
    changed_by_name VARCHAR(200),
    authorized_by_id UUID,
    authorized_by_name VARCHAR(200),

    -- Bed assignment reference
    bed_assignment_id UUID, -- Link to bed_assignment table

    -- Flags
    is_current BOOLEAN DEFAULT true,
    is_icu BOOLEAN DEFAULT false,
    isolation_required BOOLEAN DEFAULT false,
    isolation_type VARCHAR(50), -- CONTACT, DROPLET, AIRBORNE

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_location_history_encounter ON clinical_schema.encounter_location_history(encounter_id);
CREATE INDEX idx_location_history_patient ON clinical_schema.encounter_location_history(patient_id);
CREATE INDEX idx_location_history_start ON clinical_schema.encounter_location_history(start_time);
CREATE INDEX idx_location_history_current ON clinical_schema.encounter_location_history(is_current);
CREATE INDEX idx_location_history_bed ON clinical_schema.encounter_location_history(bed_id);
CREATE INDEX idx_location_history_type ON clinical_schema.encounter_location_history(location_event_type);

COMMENT ON TABLE clinical_schema.encounter_location_history IS 'Complete location and bed change history for encounters';
COMMENT ON COLUMN clinical_schema.encounter_location_history.is_current IS 'Only one record should have is_current=true per encounter';

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Update timestamp triggers
CREATE TRIGGER update_progress_note_timestamp
    BEFORE UPDATE ON clinical_schema.progress_note
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_vital_signs_timestamp
    BEFORE UPDATE ON clinical_schema.vital_signs
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_medication_administration_timestamp
    BEFORE UPDATE ON clinical_schema.medication_administration
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_encounter_location_history_timestamp
    BEFORE UPDATE ON clinical_schema.encounter_location_history
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================================
-- HELPER FUNCTIONS
-- ============================================================================

-- Function to calculate BMI
CREATE OR REPLACE FUNCTION clinical_schema.calculate_bmi(
    weight_kg DECIMAL,
    height_cm DECIMAL
) RETURNS DECIMAL AS $$
BEGIN
    IF weight_kg IS NULL OR height_cm IS NULL OR height_cm = 0 THEN
        RETURN NULL;
    END IF;
    RETURN ROUND((weight_kg / ((height_cm / 100.0) * (height_cm / 100.0)))::DECIMAL, 2);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION clinical_schema.calculate_bmi IS 'Calculate BMI from weight (kg) and height (cm)';

-- Function to calculate MAP (Mean Arterial Pressure)
CREATE OR REPLACE FUNCTION clinical_schema.calculate_map(
    systolic INTEGER,
    diastolic INTEGER
) RETURNS INTEGER AS $$
BEGIN
    IF systolic IS NULL OR diastolic IS NULL THEN
        RETURN NULL;
    END IF;
    RETURN ROUND(diastolic + ((systolic - diastolic) / 3.0));
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION clinical_schema.calculate_map IS 'Calculate Mean Arterial Pressure: MAP = DBP + (SBP-DBP)/3';

-- ============================================================================
-- SUMMARY
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V12 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Inpatient Clinical Tracking Tables Created:';
    RAISE NOTICE '  - progress_note (SOAP notes and daily progress)';
    RAISE NOTICE '  - vital_signs (comprehensive vitals monitoring)';
    RAISE NOTICE '  - medication_administration (MAR tracking)';
    RAISE NOTICE '  - encounter_location_history (bed/location tracking)';
    RAISE NOTICE '';
    RAISE NOTICE 'Helper Functions Created:';
    RAISE NOTICE '  - calculate_bmi(weight_kg, height_cm)';
    RAISE NOTICE '  - calculate_map(systolic, diastolic)';
    RAISE NOTICE '';
    RAISE NOTICE 'Features Enabled:';
    RAISE NOTICE '  - Daily SOAP notes for inpatients';
    RAISE NOTICE '  - Real-time vital signs monitoring';
    RAISE NOTICE '  - Medication administration tracking';
    RAISE NOTICE '  - Complete location/bed change history';
    RAISE NOTICE '  - Adverse reaction tracking';
    RAISE NOTICE '  - Glasgow Coma Scale for critical care';
    RAISE NOTICE '  - Fluid balance monitoring';
    RAISE NOTICE '  - High-alert medication flagging';
    RAISE NOTICE '============================================';
END $$;
