

# HMS Database Configuration Guide

## Overview

The HMS Backend uses **PostgreSQL 16.6** with a multi-schema architecture for modular organization. Database versioning is managed using **Flyway 10.x** for automated migrations.

## Architecture

### Multi-Schema Design

The database is organized into multiple schemas for logical separation:

| Schema | Purpose | Tables |
|--------|---------|--------|
| `public` | Default schema, Flyway metadata | flyway_schema_history |
| `master_schema` | Reference/lookup data | provinces, cities, ICD-10, drug catalog |
| `patient_schema` | Patient demographics | patient, patient_address, emergency_contact |
| `registration_schema` | Registration workflows | registration, appointment, bed_assignment |
| `clinical_schema` | Clinical documentation | clinical_note, diagnosis, vital_signs |
| `billing_schema` | Financial operations | invoice, payment, claims |
| `pharmacy_schema` | Pharmacy operations | drug, prescription, dispensing |
| `laboratory_schema` | Lab operations | lab_order, lab_result, specimen |
| `radiology_schema` | Imaging operations | radiology_order, imaging_study |
| `integration_schema` | External integrations | bpjs_sync, satusehat_sync |

### Benefits of Multi-Schema Design

✅ **Modular Organization**: Clear separation of concerns
✅ **Access Control**: Schema-level permissions
✅ **Backup Strategy**: Schema-specific backups
✅ **Performance**: Reduced table name conflicts
✅ **Scalability**: Easier to extract to separate databases

---

## Prerequisites

### 1. PostgreSQL 16.6 Installation

**macOS (Homebrew):**
```bash
brew install postgresql@16
brew services start postgresql@16
```

**Ubuntu/Debian:**
```bash
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget -qO- https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo tee /etc/apt/trusted.gpg.d/pgdg.asc
sudo apt update
sudo apt install postgresql-16 postgresql-contrib-16
```

**Windows:**
Download from: https://www.postgresql.org/download/windows/

### 2. Verify PostgreSQL Version

```bash
psql --version
# Expected: psql (PostgreSQL) 16.x
```

---

## Initial Database Setup

### Step 1: Create Database and User

Run the initialization script as PostgreSQL superuser:

```bash
# Navigate to database directory
cd "/Volumes/Data001/Hospital MS Dev/hms-backend/database"

# Run initialization script
psql -U postgres -f 00_init_database.sql
```

This script will:
1. Create `hms_dev` database
2. Create `hms_user` application user
3. Install PostgreSQL extensions (uuid-ossp, pgcrypto, pg_trgm, unaccent)
4. Create all schemas (master_schema, patient_schema, etc.)
5. Grant privileges to `hms_user`
6. Create audit trigger function

### Step 2: Verify Database Setup

```bash
psql -U hms_user -d hms_dev

# List schemas
\dn+

# Expected output:
#   public
#   master_schema
#   patient_schema
#   registration_schema
#   clinical_schema
#   billing_schema
#   pharmacy_schema
#   laboratory_schema
#   radiology_schema
#   integration_schema

# List extensions
\dx

# Expected extensions:
#   uuid-ossp
#   pgcrypto
#   pg_trgm
#   unaccent

# Exit
\q
```

---

## Flyway Database Migrations

### How Flyway Works

Flyway automatically runs SQL migration scripts on application startup. Migrations are versioned and executed in order.

**Migration Files Location:**
```
src/main/resources/db/migration/
├── V1__create_schemas.sql
├── V2__create_master_tables.sql
├── V3__create_patient_tables.sql
├── V4__create_registration_and_clinical_tables.sql
└── ... (additional migrations)
```

**Naming Convention:**
- `V{version}__{description}.sql`
- Example: `V1__create_schemas.sql`
- Version must be unique and sequential
- Underscores in description become spaces

### Migration Details

#### V1: Create Schemas
- Creates all database schemas
- Creates audit trigger function
- **Run automatically** on first application startup

#### V2: Create Master Tables
- Indonesian geographic data (provinces, cities, districts, villages)
- Medical classifications (ICD-10, ICD-9-CM)
- Indonesian reference data (religions, marital status, blood types, education, occupation)
- Hospital organization (departments, polyclinics)
- BPJS reference data
- **Pre-populated** with Indonesian-specific data

#### V3: Create Patient Tables
- `patient` - Core patient demographics with NIK and BPJS
- `patient_address` - KTP and domicile addresses
- `emergency_contact` - Emergency contact information
- `patient_allergy` - Patient allergies
- `mrn_sequence` - Medical record number generation
- **Indonesian fields**: NIK, BPJS, religion, RT/RW

#### V4: Create Registration and Clinical Tables
- `registration` - Outpatient/Inpatient/Emergency registration
- `clinical_note` - SOAP format clinical documentation
- `diagnosis` - ICD-10 diagnoses
- `vital_signs` - Vital signs measurements
- **Indonesian features**: SEP number, rujukan, SATUSEHAT integration

### Manual Migration Execution

If you need to run migrations manually:

```bash
# Using Maven
mvn flyway:migrate

# Using Maven with specific profile
mvn flyway:migrate -Dspring.profiles.active=dev

# View migration history
mvn flyway:info

# Clean database (CAUTION: Removes all data!)
mvn flyway:clean
```

---

## Configuration

### application.yml (Main Configuration)

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
    schemas: public
    validate-on-migrate: true
    out-of-order: false

  datasource:
    hikari:
      maximum-pool-size: 20      # Adjust based on load
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: HMSHikariPool

  threads:
    virtual:
      enabled: true  # Java 21 virtual threads
```

### application-dev.yml (Development)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hms_dev
    username: hms_user
    password: hms_password

  flyway:
    enabled: true
    clean-disabled: false  # Allow clean in dev
```

### application-production.yml (Production)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 50      # Higher for production
      minimum-idle: 20

  flyway:
    enabled: true
    clean-disabled: true  # NEVER clean in production
```

---

## HikariCP Connection Pooling

### Optimized for 200+ Concurrent Users

**Development:**
- Maximum pool size: 20
- Minimum idle: 5
- Connection timeout: 30 seconds

**Production:**
- Maximum pool size: 50 (adjust based on database server capacity)
- Minimum idle: 20
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes
- Leak detection: 60 seconds

### Sizing Formula

```
max_pool_size = ((core_count * 2) + effective_spindle_count)
```

For web applications:
```
max_pool_size = Number of expected concurrent users / 2
```

Example: 200 concurrent users → pool size of 50-100

### Connection Pooling with Virtual Threads

Java 21 virtual threads improve connection handling:
- Better utilization of connection pool
- Reduced thread blocking on database operations
- Higher concurrency with same pool size

Enabled in `application.yml`:
```yaml
spring:
  threads:
    virtual:
      enabled: true
```

---

## Database Performance Optimization

### Indexes

All tables include appropriate indexes:
- **Primary keys**: UUID with B-tree index
- **Foreign keys**: Indexed for joins
- **Search fields**: Full-text search indexes (pg_trgm)
- **Date ranges**: Indexed for time-based queries
- **Status fields**: Indexed for filtering

### Example Indexes (Patient Table)

```sql
-- Unique indexes
CREATE UNIQUE INDEX idx_patient_mrn ON patient(mrn);
CREATE UNIQUE INDEX idx_patient_nik ON patient(nik);

-- Regular indexes
CREATE INDEX idx_patient_name ON patient(full_name);
CREATE INDEX idx_patient_birth_date ON patient(birth_date);

-- Full-text search
CREATE INDEX idx_patient_name_trgm ON patient
  USING gin(full_name gin_trgm_ops);
```

### Query Optimization

1. **Always use indexes** for search queries
2. **Limit result sets** with pagination
3. **Use JPA fetch strategies** wisely
4. **Enable query caching** where appropriate
5. **Monitor slow queries** with pg_stat_statements

---

## Backup and Recovery

### Daily Backup Script

```bash
#!/bin/bash
# backup_hms_db.sh

BACKUP_DIR="/var/backups/hms"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="hms_dev"

# Create backup directory
mkdir -p $BACKUP_DIR

# Full database backup
pg_dump -U hms_user -F c -b -v -f "$BACKUP_DIR/hms_full_$DATE.backup" $DB_NAME

# Schema-specific backups
pg_dump -U hms_user -n patient_schema -F c -f "$BACKUP_DIR/patient_schema_$DATE.backup" $DB_NAME
pg_dump -U hms_user -n clinical_schema -F c -f "$BACKUP_DIR/clinical_schema_$DATE.backup" $DB_NAME

# Remove backups older than 30 days
find $BACKUP_DIR -type f -name "*.backup" -mtime +30 -delete

echo "Backup completed: $DATE"
```

### Restore from Backup

```bash
# Restore full database
pg_restore -U hms_user -d hms_dev -c /var/backups/hms/hms_full_20250118.backup

# Restore specific schema
pg_restore -U hms_user -d hms_dev -n patient_schema /var/backups/hms/patient_schema_20250118.backup
```

---

## Monitoring

### Query Performance

```sql
-- Enable pg_stat_statements extension
CREATE EXTENSION pg_stat_statements;

-- View slow queries
SELECT
    query,
    calls,
    total_exec_time / 1000 as total_time_sec,
    mean_exec_time / 1000 as mean_time_sec,
    max_exec_time / 1000 as max_time_sec
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 20;
```

### Connection Monitoring

```sql
-- Active connections
SELECT
    datname,
    count(*) as connections,
    max(state) as state
FROM pg_stat_activity
WHERE datname = 'hms_dev'
GROUP BY datname;

-- Long-running queries
SELECT
    pid,
    now() - query_start as duration,
    state,
    query
FROM pg_stat_activity
WHERE state = 'active'
AND query_start < now() - interval '5 minutes'
ORDER BY duration DESC;
```

### Database Size

```sql
-- Database size
SELECT pg_size_pretty(pg_database_size('hms_dev'));

-- Schema sizes
SELECT
    schemaname,
    pg_size_pretty(sum(pg_total_relation_size(schemaname||'.'||tablename))::bigint) as size
FROM pg_tables
WHERE schemaname NOT IN ('pg_catalog', 'information_schema')
GROUP BY schemaname
ORDER BY sum(pg_total_relation_size(schemaname||'.'||tablename)) DESC;
```

---

## Troubleshooting

### Issue: Flyway Migration Fails

**Symptoms:**
```
FlywayException: Validate failed: Migration checksum mismatch
```

**Solution:**
```bash
# Option 1: Repair Flyway metadata
mvn flyway:repair

# Option 2: Baseline and retry (dev only)
mvn flyway:baseline
mvn flyway:migrate
```

### Issue: Connection Pool Exhausted

**Symptoms:**
```
HikariPool - Connection is not available, request timed out after 30000ms
```

**Solution:**
1. Increase pool size in application.yml
2. Check for connection leaks in code
3. Monitor long-running transactions
4. Enable leak detection:
   ```yaml
   spring:
     datasource:
       hikari:
         leak-detection-threshold: 60000
   ```

### Issue: Slow Queries

**Symptoms:**
- Application response time > 2 seconds
- High database CPU usage

**Solution:**
1. Check missing indexes
2. Analyze query execution plans
3. Review N+1 query problems in JPA
4. Enable query logging:
   ```yaml
   logging:
     level:
       org.hibernate.SQL: DEBUG
       org.hibernate.type.descriptor.sql.BasicBinder: TRACE
   ```

---

## Security Best Practices

### 1. Password Management
- ❌ NEVER commit real passwords to git
- ✅ Use environment variables in production
- ✅ Rotate passwords regularly
- ✅ Use strong passwords (min 16 chars)

### 2. Access Control
```sql
-- Grant minimum required privileges
GRANT CONNECT ON DATABASE hms_dev TO hms_user;
GRANT USAGE ON SCHEMA patient_schema TO hms_user;
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA patient_schema TO hms_user;

-- Revoke DELETE for audit trail (use soft deletes)
REVOKE DELETE ON ALL TABLES IN SCHEMA patient_schema FROM hms_user;
```

### 3. Encryption
- Enable SSL/TLS for database connections
- Use pgcrypto for sensitive data
- Encrypt backups

### 4. Audit Trail
- All tables have created_at, updated_at, created_by, updated_by
- Use soft deletes (deleted_at) instead of hard deletes
- Log all DDL changes

---

## Next Steps

After database setup is complete:

1. ✅ Verify all schemas created
2. ✅ Verify all extensions installed
3. ✅ Run application (Flyway will migrate tables)
4. ✅ Check Flyway migration history: `mvn flyway:info`
5. ✅ Start implementing JPA entities for Phase 2 (Patient Management)

---

## Reference

### Flyway Commands

| Command | Purpose |
|---------|---------|
| `mvn flyway:migrate` | Run pending migrations |
| `mvn flyway:info` | Show migration status |
| `mvn flyway:validate` | Validate applied migrations |
| `mvn flyway:baseline` | Baseline existing database |
| `mvn flyway:repair` | Repair metadata table |
| `mvn flyway:clean` | Drop all objects (DANGER!) |

### PostgreSQL Extensions

| Extension | Purpose |
|-----------|---------|
| uuid-ossp | UUID generation (primary keys) |
| pgcrypto | Cryptographic functions |
| pg_trgm | Trigram matching (full-text search) |
| unaccent | Text normalization |

### Connection String Format

```
jdbc:postgresql://<host>:<port>/<database>?<params>
```

Example:
```
jdbc:postgresql://localhost:5432/hms_dev?ssl=true&sslmode=require
```

---

**Database setup is now complete!** The HMS application is ready for development.

For questions or issues, refer to:
- PostgreSQL Documentation: https://www.postgresql.org/docs/16/
- Flyway Documentation: https://flywaydb.org/documentation/
- HikariCP Configuration: https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby