package ru.practicum.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ApiError {

    private List<String> errors;

    private final String message;

    private String reason;

    private String status;

    private String timestamp;

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String message, String reason, String status, String timestamp) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }

    public ApiError(List<String> errors, String message, String reason, String status, String timestamp) {
        this(message, reason, status, timestamp);
        this.errors = errors;
    }
}
