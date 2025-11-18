package com.yudha.hms.shared.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested resource cannot be found.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s dengan %s '%s' tidak ditemukan", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(String.format("%s dengan ID '%s' tidak ditemukan", resourceName, id));
        this.resourceName = resourceName;
        this.fieldName = "id";
        this.fieldValue = id;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}