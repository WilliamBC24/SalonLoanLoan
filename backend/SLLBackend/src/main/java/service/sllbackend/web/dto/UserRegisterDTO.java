package service.sllbackend.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.Gender;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._]{3,20}$")
    private String username;

    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]{8,50}$"
    )
    private String password;

    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{7,15}$")
    private String phoneNumber;

    @NotNull
    private Gender gender;

    @PastOrPresent
    private LocalDate birthDate;
}
