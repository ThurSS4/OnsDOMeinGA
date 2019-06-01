package com.example.tim.onsdomeinga.model;

public class ValidationException extends Exception {
    String reason;

    public ValidationException(String reason) {
        this.reason = reason;
    }

    public String toString() {
        return "ValidationException: " + reason;
    }
}
