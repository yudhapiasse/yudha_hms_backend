/**
 * Base Entity Classes for HMS Application.
 *
 * This package contains base entity classes that provide common functionality
 * for all JPA entities in the HMS system.
 *
 * <h2>Entity Hierarchy:</h2>
 * <pre>
 * BaseEntity (abstract)
 *   ├── Provides: UUID id, createdAt, updatedAt
 *   ├── Features: Automatic timestamps, equals/hashCode, toString
 *   └── Use when: You only need basic entity fields
 *
 * AuditableEntity extends BaseEntity (abstract)
 *   ├── Provides: All from BaseEntity + createdBy, updatedBy, version
 *   ├── Features: User auditing, optimistic locking
 *   └── Use when: You need to track who created/updated records
 *
 * SoftDeletableEntity extends AuditableEntity (abstract)
 *   ├── Provides: All from AuditableEntity + deletedAt, deletedBy
 *   ├── Features: Soft delete, automatic filtering of deleted records
 *   └── Use when: You need audit trail and recovery of deleted records
 * </pre>
 *
 * <h2>Usage Examples:</h2>
 *
 * <h3>Example 1: Using BaseEntity</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "lookup_data", schema = "master_schema")
 * public class LookupData extends BaseEntity {
 *     @Column(name = "code")
 *     private String code;
 *
 *     @Column(name = "description")
 *     private String description;
 *
 *     // Getters and setters
 * }
 *
 * // Usage
 * LookupData data = new LookupData();
 * data.setCode("A001");
 * data.setDescription("Some data");
 * repository.save(data);
 * // createdAt and updatedAt are set automatically
 * }</pre>
 *
 * <h3>Example 2: Using AuditableEntity</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "invoice", schema = "billing_schema")
 * public class Invoice extends AuditableEntity {
 *     @Column(name = "invoice_number")
 *     private String invoiceNumber;
 *
 *     @Column(name = "total_amount")
 *     private BigDecimal totalAmount;
 *
 *     // Getters and setters
 * }
 *
 * // Usage
 * Invoice invoice = new Invoice();
 * invoice.setInvoiceNumber("INV-2025-001");
 * invoice.setTotalAmount(new BigDecimal("1000000"));
 * repository.save(invoice);
 * // createdAt, updatedAt, createdBy, updatedBy are set automatically
 * // version is initialized to 0 for optimistic locking
 * }</pre>
 *
 * <h3>Example 3: Using SoftDeletableEntity (Recommended for most entities)</h3>
 * <pre>{@code
 * @Entity
 * @Table(name = "patient", schema = "patient_schema")
 * public class Patient extends SoftDeletableEntity {
 *     @Column(name = "mrn", unique = true)
 *     private String mrn;
 *
 *     @Column(name = "full_name")
 *     private String fullName;
 *
 *     @Column(name = "nik")
 *     private String nik;
 *
 *     // Getters and setters
 * }
 *
 * // Usage - Creating
 * Patient patient = new Patient();
 * patient.setMrn("202501-00001");
 * patient.setFullName("Budi Santoso");
 * patient.setNik("3201234567890001");
 * patientRepository.save(patient);
 * // All audit fields populated automatically
 *
 * // Usage - Soft Deleting (Method 1: Manual)
 * patient.softDelete("admin");
 * patientRepository.save(patient);
 *
 * // Usage - Soft Deleting (Method 2: Repository delete)
 * patientRepository.delete(patient);
 * // Automatically soft deletes due to @SQLDelete annotation
 *
 * // Usage - Finding (excludes soft-deleted)
 * Optional<Patient> found = patientRepository.findById(id);
 * // Only returns patient if deletedAt is NULL
 *
 * // Usage - Restoring
 * patient.restore();
 * patientRepository.save(patient);
 * }</pre>
 *
 * <h2>Audit Fields Behavior:</h2>
 * <table border="1">
 * <tr>
 *   <th>Field</th>
 *   <th>When Set</th>
 *   <th>Set By</th>
 *   <th>Can Update</th>
 * </tr>
 * <tr>
 *   <td>id</td>
 *   <td>On persist</td>
 *   <td>JPA (UUID generator)</td>
 *   <td>No (updatable=false)</td>
 * </tr>
 * <tr>
 *   <td>createdAt</td>
 *   <td>On persist</td>
 *   <td>@PrePersist callback</td>
 *   <td>No (updatable=false)</td>
 * </tr>
 * <tr>
 *   <td>updatedAt</td>
 *   <td>On persist and update</td>
 *   <td>@PrePersist, @PreUpdate</td>
 *   <td>Yes (automatic)</td>
 * </tr>
 * <tr>
 *   <td>createdBy</td>
 *   <td>On persist</td>
 *   <td>Spring Data JPA Auditing</td>
 *   <td>No (updatable=false)</td>
 * </tr>
 * <tr>
 *   <td>updatedBy</td>
 *   <td>On persist and update</td>
 *   <td>Spring Data JPA Auditing</td>
 *   <td>Yes (automatic)</td>
 * </tr>
 * <tr>
 *   <td>version</td>
 *   <td>On persist</td>
 *   <td>JPA</td>
 *   <td>Yes (automatic, for optimistic locking)</td>
 * </tr>
 * <tr>
 *   <td>deletedAt</td>
 *   <td>On soft delete</td>
 *   <td>Manual or @SQLDelete</td>
 *   <td>Yes</td>
 * </tr>
 * <tr>
 *   <td>deletedBy</td>
 *   <td>On soft delete</td>
 *   <td>Manual</td>
 *   <td>Yes</td>
 * </tr>
 * </table>
 *
 * <h2>Optimistic Locking:</h2>
 * <p>
 * The {@code version} field in AuditableEntity provides optimistic locking to prevent
 * lost updates in concurrent transactions.
 * </p>
 *
 * <p>Example scenario:</p>
 * <pre>{@code
 * // User A loads patient with version = 1
 * Patient patientA = patientRepository.findById(id).get();
 *
 * // User B loads same patient with version = 1
 * Patient patientB = patientRepository.findById(id).get();
 *
 * // User A updates and saves (version becomes 2)
 * patientA.setFullName("Updated Name A");
 * patientRepository.save(patientA); // Success, version = 2
 *
 * // User B tries to save with old version = 1
 * patientB.setFullName("Updated Name B");
 * patientRepository.save(patientB); // Throws OptimisticLockException
 *
 * // User B must reload and retry
 * Patient freshPatient = patientRepository.findById(id).get();
 * freshPatient.setFullName("Updated Name B");
 * patientRepository.save(freshPatient); // Success
 * }</pre>
 *
 * <h2>Soft Delete Behavior:</h2>
 * <ul>
 *   <li>Query automatically excludes soft-deleted records (via @Where clause)</li>
 *   <li>Use {@code softDelete()} method or {@code repository.delete()} to soft delete</li>
 *   <li>Use {@code restore()} method to un-delete</li>
 *   <li>To find including deleted: create custom repository method with @Query</li>
 * </ul>
 *
 * <h2>Best Practices:</h2>
 * <ol>
 *   <li><b>Use SoftDeletableEntity for most entities</b> - Especially for medical records (required by law)</li>
 *   <li><b>Use AuditableEntity for lookup/master data</b> - When soft delete is not needed</li>
 *   <li><b>Use BaseEntity for simple entities</b> - Rarely needed, but available</li>
 *   <li><b>Never hard delete medical records</b> - Always use soft delete</li>
 *   <li><b>Handle OptimisticLockException</b> - Implement retry logic in service layer</li>
 *   <li><b>Test concurrent updates</b> - Verify optimistic locking works correctly</li>
 *   <li><b>Document entity choice</b> - Comment why you chose specific base class</li>
 * </ol>
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
package com.yudha.hms.shared.entity;