package ru.practicum.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.dto.GetEventParametersUserRequest;

import java.time.LocalDateTime;

public class StartBeforeEndUserValidator implements ConstraintValidator<StartBeforeEndUserConstraint, GetEventParametersUserRequest> {

    @Override
    public boolean isValid(GetEventParametersUserRequest parameters, ConstraintValidatorContext constraintValidatorContext) {
        if (parameters == null) {
            return true;
        }

        LocalDateTime start = parameters.getRangeStart();
        LocalDateTime end = parameters.getRangeEnd();

        if (start != null && end != null) {
            if (start.isEqual(end)) {
                return false;
            }
            return start.isBefore(end);
        } else {
            return true;
        }
    }
}

