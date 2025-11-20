-- ============================================================================
-- Migration: V13 - Outpatient Workflow Phase 1 Enhancements
-- Description: Add encounter integration and queue calling system
-- Author: HMS Development Team
-- Date: 2025-11-20
-- ============================================================================

-- ============================================================================
-- 1. Add encounter_id to outpatient_registration
-- ============================================================================

ALTER TABLE registration_schema.outpatient_registration
ADD COLUMN encounter_id UUID REFERENCES clinical_schema.encounter(id);

CREATE INDEX idx_outpatient_registration_encounter
ON registration_schema.outpatient_registration(encounter_id);

COMMENT ON COLUMN registration_schema.outpatient_registration.encounter_id IS
'Link to the clinical encounter created for this outpatient visit';

-- ============================================================================
-- 2. Add queue status tracking to outpatient_registration
-- ============================================================================

ALTER TABLE registration_schema.outpatient_registration
ADD COLUMN queue_status VARCHAR(20) DEFAULT 'WAITING';

CREATE INDEX idx_outpatient_registration_queue_status
ON registration_schema.outpatient_registration(queue_status);

COMMENT ON COLUMN registration_schema.outpatient_registration.queue_status IS
'Current queue status: WAITING, CALLED, SERVING, COMPLETED, SKIPPED, CANCELLED';

-- ============================================================================
-- 3. Add queue timestamps
-- ============================================================================

ALTER TABLE registration_schema.outpatient_registration
ADD COLUMN queue_called_at TIMESTAMP,
ADD COLUMN queue_called_by VARCHAR(100),
ADD COLUMN queue_serving_started_at TIMESTAMP,
ADD COLUMN queue_serving_ended_at TIMESTAMP,
ADD COLUMN queue_skipped_at TIMESTAMP,
ADD COLUMN queue_skip_reason TEXT;

CREATE INDEX idx_outpatient_registration_queue_called_at
ON registration_schema.outpatient_registration(queue_called_at);

CREATE INDEX idx_outpatient_registration_queue_serving_started_at
ON registration_schema.outpatient_registration(queue_serving_started_at);

COMMENT ON COLUMN registration_schema.outpatient_registration.queue_called_at IS
'Timestamp when patient was called from waiting area';

COMMENT ON COLUMN registration_schema.outpatient_registration.queue_called_by IS
'User who called the patient';

COMMENT ON COLUMN registration_schema.outpatient_registration.queue_serving_started_at IS
'Timestamp when patient consultation started';

COMMENT ON COLUMN registration_schema.outpatient_registration.queue_serving_ended_at IS
'Timestamp when patient consultation ended';

COMMENT ON COLUMN registration_schema.outpatient_registration.queue_skipped_at IS
'Timestamp when patient was skipped (not present)';

-- ============================================================================
-- 4. Update progress_note table to support outpatient encounters
-- ============================================================================

-- Add outpatient-specific fields if needed
ALTER TABLE clinical_schema.progress_note
ADD COLUMN is_outpatient BOOLEAN DEFAULT false;

CREATE INDEX idx_progress_note_is_outpatient
ON clinical_schema.progress_note(is_outpatient);

COMMENT ON COLUMN clinical_schema.progress_note.is_outpatient IS
'Flag to indicate if this is an outpatient consultation note';

-- ============================================================================
-- 5. Add queue call history table for tracking all queue calls
-- ============================================================================

CREATE TABLE registration_schema.queue_call_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    outpatient_registration_id UUID NOT NULL REFERENCES registration_schema.outpatient_registration(id),
    queue_number INTEGER NOT NULL,
    queue_code VARCHAR(20) NOT NULL,
    called_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    called_by_id UUID,
    called_by_name VARCHAR(100) NOT NULL,
    call_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL', -- NORMAL, RECALL, URGENT
    polyclinic_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    consultation_room VARCHAR(100),
    response_status VARCHAR(20), -- RESPONDED, NO_RESPONSE, SKIPPED
    responded_at TIMESTAMP,
    notes TEXT,
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_queue_call_history_registration
ON registration_schema.queue_call_history(outpatient_registration_id);

CREATE INDEX idx_queue_call_history_called_at
ON registration_schema.queue_call_history(called_at);

CREATE INDEX idx_queue_call_history_polyclinic
ON registration_schema.queue_call_history(polyclinic_id);

COMMENT ON TABLE registration_schema.queue_call_history IS
'History of all queue calls for audit and analytics';

-- ============================================================================
-- 6. Create view for queue dashboard
-- ============================================================================

CREATE OR REPLACE VIEW registration_schema.v_queue_dashboard AS
SELECT
    r.id,
    r.registration_number,
    r.queue_number,
    r.queue_code,
    r.queue_status,
    r.patient_id,
    r.polyclinic_id,
    p.code AS polyclinic_code,
    p.name AS polyclinic_name,
    r.doctor_id,
    r.registration_date,
    r.appointment_time,
    r.check_in_time,
    r.queue_called_at,
    r.queue_serving_started_at,
    r.queue_serving_ended_at,
    r.is_bpjs,
    r.chief_complaint,
    CASE
        WHEN r.queue_serving_started_at IS NOT NULL THEN
            EXTRACT(EPOCH FROM (r.queue_serving_started_at - r.check_in_time))/60
        WHEN r.queue_called_at IS NOT NULL THEN
            EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - r.check_in_time))/60
        ELSE
            EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - r.check_in_time))/60
    END AS wait_time_minutes,
    CASE
        WHEN r.queue_serving_ended_at IS NOT NULL THEN
            EXTRACT(EPOCH FROM (r.queue_serving_ended_at - r.queue_serving_started_at))/60
        WHEN r.queue_serving_started_at IS NOT NULL THEN
            EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - r.queue_serving_started_at))/60
        ELSE NULL
    END AS service_time_minutes
FROM registration_schema.outpatient_registration r
JOIN registration_schema.polyclinic p ON r.polyclinic_id = p.id
WHERE r.registration_date = CURRENT_DATE
  AND r.status NOT IN ('CANCELLED')
ORDER BY r.queue_number;

COMMENT ON VIEW registration_schema.v_queue_dashboard IS
'Real-time queue dashboard view with wait times and service times';

-- ============================================================================
-- 7. Add constraints and validations
-- ============================================================================

-- Ensure queue_status has valid values
ALTER TABLE registration_schema.outpatient_registration
ADD CONSTRAINT chk_queue_status
CHECK (queue_status IN ('WAITING', 'CALLED', 'SERVING', 'COMPLETED', 'SKIPPED', 'CANCELLED'));

-- Ensure call_type has valid values
ALTER TABLE registration_schema.queue_call_history
ADD CONSTRAINT chk_call_type
CHECK (call_type IN ('NORMAL', 'RECALL', 'URGENT'));

-- Ensure response_status has valid values
ALTER TABLE registration_schema.queue_call_history
ADD CONSTRAINT chk_response_status
CHECK (response_status IN ('RESPONDED', 'NO_RESPONSE', 'SKIPPED'));

-- ============================================================================
-- 8. Insert initial data / updates
-- ============================================================================

-- Set default queue_status for existing registrations based on current status
UPDATE registration_schema.outpatient_registration
SET queue_status = CASE
    WHEN status = 'REGISTERED' OR status = 'WAITING' THEN 'WAITING'
    WHEN status = 'IN_CONSULTATION' THEN 'SERVING'
    WHEN status = 'COMPLETED' THEN 'COMPLETED'
    WHEN status = 'CANCELLED' THEN 'CANCELLED'
    ELSE 'WAITING'
END
WHERE queue_status IS NULL;

-- ============================================================================
-- Migration Complete
-- ============================================================================
