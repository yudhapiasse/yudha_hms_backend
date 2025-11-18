package com.yudha.hms.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing Configuration.
 * Enables Spring Data JPA auditing for automatic population of audit fields.
 *
 * What this configuration does:
 * 1. Enables JPA Auditing with @EnableJpaAuditing
 * 2. Sets up AuditorAware to provide current user
 * 3. Enables date/time auditing (createdAt, updatedAt)
 * 4. Enables user auditing (createdBy, updatedBy)
 *
 * Audit fields automatically populated:
 * - @CreatedDate → createdAt (in BaseEntity)
 * - @LastModifiedDate → updatedAt (in BaseEntity)
 * - @CreatedBy → createdBy (in AuditableEntity)
 * - @LastModifiedBy → updatedBy (in AuditableEntity)
 *
 * How to use:
 * - Extend BaseEntity for basic timestamp auditing
 * - Extend AuditableEntity for full auditing (timestamps + user tracking)
 * - Extend SoftDeletableEntity for full auditing + soft delete
 *
 * Note: The @EnableJpaAuditing annotation is already in the main application class,
 * but we include it here as well for clarity and to explicitly configure auditorAwareRef.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditConfiguration {

    /**
     * JPA Auditing is enabled with auditorAwareRef pointing to the bean named "auditorAware".
     * The actual AuditorAware implementation is provided by AuditorAwareImpl class
     * which is annotated with @Component("auditorAware").
     *
     * No need to define a @Bean here since AuditorAwareImpl already creates the bean
     * via @Component annotation.
     */
}