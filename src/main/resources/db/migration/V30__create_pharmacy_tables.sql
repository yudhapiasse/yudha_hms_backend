-- V30: Create Pharmacy Tables
-- Description: Create pharmacy schema with drug master data, categories, suppliers, and interactions
-- Author: HMS Development Team
-- Date: 2025-01-21

-- Create pharmacy schema if not exists
CREATE SCHEMA IF NOT EXISTS pharmacy_schema;

-- =====================================================
-- Table: drug_category
-- Description: Drug categorization (hierarchical)
-- =====================================================
CREATE TABLE pharmacy_schema.drug_category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    parent_id UUID,
    level INTEGER NOT NULL DEFAULT 0,
    display_order INTEGER,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_drug_category_parent FOREIGN KEY (parent_id)
        REFERENCES pharmacy_schema.drug_category(id)
);

-- Indexes for drug_category
CREATE UNIQUE INDEX idx_drug_category_code ON pharmacy_schema.drug_category(code);
CREATE INDEX idx_drug_category_name ON pharmacy_schema.drug_category(name);
CREATE INDEX idx_drug_category_parent ON pharmacy_schema.drug_category(parent_id);
CREATE INDEX idx_drug_category_active ON pharmacy_schema.drug_category(active);
CREATE INDEX idx_drug_category_level ON pharmacy_schema.drug_category(level);

-- Trigger for updated_at
CREATE TRIGGER update_drug_category_updated_at
    BEFORE UPDATE ON pharmacy_schema.drug_category
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: supplier
-- Description: Pharmaceutical suppliers/vendors
-- =====================================================
CREATE TABLE pharmacy_schema.supplier (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    legal_name VARCHAR(200),
    tax_id VARCHAR(50),
    license_number VARCHAR(100),
    contact_person VARCHAR(200),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(100),
    contact_fax VARCHAR(50),
    address VARCHAR(500),
    city VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(20),
    website VARCHAR(200),
    payment_terms_days INTEGER,
    delivery_lead_time_days INTEGER,
    minimum_order_value DECIMAL(15, 2),
    bank_name VARCHAR(100),
    bank_account_number VARCHAR(50),
    bank_account_holder VARCHAR(200),
    rating DECIMAL(3, 2),
    is_preferred BOOLEAN DEFAULT false,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_supplier_rating CHECK (rating IS NULL OR (rating >= 1.0 AND rating <= 5.0)),
    CONSTRAINT chk_supplier_payment_terms CHECK (payment_terms_days IS NULL OR payment_terms_days >= 0),
    CONSTRAINT chk_supplier_delivery_lead_time CHECK (delivery_lead_time_days IS NULL OR delivery_lead_time_days >= 0),
    CONSTRAINT chk_supplier_minimum_order CHECK (minimum_order_value IS NULL OR minimum_order_value >= 0)
);

-- Indexes for supplier
CREATE UNIQUE INDEX idx_supplier_code ON pharmacy_schema.supplier(code);
CREATE INDEX idx_supplier_name ON pharmacy_schema.supplier(name);
CREATE INDEX idx_supplier_active ON pharmacy_schema.supplier(active);
CREATE INDEX idx_supplier_preferred ON pharmacy_schema.supplier(is_preferred);

-- Trigger for updated_at
CREATE TRIGGER update_supplier_updated_at
    BEFORE UPDATE ON pharmacy_schema.supplier
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: drug
-- Description: Drug master data
-- =====================================================
CREATE TABLE pharmacy_schema.drug (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    drug_code VARCHAR(50) NOT NULL UNIQUE,
    generic_name VARCHAR(200) NOT NULL,
    brand_name VARCHAR(200),
    category_id UUID NOT NULL,
    strength VARCHAR(100),
    unit VARCHAR(50) NOT NULL,
    dosage_form VARCHAR(200),
    route_of_administration VARCHAR(100),
    manufacturer VARCHAR(200),
    country_of_origin VARCHAR(100),

    -- Identification codes
    barcode VARCHAR(100),
    national_drug_code VARCHAR(50),
    atc_code VARCHAR(20),

    -- Storage requirements
    storage_condition VARCHAR(50),
    storage_temperature VARCHAR(100),
    storage_instructions TEXT,

    -- Formularium and regulatory
    formularium_status VARCHAR(50),
    bpjs_drug_code VARCHAR(50),
    registration_number VARCHAR(100),
    registration_expiry_date DATE,
    shelf_life_days INTEGER,

    -- Safety flags
    is_narcotic BOOLEAN,
    is_psychotropic BOOLEAN,
    is_high_alert BOOLEAN,
    requires_prescription BOOLEAN,

    -- Inventory management
    minimum_stock_level DECIMAL(10, 2),
    maximum_stock_level DECIMAL(10, 2),
    reorder_quantity DECIMAL(10, 2),
    current_stock DECIMAL(10, 2),

    -- Pricing
    unit_price DECIMAL(15, 2),
    bpjs_unit_price DECIMAL(15, 2),

    -- Supplier information
    primary_supplier_id UUID,
    primary_supplier_name VARCHAR(200),

    -- Clinical information
    indications TEXT,
    contraindications TEXT,
    side_effects TEXT,
    dosage_instructions TEXT,
    warnings TEXT,
    package_size VARCHAR(200),

    -- Discontinuation
    is_discontinued BOOLEAN,
    discontinuation_date DATE,
    replacement_drug_id UUID,

    -- Notes
    notes TEXT,

    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_drug_category FOREIGN KEY (category_id)
        REFERENCES pharmacy_schema.drug_category(id),
    CONSTRAINT fk_drug_supplier FOREIGN KEY (primary_supplier_id)
        REFERENCES pharmacy_schema.supplier(id)
);

-- Indexes for drug
CREATE UNIQUE INDEX idx_drug_code ON pharmacy_schema.drug(drug_code);
CREATE INDEX idx_drug_barcode ON pharmacy_schema.drug(barcode) WHERE barcode IS NOT NULL;
CREATE INDEX idx_drug_generic_name ON pharmacy_schema.drug(generic_name);
CREATE INDEX idx_drug_brand_name ON pharmacy_schema.drug(brand_name);
CREATE INDEX idx_drug_category ON pharmacy_schema.drug(category_id);
CREATE INDEX idx_drug_active ON pharmacy_schema.drug(active);
CREATE INDEX idx_drug_formularium ON pharmacy_schema.drug(formularium_status);
CREATE INDEX idx_drug_narcotic ON pharmacy_schema.drug(is_narcotic);
CREATE INDEX idx_drug_psychotropic ON pharmacy_schema.drug(is_psychotropic);
CREATE INDEX idx_drug_high_alert ON pharmacy_schema.drug(is_high_alert);
CREATE INDEX idx_drug_supplier ON pharmacy_schema.drug(primary_supplier_id);

-- Trigger for updated_at
CREATE TRIGGER update_drug_updated_at
    BEFORE UPDATE ON pharmacy_schema.drug
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Table: drug_interaction
-- Description: Drug-drug interactions
-- =====================================================
CREATE TABLE pharmacy_schema.drug_interaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    drug1_id UUID NOT NULL,
    drug2_id UUID NOT NULL,
    severity VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    clinical_effects TEXT,
    management TEXT,
    evidence_level VARCHAR(50),
    reference TEXT,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign keys
    CONSTRAINT fk_drug_interaction_drug1 FOREIGN KEY (drug1_id)
        REFERENCES pharmacy_schema.drug(id),
    CONSTRAINT fk_drug_interaction_drug2 FOREIGN KEY (drug2_id)
        REFERENCES pharmacy_schema.drug(id),

    -- Constraints
    CONSTRAINT chk_drug_interaction_different_drugs CHECK (drug1_id != drug2_id),
    CONSTRAINT chk_drug_interaction_unique UNIQUE (drug1_id, drug2_id)
);

-- Indexes for drug_interaction
CREATE INDEX idx_drug_interaction_drug1 ON pharmacy_schema.drug_interaction(drug1_id);
CREATE INDEX idx_drug_interaction_drug2 ON pharmacy_schema.drug_interaction(drug2_id);
CREATE INDEX idx_drug_interaction_severity ON pharmacy_schema.drug_interaction(severity);
CREATE INDEX idx_drug_interaction_active ON pharmacy_schema.drug_interaction(active);

-- Trigger for updated_at
CREATE TRIGGER update_drug_interaction_updated_at
    BEFORE UPDATE ON pharmacy_schema.drug_interaction
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Comments
-- =====================================================
COMMENT ON TABLE pharmacy_schema.drug_category IS 'Drug categorization with hierarchical structure';
COMMENT ON TABLE pharmacy_schema.supplier IS 'Pharmaceutical suppliers and vendors';
COMMENT ON TABLE pharmacy_schema.drug IS 'Drug master data with comprehensive information';
COMMENT ON TABLE pharmacy_schema.drug_interaction IS 'Drug-drug interaction database';

COMMENT ON COLUMN pharmacy_schema.drug.formularium_status IS 'BPJS formularium approval status: APPROVED, NOT_APPROVED, RESTRICTED, UNDER_REVIEW';
COMMENT ON COLUMN pharmacy_schema.drug.storage_condition IS 'Storage requirements: ROOM_TEMPERATURE, REFRIGERATED, FROZEN, etc.';
COMMENT ON COLUMN pharmacy_schema.drug.unit IS 'Drug unit: TABLET, CAPSULE, AMPULE, VIAL, BOTTLE, etc.';
COMMENT ON COLUMN pharmacy_schema.drug_interaction.severity IS 'Interaction severity: MINOR, MODERATE, MAJOR, CONTRAINDICATED';
