package com.example.eventlog;


import com.example.eventlog.model.Event;
import com.example.eventlog.parser.EventParser;
import com.example.eventlog.service.EventProcessor;
import com.example.eventlog.service.Statistics;
import com.example.eventlog.validator.EventValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Main {
//    public static void main(String[] args) {
//
//        EventParser parser = new EventParser();
//        EventValidator validator = new EventValidator();
//
////        String jsonLine = """
////                {"timestamp":"2026-05-01T10:00:00Z",
////                "eventId":"550e8400-e29b-41d4-a716-446655440000",
////                "userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001",
////                "action":"login"}
////                """;
////
////        Optional<Event> event = parser.parseLine(jsonLine);
////
////        if (event.isPresent()) {
////            System.out.println("Parsed event successfully!");
////            System.out.println("Action: " + event.get().getAction());
////            System.out.println("User ID: " + event.get().getUserId());
////        } else {
////            System.out.println("Invalid JSON line.");
////        }
//
//       // -----------------------------------------------------------------
//
//        //invalid json
//        String jsonLine = """
//        {"timestamp":"2026-05-01T10:01:12Z",
//        "eventId":"550e8400-e29b-41d4-a716-446655440001",
//        "userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001",
//        "action":"view"}
//        """;
//
////        String jsonLine = """
////                {"timestamp":"2026-05-01T10:01:12Z",
////                "eventId":"550e8400-e29b-41d4-a716-446655440001",
////                "userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001",
////                "action":"view","articleId":"art-900"}
////                """;
//
//        Optional<Event> parsedEvent = parser.parseLine(jsonLine);
//
//        if (parsedEvent.isPresent() && validator.isValid(parsedEvent.get())) {
//            System.out.println("Valid event!");
//            System.out.println("Action: " + parsedEvent.get().getAction());
//        } else {
//            System.out.println("Invalid event!");
//        }
//
//        System.out.println("Event Log Processor is running...");
//    }

    //------------------------------------------------------------------

//    public static void main(String[] args) {
//        EventParser parser = new EventParser();
//        EventValidator validator = new EventValidator();
//        EventProcessor processor = new EventProcessor(parser, validator);
//
//        List<String> lines = List.of(
//                "{\"timestamp\":\"2026-05-01T10:00:00Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440000\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"login\"}",
//                "{\"timestamp\":\"2026-05-01T10:01:12Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440001\",\"userId\":\"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001\",\"action\":\"view\",\"articleId\":\"art-900\"}",
//                "{\"timestamp\":\"2026-05-01T10:02:05Z\",\"eventId\":\"550e8400-e29b-41d4-a716-446655440003\",\"userId\":\"d2d44db8-b8d9-4b43-9c2f-3bb47e87f221\",\"action\":\"purchase\",\"amount\":19.99}",
//                "INVALID_LINE"
//        );
//
//        Statistics statistics = processor.processLines(lines);
//
//        System.out.println("Total valid events: " + statistics.getTotalValidEvents());
//        System.out.println("Total invalid lines: " + statistics.getTotalInvalidLines());
//        System.out.println("Total purchase amount: " + statistics.getTotalPurchaseAmount());
//        System.out.println("Average purchase amount: " + statistics.getAveragePurchaseAmount());
//        System.out.println("Largest purchase: " + statistics.getLargestPurchase());
//        System.out.println("Most active user: " + statistics.getMostActiveUser());
//        System.out.println("Event count per user: " + statistics.getEventCountPerUser());
//        System.out.println("Event count per action: " + statistics.getEventCountPerAction());
//        System.out.println("Top 3 users: " + statistics.getTopThreeMostActiveUsers());
//    }

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

    private static void printStatistics(Statistics statistics) {
        System.out.println("Total valid events: " + statistics.getTotalValidEvents());
        System.out.println("Total invalid lines: " + statistics.getTotalInvalidLines());

        if (!statistics.getInvalidLineReasons().isEmpty()) {
            System.out.println();
            System.out.println("Invalid line details:");
            statistics.getInvalidLineReasons()
                    .forEach((lineNumber, reason) ->
                            System.out.println("Line " + lineNumber + ": " + reason));
        }

        System.out.println();
        System.out.println("Event count per user:");
        statistics.getEventCountPerUser()
                .forEach((userId, count) -> System.out.println(userId + ": " + count));

        System.out.println();
        System.out.println("Purchase statistics:");
        System.out.println("Total purchase amount: " + statistics.getTotalPurchaseAmount());
        System.out.println("Average purchase amount: " + statistics.getAveragePurchaseAmount());
        System.out.println("Largest purchase: " + statistics.getLargestPurchase());

        System.out.println();
        System.out.println("Most active user: " + statistics.getMostActiveUser());

        System.out.println();
        System.out.println("Top 3 most active users:");
        final int[] rank = {1};
        statistics.getTopThreeMostActiveUsers()
                .forEach((userId, count) -> {
                    System.out.println(rank[0] + ". " + userId + " - " + count + " events");
                    rank[0]++;
                });

        System.out.println();
        System.out.println("Event count per action:");
        statistics.getEventCountPerAction()
                .forEach((action, count) -> System.out.println(action + ": " + count));
    }


}
