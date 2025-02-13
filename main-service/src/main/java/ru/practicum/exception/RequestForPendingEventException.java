package ru.practicum.exception;

public class RequestForPendingEventException extends RuntimeException {
    public RequestForPendingEventException(String message) {
        super(message);
    }
}
