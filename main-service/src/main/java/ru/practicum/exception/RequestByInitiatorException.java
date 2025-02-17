package ru.practicum.exception;

public class RequestByInitiatorException extends RuntimeException {
    public RequestByInitiatorException(final String message) {
        super(message);
    }
}
