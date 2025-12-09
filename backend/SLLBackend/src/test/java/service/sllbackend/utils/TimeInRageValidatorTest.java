package service.sllbackend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.sllbackend.utils.annotations.TimeInRange;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimeInRageValidatorTest {

    private TimeInRageValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TimeInRageValidator();
    }

    @Test
    void testIsValid_WithTimeInRange_ShouldReturnTrue() {
        // Arrange
        TimeInRange annotation = mock(TimeInRange.class);
        when(annotation.start()).thenReturn("09:00");
        when(annotation.end()).thenReturn("17:00");
        validator.initialize(annotation);

        LocalTime timeInRange = LocalTime.of(12, 30);

        // Act
        boolean result = validator.isValid(timeInRange, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValid_WithTimeOutOfRange_ShouldReturnFalse() {
        // Arrange
        TimeInRange annotation = mock(TimeInRange.class);
        when(annotation.start()).thenReturn("09:00");
        when(annotation.end()).thenReturn("17:00");
        validator.initialize(annotation);

        LocalTime timeOutOfRange = LocalTime.of(20, 0);

        // Act
        boolean result = validator.isValid(timeOutOfRange, null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValid_WithTimeAtStartBoundary_ShouldReturnTrue() {
        // Arrange
        TimeInRange annotation = mock(TimeInRange.class);
        when(annotation.start()).thenReturn("09:00");
        when(annotation.end()).thenReturn("17:00");
        validator.initialize(annotation);

        LocalTime startTime = LocalTime.of(9, 0);

        // Act
        boolean result = validator.isValid(startTime, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValid_WithNullTime_ShouldReturnFalse() {
        // Arrange
        TimeInRange annotation = mock(TimeInRange.class);
        when(annotation.start()).thenReturn("09:00");
        when(annotation.end()).thenReturn("17:00");
        validator.initialize(annotation);

        // Act
        boolean result = validator.isValid(null, null);

        // Assert
        assertFalse(result);
    }
}
