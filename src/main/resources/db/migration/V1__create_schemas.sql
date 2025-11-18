-- ============================================================================
-- Flyway Migration V1: Create Database Schemas
-- Description: Creates multiple schemas for modular organization
-- Author: HMS Development Team
-- Date: 2025-01-18
-- ============================================================================

-- Note: Extensions and database-level setup should be done manually using
-- the database/00_init_database.sql script before running the application.

-- Create schemas if they don't exist
-- Flyway runs in the public schema by default

-- Note: Schemas are created by 00_init_database.sql with proper ownership
-- These CREATE SCHEMA statements are kept for idempotency in case init script wasn't run
-- Comments are omitted here to avoid permission errors (they're in init script)

-- Master data schema
CREATE SCHEMA IF NOT EXISTS master_schema;

-- Patient module schema
CREATE SCHEMA IF NOT EXISTS patient_schema;

-- Registration module schema
CREATE SCHEMA IF NOT EXISTS registration_schema;

-- Clinical module schema
CREATE SCHEMA IF NOT EXISTS clinical_schema;

-- Billing module schema
CREATE SCHEMA IF NOT EXISTS billing_schema;

-- Pharmacy module schema
CREATE SCHEMA IF NOT EXISTS pharmacy_schema;

-- Laboratory module schema
CREATE SCHEMA IF NOT EXISTS laboratory_schema;

-- Radiology module schema
CREATE SCHEMA IF NOT EXISTS radiology_schema;

-- Integration module schema
CREATE SCHEMA IF NOT EXISTS integration_schema;

-- ============================================================================
-- Audit trigger function
-- ============================================================================

-- Note: The update_updated_at_column() function is created in 00_init_database.sql
-- and should already exist. Skipped here to avoid ownership conflicts.

-- ============================================================================
-- Summary
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V1 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Schemas created:';
    RAISE NOTICE '  - master_schema';
    RAISE NOTICE '  - patient_schema';
    RAISE NOTICE '  - registration_schema';
    RAISE NOTICE '  - clinical_schema';
    RAISE NOTICE '  - billing_schema';
    RAISE NOTICE '  - pharmacy_schema';
    RAISE NOTICE '  - laboratory_schema';
    RAISE NOTICE '  - radiology_schema';
    RAISE NOTICE '  - integration_schema';
    RAISE NOTICE '============================================';
END $$;