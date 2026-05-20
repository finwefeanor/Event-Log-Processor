package com.example.eventlog.parser;

import com.example.eventlog.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Optional;

public class EventParser {



    /**
     *
     * This parser only checks whether the line can be converted from JSON into an Event object.
     * It does not decide whether the event is logically valid. That is the validators responsibility.
     */

    private final ObjectMapper objectMapper;

    public EventParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public Optional<Event> parseLine(String line) {
        if (line == null || line.isBlank()) {
            return Optional.empty();
        }

        try {
            Event event = objectMapper.readValue(line, Event.class);
            return Optional.of(event);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}