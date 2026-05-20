package com.example.eventlog.validator;

public class ValidationResult {

    //Instead of validator returning only true or false,
    // we should return a reason.
    /**
     * Originally validation returned only boolean, but after adding invalid-line reporting,
     * I added ValidationResult so the processor can print why each line failed.
     */

    private final boolean valid;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
