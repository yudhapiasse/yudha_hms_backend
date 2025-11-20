package com.yudha.hms.clinical.exception;

/**
 * Exception thrown when an encounter business rule is violated.
 *
 * Business rules include:
 * - Cannot finish encounter without at least one diagnosis
 * - Cannot cancel encounter with linked billing transactions
 * - Other domain-specific business constraints
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public class EncounterBusinessRuleException extends RuntimeException {

    private final String ruleViolated;

    public EncounterBusinessRuleException(String message) {
        super(message);
        this.ruleViolated = null;
    }

    public EncounterBusinessRuleException(String ruleViolated, String message) {
        super(message);
        this.ruleViolated = ruleViolated;
    }

    public EncounterBusinessRuleException(String ruleViolated, String message, Throwable cause) {
        super(message, cause);
        this.ruleViolated = ruleViolated;
    }

    public String getRuleViolated() {
        return ruleViolated;
    }
}
