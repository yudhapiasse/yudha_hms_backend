-- ============================================================================
-- HMS (Hospital Management System) Database Initialization Script
-- Database: PostgreSQL 16.6
-- Purpose: Create database, roles, and initial setup
-- ============================================================================
--
-- This script should be run by a PostgreSQL superuser (sudarmanst) to:
-- 1. Create the HMS database
-- 2. Create the application user
-- 3. Grant necessary privileges
-- 4. Create schemas for modular organization
--
-- Usage:
--   psql -U sudarmanst -f 00_init_database.sql
-- ============================================================================

-- ============================================================================
-- SECTION 1: Database and User Creation
-- ============================================================================

-- Create the HMS database (if not exists)
SELECT 'Creating HMS database...' AS status;

-- Drop database if exists (CAUTION: Only for fresh install!)
-- DROP DATABASE IF EXISTS hms_dev;
-- DROP USER IF EXISTS hms_user;

-- Create database
CREATE DATABASE hms_dev
    WITH
    OWNER = sudarmanst
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = 200
    TEMPLATE = template0;

COMMENT ON DATABASE hms_dev IS 'Hospital Management System - Development Database';

-- Create application user
CREATE USER hms_user WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    INHERIT
    NOREPLICATION
    CONNECTION LIMIT 100
    PASSWORD 'hms_password';

COMMENT ON ROLE hms_user IS 'HMS Application Database User';

-- Grant connection and create privileges
GRANT CONNECT, CREATE ON DATABASE hms_dev TO hms_user;

SELECT 'Database and user created successfully!' AS status;

-- ============================================================================
-- SECTION 2: Connect to HMS Database
-- ============================================================================

\c hms_dev

SELECT 'Connected to hms_dev database' AS status;

-- ============================================================================
-- SECTION 3: Enable Required Extensions
-- ============================================================================

SELECT 'Installing PostgreSQL extensions...' AS status;

-- UUID generation (for primary keys)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
COMMENT ON EXTENSION "uuid-ossp" IS 'UUID generation functions';

-- Cryptographic functions (for password hashing if needed)
CREATE EXTENSION IF NOT EXISTS pgcrypto;
COMMENT ON EXTENSION pgcrypto IS 'Cryptographic functions';

-- Full-text search (for patient/document search)
CREATE EXTENSION IF NOT EXISTS pg_trgm;
COMMENT ON EXTENSION pg_trgm IS 'Trigram matching for text search';

-- Additional text search features
CREATE EXTENSION IF NOT EXISTS unaccent;
COMMENT ON EXTENSION unaccent IS 'Text search dictionary that removes accents';

SELECT 'Extensions installed successfully!' AS status;

-- ============================================================================
-- SECTION 4: Create Schemas for Modular Organization
-- ============================================================================

SELECT 'Creating database schemas...' AS status;

-- Public schema (default, used by Flyway)
-- Already exists, just add comment
COMMENT ON SCHEMA public IS 'Default schema for common tables and Flyway metadata';

-- Master data schema (reference/lookup data)
CREATE SCHEMA IF NOT EXISTS master_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA master_schema IS 'Master data: provinces, cities, ICD codes, drug catalog';

-- Patient module schema
CREATE SCHEMA IF NOT EXISTS patient_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA patient_schema IS 'Patient demographics, emergency contacts, addresses';

-- Registration module schema
CREATE SCHEMA IF NOT EXISTS registration_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA registration_schema IS 'Outpatient/Inpatient registration, appointments, bed management';

-- Clinical module schema
CREATE SCHEMA IF NOT EXISTS clinical_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA clinical_schema IS 'Clinical notes, diagnoses, procedures, vital signs';

-- Billing module schema
CREATE SCHEMA IF NOT EXISTS billing_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA billing_schema IS 'Invoices, payments, claims, tariffs';

-- Pharmacy module schema
CREATE SCHEMA IF NOT EXISTS pharmacy_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA pharmacy_schema IS 'Drug inventory, prescriptions, dispensing';

-- Laboratory module schema
CREATE SCHEMA IF NOT EXISTS laboratory_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA laboratory_schema IS 'Lab orders, specimens, results';

-- Radiology module schema
CREATE SCHEMA IF NOT EXISTS radiology_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA radiology_schema IS 'Radiology orders, imaging studies, PACS integration';

-- Integration module schema
CREATE SCHEMA IF NOT EXISTS integration_schema AUTHORIZATION hms_user;
COMMENT ON SCHEMA integration_schema IS 'BPJS, SATUSEHAT, and other external system integrations';

SELECT 'Schemas created successfully!' AS status;

-- ============================================================================
-- SECTION 5: Grant Schema Privileges to Application User
-- ============================================================================

SELECT 'Granting privileges to hms_user...' AS status;

-- Grant usage and create privileges on all schemas
GRANT USAGE, CREATE ON SCHEMA public TO hms_user;
GRANT USAGE, CREATE ON SCHEMA master_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA patient_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA registration_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA clinical_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA billing_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA pharmacy_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA laboratory_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA radiology_schema TO hms_user;
GRANT USAGE, CREATE ON SCHEMA integration_schema TO hms_user;

-- Grant all privileges on all tables in schemas (present and future)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA master_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA patient_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA registration_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA clinical_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA billing_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA pharmacy_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA laboratory_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA radiology_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA integration_schema TO hms_user;

-- Grant privileges on sequences
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA master_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA patient_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA registration_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA clinical_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA billing_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA pharmacy_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA laboratory_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA radiology_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA integration_schema TO hms_user;

-- Grant privileges on functions
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA master_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA patient_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA registration_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA clinical_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA billing_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA pharmacy_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA laboratory_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA radiology_schema TO hms_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA integration_schema TO hms_user;

-- Alter default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA master_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA patient_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA registration_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA clinical_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA billing_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA pharmacy_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA laboratory_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA radiology_schema GRANT ALL ON TABLES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA integration_schema GRANT ALL ON TABLES TO hms_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA master_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA patient_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA registration_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA clinical_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA billing_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA pharmacy_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA laboratory_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA radiology_schema GRANT ALL ON SEQUENCES TO hms_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA integration_schema GRANT ALL ON SEQUENCES TO hms_user;

SELECT 'Privileges granted successfully!' AS status;

-- ============================================================================
-- SECTION 6: Set Search Path
-- ============================================================================

-- Set default search path for hms_user
ALTER ROLE hms_user SET search_path TO public, patient_schema, registration_schema, clinical_schema, billing_schema, pharmacy_schema, laboratory_schema, radiology_schema, integration_schema, master_schema;

SELECT 'Search path configured!' AS status;

-- ============================================================================
-- SECTION 7: Create Audit Trigger Function
-- ============================================================================

SELECT 'Creating audit trigger function...' AS status;

CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION public.update_updated_at_column() IS 'Automatically update updated_at timestamp on row modification';

SELECT 'Audit trigger function created!' AS status;

-- ============================================================================
-- SECTION 8: Summary Information
-- ============================================================================

SELECT '============================================' AS summary;
SELECT 'HMS Database Initialization Complete!' AS summary;
SELECT '============================================' AS summary;
SELECT '' AS summary;
SELECT 'Database Details:' AS summary;
SELECT '  Name: hms_dev' AS summary;
SELECT '  User: hms_user' AS summary;
SELECT '  Password: hms_password (CHANGE IN PRODUCTION!)' AS summary;
SELECT '  Max Connections: 200' AS summary;
SELECT '' AS summary;
SELECT 'Schemas Created:' AS summary;
SELECT '  - public (default)' AS summary;
SELECT '  - master_schema (reference data)' AS summary;
SELECT '  - patient_schema (patient module)' AS summary;
SELECT '  - registration_schema (registration module)' AS summary;
SELECT '  - clinical_schema (clinical module)' AS summary;
SELECT '  - billing_schema (billing module)' AS summary;
SELECT '  - pharmacy_schema (pharmacy module)' AS summary;
SELECT '  - laboratory_schema (laboratory module)' AS summary;
SELECT '  - radiology_schema (radiology module)' AS summary;
SELECT '  - integration_schema (integrations)' AS summary;
SELECT '' AS summary;
SELECT 'Extensions Installed:' AS summary;
SELECT '  - uuid-ossp (UUID generation)' AS summary;
SELECT '  - pgcrypto (encryption)' AS summary;
SELECT '  - pg_trgm (full-text search)' AS summary;
SELECT '  - unaccent (text normalization)' AS summary;
SELECT '' AS summary;
SELECT 'Next Steps:' AS summary;
SELECT '  1. Update application-dev.yml with database credentials' AS summary;
SELECT '  2. Run Spring Boot application (Flyway will migrate tables)' AS summary;
SELECT '  3. Check logs for migration status' AS summary;
SELECT '============================================' AS summary;

-- List all schemas
\dn+

-- List all extensions
\dx

-- Show database summary
\l+ hms_dev