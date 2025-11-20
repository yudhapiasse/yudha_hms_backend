-- =============================================================================
-- SATUSEHAT Integration Tables
-- Version: 1.0.0
-- Date: 2025-01-20
-- Description: Creates tables for SATUSEHAT FHIR R4 API integration
--              supporting OAuth2 authentication and resource management
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. SATUSEHAT Configuration Table
-- -----------------------------------------------------------------------------
CREATE TABLE satusehat_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id VARCHAR(100) NOT NULL UNIQUE,
    organization_name VARCHAR(255) NOT NULL,

    -- Environment settings
    environment VARCHAR(20) NOT NULL DEFAULT 'SANDBOX', -- SANDBOX or PRODUCTION
    is_active BOOLEAN DEFAULT TRUE,

    -- OAuth2 Credentials
    client_id VARCHAR(255) NOT NULL,
    client_secret TEXT NOT NULL, -- Encrypted

    -- API Endpoints
    auth_url VARCHAR(500) NOT NULL,
    fhir_base_url VARCHAR(500) NOT NULL,
    consent_url VARCHAR(500) NOT NULL,

    -- Token Management
    current_access_token TEXT,
    token_expires_at TIMESTAMP,
    token_issued_at TIMESTAMP,

    -- Rate Limiting Configuration
    rate_limit_per_second INTEGER DEFAULT 100,
    rate_limit_burst INTEGER DEFAULT 1000,

    -- Retry Configuration
    max_retry_attempts INTEGER DEFAULT 3,
    retry_backoff_ms INTEGER DEFAULT 1000,
    timeout_seconds INTEGER DEFAULT 30,

    -- Location & Practitioner Mapping
    location_ids JSONB, -- Array of location IDs for facilities
    practitioner_mapping JSONB, -- Map of NIK to Practitioner IDs

    -- Audit fields
    created_by UUID NOT NULL,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_environment CHECK (environment IN ('SANDBOX', 'PRODUCTION'))
);

COMMENT ON TABLE satusehat_config IS 'SATUSEHAT OAuth2 and FHIR API configuration per organization';
COMMENT ON COLUMN satusehat_config.client_secret IS 'AES-256 encrypted OAuth2 client secret';
COMMENT ON COLUMN satusehat_config.current_access_token IS 'Cached access token (also stored in Redis)';
COMMENT ON COLUMN satusehat_config.location_ids IS 'Array of SATUSEHAT Location IDs for hospital facilities';
COMMENT ON COLUMN satusehat_config.practitioner_mapping IS 'JSON map: {"NIK": "Practitioner-ID"}';

-- Index for fast lookup
CREATE INDEX idx_satusehat_config_org_id ON satusehat_config(organization_id);
CREATE INDEX idx_satusehat_config_environment ON satusehat_config(environment, is_active);

-- -----------------------------------------------------------------------------
-- 2. SATUSEHAT Audit Logs Table
-- -----------------------------------------------------------------------------
CREATE TABLE satusehat_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_id UUID REFERENCES satusehat_config(id) ON DELETE CASCADE,

    -- Request Details
    operation_type VARCHAR(50) NOT NULL, -- AUTH, CREATE, UPDATE, READ, SEARCH, etc.
    resource_type VARCHAR(100), -- Patient, Encounter, Observation, etc.
    resource_id VARCHAR(100),
    method VARCHAR(10) NOT NULL, -- GET, POST, PUT, PATCH, DELETE
    endpoint VARCHAR(1000) NOT NULL,

    -- Request/Response Data
    request_headers JSONB,
    request_body JSONB,
    response_status INTEGER,
    response_headers JSONB,
    response_body JSONB,

    -- Error Tracking
    error_code VARCHAR(50),
    error_message TEXT,

    -- Performance Metrics
    execution_time_ms INTEGER,
    retry_count INTEGER DEFAULT 0,

    -- Client Information
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    user_id UUID NOT NULL,

    -- Audit Trail
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE satusehat_audit_logs IS 'Comprehensive audit trail for SATUSEHAT API operations';
COMMENT ON COLUMN satusehat_audit_logs.operation_type IS 'Type of operation: AUTH, CREATE, UPDATE, READ, SEARCH, DELETE';
COMMENT ON COLUMN satusehat_audit_logs.resource_type IS 'FHIR resource type (Patient, Encounter, Observation, etc.)';

-- Indexes for audit log queries
CREATE INDEX idx_satusehat_audit_config ON satusehat_audit_logs(config_id);
CREATE INDEX idx_satusehat_audit_created_at ON satusehat_audit_logs(created_at);
CREATE INDEX idx_satusehat_audit_operation ON satusehat_audit_logs(operation_type);
CREATE INDEX idx_satusehat_audit_resource ON satusehat_audit_logs(resource_type);
CREATE INDEX idx_satusehat_audit_user ON satusehat_audit_logs(user_id);
CREATE INDEX idx_satusehat_audit_errors ON satusehat_audit_logs(response_status) WHERE error_message IS NOT NULL;

-- -----------------------------------------------------------------------------
-- 3. SATUSEHAT Resource Mapping Table (for tracking submitted resources)
-- -----------------------------------------------------------------------------
CREATE TABLE satusehat_resource_mappings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_id UUID REFERENCES satusehat_config(id) ON DELETE CASCADE,

    -- Local Resource Reference
    local_resource_type VARCHAR(100) NOT NULL, -- Patient, Encounter, Observation, etc.
    local_resource_id UUID NOT NULL,

    -- SATUSEHAT Resource Reference
    satusehat_resource_type VARCHAR(100) NOT NULL,
    satusehat_resource_id VARCHAR(100) NOT NULL,
    satusehat_identifier VARCHAR(255), -- IHS number, NIK, etc.

    -- Resource Status
    submission_status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SUBMITTED, UPDATED, FAILED
    last_submitted_at TIMESTAMP,
    last_error TEXT,

    -- Version Control
    version_id VARCHAR(100), -- FHIR resource version
    last_updated TIMESTAMP,

    -- Resource Data Cache
    resource_snapshot JSONB, -- Cache of the FHIR resource

    -- Audit fields
    created_by UUID NOT NULL,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_local_resource UNIQUE (local_resource_type, local_resource_id),
    CONSTRAINT chk_submission_status CHECK (submission_status IN ('PENDING', 'SUBMITTED', 'UPDATED', 'FAILED'))
);

COMMENT ON TABLE satusehat_resource_mappings IS 'Mapping between local HMS resources and SATUSEHAT FHIR resources';
COMMENT ON COLUMN satusehat_resource_mappings.satusehat_identifier IS 'Primary identifier (IHS, NIK, etc.)';
COMMENT ON COLUMN satusehat_resource_mappings.resource_snapshot IS 'Cached FHIR resource for reference';

-- Indexes for resource mapping queries
CREATE INDEX idx_satusehat_mapping_local ON satusehat_resource_mappings(local_resource_type, local_resource_id);
CREATE INDEX idx_satusehat_mapping_remote ON satusehat_resource_mappings(satusehat_resource_type, satusehat_resource_id);
CREATE INDEX idx_satusehat_mapping_status ON satusehat_resource_mappings(submission_status);
CREATE INDEX idx_satusehat_mapping_identifier ON satusehat_resource_mappings(satusehat_identifier);

-- -----------------------------------------------------------------------------
-- Triggers for updated_at timestamps
-- -----------------------------------------------------------------------------

-- Function to update timestamp
CREATE OR REPLACE FUNCTION update_satusehat_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers
CREATE TRIGGER satusehat_config_updated_at
    BEFORE UPDATE ON satusehat_config
    FOR EACH ROW
    EXECUTE FUNCTION update_satusehat_timestamp();

CREATE TRIGGER satusehat_mapping_updated_at
    BEFORE UPDATE ON satusehat_resource_mappings
    FOR EACH ROW
    EXECUTE FUNCTION update_satusehat_timestamp();

-- -----------------------------------------------------------------------------
-- Sample Configuration (Development/Sandbox)
-- -----------------------------------------------------------------------------
-- Uncomment to insert sample configuration

-- INSERT INTO satusehat_config (
--     organization_id,
--     organization_name,
--     environment,
--     client_id,
--     client_secret,
--     auth_url,
--     fhir_base_url,
--     consent_url,
--     is_active,
--     rate_limit_per_second,
--     rate_limit_burst,
--     created_by
-- ) VALUES (
--     'ORG-SANDBOX-001',
--     'Test Hospital - Sandbox',
--     'SANDBOX',
--     'YOUR_CLIENT_ID_FROM_SATUSEHAT',
--     'ENCRYPTED_CLIENT_SECRET',
--     'https://api-satusehat-stg.dto.kemkes.go.id/oauth2/v1',
--     'https://api-satusehat-stg.dto.kemkes.go.id/fhir-r4/v1',
--     'https://api-satusehat-stg.dto.kemkes.go.id/consent/v1',
--     TRUE,
--     100,
--     1000,
--     '00000000-0000-0000-0000-000000000000'
-- );

-- =============================================================================
-- End of SATUSEHAT Integration Tables Migration
-- =============================================================================
