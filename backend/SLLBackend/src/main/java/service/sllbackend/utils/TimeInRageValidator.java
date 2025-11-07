package service.sllbackend.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import service.sllbackend.utils.annotations.TimeInRange;
import java.time.LocalTime;

public class TimeInRageValidator implements ConstraintValidator<TimeInRange, LocalTime> {

    private LocalTime start;
    private LocalTime end;

    @Override
    public void initialize(TimeInRange constraintAnnotation) {
        this.start = LocalTime.parse(constraintAnnotation.start());
        this.end = LocalTime.parse(constraintAnnotation.end());
    }

    @Override
    public boolean isValid(LocalTime time, ConstraintValidatorContext context) {
        if (time == null) return false;

        return !time.isBefore(start) && !time.isAfter(end);
    }
}
