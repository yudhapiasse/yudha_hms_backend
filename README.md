# Hospital Management System (HMS) - Backend

A comprehensive Hospital Management System designed for Indonesian healthcare facilities with full BPJS and SATUSEHAT integration.

## Technology Stack

- **Framework**: Spring Boot 3.4.1
- **Language**: Java 21 LTS
- **Build Tool**: Maven
- **Database**: PostgreSQL 16.6
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Documentation**: SpringDoc OpenAPI

## Project Structure

```
hms-backend/
├── src/
│   ├── main/
│   │   ├── java/com/yudha/hms/
│   │   │   ├── patient/              # Patient Management Module
│   │   │   ├── registration/         # Patient Registration (Outpatient/Inpatient)
│   │   │   ├── clinical/             # Clinical Records (SOAP, Diagnoses, Procedures)
│   │   │   ├── billing/              # Billing & Insurance Claims
│   │   │   ├── pharmacy/             # Pharmacy & Drug Management
│   │   │   ├── laboratory/           # Laboratory Orders & Results
│   │   │   ├── radiology/            # Radiology Orders & PACS Integration
│   │   │   ├── integration/
│   │   │   │   ├── bpjs/             # BPJS Kesehatan Integration
│   │   │   │   └── satusehat/        # SATUSEHAT Platform Integration
│   │   │   ├── shared/               # Shared Utilities & Common Components
│   │   │   └── HmsBackendApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-staging.yml
│   │       └── application-production.yml
│   └── test/
│       └── java/com/yudha/hms/
└── pom.xml
```

## Module Overview

### Core Modules

- **patient**: Patient demographics, medical record numbers, NIK validation
- **registration**: Outpatient/Inpatient registration, appointments
- **clinical**: Clinical documentation (SOAP notes, ICD-10, ICD-9-CM)
- **billing**: Invoicing, payments, insurance claims processing
- **pharmacy**: Drug inventory, e-prescribing, dispensing
- **laboratory**: Lab orders, specimen tracking, results management
- **radiology**: Imaging orders, DICOM integration, PACS connectivity

### Integration Modules

- **integration/bpjs**: VClaim, SEP creation, claims submission
- **integration/satusehat**: FHIR R4 resources, patient/encounter data exchange

### Shared Module

- **shared**: Common utilities, constants, DTOs, exceptions, configurations

## Prerequisites

- Java 21 LTS
- Maven 3.9+
- PostgreSQL 16.6
- IntelliJ IDEA (recommended) or any Java IDE

## Getting Started

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE hms_dev;

-- Create user
CREATE USER hms_user WITH PASSWORD 'hms_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE hms_dev TO hms_user;
```

### 2. Clone and Build

```bash
cd hms-backend
mvn clean install
```

### 3. Run Application

```bash
# Development mode
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. Access Application

- API Base URL: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- API Documentation: http://localhost:8080/swagger-ui.html (after adding SpringDoc)

## Configuration

### Environment Variables

For production, set these environment variables:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hms_production
DB_USERNAME=hms_user
DB_PASSWORD=your_secure_password
JWT_SECRET=your_strong_jwt_secret_key_minimum_256_bits
```

### Profiles

- **dev**: Development environment (localhost, verbose logging)
- **staging**: Staging environment (UAT testing)
- **production**: Production environment (optimized, secure)

## Development Guidelines

### Code Standards

- Follow Java naming conventions
- Use Lombok to reduce boilerplate
- Write meaningful Javadoc comments
- Follow SOLID principles
- Write unit and integration tests (80%+ coverage target)

### Package Structure Convention

Each module follows this structure:

```
module/
├── controller/     # REST API endpoints
├── service/        # Business logic
├── repository/     # Data access layer
├── entity/         # JPA entities (database tables)
└── dto/            # Data Transfer Objects (API contracts)
```

### Indonesian Specific Requirements

- All dates/times use `Asia/Jakarta` timezone
- Currency in Indonesian Rupiah (IDR)
- Support NIK (16-digit national ID) validation
- BPJS integration for national health insurance
- SATUSEHAT integration for health data exchange
- Indonesian language support for error messages and reports

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PatientServiceTest

# Run with coverage
mvn clean verify
```

## Building for Production

```bash
# Build JAR file
mvn clean package -DskipTests

# JAR will be created at: target/hms-backend-1.0.0-SNAPSHOT.jar
```

## Docker Support (Coming Soon)

Docker configuration will be added in Phase 19 of the development guide.

## Security

- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption with BCrypt
- API rate limiting
- SQL injection prevention
- XSS protection

## License

Proprietary - Copyright (c) 2025 Yudha HMS Project

## Contact

For questions or support, please contact the development team.

---

**Note**: This project follows the comprehensive HMS Development Guide. Refer to the guide document for detailed implementation instructions for each phase.