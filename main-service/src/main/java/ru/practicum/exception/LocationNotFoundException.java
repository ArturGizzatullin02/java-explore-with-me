package ru.practicum.exception;

public class LocationNotFoundException extends EntityNotFoundException {
    public LocationNotFoundException(String message) {
        super(message);
    }
}
