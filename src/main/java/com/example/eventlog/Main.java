package com.example.eventlog;

import com.example.eventlog.parser.EventParser;
import com.example.eventlog.service.EventProcessor;
import com.example.eventlog.service.Statistics;
import com.example.eventlog.validator.EventValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

/**
 * Application entry point.
 *
 * This class handles command-line input and console output.
 * The actual parsing, validation, and statistics calculation are delegated
 * to EventParser, EventValidator, and EventProcessor.
 * -----------
 * Main is only the entry point. it checks the file path argument, creates the required objects,
 * calls EventProcessor, and prints the final statistics. The business logic is not inside Main;
 * it is delegated to the service classes. This keeps the application easier to test and maintain.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide input file path.");
            System.out.println("Example:");
            System.out.println("java -jar event-log-processor.jar input/events.txt");
            return;
        }

        Path inputFilePath = Path.of(args[0]);

        if (!Files.exists(inputFilePath)) {
            System.out.println("File not found: " + inputFilePath);
            return;
        }

        try {
            EventParser parser = new EventParser();
            EventValidator validator = new EventValidator();
            EventProcessor processor = new EventProcessor(parser, validator);

            Statistics statistics = processor.processFile(inputFilePath);

            printStatistics(statistics);

        } catch (IOException ex) {
            System.out.println("Could not read file: " + inputFilePath);
            System.out.println("Reason: " + ex.getMessage());
        }
    }

    /**
     * Prints the final statistics in a readable console format.
     */
    private static void printStatistics(Statistics statistics) {
        System.out.println("Total valid events: " + statistics.getTotalValidEvents());
        System.out.println("Total invalid lines: " + statistics.getTotalInvalidLines());

        printInvalidLineDetails(statistics);
        printEventCountPerUser(statistics);
        printPurchaseStatistics(statistics);
        printMostActiveUser(statistics);
        printTopThreeMostActiveUsers(statistics);
        printEventCountPerAction(statistics);
    }

    private static void printInvalidLineDetails(Statistics statistics) {
        if (statistics.getInvalidLineReasons().isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println("Invalid line details:");
        statistics.getInvalidLineReasons()
                .forEach((lineNumber, reason) ->
                        System.out.println("Line " + lineNumber + ": " + reason));
    }

    private static void printEventCountPerUser(Statistics statistics) {
        System.out.println();
        System.out.println("Event count per user:");

        //sorted
        statistics.getEventCountPerUser()
                .entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    private static void printPurchaseStatistics(Statistics statistics) {
        System.out.println();
        System.out.println("Purchase statistics:");
        System.out.println("Total purchase amount: " + statistics.getTotalPurchaseAmount());
        System.out.println("Average purchase amount: " + statistics.getAveragePurchaseAmount());
        System.out.println("Largest purchase: " + statistics.getLargestPurchase());
    }

    private static void printMostActiveUser(Statistics statistics) {
        System.out.println();
        System.out.println("Most active user: " + statistics.getMostActiveUser());
    }

    private static void printTopThreeMostActiveUsers(Statistics statistics) {
        System.out.println();
        System.out.println("Top 3 most active users:");

        final int[] rank = {1};

        statistics.getTopThreeMostActiveUsers()
                .forEach((userId, count) -> {
                    System.out.println(rank[0] + ". " + userId + " - " + count + " events");
                    rank[0]++;
                });
    }

    private static void printEventCountPerAction(Statistics statistics) {
        System.out.println();
        System.out.println("Event count per action:");
        statistics.getEventCountPerAction()
                .forEach((action, count) -> System.out.println(action + ": " + count));
    }
}