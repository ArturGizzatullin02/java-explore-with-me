package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    public ErrorHandler() {
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        log.warn(e.getMessage(), e);
        return new ApiError(e.getMessage(), e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(EntityNotFoundException e) {
        log.warn(e.getMessage(), e);
        return new ApiError(e.getMessage(), e.getLocalizedMessage(), HttpStatus.NOT_FOUND.toString(), LocalDateTime.now().toString());
    }

    @ExceptionHandler(value = {AlreadyExistsException.class, DeleteCategoryWithEventsException.class,
            EventAlreadyCanceledException.class, EventAlreadyPublishedException.class, RequestAlreadyExistsException.class,
            RequestByInitiatorException.class, RequestForLimitReachedEventException.class, RequestForPendingEventException.class,
            UserAlreadyExistsException.class, RequestAlreadyConfirmedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final RuntimeException e) {
        log.warn(e.getMessage(), e);
        return new ApiError(e.getMessage(), e.getLocalizedMessage(), HttpStatus.CONFLICT.toString(), LocalDateTime.now().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(PermissionDeniedException e) {
        log.warn(e.getMessage(), e);
        return new ApiError(e.getMessage(), e.getLocalizedMessage(), HttpStatus.FORBIDDEN.toString(), LocalDateTime.now().toString());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(final Exception e) {
        log.warn(e.getMessage(), e);
        return new ApiError(e.getMessage(), e.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now().toString());
    }
}
