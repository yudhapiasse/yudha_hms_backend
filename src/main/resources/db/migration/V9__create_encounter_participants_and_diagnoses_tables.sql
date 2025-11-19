-- ============================================================================
-- Flyway Migration V9: Create Encounter Participants and Diagnoses Tables
-- Description: Many-to-many relationships for encounter participants and diagnoses
-- Author: HMS Development Team
-- Date: 2025-01-19
-- ============================================================================

-- ============================================================================
-- ENCOUNTER PARTICIPANTS TABLE (Many-to-Many)
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.encounter_participants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Relationships
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,
    practitioner_id UUID NOT NULL,

    -- Participant details
    participant_type VARCHAR(30) NOT NULL, -- PRIMARY, SECONDARY, CONSULTANT, ANESTHESIOLOGIST, NURSE, SPECIALIST
    participant_name VARCHAR(200),
    participant_role VARCHAR(100), -- Additional role description

    -- Period of participation
    period_start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    period_end TIMESTAMP,

    -- Additional information
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Ensure unique participant per encounter (can't have duplicate participant types for same practitioner)
    CONSTRAINT uk_encounter_participant UNIQUE (encounter_id, practitioner_id, participant_type)
);

CREATE INDEX idx_encounter_participants_encounter ON clinical_schema.encounter_participants(encounter_id);
CREATE INDEX idx_encounter_participants_practitioner ON clinical_schema.encounter_participants(practitioner_id);
CREATE INDEX idx_encounter_participants_type ON clinical_schema.encounter_participants(participant_type);
CREATE INDEX idx_encounter_participants_period ON clinical_schema.encounter_participants(period_start, period_end);

COMMENT ON TABLE clinical_schema.encounter_participants IS 'Practitioners participating in an encounter (care team)';
COMMENT ON COLUMN clinical_schema.encounter_participants.participant_type IS 'PRIMARY, SECONDARY, CONSULTANT, ANESTHESIOLOGIST, NURSE, SPECIALIST';

-- ============================================================================
-- ENCOUNTER DIAGNOSES TABLE (Many-to-Many)
-- ============================================================================
CREATE TABLE IF NOT EXISTS clinical_schema.encounter_diagnoses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Relationships
    encounter_id UUID NOT NULL REFERENCES clinical_schema.encounter(id) ON DELETE CASCADE,
    diagnosis_id UUID, -- FK to icd10_codes table (will be created in master data)

    -- Diagnosis information
    diagnosis_code VARCHAR(10) NOT NULL, -- ICD-10 code (e.g., A00.0)
    diagnosis_text TEXT NOT NULL, -- Description of the diagnosis

    -- Diagnosis classification
    diagnosis_type VARCHAR(30) NOT NULL, -- PRIMARY, SECONDARY, ADMISSION, DISCHARGE, DIFFERENTIAL, WORKING
    clinical_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, RESOLVED, RECURRENCE, REMISSION, INACTIVE

    -- Priority/Ranking
    rank INTEGER DEFAULT 1, -- Order of importance (1 = highest priority)

    -- Verification
    verification_status VARCHAR(30) DEFAULT 'PROVISIONAL', -- PROVISIONAL, CONFIRMED, DIFFERENTIAL, REFUTED

    -- Clinical details
    onset_date DATE, -- When the condition started
    recorded_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- When this diagnosis was recorded
    severity VARCHAR(20), -- MILD, MODERATE, SEVERE, CRITICAL

    -- Provider information
    diagnosed_by_id UUID, -- Practitioner who made the diagnosis
    diagnosed_by_name VARCHAR(200),

    -- Notes
    clinical_notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Ensure valid ranking
    CONSTRAINT chk_diagnosis_rank CHECK (rank > 0)
);

CREATE INDEX idx_encounter_diagnoses_encounter ON clinical_schema.encounter_diagnoses(encounter_id);
CREATE INDEX idx_encounter_diagnoses_diagnosis ON clinical_schema.encounter_diagnoses(diagnosis_id);
CREATE INDEX idx_encounter_diagnoses_code ON clinical_schema.encounter_diagnoses(diagnosis_code);
CREATE INDEX idx_encounter_diagnoses_type ON clinical_schema.encounter_diagnoses(diagnosis_type);
CREATE INDEX idx_encounter_diagnoses_status ON clinical_schema.encounter_diagnoses(clinical_status);
CREATE INDEX idx_encounter_diagnoses_rank ON clinical_schema.encounter_diagnoses(encounter_id, rank);

COMMENT ON TABLE clinical_schema.encounter_diagnoses IS 'Diagnoses associated with encounters (ICD-10 coded)';
COMMENT ON COLUMN clinical_schema.encounter_diagnoses.diagnosis_type IS 'PRIMARY, SECONDARY, ADMISSION, DISCHARGE, DIFFERENTIAL, WORKING';
COMMENT ON COLUMN clinical_schema.encounter_diagnoses.clinical_status IS 'ACTIVE, RESOLVED, RECURRENCE, REMISSION, INACTIVE';
COMMENT ON COLUMN clinical_schema.encounter_diagnoses.rank IS 'Priority ranking (1 = highest)';

-- ============================================================================
-- UPDATE TRIGGERS
-- ============================================================================

CREATE TRIGGER update_encounter_participants_timestamp
    BEFORE UPDATE ON clinical_schema.encounter_participants
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_encounter_diagnoses_timestamp
    BEFORE UPDATE ON clinical_schema.encounter_diagnoses
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================================
-- SUMMARY
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V9 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Encounter Management Extensions Created:';
    RAISE NOTICE '  - encounter_participants (care team management)';
    RAISE NOTICE '  - encounter_diagnoses (ICD-10 diagnosis tracking)';
    RAISE NOTICE '';
    RAISE NOTICE 'Key Features:';
    RAISE NOTICE '  - Many-to-many relationship for encounter participants';
    RAISE NOTICE '  - Participant type classification (PRIMARY, SECONDARY, CONSULTANT, etc.)';
    RAISE NOTICE '  - Period tracking for participant involvement';
    RAISE NOTICE '  - ICD-10 diagnosis coding support';
    RAISE NOTICE '  - Diagnosis type and clinical status tracking';
    RAISE NOTICE '  - Priority ranking for diagnoses';
    RAISE NOTICE '  - Comprehensive audit trail';
    RAISE NOTICE '============================================';
END $$;