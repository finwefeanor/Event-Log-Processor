package com.example.eventlog.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents one parsed event from the JSON log file.
 * The field types match the expected input format: UUID for IDs,
 * Instant for timestamp, and BigDecimal for purchase amount.
 *
 * action is kept as String so invalid action values do not break parsing
 * before validation can return a clear error.
 */
public class Event {

    private Instant timestamp;
    private UUID eventId;
    private UUID userId;
    private String action;

    // Optional fields depending on action
    // These are required only for certain actions:
    // view -> articleId
    // click -> target
    // purchase -> amount
    private String articleId;
    private String target;
    private BigDecimal amount;

    public Event() {
        // // Required by Jackson when converting JSON into an Event object.
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
