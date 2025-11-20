/**
 * Emergency Workflow Phase 1 Enhancements
 *
 * Changes:
 * 1. Add encounter_id to emergency_registration table for seamless clinical integration
 * 2. Create emergency_intervention table for tracking critical interventions
 * 3. Add ARRIVED status support (will be handled in Java enum)
 * 4. Add indexes for performance optimization
 *
 * Related Documents:
 * - EMERGENCY_WORKFLOW_GAP_ANALYSIS.md
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */

-- ============================================================================
-- 1. Add Encounter Integration to Emergency Registration
-- ============================================================================

-- Add encounter_id column to link emergency registration with clinical encounter
ALTER TABLE registration_schema.emergency_registration
    ADD COLUMN encounter_id UUID REFERENCES clinical_schema.encounter(id);

COMMENT ON COLUMN registration_schema.emergency_registration.encounter_id IS
    'Link to clinical encounter for seamless SOAP notes, vital signs, and orders integration';

-- Create index for encounter lookups
CREATE INDEX idx_emergency_encounter
    ON registration_schema.emergency_registration(encounter_id);

-- ============================================================================
-- 2. Create Emergency Intervention Tracking Table
-- ============================================================================

-- Emergency interventions track critical procedures and treatments during ER stay
CREATE TABLE registration_schema.emergency_intervention (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Links
    emergency_registration_id UUID NOT NULL
        REFERENCES registration_schema.emergency_registration(id)
        ON DELETE CASCADE,
    encounter_id UUID NOT NULL
        REFERENCES clinical_schema.encounter(id)
        ON DELETE CASCADE,

    -- Intervention Metadata
    intervention_type VARCHAR(50) NOT NULL,
    intervention_name VARCHAR(200) NOT NULL,
    intervention_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    performed_by_id UUID,
    performed_by_name VARCHAR(200) NOT NULL,
    performed_by_role VARCHAR(50), -- DOCTOR, NURSE, PARAMEDIC, etc.

    -- Resuscitation Specific Fields
    is_resuscitation BOOLEAN NOT NULL DEFAULT FALSE,
    resuscitation_start_time TIMESTAMP,
    resuscitation_end_time TIMESTAMP,
    resuscitation_duration_minutes INTEGER,
    rosc_achieved BOOLEAN, -- Return of Spontaneous Circulation
    rosc_time TIMESTAMP,
    cpr_quality_score INTEGER CHECK (cpr_quality_score BETWEEN 0 AND 100),
    defibrillation_attempts INTEGER,
    epinephrine_doses INTEGER,

    -- Airway Management Specific
    airway_type VARCHAR(50), -- INTUBATION, TRACHEOSTOMY, CRICOTHYROIDOTOMY, LMA
    tube_size VARCHAR(20),
    insertion_attempts INTEGER,
    airway_secured BOOLEAN,

    -- Procedure Specific Fields
    procedure_code VARCHAR(50),
    procedure_site VARCHAR(100),
    procedure_approach VARCHAR(50), -- PERCUTANEOUS, SURGICAL, etc.
    complications TEXT,
    procedure_outcome VARCHAR(50), -- SUCCESS, FAILED, PARTIAL

    -- Medication Specific Fields
    medication_name VARCHAR(200),
    medication_dose VARCHAR(100),
    medication_route VARCHAR(50), -- IV, IM, PO, SUBLINGUAL, etc.
    medication_frequency VARCHAR(100),

    -- Transfusion Specific
    blood_product_type VARCHAR(50), -- PRBC, FFP, PLATELETS, CRYOPRECIPITATE
    units_transfused INTEGER,
    transfusion_reaction BOOLEAN,
    cross_match_required BOOLEAN,

    -- Common Fields
    indication TEXT NOT NULL,
    urgency VARCHAR(20) DEFAULT 'ROUTINE', -- EMERGENCY, URGENT, ROUTINE
    outcome VARCHAR(50),
    outcome_notes TEXT,
    complications_occurred BOOLEAN DEFAULT FALSE,
    notes TEXT,

    -- Location
    location VARCHAR(100), -- RED_ZONE, RESUS_ROOM, etc.
    bed_number VARCHAR(20),

    -- Audit Trail
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT chk_intervention_type CHECK (intervention_type IN (
        'RESUSCITATION',
        'AIRWAY_MANAGEMENT',
        'VASCULAR_ACCESS',
        'CHEST_TUBE',
        'CENTRAL_LINE',
        'ARTERIAL_LINE',
        'PROCEDURE',
        'EMERGENCY_MEDICATION',
        'TRANSFUSION',
        'CARDIOVERSION',
        'DEFIBRILLATION',
        'PACING',
        'IMAGING',
        'WOUND_CARE',
        'SPLINTING',
        'CONSULTATION'
    )),

    CONSTRAINT chk_urgency CHECK (urgency IN ('EMERGENCY', 'URGENT', 'ROUTINE')),

    -- Resuscitation validation
    CONSTRAINT chk_resuscitation_times CHECK (
        is_resuscitation = FALSE OR
        (resuscitation_start_time IS NOT NULL AND
         (resuscitation_end_time IS NULL OR resuscitation_end_time >= resuscitation_start_time))
    ),

    CONSTRAINT chk_rosc CHECK (
        rosc_achieved = FALSE OR rosc_achieved IS NULL OR
        (rosc_achieved = TRUE AND rosc_time IS NOT NULL)
    )
);

-- Comments
COMMENT ON TABLE registration_schema.emergency_intervention IS
    'Tracks critical interventions, procedures, and treatments during emergency care';

COMMENT ON COLUMN registration_schema.emergency_intervention.intervention_type IS
    'Type of emergency intervention performed';

COMMENT ON COLUMN registration_schema.emergency_intervention.is_resuscitation IS
    'Flag indicating if this is a resuscitation/code event';

COMMENT ON COLUMN registration_schema.emergency_intervention.rosc_achieved IS
    'Return of Spontaneous Circulation achieved during resuscitation';

COMMENT ON COLUMN registration_schema.emergency_intervention.cpr_quality_score IS
    'CPR quality score based on compression rate/depth metrics (0-100)';

-- Indexes for Performance
CREATE INDEX idx_intervention_emergency
    ON registration_schema.emergency_intervention(emergency_registration_id);

CREATE INDEX idx_intervention_encounter
    ON registration_schema.emergency_intervention(encounter_id);

CREATE INDEX idx_intervention_time
    ON registration_schema.emergency_intervention(intervention_time DESC);

CREATE INDEX idx_intervention_type
    ON registration_schema.emergency_intervention(intervention_type);

CREATE INDEX idx_intervention_resuscitation
    ON registration_schema.emergency_intervention(is_resuscitation)
    WHERE is_resuscitation = TRUE;

CREATE INDEX idx_intervention_performer
    ON registration_schema.emergency_intervention(performed_by_id);

-- ============================================================================
-- 3. Add Emergency Timing Enhancements
-- ============================================================================

-- Add arrival acknowledged timestamp (when staff first sees patient)
ALTER TABLE registration_schema.emergency_registration
    ADD COLUMN arrival_acknowledged_at TIMESTAMP,
    ADD COLUMN arrival_acknowledged_by VARCHAR(200);

COMMENT ON COLUMN registration_schema.emergency_registration.arrival_acknowledged_at IS
    'Timestamp when ER staff first acknowledged patient arrival (ARRIVED status)';

-- Add treatment start timestamp (when active treatment begins)
ALTER TABLE registration_schema.emergency_registration
    ADD COLUMN treatment_start_time TIMESTAMP,
    ADD COLUMN treatment_started_by VARCHAR(200);

COMMENT ON COLUMN registration_schema.emergency_registration.treatment_start_time IS
    'Timestamp when active emergency treatment began (IN_TREATMENT status)';

-- ============================================================================
-- 4. Create Emergency Intervention Summary View
-- ============================================================================

CREATE OR REPLACE VIEW registration_schema.v_emergency_intervention_summary AS
SELECT
    ei.emergency_registration_id,
    er.emergency_number,
    er.patient_id,
    ei.encounter_id,

    -- Intervention Counts
    COUNT(*) as total_interventions,
    COUNT(*) FILTER (WHERE ei.intervention_type = 'RESUSCITATION') as resuscitation_count,
    COUNT(*) FILTER (WHERE ei.intervention_type = 'AIRWAY_MANAGEMENT') as airway_procedures,
    COUNT(*) FILTER (WHERE ei.intervention_type IN ('VASCULAR_ACCESS', 'CENTRAL_LINE', 'ARTERIAL_LINE')) as vascular_access_count,
    COUNT(*) FILTER (WHERE ei.intervention_type = 'EMERGENCY_MEDICATION') as emergency_meds_count,
    COUNT(*) FILTER (WHERE ei.intervention_type = 'TRANSFUSION') as transfusion_count,
    COUNT(*) FILTER (WHERE ei.intervention_type IN ('CARDIOVERSION', 'DEFIBRILLATION')) as cardiac_interventions,

    -- Critical Flags
    BOOL_OR(ei.is_resuscitation) as had_resuscitation,
    BOOL_OR(ei.rosc_achieved) as rosc_achieved,
    BOOL_OR(ei.complications_occurred) as had_complications,

    -- Timing
    MIN(ei.intervention_time) as first_intervention_time,
    MAX(ei.intervention_time) as last_intervention_time,
    MIN(ei.resuscitation_start_time) as resuscitation_start,
    MAX(ei.resuscitation_end_time) as resuscitation_end,

    -- Resuscitation Metrics
    SUM(ei.defibrillation_attempts) as total_defibrillations,
    SUM(ei.epinephrine_doses) as total_epinephrine_doses,
    AVG(ei.cpr_quality_score) as avg_cpr_quality,

    -- Audit
    MAX(ei.updated_at) as last_updated

FROM registration_schema.emergency_intervention ei
INNER JOIN registration_schema.emergency_registration er
    ON ei.emergency_registration_id = er.id

GROUP BY
    ei.emergency_registration_id,
    er.emergency_number,
    er.patient_id,
    ei.encounter_id;

COMMENT ON VIEW registration_schema.v_emergency_intervention_summary IS
    'Summarized view of all emergency interventions per patient for dashboard and reporting';

-- ============================================================================
-- 5. Create Indexes for Emergency Dashboard Queries
-- ============================================================================

-- Index for finding active emergency patients with critical interventions
CREATE INDEX idx_emergency_active_critical
    ON registration_schema.emergency_registration(status, is_critical, triage_level)
    WHERE deleted_at IS NULL;

-- Index for recent emergency registrations with encounter link
CREATE INDEX idx_emergency_recent_with_encounter
    ON registration_schema.emergency_registration(registration_date DESC, encounter_id)
    WHERE deleted_at IS NULL;

-- ============================================================================
-- 6. Update Emergency Registration Statistics Function
-- ============================================================================

-- Function to calculate comprehensive emergency metrics
CREATE OR REPLACE FUNCTION registration_schema.calculate_emergency_metrics(
    p_start_date DATE,
    p_end_date DATE
)
RETURNS TABLE (
    total_registrations BIGINT,
    critical_cases BIGINT,
    unknown_patients BIGINT,
    police_cases BIGINT,
    trauma_cases BIGINT,
    avg_door_to_triage_minutes NUMERIC,
    avg_door_to_doctor_minutes NUMERIC,
    avg_total_er_time_minutes NUMERIC,
    patients_with_interventions BIGINT,
    total_interventions BIGINT,
    resuscitation_cases BIGINT,
    rosc_success_rate NUMERIC,
    converted_to_inpatient BIGINT,
    discharged_home BIGINT,
    transferred_out BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(DISTINCT er.id)::BIGINT as total_registrations,
        COUNT(DISTINCT er.id) FILTER (WHERE er.is_critical = TRUE)::BIGINT as critical_cases,
        COUNT(DISTINCT er.id) FILTER (WHERE er.is_unknown_patient = TRUE)::BIGINT as unknown_patients,
        COUNT(DISTINCT er.id) FILTER (WHERE er.is_police_case = TRUE)::BIGINT as police_cases,
        COUNT(DISTINCT er.id) FILTER (WHERE er.is_trauma_case = TRUE)::BIGINT as trauma_cases,

        ROUND(AVG(er.door_to_triage_minutes), 1) as avg_door_to_triage_minutes,
        ROUND(AVG(er.door_to_doctor_minutes), 1) as avg_door_to_doctor_minutes,
        ROUND(AVG(er.total_er_time_minutes), 1) as avg_total_er_time_minutes,

        COUNT(DISTINCT ei.emergency_registration_id)::BIGINT as patients_with_interventions,
        COUNT(ei.id)::BIGINT as total_interventions,
        COUNT(DISTINCT er.id) FILTER (WHERE EXISTS (
            SELECT 1 FROM registration_schema.emergency_intervention ei2
            WHERE ei2.emergency_registration_id = er.id AND ei2.is_resuscitation = TRUE
        ))::BIGINT as resuscitation_cases,

        CASE
            WHEN COUNT(ei.id) FILTER (WHERE ei.is_resuscitation = TRUE) > 0 THEN
                ROUND(
                    (COUNT(ei.id) FILTER (WHERE ei.is_resuscitation = TRUE AND ei.rosc_achieved = TRUE)::NUMERIC /
                     COUNT(ei.id) FILTER (WHERE ei.is_resuscitation = TRUE)::NUMERIC) * 100,
                    1
                )
            ELSE 0
        END as rosc_success_rate,

        COUNT(DISTINCT er.id) FILTER (WHERE er.converted_to_inpatient = TRUE)::BIGINT as converted_to_inpatient,
        COUNT(DISTINCT er.id) FILTER (WHERE er.disposition = 'DISCHARGED_HOME')::BIGINT as discharged_home,
        COUNT(DISTINCT er.id) FILTER (WHERE er.disposition LIKE 'TRANSFERRED%')::BIGINT as transferred_out

    FROM registration_schema.emergency_registration er
    LEFT JOIN registration_schema.emergency_intervention ei ON er.id = ei.emergency_registration_id
    WHERE er.registration_date::DATE BETWEEN p_start_date AND p_end_date
      AND er.deleted_at IS NULL;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION registration_schema.calculate_emergency_metrics IS
    'Calculates comprehensive emergency department metrics for a date range';

-- ============================================================================
-- 7. Data Migration - Link Existing Emergency Registrations to Encounters
-- ============================================================================

-- Note: encounter_id will be NULL for existing records.
-- These will be populated when:
-- 1. New emergency registrations are created (auto-create encounter)
-- 2. Existing registrations can have encounters manually linked or auto-created via script

-- ============================================================================
-- Migration Complete
-- ============================================================================

-- Verification Queries
DO $$
BEGIN
    -- Verify encounter_id column added
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'registration_schema'
          AND table_name = 'emergency_registration'
          AND column_name = 'encounter_id'
    ) THEN
        RAISE NOTICE 'SUCCESS: encounter_id column added to emergency_registration';
    END IF;

    -- Verify emergency_intervention table created
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'registration_schema'
          AND table_name = 'emergency_intervention'
    ) THEN
        RAISE NOTICE 'SUCCESS: emergency_intervention table created';
    END IF;

    -- Count indexes created
    RAISE NOTICE 'Migration V14 completed successfully';
    RAISE NOTICE 'Phase 1: Emergency Workflow Enhancements - Database changes applied';
END $$;
