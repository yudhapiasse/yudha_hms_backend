-- =============================================================================
-- E-Klaim Integration Tables
-- Version: 1.0.0
-- Date: 2025-01-20
-- Description: Creates tables for E-Klaim 5.10.x Web Service integration
--              supporting INA-CBGs claim processing
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. E-Klaim Configuration Table
-- -----------------------------------------------------------------------------
CREATE TABLE eklaim_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hospital_code VARCHAR(50) NOT NULL UNIQUE,
    base_url VARCHAR(255) NOT NULL,
    cons_id VARCHAR(100) NOT NULL,
    secret_key TEXT NOT NULL, -- AES-256 key (hex format)
    user_key TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_production BOOLEAN DEFAULT FALSE,
    rate_limit_per_minute INTEGER DEFAULT 100,
    timeout_seconds INTEGER DEFAULT 30,
    max_retry_attempts INTEGER DEFAULT 3,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE eklaim_config IS 'E-Klaim Web Service configuration per hospital/facility';
COMMENT ON COLUMN eklaim_config.secret_key IS 'AES-256 encryption key in hexadecimal format (64 characters)';
COMMENT ON COLUMN eklaim_config.user_key IS 'E-Klaim user authentication key';
COMMENT ON COLUMN eklaim_config.rate_limit_per_minute IS 'Maximum API requests allowed per minute';

-- Index for fast lookup by hospital code
CREATE INDEX idx_eklaim_config_hospital ON eklaim_config(hospital_code);

-- -----------------------------------------------------------------------------
-- 2. E-Klaim Claims Table
-- -----------------------------------------------------------------------------
CREATE TABLE eklaim_claims (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number VARCHAR(50) UNIQUE,
    nomor_sep VARCHAR(14) NOT NULL UNIQUE,
    status INTEGER NOT NULL DEFAULT 1,
    patient_id UUID NOT NULL,
    encounter_id UUID NOT NULL,

    -- JSON data fields for flexible storage
    sep_data JSONB NOT NULL,
    patient_data JSONB,
    admission_data JSONB,
    billing_data JSONB,
    diagnosis_data JSONB,
    procedure_data JSONB,

    -- iDRG results
    idrg_code VARCHAR(50),
    idrg_tariff DECIMAL(15,2),
    idrg_result JSONB,

    -- INA-CBGs results
    cbg_code VARCHAR(50),
    base_tariff DECIMAL(15,2),

    -- Top-ups and adjustments
    top_up_covid DECIMAL(15,2) DEFAULT 0,
    top_up_chronic DECIMAL(15,2) DEFAULT 0,
    upgrade_class DECIMAL(15,2) DEFAULT 0,
    special_cmg DECIMAL(15,2) DEFAULT 0,
    special_prosthesis DECIMAL(15,2) DEFAULT 0,
    special_drug DECIMAL(15,2) DEFAULT 0,
    total_tariff DECIMAL(15,2),

    inacbg_result JSONB,
    prosthesis_items JSONB,
    special_cmg_data JSONB,

    -- SITB (TB Information System) integration
    is_tb_case BOOLEAN DEFAULT FALSE,
    sitb_tasks JSONB,
    sitb_completed BOOLEAN DEFAULT FALSE,

    -- Finalization flags
    idrg_finalized BOOLEAN DEFAULT FALSE,
    inacbg_finalized BOOLEAN DEFAULT FALSE,

    -- Submission tracking
    submission_receipt VARCHAR(100),
    submission_date TIMESTAMP,

    -- Verification (from BPJS)
    verifier_name VARCHAR(200),
    verifier_notes TEXT,
    verification_date TIMESTAMP,

    -- Payment
    ba_number VARCHAR(100), -- Berita Acara (Payment Batch Number)
    payment_date TIMESTAMP,

    -- Rejection handling
    rejection_reason TEXT,
    original_claim_id UUID REFERENCES eklaim_claims(id),

    -- Audit fields
    created_by UUID NOT NULL,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE eklaim_claims IS 'E-Klaim claims for INA-CBGs processing';
COMMENT ON COLUMN eklaim_claims.status IS '1=Draft, 2=Ungrouped, 3=iDRG Grouped, 4=INACBG Grouped, 5=Finalized, 6=Submitted, 7=Verified, 8=Approved with BA, 9=Rejected, 10=Resubmitted';
COMMENT ON COLUMN eklaim_claims.nomor_sep IS 'SEP number from VClaim (14 digits)';
COMMENT ON COLUMN eklaim_claims.claim_number IS 'E-Klaim claim number';
COMMENT ON COLUMN eklaim_claims.idrg_code IS 'Indonesia Diagnosis Related Group code';
COMMENT ON COLUMN eklaim_claims.cbg_code IS 'INA-CBGs (Indonesian Case-Based Groups) code';
COMMENT ON COLUMN eklaim_claims.ba_number IS 'Berita Acara (Payment Batch) number';

-- Indexes for performance
CREATE INDEX idx_eklaim_claims_nomor_sep ON eklaim_claims(nomor_sep);
CREATE INDEX idx_eklaim_claims_claim_number ON eklaim_claims(claim_number);
CREATE INDEX idx_eklaim_claims_status ON eklaim_claims(status);
CREATE INDEX idx_eklaim_claims_submission_date ON eklaim_claims(submission_date);
CREATE INDEX idx_eklaim_claims_patient ON eklaim_claims(patient_id);
CREATE INDEX idx_eklaim_claims_encounter ON eklaim_claims(encounter_id);
CREATE INDEX idx_eklaim_claims_ba_number ON eklaim_claims(ba_number);
CREATE INDEX idx_eklaim_claims_tb_case ON eklaim_claims(is_tb_case) WHERE is_tb_case = TRUE;

-- -----------------------------------------------------------------------------
-- 3. E-Klaim Audit Logs Table
-- -----------------------------------------------------------------------------
CREATE TABLE eklaim_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_id UUID REFERENCES eklaim_claims(id) ON DELETE CASCADE,
    action VARCHAR(100) NOT NULL,
    method VARCHAR(10) NOT NULL,

    -- Request/Response data
    request_data JSONB,
    encrypted_request TEXT,
    response_data JSONB,
    encrypted_response TEXT,

    -- Status and errors
    status_code VARCHAR(10),
    error_message TEXT,
    execution_time_ms INTEGER,

    -- Client information
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),

    -- User tracking
    user_id UUID NOT NULL,

    -- Data modification tracking
    old_values JSONB,
    new_values JSONB,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE eklaim_audit_logs IS 'Comprehensive audit trail for E-Klaim operations (5-year retention required)';
COMMENT ON COLUMN eklaim_audit_logs.action IS 'E-Klaim method name (e.g., new_claim, grouper_1, etc.)';
COMMENT ON COLUMN eklaim_audit_logs.encrypted_request IS 'Encrypted request sent to E-Klaim API';
COMMENT ON COLUMN eklaim_audit_logs.encrypted_response IS 'Encrypted response received from E-Klaim API';

-- Indexes for audit log queries
CREATE INDEX idx_eklaim_audit_claim_id ON eklaim_audit_logs(claim_id);
CREATE INDEX idx_eklaim_audit_created_at ON eklaim_audit_logs(created_at);
CREATE INDEX idx_eklaim_audit_action ON eklaim_audit_logs(action);
CREATE INDEX idx_eklaim_audit_user ON eklaim_audit_logs(user_id);
CREATE INDEX idx_eklaim_audit_errors ON eklaim_audit_logs(status_code) WHERE error_message IS NOT NULL;

-- -----------------------------------------------------------------------------
-- Triggers for updated_at timestamps
-- -----------------------------------------------------------------------------

-- Function to update timestamp
CREATE OR REPLACE FUNCTION update_eklaim_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers
CREATE TRIGGER eklaim_config_updated_at
    BEFORE UPDATE ON eklaim_config
    FOR EACH ROW
    EXECUTE FUNCTION update_eklaim_timestamp();

CREATE TRIGGER eklaim_claims_updated_at
    BEFORE UPDATE ON eklaim_claims
    FOR EACH ROW
    EXECUTE FUNCTION update_eklaim_timestamp();

-- -----------------------------------------------------------------------------
-- Sample Configuration (Development)
-- -----------------------------------------------------------------------------
-- Uncomment to insert sample configuration

-- INSERT INTO eklaim_config (
--     hospital_code,
--     base_url,
--     cons_id,
--     secret_key,
--     user_key,
--     is_active,
--     is_production,
--     rate_limit_per_minute,
--     timeout_seconds,
--     max_retry_attempts
-- ) VALUES (
--     '1234',
--     'https://dvlp.bpjs-kesehatan.go.id:9081/eklaim-ws/ws/',
--     'CONS_ID_FROM_BPJS',
--     '0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF', -- Sample hex key
--     'USER_KEY_FROM_BPJS',
--     TRUE,
--     FALSE,
--     100,
--     30,
--     3
-- );

-- =============================================================================
-- End of E-Klaim Integration Tables Migration
-- =============================================================================
