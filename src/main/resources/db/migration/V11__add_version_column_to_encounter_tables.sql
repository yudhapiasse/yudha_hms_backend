-- ============================================================================
-- Flyway Migration V11: Add Version Column to Encounter Tables
-- Description: Add version column for optimistic locking
-- Author: HMS Development Team
-- Date: 2025-01-19
-- ============================================================================

-- Add version column to encounter_participants if it doesn't exist
ALTER TABLE clinical_schema.encounter_participants
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Add version column to encounter_diagnoses if it doesn't exist
ALTER TABLE clinical_schema.encounter_diagnoses
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Add comments
COMMENT ON COLUMN clinical_schema.encounter_participants.version IS 'Version for optimistic locking';
COMMENT ON COLUMN clinical_schema.encounter_diagnoses.version IS 'Version for optimistic locking';

-- ============================================================================
-- SUMMARY
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V11 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Version Column Added for Optimistic Locking:';
    RAISE NOTICE '  - clinical_schema.encounter_participants.version';
    RAISE NOTICE '  - clinical_schema.encounter_diagnoses.version';
    RAISE NOTICE '============================================';
END $$;
