-- =============================================================================
-- Add Missing Columns to SATUSEHAT Resource Mappings
-- Version: 1.0.1
-- Date: 2025-01-20
-- Description: Adds organization_id, resource_type, and retry_count columns
--              to support Phase 7.2 Patient Resource Management
-- =============================================================================

-- Add missing columns
ALTER TABLE satusehat_resource_mappings
    ADD COLUMN IF NOT EXISTS organization_id VARCHAR(100),
    ADD COLUMN IF NOT EXISTS resource_type VARCHAR(50),
    ADD COLUMN IF NOT EXISTS retry_count INTEGER DEFAULT 0;

-- Make satusehat_resource_id nullable (it's null during pending submissions)
ALTER TABLE satusehat_resource_mappings
    ALTER COLUMN satusehat_resource_id DROP NOT NULL;

-- Make satusehat_resource_type nullable (it's null during initial creation)
ALTER TABLE satusehat_resource_mappings
    ALTER COLUMN satusehat_resource_type DROP NOT NULL;

-- Add comments
COMMENT ON COLUMN satusehat_resource_mappings.organization_id IS 'Organization identifier for multi-tenant support';
COMMENT ON COLUMN satusehat_resource_mappings.resource_type IS 'FHIR resource type (Patient, Encounter, etc.) - simplified for queries';
COMMENT ON COLUMN satusehat_resource_mappings.retry_count IS 'Number of retry attempts for failed submissions';

-- Add indexes for the new columns
CREATE INDEX IF NOT EXISTS idx_satusehat_mapping_org ON satusehat_resource_mappings(organization_id);
CREATE INDEX IF NOT EXISTS idx_satusehat_mapping_resource_type ON satusehat_resource_mappings(resource_type);
CREATE INDEX IF NOT EXISTS idx_satusehat_mapping_org_resource_status ON satusehat_resource_mappings(organization_id, resource_type, submission_status);

-- Backfill resource_type from local_resource_type for existing data
UPDATE satusehat_resource_mappings
SET resource_type = local_resource_type
WHERE resource_type IS NULL;

-- =============================================================================
-- End of Migration
-- =============================================================================
