package com.example.eventlog.validator;

import com.example.eventlog.model.Event;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventValidatorTest {

    private final EventValidator validator = new EventValidator();

    @Test
    void isValid_shouldReturnTrue_whenLoginEventIsValid() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:00:00Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001"),
                "login",
                null,
                null,
                null
        );

        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenActionIsUnknown() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:00:00Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001"),
                "unknown",
                null,
                null,
                null
        );

        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenViewEventIsMissingArticleId() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:01:12Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001"),
                "view",
                null,
                null,
                null
        );

        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnTrue_whenViewEventHasArticleId() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:01:12Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001"),
                "view",
                "art-900",
                null,
                null
        );

        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenClickEventIsMissingTarget() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:01:45Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440002"),
                UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001"),
                "click",
                null,
                null,
                null
        );

        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnTrue_whenClickEventHasTarget() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:01:45Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440002"),
                UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001"),
                "click",
                null,
                "subscribe-button",
                null
        );

        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnTrue_whenPurchaseAmountIsValid() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:02:05Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440003"),
                UUID.fromString("d2d44db8-b8d9-4b43-9c2f-3bb47e87f221"),
                "purchase",
                null,
                null,
                new BigDecimal("19.99")
        );

        assertTrue(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenPurchaseAmountIsMissing() {
        Event event = new Event(
                Instant.parse("2026-05-01T10:02:05Z"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440003"),
                UUID.fromString("d2d44db8-b8d9-4b43-9c2f-3bb47e87f221"),
                "purchase",
                null,
                null,
                null
        );

        assertFalse(validator.isValid(event));
    }

    @Test
    void isValid_shouldReturnFalse_whenRequiredFieldsAreMissing() {
        Event event = new Event();

        assertFalse(validator.isValid(event));
    }
}
