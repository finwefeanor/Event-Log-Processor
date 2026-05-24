# Event Log Processor

A small Java console application that reads event logs from a file, validates each event, skips malformed or invalid lines, and prints the statistics.

## Tech Stack

- Java 17
- Maven
- Jackson
- JUnit 5

## How to Run

Open a terminal, Command Prompt, PowerShell, or Git Bash from the root folder of the project.

Build the project:

```bash
mvn clean package
```

## Running Notes

Run the application with an input file path:

```bash
java -jar target/event-log-processor.jar input/events.txt
```

This is a command-line application, so the input file path must be provided as an argument.


The input file, for example input/events.txt, should contain one JSON object per line.

```text
{"timestamp":"2026-05-01T10:00:00Z","eventId":"550e8400-e29b-41d4-a716-446655440000","userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001","action":"login"}
{"timestamp":"2026-05-01T10:01:12Z","eventId":"550e8400-e29b-41d4-a716-446655440001","userId":"c1b7d8f0-1c3a-4d95-8d0d-6df3f1d5b001","action":"view","articleId":"art-900"}
INVALID_LINE
```

## Output

The application prints:

- Total valid events
- Total invalid lines
- Event count per user
- Purchase statistics
- Most active user
- Top 3 most active users
- Event count per action

## Design Decisions

The application is implemented as a console application because the task focuses on file processing, validation, and statistics generation.


The code is split into small components:

- `EventParser` parses JSON lines into `Event` objects.
- `EventValidator` checks required fields and action-specific rules.
- `EventProcessor` processes valid events and calculates statistics.
- `Statistics` stores the calculated result.
- `Main` handles command-line input and output.



## Assumptions
Each line in the input file represents one event.
Invalid lines are counted and skipped.
Statistics are calculated only from valid events.
Unknown actions are treated as invalid.
Purchase amounts must be valid decimal numbers and cannot be negative.
Duplicate eventId handling is not implemented because it is listed as optional bonus work.

## Testing

Run tests with:
```bash
mvn test
```
The project includes tests for:

- JSON parsing
- Event validation
- Statistics calculation

## Bonus Implemented

- Streaming large files efficiently using `BufferedReader`, so the full input file is not loaded into memory at once.

