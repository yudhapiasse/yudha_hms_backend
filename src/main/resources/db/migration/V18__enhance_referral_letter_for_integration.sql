/**
 * Referral Letter Enhancement for Integration
 *
 * Enhances referral letter system with:
 * - Enum-based referral types, status, and urgency
 * - Physical examination and anamnesis fields
 * - BPJS VClaim integration tracking
 * - PCare integration tracking
 * - QR code for verification
 * - Complete workflow support
 *
 * Related Specification: 3.4.6 Referral Letter Generation
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */

-- ============================================================================
-- 1. Add New Clinical Information Fields
-- ============================================================================

-- Add anamnesis and physical examination
ALTER TABLE clinical_schema.referral_letter
    ADD COLUMN IF NOT EXISTS anamnesis TEXT,
    ADD COLUMN IF NOT EXISTS physical_examination TEXT;

COMMENT ON COLUMN clinical_schema.referral_letter.anamnesis IS
    'Patient history and complaints (required)';

COMMENT ON COLUMN clinical_schema.referral_letter.physical_examination IS
    'Physical examination findings (required)';

-- ============================================================================
-- 2. Add Urgency Column (Enum)
-- ============================================================================

ALTER TABLE clinical_schema.referral_letter
    ADD COLUMN IF NOT EXISTS urgency VARCHAR(20);

COMMENT ON COLUMN clinical_schema.referral_letter.urgency IS
    'Urgency level: ROUTINE, URGENT, EMERGENCY';

-- Migrate existing urgency_level data if exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'clinical_schema'
               AND table_name = 'referral_letter'
               AND column_name = 'urgency_level') THEN
        UPDATE clinical_schema.referral_letter
        SET urgency = urgency_level
        WHERE urgency IS NULL AND urgency_level IS NOT NULL;
    END IF;
END $$;

-- ============================================================================
-- 3. Add Integration Tracking Fields
-- ============================================================================

-- BPJS VClaim Integration
ALTER TABLE clinical_schema.referral_letter
    ADD COLUMN IF NOT EXISTS bpjs_vclaim_submitted BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS bpjs_vclaim_submission_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS bpjs_vclaim_reference_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS bpjs_vclaim_response TEXT;

COMMENT ON COLUMN clinical_schema.referral_letter.bpjs_vclaim_submitted IS
    'Indicates if referral has been submitted to BPJS VClaim system';

-- PCare Integration
ALTER TABLE clinical_schema.referral_letter
    ADD COLUMN IF NOT EXISTS pcare_submitted BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS pcare_submission_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS pcare_reference_number VARCHAR(100);

COMMENT ON COLUMN clinical_schema.referral_letter.pcare_submitted IS
    'Indicates if referral has been submitted to PCare system';

-- ============================================================================
-- 4. Add QR Code Fields
-- ============================================================================

ALTER TABLE clinical_schema.referral_letter
    ADD COLUMN IF NOT EXISTS qr_code VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS qr_code_url VARCHAR(500);

COMMENT ON COLUMN clinical_schema.referral_letter.qr_code IS
    'QR code data for referral verification';

COMMENT ON COLUMN clinical_schema.referral_letter.qr_code_url IS
    'URL to QR code image for referral verification';

-- ============================================================================
-- 5. Create Indexes for Performance
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_referral_urgency
    ON clinical_schema.referral_letter(urgency);

CREATE INDEX IF NOT EXISTS idx_referral_bpjs
    ON clinical_schema.referral_letter(is_bpjs_referral)
    WHERE is_bpjs_referral = TRUE;

CREATE INDEX IF NOT EXISTS idx_referral_vclaim_pending
    ON clinical_schema.referral_letter(bpjs_vclaim_submitted)
    WHERE is_bpjs_referral = TRUE AND bpjs_vclaim_submitted = FALSE;

CREATE INDEX IF NOT EXISTS idx_referral_pcare_pending
    ON clinical_schema.referral_letter(pcare_submitted)
    WHERE pcare_submitted = FALSE;

CREATE INDEX IF NOT EXISTS idx_referral_created_at
    ON clinical_schema.referral_letter(referral_created_at);

-- ============================================================================
-- 6. Create Referral Summary View
-- ============================================================================

CREATE OR REPLACE VIEW clinical_schema.v_referral_summary AS
SELECT
    rl.id,
    rl.referral_number,
    rl.encounter_id,
    rl.patient_id,
    rl.referral_type,
    rl.referral_status,
    rl.urgency,
    rl.referral_date,
    rl.valid_until,

    -- Source
    rl.referring_facility,
    rl.referring_department,
    rl.referring_doctor_name,

    -- Destination
    rl.referred_to_facility,
    rl.referred_to_department,
    rl.referred_to_specialty,

    -- Status
    rl.signed,
    rl.document_generated,
    rl.referral_accepted,

    -- Integration Status
    rl.is_bpjs_referral,
    rl.bpjs_vclaim_submitted,
    rl.pcare_submitted,
    rl.satusehat_submitted,

    -- Flags
    (rl.referral_status = 'DRAFT' OR
     rl.referral_status = 'PENDING_SIGNATURE' OR
     rl.referral_status = 'SIGNED' OR
     rl.referral_status = 'SENT') as is_pending,
    (rl.referral_status = 'ACCEPTED') as is_accepted,
    (rl.referral_status = 'COMPLETED') as is_completed,
    (rl.referral_status IN ('REJECTED', 'CANCELLED')) as is_terminated,
    (rl.valid_until IS NOT NULL AND rl.valid_until < CURRENT_DATE) as is_expired,

    -- Audit
    rl.created_at,
    rl.updated_at

FROM clinical_schema.referral_letter rl;

COMMENT ON VIEW clinical_schema.v_referral_summary IS
    'Summary view of referral letters with status flags';

-- ============================================================================
-- 7. Create Referral Metrics Function
-- ============================================================================

CREATE OR REPLACE FUNCTION clinical_schema.calculate_referral_metrics(
    p_start_date DATE,
    p_end_date DATE,
    p_referral_type VARCHAR DEFAULT NULL
)
RETURNS TABLE (
    total_referrals BIGINT,
    internal_referrals BIGINT,
    external_referrals BIGINT,
    bpjs_referrals BIGINT,
    emergency_referrals BIGINT,
    pending_signature BIGINT,
    signed_referrals BIGINT,
    accepted_referrals BIGINT,
    completed_referrals BIGINT,
    rejected_referrals BIGINT,
    avg_acceptance_time_hours NUMERIC,
    vclaim_submission_rate NUMERIC,
    pcare_submission_rate NUMERIC,
    satusehat_submission_rate NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::BIGINT as total_referrals,
        COUNT(*) FILTER (WHERE referral_type = 'INTERNAL')::BIGINT as internal_referrals,
        COUNT(*) FILTER (WHERE referral_type IN ('EXTERNAL', 'BPJS_SPECIALIST', 'BPJS_ADVANCED'))::BIGINT as external_referrals,
        COUNT(*) FILTER (WHERE is_bpjs_referral = TRUE)::BIGINT as bpjs_referrals,
        COUNT(*) FILTER (WHERE urgency = 'EMERGENCY')::BIGINT as emergency_referrals,
        COUNT(*) FILTER (WHERE referral_status = 'PENDING_SIGNATURE')::BIGINT as pending_signature,
        COUNT(*) FILTER (WHERE signed = TRUE)::BIGINT as signed_referrals,
        COUNT(*) FILTER (WHERE referral_status = 'ACCEPTED')::BIGINT as accepted_referrals,
        COUNT(*) FILTER (WHERE referral_status = 'COMPLETED')::BIGINT as completed_referrals,
        COUNT(*) FILTER (WHERE referral_status = 'REJECTED')::BIGINT as rejected_referrals,

        ROUND(AVG(
            CASE
                WHEN acceptance_date IS NOT NULL AND referral_created_at IS NOT NULL THEN
                    EXTRACT(EPOCH FROM (acceptance_date - referral_created_at)) / 3600
                ELSE NULL
            END
        ), 2) as avg_acceptance_time_hours,

        ROUND(
            100.0 * COUNT(*) FILTER (WHERE bpjs_vclaim_submitted = TRUE) /
            NULLIF(COUNT(*) FILTER (WHERE is_bpjs_referral = TRUE), 0),
            1
        ) as vclaim_submission_rate,

        ROUND(
            100.0 * COUNT(*) FILTER (WHERE pcare_submitted = TRUE) / NULLIF(COUNT(*), 0),
            1
        ) as pcare_submission_rate,

        ROUND(
            100.0 * COUNT(*) FILTER (WHERE satusehat_submitted = TRUE) / NULLIF(COUNT(*), 0),
            1
        ) as satusehat_submission_rate

    FROM clinical_schema.referral_letter
    WHERE referral_date BETWEEN p_start_date AND p_end_date
      AND (p_referral_type IS NULL OR referral_type = p_referral_type);
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION clinical_schema.calculate_referral_metrics IS
    'Calculate comprehensive referral metrics for a date range and optional type filter';

-- ============================================================================
-- Migration Complete
-- ============================================================================

DO $$
BEGIN
    RAISE NOTICE 'Referral Letter Enhancement Migration Completed';
    RAISE NOTICE 'Added: Anamnesis, Physical Examination, Urgency, Integration tracking';
    RAISE NOTICE 'Added: QR Code fields for verification';
    RAISE NOTICE 'Created: Referral summary view and metrics function';
END $$;
