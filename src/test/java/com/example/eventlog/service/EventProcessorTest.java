package com.example.eventlog.service;

import com.example.eventlog.parser.EventParser;
import com.example.eventlog.validator.EventValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventProcessorTest {

    private final EventProcessor processor = new EventProcessor(
            new EventParser(),
            new EventValidator()
    );

    @Test
    void processLines_shouldCalculateStatisticsUsingValidEventsOnly() {
        List<String> lines = List.of(
                "{\"timestamp\":\"2026-05-01T10:00:00Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440000\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"login\"}",
                "{\"timestamp\":\"2026-05-01T10:01:12Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440001\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"view\",\"articleId\":\"art-900\"}",
                "{\"timestamp\":\"2026-05-01T10:01:45Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440002\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"click\",\"target\":\"subscribe-button\"}",
                "{\"timestamp\":\"2026-05-01T10:02:05Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440003\",\"userId\":\"d2d44db8-b8d9-4b43-9c2f-3bb47e87f221\",\"action\":\"purchase\",\"amount\":19.99}",
                "INVALID_LINE",
                "{\"timestamp\":\"2026-05-01T10:03:40Z\",\"eventId\":\"NOT-A-UUID\",\"userId\":\"f2f09c5a-88bc-4c3e-8b78-0a91cfa0f332\",\"action\":\"view\",\"articleId\":\"art-901\"}",
                "{\"timestamp\":\"2026-05-01T10:05:20Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440006\",\"userId\":\"d2d44db8-b8d9-4b43-9c2f-3bb47e87f221\",\"action\":\"logout\"}"
        );

        Statistics statistics = processor.processLines(lines);

        UUID userOne = UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001");
        UUID userTwo = UUID.fromString("d2d44db8-b8d9-4b43-9c2f-3bb47e87f221");

        assertEquals(5, statistics.getTotalValidEvents());
        assertEquals(2, statistics.getTotalInvalidLines());

        assertEquals(3, statistics.getEventCountPerUser().get(userOne));
        assertEquals(2, statistics.getEventCountPerUser().get(userTwo));

        assertEquals(1, statistics.getEventCountPerAction().get("login"));
        assertEquals(1, statistics.getEventCountPerAction().get("view"));
        assertEquals(1, statistics.getEventCountPerAction().get("click"));
        assertEquals(1, statistics.getEventCountPerAction().get("purchase"));
        assertEquals(1, statistics.getEventCountPerAction().get("logout"));

        assertEquals(new BigDecimal("19.99"), statistics.getTotalPurchaseAmount());
        assertEquals(new BigDecimal("19.99"), statistics.getAveragePurchaseAmount());
        assertEquals(new BigDecimal("19.99"), statistics.getLargestPurchase());

        assertEquals(userOne, statistics.getMostActiveUser());
        assertEquals(2, statistics.getTopThreeMostActiveUsers().size());
    }
}