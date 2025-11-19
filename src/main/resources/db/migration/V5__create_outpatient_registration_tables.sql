-- ============================================================================
-- Flyway Migration V5: Create Outpatient Registration Tables
-- Description: Creates tables for polyclinics, doctors, schedules, registrations, and queues
-- Author: HMS Development Team
-- Date: 2025-01-19
-- ============================================================================

-- ============================================================================
-- POLYCLINIC (POLIKLINIK) TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.polyclinic (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    floor_location VARCHAR(50),
    building VARCHAR(50),
    phone VARCHAR(20),
    extension VARCHAR(10),

    -- Operating hours
    operating_days VARCHAR(100), -- JSON array: ["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]
    opening_time TIME, -- e.g., 08:00:00
    closing_time TIME, -- e.g., 16:00:00

    -- Capacity and configuration
    max_patients_per_day INTEGER DEFAULT 50,
    appointment_duration_minutes INTEGER DEFAULT 15,
    allow_walk_in BOOLEAN DEFAULT true,
    allow_appointments BOOLEAN DEFAULT true,

    -- Status
    is_active BOOLEAN DEFAULT true,
    is_emergency BOOLEAN DEFAULT false,

    -- Registration fee
    base_registration_fee DECIMAL(10,2) DEFAULT 0,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_polyclinic_code ON registration_schema.polyclinic(code);
CREATE INDEX idx_polyclinic_active ON registration_schema.polyclinic(is_active);

COMMENT ON TABLE registration_schema.polyclinic IS 'Polyclinic/Poli master data';
COMMENT ON COLUMN registration_schema.polyclinic.operating_days IS 'JSON array of operating days';
COMMENT ON COLUMN registration_schema.polyclinic.appointment_duration_minutes IS 'Default appointment duration in minutes';

-- ============================================================================
-- DOCTOR TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.doctor (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id VARCHAR(50) UNIQUE,
    str_number VARCHAR(50) UNIQUE, -- Surat Tanda Registrasi (Medical License)
    str_expiry_date DATE,
    sip_number VARCHAR(50), -- Surat Izin Praktik
    sip_expiry_date DATE,

    -- Personal information
    title VARCHAR(50), -- dr., dr. Sp.A, Prof. Dr., etc.
    full_name VARCHAR(200) NOT NULL,
    specialization VARCHAR(100), -- Spesialis Anak, Spesialis Kandungan, etc.
    sub_specialization VARCHAR(100),

    -- Contact
    phone VARCHAR(20),
    email VARCHAR(100),

    -- Professional details
    medical_school VARCHAR(200),
    graduation_year INTEGER,
    years_of_experience INTEGER,

    -- Consultation fee
    base_consultation_fee DECIMAL(10,2) DEFAULT 0,
    bpjs_consultation_fee DECIMAL(10,2) DEFAULT 0,

    -- Status
    is_active BOOLEAN DEFAULT true,
    is_available_for_telemedicine BOOLEAN DEFAULT false,

    -- Photo
    photo_url VARCHAR(500),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_doctor_str ON registration_schema.doctor(str_number);
CREATE INDEX idx_doctor_active ON registration_schema.doctor(is_active);
CREATE INDEX idx_doctor_name ON registration_schema.doctor(full_name);

COMMENT ON TABLE registration_schema.doctor IS 'Doctor master data';
COMMENT ON COLUMN registration_schema.doctor.str_number IS 'Medical license number (Surat Tanda Registrasi)';
COMMENT ON COLUMN registration_schema.doctor.sip_number IS 'Practice permit number (Surat Izin Praktik)';

-- ============================================================================
-- DOCTOR SCHEDULE TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.doctor_schedule (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID NOT NULL REFERENCES registration_schema.doctor(id) ON DELETE CASCADE,
    polyclinic_id UUID NOT NULL REFERENCES registration_schema.polyclinic(id) ON DELETE CASCADE,

    -- Schedule timing
    day_of_week VARCHAR(20) NOT NULL, -- MONDAY, TUESDAY, etc.
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    -- Date range (optional - for temporary schedules)
    effective_date DATE,
    expiry_date DATE,

    -- Capacity
    max_patients INTEGER DEFAULT 20,
    appointment_duration_minutes INTEGER DEFAULT 15,

    -- Status
    is_active BOOLEAN DEFAULT true,

    -- Notes
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,

    CONSTRAINT chk_schedule_time CHECK (end_time > start_time)
);

CREATE INDEX idx_doctor_schedule_doctor ON registration_schema.doctor_schedule(doctor_id);
CREATE INDEX idx_doctor_schedule_polyclinic ON registration_schema.doctor_schedule(polyclinic_id);
CREATE INDEX idx_doctor_schedule_day ON registration_schema.doctor_schedule(day_of_week);
CREATE INDEX idx_doctor_schedule_active ON registration_schema.doctor_schedule(is_active);

COMMENT ON TABLE registration_schema.doctor_schedule IS 'Doctor practice schedules per polyclinic';
COMMENT ON COLUMN registration_schema.doctor_schedule.day_of_week IS 'Day of week: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY';

-- ============================================================================
-- OUTPATIENT REGISTRATION TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.outpatient_registration (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    registration_number VARCHAR(50) NOT NULL UNIQUE,

    -- Patient reference
    patient_id UUID NOT NULL,

    -- Polyclinic and doctor
    polyclinic_id UUID NOT NULL REFERENCES registration_schema.polyclinic(id),
    doctor_id UUID NOT NULL REFERENCES registration_schema.doctor(id),
    doctor_schedule_id UUID REFERENCES registration_schema.doctor_schedule(id),

    -- Registration details
    registration_date DATE NOT NULL,
    registration_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    registration_type VARCHAR(20) NOT NULL, -- WALK_IN, APPOINTMENT

    -- Appointment details (for APPOINTMENT type)
    appointment_date DATE,
    appointment_time TIME,
    appointment_end_time TIME,

    -- Payment
    payment_method VARCHAR(20), -- CASH, BPJS, INSURANCE, DEBIT, CREDIT
    is_bpjs BOOLEAN DEFAULT false,
    bpjs_card_number VARCHAR(50),
    registration_fee DECIMAL(10,2) DEFAULT 0,
    consultation_fee DECIMAL(10,2) DEFAULT 0,
    total_fee DECIMAL(10,2) DEFAULT 0,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED', -- REGISTERED, WAITING, IN_CONSULTATION, COMPLETED, CANCELLED

    -- Queue
    queue_number INTEGER,
    queue_code VARCHAR(10), -- e.g., "A001", "B015"

    -- Visit reason
    chief_complaint TEXT,
    notes TEXT,

    -- Referral
    referral_from VARCHAR(100),
    referral_letter_number VARCHAR(50),

    -- Timestamps
    check_in_time TIMESTAMP,
    consultation_start_time TIMESTAMP,
    consultation_end_time TIMESTAMP,

    -- Cancellation
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    cancelled_by VARCHAR(100),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_outpatient_registration_number ON registration_schema.outpatient_registration(registration_number);
CREATE INDEX idx_outpatient_registration_patient ON registration_schema.outpatient_registration(patient_id);
CREATE INDEX idx_outpatient_registration_date ON registration_schema.outpatient_registration(registration_date);
CREATE INDEX idx_outpatient_registration_polyclinic ON registration_schema.outpatient_registration(polyclinic_id);
CREATE INDEX idx_outpatient_registration_doctor ON registration_schema.outpatient_registration(doctor_id);
CREATE INDEX idx_outpatient_registration_status ON registration_schema.outpatient_registration(status);
CREATE INDEX idx_outpatient_registration_type ON registration_schema.outpatient_registration(registration_type);
CREATE INDEX idx_outpatient_registration_queue ON registration_schema.outpatient_registration(queue_number);

COMMENT ON TABLE registration_schema.outpatient_registration IS 'Outpatient registration records';
COMMENT ON COLUMN registration_schema.outpatient_registration.registration_type IS 'WALK_IN or APPOINTMENT';
COMMENT ON COLUMN registration_schema.outpatient_registration.status IS 'REGISTERED, WAITING, IN_CONSULTATION, COMPLETED, CANCELLED';

-- ============================================================================
-- QUEUE NUMBER SEQUENCE TABLE (per polyclinic per day)
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.queue_sequence (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    polyclinic_id UUID NOT NULL REFERENCES registration_schema.polyclinic(id),
    queue_date DATE NOT NULL,
    last_queue_number INTEGER DEFAULT 0,
    prefix VARCHAR(5), -- e.g., "A", "B", "UM" (Umum), "AN" (Anak)

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_queue_sequence UNIQUE (polyclinic_id, queue_date)
);

CREATE INDEX idx_queue_sequence_polyclinic ON registration_schema.queue_sequence(polyclinic_id);
CREATE INDEX idx_queue_sequence_date ON registration_schema.queue_sequence(queue_date);

COMMENT ON TABLE registration_schema.queue_sequence IS 'Tracks queue numbers per polyclinic per day';

-- ============================================================================
-- INSERT SAMPLE DATA
-- ============================================================================

-- Insert sample polyclinics
INSERT INTO registration_schema.polyclinic (code, name, description, floor_location, building, operating_days, opening_time, closing_time, max_patients_per_day, base_registration_fee, is_active) VALUES
('POLI-UMUM', 'Poli Umum', 'Poliklinik Umum untuk pemeriksaan kesehatan umum', 'Lantai 1', 'Gedung A', '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"]', '08:00:00', '16:00:00', 50, 25000, true),
('POLI-ANAK', 'Poli Anak', 'Poliklinik Anak untuk pemeriksaan kesehatan anak', 'Lantai 2', 'Gedung A', '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]', '08:00:00', '15:00:00', 40, 30000, true),
('POLI-KAND', 'Poli Kandungan', 'Poliklinik Kandungan dan Kebidanan', 'Lantai 2', 'Gedung B', '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]', '08:00:00', '14:00:00', 30, 35000, true),
('POLI-GIGI', 'Poli Gigi', 'Poliklinik Gigi dan Mulut', 'Lantai 1', 'Gedung B', '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]', '08:00:00', '16:00:00', 25, 30000, true),
('POLI-MATA', 'Poli Mata', 'Poliklinik Mata', 'Lantai 3', 'Gedung A', '["MONDAY","WEDNESDAY","FRIDAY"]', '08:00:00', '12:00:00', 20, 35000, true),
('POLI-THT', 'Poli THT', 'Poliklinik Telinga Hidung Tenggorokan', 'Lantai 3', 'Gedung A', '["TUESDAY","THURSDAY","SATURDAY"]', '08:00:00', '13:00:00', 20, 35000, true),
('POLI-JANT', 'Poli Jantung', 'Poliklinik Jantung dan Pembuluh Darah', 'Lantai 3', 'Gedung B', '["MONDAY","WEDNESDAY","FRIDAY"]', '08:00:00', '14:00:00', 15, 50000, true),
('POLI-PENY', 'Poli Penyakit Dalam', 'Poliklinik Penyakit Dalam', 'Lantai 2', 'Gedung B', '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]', '08:00:00', '15:00:00', 35, 40000, true)
ON CONFLICT (code) DO NOTHING;

-- Insert sample doctors
INSERT INTO registration_schema.doctor (str_number, full_name, title, specialization, phone, base_consultation_fee, bpjs_consultation_fee, is_active) VALUES
('STR-2024-001', 'Dr. Ahmad Hidayat', 'dr.', 'Dokter Umum', '081234567890', 100000, 0, true),
('STR-2024-002', 'Dr. Siti Nurhaliza, Sp.A', 'dr. Sp.A', 'Spesialis Anak', '081234567891', 200000, 0, true),
('STR-2024-003', 'Dr. Budi Santoso, Sp.OG', 'dr. Sp.OG', 'Spesialis Kandungan', '081234567892', 250000, 0, true),
('STR-2024-004', 'Dr. Lisa Permata, Sp.Gigi', 'drg.', 'Dokter Gigi', '081234567893', 150000, 0, true),
('STR-2024-005', 'Dr. Rahman Abdullah, Sp.M', 'dr. Sp.M', 'Spesialis Mata', '081234567894', 200000, 0, true),
('STR-2024-006', 'Dr. Dewi Lestari, Sp.THT', 'dr. Sp.THT', 'Spesialis THT', '081234567895', 200000, 0, true),
('STR-2024-007', 'Dr. Andi Wijaya, Sp.JP', 'dr. Sp.JP', 'Spesialis Jantung', '081234567896', 300000, 0, true),
('STR-2024-008', 'Dr. Nina Marlina, Sp.PD', 'dr. Sp.PD', 'Spesialis Penyakit Dalam', '081234567897', 250000, 0, true)
ON CONFLICT (str_number) DO NOTHING;

-- Insert sample doctor schedules
-- Dr. Ahmad (Poli Umum) - Monday to Saturday
INSERT INTO registration_schema.doctor_schedule (doctor_id, polyclinic_id, day_of_week, start_time, end_time, max_patients, is_active)
SELECT
    (SELECT id FROM registration_schema.doctor WHERE str_number = 'STR-2024-001'),
    (SELECT id FROM registration_schema.polyclinic WHERE code = 'POLI-UMUM'),
    day,
    '08:00:00',
    '16:00:00',
    30,
    true
FROM unnest(ARRAY['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']) AS day;

-- Dr. Siti (Poli Anak) - Monday to Friday
INSERT INTO registration_schema.doctor_schedule (doctor_id, polyclinic_id, day_of_week, start_time, end_time, max_patients, is_active)
SELECT
    (SELECT id FROM registration_schema.doctor WHERE str_number = 'STR-2024-002'),
    (SELECT id FROM registration_schema.polyclinic WHERE code = 'POLI-ANAK'),
    day,
    '08:00:00',
    '15:00:00',
    25,
    true
FROM unnest(ARRAY['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']) AS day;

-- Dr. Budi (Poli Kandungan) - Monday, Wednesday, Friday
INSERT INTO registration_schema.doctor_schedule (doctor_id, polyclinic_id, day_of_week, start_time, end_time, max_patients, is_active)
SELECT
    (SELECT id FROM registration_schema.doctor WHERE str_number = 'STR-2024-003'),
    (SELECT id FROM registration_schema.polyclinic WHERE code = 'POLI-KAND'),
    day,
    '08:00:00',
    '14:00:00',
    20,
    true
FROM unnest(ARRAY['MONDAY', 'WEDNESDAY', 'FRIDAY']) AS day;

SELECT 'Outpatient registration tables created successfully!' AS status;
