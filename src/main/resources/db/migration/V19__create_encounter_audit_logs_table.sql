-- ============================================================================
-- Flyway Migration V19: Create Encounter Audit Logs Table
-- Description: Audit log for encounter changes and access tracking
-- Author: HMS Development Team
-- Date: 2025-11-20
-- ============================================================================

-- ============================================================================
-- ENCOUNTER AUDIT LOGS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS encounter_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Encounter reference
    encounter_id UUID NOT NULL,
    encounter_number VARCHAR(50),
    patient_id UUID,

    -- Audit action details
    action_type VARCHAR(50) NOT NULL,
    action_description TEXT,

    -- Change tracking
    old_value TEXT,
    new_value TEXT,
    field_changed VARCHAR(100),

    -- User information
    user_id UUID,
    username VARCHAR(100),
    user_role VARCHAR(50),

    -- Session tracking
    ip_address VARCHAR(50),
    user_agent TEXT,
    session_id VARCHAR(100),

    -- Timestamp
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Sensitive access tracking
    is_sensitive_access BOOLEAN,
    access_reason TEXT,

    -- Supervisor override
    supervisor_override BOOLEAN,
    supervisor_id UUID
);

-- ============================================================================
-- INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_audit_encounter_id ON encounter_audit_logs(encounter_id);
CREATE INDEX IF NOT EXISTS idx_audit_user_id ON encounter_audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON encounter_audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_action_type ON encounter_audit_logs(action_type);

-- ============================================================================
-- COMMENTS
-- ============================================================================
COMMENT ON TABLE encounter_audit_logs IS 'Audit log for encounter changes and access tracking';
COMMENT ON COLUMN encounter_audit_logs.encounter_id IS 'Reference to the encounter being audited';
COMMENT ON COLUMN encounter_audit_logs.action_type IS 'Type of action: CREATED, VIEWED, UPDATED, STATUS_CHANGED, etc.';
COMMENT ON COLUMN encounter_audit_logs.is_sensitive_access IS 'Flag for VIP or psychiatric encounter access';
COMMENT ON COLUMN encounter_audit_logs.supervisor_override IS 'Indicates if supervisor approval was used';
