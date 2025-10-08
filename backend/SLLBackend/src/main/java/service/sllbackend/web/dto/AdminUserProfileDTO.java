package service.sllbackend.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.AccountStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserProfileDTO {
    @Email
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$")
    private String phoneNumber;

    private AccountStatus accountStatus;
}
