package ru.practicum.exception;

public class RequestNotFoundException extends EntityNotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
