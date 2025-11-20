# E-Klaim 5.10.x Integration

Complete implementation of Indonesian E-Klaim Web Service integration for INA-CBGs (Indonesian Case-Based Groups) claim processing.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [E-Klaim Workflow](#e-klaim-workflow)
- [Security](#security)
- [Error Handling](#error-handling)
- [Audit Logging](#audit-logging)
- [Development](#development)

## Overview

E-Klaim (Electronic Klaim) is the Indonesian Ministry of Health's web service for processing healthcare claims under the INA-CBGs (Indonesia Case Based Groups) system. This integration enables hospitals to:

- Create and manage claims electronically
- Perform iDRG and INA-CBGs grouping
- Submit claims to BPJS Kesehatan
- Monitor claim status and payment
- Handle TB cases through SITB integration

### System Requirements

- Java 17+
- Spring Boot 3.x
- PostgreSQL 14+
- BPJS E-Klaim credentials (cons_id, secret_key, user_key)

### E-Klaim Versions

- **Current**: E-Klaim 5.10.x
- **API Base URL**:
  - Development: `https://dvlp.bpjs-kesehatan.go.id:9081/eklaim-ws/ws/`
  - Production: `https://api.bpjs-kesehatan.go.id/eklaim-ws/ws/`

## Architecture

### Package Structure

```
com.yudha.hms.integration.eklaim/
├── config/                     # Spring configurations
│   └── EklaimRestTemplateConfig.java
├── controller/                 # REST controllers
│   └── EklaimController.java
├── dto/                        # Data Transfer Objects
│   ├── EklaimBaseRequest.java
│   ├── EklaimBaseResponse.java
│   ├── NewClaimRequest.java
│   ├── ClaimDataRequest.java
│   ├── DiagnosisRequest.java
│   ├── ProcedureRequest.java
│   ├── GrouperRequest.java
│   └── GrouperResponse.java
├── entity/                     # JPA entities
│   ├── EklaimClaim.java
│   ├── EklaimAuditLog.java
│   └── EklaimConfig.java
├── exception/                  # Custom exceptions
│   ├── EklaimIntegrationException.java
│   ├── EklaimEncryptionException.java
│   ├── EklaimAuthenticationException.java
│   ├── EklaimHttpException.java
│   └── EklaimValidationException.java
├── repository/                 # JPA repositories
│   ├── EklaimClaimRepository.java
│   ├── EklaimAuditLogRepository.java
│   └── EklaimConfigRepository.java
└── service/                    # Business logic
    ├── EklaimEncryptionService.java
    ├── EklaimHttpClient.java
    ├── EklaimClaimService.java
    └── EklaimAuditService.java
```

### Database Schema

Three main tables:

1. **eklaim_config** - Configuration per hospital
2. **eklaim_claims** - Claim data and status
3. **eklaim_audit_logs** - Audit trail (5-year retention)

## Features

### ✅ Implemented

- ✅ **Claim Management**
  - Create new claim (new_claim)
  - Set claim data (set_claim_data)
- ✅ **Clinical Data Entry**
  - Set diagnoses - ICD-10 (diagnosa_set)
  - Set procedures - ICD-9-CM (procedure_set)
- ✅ **Grouping**
  - iDRG grouper (grouper_1)
  - INA-CBGs grouper (grouper_2)
- ✅ **Security**
  - AES-256-CBC encryption with HMAC-SHA256 signature
  - Rate limiting (100 requests/minute default)
  - Request/response encryption
- ✅ **Audit Logging**
  - Complete audit trail for all operations
  - Request/response logging
  - Data modification tracking
  - 5-year retention policy compliance
- ✅ **Error Handling**
  - E-Klaim error code mapping (E2001-E2020)
  - Ungroupable error handling (36.xxxx series)
  - Retry logic with exponential backoff

### 🚧 To Be Implemented

The following 28 methods need implementation:

- Get claim data (get_claim_data)
- Delete claim (delete_claim)
- Reedit claim (reedit_claim)
- Finalize grouper (grouper_final)
- Special CMG options (special_cmg_option)
- Add prosthesis (claim_prosthesis)
- Finalize claim (claim_final)
- Submit claim (send_claim_individual)
- Resubmit claim (send_claim_reconsider)
- Print claim (claim_print)
- Get claim status (get_claim_status)
- Batch monitoring (monitoring_klaim)
- Get plafon data (get_data_plafon)
- Payment batch (claim_ba)
- JKN monitoring (pantauan_jkn)
- SITB methods (7 methods for TB cases)

## Configuration

### Application Properties

Add to `application.yml`:

```yaml
eklaim:
  enabled: true
  environment: development  # or production
  rate-limit-per-minute: 100
  timeout-seconds: 30
  max-retry-attempts: 3
```

### Database Configuration

Configure E-Klaim credentials in the `eklaim_config` table:

```sql
INSERT INTO eklaim_config (
    hospital_code,
    base_url,
    cons_id,
    secret_key,
    user_key,
    is_active,
    is_production
) VALUES (
    '1234',
    'https://dvlp.bpjs-kesehatan.go.id:9081/eklaim-ws/ws/',
    'YOUR_CONS_ID',
    'YOUR_SECRET_KEY_IN_HEX', -- 64 hex characters
    'YOUR_USER_KEY',
    TRUE,
    FALSE
);
```

**Important**:
- `secret_key` must be 64 hexadecimal characters (256-bit key)
- Credentials obtained from BPJS Kesehatan
- Store credentials securely (consider encryption at rest)

## API Endpoints

All endpoints require JWT authentication.

### Claim Management

```http
POST   /api/v1/eklaim/claims                     # Create new claim
PUT    /api/v1/eklaim/claims/{claimNumber}/data  # Set claim data
```

### Clinical Data

```http
POST   /api/v1/eklaim/claims/{claimNumber}/diagnoses   # Set diagnoses
POST   /api/v1/eklaim/claims/{claimNumber}/procedures  # Set procedures
```

### Grouping

```http
POST   /api/v1/eklaim/claims/{claimNumber}/grouper     # Execute grouper
```

### Examples

#### 1. Create New Claim

```bash
curl -X POST http://localhost:8080/api/v1/eklaim/claims \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "data": {
      "nomor_sep": "12340001234567",
      "hospital_code": "1234"
    }
  }'
```

#### 2. Set Diagnoses

```bash
curl -X POST http://localhost:8080/api/v1/eklaim/claims/CLAIM123/diagnoses \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "data": {
      "claim_number": "CLAIM123",
      "diagnoses": [
        {
          "code": "I10",
          "type": "1",
          "level": "2"
        }
      ]
    }
  }'
```

#### 3. Execute Grouper

```bash
curl -X POST http://localhost:8080/api/v1/eklaim/claims/CLAIM123/grouper \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "data": {
      "claim_number": "CLAIM123",
      "grouper_type": "1"
    }
  }'
```

## E-Klaim Workflow

### Standard Claim Lifecycle

```
1. NEW_CLAIM (Status: 1 - Draft)
   ↓
2. SET_CLAIM_DATA (Status: 2 - Ungrouped)
   ↓
3. DIAGNOSA_SET & PROCEDURE_SET
   ↓
4. GROUPER_1 (iDRG) → (Status: 3 - iDRG Grouped)
   ↓
5. GROUPER_2 (INACBG) → (Status: 4 - INACBG Grouped)
   ↓
6. CLAIM_FINAL (Status: 5 - Finalized)
   ↓
7. SEND_CLAIM_INDIVIDUAL (Status: 6 - Submitted)
   ↓
8. VERIFICATION → (Status: 7 - Verified)
   ↓
9. CLAIM_BA → (Status: 8 - Approved with BA)
```

### Claim Status Values

| Status | Description           | E-Klaim Method         |
|--------|-----------------------|------------------------|
| 1      | Draft                 | new_claim              |
| 2      | Ungrouped             | set_claim_data         |
| 3      | iDRG Grouped          | grouper_1              |
| 4      | INACBG Grouped        | grouper_2              |
| 5      | Finalized             | claim_final            |
| 6      | Submitted             | send_claim_individual  |
| 7      | Verified              | (from BPJS)            |
| 8      | Approved with BA      | claim_ba               |
| 9      | Rejected              | (from BPJS)            |
| 10     | Resubmitted           | send_claim_reconsider  |

## Security

### Encryption Protocol

E-Klaim uses AES-256-CBC encryption with HMAC-SHA256 signature:

**Encryption Format**:
```
[HMAC Signature (10 bytes)][IV (16 bytes)][Encrypted Data]
```

**Process**:
1. Encrypt request with AES-256-CBC
2. Generate HMAC-SHA256 signature
3. Combine: signature (first 10 bytes) + IV + encrypted data
4. Base64 encode
5. Send to E-Klaim API

**Decryption Process**:
1. Base64 decode
2. Extract signature, IV, and encrypted data
3. Verify HMAC signature
4. Decrypt with AES-256-CBC
5. Parse JSON response

### Rate Limiting

- Default: 100 requests/minute per hospital
- Sliding window algorithm
- 429 status code on rate limit exceeded
- Configurable per hospital

### Authentication

- Header: `X-cons-id` - Consumer ID
- Header: `X-user-key` - User authentication key
- All requests must include both headers

## Error Handling

### E-Klaim Error Codes

| Code   | Description                                    | HTTP Status |
|--------|------------------------------------------------|-------------|
| E2001  | Encryption/decryption failure                  | 500         |
| E2002  | Invalid signature - data integrity compromised | 400         |
| E2003  | Consumer ID not found or inactive              | 401         |
| E2004  | User key authentication failed                 | 401         |
| E2005  | Invalid claim_number reference                 | 404         |
| E2006  | Claim already finalized - cannot edit          | 400         |
| E2007  | Required field missing                         | 400         |
| E2008  | Invalid ICD-10 diagnosis code                  | 400         |
| E2009  | Invalid ICD-9-CM procedure code                | 400         |
| E2010  | Duplicate diagnosis entry                      | 409         |
| E2011  | Duplicate procedure entry                      | 409         |
| E2012  | Grouping failed - incomplete data              | 400         |
| E2013  | iDRG grouping required before INACBG           | 400         |
| E2014  | SITB validation incomplete (TB cases)          | 400         |
| E2015  | SEP number not found in VClaim                 | 404         |
| E2016  | SEP already used for another claim             | 409         |
| E2017  | Claim date outside SEP validity period         | 400         |
| E2018  | Hospital not authorized for service type       | 403         |
| E2019  | Patient class mismatch with SEP                | 400         |
| E2020  | Invalid tariff calculation                     | 400         |

### Ungroupable Errors (36.xxxx)

Claims that cannot be grouped due to data inconsistencies:

- `36.0000` - Incomplete diagnosis information
- `36.0001` - Invalid age for diagnosis
- `36.0002` - Gender mismatch with diagnosis
- `36.0003` - Length of stay inconsistent with diagnosis
- `36.0004` - Procedure not related to diagnosis
- `36.0005` - Missing required secondary diagnosis
- `36.0006` - Invalid principal diagnosis for admission type
- `36.0007` - Diagnosis-procedure combination not allowed
- `36.0008` - Birth weight missing for neonatal case
- `36.0009` - Invalid admission source
- `36.0010` - Missing required laboratory results

## Audit Logging

All E-Klaim operations are logged for compliance and troubleshooting.

### Audit Data Captured

- **API Calls**: Request/response (encrypted and decrypted)
- **User Actions**: Who, when, from where
- **Data Modifications**: Old and new values
- **Errors**: Complete error details
- **Performance**: Execution time tracking

### Retention Policy

- **Minimum**: 5 years (Indonesian regulatory requirement)
- **Cleanup**: Automated job recommended
- **Access**: Restricted to authorized personnel

### Query Audit Logs

```java
// Get claim audit trail
List<EklaimAuditLog> trail = auditService.getClaimAuditTrail(claimId);

// Get failed requests
List<EklaimAuditLog> failures = auditService.getFailedRequests();

// Get logs by date range
List<EklaimAuditLog> logs = auditService.getAuditLogsByDateRange(
    startDate,
    endDate
);
```

## Development

### Running Locally

1. **Database Setup**:
```bash
# Run migration
./mvnw flyway:migrate
```

2. **Configure E-Klaim**:
```sql
-- Insert test configuration (use development endpoint)
INSERT INTO eklaim_config (...) VALUES (...);
```

3. **Start Application**:
```bash
./mvnw spring-boot:run
```

### Testing

```bash
# Run all tests
./mvnw test

# Test encryption
./mvnw test -Dtest=EklaimEncryptionServiceTest

# Test HTTP client
./mvnw test -Dtest=EklaimHttpClientTest
```

### Adding New E-Klaim Methods

To implement remaining 28 methods, follow this pattern:

1. **Create DTO** in `dto/` package
2. **Add Service Method** in `EklaimClaimService.java`:
```java
@Transactional
public ResponseType methodName(RequestType request, User user) {
    // 1. Get and validate claim
    // 2. Call E-Klaim API via httpClient
    // 3. Update local claim status
    // 4. Create audit log
    // 5. Return result
}
```
3. **Add Controller Endpoint** in `EklaimController.java`
4. **Add Tests**

### Troubleshooting

#### Encryption Errors (E2001, E2002)

- Verify secret_key is exactly 64 hex characters
- Check key format in database
- Validate encryption test: `encryptionService.validateConfiguration(config)`

#### Authentication Errors (E2003, E2004)

- Verify cons_id and user_key with BPJS
- Check credentials not expired
- Ensure environment (dev/prod) matches credentials

#### Rate Limit Exceeded (429)

- Check `rate_limit_per_minute` in configuration
- Monitor request frequency
- Implement request queuing if needed

## References

- [E-Klaim Official Documentation](https://bpjs-kesehatan.go.id/eklaim)
- [INA-CBGs Grouper Manual](https://inacbg.bpjs-kesehatan.go.id/)
- [BPJS Web Service Documentation](https://dvlp.bpjs-kesehatan.go.id/katalog)

## License

Copyright © 2025 HMS Development Team. All rights reserved.

---

**Version**: 1.0.0
**Last Updated**: 2025-01-20
**Maintainer**: HMS Development Team
