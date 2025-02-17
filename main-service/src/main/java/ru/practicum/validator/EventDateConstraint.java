package ru.practicum.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(java.lang.annotation.ElementType.FIELD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
public @interface EventDateConstraint {

    String message() default "Стартовое время должно быть раньше конечного, при этом они оба не null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
