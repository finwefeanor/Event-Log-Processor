package com.example.eventlog.validator;

import com.example.eventlog.model.Event;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EventValidatorTest is larger because the validator contains most of the business rules.
 * I wrote separate tests for each action-specific rule, like view requiring articleId,
 * click requiring target, and purchase requiring a valid amount.
 * I focused tests on behavior from the requirements, not on simple getters and setters.
 */
class EventValidatorTest {

    private final EventValidator validator = new EventValidator();

    private Event createEvent(String action,
                              String articleId,
                              String target,
                              BigDecimal amount) {
        return new Event(
                Instant.parse("2026-05-01T10:00:00Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001"),
                action,
                articleId,
                target,
                amount
        );
    }

    @Test
    void isValid_shouldReturnTrue_whenLoginEventIsValid() {
        Event event = createEvent("login", null, null, null);

        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenActionIsUnknown() {
        Event event = createEvent("unknown", null, null, null);
        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenViewEventIsMissingArticleId() {
        Event event = createEvent("view",  null, null, null);
        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnTrue_whenViewEventHasArticleId() {
        Event event = createEvent("view","art-900", null, null);
        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenClickEventIsMissingTarget() {
        Event event = createEvent("click", null, null, null);
        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnTrue_whenClickEventHasTarget() {
        Event event = createEvent("click", null, "subscribe-button", null);
        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnTrue_whenPurchaseAmountIsValid() {
        Event event = createEvent("purchase", null, null, new BigDecimal("19.99"));
        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenPurchaseAmountIsMissing() {
        Event event = createEvent("purchase", null, null, null);
        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenPurchaseAmountIsNegative() {
        Event event = createEvent("purchase", null, null, new BigDecimal("-5.00"));

        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenRequiredFieldsAreMissing() {
        Event event = new Event();
        assertFalse(validator.isValid(event));
    }
}
