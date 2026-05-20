package com.example.eventlog.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Event {


    /**
    This class represents one event line from the JSON file.

     I used UUID for eventId and userId because the PDF says these fields must be UUIDs.
     I used Instant for timestamp because the PDF uses ISO-8601 timestamps like 2026-05-01T10:00:00Z.
     I used BigDecimal for amount because money/decimal values should not be handled with double if precision matters.
     I kept action as String instead of enum so unknown actions can be parsed first and rejected by validation later.
     */

    private Instant timestamp;
    private UUID eventId;
    private UUID userId;
    private String action;

    // Optional fields depending on action
    private String articleId;   // required for view
    private String target;      // required for click
    private BigDecimal amount;  // required for purchase

    public Event() {
        // Needed by Jackson
    }

    public Event(Instant timestamp,
                 UUID eventId,
                 UUID userId,
                 String action,
                 String articleId,
                 String target,
                 BigDecimal amount) {
        this.timestamp = timestamp;
        this.eventId = eventId;
        this.userId = userId;
        this.action = action;
        this.articleId = articleId;
        this.target = target;
        this.amount = amount;
    }


    //getter setters
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
