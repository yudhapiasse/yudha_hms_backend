-- =====================================================
-- HMS Database Migration V29
-- Insurance Claim Management Tables
-- Created: 2025-01-21
-- Description: Creates tables for insurance company master data,
--              insurance claims, claim items, documents, and audit logs
-- =====================================================

-- Create insurance_company table
CREATE TABLE billing_schema.insurance_company (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    company_type VARCHAR(50),
    license_number VARCHAR(100),
    tax_id VARCHAR(50),

    -- Contact information
    contact_person VARCHAR(200),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(100),
    contact_fax VARCHAR(50),

    -- Address
    address VARCHAR(500),
    city VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(20),
    website VARCHAR(200),

    -- Contract information
    contract_start_date DATE,
    contract_end_date DATE,
    contract_number VARCHAR(100),
    payment_terms_days INTEGER,

    -- Financial information
    credit_limit DECIMAL(15, 2),
    current_outstanding DECIMAL(15, 2) DEFAULT 0.00,
    default_coverage_percentage DECIMAL(5, 2),

    -- Coverage settings
    requires_pre_authorization BOOLEAN DEFAULT false,
    claim_submission_deadline_days INTEGER,

    -- Claim submission
    electronic_claim_supported BOOLEAN DEFAULT false,
    claim_submission_email VARCHAR(100),
    claim_submission_portal VARCHAR(200),

    -- Banking information
    bank_name VARCHAR(100),
    bank_account_number VARCHAR(50),
    bank_account_holder VARCHAR(200),

    -- Status and notes
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    -- Soft delete
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),

    CONSTRAINT chk_contract_dates CHECK (contract_end_date >= contract_start_date),
    CONSTRAINT chk_credit_limit CHECK (credit_limit IS NULL OR credit_limit >= 0),
    CONSTRAINT chk_current_outstanding CHECK (current_outstanding >= 0),
    CONSTRAINT chk_coverage_percentage CHECK (default_coverage_percentage IS NULL OR
                                             (default_coverage_percentage >= 0 AND default_coverage_percentage <= 100))
);

-- Create indexes for insurance_company
CREATE INDEX idx_insurance_company_code ON billing_schema.insurance_company(code);
CREATE INDEX idx_insurance_company_name ON billing_schema.insurance_company(name);
CREATE INDEX idx_insurance_company_active ON billing_schema.insurance_company(active);
CREATE INDEX idx_insurance_company_contract_dates ON billing_schema.insurance_company(contract_start_date, contract_end_date);

-- Create trigger for insurance_company updated_at
CREATE TRIGGER update_insurance_company_updated_at
    BEFORE UPDATE ON billing_schema.insurance_company
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create insurance_claim table
CREATE TABLE billing_schema.insurance_claim (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number VARCHAR(50) NOT NULL UNIQUE,
    insurance_company_id UUID NOT NULL,
    invoice_id UUID NOT NULL,

    -- Patient information
    patient_id UUID NOT NULL,
    patient_mrn VARCHAR(50) NOT NULL,
    patient_name VARCHAR(200) NOT NULL,

    -- Policy information
    policy_number VARCHAR(100) NOT NULL,
    policy_holder_name VARCHAR(200),
    relationship_to_holder VARCHAR(50),

    -- Claim details
    claim_type VARCHAR(50) NOT NULL,
    service_start_date DATE NOT NULL,
    service_end_date DATE NOT NULL,

    -- Diagnosis and procedure
    diagnosis_codes VARCHAR(500),
    primary_diagnosis VARCHAR(500),
    procedure_codes VARCHAR(500),

    -- Provider information
    treating_physician_id UUID,
    treating_physician_name VARCHAR(200),

    -- Financial information
    claim_amount DECIMAL(15, 2) NOT NULL,
    approved_amount DECIMAL(15, 2),
    paid_amount DECIMAL(15, 2),
    patient_responsibility DECIMAL(15, 2),
    coverage_percentage DECIMAL(5, 2),

    -- Status and dates
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    submission_date TIMESTAMP,
    submitted_by VARCHAR(100),
    review_start_date TIMESTAMP,
    approval_date TIMESTAMP,
    approved_by VARCHAR(200),
    payment_date TIMESTAMP,
    payment_reference VARCHAR(100),

    -- Rejection information
    rejection_date TIMESTAMP,
    rejection_reason VARCHAR(50),
    rejection_notes TEXT,

    -- Appeal information
    appeal_date TIMESTAMP,
    appeal_reason TEXT,

    -- Pre-authorization
    pre_authorization_number VARCHAR(100),
    pre_authorization_date DATE,

    -- Coordination of Benefits (COB)
    requires_cob BOOLEAN DEFAULT false,
    primary_claim_number VARCHAR(50),
    primary_insurance_company VARCHAR(200),
    primary_insurance_paid DECIMAL(15, 2),

    -- Reviewer information
    reviewer_name VARCHAR(200),
    reviewer_phone VARCHAR(50),
    reviewer_email VARCHAR(100),

    -- Notes
    notes TEXT,
    internal_notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    -- Soft delete
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),

    CONSTRAINT fk_claim_insurance_company FOREIGN KEY (insurance_company_id)
        REFERENCES billing_schema.insurance_company(id),
    CONSTRAINT fk_claim_invoice FOREIGN KEY (invoice_id)
        REFERENCES billing_schema.invoice(id),
    CONSTRAINT chk_service_dates CHECK (service_end_date >= service_start_date),
    CONSTRAINT chk_claim_amount CHECK (claim_amount >= 0),
    CONSTRAINT chk_approved_amount CHECK (approved_amount IS NULL OR approved_amount >= 0),
    CONSTRAINT chk_paid_amount CHECK (paid_amount IS NULL OR paid_amount >= 0)
);

-- Create indexes for insurance_claim
CREATE INDEX idx_claim_number ON billing_schema.insurance_claim(claim_number);
CREATE INDEX idx_claim_patient ON billing_schema.insurance_claim(patient_id);
CREATE INDEX idx_claim_insurance ON billing_schema.insurance_claim(insurance_company_id);
CREATE INDEX idx_claim_invoice ON billing_schema.insurance_claim(invoice_id);
CREATE INDEX idx_claim_status ON billing_schema.insurance_claim(status);
CREATE INDEX idx_claim_submission_date ON billing_schema.insurance_claim(submission_date);
CREATE INDEX idx_claim_policy_number ON billing_schema.insurance_claim(policy_number);

-- Create trigger for insurance_claim updated_at
CREATE TRIGGER update_insurance_claim_updated_at
    BEFORE UPDATE ON billing_schema.insurance_claim
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create claim_item table
CREATE TABLE billing_schema.claim_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    service_date DATE NOT NULL,
    invoice_item_id UUID,

    -- Item details
    item_type VARCHAR(50) NOT NULL,
    item_code VARCHAR(50),
    item_description VARCHAR(500) NOT NULL,
    diagnosis_code VARCHAR(20),
    procedure_code VARCHAR(20),

    -- Quantity and pricing
    quantity DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(50),
    unit_price DECIMAL(15, 2) NOT NULL,
    total_price DECIMAL(15, 2) NOT NULL,
    claim_amount DECIMAL(15, 2) NOT NULL,
    approved_amount DECIMAL(15, 2),
    rejection_reason VARCHAR(500),

    -- Provider information
    provider_id UUID,
    provider_name VARCHAR(200),
    department_name VARCHAR(200),

    -- Specialty fields
    tooth_code VARCHAR(20),
    body_site VARCHAR(100),

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_claim_item_claim FOREIGN KEY (claim_id)
        REFERENCES billing_schema.insurance_claim(id) ON DELETE CASCADE,
    CONSTRAINT chk_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_item_total_price CHECK (total_price >= 0),
    CONSTRAINT chk_item_claim_amount CHECK (claim_amount >= 0)
);

-- Create indexes for claim_item
CREATE INDEX idx_claim_item_claim ON billing_schema.claim_item(claim_id);
CREATE INDEX idx_claim_item_service_date ON billing_schema.claim_item(service_date);

-- Create trigger for claim_item updated_at
CREATE TRIGGER update_claim_item_updated_at
    BEFORE UPDATE ON billing_schema.claim_item
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create claim_document table
CREATE TABLE billing_schema.claim_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_name VARCHAR(200) NOT NULL,

    -- File information
    file_name VARCHAR(255),
    file_path VARCHAR(500) NOT NULL,
    file_url VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),

    description VARCHAR(500),
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by VARCHAR(100),

    -- Verification
    verified BOOLEAN DEFAULT false,
    verified_by VARCHAR(100),
    verification_date TIMESTAMP,

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_claim_document_claim FOREIGN KEY (claim_id)
        REFERENCES billing_schema.insurance_claim(id) ON DELETE CASCADE,
    CONSTRAINT chk_file_size CHECK (file_size IS NULL OR file_size >= 0)
);

-- Create indexes for claim_document
CREATE INDEX idx_claim_document_claim ON billing_schema.claim_document(claim_id);
CREATE INDEX idx_claim_document_type ON billing_schema.claim_document(document_type);
CREATE INDEX idx_claim_document_upload_date ON billing_schema.claim_document(upload_date);

-- Create trigger for claim_document updated_at
CREATE TRIGGER update_claim_document_updated_at
    BEFORE UPDATE ON billing_schema.claim_document
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create claim_audit_log table
CREATE TABLE billing_schema.claim_audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_id UUID NOT NULL,
    claim_number VARCHAR(50) NOT NULL,

    -- Action details
    action VARCHAR(50) NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- User information
    performed_by VARCHAR(100) NOT NULL,
    user_role VARCHAR(50),

    -- Change details
    change_description TEXT,
    previous_values TEXT,
    new_values TEXT,

    -- System information
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),

    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0
);

-- Create indexes for claim_audit_log
CREATE INDEX idx_claim_audit_claim ON billing_schema.claim_audit_log(claim_id);
CREATE INDEX idx_claim_audit_timestamp ON billing_schema.claim_audit_log(timestamp);
CREATE INDEX idx_claim_audit_action ON billing_schema.claim_audit_log(action);
CREATE INDEX idx_claim_audit_performed_by ON billing_schema.claim_audit_log(performed_by);

-- Create trigger for claim_audit_log updated_at
CREATE TRIGGER update_claim_audit_log_updated_at
    BEFORE UPDATE ON billing_schema.claim_audit_log
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE billing_schema.insurance_company IS 'Master data for insurance companies with contracts';
COMMENT ON TABLE billing_schema.insurance_claim IS 'Insurance claims for non-BPJS insurance companies';
COMMENT ON TABLE billing_schema.claim_item IS 'Individual line items in insurance claims';
COMMENT ON TABLE billing_schema.claim_document IS 'Supporting documents for insurance claims';
COMMENT ON TABLE billing_schema.claim_audit_log IS 'Audit trail for all claim changes and status transitions';

COMMENT ON COLUMN billing_schema.insurance_company.current_outstanding IS 'Current outstanding amount owed by hospital to insurance company';
COMMENT ON COLUMN billing_schema.insurance_claim.requires_cob IS 'Coordination of Benefits - indicates if patient has multiple insurance coverage';
COMMENT ON COLUMN billing_schema.claim_item.tooth_code IS 'Tooth number/code for dental claims';
COMMENT ON COLUMN billing_schema.claim_item.body_site IS 'Body site/location for surgical claims';
