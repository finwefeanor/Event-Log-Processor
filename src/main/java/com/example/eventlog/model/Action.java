package com.example.eventlog.model;

/**
 * This Defines the supported event actions.
 * The enum keeps valid action names in one place.
 * isSupported() checks unknown values safely.
 * JSON values are lowercase, so they are converted to uppercase before comparing with the enum constants.
 */
public enum Action {

    LOGIN,
    LOGOUT,
    VIEW,
    CLICK,
    PURCHASE;

    public static boolean isSupported(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            Action.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}