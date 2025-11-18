# HMS Backend - Modular Monolith Architecture Guide

## Overview

The HMS Backend is built using a **Modular Monolith** architecture pattern. This approach combines the benefits of both monolithic and microservices architectures, providing:

- **Modular Organization**: Clear separation of concerns into functional modules
- **Simplified Deployment**: Single deployable artifact (JAR file)
- **Easier Development**: No distributed system complexity during development
- **Future-Ready**: Can be split into microservices if needed
- **Better Performance**: No network latency between modules

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      HMS Backend Application                     │
│                     (Spring Boot 3.4.1 + Java 21)               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Patient    │  │ Registration │  │   Clinical   │          │
│  │   Module     │  │    Module    │  │    Module    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Billing    │  │   Pharmacy   │  │  Laboratory  │          │
│  │   Module     │  │    Module    │  │    Module    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                  │
│  ┌──────────────┐  ┌───────────────────────────────┐           │
│  │  Radiology   │  │   Integration Modules         │           │
│  │   Module     │  │  ┌─────────┐  ┌─────────────┐ │           │
│  └──────────────┘  │  │  BPJS   │  │ SATUSEHAT   │ │           │
│                    │  └─────────┘  └─────────────┘ │           │
│                    └───────────────────────────────┘           │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Shared Module (Common Utilities)             │  │
│  │   Config │ Constants │ DTOs │ Exceptions │ Utilities     │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
├─────────────────────────────────────────────────────────────────┤
│                    Spring Data JPA / Hibernate                  │
├─────────────────────────────────────────────────────────────────┤
│                      PostgreSQL 16.6 Database                   │
└─────────────────────────────────────────────────────────────────┘
```

## Module Structure

Each module follows a consistent **layered architecture** pattern:

```
module/
├── controller/     # REST API Layer
│   └── XxxController.java
├── service/        # Business Logic Layer
│   ├── XxxService.java (interface)
│   └── XxxServiceImpl.java (implementation)
├── repository/     # Data Access Layer
│   └── XxxRepository.java (extends JpaRepository)
├── entity/         # Domain Model / JPA Entities
│   └── Xxx.java (@Entity)
└── dto/            # Data Transfer Objects
    ├── XxxRequestDto.java
    ├── XxxResponseDto.java
    └── XxxSearchCriteria.java
```

## Core Modules

### 1. Patient Module
**Package**: `com.yudha.hms.patient`

**Responsibilities**:
- Patient registration and demographics
- NIK validation (16-digit Indonesian national ID)
- Medical record number (MRN) generation
- Patient search and retrieval
- Patient photo management
- Emergency contact management

**Key Entities**: Patient, EmergencyContact, PatientAddress

**Dependencies**: Shared Module, Integration/DUKCAPIL (future)

---

### 2. Registration Module
**Package**: `com.yudha.hms.registration`

**Responsibilities**:
- Outpatient (Rawat Jalan) registration
- Inpatient (Rawat Inap) admission
- Emergency room (IGD) registration
- Appointment scheduling
- Queue management
- Bed assignment

**Key Entities**: Registration, Appointment, BedAssignment, Queue

**Dependencies**: Patient Module, BPJS Module (for SEP), Shared Module

---

### 3. Clinical Module
**Package**: `com.yudha.hms.clinical`

**Responsibilities**:
- SOAP notes documentation
- ICD-10 diagnosis coding
- ICD-9-CM procedure coding
- Vital signs recording
- Clinical orders
- Progress notes
- Discharge summaries

**Key Entities**: ClinicalNote, Diagnosis, Procedure, VitalSigns, ClinicalOrder

**Dependencies**: Registration Module, Patient Module, Shared Module

---

### 4. Billing Module
**Package**: `com.yudha.hms.billing`

**Responsibilities**:
- Invoice generation
- Payment processing
- BPJS claims management
- Tariff management
- Discount and packages
- Payment reconciliation

**Key Entities**: Invoice, Payment, Claim, Tariff, InvoiceItem

**Dependencies**: Registration Module, Clinical Module, BPJS Module, Shared Module

---

### 5. Pharmacy Module
**Package**: `com.yudha.hms.pharmacy`

**Responsibilities**:
- Drug inventory management
- Electronic prescribing
- Drug dispensing
- Stock management (FIFO/FEFO)
- Drug interaction checking
- Controlled substance tracking

**Key Entities**: Drug, Prescription, Dispensing, Stock, DrugInteraction

**Dependencies**: Clinical Module, Patient Module, Shared Module

---

### 6. Laboratory Module
**Package**: `com.yudha.hms.laboratory`

**Responsibilities**:
- Lab test ordering
- Specimen tracking
- Result entry and validation
- LIS integration
- Critical value alerts
- Quality control

**Key Entities**: LabOrder, LabResult, Specimen, LabTest, ReferenceRange

**Dependencies**: Clinical Module, Registration Module, Shared Module

---

### 7. Radiology Module
**Package**: `com.yudha.hms.radiology`

**Responsibilities**:
- Radiology exam ordering
- PACS integration
- DICOM integration
- Radiology reports
- Image storage
- Modality scheduling

**Key Entities**: RadiologyOrder, RadiologyReport, ImagingStudy, Modality

**Dependencies**: Clinical Module, Registration Module, Shared Module

---

## Integration Modules

### 8. BPJS Integration Module
**Package**: `com.yudha.hms.integration.bpjs`

**Responsibilities**:
- VClaim web service integration
- SEP (eligibility letter) creation
- BPJS claim submission
- Participant verification
- Rujukan (referral) management
- INA-CBGs grouper integration

**Key Entities**: SEP, BpjsClaim, Rujukan, BpjsParticipant

**External Systems**: BPJS VClaim API, Aplicare, P-Care

**Dependencies**: Registration Module, Billing Module, Clinical Module, Shared Module

---

### 9. SATUSEHAT Integration Module
**Package**: `com.yudha.hms.integration.satusehat`

**Responsibilities**:
- FHIR R4 resource creation
- Patient data submission
- Encounter reporting
- Lab/radiology result submission
- Medication tracking
- OAuth 2.0 authentication

**Key Entities**: SatusehatSync, FhirResource, SubmissionLog

**External Systems**: SATUSEHAT FHIR API (Ministry of Health)

**Dependencies**: All clinical modules, Shared Module

---

## Shared Module

### 10. Shared Module
**Package**: `com.yudha.hms.shared`

**Sub-packages**:

#### a. Config (`com.yudha.hms.shared.config`)
- **SecurityConfig**: JWT authentication, CORS, authorization
- **DatabaseConfig**: JPA, Hibernate, transaction management
- **JacksonConfig**: JSON serialization, date/time formatting
- **AsyncConfig**: Async task execution
- **CacheConfig**: Redis caching configuration
- **AuditConfig**: JPA auditing, created/updated timestamps

#### b. Constants (`com.yudha.hms.shared.constant`)
- **IndonesiaConstant**: Provinces, cities, religions
- **MedicalConstant**: Blood types, marital status
- **BpjsConstant**: Claim types, SEP types
- **ApiConstant**: API endpoints, versions
- **ErrorCode**: Standardized error codes

#### c. DTOs (`com.yudha.hms.shared.dto`)
- **ApiResponse<T>**: Standardized API response wrapper
- **PageResponse<T>**: Pagination response
- **ErrorResponse**: Error details
- **SearchCriteria**: Common search parameters

#### d. Exceptions (`com.yudha.hms.shared.exception`)
- **GlobalExceptionHandler**: Centralized exception handling
- **NotFoundException**: Resource not found
- **ValidationException**: Business validation errors
- **IntegrationException**: External system failures
- **UnauthorizedException**: Authentication/authorization errors

#### e. Utilities (`com.yudha.hms.shared.util`)
- **NikValidator**: NIK validation and parsing
- **MrnGenerator**: Medical record number generation
- **BarcodeGenerator**: Barcode/QR code generation
- **DateUtil**: Indonesian timezone date handling
- **CurrencyUtil**: IDR formatting
- **PdfGenerator**: PDF report generation
- **ExcelGenerator**: Excel report generation

---

## Module Dependencies

### Dependency Graph

```
┌─────────────────────────────────────────────────┐
│              Shared Module                      │
│  (Used by all modules - zero dependencies)      │
└─────────────────────────────────────────────────┘
                        ▲
                        │
        ┌───────────────┼───────────────┐
        │               │               │
┌───────┴─────┐ ┌──────┴──────┐ ┌─────┴──────┐
│   Patient   │ │ Registration│ │  Clinical   │
│   Module    │ │   Module    │ │   Module    │
└─────────────┘ └─────────────┘ └─────────────┘
        │               │               │
        └───────┬───────┴───────┬───────┘
                │               │
        ┌───────┴─────┐ ┌──────┴──────┐
        │   Billing   │ │  Pharmacy   │
        │   Module    │ │   Module    │
        └─────────────┘ └─────────────┘
                │
        ┌───────┴───────────────┐
        │                       │
┌───────┴─────┐         ┌──────┴────────┐
│    BPJS     │         │  SATUSEHAT    │
│ Integration │         │  Integration  │
└─────────────┘         └───────────────┘
```

### Dependency Rules

1. **Shared Module**: Has ZERO dependencies on other modules
2. **Core Modules**: Can depend on Shared Module and Patient Module
3. **Integration Modules**: Can depend on any core module
4. **No Circular Dependencies**: Strictly enforced
5. **Loose Coupling**: Modules communicate via interfaces

---

## Communication Between Modules

### 1. Direct Method Calls (Synchronous)
```java
// Example: Registration module calling Patient service
@Service
public class RegistrationServiceImpl {
    @Autowired
    private PatientService patientService; // From Patient module

    public Registration createRegistration(RegistrationRequest request) {
        Patient patient = patientService.findByMrn(request.getMrn());
        // ... registration logic
    }
}
```

### 2. Spring Events (Asynchronous)
```java
// Example: Publishing event when registration is created
@Service
public class RegistrationServiceImpl {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Registration create(RegistrationRequest request) {
        Registration registration = // ... create registration
        eventPublisher.publishEvent(new RegistrationCreatedEvent(registration));
        return registration;
    }
}

// Other modules can listen
@Component
public class BpjsEventListener {
    @EventListener
    public void handleRegistration(RegistrationCreatedEvent event) {
        // Create SEP automatically
    }
}
```

### 3. Message Queue (Future Enhancement)
```java
// For critical integrations like BPJS, SATUSEHAT
// RabbitMQ or Kafka for guaranteed delivery
```

---

## Module Independence Guidelines

### DO's ✅

1. **Each module should**:
   - Have clear, well-defined responsibilities
   - Expose clean interfaces (service interfaces)
   - Handle its own validation logic
   - Manage its own database entities
   - Have comprehensive unit tests

2. **Module communication**:
   - Use service interfaces, not implementations
   - Use DTOs for data exchange
   - Emit events for cross-module notifications
   - Document all public APIs

### DON'Ts ❌

1. **Never**:
   - Access another module's repository directly
   - Create circular dependencies
   - Expose entities outside the module
   - Share mutable state between modules
   - Bypass service layer

---

## Database Schema Organization

Each module has its own tables with a consistent naming convention:

```sql
-- Patient Module
patient
patient_address
patient_emergency_contact

-- Registration Module
registration
appointment
bed_assignment
queue

-- Clinical Module
clinical_note
diagnosis
procedure
vital_signs

-- Billing Module
invoice
invoice_item
payment
claim

-- etc.
```

**Foreign Keys**: Allowed across module tables (managed carefully)

**Schemas**: Can use PostgreSQL schemas for logical separation:
- `master_schema`: Master data
- `patient_schema`: Patient module
- `clinical_schema`: Clinical module
- `billing_schema`: Billing module
- `integration_schema`: Integration tracking

---

## Testing Strategy

### 1. Unit Tests
- Test each service method independently
- Mock dependencies from other modules
- 80%+ code coverage target

### 2. Integration Tests
- Test module interactions
- Use @SpringBootTest with test database
- Test REST API endpoints

### 3. Module Isolation Tests
- Verify module boundaries
- Check dependency rules
- Use ArchUnit for architecture testing

---

## Migration to Microservices (Future)

If needed, modules can be extracted into microservices:

1. **Phase 1**: Convert module to separate Spring Boot app
2. **Phase 2**: Change method calls to REST/gRPC calls
3. **Phase 3**: Separate database per service
4. **Phase 4**: Add service mesh, API gateway

**Candidates for Early Extraction**:
- Integration modules (BPJS, SATUSEHAT)
- Laboratory module (can be standalone)
- Radiology module (PACS integration)

---

## Best Practices

### 1. Module Development
- Start with interfaces, then implement
- Write tests first (TDD)
- Use DTOs for all external communication
- Document public APIs with Javadoc

### 2. Code Organization
- Keep related code together in the module
- Use package-private for internal classes
- Expose only necessary classes as public
- Follow consistent naming conventions

### 3. Database Access
- Each module manages its own entities
- Use repository pattern consistently
- Implement soft deletes for audit trail
- Use optimistic locking for concurrency

### 4. Error Handling
- Use custom exceptions from shared module
- Log errors with proper context
- Return meaningful error messages
- Never expose stack traces to clients

### 5. Security
- Secure endpoints with Spring Security
- Validate all inputs
- Sanitize outputs
- Use parameterized queries (JPA does this)

---

## Development Workflow

1. **Start with Patient Module** (Phase 2 of guide)
2. **Add Registration Module** (Phase 3)
3. **Implement Clinical Module** (Phase 4)
4. **Add BPJS Integration** (Phase 5)
5. **Continue with other modules** as per guide

**Each module should be**:
- ✅ Fully implemented
- ✅ Fully tested
- ✅ Documented
- ✅ Reviewed

Before moving to the next module.

---

## Conclusion

The modular monolith architecture provides:
- **Clear organization** of complex HMS system
- **Team scalability** (different teams can work on different modules)
- **Code reusability** through shared module
- **Easier testing** with isolated modules
- **Future flexibility** for microservices migration

Follow the HMS Development Guide and implement modules incrementally for best results!

---

**Need help?** Refer to individual module `package-info.java` files for detailed documentation.
