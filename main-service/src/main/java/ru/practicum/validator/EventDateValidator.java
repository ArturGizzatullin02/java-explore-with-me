package ru.practicum.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDateConstraint, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext constraintValidatorContext) {
        if (dateTime == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isEqual(dateTime)) {
            return false;
        }
        return now.isBefore(dateTime);

    }
}
