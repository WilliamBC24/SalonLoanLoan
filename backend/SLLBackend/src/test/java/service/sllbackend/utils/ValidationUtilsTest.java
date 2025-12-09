package service.sllbackend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsTest {

    @Mock
    private UserAccountRepo userAccountRepo;

    @Mock
    private StaffRepo staffRepo;

    @InjectMocks
    private ValidationUtils validationUtils;

    @Test
    void testValidateNewUser_WithAvailableCredentials_ShouldNotThrowException() {
        // Arrange
        String username = "newuser";
        String email = "newuser@example.com";
        String phoneNumber = "1234567890";

        when(userAccountRepo.existsByUsername(username)).thenReturn(false);
        when(userAccountRepo.existsByEmail(email)).thenReturn(false);
        when(userAccountRepo.existsByPhoneNumber(phoneNumber)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> validationUtils.validateNewUser(username, email, phoneNumber));
    }

    @Test
    void testValidateNewUser_WithDuplicateUsername_ShouldThrowException() {
        // Arrange
        String username = "existinguser";
        String email = "newuser@example.com";
        String phoneNumber = "1234567890";

        when(userAccountRepo.existsByUsername(username)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateNewUser(username, email, phoneNumber)
        );

        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void testValidateNewUser_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        String username = "newuser";
        String email = "existing@example.com";
        String phoneNumber = "1234567890";

        when(userAccountRepo.existsByUsername(username)).thenReturn(false);
        when(userAccountRepo.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateNewUser(username, email, phoneNumber)
        );

        assertEquals("Email is already in use", exception.getMessage());
    }

    @Test
    void testValidateUserProfile_WithInvalidBirthDate_ShouldThrowException() {
        // Arrange
        Long currentUserId = 1L;
        String username = "user";
        String email = "user@example.com";
        String phoneNumber = "1234567890";
        LocalDate futureBirthDate = LocalDate.now().plusYears(1);

        // No need to stub findConflicts because the validation fails before reaching it

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateUserProfile(currentUserId, username, email, phoneNumber, futureBirthDate)
        );

        assertTrue(exception.getMessage().contains("Invalid age"));
    }

    @Test
    void testValidateStaffProfile_WithAvailableName_ShouldNotThrowException() {
        // Arrange
        Long currentStaffId = 1L;
        String name = "John Doe";

        when(staffRepo.existsByNameAndIdNot(name, currentStaffId)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> validationUtils.validateStaffProfile(currentStaffId, name));
    }

    @Test
    void testValidateStaffProfile_WithDuplicateName_ShouldThrowException() {
        // Arrange
        Long currentStaffId = 1L;
        String name = "Existing Staff";

        when(staffRepo.existsByNameAndIdNot(name, currentStaffId)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateStaffProfile(currentStaffId, name)
        );

        assertEquals("Staff name is already in use", exception.getMessage());
    }
}
