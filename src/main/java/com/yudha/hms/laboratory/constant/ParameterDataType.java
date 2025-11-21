package com.yudha.hms.laboratory.constant;

/**
 * Parameter Data Type Enumeration.
 *
 * Data types for test parameters.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ParameterDataType {
    /**
     * Numeric value (integer or decimal)
     */
    NUMERIC("Numeric", "Numeric value (integer or decimal)"),

    /**
     * Text value
     */
    TEXT("Text", "Text value"),

    /**
     * Boolean value (yes/no, positive/negative)
     */
    BOOLEAN("Boolean", "Boolean value (yes/no, positive/negative)"),

    /**
     * Option value (from predefined list)
     */
    OPTION("Option", "Option value (from predefined list)");

    private final String displayName;
    private final String description;

    ParameterDataType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
