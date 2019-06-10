package com.example.tim.onsdomeinga.model;

/**
 * Created the custom Exception but didn't have the time to implement it yet
 */
public class ValidationException extends Exception {
    String reason;

    public ValidationException(String reason) {
        this.reason = reason;
    }

    public String toString() {
        return "ValidationException: " + reason;
    }
}
