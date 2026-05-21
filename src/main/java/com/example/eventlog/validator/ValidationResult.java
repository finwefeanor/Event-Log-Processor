package com.example.eventlog.validator;

/**
 * Represents the result of validating an event.
 *
 * It stores whether validation passed and, when it fails,
 * the reason that can be printed in the final output.
 */
public class ValidationResult {

    private final boolean valid;
    private final String errorMessage;

    public ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
