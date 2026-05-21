package com.example.eventlog.service;

import com.example.eventlog.parser.EventParser;
import com.example.eventlog.validator.EventValidator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EventProcessorTest checks the full processing behavior using a mix of valid and invalid lines.
 * It verifies that invalid lines are skipped, valid events are counted, purchase statistics are calculated,
 * action counts are correct, most active user is found, and invalid line numbers are tracked.
 */
class EventProcessorTest {

    private final EventProcessor processor = new EventProcessor(
            new EventParser(),
            new EventValidator()
    );

    @Test
    void processLines_shouldCountValidAndInvalidLines() {
        Statistics statistics = processSampleLines();

        assertEquals(5, statistics.getTotalValidEvents());
        assertEquals(2, statistics.getTotalInvalidLines());
    }

    @Test
    void processLines_shouldCountEventsPerUser() {
        Statistics statistics = processSampleLines();

        UUID userOne = UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001");
        UUID userTwo = UUID.fromString("d2d44db8-b8d9-4b43-9c2f-3bb47e87f221");

        assertEquals(3, statistics.getEventCountPerUser().get(userOne));
        assertEquals(2, statistics.getEventCountPerUser().get(userTwo));
    }

    @Test
    void processLines_shouldCountEventsPerAction() {
        Statistics statistics = processSampleLines();

        assertEquals(1, statistics.getEventCountPerAction().get("login"));
        assertEquals(1, statistics.getEventCountPerAction().get("view"));
        assertEquals(1, statistics.getEventCountPerAction().get("click"));
        assertEquals(1, statistics.getEventCountPerAction().get("purchase"));
        assertEquals(1, statistics.getEventCountPerAction().get("logout"));
    }

    @Test
    void processLines_shouldCalculatePurchaseStatistics() {
        Statistics statistics = processSampleLines();

        assertEquals(new BigDecimal("19.99"), statistics.getTotalPurchaseAmount());
        assertEquals(new BigDecimal("19.99"), statistics.getAveragePurchaseAmount());
        assertEquals(new BigDecimal("19.99"), statistics.getLargestPurchase());
    }

    @Test
    void processLines_shouldFindMostActiveUser() {
        Statistics statistics = processSampleLines();

        UUID userOne = UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001");

        assertEquals(userOne, statistics.getMostActiveUser());
    }

    @Test
    void processLines_shouldFindTopThreeMostActiveUsers() {
        Statistics statistics = processSampleLines();

        UUID userOne = UUID.fromString("c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001");
        UUID userTwo = UUID.fromString("d2d44db8-b8d9-4b43-9c2f-3bb47e87f221");

        assertEquals(2, statistics.getTopThreeMostActiveUsers().size());
        assertEquals(3, statistics.getTopThreeMostActiveUsers().get(userOne));
        assertEquals(2, statistics.getTopThreeMostActiveUsers().get(userTwo));
    }

    @Test
    void processLines_shouldTrackInvalidLineReasons() {
        Statistics statistics = processSampleLines();

        assertEquals(2, statistics.getInvalidLineReasons().size());
        assertTrue(statistics.getInvalidLineReasons().containsKey(5));
        assertTrue(statistics.getInvalidLineReasons().containsKey(6));
    }

    private Statistics processSampleLines() {
        return processor.processLines(createSampleLines());
    }

    private List<String> createSampleLines() {
        return List.of(
                "{\"timestamp\":\"2026-05-01T10:00:00Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440000\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"login\"}",
                "{\"timestamp\":\"2026-05-01T10:01:12Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440001\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"view\",\"articleId\":\"art-900\"}",
                "{\"timestamp\":\"2026-05-01T10:01:45Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440002\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"click\",\"target\":\"subscribe-button\"}",
                "{\"timestamp\":\"2026-05-01T10:02:05Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440003\",\"userId\":\"d2d44db8-b8d9-4b43-9c2f-3bb47e87f221\",\"action\":\"purchase\",\"amount\":19.99}",
                "INVALID_LINE",
                "{\"timestamp\":\"2026-05-01T10:03:40Z\",\"eventId\":\"NOT-A-UUID\",\"userId\":\"f2f09c5a-88bc-4c3e-8b78-0a91cfa0f332\",\"action\":\"view\",\"articleId\":\"art-901\"}",
                "{\"timestamp\":\"2026-05-01T10:05:20Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440006\",\"userId\":\"d2d44db8-b8d9-4b43-9c2f-3bb47e87f221\",\"action\":\"logout\"}"
        );
    }
}