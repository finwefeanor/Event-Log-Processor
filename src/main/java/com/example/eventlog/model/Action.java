package com.example.eventlog.model;

public enum Action {

    //I used enums to centralize the supported action names
    // and avoid hardcoding action checks everywhere.

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