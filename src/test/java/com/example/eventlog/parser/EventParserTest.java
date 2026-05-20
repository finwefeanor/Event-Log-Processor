package com.example.eventlog.parser;


import com.example.eventlog.model.Event;
import com.example.eventlog.parser.EventParser;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EventParserTest {

    private final EventParser parser = new EventParser();

    @Test
    void parseLine_shouldReturnEvent_whenJsonIsValid() {
        String jsonLine = """
                {"timestamp":"2026-05-01T10:00:00Z","eventId":"550e8400-e29b-41d4-a716-446655440000","userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001","action":"login"}
                """;

        Optional<Event> result = parser.parseLine(jsonLine);

        assertTrue(result.isPresent());
        assertEquals("login", result.get().getAction());
        assertEquals("550e8400-e29b-41d4-a716-446655440000", result.get().getEventId().toString());
        assertEquals("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001", result.get().getUserId().toString());
    }

    @Test
    void parseLine_shouldReturnEmpty_whenJsonIsMalformed() {
        String invalidLine = "INVALID_LINE";

        Optional<Event> result = parser.parseLine(invalidLine);

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