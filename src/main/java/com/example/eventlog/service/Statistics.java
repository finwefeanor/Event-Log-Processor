package com.example.eventlog.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores the final statistics produced by EventProcessor.
 *
 * This class only holds the calculated result. BigDecimal is used for purchase values,
 * and LinkedHashMap keeps printed map results in a predictable order.
 */

public class Statistics {

    private int totalValidEvents;
    private int totalInvalidLines;

    private Map<UUID, Integer> eventCountPerUser = new LinkedHashMap<>();
    private Map<String, Integer> eventCountPerAction = new LinkedHashMap<>();

    private BigDecimal totalPurchaseAmount = BigDecimal.ZERO;
    private BigDecimal averagePurchaseAmount = BigDecimal.ZERO;
    private BigDecimal largestPurchase = BigDecimal.ZERO;

    private UUID mostActiveUser;
    private Map<UUID, Integer> topThreeMostActiveUsers = new LinkedHashMap<>();
    private Map<Integer, String> invalidLineReasons = new LinkedHashMap<>();

    public int getTotalValidEvents() {
        return totalValidEvents;
    }

    public void setTotalValidEvents(int totalValidEvents) {
        this.totalValidEvents = totalValidEvents;
    }

    public int getTotalInvalidLines() {
        return totalInvalidLines;
    }

    public void setTotalInvalidLines(int totalInvalidLines) {
        this.totalInvalidLines = totalInvalidLines;
    }

    public Map<UUID, Integer> getEventCountPerUser() {
        return eventCountPerUser;
    }

    public void setEventCountPerUser(Map<UUID, Integer> eventCountPerUser) {
        this.eventCountPerUser = eventCountPerUser;
    }

    public Map<String, Integer> getEventCountPerAction() {
        return eventCountPerAction;
    }

    public void setEventCountPerAction(Map<String, Integer> eventCountPerAction) {
        this.eventCountPerAction = eventCountPerAction;
    }

    public BigDecimal getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(BigDecimal totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }

    public BigDecimal getAveragePurchaseAmount() {
        return averagePurchaseAmount;
    }

    public void setAveragePurchaseAmount(BigDecimal averagePurchaseAmount) {
        this.averagePurchaseAmount = averagePurchaseAmount;
    }

    public BigDecimal getLargestPurchase() {
        return largestPurchase;
    }

    public void setLargestPurchase(BigDecimal largestPurchase) {
        this.largestPurchase = largestPurchase;
    }

    public UUID getMostActiveUser() {
        return mostActiveUser;
    }

    public void setMostActiveUser(UUID mostActiveUser) {
        this.mostActiveUser = mostActiveUser;
    }

    public Map<UUID, Integer> getTopThreeMostActiveUsers() {
        return topThreeMostActiveUsers;
    }

    public void setTopThreeMostActiveUsers(Map<UUID, Integer> topThreeMostActiveUsers) {
        this.topThreeMostActiveUsers = topThreeMostActiveUsers;
    }

    public Map<Integer, String> getInvalidLineReasons() {
        return invalidLineReasons;
    }

    public void setInvalidLineReasons(Map<Integer, String> invalidLineReasons) {
        this.invalidLineReasons = invalidLineReasons;
    }


}