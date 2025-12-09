package service.sllbackend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.sllbackend.web.dto.PasswordChangeDTO;

import static org.junit.jupiter.api.Assertions.*;

class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchesValidator();
    }

    @Test
    void testIsValid_WithMatchingPasswords_ShouldReturnTrue() {
        // Arrange
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setNewPassword("SecurePassword123");
        dto.setConfirmPassword("SecurePassword123");

        // Act
        boolean result = validator.isValid(dto, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValid_WithNonMatchingPasswords_ShouldReturnFalse() {
        // Arrange
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setNewPassword("SecurePassword123");
        dto.setConfirmPassword("DifferentPassword456");

        // Act
        boolean result = validator.isValid(dto, null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValid_WithNullNewPassword_ShouldReturnFalse() {
        // Arrange
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setNewPassword(null);
        dto.setConfirmPassword("SomePassword");

        // Act
        boolean result = validator.isValid(dto, null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValid_WithNullConfirmPassword_ShouldReturnFalse() {
        // Arrange
        PasswordChangeDTO dto = new PasswordChangeDTO();
        dto.setNewPassword("SomePassword");
        dto.setConfirmPassword(null);

        // Act
        boolean result = validator.isValid(dto, null);

        // Assert
        assertFalse(result);
    }
}
