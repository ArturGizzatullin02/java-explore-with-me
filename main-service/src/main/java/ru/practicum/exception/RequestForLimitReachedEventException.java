package ru.practicum.exception;

public class RequestForLimitReachedEventException extends RuntimeException {
    public RequestForLimitReachedEventException(String message) {
        super(message);
    }
}
