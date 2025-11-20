/**
 * Discharge Process Tables and Enhancements
 *
 * Creates comprehensive discharge management system including:
 * - Discharge readiness assessment tracking
 * - Enhanced discharge summary with enums
 * - Discharge prescriptions with detailed dosing
 * - Discharge instructions for patient care
 *
 * Related Specification: 3.4.5 Discharge Process
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */

-- ============================================================================
-- 1. Enhance Discharge Summary Table
-- ============================================================================

-- Add new enum-based columns
ALTER TABLE clinical_schema.discharge_summary
    ADD COLUMN IF NOT EXISTS discharge_condition VARCHAR(30),
    ADD COLUMN IF NOT EXISTS discharge_disposition VARCHAR(50);

COMMENT ON COLUMN clinical_schema.discharge_summary.discharge_condition IS
    'Patient condition at discharge: IMPROVED, STABLE, UNCHANGED, DETERIORATED, DECEASED, UNKNOWN';

COMMENT ON COLUMN clinical_schema.discharge_summary.discharge_disposition IS
    'Where patient is going: HOME, HOME_HEALTH_SERVICE, TRANSFER_OTHER_FACILITY, etc.';

-- Rename/update existing columns if they exist differently
DO $$
BEGIN
    -- Check if old condition_at_discharge column exists and copy data
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'clinical_schema'
               AND table_name = 'discharge_summary'
               AND column_name = 'condition_at_discharge') THEN
        -- Copy data from old to new if discharge_condition is empty
        UPDATE clinical_schema.discharge_summary
        SET discharge_condition = condition_at_discharge
        WHERE discharge_condition IS NULL;
    END IF;
END $$;

-- ============================================================================
-- 2. Create Discharge Readiness Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS clinical_schema.discharge_readiness (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- References
    encounter_id UUID NOT NULL,
    patient_id UUID NOT NULL,

    -- Medical Stability
    medical_stability_met BOOLEAN DEFAULT FALSE,
    medical_stability_notes TEXT,
    medical_stability_assessed_at TIMESTAMP,
    medical_stability_assessed_by VARCHAR(200),

    -- Home Care Arrangements
    home_care_arranged BOOLEAN DEFAULT FALSE,
    home_care_notes TEXT,
    caregiver_name VARCHAR(200),
    caregiver_contact VARCHAR(100),

    -- Medication Reconciliation
    medications_reconciled BOOLEAN DEFAULT FALSE,
    medication_reconciliation_notes TEXT,
    medication_reconciled_at TIMESTAMP,
    medication_reconciled_by VARCHAR(200),

    -- Follow-up Scheduling
    follow_up_scheduled BOOLEAN DEFAULT FALSE,
    follow_up_appointment_date TIMESTAMP,
    follow_up_provider VARCHAR(200),
    follow_up_department VARCHAR(100),

    -- Patient Education
    patient_education_completed BOOLEAN DEFAULT FALSE,
    patient_education_topics TEXT,
    patient_understanding_verified BOOLEAN DEFAULT FALSE,

    -- Equipment and Supplies
    dme_ordered BOOLEAN DEFAULT FALSE,
    dme_description TEXT,
    medical_supplies_provided BOOLEAN DEFAULT FALSE,
    medical_supplies_list TEXT,

    -- Discharge Barriers
    has_discharge_barriers BOOLEAN DEFAULT FALSE,
    discharge_barriers TEXT,
    barriers_resolved BOOLEAN DEFAULT FALSE,

    -- Overall Assessment
    ready_for_discharge BOOLEAN DEFAULT FALSE,
    readiness_assessed_at TIMESTAMP,
    readiness_assessed_by_id UUID,
    readiness_assessed_by_name VARCHAR(200),

    additional_notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,

    -- Constraints
    CONSTRAINT fk_readiness_encounter FOREIGN KEY (encounter_id)
        REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_readiness_encounter
    ON clinical_schema.discharge_readiness(encounter_id);

CREATE INDEX IF NOT EXISTS idx_readiness_patient
    ON clinical_schema.discharge_readiness(patient_id);

CREATE INDEX IF NOT EXISTS idx_readiness_status
    ON clinical_schema.discharge_readiness(ready_for_discharge);

COMMENT ON TABLE clinical_schema.discharge_readiness IS
    'Tracks discharge readiness assessment criteria';

-- ============================================================================
-- 3. Create Discharge Prescription Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS clinical_schema.discharge_prescription (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Reference
    discharge_summary_id UUID NOT NULL,

    -- Medication Information
    medication_id UUID,
    medication_name VARCHAR(200) NOT NULL,
    generic_name VARCHAR(200),
    medication_category VARCHAR(100),

    -- Dosing Instructions
    dosage VARCHAR(100) NOT NULL,
    route VARCHAR(50) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100),
    quantity INTEGER,
    unit VARCHAR(50),

    -- Administration Instructions
    timing VARCHAR(200),
    special_instructions TEXT,
    food_interaction VARCHAR(200),

    -- Purpose and Warnings
    purpose TEXT,
    side_effects TEXT,
    warnings TEXT,

    -- Prescription Details
    is_new_medication BOOLEAN DEFAULT FALSE,
    is_changed_medication BOOLEAN DEFAULT FALSE,
    change_notes TEXT,
    refills_allowed INTEGER,

    -- Pharmacy Instructions
    pharmacy_notes TEXT,
    substitution_allowed BOOLEAN DEFAULT TRUE,

    -- Prescriber
    prescriber_id UUID,
    prescriber_name VARCHAR(200),

    -- Status
    prescription_status VARCHAR(30) DEFAULT 'ACTIVE',
    discontinued_reason TEXT,

    -- Display
    display_order INTEGER,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,

    -- Constraints
    CONSTRAINT fk_prescription_summary FOREIGN KEY (discharge_summary_id)
        REFERENCES clinical_schema.discharge_summary(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_rx_summary
    ON clinical_schema.discharge_prescription(discharge_summary_id);

CREATE INDEX IF NOT EXISTS idx_rx_medication
    ON clinical_schema.discharge_prescription(medication_name);

CREATE INDEX IF NOT EXISTS idx_rx_status
    ON clinical_schema.discharge_prescription(prescription_status);

COMMENT ON TABLE clinical_schema.discharge_prescription IS
    'Discharge medications with dosing instructions';

-- ============================================================================
-- 4. Create Discharge Instruction Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS clinical_schema.discharge_instruction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Reference
    discharge_summary_id UUID NOT NULL,

    -- Instruction Details
    instruction_category VARCHAR(50) NOT NULL,
    instruction_title VARCHAR(200) NOT NULL,
    instruction_details TEXT NOT NULL,
    instruction_frequency VARCHAR(100),
    instruction_duration VARCHAR(100),

    -- Specific Instructions
    do_instructions TEXT,
    dont_instructions TEXT,
    when_to_call_doctor TEXT,

    -- Media/Resources
    has_video_tutorial BOOLEAN DEFAULT FALSE,
    video_url VARCHAR(500),
    has_printed_material BOOLEAN DEFAULT FALSE,
    printed_material_url VARCHAR(500),
    diagram_url VARCHAR(500),

    -- Patient Education
    patient_educated BOOLEAN DEFAULT FALSE,
    patient_demonstrates_understanding BOOLEAN DEFAULT FALSE,
    education_notes TEXT,
    educator_name VARCHAR(200),

    -- Priority
    is_critical_instruction BOOLEAN DEFAULT FALSE,
    display_order INTEGER,

    additional_notes TEXT,

    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,

    -- Constraints
    CONSTRAINT fk_instruction_summary FOREIGN KEY (discharge_summary_id)
        REFERENCES clinical_schema.discharge_summary(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_instruction_summary
    ON clinical_schema.discharge_instruction(discharge_summary_id);

CREATE INDEX IF NOT EXISTS idx_instruction_category
    ON clinical_schema.discharge_instruction(instruction_category);

CREATE INDEX IF NOT EXISTS idx_instruction_critical
    ON clinical_schema.discharge_instruction(is_critical_instruction)
    WHERE is_critical_instruction = TRUE;

COMMENT ON TABLE clinical_schema.discharge_instruction IS
    'Patient care instructions after discharge';

-- ============================================================================
-- 5. Create Discharge Summary View
-- ============================================================================

CREATE OR REPLACE VIEW clinical_schema.v_discharge_summary AS
SELECT
    ds.id,
    ds.discharge_number,
    ds.encounter_id,
    ds.patient_id,
    ds.discharge_date,
    ds.discharge_condition,
    ds.discharge_disposition,
    ds.signed,
    ds.signed_at,
    ds.document_generated,

    -- Readiness Assessment
    dr.ready_for_discharge,
    dr.medications_reconciled,
    dr.follow_up_scheduled,
    dr.patient_education_completed,

    -- Counts
    (SELECT COUNT(*) FROM clinical_schema.discharge_prescription
     WHERE discharge_summary_id = ds.id) as prescription_count,
    (SELECT COUNT(*) FROM clinical_schema.discharge_instruction
     WHERE discharge_summary_id = ds.id) as instruction_count,
    (SELECT COUNT(*) FROM clinical_schema.discharge_instruction
     WHERE discharge_summary_id = ds.id AND is_critical_instruction = TRUE) as critical_instruction_count,

    -- Completion Flags
    (ds.signed = TRUE AND
     ds.document_generated = TRUE AND
     dr.ready_for_discharge = TRUE) as discharge_complete,

    -- Audit
    ds.created_at,
    ds.updated_at

FROM clinical_schema.discharge_summary ds
LEFT JOIN clinical_schema.discharge_readiness dr
    ON dr.encounter_id = ds.encounter_id;

COMMENT ON VIEW clinical_schema.v_discharge_summary IS
    'Comprehensive discharge summary view with readiness status';

-- ============================================================================
-- 6. Create Discharge Metrics Function
-- ============================================================================

CREATE OR REPLACE FUNCTION clinical_schema.calculate_discharge_metrics(
    p_start_date DATE,
    p_end_date DATE
)
RETURNS TABLE (
    total_discharges BIGINT,
    discharged_home BIGINT,
    transferred BIGINT,
    deceased BIGINT,
    against_advice BIGINT,
    avg_length_of_stay NUMERIC,
    completed_discharge_summaries BIGINT,
    pending_discharge_summaries BIGINT,
    avg_prescriptions_per_discharge NUMERIC,
    avg_instructions_per_discharge NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::BIGINT as total_discharges,
        COUNT(*) FILTER (WHERE discharge_disposition IN ('HOME', 'HOME_HEALTH_SERVICE'))::BIGINT as discharged_home,
        COUNT(*) FILTER (WHERE discharge_disposition LIKE 'TRANSFER%')::BIGINT as transferred,
        COUNT(*) FILTER (WHERE discharge_condition = 'DECEASED' OR discharge_disposition = 'DECEASED')::BIGINT as deceased,
        COUNT(*) FILTER (WHERE discharge_disposition = 'AGAINST_MEDICAL_ADVICE')::BIGINT as against_advice,

        ROUND(AVG(length_of_stay_days), 1) as avg_length_of_stay,

        COUNT(*) FILTER (WHERE signed = TRUE AND document_generated = TRUE)::BIGINT as completed_discharge_summaries,
        COUNT(*) FILTER (WHERE signed = FALSE OR document_generated = FALSE)::BIGINT as pending_discharge_summaries,

        ROUND(AVG((SELECT COUNT(*) FROM clinical_schema.discharge_prescription dp
                   WHERE dp.discharge_summary_id = ds.id)), 1) as avg_prescriptions_per_discharge,
        ROUND(AVG((SELECT COUNT(*) FROM clinical_schema.discharge_instruction di
                   WHERE di.discharge_summary_id = ds.id)), 1) as avg_instructions_per_discharge

    FROM clinical_schema.discharge_summary ds
    WHERE discharge_date::DATE BETWEEN p_start_date AND p_end_date;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION clinical_schema.calculate_discharge_metrics IS
    'Calculate comprehensive discharge metrics for a date range';

-- ============================================================================
-- Migration Complete
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE 'Discharge Process Tables Created Successfully';
    RAISE NOTICE 'Tables: discharge_readiness, discharge_prescription, discharge_instruction';
    RAISE NOTICE 'Enhanced: discharge_summary with discharge_condition and discharge_disposition';
    RAISE NOTICE 'Created: discharge summary view and metrics function';
END $$;
