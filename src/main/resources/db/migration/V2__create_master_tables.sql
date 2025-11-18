-- ============================================================================
-- Flyway Migration V2: Create Master Data Tables
-- Description: Creates reference/lookup tables with Indonesian-specific data
-- Author: HMS Development Team
-- Date: 2025-01-18
-- ============================================================================

-- ============================================================================
-- SECTION 1: Indonesian Geographic Data
-- ============================================================================

-- Provinces (Provinsi)
CREATE TABLE IF NOT EXISTS master_schema.province (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(2) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_province_code ON master_schema.province(code);
CREATE INDEX idx_province_name ON master_schema.province(name);

COMMENT ON TABLE master_schema.province IS 'Indonesian provinces (Provinsi)';
COMMENT ON COLUMN master_schema.province.code IS '2-digit province code';

-- Cities/Regencies (Kota/Kabupaten)
CREATE TABLE IF NOT EXISTS master_schema.city (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    province_id UUID NOT NULL REFERENCES master_schema.province(id),
    code VARCHAR(4) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- KOTA or KABUPATEN
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_city_province ON master_schema.city(province_id);
CREATE INDEX idx_city_code ON master_schema.city(code);
CREATE INDEX idx_city_name ON master_schema.city(name);

COMMENT ON TABLE master_schema.city IS 'Indonesian cities and regencies (Kota/Kabupaten)';

-- Districts (Kecamatan)
CREATE TABLE IF NOT EXISTS master_schema.district (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    city_id UUID NOT NULL REFERENCES master_schema.city(id),
    code VARCHAR(7) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_district_city ON master_schema.district(city_id);
CREATE INDEX idx_district_code ON master_schema.district(code);

COMMENT ON TABLE master_schema.district IS 'Indonesian districts (Kecamatan)';

-- Villages (Kelurahan/Desa)
CREATE TABLE IF NOT EXISTS master_schema.village (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    district_id UUID NOT NULL REFERENCES master_schema.district(id),
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_village_district ON master_schema.village(district_id);
CREATE INDEX idx_village_code ON master_schema.village(code);
CREATE INDEX idx_village_postal ON master_schema.village(postal_code);

COMMENT ON TABLE master_schema.village IS 'Indonesian villages (Kelurahan/Desa)';

-- ============================================================================
-- SECTION 2: Medical Classification Systems
-- ============================================================================

-- ICD-10 Diagnosis Codes
CREATE TABLE IF NOT EXISTS master_schema.icd10 (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(10) NOT NULL UNIQUE,
    description_en TEXT NOT NULL,
    description_id TEXT,
    category VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_icd10_code ON master_schema.icd10(code);
CREATE INDEX idx_icd10_desc_en ON master_schema.icd10 USING gin(to_tsvector('english', description_en));
CREATE INDEX idx_icd10_desc_id ON master_schema.icd10 USING gin(to_tsvector('indonesian', description_id));
CREATE INDEX idx_icd10_active ON master_schema.icd10(is_active);

COMMENT ON TABLE master_schema.icd10 IS 'ICD-10 diagnosis codes (WHO International Classification of Diseases)';

-- ICD-9-CM Procedure Codes
CREATE TABLE IF NOT EXISTS master_schema.icd9cm (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(10) NOT NULL UNIQUE,
    description_en TEXT NOT NULL,
    description_id TEXT,
    category VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_icd9cm_code ON master_schema.icd9cm(code);
CREATE INDEX idx_icd9cm_desc ON master_schema.icd9cm USING gin(to_tsvector('english', description_en));
CREATE INDEX idx_icd9cm_active ON master_schema.icd9cm(is_active);

COMMENT ON TABLE master_schema.icd9cm IS 'ICD-9-CM procedure codes';

-- ============================================================================
-- SECTION 3: Indonesian-Specific Reference Data
-- ============================================================================

-- Religions (Agama)
CREATE TABLE IF NOT EXISTS master_schema.religion (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_religion_code ON master_schema.religion(code);

COMMENT ON TABLE master_schema.religion IS 'Indonesian religions (required by law for patient registration)';

-- Insert Indonesian religions
INSERT INTO master_schema.religion (code, name) VALUES
    ('ISLAM', 'Islam'),
    ('KRISTEN', 'Kristen Protestan'),
    ('KATOLIK', 'Katolik'),
    ('HINDU', 'Hindu'),
    ('BUDDHA', 'Buddha'),
    ('KONGHUCU', 'Konghucu'),
    ('KEPERCAYAAN', 'Kepercayaan')
ON CONFLICT (code) DO NOTHING;

-- Marital Status (Status Perkawinan)
CREATE TABLE IF NOT EXISTS master_schema.marital_status (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO master_schema.marital_status (code, name) VALUES
    ('SINGLE', 'Belum Kawin'),
    ('MARRIED', 'Kawin'),
    ('DIVORCED', 'Cerai Hidup'),
    ('WIDOWED', 'Cerai Mati')
ON CONFLICT (code) DO NOTHING;

COMMENT ON TABLE master_schema.marital_status IS 'Marital status options';

-- Blood Types (Golongan Darah)
CREATE TABLE IF NOT EXISTS master_schema.blood_type (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO master_schema.blood_type (code, name) VALUES
    ('A+', 'A RhD Positif'),
    ('A-', 'A RhD Negatif'),
    ('B+', 'B RhD Positif'),
    ('B-', 'B RhD Negatif'),
    ('AB+', 'AB RhD Positif'),
    ('AB-', 'AB RhD Negatif'),
    ('O+', 'O RhD Positif'),
    ('O-', 'O RhD Negatif'),
    ('UNKNOWN', 'Tidak Diketahui')
ON CONFLICT (code) DO NOTHING;

COMMENT ON TABLE master_schema.blood_type IS 'Blood type classifications';

-- Education Levels (Pendidikan)
CREATE TABLE IF NOT EXISTS master_schema.education_level (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO master_schema.education_level (code, name) VALUES
    ('NONE', 'Tidak Sekolah'),
    ('SD', 'SD/Sederajat'),
    ('SMP', 'SMP/Sederajat'),
    ('SMA', 'SMA/Sederajat'),
    ('D1', 'Diploma 1'),
    ('D2', 'Diploma 2'),
    ('D3', 'Diploma 3'),
    ('D4', 'Diploma 4'),
    ('S1', 'Sarjana (S1)'),
    ('S2', 'Magister (S2)'),
    ('S3', 'Doktor (S3)')
ON CONFLICT (code) DO NOTHING;

COMMENT ON TABLE master_schema.education_level IS 'Education level classifications';

-- Occupations (Pekerjaan)
CREATE TABLE IF NOT EXISTS master_schema.occupation (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO master_schema.occupation (code, name) VALUES
    ('NOT_WORKING', 'Tidak Bekerja'),
    ('STUDENT', 'Pelajar/Mahasiswa'),
    ('CIVIL_SERVANT', 'PNS'),
    ('MILITARY', 'TNI/Polri'),
    ('PRIVATE_EMPLOYEE', 'Pegawai Swasta'),
    ('ENTREPRENEUR', 'Wiraswasta'),
    ('FARMER', 'Petani'),
    ('FISHERMAN', 'Nelayan'),
    ('LABORER', 'Buruh'),
    ('HOUSEWIFE', 'Ibu Rumah Tangga'),
    ('RETIRED', 'Pensiunan'),
    ('OTHER', 'Lainnya')
ON CONFLICT (code) DO NOTHING;

COMMENT ON TABLE master_schema.occupation IS 'Occupation classifications';

-- ============================================================================
-- SECTION 4: Hospital Organization Structure
-- ============================================================================

-- Departments/Units (Instalasi/Unit)
CREATE TABLE IF NOT EXISTS master_schema.department (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50), -- CLINICAL, SUPPORT, ADMINISTRATIVE
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_department_code ON master_schema.department(code);
CREATE INDEX idx_department_active ON master_schema.department(is_active);

COMMENT ON TABLE master_schema.department IS 'Hospital departments and units';

-- Polyclinics/Specializations (Poliklinik)
CREATE TABLE IF NOT EXISTS master_schema.polyclinic (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    department_id UUID REFERENCES master_schema.department(id),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    schedule_info TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_polyclinic_dept ON master_schema.polyclinic(department_id);
CREATE INDEX idx_polyclinic_code ON master_schema.polyclinic(code);
CREATE INDEX idx_polyclinic_active ON master_schema.polyclinic(is_active);

COMMENT ON TABLE master_schema.polyclinic IS 'Outpatient polyclinics/specializations';

-- ============================================================================
-- SECTION 5: BPJS Reference Data
-- ============================================================================

-- BPJS Provider Types (Jenis Faskes)
CREATE TABLE IF NOT EXISTS master_schema.bpjs_provider_type (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    level INTEGER, -- 1, 2, 3
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO master_schema.bpjs_provider_type (code, name, level) VALUES
    ('FASKES_1', 'Faskes Tingkat 1 (Puskesmas, Klinik, Dokter Praktek)', 1),
    ('FASKES_2', 'Faskes Tingkat 2 (RS Tipe D, C)', 2),
    ('FASKES_3', 'Faskes Tingkat 3 (RS Tipe B, A)', 3)
ON CONFLICT (code) DO NOTHING;

COMMENT ON TABLE master_schema.bpjs_provider_type IS 'BPJS healthcare provider types and levels';

-- BPJS Claim Types
CREATE TABLE IF NOT EXISTS master_schema.bpjs_claim_type (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO master_schema.bpjs_claim_type (code, name, description) VALUES
    ('RI_1', 'Rawat Inap Tingkat 1', 'Inpatient claim level 1'),
    ('RI_2', 'Rawat Inap Tingkat 2', 'Inpatient claim level 2'),
    ('RJ', 'Rawat Jalan', 'Outpatient claim'),
    ('ODC', 'One Day Care', 'Same-day procedure claim')
ON CONFLICT (code) DO NOTHING;

COMMENT ON TABLE master_schema.bpjs_claim_type IS 'BPJS claim type classifications';

-- ============================================================================
-- SECTION 6: Add Triggers for updated_at
-- ============================================================================

CREATE TRIGGER update_province_timestamp
    BEFORE UPDATE ON master_schema.province
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_city_timestamp
    BEFORE UPDATE ON master_schema.city
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_district_timestamp
    BEFORE UPDATE ON master_schema.district
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_village_timestamp
    BEFORE UPDATE ON master_schema.village
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_icd10_timestamp
    BEFORE UPDATE ON master_schema.icd10
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_icd9cm_timestamp
    BEFORE UPDATE ON master_schema.icd9cm
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_department_timestamp
    BEFORE UPDATE ON master_schema.department
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_polyclinic_timestamp
    BEFORE UPDATE ON master_schema.polyclinic
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================================
-- Summary
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Flyway Migration V2 Completed Successfully!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Master tables created:';
    RAISE NOTICE '  - Geographic data (province, city, district, village)';
    RAISE NOTICE '  - Medical classifications (ICD-10, ICD-9-CM)';
    RAISE NOTICE '  - Indonesian reference data (religion, marital status, blood type, education, occupation)';
    RAISE NOTICE '  - Hospital organization (department, polyclinic)';
    RAISE NOTICE '  - BPJS reference data';
    RAISE NOTICE '============================================';
END $$;