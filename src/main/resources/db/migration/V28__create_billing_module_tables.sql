-- ============================================================================
-- Flyway Migration V28: Create Billing Module Tables
-- Description: Creates tables for Phase 8.1 Billing Structure
-- Author: HMS Development Team
-- Date: 2025-01-21
-- ============================================================================

-- ============================================================================
-- SECTION 1: Tariff Category Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_schema.tariff_category (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Category identification
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),

    -- Categorization
    tariff_type VARCHAR(50) NOT NULL, -- ROOM, DOCTOR_FEE, PROCEDURE, etc.
    parent_id UUID REFERENCES billing_schema.tariff_category(id),

    -- Display settings
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Notes
    notes VARCHAR(1000),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- Indexes
CREATE UNIQUE INDEX idx_tariff_cat_code ON billing_schema.tariff_category(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_tariff_cat_type ON billing_schema.tariff_category(tariff_type);
CREATE INDEX idx_tariff_cat_parent ON billing_schema.tariff_category(parent_id);
CREATE INDEX idx_tariff_cat_active ON billing_schema.tariff_category(is_active);

COMMENT ON TABLE billing_schema.tariff_category IS 'Tariff categories for organizing service pricing';

-- ============================================================================
-- SECTION 2: Tariff Table (Master Pricing)
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_schema.tariff (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Basic information
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(300) NOT NULL,
    description VARCHAR(1000),
    tariff_type VARCHAR(50) NOT NULL,
    category_id UUID REFERENCES billing_schema.tariff_category(id),

    -- Pricing (multiple tiers)
    base_price NUMERIC(15,2) NOT NULL CHECK (base_price >= 0),
    bpjs_class1_price NUMERIC(15,2) CHECK (bpjs_class1_price >= 0),
    bpjs_class2_price NUMERIC(15,2) CHECK (bpjs_class2_price >= 0),
    bpjs_class3_price NUMERIC(15,2) CHECK (bpjs_class3_price >= 0),
    insurance_price NUMERIC(15,2) CHECK (insurance_price >= 0),
    company_price NUMERIC(15,2) CHECK (company_price >= 0),

    -- Indonesian specific
    ina_cbgs_code VARCHAR(50),
    icd10_code VARCHAR(20),
    icd9_code VARCHAR(20),
    is_pph23_applicable BOOLEAN DEFAULT false,
    pph23_percentage NUMERIC(5,2),

    -- Billing configuration
    unit VARCHAR(50), -- per day, per procedure, per test, per item
    min_quantity INTEGER DEFAULT 1,
    max_quantity INTEGER,
    allow_discount BOOLEAN DEFAULT true,
    max_discount_percentage NUMERIC(5,2),

    -- Validity
    effective_date DATE,
    expiry_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Reference
    external_code VARCHAR(100),
    notes VARCHAR(1000),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- Indexes
CREATE UNIQUE INDEX idx_tariff_code ON billing_schema.tariff(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_tariff_type ON billing_schema.tariff(tariff_type);
CREATE INDEX idx_tariff_category ON billing_schema.tariff(category_id);
CREATE INDEX idx_tariff_active ON billing_schema.tariff(is_active);
CREATE INDEX idx_tariff_name ON billing_schema.tariff(name);
CREATE INDEX idx_tariff_name_trgm ON billing_schema.tariff USING gin(name gin_trgm_ops);

COMMENT ON TABLE billing_schema.tariff IS 'Master pricing for all hospital services';

-- ============================================================================
-- SECTION 3: Package Deal Tables
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_schema.package_deal (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Package identification
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(300) NOT NULL,
    description VARCHAR(2000),

    -- Pricing
    regular_price NUMERIC(15,2) NOT NULL CHECK (regular_price >= 0),
    package_price NUMERIC(15,2) NOT NULL CHECK (package_price >= 0),
    discount_amount NUMERIC(15,2),
    discount_percentage NUMERIC(5,2),

    -- Validity
    effective_date DATE,
    expiry_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Configuration
    applicable_payment_types VARCHAR(200), -- comma-separated
    terms_conditions VARCHAR(2000),
    notes VARCHAR(1000),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

CREATE UNIQUE INDEX idx_package_code ON billing_schema.package_deal(code) WHERE deleted_at IS NULL;
CREATE INDEX idx_package_active ON billing_schema.package_deal(is_active);
CREATE INDEX idx_package_name ON billing_schema.package_deal(name);

COMMENT ON TABLE billing_schema.package_deal IS 'Bundled service packages at discounted rates';

-- Package deal items
CREATE TABLE IF NOT EXISTS billing_schema.package_deal_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- References
    package_id UUID NOT NULL REFERENCES billing_schema.package_deal(id) ON DELETE CASCADE,
    tariff_id UUID NOT NULL REFERENCES billing_schema.tariff(id),

    -- Quantity and pricing
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity >= 1),
    unit_price NUMERIC(15,2) NOT NULL CHECK (unit_price >= 0),
    total_price NUMERIC(15,2) NOT NULL CHECK (total_price >= 0),

    -- Configuration
    is_optional BOOLEAN DEFAULT false,
    notes VARCHAR(500),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_pkg_item_package ON billing_schema.package_deal_item(package_id);
CREATE INDEX idx_pkg_item_tariff ON billing_schema.package_deal_item(tariff_id);

COMMENT ON TABLE billing_schema.package_deal_item IS 'Items included in package deals';

-- ============================================================================
-- SECTION 4: Invoice Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_schema.invoice (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Invoice identification
    invoice_number VARCHAR(50) NOT NULL UNIQUE,

    -- Patient information
    patient_id UUID NOT NULL,
    patient_mrn VARCHAR(50),
    patient_name VARCHAR(200),

    -- Encounter information
    encounter_id UUID,
    encounter_type VARCHAR(50), -- OUTPATIENT, INPATIENT, EMERGENCY

    -- Dates
    invoice_date DATE NOT NULL,
    due_date DATE,
    service_period_start DATE,
    service_period_end DATE,

    -- Status
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',

    -- Amounts
    subtotal NUMERIC(15,2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
    discount_amount NUMERIC(15,2) DEFAULT 0 CHECK (discount_amount >= 0),
    discount_percentage NUMERIC(5,2),
    discount_reason VARCHAR(500),
    tax_amount NUMERIC(15,2) DEFAULT 0 CHECK (tax_amount >= 0),
    tax_percentage NUMERIC(5,2),
    total NUMERIC(15,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    deposit_deduction NUMERIC(15,2) DEFAULT 0 CHECK (deposit_deduction >= 0),
    paid_amount NUMERIC(15,2) DEFAULT 0 CHECK (paid_amount >= 0),
    outstanding_balance NUMERIC(15,2) DEFAULT 0,

    -- Payment information
    payment_type VARCHAR(50), -- CASH, BPJS, INSURANCE, COMPANY
    insurance_company_id UUID,
    insurance_claim_number VARCHAR(100),
    bpjs_sep_number VARCHAR(100),
    company_id UUID,

    -- Void/Correction
    is_voided BOOLEAN DEFAULT false,
    void_reason VARCHAR(500),
    voided_date TIMESTAMP,
    voided_by VARCHAR(100),
    replacement_invoice_id UUID,
    original_invoice_id UUID,

    -- Additional information
    payment_terms VARCHAR(500),
    notes VARCHAR(2000),

    -- Printing
    is_printed BOOLEAN DEFAULT false,
    print_count INTEGER DEFAULT 0,
    last_printed_date TIMESTAMP,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- Indexes
CREATE UNIQUE INDEX idx_invoice_number ON billing_schema.invoice(invoice_number) WHERE deleted_at IS NULL;
CREATE INDEX idx_invoice_patient ON billing_schema.invoice(patient_id);
CREATE INDEX idx_invoice_encounter ON billing_schema.invoice(encounter_id);
CREATE INDEX idx_invoice_status ON billing_schema.invoice(status);
CREATE INDEX idx_invoice_date ON billing_schema.invoice(invoice_date);
CREATE INDEX idx_invoice_due_date ON billing_schema.invoice(due_date);
CREATE INDEX idx_invoice_patient_mrn ON billing_schema.invoice(patient_mrn);

COMMENT ON TABLE billing_schema.invoice IS 'Patient billing invoices';

-- ============================================================================
-- SECTION 5: Invoice Item Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_schema.invoice_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Invoice reference
    invoice_id UUID NOT NULL REFERENCES billing_schema.invoice(id) ON DELETE CASCADE,

    -- Line information
    line_number INTEGER NOT NULL CHECK (line_number >= 1),
    service_date DATE,

    -- Tariff reference
    tariff_id UUID REFERENCES billing_schema.tariff(id),
    item_type VARCHAR(50), -- ROOM, DOCTOR_FEE, PROCEDURE, etc.

    -- Item details
    item_code VARCHAR(100),
    item_name VARCHAR(500) NOT NULL,
    item_description VARCHAR(1000),

    -- Quantity and pricing
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity >= 1),
    unit VARCHAR(50),
    unit_price NUMERIC(15,2) NOT NULL CHECK (unit_price >= 0),
    total_price NUMERIC(15,2) NOT NULL CHECK (total_price >= 0),
    discount_amount NUMERIC(15,2) DEFAULT 0 CHECK (discount_amount >= 0),
    discount_percentage NUMERIC(5,2),
    net_amount NUMERIC(15,2),
    tax_amount NUMERIC(15,2) DEFAULT 0 CHECK (tax_amount >= 0),

    -- Department and practitioner
    department_id UUID,
    department_name VARCHAR(200),
    practitioner_id UUID,
    practitioner_name VARCHAR(200),

    -- Source reference
    source_reference_id UUID,
    source_reference_type VARCHAR(50), -- ORDER, PRESCRIPTION, PROCEDURE, etc.

    -- Package reference
    package_deal_id UUID REFERENCES billing_schema.package_deal(id),

    -- Coverage
    is_covered BOOLEAN DEFAULT false,
    coverage_percentage NUMERIC(5,2),
    covered_amount NUMERIC(15,2),
    patient_responsibility NUMERIC(15,2),

    -- Notes
    notes VARCHAR(1000),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- Indexes
CREATE INDEX idx_invoice_item_invoice ON billing_schema.invoice_item(invoice_id);
CREATE INDEX idx_invoice_item_tariff ON billing_schema.invoice_item(tariff_id);
CREATE INDEX idx_invoice_item_date ON billing_schema.invoice_item(service_date);
CREATE INDEX idx_invoice_item_type ON billing_schema.invoice_item(item_type);

COMMENT ON TABLE billing_schema.invoice_item IS 'Individual line items in invoices';

-- ============================================================================
-- SECTION 6: Payment Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS billing_schema.payment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Payment identification
    payment_number VARCHAR(50) NOT NULL UNIQUE,

    -- Invoice and patient reference
    invoice_id UUID NOT NULL REFERENCES billing_schema.invoice(id),
    patient_id UUID NOT NULL,
    patient_mrn VARCHAR(50),
    patient_name VARCHAR(200),

    -- Payment details
    payment_date TIMESTAMP NOT NULL,
    payment_method VARCHAR(50) NOT NULL, -- CASH, DEBIT, CREDIT, QRIS, etc.
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    -- Payment gateway information
    gateway_transaction_id VARCHAR(200),
    gateway_name VARCHAR(100),
    gateway_response VARCHAR(2000),
    authorization_code VARCHAR(100),

    -- Card/Bank information
    card_last4 VARCHAR(4),
    card_type VARCHAR(50),
    bank_name VARCHAR(100),
    account_last4 VARCHAR(4),

    -- Cash payment
    cash_tendered NUMERIC(15,2),
    change_amount NUMERIC(15,2),

    -- Cashier information
    cashier_id UUID,
    cashier_name VARCHAR(200),
    cash_register_id UUID,
    shift_id UUID,

    -- Receipt
    receipt_number VARCHAR(50),
    is_receipt_printed BOOLEAN DEFAULT false,
    receipt_print_count INTEGER DEFAULT 0,
    last_receipt_print_date TIMESTAMP,

    -- Refund
    is_refunded BOOLEAN DEFAULT false,
    refund_amount NUMERIC(15,2),
    refund_date TIMESTAMP,
    refund_reason VARCHAR(500),
    refund_processed_by VARCHAR(100),
    original_payment_id UUID REFERENCES billing_schema.payment(id),

    -- Additional information
    reference_number VARCHAR(200),
    notes VARCHAR(1000),
    confirmed_date TIMESTAMP,
    confirmed_by VARCHAR(100),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- Indexes
CREATE UNIQUE INDEX idx_payment_number ON billing_schema.payment(payment_number) WHERE deleted_at IS NULL;
CREATE INDEX idx_payment_invoice ON billing_schema.payment(invoice_id);
CREATE INDEX idx_payment_patient ON billing_schema.payment(patient_id);
CREATE INDEX idx_payment_date ON billing_schema.payment(payment_date);
CREATE INDEX idx_payment_status ON billing_schema.payment(status);
CREATE INDEX idx_payment_method ON billing_schema.payment(payment_method);
CREATE INDEX idx_payment_cashier ON billing_schema.payment(cashier_id);
CREATE INDEX idx_payment_shift ON billing_schema.payment(shift_id);

COMMENT ON TABLE billing_schema.payment IS 'Payment transactions for invoices';

-- ============================================================================
-- Triggers for updated_at
-- ============================================================================

CREATE TRIGGER update_tariff_category_updated_at BEFORE UPDATE ON billing_schema.tariff_category
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tariff_updated_at BEFORE UPDATE ON billing_schema.tariff
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_package_deal_updated_at BEFORE UPDATE ON billing_schema.package_deal
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_package_deal_item_updated_at BEFORE UPDATE ON billing_schema.package_deal_item
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoice_updated_at BEFORE UPDATE ON billing_schema.invoice
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_invoice_item_updated_at BEFORE UPDATE ON billing_schema.invoice_item
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payment_updated_at BEFORE UPDATE ON billing_schema.payment
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- Summary
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V28 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Created billing tables:';
    RAISE NOTICE '  - tariff_category (service categorization)';
    RAISE NOTICE '  - tariff (master pricing)';
    RAISE NOTICE '  - package_deal (bundled packages)';
    RAISE NOTICE '  - package_deal_item (package items)';
    RAISE NOTICE '  - invoice (patient bills)';
    RAISE NOTICE '  - invoice_item (bill line items)';
    RAISE NOTICE '  - payment (payment transactions)';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Phase 8.1 Billing Structure: COMPLETE';
    RAISE NOTICE '============================================';
END $$;
