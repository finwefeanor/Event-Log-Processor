package com.example.eventlog.service;

import com.example.eventlog.model.Event;
import com.example.eventlog.parser.EventParser;
import com.example.eventlog.validator.EventValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.eventlog.validator.ValidationResult;

/**
 * Coordinates parsing, validation, and statistics calculation.
 *
 * Valid events are used to update the statistics. Invalid lines are counted
 * and skipped, so one bad line does not stop the whole file.
 *
 * processLines() is useful for tests, while processFile() reads from an actual file.
 * Both methods use the same line-processing logic to avoid duplication.
 */
public class EventProcessor {

    private final EventParser parser;
    private final EventValidator validator;

    public EventProcessor(EventParser parser, EventValidator validator) {
        this.parser = parser;
        this.validator = validator;
    }

    /**
     * Processes a list of lines already loaded in memory.
     *
     * This is mainly useful for unit tests, because test data can be passed directly
     * without creating a real input file.
     */
    public Statistics processLines(List<String> lines) {
        ProcessingState state = new ProcessingState();

        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            processSingleLine(lines.get(i), lineNumber, state);
        }

        return buildStatistics(state);
    }

    /**
     * Processes an input file line by line.
     *
     * BufferedReader is used so the full file does not need to be loaded into memory.
     * This makes it more suitable for larger log files.
     */
    public Statistics processFile(Path inputFilePath) throws IOException {
        ProcessingState state = new ProcessingState();

        try (BufferedReader reader = Files.newBufferedReader(inputFilePath)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                processSingleLine(line, lineNumber, state);
            }
        }

        return buildStatistics(state);
    }

    /**
     * Handles one input line by parsing, validating, and updating the current state.
     */
    private void processSingleLine(String line, int lineNumber, ProcessingState state) {
        Optional<Event> parsedEvent = parser.parseLine(line);

        if (parsedEvent.isEmpty()) {
            state.invalidCount++;
            state.invalidLineReasons.put(lineNumber, "Line is not valid JSON or contains invalid field format");
            return;
        }

        Event event = parsedEvent.get();
        ValidationResult validationResult = validator.validate(event);

        if (!validationResult.isValid()) {
            state.invalidCount++;
            state.invalidLineReasons.put(lineNumber, validationResult.getErrorMessage());
            return;
        }

        updateStatisticsWithValidEvent(event, state);
    }

    private void updateStatisticsWithValidEvent(Event event, ProcessingState state) {
        state.validCount++;

        UUID userId = event.getUserId();
        String action = event.getAction().toLowerCase();

        state.userCounts.put(userId, state.userCounts.getOrDefault(userId, 0) + 1);
        state.actionCounts.put(action, state.actionCounts.getOrDefault(action, 0) + 1);

        //Every valid event should update.
        //Then, only if the event is purchase, we additionally update purchase statistics.

        if ("purchase".equals(action)) {
            BigDecimal amount = event.getAmount();

            state.totalPurchaseAmount = state.totalPurchaseAmount.add(amount);
            state.purchaseCount++;

            //If the current purchase amount is bigger than the largest purchase found so far,
            // replace largestPurchase.
            if (amount.compareTo(state.largestPurchase) > 0) {
                state.largestPurchase = amount;
            }
        }
    }

    /**
     * Builds the final Statistics object from the collected processing state.
     */
    private Statistics buildStatistics(ProcessingState state) {
        Statistics statistics = new Statistics();

        statistics.setTotalValidEvents(state.validCount);
        statistics.setTotalInvalidLines(state.invalidCount);
        statistics.setInvalidLineReasons(state.invalidLineReasons);

        statistics.setEventCountPerUser(state.userCounts);
        statistics.setEventCountPerAction(state.actionCounts);

        statistics.setTotalPurchaseAmount(state.totalPurchaseAmount);
        statistics.setLargestPurchase(state.largestPurchase);

        if (state.purchaseCount > 0) {
            BigDecimal average = state.totalPurchaseAmount.divide(
                    BigDecimal.valueOf(state.purchaseCount),
                    2,
                    RoundingMode.HALF_UP
            );
            statistics.setAveragePurchaseAmount(average);
        }

        statistics.setMostActiveUser(findMostActiveUser(state.userCounts));
        statistics.setTopThreeMostActiveUsers(findTopThreeUsers(state.userCounts));

        return statistics;
    }

    /**
     * Finds the user with the highest number of valid events.
     *
     * Returns null if there are no valid events.
     */
    private UUID findMostActiveUser(Map<UUID, Integer> userCounts) {
        return userCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Returns up to three users with the highest valid event counts.
     *
     * LinkedHashMap keeps the sorted order when the result is returned.
     */
    private Map<UUID, Integer> findTopThreeUsers(Map<UUID, Integer> userCounts) {
        return userCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    /**
     * Temporary state used while processing lines.
     *
     * This keeps the internal counters and maps together instead of passing them
     * separately between helper methods.
     */
    private static class ProcessingState {
        private int validCount;
        private int invalidCount;
        private int purchaseCount;

        private final Map<UUID, Integer> userCounts = new LinkedHashMap<>();
        private final Map<String, Integer> actionCounts = new LinkedHashMap<>();
        private final Map<Integer, String> invalidLineReasons = new LinkedHashMap<>();

        private BigDecimal totalPurchaseAmount = BigDecimal.ZERO;
        private BigDecimal largestPurchase = BigDecimal.ZERO;
    }
}
