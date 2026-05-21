package com.example.eventlog.validator;

import com.example.eventlog.model.Action;
import com.example.eventlog.model.Event;

import java.math.BigDecimal;
/**
 * Checks whether a parsed Event follows the expected event rules.
 *
 * Parsing and validation are kept separate because a line can be valid JSON
 * but still be invalid for this task. For example, a view event can parse
 * correctly but still be invalid if articleId is missing.
 *
 * The validator returns a ValidationResult so invalid events can include
 * a clear reason instead of only true or false.
 */

public class EventValidator {

    /**
     * Helper method for callers that only need a true or false result.
     */
    public boolean isValid(Event event) {
        return validate(event).isValid();
    }

    /**
     * Validates one event and returns the result with an error message if it fails.
     */
    public ValidationResult validate(Event event) {
        if (event == null) {
            return new ValidationResult(false,"Event is null or could not be parsed");
        }

        if (event.getTimestamp() == null) {
            return new ValidationResult(false,"timestamp is missing or invalid");
        }

        if (event.getEventId() == null) {
            return new ValidationResult(false,"eventId is missing or invalid UUID");
        }

        if (event.getUserId() == null) {
            return new ValidationResult(false,"userId is missing or invalid UUID");
        }

        if (event.getAction() == null || event.getAction().isBlank()) {
            return new ValidationResult(false,"action is missing");
        }

        if (!Action.isSupported(event.getAction())) {
            return new ValidationResult(false,"unknown action: " + event.getAction());
        }

        String action = event.getAction().toLowerCase();

        // After common fields are validated, check action-specific required fields.
        return switch (action) {
            case "login", "logout" -> new ValidationResult(true, null);

            case "view" -> isNotBlank(event.getArticleId())
                    ? new ValidationResult(true, null)
                    : new ValidationResult(false, "view event is missing articleId");

            case "click" -> isNotBlank(event.getTarget())
                    ? new ValidationResult(true, null)
                    : new ValidationResult(false,"click event is missing target");

            case "purchase" -> isValidPurchaseAmount(event.getAmount())
                    ? new ValidationResult(true, null)
                    : new ValidationResult(false,"purchase event contains missing, invalid, or negative amount");

            default -> new ValidationResult(false,"unsupported action");
        };
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isValidPurchaseAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

}
