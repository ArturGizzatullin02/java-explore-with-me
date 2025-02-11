package ru.practicum.exception;

public class EventNotFoundException extends EntityNotFoundException {
    public EventNotFoundException(final String message) {
        super(message);
    }
}
