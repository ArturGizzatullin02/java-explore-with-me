package ru.practicum.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.GetHitsRequestParametersDto;

import java.time.LocalDateTime;

public class StartBeforeEndHitRequestValidator implements ConstraintValidator<StartBeforeEndHitRequestConstraint, GetHitsRequestParametersDto> {

    @Override
    public boolean isValid(GetHitsRequestParametersDto parameters, ConstraintValidatorContext constraintValidatorContext) {
        if (parameters == null) {
            return true;
        }

        LocalDateTime start = parameters.getStart();
        LocalDateTime end = parameters.getEnd();

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

