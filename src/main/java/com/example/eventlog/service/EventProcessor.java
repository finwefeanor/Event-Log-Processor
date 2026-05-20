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

public class EventProcessor {

    /**
     * EventProcessor coordinates the parser and validator,
     * then updates statistics only for valid events.
     * Invalid lines are counted and skipped, so processing continues even if one line is bad.
     *
     * I used BufferedReader in processFile method for file processing because it reads the file line by line,
     * instead of loading the entire file into memory.
     * This satisfies the optional streaming-large-files requirement.
     */

    private final EventParser parser;
    private final EventValidator validator;

    public EventProcessor(EventParser parser, EventValidator validator) {
        this.parser = parser;
        this.validator = validator;
    }

    public Statistics processLines(List<String> lines) {
        Statistics statistics = new Statistics();

        int validCount = 0;
        int invalidCount = 0;
        int purchaseCount = 0;

        Map<UUID, Integer> userCounts = new LinkedHashMap<>();
        Map<String, Integer> actionCounts = new LinkedHashMap<>();
        Map<Integer, String> invalidLineReasons = new LinkedHashMap<>();

        BigDecimal totalPurchaseAmount = BigDecimal.ZERO;
        BigDecimal largestPurchase = BigDecimal.ZERO;

        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i);

            Optional<Event> parsedEvent = parser.parseLine(line);

            if (parsedEvent.isEmpty()) {
                invalidCount++;
                invalidLineReasons.put(lineNumber, "Line is not valid JSON or contains invalid field format");
                continue;
            }

            Event event = parsedEvent.get();
            ValidationResult validationResult = validator.validate(event);

            if (!validationResult.isValid()) {
                invalidCount++;
                invalidLineReasons.put(lineNumber, validationResult.getErrorMessage());
                continue;
            }

            validCount++;

            UUID userId = event.getUserId();
            String action = event.getAction().toLowerCase();

            userCounts.put(userId, userCounts.getOrDefault(userId, 0) + 1);
            actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);

            if ("purchase".equals(action)) {
                BigDecimal amount = event.getAmount();

                totalPurchaseAmount = totalPurchaseAmount.add(amount);
                purchaseCount++;

                if (amount.compareTo(largestPurchase) > 0) {
                    largestPurchase = amount;
                }
            }
        }

        statistics.setTotalValidEvents(validCount);
        statistics.setTotalInvalidLines(invalidCount);
        statistics.setEventCountPerUser(userCounts);
        statistics.setEventCountPerAction(actionCounts);
        statistics.setInvalidLineReasons(invalidLineReasons);
        statistics.setTotalPurchaseAmount(totalPurchaseAmount);
        statistics.setLargestPurchase(largestPurchase);

        if (purchaseCount > 0) {
            BigDecimal average = totalPurchaseAmount.divide(
                    BigDecimal.valueOf(purchaseCount),
                    2,
                    RoundingMode.HALF_UP
            );
            statistics.setAveragePurchaseAmount(average);
        }

        Optional<Map.Entry<UUID, Integer>> mostActiveUser = userCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        mostActiveUser.ifPresent(entry -> statistics.setMostActiveUser(entry.getKey()));

        Map<UUID, Integer> topThreeUsers = userCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        statistics.setTopThreeMostActiveUsers(topThreeUsers);

        return statistics;
    }

    public Statistics processFile(Path inputFilePath) throws IOException {
        Statistics statistics = new Statistics();

        int validCount = 0;
        int invalidCount = 0;
        int purchaseCount = 0;

        Map<UUID, Integer> userCounts = new LinkedHashMap<>();
        Map<String, Integer> actionCounts = new LinkedHashMap<>();
        Map<Integer, String> invalidLineReasons = new LinkedHashMap<>();

        BigDecimal totalPurchaseAmount = BigDecimal.ZERO;
        BigDecimal largestPurchase = BigDecimal.ZERO;

        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(inputFilePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                Optional<Event> parsedEvent = parser.parseLine(line);

                if (parsedEvent.isEmpty()) {
                    invalidCount++;
                    invalidLineReasons.put(lineNumber, "Line is not valid JSON or contains invalid field format");
                    continue;
                }

                Event event = parsedEvent.get();
                ValidationResult validationResult = validator.validate(event);

                if (!validationResult.isValid()) {
                    invalidCount++;
                    invalidLineReasons.put(lineNumber, validationResult.getErrorMessage());
                    continue;
                }

                validCount++;

                UUID userId = event.getUserId();
                String action = event.getAction().toLowerCase();

                userCounts.put(userId, userCounts.getOrDefault(userId, 0) + 1);
                actionCounts.put(action, actionCounts.getOrDefault(action, 0) + 1);

                if ("purchase".equals(action)) {
                    BigDecimal amount = event.getAmount();

                    totalPurchaseAmount = totalPurchaseAmount.add(amount);
                    purchaseCount++;

                    if (amount.compareTo(largestPurchase) > 0) {
                        largestPurchase = amount;
                    }
                }
            }
        }

        fillStatistics(
                statistics,
                validCount,
                invalidCount,
                purchaseCount,
                userCounts,
                actionCounts,
                invalidLineReasons,
                totalPurchaseAmount,
                largestPurchase
        );

        return statistics;
    }

    private void fillStatistics(Statistics statistics,
                                int validCount,
                                int invalidCount,
                                int purchaseCount,
                                Map<UUID, Integer> userCounts,
                                Map<String, Integer> actionCounts,
                                Map<Integer, String> invalidLineReasons,
                                BigDecimal totalPurchaseAmount,
                                BigDecimal largestPurchase) {

        statistics.setTotalValidEvents(validCount);
        statistics.setTotalInvalidLines(invalidCount);
        statistics.setEventCountPerUser(userCounts);
        statistics.setEventCountPerAction(actionCounts);
        statistics.setInvalidLineReasons(invalidLineReasons);
        statistics.setTotalPurchaseAmount(totalPurchaseAmount);
        statistics.setLargestPurchase(largestPurchase);

        if (purchaseCount > 0) {
            BigDecimal average = totalPurchaseAmount.divide(
                    BigDecimal.valueOf(purchaseCount),
                    2,
                    RoundingMode.HALF_UP
            );
            statistics.setAveragePurchaseAmount(average);
        }

        Optional<Map.Entry<UUID, Integer>> mostActiveUser = userCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        mostActiveUser.ifPresent(entry -> statistics.setMostActiveUser(entry.getKey()));

        Map<UUID, Integer> topThreeUsers = userCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        statistics.setTopThreeMostActiveUsers(topThreeUsers);
    }

}
