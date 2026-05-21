package com.example.eventlog.parser;

import com.example.eventlog.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Optional;
/**
 * Converts raw JSON lines into Event objects.
 *
 * This class only handles parsing. It does not decide whether the event is valid
 * according to business rules; that is handled by EventValidator.
 *
 * ObjectMapper is used for JSON parsing, and JavaTimeModule is registered
 * because Event uses Instant for timestamps.
 */
public class EventParser {

    private final ObjectMapper objectMapper;

    public EventParser() {
        this.objectMapper = new ObjectMapper();

        // Registers support for Java time types such as Instant.
        // Without this module, Jackson may not correctly parse ISO-8601 timestamps.
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Tries to parse one line into an Event.
     *
     * Returns Optional.empty() if the line is blank, malformed,
     * or contains values that cannot be converted, such as an invalid UUID.
     */
    public Optional<Event> parseLine(String line) {
        if (line == null || line.isBlank()) {
            return Optional.empty();
        }

        try {
            Event event = objectMapper.readValue(line, Event.class);
            return Optional.of(event);
        } catch (Exception ex) {
            // Invalid lines should not stop the whole application.
            // They are handled later by the processor as invalid input lines.
            return Optional.empty();
        }
    }
}