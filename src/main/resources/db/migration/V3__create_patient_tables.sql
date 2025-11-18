-- ============================================================================
-- Flyway Migration V3: Create Patient Module Tables
-- Description: Creates tables for patient management
-- Author: HMS Development Team
-- Date: 2025-01-18
-- ============================================================================

-- ============================================================================
-- SECTION 1: Patient Core Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS patient_schema.patient (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Medical Record Number (auto-generated)
    mrn VARCHAR(50) NOT NULL UNIQUE,

    -- Indonesian Identity (NIK - Nomor Induk Kependudukan)
    nik VARCHAR(16) UNIQUE,
    nik_verified BOOLEAN DEFAULT false,
    nik_verification_date TIMESTAMP,

    -- BPJS Information
    bpjs_number VARCHAR(13) UNIQUE,
    bpjs_active BOOLEAN DEFAULT false,
    bpjs_class VARCHAR(10), -- Kelas 1, 2, 3
    bpjs_provider_code VARCHAR(20), -- Faskes Tingkat 1

    -- Personal Information
    title VARCHAR(10), -- Tn., Ny., An., etc.
    full_name VARCHAR(200) NOT NULL,
    birth_place VARCHAR(100),
    birth_date DATE NOT NULL,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),

    -- Indonesian-specific fields
    religion_id UUID REFERENCES master_schema.religion(id),
    marital_status_id UUID REFERENCES master_schema.marital_status(id),
    blood_type_id UUID REFERENCES master_schema.blood_type(id),
    education_id UUID REFERENCES master_schema.education_level(id),
    occupation_id UUID REFERENCES master_schema.occupation(id),

    -- Nationality
    nationality VARCHAR(50) DEFAULT 'Indonesian',
    citizenship VARCHAR(50) DEFAULT 'WNI', -- WNI or WNA

    -- Mother's maiden name (for verification)
    mother_maiden_name VARCHAR(200),

    -- Contact Information
    phone_primary VARCHAR(20),
    phone_secondary VARCHAR(20),
    email VARCHAR(100),

    -- Photo
    photo_url TEXT,

    -- Registration Info
    registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    registration_source VARCHAR(50), -- WALK_IN, ONLINE, MOBILE_JKN, REFERRAL

    -- Status
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_deceased BOOLEAN NOT NULL DEFAULT false,
    deceased_date TIMESTAMP,
    deceased_reason TEXT,

    -- VIP/Special Status
    is_vip BOOLEAN DEFAULT false,
    vip_notes TEXT,

    -- Notes
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- Indexes for patient table
CREATE UNIQUE INDEX idx_patient_mrn ON patient_schema.patient(mrn) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX idx_patient_nik ON patient_schema.patient(nik) WHERE nik IS NOT NULL AND deleted_at IS NULL;
CREATE UNIQUE INDEX idx_patient_bpjs ON patient_schema.patient(bpjs_number) WHERE bpjs_number IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_patient_name ON patient_schema.patient(full_name);
CREATE INDEX idx_patient_birth_date ON patient_schema.patient(birth_date);
CREATE INDEX idx_patient_gender ON patient_schema.patient(gender);
CREATE INDEX idx_patient_active ON patient_schema.patient(is_active);
CREATE INDEX idx_patient_deceased ON patient_schema.patient(is_deceased);
CREATE INDEX idx_patient_created ON patient_schema.patient(created_at);

-- Full-text search index for patient name
CREATE INDEX idx_patient_name_trgm ON patient_schema.patient USING gin(full_name gin_trgm_ops);

COMMENT ON TABLE patient_schema.patient IS 'Core patient demographic information';
COMMENT ON COLUMN patient_schema.patient.mrn IS 'Medical Record Number (unique patient identifier)';
COMMENT ON COLUMN patient_schema.patient.nik IS 'NIK - Indonesian National ID (16 digits)';
COMMENT ON COLUMN patient_schema.patient.bpjs_number IS 'BPJS card number (13 digits)';

-- ============================================================================
-- SECTION 2: Patient Address (KTP vs Domicile)
-- ============================================================================

CREATE TABLE IF NOT EXISTS patient_schema.patient_address (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patient_schema.patient(id) ON DELETE CASCADE,

    address_type VARCHAR(20) NOT NULL, -- KTP or DOMICILE

    -- Full address
    address_line1 TEXT NOT NULL,
    address_line2 TEXT,

    -- Indonesian administrative divisions
    village_id UUID REFERENCES master_schema.village(id),
    district_id UUID REFERENCES master_schema.district(id),
    city_id UUID REFERENCES master_schema.city(id),
    province_id UUID REFERENCES master_schema.province(id),

    postal_code VARCHAR(5),

    -- RT/RW (Indonesian neighborhood/hamlet)
    rt VARCHAR(3),
    rw VARCHAR(3),

    -- Is this the primary address?
    is_primary BOOLEAN DEFAULT false,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_patient_address_patient ON patient_schema.patient_address(patient_id);
CREATE INDEX idx_patient_address_type ON patient_schema.patient_address(address_type);
CREATE INDEX idx_patient_address_primary ON patient_schema.patient_address(is_primary);

COMMENT ON TABLE patient_schema.patient_address IS 'Patient addresses (KTP and Domicile)';
COMMENT ON COLUMN patient_schema.patient_address.address_type IS 'KTP (ID card address) or DOMICILE (current residence)';

-- ============================================================================
-- SECTION 3: Emergency Contacts
-- ============================================================================

CREATE TABLE IF NOT EXISTS patient_schema.emergency_contact (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patient_schema.patient(id) ON DELETE CASCADE,

    -- Contact information
    full_name VARCHAR(200) NOT NULL,
    relationship VARCHAR(50) NOT NULL, -- PARENT, SPOUSE, SIBLING, CHILD, FRIEND, etc.

    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(100),

    address TEXT,

    -- Priority (1 = primary contact)
    priority INTEGER DEFAULT 1,

    -- Notes
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_emergency_contact_patient ON patient_schema.emergency_contact(patient_id);
CREATE INDEX idx_emergency_contact_priority ON patient_schema.emergency_contact(priority);

COMMENT ON TABLE patient_schema.emergency_contact IS 'Patient emergency contact information';

-- ============================================================================
-- SECTION 4: Patient Allergies
-- ============================================================================

CREATE TABLE IF NOT EXISTS patient_schema.patient_allergy (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patient_schema.patient(id) ON DELETE CASCADE,

    allergen_type VARCHAR(50) NOT NULL, -- DRUG, FOOD, ENVIRONMENTAL, OTHER
    allergen_name VARCHAR(200) NOT NULL,

    reaction TEXT, -- Description of allergic reaction
    severity VARCHAR(20), -- MILD, MODERATE, SEVERE, LIFE_THREATENING

    verified_by VARCHAR(100), -- Doctor/nurse who verified
    verified_date TIMESTAMP,

    notes TEXT,

    is_active BOOLEAN DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_patient_allergy_patient ON patient_schema.patient_allergy(patient_id);
CREATE INDEX idx_patient_allergy_type ON patient_schema.patient_allergy(allergen_type);
CREATE INDEX idx_patient_allergy_active ON patient_schema.patient_allergy(is_active);

COMMENT ON TABLE patient_schema.patient_allergy IS 'Patient allergy information';

-- ============================================================================
-- SECTION 5: Medical Record Number Sequence
-- ============================================================================

-- Create sequence for MRN generation
CREATE SEQUENCE IF NOT EXISTS patient_schema.mrn_sequence START WITH 1;

COMMENT ON SEQUENCE patient_schema.mrn_sequence IS 'Sequence for generating Medical Record Numbers';

-- ============================================================================
-- SECTION 6: Add Triggers for updated_at
-- ============================================================================

CREATE TRIGGER update_patient_timestamp
    BEFORE UPDATE ON patient_schema.patient
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_patient_address_timestamp
    BEFORE UPDATE ON patient_schema.patient_address
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_emergency_contact_timestamp
    BEFORE UPDATE ON patient_schema.emergency_contact
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_patient_allergy_timestamp
    BEFORE UPDATE ON patient_schema.patient_allergy
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================================
-- Summary
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V3 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Patient module tables created:';
    RAISE NOTICE '  - patient (core demographic data)';
    RAISE NOTICE '  - patient_address (KTP and domicile)';
    RAISE NOTICE '  - emergency_contact';
    RAISE NOTICE '  - patient_allergy';
    RAISE NOTICE '  - mrn_sequence (for MRN generation)';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Indonesian-specific fields:';
    RAISE NOTICE '  - NIK (16-digit national ID)';
    RAISE NOTICE '  - BPJS number and class';
    RAISE NOTICE '  - Religion (required)';
    RAISE NOTICE '  - RT/RW (neighborhood identifiers)';
    RAISE NOTICE '  - KTP vs Domicile addresses';
    RAISE NOTICE '============================================';
END $$;