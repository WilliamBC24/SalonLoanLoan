package service.sllbackend.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import service.sllbackend.utils.TimeInRageValidator;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeInRageValidator.class)
@Documented
public @interface TimeInRange {
    String message() default "Time must be between {start} and {end}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String start();
    String end();
}
