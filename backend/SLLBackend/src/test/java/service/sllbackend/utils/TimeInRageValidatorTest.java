package service.sllbackend.utils;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.utils.annotations.TimeInRange;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeInRageValidatorTest {

    @Mock
    private TimeInRange timeInRange;

    @Mock
    private ConstraintValidatorContext context;

    private TimeInRageValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TimeInRageValidator();
    }

    @Test
    void testIsValid_WithTimeInRange_ShouldReturnTrue() {
        // Arrange
        when(timeInRange.start()).thenReturn("09:00");
        when(timeInRange.end()).thenReturn("17:00");
        validator.initialize(timeInRange);
        
        LocalTime testTime = LocalTime.of(12, 30);

        // Act
        boolean result = validator.isValid(testTime, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValid_WithTimeAtStartBoundary_ShouldReturnTrue() {
        // Arrange
        when(timeInRange.start()).thenReturn("09:00");
        when(timeInRange.end()).thenReturn("17:00");
        validator.initialize(timeInRange);
        
        LocalTime testTime = LocalTime.of(9, 0);

        // Act
        boolean result = validator.isValid(testTime, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValid_WithTimeAtEndBoundary_ShouldReturnTrue() {
        // Arrange
        when(timeInRange.start()).thenReturn("09:00");
        when(timeInRange.end()).thenReturn("17:00");
        validator.initialize(timeInRange);
        
        LocalTime testTime = LocalTime.of(17, 0);

        // Act
        boolean result = validator.isValid(testTime, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValid_WithTimeBeforeRange_ShouldReturnFalse() {
        // Arrange
        when(timeInRange.start()).thenReturn("09:00");
        when(timeInRange.end()).thenReturn("17:00");
        validator.initialize(timeInRange);
        
        LocalTime testTime = LocalTime.of(8, 59);

        // Act
        boolean result = validator.isValid(testTime, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValid_WithTimeAfterRange_ShouldReturnFalse() {
        // Arrange
        when(timeInRange.start()).thenReturn("09:00");
        when(timeInRange.end()).thenReturn("17:00");
        validator.initialize(timeInRange);
        
        LocalTime testTime = LocalTime.of(17, 1);

        // Act
        boolean result = validator.isValid(testTime, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValid_WithNullTime_ShouldReturnFalse() {
        // Arrange
        when(timeInRange.start()).thenReturn("09:00");
        when(timeInRange.end()).thenReturn("17:00");
        validator.initialize(timeInRange);

        // Act
        boolean result = validator.isValid(null, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValid_WithMidnightRange_ShouldWork() {
        // Arrange
        when(timeInRange.start()).thenReturn("00:00");
        when(timeInRange.end()).thenReturn("23:59");
        validator.initialize(timeInRange);
        
        LocalTime testTime = LocalTime.of(12, 0);

        // Act
        boolean result = validator.isValid(testTime, context);

        // Assert
        assertTrue(result);
    }
}
