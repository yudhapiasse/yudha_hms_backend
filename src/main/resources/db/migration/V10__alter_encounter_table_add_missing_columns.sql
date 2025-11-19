-- ============================================================================
-- Flyway Migration V10: Alter Encounter Table - Add Missing Columns
-- Description: Add new columns to encounter table for enhanced functionality
-- Author: HMS Development Team
-- Date: 2025-01-19
-- ============================================================================

-- Add department_id column
ALTER TABLE clinical_schema.encounter
ADD COLUMN IF NOT EXISTS department_id UUID;

-- Add location_id column
ALTER TABLE clinical_schema.encounter
ADD COLUMN IF NOT EXISTS location_id UUID;

-- Add practitioner_id column (attending/primary doctor)
ALTER TABLE clinical_schema.encounter
ADD COLUMN IF NOT EXISTS practitioner_id UUID;

-- Add referring_practitioner_id column
ALTER TABLE clinical_schema.encounter
ADD COLUMN IF NOT EXISTS referring_practitioner_id UUID;

-- Add insurance_type column
ALTER TABLE clinical_schema.encounter
ADD COLUMN IF NOT EXISTS insurance_type VARCHAR(30);

-- Add satusehat_synced column
ALTER TABLE clinical_schema.encounter
ADD COLUMN IF NOT EXISTS satusehat_synced BOOLEAN DEFAULT false;

-- Add satusehat_synced_at column
ALTER TABLE clinical_schema.encounter
ADD COLUMN IF NOT EXISTS satusehat_synced_at TIMESTAMP;

-- Create indexes for new columns
CREATE INDEX IF NOT EXISTS idx_encounter_department ON clinical_schema.encounter(department_id);
CREATE INDEX IF NOT EXISTS idx_encounter_location ON clinical_schema.encounter(location_id);
CREATE INDEX IF NOT EXISTS idx_encounter_practitioner ON clinical_schema.encounter(practitioner_id);
CREATE INDEX IF NOT EXISTS idx_encounter_referring_practitioner ON clinical_schema.encounter(referring_practitioner_id);
CREATE INDEX IF NOT EXISTS idx_encounter_insurance_type ON clinical_schema.encounter(insurance_type);

-- Add comments
COMMENT ON COLUMN clinical_schema.encounter.department_id IS 'Department ID reference';
COMMENT ON COLUMN clinical_schema.encounter.location_id IS 'Location/Room ID reference';
COMMENT ON COLUMN clinical_schema.encounter.practitioner_id IS 'Attending/Primary doctor ID';
COMMENT ON COLUMN clinical_schema.encounter.referring_practitioner_id IS 'Referring doctor ID';
COMMENT ON COLUMN clinical_schema.encounter.insurance_type IS 'BPJS, PRIVATE_INSURANCE, SELF_PAY, GOVERNMENT, CORPORATE';
COMMENT ON COLUMN clinical_schema.encounter.satusehat_synced IS 'Whether encounter has been synced to SATUSEHAT';
COMMENT ON COLUMN clinical_schema.encounter.satusehat_synced_at IS 'Timestamp when synced to SATUSEHAT';

-- ============================================================================
-- SUMMARY
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V10 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Columns Added to clinical_schema.encounter:';
    RAISE NOTICE '  - department_id (UUID)';
    RAISE NOTICE '  - location_id (UUID)';
    RAISE NOTICE '  - practitioner_id (UUID)';
    RAISE NOTICE '  - referring_practitioner_id (UUID)';
    RAISE NOTICE '  - insurance_type (VARCHAR)';
    RAISE NOTICE '  - satusehat_synced (BOOLEAN)';
    RAISE NOTICE '  - satusehat_synced_at (TIMESTAMP)';
    RAISE NOTICE '';
    RAISE NOTICE 'Indexes Created:';
    RAISE NOTICE '  - idx_encounter_department';
    RAISE NOTICE '  - idx_encounter_location';
    RAISE NOTICE '  - idx_encounter_practitioner';
    RAISE NOTICE '  - idx_encounter_referring_practitioner';
    RAISE NOTICE '  - idx_encounter_insurance_type';
    RAISE NOTICE '============================================';
END $$;
