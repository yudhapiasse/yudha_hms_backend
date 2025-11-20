-- ============================================================================
-- Flyway Migration V21: Fix Version Column Types in Phase 4.1 Tables
-- Description: Change version column type from INTEGER to BIGINT for JPA
--              Required by AuditableEntity base class which uses Long
-- Author: HMS Development Team
-- Date: 2025-11-20
-- ============================================================================

-- Fix version column type in physical_examination
DO $$
BEGIN
    -- Check if column exists and alter type if needed
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'clinical_schema'
               AND table_name = 'physical_examination'
               AND column_name = 'version') THEN
        ALTER TABLE clinical_schema.physical_examination
        ALTER COLUMN version TYPE BIGINT;
    ELSE
        ALTER TABLE clinical_schema.physical_examination
        ADD COLUMN version BIGINT DEFAULT 0;
    END IF;
END $$;

-- Fix version column type in encounter_procedures
DO $$
BEGIN
    -- Check if column exists and alter type if needed
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'clinical_schema'
               AND table_name = 'encounter_procedures'
               AND column_name = 'version') THEN
        ALTER TABLE clinical_schema.encounter_procedures
        ALTER COLUMN version TYPE BIGINT;
    ELSE
        ALTER TABLE clinical_schema.encounter_procedures
        ADD COLUMN version BIGINT DEFAULT 0;
    END IF;
END $$;

-- Fix version column type in clinical_note_templates
DO $$
BEGIN
    -- Check if column exists and alter type if needed
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'clinical_schema'
               AND table_name = 'clinical_note_templates'
               AND column_name = 'version') THEN
        ALTER TABLE clinical_schema.clinical_note_templates
        ALTER COLUMN version TYPE BIGINT;
    ELSE
        ALTER TABLE clinical_schema.clinical_note_templates
        ADD COLUMN version BIGINT DEFAULT 0;
    END IF;
END $$;
