-- ============================================================================
-- Flyway Checksum Repair Script
-- Description: Updates V20 migration checksum to match current file
-- Date: 2025-11-20
-- ============================================================================

-- Update the checksum for V20 migration to match the current file
UPDATE flyway_schema_history
SET checksum = 333400169
WHERE version = '20';

-- Verify the update
SELECT version, description, checksum, installed_on
FROM flyway_schema_history
WHERE version IN ('20', '21')
ORDER BY version;

-- Expected result:
-- V20 should have checksum: 333400169
-- V21 should be pending (not yet in table)
