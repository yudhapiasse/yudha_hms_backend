/**
 * Department Transfer Workflow Enhancements
 *
 * Adds support for complete department transfer workflow including:
 * - Department and location ID references
 * - Approval workflow for ICU/special care transfers
 * - Transferring and receiving practitioner tracking
 * - Enhanced transfer status management
 *
 * Related Specification: 3.4.4 Department Transfer Management
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */

-- ============================================================================
-- 1. Add Department and Location ID References
-- ============================================================================

-- Add department ID columns (UUID references to department master if exists)
ALTER TABLE clinical_schema.department_transfer
    ADD COLUMN IF NOT EXISTS from_department_id UUID,
    ADD COLUMN IF NOT EXISTS to_department_id UUID;

COMMENT ON COLUMN clinical_schema.department_transfer.from_department_id IS
    'Reference to source department (if using department master table)';

COMMENT ON COLUMN clinical_schema.department_transfer.to_department_id IS
    'Reference to destination department (if using department master table)';

-- Add location ID columns (UUID references to bed/room master if exists)
ALTER TABLE clinical_schema.department_transfer
    ADD COLUMN IF NOT EXISTS from_location_id UUID,
    ADD COLUMN IF NOT EXISTS to_location_id UUID;

COMMENT ON COLUMN clinical_schema.department_transfer.from_location_id IS
    'Reference to source bed/room location (if using location master table)';

COMMENT ON COLUMN clinical_schema.department_transfer.to_location_id IS
    'Reference to destination bed/room location (if using location master table)';

-- ============================================================================
-- 2. Add Practitioner References
-- ============================================================================

-- Add transferring practitioner (doctor sending the patient)
ALTER TABLE clinical_schema.department_transfer
    ADD COLUMN IF NOT EXISTS transferring_practitioner_id UUID,
    ADD COLUMN IF NOT EXISTS transferring_practitioner_name VARCHAR(200);

COMMENT ON COLUMN clinical_schema.department_transfer.transferring_practitioner_id IS
    'Doctor or practitioner who is transferring the patient';

-- Add receiving practitioner (doctor receiving the patient)
ALTER TABLE clinical_schema.department_transfer
    ADD COLUMN IF NOT EXISTS receiving_practitioner_id UUID,
    ADD COLUMN IF NOT EXISTS receiving_practitioner_name VARCHAR(200);

COMMENT ON COLUMN clinical_schema.department_transfer.receiving_practitioner_id IS
    'Doctor or practitioner who is receiving the patient';

-- ============================================================================
-- 3. Add Approval Workflow Fields
-- ============================================================================

-- Add approval fields for ICU and special care transfers
ALTER TABLE clinical_schema.department_transfer
    ADD COLUMN IF NOT EXISTS requires_approval BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS approved_by_id UUID,
    ADD COLUMN IF NOT EXISTS approved_by_name VARCHAR(200),
    ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS approval_notes TEXT;

COMMENT ON COLUMN clinical_schema.department_transfer.requires_approval IS
    'Flag indicating if this transfer requires supervisor/specialist approval';

COMMENT ON COLUMN clinical_schema.department_transfer.approved_by_id IS
    'ID of supervisor or specialist who approved the transfer';

COMMENT ON COLUMN clinical_schema.department_transfer.approved_at IS
    'Timestamp when transfer was approved';

-- ============================================================================
-- 4. Create Indexes for Performance
-- ============================================================================

-- Index for department transfers by department
CREATE INDEX IF NOT EXISTS idx_transfer_from_dept
    ON clinical_schema.department_transfer(from_department_id);

CREATE INDEX IF NOT EXISTS idx_transfer_to_dept
    ON clinical_schema.department_transfer(to_department_id);

-- Index for location transfers
CREATE INDEX IF NOT EXISTS idx_transfer_from_location
    ON clinical_schema.department_transfer(from_location_id);

CREATE INDEX IF NOT EXISTS idx_transfer_to_location
    ON clinical_schema.department_transfer(to_location_id);

-- Index for practitioner lookups
CREATE INDEX IF NOT EXISTS idx_transfer_transferring_pract
    ON clinical_schema.department_transfer(transferring_practitioner_id);

CREATE INDEX IF NOT EXISTS idx_transfer_receiving_pract
    ON clinical_schema.department_transfer(receiving_practitioner_id);

-- Index for approval workflow
CREATE INDEX IF NOT EXISTS idx_transfer_pending_approval
    ON clinical_schema.department_transfer(requires_approval, transfer_status)
    WHERE requires_approval = TRUE;

CREATE INDEX IF NOT EXISTS idx_transfer_approved_by
    ON clinical_schema.department_transfer(approved_by_id)
    WHERE approved_by_id IS NOT NULL;

-- ============================================================================
-- 5. Create Transfer Summary View
-- ============================================================================

CREATE OR REPLACE VIEW clinical_schema.v_department_transfer_summary AS
SELECT
    dt.id,
    dt.transfer_number,
    dt.encounter_id,
    dt.patient_id,
    dt.transfer_type,
    dt.transfer_status,
    dt.requires_approval,

    -- Source Information
    dt.from_department_id,
    dt.from_department,
    dt.from_location_id,
    dt.from_location,

    -- Destination Information
    dt.to_department_id,
    dt.to_department,
    dt.to_location_id,
    dt.to_location,

    -- Timeline
    dt.transfer_requested_at,
    dt.approved_at,
    dt.transfer_accepted_at,
    dt.transfer_completed_at,

    -- Calculate durations
    CASE
        WHEN dt.transfer_completed_at IS NOT NULL THEN
            EXTRACT(EPOCH FROM (dt.transfer_completed_at - dt.transfer_requested_at)) / 60
        ELSE NULL
    END as total_transfer_duration_minutes,

    CASE
        WHEN dt.approved_at IS NOT NULL AND dt.transfer_requested_at IS NOT NULL THEN
            EXTRACT(EPOCH FROM (dt.approved_at - dt.transfer_requested_at)) / 60
        ELSE NULL
    END as approval_duration_minutes,

    -- Practitioners
    dt.transferring_practitioner_id,
    dt.transferring_practitioner_name,
    dt.receiving_practitioner_id,
    dt.receiving_practitioner_name,
    dt.approved_by_id,
    dt.approved_by_name,

    -- Flags
    dt.urgency,
    (dt.transfer_status IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED', 'IN_TRANSIT')) as is_active,
    (dt.transfer_status = 'COMPLETED') as is_completed,
    (dt.transfer_status IN ('REJECTED', 'CANCELLED')) as is_terminated,

    -- Audit
    dt.created_at,
    dt.updated_at

FROM clinical_schema.department_transfer dt;

COMMENT ON VIEW clinical_schema.v_department_transfer_summary IS
    'Summary view of department transfers with calculated durations and flags';

-- ============================================================================
-- 6. Create Transfer Statistics Function
-- ============================================================================

CREATE OR REPLACE FUNCTION clinical_schema.calculate_transfer_metrics(
    p_start_date DATE,
    p_end_date DATE,
    p_department_id UUID DEFAULT NULL
)
RETURNS TABLE (
    total_transfers BIGINT,
    completed_transfers BIGINT,
    pending_transfers BIGINT,
    cancelled_transfers BIGINT,
    transfers_requiring_approval BIGINT,
    approved_transfers BIGINT,
    avg_transfer_duration_minutes NUMERIC,
    avg_approval_duration_minutes NUMERIC,
    urgent_transfers BIGINT,
    emergency_transfers BIGINT,
    icu_transfers BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::BIGINT as total_transfers,
        COUNT(*) FILTER (WHERE transfer_status = 'COMPLETED')::BIGINT as completed_transfers,
        COUNT(*) FILTER (WHERE transfer_status IN ('REQUESTED', 'PENDING_APPROVAL', 'APPROVED', 'ACCEPTED'))::BIGINT as pending_transfers,
        COUNT(*) FILTER (WHERE transfer_status IN ('CANCELLED', 'REJECTED'))::BIGINT as cancelled_transfers,
        COUNT(*) FILTER (WHERE requires_approval = TRUE)::BIGINT as transfers_requiring_approval,
        COUNT(*) FILTER (WHERE approved_at IS NOT NULL)::BIGINT as approved_transfers,

        ROUND(AVG(
            CASE
                WHEN transfer_completed_at IS NOT NULL THEN
                    EXTRACT(EPOCH FROM (transfer_completed_at - transfer_requested_at)) / 60
                ELSE NULL
            END
        ), 1) as avg_transfer_duration_minutes,

        ROUND(AVG(
            CASE
                WHEN approved_at IS NOT NULL THEN
                    EXTRACT(EPOCH FROM (approved_at - transfer_requested_at)) / 60
                ELSE NULL
            END
        ), 1) as avg_approval_duration_minutes,

        COUNT(*) FILTER (WHERE urgency = 'URGENT')::BIGINT as urgent_transfers,
        COUNT(*) FILTER (WHERE urgency = 'EMERGENCY' OR transfer_type = 'EMERGENCY')::BIGINT as emergency_transfers,
        COUNT(*) FILTER (WHERE transfer_type IN ('ICU_ADMISSION', 'ICU_DISCHARGE', 'STEP_UP'))::BIGINT as icu_transfers

    FROM clinical_schema.department_transfer
    WHERE transfer_requested_at::DATE BETWEEN p_start_date AND p_end_date
      AND (p_department_id IS NULL OR from_department_id = p_department_id OR to_department_id = p_department_id);
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION clinical_schema.calculate_transfer_metrics IS
    'Calculate comprehensive transfer metrics for a date range and optional department';

-- ============================================================================
-- Migration Complete
-- ============================================================================

-- Verification
DO $$
BEGIN
    RAISE NOTICE 'Department Transfer Workflow Enhancement Migration Completed';
    RAISE NOTICE 'Added: Department/Location ID references, Approval workflow, Practitioner tracking';
    RAISE NOTICE 'Created: Transfer summary view and metrics function';
END $$;
