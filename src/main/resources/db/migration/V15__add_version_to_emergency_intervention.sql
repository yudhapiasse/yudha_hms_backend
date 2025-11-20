/**
 * Add missing version column to emergency_intervention table
 *
 * This migration fixes the schema validation error caused by the
 * emergency_intervention entity extending AuditableEntity which
 * includes a @Version field for optimistic locking.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */

-- Add version column for optimistic locking if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'registration_schema'
          AND table_name = 'emergency_intervention'
          AND column_name = 'version'
    ) THEN
        ALTER TABLE registration_schema.emergency_intervention
            ADD COLUMN version BIGINT DEFAULT 0;

        RAISE NOTICE 'Added version column to emergency_intervention table';
    ELSE
        RAISE NOTICE 'Version column already exists in emergency_intervention table';
    END IF;
END $$;

COMMENT ON COLUMN registration_schema.emergency_intervention.version IS
    'Optimistic locking version for concurrent update management';
