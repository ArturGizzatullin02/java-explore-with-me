package ru.practicum.exception;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
