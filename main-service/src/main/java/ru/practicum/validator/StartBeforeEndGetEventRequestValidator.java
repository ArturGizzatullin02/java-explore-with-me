package ru.practicum.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.dto.GetEventParametersBaseRequest;

import java.time.LocalDateTime;

public class StartBeforeEndGetEventRequestValidator implements ConstraintValidator<StartBeforeEndGetEventRequestConstraint, GetEventParametersBaseRequest> {

    @Override
    public boolean isValid(GetEventParametersBaseRequest parameters, ConstraintValidatorContext context) {
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
        }
        return true;
    }
}
