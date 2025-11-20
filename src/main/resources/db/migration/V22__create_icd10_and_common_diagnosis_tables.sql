-- ============================================================================
-- Flyway Migration V22: Create ICD-10 Master Data and Common Diagnosis Tables
-- Description: Phase 4.2 Diagnosis Management - ICD-10 codes with Indonesian
--              translations and department-specific common diagnoses
-- Author: HMS Development Team
-- Date: 2025-01-20
-- ============================================================================

-- Create ICD-10 codes master data table
CREATE TABLE IF NOT EXISTS clinical_schema.icd10_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- ICD-10 Code Structure
    code VARCHAR(10) NOT NULL UNIQUE,
    description_en TEXT NOT NULL,
    description_id TEXT NOT NULL,

    -- ICD-10 Hierarchy
    chapter_code VARCHAR(5),
    chapter_name_en VARCHAR(500),
    chapter_name_id VARCHAR(500),
    category_code VARCHAR(5),
    category_name_en VARCHAR(500),
    category_name_id VARCHAR(500),

    -- Classification
    is_three_character BOOLEAN NOT NULL DEFAULT FALSE,
    is_four_character BOOLEAN NOT NULL DEFAULT FALSE,
    code_type VARCHAR(20) DEFAULT 'DIAGNOSIS',

    -- Usage Tracking
    usage_count BIGINT NOT NULL DEFAULT 0,
    is_common BOOLEAN NOT NULL DEFAULT FALSE,

    -- Insurance and Billing
    is_billable BOOLEAN NOT NULL DEFAULT TRUE,
    requires_additional_info BOOLEAN NOT NULL DEFAULT FALSE,
    insurance_notes TEXT,

    -- Status
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    effective_date DATE,
    deprecated_date DATE,
    replaced_by_code VARCHAR(10),

    -- Search and Display
    search_terms TEXT,
    short_description_id VARCHAR(255),

    -- Notes
    clinical_notes TEXT,
    coding_notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Create indexes for ICD-10 codes
CREATE INDEX idx_icd10_code ON clinical_schema.icd10_codes(code);
CREATE INDEX idx_icd10_category ON clinical_schema.icd10_codes(category_code);
CREATE INDEX idx_icd10_chapter ON clinical_schema.icd10_codes(chapter_code);
CREATE INDEX idx_icd10_active ON clinical_schema.icd10_codes(is_active);
CREATE INDEX idx_icd10_search ON clinical_schema.icd10_codes(code, description_en, description_id);
CREATE INDEX idx_icd10_usage ON clinical_schema.icd10_codes(usage_count DESC);
CREATE INDEX idx_icd10_common ON clinical_schema.icd10_codes(is_common) WHERE is_common = TRUE;

-- Add comments
COMMENT ON TABLE clinical_schema.icd10_codes IS 'ICD-10 diagnosis codes master data with Indonesian translations';
COMMENT ON COLUMN clinical_schema.icd10_codes.code IS 'ICD-10 code (e.g., A00.0, E11.9)';
COMMENT ON COLUMN clinical_schema.icd10_codes.description_en IS 'English description';
COMMENT ON COLUMN clinical_schema.icd10_codes.description_id IS 'Indonesian translation';
COMMENT ON COLUMN clinical_schema.icd10_codes.usage_count IS 'Number of times this code has been used';
COMMENT ON COLUMN clinical_schema.icd10_codes.is_billable IS 'Can be billed to insurance';
COMMENT ON COLUMN clinical_schema.icd10_codes.requires_additional_info IS 'Requires extra documentation for insurance';

-- Create common diagnoses table (top diagnoses per department)
CREATE TABLE IF NOT EXISTS clinical_schema.common_diagnoses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Department/Polyclinic
    department_code VARCHAR(20) NOT NULL,
    department_name VARCHAR(200) NOT NULL,

    -- ICD-10 Code Reference
    icd10_code_id UUID NOT NULL REFERENCES clinical_schema.icd10_codes(id) ON DELETE CASCADE,
    icd10_code VARCHAR(10) NOT NULL,
    icd10_description_id TEXT NOT NULL,

    -- Ranking
    rank_order INTEGER NOT NULL CHECK (rank_order > 0),
    usage_count BIGINT NOT NULL DEFAULT 0,
    usage_percentage DOUBLE PRECISION,

    -- Configuration
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    auto_calculated BOOLEAN NOT NULL DEFAULT TRUE,

    -- Statistics
    last_used_date DATE,
    last_recalculated_date DATE,
    trend VARCHAR(20), -- UP, DOWN, STABLE

    -- Display Configuration
    display_order INTEGER,
    display_color VARCHAR(10),
    display_icon VARCHAR(50),

    -- Notes
    usage_notes TEXT,
    admin_notes TEXT,

    -- Audit fields
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Unique constraint: one diagnosis per department
    CONSTRAINT uk_common_diagnosis_dept_icd10 UNIQUE (department_code, icd10_code_id)
);

-- Create indexes for common diagnoses
CREATE INDEX idx_common_diagnosis_dept ON clinical_schema.common_diagnoses(department_code);
CREATE INDEX idx_common_diagnosis_icd10 ON clinical_schema.common_diagnoses(icd10_code_id);
CREATE INDEX idx_common_diagnosis_rank ON clinical_schema.common_diagnoses(department_code, rank_order);
CREATE INDEX idx_common_diagnosis_active ON clinical_schema.common_diagnoses(is_active);
CREATE INDEX idx_common_diagnosis_usage ON clinical_schema.common_diagnoses(usage_count DESC);

-- Add comments
COMMENT ON TABLE clinical_schema.common_diagnoses IS 'Common diagnoses per department for quick selection';
COMMENT ON COLUMN clinical_schema.common_diagnoses.department_code IS 'Department/polyclinic code (e.g., POLI_UMUM, POLI_GIGI)';
COMMENT ON COLUMN clinical_schema.common_diagnoses.rank_order IS 'Rank within department (1-10 for top 10)';
COMMENT ON COLUMN clinical_schema.common_diagnoses.is_pinned IS 'Manually pinned by admin (not auto-calculated)';
COMMENT ON COLUMN clinical_schema.common_diagnoses.auto_calculated IS 'True if calculated from usage statistics';

-- Update encounter_diagnoses to reference icd10_codes table
-- Note: The diagnosis_id column in encounter_diagnoses should point to icd10_codes
ALTER TABLE clinical_schema.encounter_diagnoses
    ADD CONSTRAINT fk_encounter_diagnoses_icd10
    FOREIGN KEY (diagnosis_id) REFERENCES clinical_schema.icd10_codes(id)
    ON DELETE SET NULL;

-- ============================================================================
-- Insert Sample ICD-10 Codes (Common Diagnoses for Indonesian Hospital)
-- ============================================================================

-- Common General Practice Diagnoses
INSERT INTO clinical_schema.icd10_codes (code, description_en, description_id, chapter_code, chapter_name_en, chapter_name_id, is_billable, is_active) VALUES
-- Respiratory
('J00', 'Acute nasopharyngitis [common cold]', 'Nasofaringitis akut [pilek]', 'X', 'Diseases of the respiratory system', 'Penyakit sistem pernapasan', TRUE, TRUE),
('J06.9', 'Acute upper respiratory infection, unspecified', 'Infeksi saluran pernapasan atas akut, tidak spesifik', 'X', 'Diseases of the respiratory system', 'Penyakit sistem pernapasan', TRUE, TRUE),
('J02.9', 'Acute pharyngitis, unspecified', 'Faringitis akut, tidak spesifik', 'X', 'Diseases of the respiratory system', 'Penyakit sistem pernapasan', TRUE, TRUE),
('J03.9', 'Acute tonsillitis, unspecified', 'Tonsilitis akut, tidak spesifik', 'X', 'Diseases of the respiratory system', 'Penyakit sistem pernapasan', TRUE, TRUE),

-- Gastrointestinal
('K29.7', 'Gastritis, unspecified', 'Gastritis, tidak spesifik', 'XI', 'Diseases of the digestive system', 'Penyakit sistem pencernaan', TRUE, TRUE),
('A09', 'Diarrhoea and gastroenteritis of presumed infectious origin', 'Diare dan gastroenteritis yang diduga berasal dari infeksi', 'I', 'Certain infectious and parasitic diseases', 'Penyakit infeksi dan parasit tertentu', TRUE, TRUE),
('K30', 'Functional dyspepsia', 'Dispepsia fungsional', 'XI', 'Diseases of the digestive system', 'Penyakit sistem pencernaan', TRUE, TRUE),

-- Metabolic
('E11.9', 'Type 2 diabetes mellitus without complications', 'Diabetes melitus tipe 2 tanpa komplikasi', 'IV', 'Endocrine, nutritional and metabolic diseases', 'Penyakit endokrin, nutrisi dan metabolik', TRUE, TRUE),
('E11.65', 'Type 2 diabetes mellitus with hyperglycemia', 'Diabetes melitus tipe 2 dengan hiperglikemia', 'IV', 'Endocrine, nutritional and metabolic diseases', 'Penyakit endokrin, nutrisi dan metabolik', TRUE, TRUE),
('E78.5', 'Hyperlipidaemia, unspecified', 'Hiperlipidemia, tidak spesifik', 'IV', 'Endocrine, nutritional and metabolic diseases', 'Penyakit endokrin, nutrisi dan metabolik', TRUE, TRUE),

-- Cardiovascular
('I10', 'Essential (primary) hypertension', 'Hipertensi esensial (primer)', 'IX', 'Diseases of the circulatory system', 'Penyakit sistem sirkulasi', TRUE, TRUE),
('I25.10', 'Atherosclerotic heart disease of native coronary artery', 'Penyakit jantung aterosklerotik arteri koroner asli', 'IX', 'Diseases of the circulatory system', 'Penyakit sistem sirkulasi', TRUE, TRUE),

-- Dermatology
('L30.9', 'Dermatitis, unspecified', 'Dermatitis, tidak spesifik', 'XII', 'Diseases of the skin and subcutaneous tissue', 'Penyakit kulit dan jaringan subkutan', TRUE, TRUE),
('B86', 'Scabies', 'Skabies (gudik)', 'I', 'Certain infectious and parasitic diseases', 'Penyakit infeksi dan parasit tertentu', TRUE, TRUE),

-- Fever
('R50.9', 'Fever, unspecified', 'Demam, tidak spesifik', 'XVIII', 'Symptoms, signs and abnormal findings', 'Gejala, tanda dan temuan abnormal', TRUE, TRUE),
('A90', 'Dengue fever', 'Demam berdarah dengue', 'I', 'Certain infectious and parasitic diseases', 'Penyakit infeksi dan parasit tertentu', TRUE, TRUE),

-- Headache
('R51', 'Headache', 'Sakit kepala', 'XVIII', 'Symptoms, signs and abnormal findings', 'Gejala, tanda dan temuan abnormal', TRUE, TRUE),
('G43.909', 'Migraine, unspecified, not intractable, without status migrainosus', 'Migrain, tidak spesifik', 'VI', 'Diseases of the nervous system', 'Penyakit sistem saraf', TRUE, TRUE),

-- Musculoskeletal
('M79.3', 'Panniculitis, unspecified', 'Mialgia (nyeri otot)', 'XIII', 'Diseases of the musculoskeletal system', 'Penyakit sistem muskuloskeletal', TRUE, TRUE),
('M25.50', 'Pain in unspecified joint', 'Nyeri sendi, tidak spesifik', 'XIII', 'Diseases of the musculoskeletal system', 'Penyakit sistem muskuloskeletal', TRUE, TRUE);

-- Mark common codes
UPDATE clinical_schema.icd10_codes SET is_common = TRUE
WHERE code IN ('J00', 'J06.9', 'K29.7', 'A09', 'E11.9', 'I10', 'R50.9', 'A90');

-- ============================================================================
-- Insert Sample Common Diagnoses for POLI_UMUM (General Practice)
-- ============================================================================

DO $$
DECLARE
    v_icd10_id UUID;
    v_dept_code VARCHAR(20) := 'POLI_UMUM';
    v_dept_name VARCHAR(200) := 'Poliklinik Umum';
BEGIN
    -- Common cold
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'J00';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'J00', 'Nasofaringitis akut [pilek]', 1, TRUE);
    END IF;

    -- URTI
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'J06.9';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'J06.9', 'Infeksi saluran pernapasan atas akut', 2, TRUE);
    END IF;

    -- Gastritis
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'K29.7';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'K29.7', 'Gastritis', 3, TRUE);
    END IF;

    -- Diarrhea
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'A09';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'A09', 'Diare dan gastroenteritis', 4, TRUE);
    END IF;

    -- Hypertension
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'I10';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'I10', 'Hipertensi', 5, TRUE);
    END IF;

    -- Diabetes
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'E11.9';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'E11.9', 'Diabetes melitus tipe 2', 6, TRUE);
    END IF;

    -- Fever
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'R50.9';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'R50.9', 'Demam', 7, TRUE);
    END IF;

    -- Dengue
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'A90';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'A90', 'Demam berdarah dengue', 8, TRUE);
    END IF;

    -- Headache
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'R51';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'R51', 'Sakit kepala', 9, TRUE);
    END IF;

    -- Dermatitis
    SELECT id INTO v_icd10_id FROM clinical_schema.icd10_codes WHERE code = 'L30.9';
    IF v_icd10_id IS NOT NULL THEN
        INSERT INTO clinical_schema.common_diagnoses (department_code, department_name, icd10_code_id, icd10_code, icd10_description_id, rank_order, is_active)
        VALUES (v_dept_code, v_dept_name, v_icd10_id, 'L30.9', 'Dermatitis', 10, TRUE);
    END IF;
END $$;

-- ============================================================================
-- End of Migration V22
-- ============================================================================
