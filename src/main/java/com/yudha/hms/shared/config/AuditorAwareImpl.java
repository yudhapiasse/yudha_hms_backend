package com.yudha.hms.shared.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of AuditorAware for Spring Data JPA Auditing.
 * Provides the current auditor (username) for @CreatedBy and @LastModifiedBy annotations.
 *
 * How it works:
 * 1. Gets the current authentication from Spring Security context
 * 2. Extracts the username from the authenticated user
 * 3. Returns it to JPA auditing mechanism
 * 4. JPA automatically populates createdBy and updatedBy fields
 *
 * Integration with Spring Security:
 * - In development (before authentication): Returns "system"
 * - After authentication implemented: Returns actual username
 * - For scheduled tasks/background jobs: Returns "system" or job name
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * Get the current auditor (username).
     *
     * This method is called automatically by Spring Data JPA when:
     * - An entity with @CreatedBy is being persisted
     * - An entity with @LastModifiedBy is being updated
     *
     * @return Optional containing the current username, or "system" if not authenticated
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        // Get the current authentication from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If no authentication or not authenticated, return "system"
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }

        // If authentication is "anonymousUser", return "system"
        if ("anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("system");
        }

        // Extract username from principal
        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            // If principal is UserDetails (standard Spring Security)
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            // If principal is a String (simple authentication)
            username = (String) principal;
        } else {
            // Fallback: use toString()
            username = principal.toString();
        }

        return Optional.ofNullable(username);
    }
}