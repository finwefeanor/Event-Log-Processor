package com.example.eventlog.parser;

import com.example.eventlog.model.Event;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EventParserTest checks only parsing behavior.
 * It verifies that valid JSON becomes an Event, malformed JSON returns empty,
 * blank lines return empty, and invalid UUID values are rejected during parsing.
 * This keeps parser tests focused only on parsing, not business validation.
 * ---------
 * I focused tests on behavior, not simple getters and setters.
 * The parser tests cover successful and failed parsing,
 * the validator tests cover business rules, and the processor test checks the end-to-end statistics
 * calculation using valid and invalid lines.
 * -----
 * I used descriptive test names instead of adding many comments.
 * Each test name follows the pattern: method_shouldExpectedResult_whenCondition.
 * That makes the test purpose clear without extra comments.
 */
class EventParserTest {

    private final EventParser parser = new EventParser();

    @Test
    void parseLine_shouldReturnEvent_whenJsonIsValid() {
        String jsonLine = """
                {"timestamp":"2026-05-01T10:00:00Z","eventId":"550e8400-e29b-41d4-a716-446655440000","userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001","action":"login"}
                """;


        /**
         * Here Optional<Event> means:
         * Maybe parsing succeeded and we have an Event.
         * Maybe parsing failed and we have empty.
         * So instead of throwing an exception and stopping the app, the parser returns Optional.empty()
         * so invalid input should not stop processing.
         */
        Optional<Event> result = parser.parseLine(jsonLine);

        assertTrue(result.isPresent());
        assertEquals("login", result.get().getAction());
        assertEquals("550e8400-e29b-41d4-a716-446655440000", result.get().getEventId().toString());
        assertEquals("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001", result.get().getUserId().toString());
    }

    @Test
    void parseLine_shouldReturnEmpty_whenJsonIsMalformed() {
        //Arrange
        String invalidLine = "INVALID_LINE";

        //Act
        Optional<Event> result = parser.parseLine(invalidLine);

        //Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void parseLine_shouldReturnEmpty_whenLineIsBlank() {
        String blankLine = "   ";

        Optional<Event> result = parser.parseLine(blankLine);

        assertTrue(result.isEmpty());
    }

    @Test
    void parseLine_shouldReturnEmpty_whenUuidIsInvalid() {
        String jsonLine = """
                {"timestamp":"2026-05-01T10:00:00Z","eventId":"NOT-A-UUID","userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001","action":"login"}
                """;

        Optional<Event> result = parser.parseLine(jsonLine);

        assertTrue(result.isEmpty());
    }
}