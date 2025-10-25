package service.sllbackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.utils.ValidationUtils;
import service.sllbackend.web.dto.UserRegisterDTO;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {
    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private UserAccountRepo userAccountRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegisterServiceImpl registerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerService = new RegisterServiceImpl(validationUtils, userAccountRepo, passwordEncoder);
    }

    @DisplayName("Function 2.2 - Register Account")
    @ParameterizedTest(name = "{0}")
    @MethodSource("registrationTestScenarios")
    void testRegisterUser_ServiceLogicScenarios(String testName,
                                                UserRegisterDTO dto,
                                                Class<? extends Throwable> expectedException,
                                                String expectedErrorMessage) {

        if (expectedException == null) {
            doNothing().when(validationUtils).validateNewUser(
                    dto.getUsername(), dto.getEmail(), dto.getPhoneNumber()
            );
            when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPasswordXYZ");
        } else {
            doThrow(new IllegalArgumentException(expectedErrorMessage))
                    .when(validationUtils)
                    .validateNewUser(dto.getUsername(), dto.getEmail(), dto.getPhoneNumber());
        }

        if (expectedException == null) {
            registerService.registerUser(dto);
            verify(validationUtils, times(1)).validateNewUser(
                    dto.getUsername(), dto.getEmail(), dto.getPhoneNumber()
            );
            ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
            verify(userAccountRepo, times(1)).save(userCaptor.capture());
            assertEquals(dto.getUsername(), userCaptor.getValue().getUsername());
        } else {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                registerService.registerUser(dto);
            });
            assertEquals(expectedErrorMessage, exception.getMessage());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userAccountRepo, never()).save(any(UserAccount.class));
        }
    }

    private static Stream<Arguments> registrationTestScenarios() {
        UserRegisterDTO UTCID01_DTO = UserRegisterDTO.builder().username("thulan").password("Thulan123").phoneNumber("0123456789").email("thulan@gmail.com").gender(Gender.FEMALE).birthDate(LocalDate.parse("2002-01-18")).build();
        UserRegisterDTO UTCID02_DTO = UserRegisterDTO.builder().username("alice").password("Thulan123").phoneNumber("0123456789").email("thulan@gmail.com").gender(Gender.FEMALE).birthDate(LocalDate.parse("2002-01-18")).build();
        UserRegisterDTO UTCID07_DTO = UserRegisterDTO.builder().username("daian").password("Thulan123").phoneNumber("0123456789").email("thulan@gmail.com").gender(Gender.MALE).birthDate(null).build();
        UserRegisterDTO UTCID16_DTO = UserRegisterDTO.builder().username("khanhhuy").password("Thulan123").phoneNumber("0123456789").email("khanhhuy@gmail.com").gender(Gender.MALE).birthDate(LocalDate.parse("2002-01-18")).build();
        UserRegisterDTO UTCID17_DTO = UserRegisterDTO.builder().username("daian").password("Thulan123").phoneNumber("0123456789").email("thulan@gmail.com").gender(Gender.MALE).birthDate(LocalDate.parse("2002-01-18")).build();

        return Stream.of(
                Arguments.of("UTCID01", UTCID01_DTO, null, null),
                Arguments.of("UTCID07", UTCID07_DTO, null, null),
                Arguments.of("UTCID02", UTCID02_DTO, IllegalArgumentException.class, "Username is already taken"),
                Arguments.of("UTCID16", UTCID16_DTO, IllegalArgumentException.class, "Email is already in use"),
                Arguments.of("UTCID17", UTCID17_DTO, IllegalArgumentException.class, "Phone number is already in use")
        );
    }
}