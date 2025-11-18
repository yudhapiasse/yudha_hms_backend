# HMS Entity Guide - Base Entity Classes

## Overview

The HMS application provides three base entity classes that handle common entity requirements:
1. **BaseEntity** - Basic entity with UUID and timestamps
2. **AuditableEntity** - Adds user auditing and optimistic locking
3. **SoftDeletableEntity** - Adds soft delete functionality

## Entity Hierarchy

```
┌─────────────────────────────────────┐
│         BaseEntity (abstract)       │
│  - UUID id                          │
│  - LocalDateTime createdAt          │
│  - LocalDateTime updatedAt          │
│  - equals/hashCode/toString         │
└─────────────────┬───────────────────┘
                  │
                  │ extends
                  │
┌─────────────────▼───────────────────┐
│     AuditableEntity (abstract)      │
│  + String createdBy                 │
│  + String updatedBy                 │
│  + Long version (optimistic lock)   │
└─────────────────┬───────────────────┘
                  │
                  │ extends
                  │
┌─────────────────▼───────────────────┐
│  SoftDeletableEntity (abstract)     │
│  + LocalDateTime deletedAt          │
│  + String deletedBy                 │
│  + softDelete() / restore()         │
└─────────────────────────────────────┘
```

## When to Use Each Base Class

### 1. BaseEntity
**Use when:** You only need basic entity fields (ID and timestamps)

**Provides:**
- ✅ UUID primary key (auto-generated)
- ✅ `createdAt` timestamp (auto-set on insert)
- ✅ `updatedAt` timestamp (auto-updated)
- ✅ `equals()`, `hashCode()`, `toString()` implementations
- ✅ `isNew()` helper method

**Example:**
```java
@Entity
@Table(name = "blood_type", schema = "master_schema")
public class BloodType extends BaseEntity {
    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    // Getters and setters
}
```

**When NOT to use:**
- If you need to track who created/updated the record → Use **AuditableEntity**
- If you need soft delete → Use **SoftDeletableEntity**

---

### 2. AuditableEntity
**Use when:** You need full audit trail and optimistic locking

**Provides:**
- ✅ Everything from BaseEntity
- ✅ `createdBy` - username who created the record
- ✅ `updatedBy` - username who last updated
- ✅ `version` - for optimistic locking (prevents lost updates)

**Example:**
```java
@Entity
@Table(name = "tariff", schema = "billing_schema")
@Getter
@Setter
@NoArgsConstructor
public class Tariff extends AuditableEntity {
    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private BigDecimal price;
}
```

**When NOT to use:**
- If you need soft delete → Use **SoftDeletableEntity**
- If you don't care about user auditing → Use **BaseEntity**

---

### 3. SoftDeletableEntity (RECOMMENDED)
**Use when:** You need audit trail AND soft delete (most entities)

**Provides:**
- ✅ Everything from AuditableEntity
- ✅ `deletedAt` - timestamp when deleted
- ✅ `deletedBy` - username who deleted
- ✅ `softDelete(username)` method
- ✅ `restore()` method
- ✅ `isDeleted()` / `isActive()` helper methods
- ✅ Automatic filtering of deleted records
- ✅ Automatic soft delete on `repository.delete()`

**Example:**
```java
@Entity
@Table(name = "patient", schema = "patient_schema")
@Getter
@Setter
@NoArgsConstructor
public class Patient extends SoftDeletableEntity {
    @Column(name = "mrn", unique = true, nullable = false)
    private String mrn;

    @Column(name = "nik", unique = true)
    private String nik;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;
}
```

**When to use:**
- ✅ **ALL medical records** (required by Indonesian law)
- ✅ Patient data
- ✅ Clinical data (notes, diagnoses, procedures)
- ✅ Billing data (invoices, payments)
- ✅ Any data that needs recovery capability

**When NOT to use:**
- Simple lookup/master data that never needs recovery
- Temporary/cache data

---

## Field Reference

| Field | Type | In Class | Set By | Updatable | Description |
|-------|------|----------|---------|-----------|-------------|
| `id` | UUID | BaseEntity | JPA | ❌ No | Primary key (auto-generated) |
| `createdAt` | LocalDateTime | BaseEntity | @PrePersist | ❌ No | Creation timestamp |
| `updatedAt` | LocalDateTime | BaseEntity | @PrePersist, @PreUpdate | ✅ Yes | Last update timestamp |
| `createdBy` | String | AuditableEntity | Spring Auditing | ❌ No | Username who created |
| `updatedBy` | String | AuditableEntity | Spring Auditing | ✅ Yes | Username who last updated |
| `version` | Long | AuditableEntity | JPA | ✅ Yes (auto) | Optimistic lock version |
| `deletedAt` | LocalDateTime | SoftDeletableEntity | Manual/@SQLDelete | ✅ Yes | Soft delete timestamp |
| `deletedBy` | String | SoftDeletableEntity | Manual | ✅ Yes | Username who deleted |

---

## Usage Examples

### Creating an Entity

```java
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public Patient createPatient(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setMrn(generateMRN());
        patient.setNik(dto.getNik());
        patient.setFullName(dto.getFullName());
        patient.setBirthDate(dto.getBirthDate());

        // Save - audit fields populated automatically
        return patientRepository.save(patient);

        // After save:
        // - id: generated UUID
        // - createdAt: current timestamp
        // - updatedAt: current timestamp
        // - createdBy: current username (from Security context)
        // - updatedBy: current username
        // - version: 0
        // - deletedAt: null
        // - deletedBy: null
    }
}
```

### Updating an Entity

```java
public Patient updatePatient(UUID id, PatientDTO dto) {
    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Patient not found"));

    patient.setFullName(dto.getFullName());
    patient.setBirthDate(dto.getBirthDate());

    // Save - updatedAt and updatedBy updated automatically
    return patientRepository.save(patient);

    // After save:
    // - updatedAt: new timestamp
    // - updatedBy: current username
    // - version: incremented (e.g., 0 → 1)
}
```

### Soft Deleting - Method 1 (Manual)

```java
public void deletePatient(UUID id, String deletedBy) {
    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Patient not found"));

    // Manual soft delete
    patient.softDelete(deletedBy);
    patientRepository.save(patient);

    // After save:
    // - deletedAt: current timestamp
    // - deletedBy: provided username
}
```

### Soft Deleting - Method 2 (Repository delete)

```java
public void deletePatient(UUID id) {
    Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Patient not found"));

    // Repository delete - automatically soft deletes
    patientRepository.delete(patient);
    // @SQLDelete annotation sets deletedAt to current timestamp
}
```

### Restoring a Soft-Deleted Entity

```java
public Patient restorePatient(UUID id) {
    // Need custom query to find deleted records
    Patient patient = patientRepository.findByIdIncludingDeleted(id)
        .orElseThrow(() -> new NotFoundException("Patient not found"));

    if (!patient.isDeleted()) {
        throw new BadRequestException("Patient is not deleted");
    }

    patient.restore();
    return patientRepository.save(patient);

    // After save:
    // - deletedAt: null
    // - deletedBy: null
}
```

### Handling Optimistic Lock Exception

```java
@Service
public class PatientService {

    public Patient updatePatient(UUID id, PatientDTO dto) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Patient not found"));

                patient.setFullName(dto.getFullName());

                return patientRepository.save(patient);

            } catch (OptimisticLockException e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new ConcurrentModificationException(
                        "Record was modified by another user. Please refresh and try again."
                    );
                }
                // Retry with fresh data
            }
        }

        throw new RuntimeException("Update failed after retries");
    }
}
```

---

## Repository Considerations

### Finding Including Deleted Records

The `@Where(clause = "deleted_at IS NULL")` annotation automatically filters deleted records.

To find including deleted, create custom repository method:

```java
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    // Default methods exclude deleted records (due to @Where clause)
    Optional<Patient> findById(UUID id); // Excludes deleted
    List<Patient> findAll(); // Excludes deleted

    // Custom query to include deleted records
    @Query("SELECT p FROM Patient p WHERE p.id = :id")
    Optional<Patient> findByIdIncludingDeleted(@Param("id") UUID id);

    // Find only deleted records
    @Query("SELECT p FROM Patient p WHERE p.deletedAt IS NOT NULL")
    List<Patient> findAllDeleted();

    // Check if soft deleted
    @Query("SELECT CASE WHEN p.deletedAt IS NOT NULL THEN true ELSE false END FROM Patient p WHERE p.id = :id")
    boolean isDeleted(@Param("id") UUID id);
}
```

---

## Best Practices

### 1. Choose the Right Base Class

```java
// ❌ BAD: Using BaseEntity for patient (no audit trail, no soft delete)
@Entity
public class Patient extends BaseEntity { }

// ✅ GOOD: Using SoftDeletableEntity for patient
@Entity
public class Patient extends SoftDeletableEntity { }

// ✅ GOOD: Using AuditableEntity for tariff (audit but no soft delete needed)
@Entity
public class Tariff extends AuditableEntity { }

// ✅ GOOD: Using BaseEntity for simple lookup
@Entity
public class BloodType extends BaseEntity { }
```

### 2. Never Hard Delete Medical Records

```java
// ❌ BAD: Hard delete (data lost forever, illegal for medical records)
patientRepository.deleteById(id);

// ✅ GOOD: Soft delete (data preserved, recoverable)
patient.softDelete("admin");
patientRepository.save(patient);
```

### 3. Handle Concurrent Updates

```java
// ✅ GOOD: Implement retry logic for optimistic lock
try {
    patientRepository.save(patient);
} catch (OptimisticLockException e) {
    // Reload fresh data and retry
    patient = patientRepository.findById(id).get();
    patient.setFullName(newName);
    patientRepository.save(patient);
}
```

### 4. Use Lombok for Entity Code

```java
@Entity
@Table(name = "patient", schema = "patient_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends SoftDeletableEntity {
    // Fields only, Lombok generates getters/setters
}
```

### 5. Always Include Schema in @Table

```java
// ✅ GOOD: Explicit schema
@Table(name = "patient", schema = "patient_schema")

// ❌ BAD: No schema (defaults to public)
@Table(name = "patient")
```

---

## Testing Audit Fields

### Unit Test Example

```java
@SpringBootTest
@Transactional
class PatientAuditTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    @WithMockUser(username = "test_user")
    void testAuditFieldsOnCreate() {
        // Given
        Patient patient = new Patient();
        patient.setMrn("202501-00001");
        patient.setFullName("Test Patient");

        // When
        Patient saved = patientRepository.save(patient);

        // Then
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals("test_user", saved.getCreatedBy());
        assertEquals("test_user", saved.getUpdatedBy());
        assertEquals(0L, saved.getVersion());
        assertNull(saved.getDeletedAt());
        assertNull(saved.getDeletedBy());
    }

    @Test
    @WithMockUser(username = "test_user")
    void testAuditFieldsOnUpdate() {
        // Given
        Patient patient = createAndSavePatient();
        LocalDateTime originalCreatedAt = patient.getCreatedAt();
        String originalCreatedBy = patient.getCreatedBy();

        // When
        patient.setFullName("Updated Name");
        Patient updated = patientRepository.save(patient);

        // Then
        assertEquals(originalCreatedAt, updated.getCreatedAt());
        assertEquals(originalCreatedBy, updated.getCreatedBy());
        assertTrue(updated.getUpdatedAt().isAfter(originalCreatedAt));
        assertEquals("test_user", updated.getUpdatedBy());
        assertEquals(1L, updated.getVersion());
    }

    @Test
    @WithMockUser(username = "test_user")
    void testSoftDelete() {
        // Given
        Patient patient = createAndSavePatient();
        UUID id = patient.getId();

        // When
        patient.softDelete("admin");
        patientRepository.save(patient);
        patientRepository.flush();

        // Then
        Optional<Patient> found = patientRepository.findById(id);
        assertTrue(found.isEmpty()); // Not found (filtered by @Where)

        // But can be found with custom query
        Optional<Patient> foundIncludingDeleted =
            patientRepository.findByIdIncludingDeleted(id);
        assertTrue(foundIncludingDeleted.isPresent());
        assertTrue(foundIncludingDeleted.get().isDeleted());
        assertEquals("admin", foundIncludingDeleted.get().getDeletedBy());
    }
}
```

---

## Summary

| Base Class | Use For | Provides |
|------------|---------|----------|
| **BaseEntity** | Simple lookup data | UUID, timestamps |
| **AuditableEntity** | Master data needing audit | + user tracking, optimistic lock |
| **SoftDeletableEntity** | Medical/critical data | + soft delete, recovery |

**Default recommendation:** Use **SoftDeletableEntity** for all entities unless you have a specific reason not to.

---

## Next Steps

1. ✅ Base entity classes are ready
2. ✅ JPA auditing is configured
3. ✅ Start creating actual entities (Patient, Registration, etc.)
4. ✅ Follow Phase 2 of the HMS Development Guide

For questions or issues, refer to the package documentation in `package-info.java`.