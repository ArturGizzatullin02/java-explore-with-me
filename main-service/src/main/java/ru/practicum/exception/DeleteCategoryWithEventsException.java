package ru.practicum.exception;

public class DeleteCategoryWithEventsException extends RuntimeException {
    public DeleteCategoryWithEventsException(String message) {
        super(message);
    }
}
