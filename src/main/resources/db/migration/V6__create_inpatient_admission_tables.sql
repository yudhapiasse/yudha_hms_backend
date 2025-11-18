-- ============================================================================
-- Flyway Migration V6: Create Inpatient (Rawat Inap) Admission Tables
-- Description: Creates tables for rooms, beds, inpatient admissions, and bed assignments
-- Author: HMS Development Team
-- Date: 2025-01-19
-- ============================================================================

-- ============================================================================
-- ROOM TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.room (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_number VARCHAR(20) NOT NULL UNIQUE,
    room_name VARCHAR(100),

    -- Room classification
    room_class VARCHAR(20) NOT NULL, -- VIP, KELAS_1, KELAS_2, KELAS_3, ICU, NICU, PICU
    room_type VARCHAR(50), -- STANDARD, ISOLATION, SUITE

    -- Location
    building VARCHAR(50),
    floor VARCHAR(20),
    wing VARCHAR(50), -- North, South, East, West

    -- Capacity
    total_beds INTEGER NOT NULL DEFAULT 1,
    available_beds INTEGER NOT NULL DEFAULT 1,

    -- Pricing per day
    base_room_rate DECIMAL(12,2) NOT NULL DEFAULT 0,

    -- Facilities
    has_ac BOOLEAN DEFAULT true,
    has_tv BOOLEAN DEFAULT false,
    has_bathroom BOOLEAN DEFAULT true,
    has_wifi BOOLEAN DEFAULT false,
    has_refrigerator BOOLEAN DEFAULT false,
    has_sofa_bed BOOLEAN DEFAULT false, -- For family

    -- Gender restriction
    gender_restriction VARCHAR(10), -- MALE, FEMALE, NULL (mixed)

    -- Status
    is_active BOOLEAN DEFAULT true,
    is_available BOOLEAN DEFAULT true,

    -- Notes
    description TEXT,
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_room_number ON registration_schema.room(room_number);
CREATE INDEX idx_room_class ON registration_schema.room(room_class);
CREATE INDEX idx_room_available ON registration_schema.room(is_available);
CREATE INDEX idx_room_floor ON registration_schema.room(floor);

COMMENT ON TABLE registration_schema.room IS 'Hospital rooms for inpatient care';
COMMENT ON COLUMN registration_schema.room.room_class IS 'VIP, KELAS_1, KELAS_2, KELAS_3, ICU, NICU, PICU';

-- ============================================================================
-- BED TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.bed (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bed_number VARCHAR(20) NOT NULL,
    room_id UUID NOT NULL REFERENCES registration_schema.room(id) ON DELETE CASCADE,

    -- Bed details
    bed_type VARCHAR(50), -- STANDARD, ELECTRIC, ICU, PEDIATRIC
    bed_position VARCHAR(20), -- WINDOW, DOOR, CENTER, CORNER

    -- Equipment
    has_monitor BOOLEAN DEFAULT false,
    has_ventilator BOOLEAN DEFAULT false,
    has_oxygen BOOLEAN DEFAULT true,
    has_suction BOOLEAN DEFAULT false,

    -- Availability
    is_occupied BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    is_maintenance BOOLEAN DEFAULT false,

    -- Current patient (denormalized for quick access)
    current_patient_id UUID,
    current_admission_id UUID,
    occupied_since TIMESTAMP,

    -- Notes
    notes TEXT,
    maintenance_notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,

    CONSTRAINT uq_bed_number_room UNIQUE (room_id, bed_number)
);

CREATE INDEX idx_bed_room ON registration_schema.bed(room_id);
CREATE INDEX idx_bed_occupied ON registration_schema.bed(is_occupied);
CREATE INDEX idx_bed_patient ON registration_schema.bed(current_patient_id);
CREATE INDEX idx_bed_admission ON registration_schema.bed(current_admission_id);

COMMENT ON TABLE registration_schema.bed IS 'Hospital beds within rooms';

-- ============================================================================
-- INPATIENT ADMISSION TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.inpatient_admission (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admission_number VARCHAR(50) NOT NULL UNIQUE,

    -- Patient reference
    patient_id UUID NOT NULL,

    -- Registration reference (if from outpatient/emergency)
    outpatient_registration_id UUID REFERENCES registration_schema.outpatient_registration(id),

    -- Admission details
    admission_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    admission_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    admission_type VARCHAR(30) NOT NULL, -- ELECTIVE, EMERGENCY, TRANSFER, OBSERVATION
    admission_source VARCHAR(30), -- OUTPATIENT, EMERGENCY, REFERRAL, DIRECT

    -- Room and bed assignment
    room_id UUID REFERENCES registration_schema.room(id),
    bed_id UUID REFERENCES registration_schema.bed(id),
    room_class VARCHAR(20) NOT NULL, -- VIP, KELAS_1, KELAS_2, KELAS_3

    -- Medical team
    admitting_doctor_id UUID, -- Will reference staff table
    admitting_doctor_name VARCHAR(200),
    attending_doctor_id UUID,
    attending_doctor_name VARCHAR(200),
    referring_doctor_id UUID,
    referring_doctor_name VARCHAR(200),
    referring_facility VARCHAR(200),

    -- Clinical information
    chief_complaint TEXT NOT NULL,
    admission_diagnosis TEXT, -- Primary diagnosis at admission
    secondary_diagnoses TEXT, -- Additional diagnoses

    -- Estimated stay
    estimated_length_of_stay_days INTEGER,
    estimated_discharge_date DATE,

    -- Payment information
    payment_method VARCHAR(20) NOT NULL, -- CASH, BPJS, INSURANCE, COMPANY
    is_bpjs BOOLEAN DEFAULT false,
    bpjs_card_number VARCHAR(50),
    insurance_name VARCHAR(100),
    insurance_number VARCHAR(50),
    insurance_coverage_limit DECIMAL(12,2),

    -- Financial
    room_rate_per_day DECIMAL(12,2) NOT NULL,
    required_deposit DECIMAL(12,2) DEFAULT 0,
    deposit_paid DECIMAL(12,2) DEFAULT 0,
    deposit_paid_date TIMESTAMP,
    deposit_receipt_number VARCHAR(50),

    -- Status tracking
    status VARCHAR(30) NOT NULL DEFAULT 'ADMITTED', -- ADMITTED, IN_TREATMENT, DISCHARGED, TRANSFERRED, DECEASED, CANCELLED

    -- Discharge information
    discharge_date TIMESTAMP,
    discharge_time TIMESTAMP,
    discharge_type VARCHAR(30), -- ROUTINE, AMA, TRANSFER, DECEASED
    discharge_disposition VARCHAR(50), -- HOME, HOME_HEALTH, REHAB, NURSING_HOME, DECEASED
    discharge_summary TEXT,
    discharge_instructions TEXT,

    -- Length of stay calculation
    actual_length_of_stay_days INTEGER,

    -- Emergency contact during admission
    emergency_contact_name VARCHAR(200),
    emergency_contact_relationship VARCHAR(50),
    emergency_contact_phone VARCHAR(20),

    -- Patient belongings
    belongings_stored BOOLEAN DEFAULT false,
    belongings_list TEXT,

    -- Special needs
    requires_isolation BOOLEAN DEFAULT false,
    isolation_type VARCHAR(50), -- AIRBORNE, DROPLET, CONTACT, PROTECTIVE
    requires_interpreter BOOLEAN DEFAULT false,
    interpreter_language VARCHAR(50),
    has_allergies BOOLEAN DEFAULT false,
    allergy_notes TEXT,

    -- Notes
    admission_notes TEXT,
    special_instructions TEXT,

    -- Cancellation
    cancelled_at TIMESTAMP,
    cancelled_by VARCHAR(100),
    cancellation_reason TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_inpatient_admission_number ON registration_schema.inpatient_admission(admission_number);
CREATE INDEX idx_inpatient_patient ON registration_schema.inpatient_admission(patient_id);
CREATE INDEX idx_inpatient_admission_date ON registration_schema.inpatient_admission(admission_date);
CREATE INDEX idx_inpatient_room ON registration_schema.inpatient_admission(room_id);
CREATE INDEX idx_inpatient_bed ON registration_schema.inpatient_admission(bed_id);
CREATE INDEX idx_inpatient_status ON registration_schema.inpatient_admission(status);
CREATE INDEX idx_inpatient_admitting_doctor ON registration_schema.inpatient_admission(admitting_doctor_id);
CREATE INDEX idx_inpatient_discharge_date ON registration_schema.inpatient_admission(discharge_date);

COMMENT ON TABLE registration_schema.inpatient_admission IS 'Inpatient admission records';
COMMENT ON COLUMN registration_schema.inpatient_admission.admission_type IS 'ELECTIVE, EMERGENCY, TRANSFER, OBSERVATION';
COMMENT ON COLUMN registration_schema.inpatient_admission.status IS 'ADMITTED, IN_TREATMENT, DISCHARGED, TRANSFERRED, DECEASED, CANCELLED';

-- ============================================================================
-- BED ASSIGNMENT HISTORY TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.bed_assignment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admission_id UUID NOT NULL REFERENCES registration_schema.inpatient_admission(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL,

    -- Bed details
    room_id UUID NOT NULL REFERENCES registration_schema.room(id),
    bed_id UUID NOT NULL REFERENCES registration_schema.bed(id),
    room_number VARCHAR(20) NOT NULL,
    bed_number VARCHAR(20) NOT NULL,
    room_class VARCHAR(20) NOT NULL,

    -- Assignment period
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    released_at TIMESTAMP,

    -- Assignment reason
    assignment_type VARCHAR(30) NOT NULL, -- INITIAL, TRANSFER, UPGRADE, DOWNGRADE
    transfer_reason TEXT,

    -- Rate at time of assignment (historical)
    room_rate_per_day DECIMAL(12,2),

    -- Status
    is_current BOOLEAN DEFAULT true,

    -- Notes
    notes TEXT,

    -- Assigned by
    assigned_by VARCHAR(100),
    released_by VARCHAR(100),

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_bed_assignment_admission ON registration_schema.bed_assignment(admission_id);
CREATE INDEX idx_bed_assignment_patient ON registration_schema.bed_assignment(patient_id);
CREATE INDEX idx_bed_assignment_bed ON registration_schema.bed_assignment(bed_id);
CREATE INDEX idx_bed_assignment_room ON registration_schema.bed_assignment(room_id);
CREATE INDEX idx_bed_assignment_current ON registration_schema.bed_assignment(is_current);
CREATE INDEX idx_bed_assignment_dates ON registration_schema.bed_assignment(assigned_at, released_at);

COMMENT ON TABLE registration_schema.bed_assignment IS 'Bed assignment history and transfers';

-- ============================================================================
-- ADMISSION DIAGNOSIS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS registration_schema.admission_diagnosis (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admission_id UUID NOT NULL REFERENCES registration_schema.inpatient_admission(id) ON DELETE CASCADE,
    patient_id UUID NOT NULL,

    -- ICD-10 Code
    icd10_id UUID REFERENCES master_schema.icd10(id),
    icd10_code VARCHAR(10) NOT NULL,
    icd10_description TEXT NOT NULL,

    -- Diagnosis type
    diagnosis_type VARCHAR(20) NOT NULL, -- PRIMARY, SECONDARY, COMPLICATION, COMORBIDITY
    is_primary BOOLEAN DEFAULT false,

    -- Timing
    diagnosed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Clinical details
    notes TEXT,

    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_admission_diagnosis_admission ON registration_schema.admission_diagnosis(admission_id);
CREATE INDEX idx_admission_diagnosis_patient ON registration_schema.admission_diagnosis(patient_id);
CREATE INDEX idx_admission_diagnosis_icd10 ON registration_schema.admission_diagnosis(icd10_code);
CREATE INDEX idx_admission_diagnosis_primary ON registration_schema.admission_diagnosis(is_primary);

COMMENT ON TABLE registration_schema.admission_diagnosis IS 'Diagnoses associated with inpatient admissions';

-- ============================================================================
-- ADMISSION NUMBER SEQUENCE
-- ============================================================================
CREATE SEQUENCE IF NOT EXISTS registration_schema.admission_number_sequence START WITH 1;

COMMENT ON SEQUENCE registration_schema.admission_number_sequence IS 'Sequence for generating admission numbers';

-- ============================================================================
-- INSERT SAMPLE ROOMS AND BEDS
-- ============================================================================

-- VIP Rooms (Building A, Floor 5)
INSERT INTO registration_schema.room (room_number, room_name, room_class, room_type, building, floor, wing, total_beds, available_beds, base_room_rate, has_ac, has_tv, has_bathroom, has_wifi, has_refrigerator, has_sofa_bed, is_active, is_available) VALUES
('A501', 'VIP Suite 1', 'VIP', 'SUITE', 'Gedung A', '5', 'East', 1, 1, 1500000, true, true, true, true, true, true, true, true),
('A502', 'VIP Suite 2', 'VIP', 'SUITE', 'Gedung A', '5', 'East', 1, 1, 1500000, true, true, true, true, true, true, true, true),
('A503', 'VIP Suite 3', 'VIP', 'SUITE', 'Gedung A', '5', 'West', 1, 1, 1500000, true, true, true, true, true, true, true, true);

-- Kelas 1 Rooms (Building A, Floor 4)
INSERT INTO registration_schema.room (room_number, room_name, room_class, room_type, building, floor, wing, total_beds, available_beds, base_room_rate, has_ac, has_tv, has_bathroom, has_wifi, is_active, is_available) VALUES
('A401', 'Kelas 1 - Room 1', 'KELAS_1', 'STANDARD', 'Gedung A', '4', 'North', 2, 2, 750000, true, true, true, true, true, true),
('A402', 'Kelas 1 - Room 2', 'KELAS_1', 'STANDARD', 'Gedung A', '4', 'North', 2, 2, 750000, true, true, true, true, true, true),
('A403', 'Kelas 1 - Room 3', 'KELAS_1', 'STANDARD', 'Gedung A', '4', 'South', 2, 2, 750000, true, true, true, true, true, true);

-- Kelas 2 Rooms (Building B, Floor 3)
INSERT INTO registration_schema.room (room_number, room_name, room_class, room_type, building, floor, wing, total_beds, available_beds, base_room_rate, has_ac, has_tv, has_bathroom, is_active, is_available) VALUES
('B301', 'Kelas 2 - Room 1', 'KELAS_2', 'STANDARD', 'Gedung B', '3', 'East', 4, 4, 400000, true, false, true, true, true),
('B302', 'Kelas 2 - Room 2', 'KELAS_2', 'STANDARD', 'Gedung B', '3', 'East', 4, 4, 400000, true, false, true, true, true),
('B303', 'Kelas 2 - Room 3', 'KELAS_2', 'STANDARD', 'Gedung B', '3', 'West', 4, 4, 400000, true, false, true, true, true);

-- Kelas 3 Rooms (Building B, Floor 2)
INSERT INTO registration_schema.room (room_number, room_name, room_class, room_type, building, floor, wing, total_beds, available_beds, base_room_rate, has_ac, has_bathroom, is_active, is_available) VALUES
('B201', 'Kelas 3 - Room 1', 'KELAS_3', 'STANDARD', 'Gedung B', '2', 'North', 6, 6, 200000, false, true, true, true),
('B202', 'Kelas 3 - Room 2', 'KELAS_3', 'STANDARD', 'Gedung B', '2', 'North', 6, 6, 200000, false, true, true, true),
('B203', 'Kelas 3 - Room 3', 'KELAS_3', 'STANDARD', 'Gedung B', '2', 'South', 6, 6, 200000, false, true, true, true);

-- ICU Rooms (Building A, Floor 3)
INSERT INTO registration_schema.room (room_number, room_name, room_class, room_type, building, floor, wing, total_beds, available_beds, base_room_rate, has_ac, has_tv, has_bathroom, has_wifi, is_active, is_available) VALUES
('A301', 'ICU Room 1', 'ICU', 'STANDARD', 'Gedung A', '3', 'Center', 4, 4, 2000000, true, false, true, true, true, true),
('A302', 'ICU Room 2', 'ICU', 'STANDARD', 'Gedung A', '3', 'Center', 4, 4, 2000000, true, false, true, true, true, true);

-- Insert beds for each room
DO $$
DECLARE
    room_record RECORD;
    bed_count INTEGER;
BEGIN
    FOR room_record IN
        SELECT id, room_number, total_beds, room_class
        FROM registration_schema.room
    LOOP
        FOR bed_count IN 1..room_record.total_beds LOOP
            INSERT INTO registration_schema.bed (
                bed_number,
                room_id,
                bed_type,
                bed_position,
                has_monitor,
                has_ventilator,
                has_oxygen,
                is_occupied,
                is_active
            ) VALUES (
                'BED-' || bed_count::TEXT,
                room_record.id,
                CASE
                    WHEN room_record.room_class = 'ICU' THEN 'ICU'
                    WHEN room_record.room_class = 'VIP' THEN 'ELECTRIC'
                    ELSE 'STANDARD'
                END,
                CASE
                    WHEN bed_count = 1 THEN 'WINDOW'
                    WHEN bed_count = room_record.total_beds THEN 'DOOR'
                    ELSE 'CENTER'
                END,
                room_record.room_class = 'ICU',
                room_record.room_class = 'ICU',
                true,
                false,
                true
            );
        END LOOP;
    END LOOP;

    RAISE NOTICE 'Successfully created beds for all rooms';
END $$;

-- ============================================================================
-- ADD TRIGGERS
-- ============================================================================

CREATE TRIGGER update_room_timestamp
    BEFORE UPDATE ON registration_schema.room
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_bed_timestamp
    BEFORE UPDATE ON registration_schema.bed
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_inpatient_admission_timestamp
    BEFORE UPDATE ON registration_schema.inpatient_admission
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_bed_assignment_timestamp
    BEFORE UPDATE ON registration_schema.bed_assignment
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_admission_diagnosis_timestamp
    BEFORE UPDATE ON registration_schema.admission_diagnosis
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- ============================================================================
-- SUMMARY
-- ============================================================================

DO $$
DECLARE
    room_count INTEGER;
    bed_count INTEGER;
    vip_count INTEGER;
    kelas1_count INTEGER;
    kelas2_count INTEGER;
    kelas3_count INTEGER;
    icu_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO room_count FROM registration_schema.room;
    SELECT COUNT(*) INTO bed_count FROM registration_schema.bed;
    SELECT COUNT(*) INTO vip_count FROM registration_schema.room WHERE room_class = 'VIP';
    SELECT COUNT(*) INTO kelas1_count FROM registration_schema.room WHERE room_class = 'KELAS_1';
    SELECT COUNT(*) INTO kelas2_count FROM registration_schema.room WHERE room_class = 'KELAS_2';
    SELECT COUNT(*) INTO kelas3_count FROM registration_schema.room WHERE room_class = 'KELAS_3';
    SELECT COUNT(*) INTO icu_count FROM registration_schema.room WHERE room_class = 'ICU';

    RAISE NOTICE '============================================';
    RAISE NOTICE 'Inpatient Admission Tables Created!';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Tables created:';
    RAISE NOTICE '  - room (% rooms)', room_count;
    RAISE NOTICE '  - bed (% beds)', bed_count;
    RAISE NOTICE '  - inpatient_admission';
    RAISE NOTICE '  - bed_assignment';
    RAISE NOTICE '  - admission_diagnosis';
    RAISE NOTICE '';
    RAISE NOTICE 'Room breakdown:';
    RAISE NOTICE '  - VIP: % rooms', vip_count;
    RAISE NOTICE '  - Kelas 1: % rooms', kelas1_count;
    RAISE NOTICE '  - Kelas 2: % rooms', kelas2_count;
    RAISE NOTICE '  - Kelas 3: % rooms', kelas3_count;
    RAISE NOTICE '  - ICU: % rooms', icu_count;
    RAISE NOTICE '';
    RAISE NOTICE 'Features:';
    RAISE NOTICE '  - Room class selection (VIP to Kelas 3)';
    RAISE NOTICE '  - Bed availability tracking';
    RAISE NOTICE '  - Deposit calculation';
    RAISE NOTICE '  - Bed assignment history';
    RAISE NOTICE '  - Multiple diagnoses per admission';
    RAISE NOTICE '  - Referring doctor tracking';
    RAISE NOTICE '============================================';
END $$;

SELECT 'Inpatient admission tables created successfully!' AS status;
