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
public class UserProfileDTO {

    @Pattern(regexp = "^([A-Za-z0-9._]{3,20})?$")
    private String username;

    @Email
    private String email;

    @Pattern(regexp = "^(\\+?[0-9]{7,15})?$")
    private String phoneNumber;

    private Gender gender;

    @PastOrPresent
    private LocalDate birthDate;
}

