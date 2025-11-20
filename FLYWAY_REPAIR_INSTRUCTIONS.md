# Flyway Migration Checksum Repair Instructions

## Problem
Migration V20 has a checksum mismatch between the database and local file.

```
Migration checksum mismatch for migration version 20
-> Applied to database : 1987744944
-> Resolved locally    : 333400169
```

## Solution Options

### Option 1: Run Flyway Repair via SQL (Recommended)

Connect to your PostgreSQL database and run this SQL command:

```sql
-- Update the checksum for V20 migration
UPDATE flyway_schema_history
SET checksum = 333400169
WHERE version = '20';
```

### Option 2: Temporarily Disable Validation

**Step 1:** Add this to your `application-dev.yml` or `application.yml`:

```yaml
spring:
  flyway:
    validate-on-migrate: false
```

**Step 2:** Start the application once (it will apply V21 and add the version columns)

**Step 3:** Remove the setting and restart

### Option 3: Use Flyway Command Line

If you have Flyway CLI installed:

```bash
flyway -url=jdbc:postgresql://localhost:5432/your_db_name \
       -user=your_username \
       -password=your_password \
       repair
```

### Option 4: Drop and Recreate (Only for Development)

**WARNING: This will delete all data!**

If this is a development environment and you don't mind losing data:

```sql
-- Drop all Phase 4.1 tables
DROP TABLE IF EXISTS clinical_schema.physical_examination CASCADE;
DROP TABLE IF EXISTS clinical_schema.encounter_procedures CASCADE;
DROP TABLE IF EXISTS clinical_schema.clinical_note_templates CASCADE;

-- Delete the V20 migration record
DELETE FROM flyway_schema_history WHERE version = '20';
```

Then restart the application - V20 will be re-applied with the current checksum.

## Verification

After repair, verify the application starts successfully:

```bash
mvn spring-boot:run
```

You should see:
```
✅ Flyway validation successful
✅ Application started successfully
```

## What Happened?

V20 migration was applied to the database, then we modified the file to add `version` columns. Flyway detected this change and prevented execution to maintain migration integrity.

The fix: We created V21 to add the version columns separately, but needed to update V20's checksum in the database to match the current file.

## Prevention

**Best Practice:** Never modify migrations after they've been applied to any environment. Always create new migrations for schema changes.
