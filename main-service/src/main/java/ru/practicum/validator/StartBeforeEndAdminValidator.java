package ru.practicum.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.dto.GetEventParametersAdminRequest;

import java.time.LocalDateTime;

public class StartBeforeEndAdminValidator implements ConstraintValidator<StartBeforeEndAdminConstraint, GetEventParametersAdminRequest> {

    @Override
    public boolean isValid(GetEventParametersAdminRequest parameters, ConstraintValidatorContext constraintValidatorContext) {
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

