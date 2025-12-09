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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsTest {

    @Mock
    private UserAccountRepo userAccountRepo;

    @Mock
    private StaffRepo staffRepo;

    @InjectMocks
    private ValidationUtils validationUtils;

    @Test
    void testValidateNewUser_WithDuplicateUsername_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateNewUser("existinguser", "new@email.com", "0123456789")
        );

        assertEquals("Username is already taken", exception.getMessage());
        verify(userAccountRepo).existsByUsername("existinguser");
    }

    @Test
    void testValidateNewUser_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.existsByUsername("newuser")).thenReturn(false);
        when(userAccountRepo.existsByEmail("existing@email.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateNewUser("newuser", "existing@email.com", "0123456789")
        );

        assertEquals("Email is already in use", exception.getMessage());
        verify(userAccountRepo).existsByEmail("existing@email.com");
    }

    @Test
    void testValidateNewUser_WithDuplicatePhoneNumber_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.existsByUsername("newuser")).thenReturn(false);
        when(userAccountRepo.existsByEmail("new@email.com")).thenReturn(false);
        when(userAccountRepo.existsByPhoneNumber("0123456789")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateNewUser("newuser", "new@email.com", "0123456789")
        );

        assertEquals("Phone number is already in use", exception.getMessage());
        verify(userAccountRepo).existsByPhoneNumber("0123456789");
    }

    @Test
    void testValidateNewUser_WithUniqueCredentials_ShouldNotThrowException() {
        // Arrange
        when(userAccountRepo.existsByUsername("newuser")).thenReturn(false);
        when(userAccountRepo.existsByEmail("new@email.com")).thenReturn(false);
        when(userAccountRepo.existsByPhoneNumber("0123456789")).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> 
            validationUtils.validateNewUser("newuser", "new@email.com", "0123456789")
        );
    }

    @Test
    void testValidateUserProfile_WithInvalidAge_ShouldThrowException() {
        // Arrange - Birth date that results in negative age
        LocalDate futureBirthDate = LocalDate.now().plusYears(1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateUserProfile(1L, "username", "email@test.com", "0123456789", futureBirthDate)
        );

        assertTrue(exception.getMessage().contains("Invalid age"));
    }

    @Test
    void testValidateUserProfile_WithAgeOver150_ShouldThrowException() {
        // Arrange - Birth date that results in age over 150
        LocalDate oldBirthDate = LocalDate.now().minusYears(151);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateUserProfile(1L, "username", "email@test.com", "0123456789", oldBirthDate)
        );

        assertTrue(exception.getMessage().contains("Invalid age"));
    }

    @Test
    void testValidateUserProfile_WithConflictingUsername_ShouldThrowException() {
        // Arrange
        LocalDate validBirthDate = LocalDate.now().minusYears(25);
        UserAccount conflictUser = new UserAccount();
        conflictUser.setId(2);
        conflictUser.setUsername("existinguser");
        
        when(userAccountRepo.findConflicts("existinguser", "email@test.com", "0123456789", 1L))
                .thenReturn(List.of(conflictUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateUserProfile(1L, "existinguser", "email@test.com", "0123456789", validBirthDate)
        );

        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void testValidateUserProfile_WithValidData_ShouldNotThrowException() {
        // Arrange
        LocalDate validBirthDate = LocalDate.now().minusYears(25);
        when(userAccountRepo.findConflicts(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertDoesNotThrow(() -> 
            validationUtils.validateUserProfile(1L, "username", "email@test.com", "0123456789", validBirthDate)
        );
    }

    @Test
    void testValidateStaffProfile_WithDuplicateName_ShouldThrowException() {
        // Arrange
        when(staffRepo.existsByNameAndIdNot("John Doe", 1L)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validationUtils.validateStaffProfile(1L, "John Doe")
        );

        assertEquals("Staff name is already in use", exception.getMessage());
        verify(staffRepo).existsByNameAndIdNot("John Doe", 1L);
    }
}
