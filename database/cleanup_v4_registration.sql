-- ============================================================================
-- Cleanup Script: Remove V4 Registration Objects for V5 Migration
-- Description: Drops V4 registration and clinical tables to resolve conflicts
-- Author: HMS Development Team
-- Date: 2025-01-19
-- ============================================================================

\c hms_dev;

-- Set search path
SET search_path TO registration_schema, clinical_schema, public;

-- ============================================================================
-- STEP 1: Drop Clinical Tables (They depend on registration table)
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Dropping Clinical Module Tables from V4...';
    RAISE NOTICE '============================================';
END $$;

-- Drop vital_signs table
DROP TABLE IF EXISTS clinical_schema.vital_signs CASCADE;
RAISE NOTICE '✓ Dropped table: clinical_schema.vital_signs';

-- Drop diagnosis table
DROP TABLE IF EXISTS clinical_schema.diagnosis CASCADE;
RAISE NOTICE '✓ Dropped table: clinical_schema.diagnosis';

-- Drop clinical_note table
DROP TABLE IF EXISTS clinical_schema.clinical_note CASCADE;
RAISE NOTICE '✓ Dropped table: clinical_schema.clinical_note';

-- ============================================================================
-- STEP 2: Drop Registration Table
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE 'Dropping Registration Table from V4...';
END $$;

-- Drop registration table (this will also drop its indexes)
DROP TABLE IF EXISTS registration_schema.registration CASCADE;
RAISE NOTICE '✓ Dropped table: registration_schema.registration';

-- ============================================================================
-- STEP 3: Drop Registration Sequence
-- ============================================================================

DROP SEQUENCE IF EXISTS registration_schema.registration_number_sequence CASCADE;
RAISE NOTICE '✓ Dropped sequence: registration_schema.registration_number_sequence';

-- ============================================================================
-- STEP 4: Verify Cleanup
-- ============================================================================

DO $$
DECLARE
    registration_exists BOOLEAN;
    clinical_note_exists BOOLEAN;
    diagnosis_exists BOOLEAN;
    vital_signs_exists BOOLEAN;
BEGIN
    -- Check if tables still exist
    SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'registration_schema'
        AND table_name = 'registration'
    ) INTO registration_exists;

    SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'clinical_schema'
        AND table_name = 'clinical_note'
    ) INTO clinical_note_exists;

    SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'clinical_schema'
        AND table_name = 'diagnosis'
    ) INTO diagnosis_exists;

    SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'clinical_schema'
        AND table_name = 'vital_signs'
    ) INTO vital_signs_exists;

    RAISE NOTICE '';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Cleanup Verification:';
    RAISE NOTICE '============================================';

    IF registration_exists THEN
        RAISE NOTICE '✗ registration table still exists!';
    ELSE
        RAISE NOTICE '✓ registration table removed';
    END IF;

    IF clinical_note_exists THEN
        RAISE NOTICE '✗ clinical_note table still exists!';
    ELSE
        RAISE NOTICE '✓ clinical_note table removed';
    END IF;

    IF diagnosis_exists THEN
        RAISE NOTICE '✗ diagnosis table still exists!';
    ELSE
        RAISE NOTICE '✓ diagnosis table removed';
    END IF;

    IF vital_signs_exists THEN
        RAISE NOTICE '✗ vital_signs table still exists!';
    ELSE
        RAISE NOTICE '✓ vital_signs table removed';
    END IF;

    RAISE NOTICE '============================================';
    RAISE NOTICE '';
    RAISE NOTICE 'Next steps:';
    RAISE NOTICE '  1. Start the HMS application to run V5 migration';
    RAISE NOTICE '  2. V5 will create comprehensive outpatient registration tables';
    RAISE NOTICE '  3. Clinical tables will be recreated in future migrations';
    RAISE NOTICE '============================================';
END $$;
