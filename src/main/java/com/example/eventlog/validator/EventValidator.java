package com.example.eventlog.validator;

import com.example.eventlog.model.Action;
import com.example.eventlog.model.Event;

import java.math.BigDecimal;

public class EventValidator {
    /**
     *
     * I separated validation from parsing because valid
     * JSON can still be invalid according to business rules.
     *
     * This checks business rules:
     * timestamp exists
     * eventId exists
     * userId exists
     * action is supported
     * view has articleId
     * click has target
     * purchase has valid amount
     */

    public boolean isValid(Event event) {
        return validate(event).isValid();
    }

    public ValidationResult validate(Event event) {
        if (event == null) {
            return ValidationResult.invalid("Event is null or could not be parsed");
        }

        if (event.getTimestamp() == null) {
            return ValidationResult.invalid("timestamp is missing or invalid");
        }

        if (event.getEventId() == null) {
            return ValidationResult.invalid("eventId is missing or invalid UUID");
        }

        if (event.getUserId() == null) {
            return ValidationResult.invalid("userId is missing or invalid UUID");
        }

        if (event.getAction() == null || event.getAction().isBlank()) {
            return ValidationResult.invalid("action is missing");
        }

        if (!Action.isSupported(event.getAction())) {
            return ValidationResult.invalid("unknown action: " + event.getAction());
        }

        String action = event.getAction().toLowerCase();

        return switch (action) {
            case "login", "logout" -> ValidationResult.valid();

            case "view" -> isNotBlank(event.getArticleId())
                    ? ValidationResult.valid()
                    : ValidationResult.invalid("view event is missing articleId");

            case "click" -> isNotBlank(event.getTarget())
                    ? ValidationResult.valid()
                    : ValidationResult.invalid("click event is missing target");

            case "purchase" -> isValidPurchaseAmount(event.getAmount())
                    ? ValidationResult.valid()
                    : ValidationResult.invalid("purchase event contains missing, invalid, or negative amount");

            default -> ValidationResult.invalid("unsupported action");
        };
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isValidPurchaseAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
    }

}
