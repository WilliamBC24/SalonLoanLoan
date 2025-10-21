package service.sllbackend.web.dto;

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
public class UserProfileEditDTO {
    private String username;
    private String email;
    private String phoneNumber;
    private boolean phoneVerified;
    private Gender gender;
    private LocalDate birthDate;
    private String accountStatus;
}
